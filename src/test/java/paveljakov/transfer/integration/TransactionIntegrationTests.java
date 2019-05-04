package paveljakov.transfer.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.math.BigDecimal;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.junit.Test;

import paveljakov.transfer.dto.EntityIdResponseDto;
import paveljakov.transfer.dto.transaction.TransactionCreateDto;
import paveljakov.transfer.dto.transaction.TransactionDto;
import paveljakov.transfer.dto.transaction.TransactionStatus;
import paveljakov.transfer.dto.wallet.WalletDto;

public class TransactionIntegrationTests extends IntegrationTestsBase {

    static {
        databaseName = "transactionIntegrationTests";
    }

    @Test
    public void testGetTransaction() throws IOException {
        // Given
        final TransactionDto expectedDto = loadDtoFromJson("json/test_transaction_1.json", TransactionDto.class);

        final HttpGet request = new HttpGet("http://localhost:8080/transactions/0c66f551-21b8-41c2-91a2-44cedf93753c");

        // When
        final HttpResponse httpResponse = getHttp().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

        final TransactionDto transaction = deserializeResponseJson(httpResponse, TransactionDto.class);

        assertThat(transaction).isEqualTo(expectedDto);
    }

    @Test
    public void testGetAccountTransactions() throws IOException {
        // Given
        final TransactionDto expectedDto = loadDtoFromJson("json/test_transaction_1.json", TransactionDto.class);

        final HttpGet requestAcc1Txs = new HttpGet("http://localhost:8080/accounts/33d7199f-0e9d-4bd0-baff-2087c3a6f152/transactions");
        final HttpGet requestAcc2Txs = new HttpGet("http://localhost:8080/accounts/8a898c75-9b03-4ecd-9019-65bdebe41de9/transactions");

        // When
        final HttpResponse acc1TxsResponse = getHttp().execute(requestAcc1Txs);
        final HttpResponse acc2TxsResponse = getHttp().execute(requestAcc2Txs);

        // Then
        assertThat(acc1TxsResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(acc2TxsResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

        final TransactionDto[] acc1Txs = deserializeResponseJson(acc1TxsResponse, TransactionDto[].class);
        final TransactionDto[] acc2Txs = deserializeResponseJson(acc2TxsResponse, TransactionDto[].class);

        assertThat(acc1Txs).containsExactly(expectedDto);
        assertThat(acc2Txs).containsExactly(expectedDto);
    }

    @Test
    public void testGetWalletTransactions() throws IOException {
        // Given
        final TransactionDto expectedDto = loadDtoFromJson("json/test_transaction_1.json", TransactionDto.class);

        final HttpGet requestAcc1Wallet1 = new HttpGet("http://localhost:8080/wallets/d2410f40-f9fd-40c5-8902-ea3ede77c7cd/transactions");
        final HttpGet requestAcc2Wallet1 = new HttpGet("http://localhost:8080/wallets/8ba41a90-2f7c-465d-b4ff-990420138e22/transactions");
        final HttpGet requestAcc2Wallet2 = new HttpGet("http://localhost:8080/wallets/88bc98f8-cc8c-4b3f-a92e-c4a423ed807f/transactions");

        // When
        final HttpResponse acc1Wallet1TxsResponse = getHttp().execute(requestAcc1Wallet1);
        final HttpResponse acc2Wallet1TxsResponse = getHttp().execute(requestAcc2Wallet1);
        final HttpResponse acc2Wallet2TxsResponse = getHttp().execute(requestAcc2Wallet2);

        // Then
        assertThat(acc1Wallet1TxsResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(acc2Wallet1TxsResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(acc2Wallet2TxsResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

        final TransactionDto[] acc1Wallet1Txs = deserializeResponseJson(acc1Wallet1TxsResponse, TransactionDto[].class);
        final TransactionDto[] acc2Wallet1Txs = deserializeResponseJson(acc2Wallet1TxsResponse, TransactionDto[].class);
        final TransactionDto[] acc2Wallet2Txs = deserializeResponseJson(acc2Wallet2TxsResponse, TransactionDto[].class);

        assertThat(acc1Wallet1Txs).containsExactly(expectedDto);
        assertThat(acc2Wallet1Txs).containsExactly(expectedDto);
        assertThat(acc2Wallet2Txs).isEmpty();
    }

    @Test
    public void testNewTransaction() throws IOException {
        // Given
        final String senderWalletId = "d2410f40-f9fd-40c5-8902-ea3ede77c7cd";
        final String receiverWalletId = "8ba41a90-2f7c-465d-b4ff-990420138e22";
        final BigDecimal amount = BigDecimal.valueOf(99.99);

        final TransactionCreateDto requestDto = new TransactionCreateDto(
                senderWalletId,
                receiverWalletId,
                amount
        );

        final HttpPut createTxRequest = createJsonPut("http://localhost:8080/transactions", requestDto);
        final HttpGet senderWalletRequest = new HttpGet("http://localhost:8080/wallets/" + senderWalletId);
        final HttpGet receiverWalletRequest = new HttpGet("http://localhost:8080/wallets/" + receiverWalletId);

        // When
        final HttpResponse createTxRequestResponse = getHttp().execute(createTxRequest);
        final HttpResponse senderWalletResponse = getHttp().execute(senderWalletRequest);
        final HttpResponse receiverWalletResponse = getHttp().execute(receiverWalletRequest);

        // Then
        assertThat(createTxRequestResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(senderWalletResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(receiverWalletResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

        final EntityIdResponseDto transactionId = deserializeResponseJson(createTxRequestResponse, EntityIdResponseDto.class);
        final HttpGet transactionRequest = new HttpGet("http://localhost:8080/transactions/" + transactionId.getId());
        final HttpResponse transactionResponse = getHttp().execute(transactionRequest);
        final TransactionDto transaction = deserializeResponseJson(transactionResponse, TransactionDto.class);

        assertThat(transaction.getStatus()).isEqualTo(TransactionStatus.CONFIRMED);
        assertThat(transaction.getAmount()).isEqualTo(amount);
        assertThat(transaction.getAuthorizedAmount()).isEqualTo(amount);

        final WalletDto senderWallet = deserializeResponseJson(senderWalletResponse, WalletDto.class);

        assertThat(senderWallet.getBalanceAvailable()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(senderWallet.getBalance()).isEqualByComparingTo(BigDecimal.ZERO);

        final WalletDto receiverWallet = deserializeResponseJson(receiverWalletResponse, WalletDto.class);

        assertThat(receiverWallet.getBalanceAvailable()).isEqualByComparingTo(BigDecimal.valueOf(199.29).add(amount));
        assertThat(receiverWallet.getBalance()).isEqualByComparingTo(BigDecimal.valueOf(199.29).add(amount));
    }

}
