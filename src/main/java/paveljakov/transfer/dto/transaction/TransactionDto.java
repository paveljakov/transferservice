package paveljakov.transfer.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TransactionDto {

    private String id;
    private TransactionStatus status;
    private LocalDateTime creationDate;
    private LocalDateTime executionDate;
    private LocalDateTime authorizationDate;
    private String senderWalletId;
    private String receiverWalletId;
    private BigDecimal amount;
    private BigDecimal authorizedAmount;

}
