package paveljakov.transfer.rest;

import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import javax.inject.Singleton;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import dagger.Binds;
import dagger.MapKey;
import dagger.Module;
import dagger.Provides;
import dagger.multibindings.IntoMap;
import dagger.multibindings.IntoSet;
import dagger.multibindings.Multibinds;
import paveljakov.transfer.rest.controller.AccountController;
import paveljakov.transfer.rest.controller.RestController;
import paveljakov.transfer.rest.controller.WalletController;
import paveljakov.transfer.rest.error.ClientErrorExceptionHandler;
import paveljakov.transfer.rest.error.ElementNotFoundExceptionHandler;
import paveljakov.transfer.rest.error.GlobalExceptionHandler;
import spark.ExceptionHandler;

@Module
public abstract class RestModule {

    @Multibinds
    abstract Set<RestController> providesControllers();

    @Multibinds
    abstract Map<Class<? extends Exception>, ExceptionHandler<Exception>> providesExceptionHandlers();

    @Binds
    @IntoSet
    abstract RestController bindAccountController(AccountController accountController);

    @Binds
    @IntoSet
    abstract RestController bindWalletController(WalletController walletController);

    @Binds
    @IntoMap
    @ExceptionType(Exception.class)
    abstract ExceptionHandler<Exception> bindGlobalExceptionHandler(GlobalExceptionHandler exceptionHandler);

    @Binds
    @IntoMap
    @ExceptionType(NoSuchElementException.class)
    abstract ExceptionHandler<Exception> bindElementNotFoundExceptionHandler(ElementNotFoundExceptionHandler exceptionHandler);

    @Binds
    @IntoMap
    @ExceptionType(IllegalArgumentException.class)
    abstract ExceptionHandler<Exception> bindClientErrorExceptionHandler(ClientErrorExceptionHandler exceptionHandler);

    @Provides
    @Singleton
    static ObjectMapper provideObjectMapper() {
        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .registerModule(new JavaTimeModule());
    }

    @MapKey
    @interface ExceptionType {
        Class<? extends Exception> value();
    }

}
