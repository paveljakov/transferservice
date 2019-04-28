package paveljakov.transfer.rest.controller;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import paveljakov.transfer.rest.transform.JsonTransformer;
import spark.Request;
import spark.Response;
import spark.Service;

@Singleton
public class HelloWorldController implements RestController {

    private final JsonTransformer jsonTransformer;

    @Inject
    public HelloWorldController(final JsonTransformer jsonTransformer) {
        this.jsonTransformer = jsonTransformer;
    }

    @Override
    public void configureRoutes(final Service service) {
        service.get("/hello", this::helloWorld, jsonTransformer);
    }

    private Map<?, ?> helloWorld(final Request request, final Response response) {
        return Map.of("text", "Hello world!");
    }

}
