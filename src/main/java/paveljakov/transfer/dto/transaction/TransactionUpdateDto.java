package paveljakov.transfer.dto.transaction;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TransactionUpdateDto {

    private final String id;
    private final TransactionStatus status;
    private final LocalDateTime executionDate;
    private final LocalDateTime authorizationDate;
    private final BigDecimal authorizedAmount;

}
