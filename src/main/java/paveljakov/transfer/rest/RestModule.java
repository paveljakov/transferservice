package paveljakov.transfer.rest;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Singleton;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import dagger.Binds;
import dagger.MapKey;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.ClassKey;
import dagger.multibindings.IntoMap;
import dagger.multibindings.IntoSet;
import dagger.multibindings.Multibinds;
import paveljakov.transfer.rest.controller.AccountController;
import paveljakov.transfer.rest.controller.RestController;
import paveljakov.transfer.rest.error.GlobalExceptionHandler;
import paveljakov.transfer.rest.error.NoSuchElementExceptionHandler;
import spark.ExceptionHandler;

@Module
public abstract class RestModule {

    @Multibinds
    abstract Set<RestController> providesControllers();

    @Multibinds
    abstract Map<Class<?>, ExceptionHandler> providesExceptionHandlers();

    @Binds
    @IntoSet
    abstract RestController bindAccountController(AccountController accountController);

    @Binds
    @IntoMap
    @ExceptionType(Exception.class)
    abstract ExceptionHandler bindGlobalExceptionHandler(GlobalExceptionHandler exceptionHandler);

    @Binds
    @IntoMap
    @ExceptionType(NoSuchElementException.class)
    abstract ExceptionHandler bindNoSuchElementExceptionHandler(NoSuchElementExceptionHandler exceptionHandler);

    @Provides
    @Singleton
    static Gson provideGson() {
        return new GsonBuilder()
                .setPrettyPrinting()
                .create();
    }

    @MapKey
    @interface ExceptionType {
        Class<? extends Exception> value();
    }

}
