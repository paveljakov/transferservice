package paveljakov.transfer.rest.error;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.gson.Gson;

import paveljakov.transfer.common.CommonConstants;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;

@Singleton
public class GlobalExceptionHandler implements ExceptionHandler {

    private final Gson gson;

    @Inject
    public GlobalExceptionHandler(final Gson gson) {
        this.gson = gson;
    }

    @Override
    public void handle(final Exception exception, final Request request, final Response response) {
        response.status(500);
        response.type(CommonConstants.JSON_TYPE);
        response.body(gson.toJson(new ErrorMessage(500, "Server error.")));
    }
}
