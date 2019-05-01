package paveljakov.transfer.repository.wallet;

import java.util.List;
import java.util.Optional;

import paveljakov.transfer.dto.WalletDto;

public interface WalletRepository {

    Optional<WalletDto> find(String id);

    List<WalletDto> findByAccount(String accountId);

    Optional<String> insert(WalletDto walletDto);

}
