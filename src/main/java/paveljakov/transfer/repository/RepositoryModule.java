package paveljakov.transfer.repository;

import dagger.Binds;
import dagger.Module;
import paveljakov.transfer.repository.account.AccountRepository;
import paveljakov.transfer.repository.account.AccountRepositoryImpl;
import paveljakov.transfer.repository.wallet.WalletRepository;
import paveljakov.transfer.repository.wallet.WalletRepositoryImpl;

@Module
public abstract class RepositoryModule {

    @Binds
    abstract AccountRepository bindAccountRepository(AccountRepositoryImpl accountRepository);

    @Binds
    abstract WalletRepository bindWalletRepository(WalletRepositoryImpl walletRepository);

}
