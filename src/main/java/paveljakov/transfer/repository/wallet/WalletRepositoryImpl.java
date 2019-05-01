package paveljakov.transfer.repository.wallet;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.query.Query;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;

import paveljakov.transfer.dto.WalletDto;

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
            return jdbc.select("SELECT * FROM WALLET WHERE ID = :id")
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

        return jdbc.select("SELECT WLT.ID, WLT.ACCOUNT_ID, WLT.STATUS, WLT.CREATION_DATE, WLT.BALANCE, WLT.BALANCE_AVAILABLE, WLT.CURRENCY "
                                   + "FROM WALLET WLT JOIN ACCOUNT ACC ON WLT.ACCOUNT_ID = ACC.ID WHERE ACC.ID = :accountId")
                .namedParam("accountId", accountId)
                .listResult(MAPPER::map);
    }

    @Override
    public Optional<String> insert(final WalletDto walletDto) {
        if (walletDto == null) {
            throw new IllegalArgumentException("Parameter walletDto is mandatory!");
        }

        return Optional.empty();
    }
}
