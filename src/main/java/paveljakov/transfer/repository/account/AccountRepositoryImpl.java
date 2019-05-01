package paveljakov.transfer.repository.account;

import java.util.List;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.codejargon.fluentjdbc.api.mapper.ObjectMappers;
import org.codejargon.fluentjdbc.api.query.Query;

import paveljakov.transfer.dto.AccountDto;

@Singleton
public class AccountRepositoryImpl implements AccountRepository {

    private final Query jdbc;
    private final ObjectMappers objectMappers;

    @Inject
    public AccountRepositoryImpl(final Query jdbc, final ObjectMappers objectMappers) {
        this.jdbc = jdbc;
        this.objectMappers = objectMappers;
    }

    @Override
    public Optional<AccountDto> find(final String id) {
        return jdbc.select("SELECT * FROM ACCOUNT WHERE ID = :id")
                .namedParam("id", id)
                .firstResult(objectMappers.forClass(AccountDto.class));
    }

    @Override
    public List<AccountDto> findAll() {
        return jdbc.select("SELECT * FROM ACCOUNT")
                .listResult(objectMappers.forClass(AccountDto.class));
    }

    @Override
    public Optional<String> insert(final AccountDto accountDto) {
        return jdbc.update("INSERT INTO ACCOUNT(FIRST_NAME, LAST_NAME, EMAIL) VALUES (:firstName, :lastName, :email)")
                .namedParam("firstName", accountDto.getFirstName())
                .namedParam("lastName", accountDto.getLastName())
                .namedParam("email", accountDto.getEmail())
                .runFetchGenKeys(rs -> rs.getString("ID"), new String[] {"ID"})
                .firstKey();
    }

}
