package models;

/**
 *
 * @author gehef
 */
public class PMCException extends Exception {

    public PMCException(Throwable cause) {
        super(cause);
    }

    public PMCException(String message, Throwable cause) {
        super(message, cause);
    }

    public PMCException(String message) {
        super(message);
    }

    public PMCException() {
    }
    
}
