package paveljakov.transfer.service;

import dagger.Binds;
import dagger.Module;
import paveljakov.transfer.repository.transaction.TransactionRepository;
import paveljakov.transfer.service.transaction.TransactionService;
import paveljakov.transfer.service.transaction.TransactionServiceImpl;
import paveljakov.transfer.service.wallet.WalletService;
import paveljakov.transfer.service.wallet.WalletServiceImpl;

@Module
public abstract class ServiceModule {

    @Binds
    abstract WalletService bindWalletService(WalletServiceImpl walletService);

    @Binds
    abstract TransactionService bindTransactionService(TransactionServiceImpl transactionService);

}
