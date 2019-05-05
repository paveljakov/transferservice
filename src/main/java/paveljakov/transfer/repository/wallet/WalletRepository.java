package paveljakov.transfer.repository.wallet;

import java.util.List;
import java.util.Optional;

import paveljakov.transfer.dto.EntityIdResponseDto;
import paveljakov.transfer.dto.wallet.WalletCreateDto;
import paveljakov.transfer.dto.wallet.WalletMonetaryAmountDto;
import paveljakov.transfer.dto.wallet.WalletDto;

public interface WalletRepository {

    Optional<WalletDto> find(String id);

    List<WalletDto> findByAccount(String accountId);

    Optional<EntityIdResponseDto> insert(WalletCreateDto dto, String accountId);

    void addAmount(String id, WalletMonetaryAmountDto dto);

    void authorizeAmount(String id, WalletMonetaryAmountDto dto);

    void unauthorizeAmount(String id, WalletMonetaryAmountDto dto);

    void captureAmount(String id, WalletMonetaryAmountDto dto);

}
