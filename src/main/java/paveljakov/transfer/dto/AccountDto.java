package paveljakov.transfer.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class AccountDto {

    private UUID id;
    private String firstName;
    private String lastName;

}
