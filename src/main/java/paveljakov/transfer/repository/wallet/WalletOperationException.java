package paveljakov.transfer.repository.wallet;

public class WalletOperationException extends RuntimeException {

    public WalletOperationException(final String message) {
        super(message);
    }

    public WalletOperationException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public WalletOperationException(final Throwable cause) {
        super(cause);
    }
}
