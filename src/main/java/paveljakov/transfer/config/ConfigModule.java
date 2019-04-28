package paveljakov.transfer.config;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;

@Module
public abstract class ConfigModule {

    @Binds
    abstract Configuration BindConfiguration(ConfigurationImpl configuration);

}
