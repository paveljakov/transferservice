package paveljakov.transfer.dto.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AccountCreateDto {

    private String firstName;
    private String lastName;
    private String email;

}
