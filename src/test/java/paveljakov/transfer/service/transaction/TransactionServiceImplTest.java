package paveljakov.transfer.service.transaction;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.refEq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.Optional;

import org.codejargon.fluentjdbc.api.query.Query;
import org.codejargon.fluentjdbc.api.query.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import paveljakov.transfer.dto.transaction.TransactionCreateDto;
import paveljakov.transfer.dto.transaction.TransactionDto;
import paveljakov.transfer.dto.transaction.TransactionStatus;
import paveljakov.transfer.dto.transaction.TransactionUpdateDto;
import paveljakov.transfer.dto.wallet.WalletDto;
import paveljakov.transfer.dto.wallet.WalletMonetaryAmountDto;
import paveljakov.transfer.repository.transaction.TransactionRepository;
import paveljakov.transfer.service.wallet.WalletService;

@RunWith(MockitoJUnitRunner.class)
public class TransactionServiceImplTest {

    @Mock
    private Query query;

    @Mock
    private Transaction transaction;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private TransactionDto transactionDto;

    @Mock
    private WalletService walletService;

    @Mock
    private WalletDto senderWalletDto;

    @Mock
    private WalletDto receiverWalletDto;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    @Before
    public void setUp() {
        doAnswer(inv -> {
            ((Runnable) inv.getArgument(0)).run();
            return null;
        }).when(transaction).inNoResult(any(Runnable.class));

        when(query.transaction()).thenReturn(transaction);

        when(transactionRepository.lock(any())).thenReturn(transactionDto);
    }

    @Test
    public void find() {
        final String transactionId = "transactionId";

        transactionService.find(transactionId);

        verify(transactionRepository).find(transactionId);
    }

    @Test
    public void findByWallet() {
        final String walletId = "walletId";

        transactionService.findByWallet(walletId);

        verify(transactionRepository).findByWallet(walletId);
    }

    @Test
    public void findByAccount() {
        final String accountId = "accountId";

        transactionService.findByAccount(accountId);

        verify(transactionRepository).findByAccount(accountId);
    }

    @Test
    public void create() {
        final TransactionCreateDto dto = new TransactionCreateDto(
                "senderWalletId",
                "receiverWalletId",
                BigDecimal.TEN
        );

        when(senderWalletDto.getCurrency()).thenReturn(Currency.getInstance("USD"));
        when(receiverWalletDto.getCurrency()).thenReturn(Currency.getInstance("USD"));

        when(walletService.find(dto.getSenderWalletId())).thenReturn(Optional.of(senderWalletDto));
        when(walletService.find(dto.getReceiverWalletId())).thenReturn(Optional.of(receiverWalletDto));

        transactionService.create(dto);

        verify(transactionRepository).create(dto);
    }

    @Test
    public void authorize() {
        when(transactionDto.getId()).thenReturn("transactionId");
        when(transactionDto.getStatus()).thenReturn(TransactionStatus.PENDING);
        when(transactionDto.getAmount()).thenReturn(BigDecimal.TEN);
        when(transactionDto.getSenderWalletId()).thenReturn("senderWalletId");

        final WalletMonetaryAmountDto expectedWltUpdateDto = new WalletMonetaryAmountDto(
                transactionDto.getAmount()
        );

        final TransactionUpdateDto expectedTxUpdateDto = new TransactionUpdateDto(
                transactionDto.getId(),
                TransactionStatus.AUTHORIZED,
                null,
                null,
                transactionDto.getAmount()
        );

        transactionService.authorize(transactionDto.getId());

        verify(transactionRepository).lock(transactionDto.getId());
        verify(walletService).authorizeAmount(transactionDto.getSenderWalletId(), expectedWltUpdateDto);
        verify(transactionRepository).update(refEq(expectedTxUpdateDto, "executionDate", "authorizationDate"));
    }

    @Test
    public void capture() {
        when(transactionDto.getId()).thenReturn("transactionId");
        when(transactionDto.getStatus()).thenReturn(TransactionStatus.AUTHORIZED);
        when(transactionDto.getAmount()).thenReturn(BigDecimal.TEN);
        when(transactionDto.getSenderWalletId()).thenReturn("senderWalletId");
        when(transactionDto.getReceiverWalletId()).thenReturn("receiverWalletId");
        when(transactionDto.getAuthorizedAmount()).thenReturn(BigDecimal.TEN);

        final WalletMonetaryAmountDto expectedWltUpdateDto = new WalletMonetaryAmountDto(
                transactionDto.getAmount()
        );

        final TransactionUpdateDto expectedTxUpdateDto = new TransactionUpdateDto(
                transactionDto.getId(),
                TransactionStatus.CONFIRMED,
                null,
                null,
                transactionDto.getAmount()
        );

        transactionService.capture(transactionDto.getId());

        verify(transactionRepository).lock(transactionDto.getId());
        verify(walletService).captureAmount(transactionDto.getSenderWalletId(), expectedWltUpdateDto);
        verify(walletService).addAmount(transactionDto.getReceiverWalletId(), expectedWltUpdateDto);
        verify(transactionRepository).update(refEq(expectedTxUpdateDto, "executionDate", "authorizationDate"));
    }

    @Test
    public void cancel() {
        when(transactionDto.getId()).thenReturn("transactionId");
        when(transactionDto.getAuthorizedAmount()).thenReturn(BigDecimal.TEN);

        final TransactionUpdateDto txUpdateDto = new TransactionUpdateDto(
                transactionDto.getId(),
                TransactionStatus.CANCELED,
                transactionDto.getExecutionDate(),
                transactionDto.getAuthorizationDate(),
                transactionDto.getAuthorizedAmount()
        );

        transactionService.cancel(transactionDto.getId());

        verify(transactionRepository).lock(transactionDto.getId());
        verify(walletService, never()).captureAmount(any(), any());
        verify(walletService, never()).addAmount(any(), any());
        verify(transactionRepository).update(refEq(txUpdateDto, "executionDate", "authorizationDate"));
    }
}