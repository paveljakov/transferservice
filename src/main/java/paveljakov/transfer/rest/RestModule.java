package paveljakov.transfer.rest;

import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import dagger.multibindings.Multibinds;
import paveljakov.transfer.rest.controller.HelloWorldController;
import paveljakov.transfer.rest.controller.RestController;

@Module
public abstract class RestModule {

    @Multibinds
    abstract Set<RestController> providesControllers();

    @Binds
    @IntoSet
    abstract RestController bindHelloWorldController(HelloWorldController helloWorldController);

    @Provides
    static Gson provideGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

}
