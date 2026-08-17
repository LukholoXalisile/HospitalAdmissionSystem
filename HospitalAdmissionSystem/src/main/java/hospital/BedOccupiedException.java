package hospital;

/** Thrown when trying to allocate a bed that is already occupied. */
public class BedOccupiedException extends Exception {
    public BedOccupiedException(String message) {
        super(message);
    }
}
