package paveljakov.transfer.app;

import java.sql.SQLException;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.flywaydb.core.Flyway;
import org.h2.tools.Server;
import org.slf4j.Logger;

import lombok.extern.slf4j.Slf4j;
import paveljakov.transfer.common.CommonConstants;
import paveljakov.transfer.config.Configuration;
import paveljakov.transfer.rest.controller.RestController;
import spark.Service;

@Slf4j
@Singleton
public class AppService {

    private final Flyway flyway;

    private final Configuration configuration;

    private final Set<RestController> controllers;

    @Inject
    public AppService(final Flyway flyway, final Configuration configuration, final Set<RestController> controllers) {
        this.flyway = flyway;
        this.configuration = configuration;
        this.controllers = controllers;
    }

    public void start() {
        setupDatabase();
        setupAppServer();
    }

    private void setupDatabase() {
        try {
            final Server server = Server.createTcpServer().start();
            log.info(server.getURL());
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        flyway.migrate();
    }

    private void setupAppServer() {
        final Service service = Service.ignite()
                .port(configuration.getServerPort());

        controllers.forEach(rest -> rest.configureRoutes(service));

        service.after((req, resp) -> resp.type(CommonConstants.JSON_TYPE));
    }

}
