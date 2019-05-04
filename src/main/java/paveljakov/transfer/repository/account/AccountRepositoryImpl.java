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

import paveljakov.transfer.dto.EntityIdResponseDto;
import paveljakov.transfer.dto.account.AccountDto;
import paveljakov.transfer.dto.account.AccountCreateDto;
import paveljakov.transfer.repository.AccountQueries;

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
            return jdbc.select(AccountQueries.FIND_BY_ID)
                    .namedParam("id", id)
                    .firstResult(MAPPER::map);

        } catch (FluentJdbcException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<AccountDto> findAll() {
        return jdbc.select(AccountQueries.FIND_ALL)
                .listResult(MAPPER::map);
    }

    @Override
    public Optional<EntityIdResponseDto> insert(final AccountCreateDto dto) {
        if (dto == null) {
            throw new IllegalArgumentException("Parameter dto is mandatory!");
        }

        return jdbc.update(AccountQueries.INSERT)
                .namedParam("firstName", dto.getFirstName())
                .namedParam("lastName", dto.getLastName())
                .namedParam("email", dto.getEmail())
                .runFetchGenKeys(rs -> rs.getString("ID"), new String[] {"ID"})
                .firstKey()
                .map(EntityIdResponseDto::new);
    }

}
