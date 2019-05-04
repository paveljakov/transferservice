package paveljakov.transfer.repository.wallet;

import java.math.BigDecimal;
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
import paveljakov.transfer.dto.wallet.CreateWalletDto;
import paveljakov.transfer.dto.wallet.WalletDto;
import paveljakov.transfer.dto.wallet.WalletMonetaryAmountDto;
import paveljakov.transfer.dto.wallet.WalletStatus;
import paveljakov.transfer.dto.wallet.WalletUpdateDto;
import paveljakov.transfer.repository.WalletQueries;

@Singleton
public class WalletRepositoryImpl implements WalletRepository {

    private static final JdbcMapper<WalletDto> MAPPER = JdbcMapperFactory.newInstance()
            .newMapper(WalletDto.class);

    private final Query jdbc;

    @Inject
    public WalletRepositoryImpl(final Query jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<WalletDto> find(final String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Parameter id is mandatory!");
        }

        try {
            return jdbc.select(WalletQueries.FIND_BY_ID)
                    .namedParam("id", id)
                    .firstResult(MAPPER::map);

        } catch (FluentJdbcException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<WalletDto> findByAccount(final String accountId) {
        if (StringUtils.isBlank(accountId)) {
            throw new IllegalArgumentException("Parameter accountId is mandatory!");
        }

        return jdbc.select(WalletQueries.FIND_BY_ACCOUNT)
                .namedParam("accountId", accountId)
                .listResult(MAPPER::map);
    }

    @Override
    public Optional<EntityIdResponseDto> insert(final CreateWalletDto dto, final String accountId) {
        if (dto == null) {
            throw new IllegalArgumentException("Parameter dto is mandatory!");
        }

        return jdbc.update(WalletQueries.INSERT)
                .namedParam("accountId", accountId)
                .namedParam("status", WalletStatus.ACTIVE)
                .namedParam("creationDate", LocalDateTime.now())
                .namedParam("balance", BigDecimal.valueOf(0))
                .namedParam("balanceAvailable", BigDecimal.valueOf(0))
                .namedParam("currency", dto.getCurrency().getCurrencyCode())
                .runFetchGenKeys(rs -> rs.getString("ID"), new String[] {"ID"})
                .firstKey()
                .map(EntityIdResponseDto::new);
    }

    @Override
    public void addAmount(final String id, final WalletMonetaryAmountDto dto) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Parameter id is mandatory!");
        }

        validateMonetaryAmountDto(dto);

        jdbc.transaction().inNoResult(() -> {
            final WalletDto wallet = lock(id);

            final WalletUpdateDto updateDto = new WalletUpdateDto(
                    id,
                    wallet.getBalance().add(dto.getAmount()),
                    wallet.getBalanceAvailable().add(dto.getAmount())
            );

            update(updateDto);
        });
    }

    @Override
    public void authorizeAmount(final String id, final WalletMonetaryAmountDto dto) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Parameter id is mandatory!");
        }

        validateMonetaryAmountDto(dto);

        jdbc.transaction().inNoResult(() -> {
            final WalletDto wallet = lock(id);

            final WalletUpdateDto updateDto = new WalletUpdateDto(
                    id,
                    wallet.getBalance(),
                    wallet.getBalanceAvailable().subtract(dto.getAmount())
            );

            update(updateDto);
        });
    }

    @Override
    public void captureAmount(final String id, final WalletMonetaryAmountDto dto) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Parameter id is mandatory!");
        }

        validateMonetaryAmountDto(dto);

        jdbc.transaction().inNoResult(() -> {
            final WalletDto wallet = lock(id);

            final WalletUpdateDto updateDto = new WalletUpdateDto(
                    id,
                    wallet.getBalance().subtract(dto.getAmount()),
                    wallet.getBalanceAvailable()
            );

            update(updateDto);
        });
    }

    private void validateMonetaryAmountDto(final WalletMonetaryAmountDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Parameter dto is mandatory!");
        }
        if (dto.getAmount() == null) {
            throw new IllegalArgumentException("Parameter dto.amount is mandatory!");
        }
        if (dto.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Parameter dto.amount must be positive!");
        }
    }

    private WalletDto lock(final String id) {
        return jdbc.select(WalletQueries.LOCK_BY_ID)
                .namedParam("id", id)
                .firstResult(MAPPER::map)
                .orElseThrow();
    }

    private void update(final WalletUpdateDto dto) {
        if (dto.getNewBalance().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Parameter dto.newAmount can not be negative!");
        }
        if (dto.getNewBalanceAvailable().compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Parameter dto.newBalanceAvailable can not be negative!");
        }

        jdbc.update(WalletQueries.UPDATE)
                .namedParam("id", dto.getId())
                .namedParam("balance", dto.getNewBalance())
                .namedParam("balanceAvailable", dto.getNewBalanceAvailable())
                .run();
    }

}
