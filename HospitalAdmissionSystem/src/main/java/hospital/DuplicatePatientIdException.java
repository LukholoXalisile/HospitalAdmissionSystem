package hospital;

/** Thrown when trying to register a patient whose ID already exists. */
public class DuplicatePatientIdException extends Exception {
    public DuplicatePatientIdException(String message) {
        super(message);
    }
}
