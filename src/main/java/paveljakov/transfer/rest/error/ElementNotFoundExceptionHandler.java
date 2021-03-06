package paveljakov.transfer.rest.error;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import paveljakov.transfer.common.CommonConstants;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;

@Singleton
class ElementNotFoundExceptionHandler implements ExceptionHandler<Exception> {

    private final ObjectMapper objectMapper;

    @Inject
    public ElementNotFoundExceptionHandler(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(final Exception exception, final Request request, final Response response) {
        try {
            response.status(404);
            response.type(CommonConstants.JSON_TYPE);
            response.body(objectMapper
                                  .writerWithDefaultPrettyPrinter()
                                  .writeValueAsString(new ErrorMessage(404, "Not found!")));

        } catch (JsonProcessingException e) {
            throw new IllegalStateException(e);
        }
    }
}
