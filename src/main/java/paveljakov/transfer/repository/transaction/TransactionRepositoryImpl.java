package paveljakov.transfer.repository.transaction;

import java.time.LocalDateTime;
import java.util.List;
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
import paveljakov.transfer.repository.TransactionQueries;

@Singleton
public class TransactionRepositoryImpl implements TransactionRepository {

    private static final JdbcMapper<TransactionDto> MAPPER = JdbcMapperFactory.newInstance()
            .newMapper(TransactionDto.class);

    private final Query jdbc;

    @Inject
    public TransactionRepositoryImpl(final Query jdbc) {
        this.jdbc = jdbc;
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
    public TransactionDto lock(final String id) {
        return jdbc.select(TransactionQueries.LOCK_BY_ID)
                .namedParam("id", id)
                .singleResult(MAPPER::map);
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
        if (dto == null) {
            throw new IllegalArgumentException("Parameter dto is mandatory!");
        }

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
    public void update(final TransactionUpdateDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Parameter dto is mandatory!");
        }

        jdbc.update(TransactionQueries.UPDATE)
                .namedParam("id", dto.getId())
                .namedParam("status", dto.getStatus())
                .namedParam("executionDate", dto.getExecutionDate())
                .namedParam("authorizationDate", dto.getAuthorizationDate())
                .namedParam("authorizedAmount", dto.getAuthorizedAmount())
                .run();
    }

}
