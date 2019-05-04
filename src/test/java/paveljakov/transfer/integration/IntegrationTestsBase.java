package paveljakov.transfer.integration;

import static paveljakov.transfer.DaggerApplication.builder;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.Getter;
import paveljakov.transfer.Application;
import paveljakov.transfer.config.Configuration;
import paveljakov.transfer.dto.EntityIdResponseDto;
import spark.Spark;

@Getter
public abstract class IntegrationTestsBase {

    protected static String databaseName;

    private final HttpClient http = HttpClientBuilder.create()
            .setConnectionTimeToLive(10, TimeUnit.SECONDS)
            .setMaxConnTotal(10)
            .setMaxConnPerRoute(10)
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
            .registerModule(new JavaTimeModule());

    @BeforeClass
    public static void startServer() {
        final Configuration cfg = new Configuration(
                8080,
                "org.h2.Driver",
                "jdbc:h2:mem:" + databaseName,
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

    protected <T> T loadDtoFromJson(final String path, final Class<T> type) throws IOException {
        try (final InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            return getObjectMapper().readValue(is, type);
        }
    }

    protected <T> T deserializeResponseJson(final HttpResponse response, final Class<T> type) throws IOException {
        return getObjectMapper().readValue(response.getEntity().getContent(), type);
    }

    protected HttpPost createJsonPost(final String url, final Object dto) throws JsonProcessingException, UnsupportedEncodingException {
        final HttpEntity entity = new StringEntity(getObjectMapper().writeValueAsString(dto));

        final HttpPost postRequest = new HttpPost(url);
        postRequest.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        postRequest.setEntity(entity);

        return postRequest;
    }

    protected HttpPut createJsonPut(final String url, final Object dto) throws JsonProcessingException, UnsupportedEncodingException {
        final HttpEntity entity = new StringEntity(getObjectMapper().writeValueAsString(dto));

        final HttpPut postRequest = new HttpPut(url);
        postRequest.setHeader(HttpHeaders.CONTENT_TYPE, ContentType.APPLICATION_JSON.getMimeType());
        postRequest.setEntity(entity);

        return postRequest;
    }

}
