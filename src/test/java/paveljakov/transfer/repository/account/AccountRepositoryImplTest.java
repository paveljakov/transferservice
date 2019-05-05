package paveljakov.transfer.repository.account;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.codejargon.fluentjdbc.api.query.Query;
import org.codejargon.fluentjdbc.api.query.SelectQuery;
import org.codejargon.fluentjdbc.api.query.UpdateQuery;
import org.codejargon.fluentjdbc.api.query.UpdateResultGenKeys;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import paveljakov.transfer.dto.account.AccountCreateDto;
import paveljakov.transfer.repository.AccountQueries;

@RunWith(MockitoJUnitRunner.class)
public class AccountRepositoryImplTest {

    @Mock
    private Query query;

    @Mock
    private UpdateQuery updateQuery;

    @Mock
    private SelectQuery selectQuery;

    @InjectMocks
    private AccountRepositoryImpl accountRepository;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() {
        when(selectQuery.namedParam(any(), any())).thenReturn(selectQuery);

        when(updateQuery.runFetchGenKeys(any(), any())).thenReturn(mock(UpdateResultGenKeys.class));
        when(updateQuery.namedParam(any(), any())).thenReturn(updateQuery);

        when(query.update(any())).thenReturn(updateQuery);
        when(query.select(any())).thenReturn(selectQuery);
    }

    @Test
    public void find() {
        final String accountId = "accountId";

        accountRepository.find(accountId);

        verify(query).select(AccountQueries.FIND_BY_ID);
        verify(selectQuery).namedParam("id", accountId);
    }

    @Test
    public void findAll() {
        accountRepository.findAll();

        verify(query).select(AccountQueries.FIND_ALL);
    }

    @Test
    public void insert() {
        final AccountCreateDto dto = new AccountCreateDto(
                "firstName",
                "lastName",
                "email"
        );

        accountRepository.insert(dto);

        verify(query).update(AccountQueries.INSERT);
        verify(updateQuery).namedParam("firstName", dto.getFirstName());
        verify(updateQuery).namedParam("lastName", dto.getLastName());
        verify(updateQuery).namedParam("email", dto.getEmail());
    }
}