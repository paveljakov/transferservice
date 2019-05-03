package paveljakov.transfer.dto.transaction;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class NewTransactionDto {

    private String senderWalletId;
    private String receiverWalletId;
    private BigDecimal amount;

}
