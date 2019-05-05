package paveljakov.transfer.service.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.codejargon.fluentjdbc.api.query.Query;

import paveljakov.transfer.dto.EntityIdResponseDto;
import paveljakov.transfer.dto.transaction.TransactionCreateDto;
import paveljakov.transfer.dto.transaction.TransactionDto;
import paveljakov.transfer.dto.transaction.TransactionStatus;
import paveljakov.transfer.dto.transaction.TransactionUpdateDto;
import paveljakov.transfer.dto.wallet.WalletDto;
import paveljakov.transfer.dto.wallet.WalletMonetaryAmountDto;
import paveljakov.transfer.repository.transaction.TransactionOperationException;
import paveljakov.transfer.repository.transaction.TransactionRepository;
import paveljakov.transfer.service.wallet.WalletService;

@Singleton
public class TransactionServiceImpl implements TransactionService {

    private final Query jdbc;

    private final TransactionRepository transactionRepository;

    private final WalletService walletService;

    @Inject
    public TransactionServiceImpl(final Query jdbc, final TransactionRepository transactionRepository,
                                  final WalletService walletService) {
        this.jdbc = jdbc;
        this.transactionRepository = transactionRepository;
        this.walletService = walletService;
    }

    @Override
    public Optional<TransactionDto> find(final String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Parameter id is mandatory!");
        }

        return transactionRepository.find(id);
    }

    @Override
    public List<TransactionDto> findByWallet(final String walletId) {
        if (StringUtils.isBlank(walletId)) {
            throw new IllegalArgumentException("Parameter walletId is mandatory!");
        }

        return transactionRepository.findByWallet(walletId);
    }

    @Override
    public List<TransactionDto> findByAccount(final String accountId) {
        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("Parameter accountId is mandatory!");
        }

        return transactionRepository.findByAccount(accountId);
    }

    @Override
    public Optional<EntityIdResponseDto> transfer(final TransactionCreateDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Parameter dto is mandatory!");
        }

        final EntityIdResponseDto transactionId = create(dto)
                .orElseThrow();

        authorize(transactionId.getId());
        capture(transactionId.getId());

        return Optional.of(transactionId);
    }

    @Override
    public Optional<EntityIdResponseDto> create(final TransactionCreateDto dto) {
        validateTransactionCreateDto(dto);

        return transactionRepository.create(dto);
    }

    @Override
    public void authorize(final String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Parameter id is mandatory!");
        }

        try {
            jdbc.transaction().inNoResult(() -> {
                tryAuthorize(transactionRepository.lock(id));
            });

        } catch (Exception e) {
            cancel(id);
            throw e;
        }
    }

    @Override
    public void capture(final String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Parameter id is mandatory!");
        }

        try {
            jdbc.transaction().inNoResult(() -> {
                tryCapture(transactionRepository.lock(id));
            });
        } catch (Exception e) {
            cancel(id);
            throw e;
        }
    }

    @Override
    public void cancel(final String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Parameter id is mandatory!");
        }

        jdbc.transaction().inNoResult(() -> {
            tryCancel(transactionRepository.lock(id));
        });
    }

    private void tryAuthorize(final TransactionDto transaction) {
        validateTransactionForAuthorization(transaction);

        final WalletMonetaryAmountDto authorizationAmount = new WalletMonetaryAmountDto(
                transaction.getAmount()
        );

        final TransactionUpdateDto txUpdateDto = new TransactionUpdateDto(
                transaction.getId(),
                TransactionStatus.AUTHORIZED,
                null,
                LocalDateTime.now(),
                authorizationAmount.getAmount()
        );

        walletService.authorizeAmount(transaction.getSenderWalletId(), authorizationAmount);

        update(txUpdateDto);
    }

    private void tryCapture(final TransactionDto transaction) {
        validateTransactionForCapture(transaction);

        final WalletMonetaryAmountDto amount = new WalletMonetaryAmountDto(
                transaction.getAmount()
        );

        final TransactionUpdateDto txUpdateDto = new TransactionUpdateDto(
                transaction.getId(),
                TransactionStatus.CONFIRMED,
                LocalDateTime.now(),
                transaction.getAuthorizationDate(),
                transaction.getAuthorizedAmount()
        );

        walletService.captureAmount(transaction.getSenderWalletId(), amount);
        walletService.addAmount(transaction.getReceiverWalletId(), amount);

        update(txUpdateDto);
    }

    private void tryCancel(final TransactionDto transaction) {
        validateTransactionForCancel(transaction);

        final TransactionUpdateDto txUpdateDto = new TransactionUpdateDto(
                transaction.getId(),
                TransactionStatus.CANCELED,
                transaction.getExecutionDate(),
                transaction.getAuthorizationDate(),
                transaction.getAuthorizedAmount()
        );

        if (transaction.getStatus() == TransactionStatus.AUTHORIZED) {
            final WalletMonetaryAmountDto unauthorizationAmount = new WalletMonetaryAmountDto(
                    transaction.getAmount()
            );

            walletService.unauthorizeAmount(transaction.getSenderWalletId(), unauthorizationAmount);
        }

        update(txUpdateDto);
    }

    private void update(final TransactionUpdateDto updateDto) {
        validateTransactionUpdateDto(updateDto);

        transactionRepository.update(updateDto);
    }

    private void validateTransactionForAuthorization(final TransactionDto transaction) {
        if (transaction.getStatus() != TransactionStatus.PENDING) {
            throw new IllegalArgumentException("Invalid transaction state!");
        }
    }

    private void validateTransactionForCapture(final TransactionDto transaction) {
        if (transaction.getStatus() != TransactionStatus.AUTHORIZED) {
            throw new IllegalArgumentException("Invalid transaction state!");
        }
    }

    private void validateTransactionForCancel(final TransactionDto transaction) {
        if (transaction.getStatus() == TransactionStatus.CANCELED) {
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

        final WalletDto senderWallet = walletService.find(dto.getSenderWalletId())
                .orElseThrow(() -> new TransactionOperationException("Wallet not found! ID: " + dto.getSenderWalletId()));

        final WalletDto receiverWallet = walletService.find(dto.getReceiverWalletId())
                .orElseThrow(() -> new TransactionOperationException("Wallet not found! ID: " + dto.getReceiverWalletId()));

        if (!Objects.equals(senderWallet.getCurrency(), receiverWallet.getCurrency())) {
            throw new TransactionOperationException("Sender and receiver wallets must contain same currency!");
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
        if (StringUtils.isBlank(dto.getId())) {
            throw new IllegalArgumentException("Parameter dto.id is mandatory!");
        }
        if (dto.getAuthorizedAmount() != null && dto.getAuthorizedAmount().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Parameter dto.authorizedAmount can not be negative!");
        }
        if (dto.getStatus() == null) {
            throw new IllegalArgumentException("Parameter dto.status can not be negative!");
        }
    }

}
