package paveljakov.transfer.dto;

import java.util.UUID;

import lombok.Data;

@Data
public class AccountDto {

    private String id;
    private String firstName;
    private String lastName;
    private String email;

}
