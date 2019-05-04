package paveljakov.transfer.repository.account;

import java.util.List;
import java.util.Optional;

import paveljakov.transfer.dto.EntityIdResponseDto;
import paveljakov.transfer.dto.account.AccountDto;
import paveljakov.transfer.dto.account.AccountCreateDto;

public interface AccountRepository {

    Optional<AccountDto> find(String id);

    List<AccountDto> findAll();

    Optional<EntityIdResponseDto> insert(AccountCreateDto dto);

}
