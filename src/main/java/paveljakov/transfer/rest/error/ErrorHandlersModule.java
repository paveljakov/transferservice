package paveljakov.transfer.rest.error;

import java.util.Map;
import java.util.NoSuchElementException;

import dagger.Binds;
import dagger.MapKey;
import dagger.Module;
import dagger.multibindings.IntoMap;
import dagger.multibindings.Multibinds;
import paveljakov.transfer.rest.error.ClientErrorExceptionHandler;
import paveljakov.transfer.rest.error.ElementNotFoundExceptionHandler;
import paveljakov.transfer.rest.error.GlobalExceptionHandler;
import spark.ExceptionHandler;

@Module
public abstract class ErrorHandlersModule {

    @Multibinds
    abstract Map<Class<? extends Exception>, ExceptionHandler<Exception>> providesExceptionHandlers();

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

    @MapKey
    @interface ExceptionType {
        Class<? extends Exception> value();
    }

}
