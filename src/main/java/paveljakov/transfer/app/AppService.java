package paveljakov.transfer.app;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;
import paveljakov.transfer.persistence.PersistenceService;
import paveljakov.transfer.rest.RestService;

@Slf4j
@Singleton
public class AppService {

    private final RestService restService;
    private final PersistenceService persistenceService;

    @Inject
    public AppService(final RestService restService, final PersistenceService persistenceService) {
        this.restService = restService;
        this.persistenceService = persistenceService;
    }

    public void start() {
        persistenceService.start();
        restService.start();
    }

}
