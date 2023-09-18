package by.vlad.elibrary.exception;

public class InvalidRequestDataException extends RuntimeException {

    public InvalidRequestDataException() {
    }

    public InvalidRequestDataException(String message) {
        super(message);
    }

    public InvalidRequestDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public InvalidRequestDataException(Throwable cause) {
        super(cause);
    }
}
