package paveljakov.transfer.rest.controller;

import java.util.Set;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoSet;
import dagger.multibindings.Multibinds;
import paveljakov.transfer.rest.controller.AccountController;
import paveljakov.transfer.rest.controller.RestController;
import paveljakov.transfer.rest.controller.TransactionController;
import paveljakov.transfer.rest.controller.WalletController;

@Module
public abstract class ControllersModule {

    @Multibinds
    abstract Set<RestController> providesControllers();

    @Binds
    @IntoSet
    abstract RestController bindAccountController(AccountController accountController);

    @Binds
    @IntoSet
    abstract RestController bindWalletController(WalletController walletController);

    @Binds
    @IntoSet
    abstract RestController bindTransactionController(TransactionController transactionController);

}
