package paveljakov.transfer.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.assertj.core.util.Objects;
import org.junit.Test;

import paveljakov.transfer.dto.EntityIdResponseDto;
import paveljakov.transfer.dto.account.AccountDto;
import paveljakov.transfer.dto.account.AccountCreateDto;

public class AccountIntegrationTests extends IntegrationTestsBase {

    static {
        databaseName = "accountIntegrationTests";
    }

    @Test
    public void testGetAccount() throws IOException {
        // Given
        final AccountDto expectedDto = loadDtoFromJson("json/test_account_1.json", AccountDto.class);

        final HttpUriRequest request = new HttpGet("http://localhost:8080/accounts/33d7199f-0e9d-4bd0-baff-2087c3a6f152");

        // When
        final HttpResponse httpResponse = getHttp().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

        final AccountDto account = deserializeResponseJson(httpResponse, AccountDto.class);

        assertThat(account).isEqualTo(expectedDto);
    }

    @Test
    public void testGetAccounts() throws IOException {
        // Given
        final List<AccountDto> expected = List.of(
                loadDtoFromJson("json/test_account_1.json", AccountDto.class),
                loadDtoFromJson("json/test_account_2.json", AccountDto.class)
        );

        final HttpUriRequest request = new HttpGet("http://localhost:8080/accounts");

        // When
        final HttpResponse httpResponse = getHttp().execute(request);

        // Then
        assertThat(httpResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

        final AccountDto[] accounts = deserializeResponseJson(httpResponse, AccountDto[].class);

        assertThat(accounts).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    public void testAddAccount() throws IOException {
        // Given
        final String newFirstName = "Sample";
        final String newLastName = "User3";
        final String newEmail = "sample.user3@none.com";

        final AccountCreateDto requestDto = new AccountCreateDto(
                newFirstName,
                newLastName,
                newEmail
        );

        final HttpPut addAccountRequest = createJsonPut("http://localhost:8080/accounts", requestDto);
        final HttpGet accountsRequest = new HttpGet("http://localhost:8080/accounts");

        // When
        final HttpResponse addAccountResponse = getHttp().execute(addAccountRequest);
        final HttpResponse accountsResponse = getHttp().execute(accountsRequest);

        // Then
        assertThat(addAccountResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);
        assertThat(accountsResponse.getStatusLine().getStatusCode()).isEqualTo(HttpStatus.SC_OK);

        final AccountDto[] accounts = deserializeResponseJson(accountsResponse, AccountDto[].class);

        assertThat(accounts).isNotEmpty().hasSize(3);

        final EntityIdResponseDto newAccountId = deserializeResponseJson(addAccountResponse, EntityIdResponseDto.class);

        final AccountDto newAccount = Stream.of(accounts)
                .filter(acc -> Objects.areEqual(acc.getId(), newAccountId.getId()))
                .findAny()
                .orElseThrow();

        assertThat(newAccount.getEmail()).isEqualTo(newEmail);
        assertThat(newAccount.getFirstName()).isEqualTo(newFirstName);
        assertThat(newAccount.getLastName()).isEqualTo(newLastName);
    }

}
