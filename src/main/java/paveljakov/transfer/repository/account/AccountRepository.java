package paveljakov.transfer.repository.account;

import java.util.List;
import java.util.Optional;

import paveljakov.transfer.dto.AccountDto;

public interface AccountRepository {

    Optional<AccountDto> find(String id);

    List<AccountDto> findAll();

    Optional<String> insert(AccountDto accountDto);

}
