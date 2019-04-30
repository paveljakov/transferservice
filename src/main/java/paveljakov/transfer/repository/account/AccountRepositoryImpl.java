package paveljakov.transfer.repository.account;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.codejargon.fluentjdbc.api.query.Query;

import paveljakov.transfer.dto.AccountDto;

@Singleton
public class AccountRepositoryImpl implements AccountRepository {

    private final Query jdbc;

    @Inject
    public AccountRepositoryImpl(final Query jdbc) {
        this.jdbc = jdbc;
    }

    public String insert(final AccountDto accountDto) {
        return jdbc.transaction().in(() -> {
            return "";
        });
    }

}
