package paveljakov.transfer.app;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import paveljakov.transfer.config.Configuration;
import spark.Service;

@Module
public class AppModule {

    @Provides
    @Singleton
    Service provideService(final Configuration configuration) {
        return Service.ignite()
                .port(configuration.getServerPort());
    }

}
