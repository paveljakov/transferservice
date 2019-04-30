package paveljakov.transfer.repository;

import dagger.Binds;
import dagger.Module;
import paveljakov.transfer.repository.account.AccountRepository;
import paveljakov.transfer.repository.account.AccountRepositoryImpl;

@Module
public abstract class RepositoryModule {

    @Binds
    abstract AccountRepository bindAccountRepository(AccountRepositoryImpl accountRepository);

}
