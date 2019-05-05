package paveljakov.transfer.service.wallet;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Currency;

import org.codejargon.fluentjdbc.api.query.Query;
import org.codejargon.fluentjdbc.api.query.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import paveljakov.transfer.dto.wallet.WalletCreateDto;
import paveljakov.transfer.dto.wallet.WalletDto;
import paveljakov.transfer.dto.wallet.WalletMonetaryAmountDto;
import paveljakov.transfer.dto.wallet.WalletUpdateDto;
import paveljakov.transfer.repository.wallet.WalletOperationException;
import paveljakov.transfer.repository.wallet.WalletRepository;

@RunWith(MockitoJUnitRunner.class)
public class WalletServiceImplTest {

    @Mock
    private Query query;

    @Mock
    private Transaction transaction;

    @Mock
    private WalletRepository walletRepository;

    @Mock
    private WalletDto walletDto;

    @InjectMocks
    private WalletServiceImpl walletService;

    @Before
    public void setUp() {
        doAnswer(inv -> {
            ((Runnable) inv.getArgument(0)).run();
            return null;
        }).when(transaction).inNoResult(any(Runnable.class));

        when(query.transaction()).thenReturn(transaction);

        when(walletDto.getBalance()).thenReturn(BigDecimal.TEN);
        when(walletDto.getBalanceAvailable()).thenReturn(BigDecimal.TEN);

        when(walletRepository.lock(any())).thenReturn(walletDto);
    }

    @Test
    public void find() {
        final String walletId = "walletId";

        walletService.find(walletId);

        verify(walletRepository).find(walletId);
    }

    @Test
    public void findByAccount() {
        final String accountId = "accountId";

        walletService.findByAccount(accountId);

        verify(walletRepository).findByAccount(accountId);
    }

    @Test
    public void insert() {
        final String accountId = "accountId";

        final WalletCreateDto dto = new WalletCreateDto(
                Currency.getInstance("USD")
        );

        walletService.insert(dto, accountId);

        verify(walletRepository).insert(dto, accountId);
    }

    @Test
    public void addAmount() {
        final String walletId = "walletId";

        final WalletMonetaryAmountDto dto = new WalletMonetaryAmountDto(
                BigDecimal.TEN
        );

        final WalletUpdateDto expectedUpdateDto = new WalletUpdateDto(
                walletId,
                walletDto.getBalance().add(dto.getAmount()),
                walletDto.getBalanceAvailable().add(dto.getAmount())
        );

        walletService.addAmount(walletId, dto);

        verify(walletRepository).lock(walletId);
        verify(walletRepository).update(expectedUpdateDto);
    }

    @Test
    public void authorizeAmount() {
        final String walletId = "walletId";

        final WalletMonetaryAmountDto dto = new WalletMonetaryAmountDto(
                BigDecimal.TEN
        );

        final WalletUpdateDto expectedUpdateDto = new WalletUpdateDto(
                walletId,
                walletDto.getBalance(),
                walletDto.getBalanceAvailable().subtract(dto.getAmount())
        );

        walletService.authorizeAmount(walletId, dto);

        verify(walletRepository).lock(walletId);
        verify(walletRepository).update(expectedUpdateDto);
    }

    @Test(expected = WalletOperationException.class)
    public void authorizeAmountInsufficientFunds() {
        final String walletId = "walletId";

        final WalletMonetaryAmountDto dto = new WalletMonetaryAmountDto(
                BigDecimal.valueOf(9999.99)
        );

        final WalletUpdateDto expectedUpdateDto = new WalletUpdateDto(
                walletId,
                walletDto.getBalance(),
                walletDto.getBalanceAvailable().subtract(dto.getAmount())
        );

        walletService.authorizeAmount(walletId, dto);

        verify(walletRepository).lock(walletId);
        verify(walletRepository).update(expectedUpdateDto);
    }

    @Test
    public void unauthorizeAmount() {
        final String walletId = "walletId";

        final WalletMonetaryAmountDto dto = new WalletMonetaryAmountDto(
                BigDecimal.TEN
        );

        final WalletUpdateDto expectedUpdateDto = new WalletUpdateDto(
                walletId,
                walletDto.getBalance(),
                walletDto.getBalanceAvailable().add(dto.getAmount())
        );

        walletService.unauthorizeAmount(walletId, dto);

        verify(walletRepository).lock(walletId);
        verify(walletRepository).update(expectedUpdateDto);
    }

    @Test
    public void captureAmount() {
        final String walletId = "walletId";

        final WalletMonetaryAmountDto dto = new WalletMonetaryAmountDto(
                BigDecimal.TEN
        );

        final WalletUpdateDto expectedUpdateDto = new WalletUpdateDto(
                walletId,
                walletDto.getBalance().subtract(dto.getAmount()),
                walletDto.getBalanceAvailable()
        );

        walletService.captureAmount(walletId, dto);

        verify(walletRepository).lock(walletId);
        verify(walletRepository).update(expectedUpdateDto);
    }
}