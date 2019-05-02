package paveljakov.transfer.dto.account;

import lombok.Data;

@Data
public class CreateAccountDto {

    private String firstName;
    private String lastName;
    private String email;

}
