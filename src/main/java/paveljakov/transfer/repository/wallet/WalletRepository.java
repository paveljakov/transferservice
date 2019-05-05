package paveljakov.transfer.repository.wallet;

import java.util.List;
import java.util.Optional;

import paveljakov.transfer.dto.EntityIdResponseDto;
import paveljakov.transfer.dto.wallet.WalletCreateDto;
import paveljakov.transfer.dto.wallet.WalletMonetaryAmountDto;
import paveljakov.transfer.dto.wallet.WalletDto;
import paveljakov.transfer.dto.wallet.WalletUpdateDto;

public interface WalletRepository {

    Optional<WalletDto> find(String id);

    WalletDto lock(String id);

    List<WalletDto> findByAccount(String accountId);

    Optional<EntityIdResponseDto> insert(WalletCreateDto dto, String accountId);

    void update(WalletUpdateDto dto);

}
