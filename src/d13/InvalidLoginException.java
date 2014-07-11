package d13;

public class InvalidLoginException extends SecurityException {

    private static final long serialVersionUID = 1L;

    public InvalidLoginException() {
    }

    public InvalidLoginException(String s) {
        super(s);
    }

    public InvalidLoginException(Throwable cause) {
        super(cause);
    }

    public InvalidLoginException(String message, Throwable cause) {
        super(message, cause);
    }

}
