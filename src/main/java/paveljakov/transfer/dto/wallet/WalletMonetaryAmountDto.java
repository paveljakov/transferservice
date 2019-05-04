package paveljakov.transfer.dto.wallet;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletMonetaryAmountDto {

    private BigDecimal amount;

}
