package hospital;

/** Thrown when searching/updating/deleting a patient ID that doesn't exist. */
public class PatientNotFoundException extends Exception {
    public PatientNotFoundException(String message) {
        super(message);
    }
}
