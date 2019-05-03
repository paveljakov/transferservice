package paveljakov.transfer.integration;

import static org.assertj.core.api.Assertions.assertThat;
import static paveljakov.transfer.DaggerApplication.builder;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import paveljakov.transfer.Application;
import paveljakov.transfer.config.Configuration;
import paveljakov.transfer.dto.account.AccountDto;
import spark.Spark;

public class AccountIntegrationTests {

    private final HttpClient http = HttpClientBuilder.create().build();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .registerModule(new JavaTimeModule());

    @BeforeClass
    public static void startServer() {
        final Configuration cfg = new Configuration(
                8080,
                "org.h2.Driver",
                "jdbc:h2:mem:transferdb",
                "sa",
                null
        );

        final Application app = builder()
                .configuration(cfg)
                .build();

        app.appService().start();

        Spark.awaitInitialization();
    }

    @AfterClass
    public static void stopServer() {
        Spark.stop();
        Spark.awaitStop();
    }

    @Test
    public void returnsExistingAccounts() throws IOException {
        // Given
        final HttpUriRequest request = new HttpGet("http://localhost:8080/accounts");

        // When
        final HttpResponse httpResponse = http.execute(request);

        // Then
        final AccountDto[] accounts = objectMapper.readValue(
                httpResponse.getEntity().getContent(), AccountDto[].class);

        assertThat(accounts).isNotNull().isNotEmpty();
    }

}
