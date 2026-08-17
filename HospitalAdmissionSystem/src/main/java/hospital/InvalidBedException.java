package hospital;

/** Thrown when a bed number that doesn't exist (e.g. "B99") is used. */
public class InvalidBedException extends Exception {
    public InvalidBedException(String message) {
        super(message);
    }
}
