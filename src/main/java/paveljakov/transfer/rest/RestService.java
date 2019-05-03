package paveljakov.transfer.rest;

import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;
import paveljakov.transfer.common.CommonConstants;
import paveljakov.transfer.config.Configuration;
import paveljakov.transfer.rest.controller.RestController;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.Spark;

@Slf4j
@Singleton
public class RestService {

    private final Set<RestController> controllers;

    private final Map<Class<? extends Exception>, ExceptionHandler<Exception>> exceptionHandlers;

    private final Configuration configuration;

    @Inject
    public RestService(final Set<RestController> controllers, final Configuration configuration,
                       final Map<Class<? extends Exception>, ExceptionHandler<Exception>> exceptionHandlers) {

        this.controllers = controllers;
        this.exceptionHandlers = exceptionHandlers;
        this.configuration = configuration;
    }

    public void start() {
        Spark.port(configuration.getServerPort());

        Spark.init();

        exceptionHandlers.forEach(Spark::exception);

        controllers.forEach(RestController::configureRoutes);

        Spark.notFound(this::notFound);

        Spark.after((req, resp) -> resp.type(CommonConstants.JSON_TYPE));
    }

    private Object notFound(final Request request, final Response response) {
        response.status(404);
        response.type(CommonConstants.JSON_TYPE);
        return null;
    }

}
