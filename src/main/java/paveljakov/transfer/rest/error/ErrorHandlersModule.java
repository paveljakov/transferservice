package paveljakov.transfer.rest.error;

import java.util.Map;
import java.util.NoSuchElementException;

import dagger.Binds;
import dagger.MapKey;
import dagger.Module;
import dagger.multibindings.IntoMap;
import dagger.multibindings.Multibinds;
import paveljakov.transfer.repository.transaction.TransactionOperationException;
import paveljakov.transfer.repository.wallet.WalletOperationException;
import spark.ExceptionHandler;

@Module
public abstract class ErrorHandlersModule {

    @Multibinds
    abstract Map<Class<? extends Exception>, ExceptionHandler<Exception>> providesExceptionHandlers();

    @Binds
    @IntoMap
    @ExceptionType(Exception.class)
    abstract ExceptionHandler<Exception> bindGenericExceptionHandler(GenericExceptionHandler exceptionHandler);

    @Binds
    @IntoMap
    @ExceptionType(NoSuchElementException.class)
    abstract ExceptionHandler<Exception> bindElementNotFoundExceptionHandler(ElementNotFoundExceptionHandler exceptionHandler);

    @Binds
    @IntoMap
    @ExceptionType(IllegalArgumentException.class)
    abstract ExceptionHandler<Exception> bindClientErrorExceptionHandler(ClientErrorExceptionHandler exceptionHandler);

    @Binds
    @IntoMap
    @ExceptionType(WalletOperationException.class)
    abstract ExceptionHandler<Exception> bindWalletErrorExceptionHandler(TransferErrorExceptionHandler exceptionHandler);

    @Binds
    @IntoMap
    @ExceptionType(TransactionOperationException.class)
    abstract ExceptionHandler<Exception> bindTransactionErrorExceptionHandler(TransferErrorExceptionHandler exceptionHandler);

    @MapKey
    @interface ExceptionType {
        Class<? extends Exception> value();
    }

}
