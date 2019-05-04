package paveljakov.transfer.repository.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.query.Query;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;

import paveljakov.transfer.dto.EntityIdResponseDto;
import paveljakov.transfer.dto.transaction.TransactionCreateDto;
import paveljakov.transfer.dto.transaction.TransactionDto;
import paveljakov.transfer.dto.transaction.TransactionStatus;
import paveljakov.transfer.dto.transaction.TransactionUpdateDto;
import paveljakov.transfer.dto.wallet.WalletDto;
import paveljakov.transfer.dto.wallet.WalletMonetaryAmountDto;
import paveljakov.transfer.repository.TransactionQueries;
import paveljakov.transfer.repository.wallet.WalletRepository;

@Singleton
public class TransactionRepositoryImpl implements TransactionRepository {

    private static final JdbcMapper<TransactionDto> MAPPER = JdbcMapperFactory.newInstance()
            .newMapper(TransactionDto.class);

    private final Query jdbc;

    private final WalletRepository walletRepository;

    @Inject
    public TransactionRepositoryImpl(final Query jdbc, final WalletRepository walletRepository) {
        this.jdbc = jdbc;
        this.walletRepository = walletRepository;
    }

    @Override
    public Optional<TransactionDto> find(final String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Parameter id is mandatory!");
        }

        try {
            return jdbc.select(TransactionQueries.FIND_BY_ID)
                    .namedParam("id", id)
                    .firstResult(MAPPER::map);

        } catch (FluentJdbcException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<TransactionDto> findByWallet(final String walletId) {
        if (StringUtils.isBlank(walletId)) {
            throw new IllegalArgumentException("Parameter walletId is mandatory!");
        }

        return jdbc.select(TransactionQueries.FIND_BY_WALLET)
                .namedParam("walletId", walletId)
                .listResult(MAPPER::map);
    }

    @Override
    public List<TransactionDto> findByAccount(final String accountId) {
        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("Parameter accountId is mandatory!");
        }

        return jdbc.select(TransactionQueries.FIND_BY_ACCOUNT)
                .namedParam("accountId", accountId)
                .listResult(MAPPER::map);
    }

    @Override
    public Optional<EntityIdResponseDto> create(final TransactionCreateDto dto) {
        validateTransactionCreateDto(dto);

        return jdbc.update(TransactionQueries.INSERT)
                .namedParam("status", TransactionStatus.PENDING)
                .namedParam("creationDate", LocalDateTime.now())
                .namedParam("executionDate", null)
                .namedParam("authorizationDate", null)
                .namedParam("senderWalletId", dto.getSenderWalletId())
                .namedParam("receiverWalletId", dto.getReceiverWalletId())
                .namedParam("amount", dto.getAmount())
                .namedParam("authorizedAmount", null)
                .runFetchGenKeys(rs -> rs.getString("ID"), new String[] {"ID"})
                .firstKey()
                .map(EntityIdResponseDto::new);
    }

    @Override
    public void authorize(final String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Parameter id is mandatory!");
        }

        jdbc.transaction().inNoResult(() -> {
            final TransactionDto transaction = lock(id);

            validateTransactionForAuthorization(transaction);

            final WalletMonetaryAmountDto authorizationAmount = new WalletMonetaryAmountDto(
                    transaction.getAmount()
            );

            final TransactionUpdateDto txUpdateDto = new TransactionUpdateDto(
                    TransactionStatus.AUTHORIZED,
                    null,
                    LocalDateTime.now(),
                    authorizationAmount.getAmount()
            );

            walletRepository.authorizeAmount(transaction.getSenderWalletId(), authorizationAmount);

            update(transaction.getId(), txUpdateDto);
        });
    }

    @Override
    public void capture(final String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Parameter id is mandatory!");
        }

        jdbc.transaction().inNoResult(() -> {
            final TransactionDto transaction = lock(id);

            validateTransactionForCapture(transaction);

            final WalletMonetaryAmountDto amount = new WalletMonetaryAmountDto(
                    transaction.getAmount()
            );

            final TransactionUpdateDto txUpdateDto = new TransactionUpdateDto(
                    TransactionStatus.CONFIRMED,
                    LocalDateTime.now(),
                    transaction.getAuthorizationDate(),
                    transaction.getAuthorizedAmount()
            );

            walletRepository.captureAmount(transaction.getSenderWalletId(), amount);
            walletRepository.addAmount(transaction.getReceiverWalletId(), amount);

            update(transaction.getId(), txUpdateDto);
        });
    }

    private TransactionDto lock(final String id) {
        return jdbc.select(TransactionQueries.LOCK_BY_ID)
                .namedParam("id", id)
                .firstResult(MAPPER::map)
                .orElseThrow();
    }

    private void update(final String id, final TransactionUpdateDto dto) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Parameter id is mandatory!");
        }

        validateTransactionUpdateDto(dto);

        jdbc.update(TransactionQueries.UPDATE)
                .namedParam("id", id)
                .namedParam("status", dto.getStatus())
                .namedParam("executionDate", dto.getExecutionDate())
                .namedParam("authorizationDate", dto.getAuthorizationDate())
                .namedParam("authorizedAmount", dto.getAuthorizedAmount())
                .run();
    }

    private void validateTransactionForAuthorization(final TransactionDto transaction) {
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalArgumentException("Invalid transaction state!");
        }

        final WalletDto senderWallet = walletRepository.find(transaction.getSenderWalletId())
                .orElseThrow();

        final WalletDto receiverWallet = walletRepository.find(transaction.getReceiverWalletId())
                .orElseThrow();

        if (!Objects.equals(senderWallet.getCurrency(), receiverWallet.getCurrency())) {
            throw new IllegalArgumentException("Sender and receiver wallets must contain same currency!");
        }
    }

    private void validateTransactionForCapture(final TransactionDto transaction) {
        if (transaction.getStatus() != TransactionStatus.AUTHORIZED) {
            throw new IllegalArgumentException("Invalid transaction state!");
        }
    }

    private void validateTransactionCreateDto(final TransactionCreateDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Parameter dto is mandatory!");
        }
        if (StringUtils.isBlank(dto.getReceiverWalletId())) {
            throw new IllegalArgumentException("Parameter dto.receiverWalletId is mandatory!");
        }
        if (StringUtils.isBlank(dto.getSenderWalletId())) {
            throw new IllegalArgumentException("Parameter dto.senderWalletId is mandatory!");
        }
        if (Objects.equals(dto.getReceiverWalletId(), dto.getSenderWalletId())) {
            throw new IllegalArgumentException("Parameter dto.senderWalletId and dto.receiverWalletId must not be the same!");
        }
        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Parameter dto.amount must be positive!");
        }
    }

    private void validateTransactionUpdateDto(final TransactionUpdateDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Parameter dto is mandatory!");
        }
        if (dto.getAuthorizedAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Parameter dto.authorizedAmount can not be negative!");
        }
        if (dto.getStatus() == null) {
            throw new IllegalArgumentException("Parameter dto.status can not be negative!");
        }
    }

}
