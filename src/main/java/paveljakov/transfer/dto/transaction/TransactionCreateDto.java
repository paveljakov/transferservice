package paveljakov.transfer.dto.transaction;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionCreateDto {

    private String senderWalletId;
    private String receiverWalletId;
    private BigDecimal amount;

}
