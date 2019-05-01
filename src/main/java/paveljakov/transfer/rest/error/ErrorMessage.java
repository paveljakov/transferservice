package paveljakov.transfer.rest.error;

import lombok.Data;

@Data
class ErrorMessage {

    private final int status;
    private final String message;

}
