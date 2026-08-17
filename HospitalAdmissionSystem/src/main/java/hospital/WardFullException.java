package hospital;

/** Thrown when trying to allocate a bed but the ward has no free beds left. */
public class WardFullException extends Exception {
    public WardFullException(String message) {
        super(message);
    }
}
