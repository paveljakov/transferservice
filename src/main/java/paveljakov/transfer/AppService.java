package paveljakov.transfer;

import javax.inject.Singleton;

import dagger.Component;
import paveljakov.transfer.config.ConfigModule;
import paveljakov.transfer.rest.RestModule;
import paveljakov.transfer.rest.RestService;

@Singleton
@Component(modules = {RestModule.class, ConfigModule.class})
public interface AppService {

    RestService restService();

}
