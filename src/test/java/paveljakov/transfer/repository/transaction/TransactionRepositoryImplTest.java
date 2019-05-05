package paveljakov.transfer.repository.transaction;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;

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

import paveljakov.transfer.dto.transaction.TransactionCreateDto;
import paveljakov.transfer.dto.transaction.TransactionStatus;
import paveljakov.transfer.dto.transaction.TransactionUpdateDto;
import paveljakov.transfer.repository.TransactionQueries;

@RunWith(MockitoJUnitRunner.class)
public class TransactionRepositoryImplTest {

    @Mock
    private Query query;

    @Mock
    private UpdateQuery updateQuery;

    @Mock
    private SelectQuery selectQuery;

    @InjectMocks
    private TransactionRepositoryImpl transactionRepository;

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
        final String transactionId = "transactionId";

        transactionRepository.find(transactionId);

        verify(query).select(TransactionQueries.FIND_BY_ID);
        verify(selectQuery).namedParam("id", transactionId);
    }

    @Test
    public void lock() {
        final String transactionId = "transactionId";

        transactionRepository.lock(transactionId);

        verify(query).select(TransactionQueries.LOCK_BY_ID);
        verify(selectQuery).namedParam("id", transactionId);
    }

    @Test
    public void findByWallet() {
        final String walletId = "walletId";

        transactionRepository.findByWallet(walletId);

        verify(query).select(TransactionQueries.FIND_BY_WALLET);
        verify(selectQuery).namedParam("walletId", walletId);
    }

    @Test
    public void findByAccount() {
        final String accountId = "accountId";

        transactionRepository.findByAccount(accountId);

        verify(query).select(TransactionQueries.FIND_BY_ACCOUNT);
        verify(selectQuery).namedParam("accountId", accountId);
    }

    @Test
    public void create() {
        final TransactionCreateDto dto = new TransactionCreateDto(
                "senderWalletId",
                "receiverWalletId",
                BigDecimal.TEN
        );

        transactionRepository.create(dto);

        verify(query).update(TransactionQueries.INSERT);
        verify(updateQuery).namedParam("status", TransactionStatus.PENDING);
        verify(updateQuery).namedParam(eq("creationDate"), any(LocalDateTime.class));
        verify(updateQuery).namedParam("executionDate", null);
        verify(updateQuery).namedParam("authorizationDate", null);
        verify(updateQuery).namedParam("senderWalletId", dto.getSenderWalletId());
        verify(updateQuery).namedParam("receiverWalletId", dto.getReceiverWalletId());
        verify(updateQuery).namedParam("amount", dto.getAmount());
        verify(updateQuery).namedParam("authorizedAmount", null);
    }

    @Test
    public void update() {
        final TransactionUpdateDto dto = new TransactionUpdateDto(
                "transactionId",
                TransactionStatus.CONFIRMED,
                LocalDateTime.now(),
                LocalDateTime.now(),
                BigDecimal.TEN
        );

        transactionRepository.update(dto);

        verify(query).update(TransactionQueries.UPDATE);
        verify(updateQuery).namedParam("id", dto.getId());
        verify(updateQuery).namedParam("status", dto.getStatus());
        verify(updateQuery).namedParam("executionDate", dto.getExecutionDate());
        verify(updateQuery).namedParam("authorizationDate", dto.getAuthorizationDate());
        verify(updateQuery).namedParam("authorizedAmount", dto.getAuthorizedAmount());
    }
}