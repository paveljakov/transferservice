package paveljakov.transfer.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Currency;
import java.util.List;
import java.util.stream.Stream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.assertj.core.util.Objects;
import org.junit.Test;

import paveljakov.transfer.dto.EntityIdResponseDto;
import paveljakov.transfer.dto.wallet.CreateWalletDto;
import paveljakov.transfer.dto.wallet.WalletDto;
import paveljakov.transfer.dto.wallet.WalletMonetaryAmountDto;
import paveljakov.transfer.dto.wallet.WalletStatus;

public class WalletIntegrationTests extends IntegrationTestsBase {

    @Test
    public void testGetWallet() throws IOException {
        // Given
        final WalletDto expectedDto = loadDtoFromJson("json/test_account_2_wallet_1.json", WalletDto.class);

        final HttpGet walletRequest = new HttpGet("http://localhost:8080/wallets/8ba41a90-2f7c-465d-b4ff-990420138e22");

        // When
        final HttpResponse walletResponse = getHttp().execute(walletRequest);

        // Then
        assertThat(walletResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

        final WalletDto actualDto = deserializeResponseJson(walletResponse, WalletDto.class);

        assertThat(actualDto).isEqualTo(expectedDto);
    }

    @Test
    public void testAddFunds() throws IOException {
        // Given
        final WalletMonetaryAmountDto requestDto = new WalletMonetaryAmountDto(
                BigDecimal.valueOf(0.01)
        );

        final HttpPost addFundsRequest = createJsonPost("http://localhost:8080/wallets/d2410f40-f9fd-40c5-8902-ea3ede77c7cd", requestDto);
        final HttpGet walletRequest = new HttpGet("http://localhost:8080/wallets/d2410f40-f9fd-40c5-8902-ea3ede77c7cd");

        // When
        final HttpResponse addFundsResponse = getHttp().execute(addFundsRequest);
        final HttpResponse walletResponse = getHttp().execute(walletRequest);

        // Then
        assertThat(addFundsResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(walletResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

        final WalletDto wallet = deserializeResponseJson(walletResponse, WalletDto.class);

        assertThat(wallet).isNotNull();
        assertThat(wallet.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(100));
        assertThat(wallet.getBalanceAvailable()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    @Test
    public void testAddNegativeFunds() throws IOException {
        // Given
        final WalletMonetaryAmountDto requestDto = new WalletMonetaryAmountDto(
                BigDecimal.valueOf(-0.01)
        );

        final HttpPost addFundsRequest = createJsonPost("http://localhost:8080/wallets/8ba41a90-2f7c-465d-b4ff-990420138e22", requestDto);
        final HttpGet walletRequest = new HttpGet("http://localhost:8080/wallets/8ba41a90-2f7c-465d-b4ff-990420138e22");

        // When
        final HttpResponse addFundsResponse = getHttp().execute(addFundsRequest);
        final HttpResponse walletResponse = getHttp().execute(walletRequest);

        // Then
        assertThat(addFundsResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_BAD_REQUEST);
        assertThat(walletResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

        final WalletDto wallet = deserializeResponseJson(walletResponse, WalletDto.class);

        assertThat(wallet).isNotNull();
        assertThat(wallet.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(199.29));
        assertThat(wallet.getBalanceAvailable()).isEqualByComparingTo(BigDecimal.valueOf(199.29));
    }

    @Test
    public void testGetAccountWallets() throws IOException {
        // Given
        final List<WalletDto> expected = List.of(
                loadDtoFromJson("json/test_account_2_wallet_1.json", WalletDto.class),
                loadDtoFromJson("json/test_account_2_wallet_2.json", WalletDto.class)
        );

        final HttpGet accountWalletsRequest = new HttpGet("http://localhost:8080/accounts/8a898c75-9b03-4ecd-9019-65bdebe41de9/wallets");

        // When
        final HttpResponse accountWalletsResponse = getHttp().execute(accountWalletsRequest);

        // Then
        assertThat(accountWalletsResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

        final WalletDto[] accountWallets = deserializeResponseJson(accountWalletsResponse, WalletDto[].class);

        assertThat(accountWallets).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testAddAccountWallet() throws IOException {
        // Given
        final Currency newCurrency = Currency.getInstance("USD");

        final CreateWalletDto requestDto = new CreateWalletDto(
                newCurrency
        );

        final HttpPut addAccountWalletRequest = createJsonPut("http://localhost:8080/accounts/33d7199f-0e9d-4bd0-baff-2087c3a6f152/wallets",
                                                              requestDto);
        final HttpGet accountWalletsRequest = new HttpGet("http://localhost:8080/accounts/33d7199f-0e9d-4bd0-baff-2087c3a6f152/wallets");

        // When
        final HttpResponse addAccountWalletResponse = getHttp().execute(addAccountWalletRequest);
        final HttpResponse accountWalletsResponse = getHttp().execute(accountWalletsRequest);

        // Then
        assertThat(addAccountWalletResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(accountWalletsResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

        final WalletDto[] wallets = deserializeResponseJson(accountWalletsResponse, WalletDto[].class);

        assertThat(wallets).isNotEmpty().hasSize(2);

        final EntityIdResponseDto newWalletId = deserializeResponseJson(addAccountWalletResponse, EntityIdResponseDto.class);

        final WalletDto newWallet = Stream.of(wallets)
                .filter(wlt -> Objects.areEqual(wlt.getId(), newWalletId.getId()))
                .findAny()
                .orElseThrow();

        assertThat(newWallet.getCurrency()).isEqualTo(newCurrency);
        assertThat(newWallet.getStatus()).isEqualTo(WalletStatus.ACTIVE);
        assertThat(newWallet.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(newWallet.getBalanceAvailable()).isEqualByComparingTo(BigDecimal.ZERO);
    }

}
