package paveljakov.transfer.dto.wallet;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class WalletUpdateDto {

    private final String id;
    private final BigDecimal newBalance;
    private final BigDecimal newBalanceAvailable;

}
