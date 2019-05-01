package paveljakov.transfer.rest;

import java.util.Set;

import javax.inject.Singleton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoSet;
import dagger.multibindings.Multibinds;
import paveljakov.transfer.rest.controller.AccountController;
import paveljakov.transfer.rest.controller.RestController;

@Module
public abstract class RestModule {

    @Multibinds
    abstract Set<RestController> providesControllers();

    @Binds
    @IntoSet
    abstract RestController bindAccountController(AccountController accountController);

    @Provides
    @Singleton
    static Gson provideGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

}
