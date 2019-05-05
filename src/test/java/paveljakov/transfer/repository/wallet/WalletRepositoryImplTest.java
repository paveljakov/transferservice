package paveljakov.transfer.repository.wallet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

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

import paveljakov.transfer.dto.wallet.WalletCreateDto;
import paveljakov.transfer.dto.wallet.WalletStatus;
import paveljakov.transfer.dto.wallet.WalletUpdateDto;
import paveljakov.transfer.repository.WalletQueries;

@RunWith(MockitoJUnitRunner.class)
public class WalletRepositoryImplTest {

    @Mock
    private Query query;

    @Mock
    private UpdateQuery updateQuery;

    @Mock
    private SelectQuery selectQuery;

    @InjectMocks
    private WalletRepositoryImpl walletRepository;

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
        final String walletId = "walletId";

        walletRepository.find(walletId);

        verify(query).select(WalletQueries.FIND_BY_ID);
        verify(selectQuery).namedParam("id", walletId);
    }

    @Test
    public void lock() {
        final String walletId = "walletId";

        walletRepository.lock(walletId);

        verify(query).select(WalletQueries.LOCK_BY_ID);
        verify(selectQuery).namedParam("id", walletId);
    }

    @Test
    public void findByAccount() {
        final String accountId = "accountId";

        walletRepository.findByAccount(accountId);

        verify(query).select(WalletQueries.FIND_BY_ACCOUNT);
        verify(selectQuery).namedParam("accountId", accountId);
    }

    @Test
    public void insert() {
        final String accountId = "accountId";

        final WalletCreateDto dto = new WalletCreateDto(
                Currency.getInstance("USD")
        );

        walletRepository.insert(dto, accountId);

        verify(query).update(WalletQueries.INSERT);
        verify(updateQuery).namedParam("accountId", accountId);
        verify(updateQuery).namedParam("status", WalletStatus.ACTIVE);
        verify(updateQuery).namedParam(eq("creationDate"), any(LocalDateTime.class));
        verify(updateQuery).namedParam("balance", BigDecimal.ZERO);
        verify(updateQuery).namedParam("balanceAvailable", BigDecimal.ZERO);
        verify(updateQuery).namedParam("currency", dto.getCurrency().getCurrencyCode());
    }

    @Test
    public void update() {
        final WalletUpdateDto dto = new WalletUpdateDto(
                "walletId",
                BigDecimal.TEN,
                BigDecimal.TEN
        );

        walletRepository.update(dto);

        verify(query).update(WalletQueries.UPDATE);
        verify(updateQuery).namedParam("id", dto.getId());
        verify(updateQuery).namedParam("balance", dto.getNewBalance());
        verify(updateQuery).namedParam("balanceAvailable", dto.getNewBalanceAvailable());
    }
}