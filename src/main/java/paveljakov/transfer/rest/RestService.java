package paveljakov.transfer.rest;

import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import paveljakov.transfer.config.Configuration;
import paveljakov.transfer.rest.controller.RestController;
import spark.Service;

@Singleton
public class RestService {

    public static final String JSON_TYPE = "application/json";

    private final Configuration configuration;

    private final Set<RestController> controllers;

    @Inject
    public RestService(final Configuration configuration, final Set<RestController> controllers) {
        this.configuration = configuration;
        this.controllers = controllers;
    }

    public void start() {
        final Service service = Service.ignite()
                .port(configuration.getServerPort());

        controllers.forEach(rest -> rest.configureRoutes(service));

        service.after((req, resp) -> resp.type(JSON_TYPE));
    }

}
