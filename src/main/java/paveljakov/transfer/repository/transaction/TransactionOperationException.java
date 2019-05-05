package paveljakov.transfer.repository.transaction;

public class TransactionOperationException extends RuntimeException {

    public TransactionOperationException(final String message) {
        super(message);
    }

    public TransactionOperationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public TransactionOperationException(final Throwable cause) {
        super(cause);
    }
}
