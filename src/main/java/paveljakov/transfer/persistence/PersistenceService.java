package paveljakov.transfer.persistence;

import java.sql.SQLException;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.flywaydb.core.Flyway;
import org.h2.tools.Server;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Singleton
public class PersistenceService {

    private final Flyway flyway;

    @Inject
    public PersistenceService(final Flyway flyway) {
        this.flyway = flyway;
    }

    public void start() {
        try {
            final Server server = Server.createTcpServer().start();
            log.info(server.getURL());
        } catch (SQLException e) {
            log.error(e.getMessage(), e);
        }
        flyway.migrate();
    }

}
