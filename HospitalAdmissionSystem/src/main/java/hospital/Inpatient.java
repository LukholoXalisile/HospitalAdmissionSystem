package hospital;

/**
 * Feature 4 requirement: Inpatient EXTENDS Patient and adds ward/bed info.
 * Only Inpatients are ever assigned a hospital bed.
 */
public class Inpatient extends Patient {

    private int wardNumber;
    private String bedNumber; // e.g. "B01" .. "B20", null until allocated

    public Inpatient(String patientId, String firstName, String lastName, int age,
                      String gender, String medicalCondition, int wardNumber) {
        // super() must be called to initialise the inherited Patient fields.
        // Category is forced to INPATIENT since this class always represents one.
        super(patientId, firstName, lastName, age, gender, medicalCondition, PatientCategory.INPATIENT);
        this.wardNumber = wardNumber;
        this.bedNumber = null;
    }

    public int getWardNumber() { return wardNumber; }
    public void setWardNumber(int wardNumber) { this.wardNumber = wardNumber; }

    public String getBedNumber() { return bedNumber; }
    public void setBedNumber(String bedNumber) { this.bedNumber = bedNumber; }

    /**
     * Overrides the superclass method: prints everything Patient prints,
     * PLUS the ward and bed information, as required by the spec.
     */
    @Override
    public void displayDetails() {
        super.displayDetails();
        System.out.println("Ward Number  : " + wardNumber);
        System.out.println("Bed Number   : " + (bedNumber == null ? "Not allocated" : bedNumber));
    }
}
