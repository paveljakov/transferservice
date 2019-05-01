package paveljakov.transfer.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Currency;

import lombok.Data;

@Data
public class WalletDto {

    private String id;
    private String accountId;
    private WalletStatus status;
    private LocalDateTime creationDate;
    private BigDecimal balance;
    private BigDecimal balanceAvailable;
    private Currency currency;

}
