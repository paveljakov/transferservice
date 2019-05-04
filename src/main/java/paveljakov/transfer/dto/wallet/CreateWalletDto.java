package paveljakov.transfer.dto.wallet;

import java.util.Currency;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateWalletDto {

    private Currency currency;

}
