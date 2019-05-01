package paveljakov.transfer.repository.account;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.apache.commons.lang3.StringUtils;
import org.codejargon.fluentjdbc.api.FluentJdbcException;
import org.codejargon.fluentjdbc.api.query.Query;
import org.simpleflatmapper.jdbc.JdbcMapper;
import org.simpleflatmapper.jdbc.JdbcMapperFactory;

import paveljakov.transfer.dto.AccountDto;

@Singleton
public class AccountRepositoryImpl implements AccountRepository {

    private static final JdbcMapper<AccountDto> MAPPER = JdbcMapperFactory.newInstance()
            .newMapper(AccountDto.class);

    private final Query jdbc;

    @Inject
    public AccountRepositoryImpl(final Query jdbc) {
        this.jdbc = jdbc;
    }

    @Override
    public Optional<AccountDto> find(final String id) {
        if (StringUtils.isBlank(id)) {
            throw new IllegalArgumentException("Parameter id is mandatory!");
        }

        try {
            return jdbc.select("SELECT * FROM ACCOUNT WHERE ID = :id")
                    .namedParam("id", id)
                    .firstResult(MAPPER::map);

        } catch (FluentJdbcException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<AccountDto> findAll() {
        return jdbc.select("SELECT * FROM ACCOUNT")
                .listResult(MAPPER::map);
    }

    @Override
    public Optional<String> insert(final AccountDto accountDto) {
        if (accountDto == null) {
            throw new IllegalArgumentException("Parameter accountDto is mandatory!");
        }

        return jdbc.update("INSERT INTO ACCOUNT(FIRST_NAME, LAST_NAME, EMAIL) VALUES (:firstName, :lastName, :email)")
                .namedParam("firstName", accountDto.getFirstName())
                .namedParam("lastName", accountDto.getLastName())
                .namedParam("email", accountDto.getEmail())
                .runFetchGenKeys(rs -> rs.getString("ID"), new String[] {"ID"})
                .firstKey();
    }

}
