package paveljakov.transfer;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import paveljakov.transfer.app.AppModule;
import paveljakov.transfer.app.AppService;
import paveljakov.transfer.config.Configuration;
import paveljakov.transfer.persistence.PersistenceModule;
import paveljakov.transfer.repository.RepositoryModule;
import paveljakov.transfer.rest.RestModule;

@Singleton
@Component(modules = {AppModule.class, RestModule.class, RepositoryModule.class, PersistenceModule.class})
public interface Application {

    AppService appService();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder configuration(Configuration configuration);

        Application build();

    }

}
