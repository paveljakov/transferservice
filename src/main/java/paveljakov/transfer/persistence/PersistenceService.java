package paveljakov.transfer.persistence;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.flywaydb.core.Flyway;

@Singleton
public class PersistenceService {

    private final Flyway flyway;

    @Inject
    public PersistenceService(final Flyway flyway) {
        this.flyway = flyway;
    }

    public void start() {
        flyway.migrate();
    }

}
