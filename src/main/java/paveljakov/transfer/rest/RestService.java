package paveljakov.transfer.rest;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;
import paveljakov.transfer.common.CommonConstants;
import paveljakov.transfer.rest.controller.RestController;
import spark.ExceptionHandler;
import spark.Request;
import spark.Response;
import spark.Service;

@Slf4j
@Singleton
public class RestService {

    private final Service service;

    private final Set<RestController> controllers;

    private final Map<Class<? extends Exception>, ExceptionHandler> exceptionHandlers;

    @Inject
    public RestService(final Service service, final Set<RestController> controllers,
                       final Map<Class<? extends Exception>, ExceptionHandler> exceptionHandlers) {

        this.service = service;
        this.controllers = controllers;
        this.exceptionHandlers = exceptionHandlers;
    }

    public void start() {
        exceptionHandlers.forEach(service::exception);

        controllers.forEach(rest -> rest.configureRoutes(service));

        service.notFound(this::notFound);

        service.after((req, resp) -> resp.type(CommonConstants.JSON_TYPE));
    }

    private Object notFound(final Request request, final Response response) {
        response.status(404);
        response.type(CommonConstants.JSON_TYPE);
        return null;
    }

}
