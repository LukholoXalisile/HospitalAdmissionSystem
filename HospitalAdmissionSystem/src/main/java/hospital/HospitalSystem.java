package hospital;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * The "brain" of the application. Holds all registered patients in an
 * ArrayList (Feature 1) and owns one Ward (Feature 2).
 */
public class HospitalSystem {

    private final ArrayList<Patient> patients;
    private final Ward ward;

    public HospitalSystem() {
        patients = new ArrayList<>();
        ward = new Ward();
    }

    public Ward getWard() {
        return ward;
    }

    // ---------- Feature 1: Patient Management ----------

    public void registerPatient(Patient patient) throws DuplicatePatientIdException {
        if (findPatientById(patient.getPatientId()) != null) {
            throw new DuplicatePatientIdException(
                    "A patient with ID " + patient.getPatientId() + " already exists.");
        }
        patients.add(patient);
    }

    /** Returns the patient or null — used internally, doesn't throw. */
    public Patient findPatientById(String patientId) {
        for (Patient p : patients) {
            if (p.getPatientId().equalsIgnoreCase(patientId)) {
                return p;
            }
        }
        return null;
    }

    /** Same lookup, but throws if not found — used by menu options that require a match. */
    public Patient searchPatient(String patientId) throws PatientNotFoundException {
        Patient p = findPatientById(patientId);
        if (p == null) {
            throw new PatientNotFoundException("No patient found with ID " + patientId);
        }
        return p;
    }

    public void deletePatient(String patientId) throws PatientNotFoundException {
        Patient p = searchPatient(patientId);
        patients.remove(p);
    }

    public void displayAllPatients() {
        if (patients.isEmpty()) {
            System.out.println("No patients registered yet.");
            return;
        }
        for (Patient p : patients) {
            p.displayDetails();
        }
    }

    public int getTotalPatients() {
        return patients.size();
    }

    public ArrayList<Patient> getAllPatients() {
        return patients;
    }

    // ---------- Feature 2: Bed Management ----------

    public void allocateBedToPatient(String patientId, String bedNumber)
            throws PatientNotFoundException, InvalidBedException, BedOccupiedException, WardFullException {
        Patient p = searchPatient(patientId);
        if (!(p instanceof Inpatient)) {
            throw new IllegalArgumentException("Only Inpatients can be allocated a hospital bed.");
        }
        ward.allocateBed(bedNumber, patientId);
        ((Inpatient) p).setBedNumber(bedNumber);
    }

    public void releaseBed(String bedNumber) throws InvalidBedException {
        ward.releaseBed(bedNumber);
        // Also clear the bed number off whichever inpatient held it
        for (Patient p : patients) {
            if (p instanceof Inpatient ip && bedNumber.equalsIgnoreCase(ip.getBedNumber())) {
                ip.setBedNumber(null);
            }
        }
    }

    // ---------- Feature 3: Reports / Sorting ----------

    public ArrayList<Patient> sortBySurname() {
        ArrayList<Patient> sorted = new ArrayList<>(patients);
        sorted.sort(Comparator.comparing(Patient::getLastName, String.CASE_INSENSITIVE_ORDER));
        return sorted;
    }

    public ArrayList<Patient> sortByPatientId() {
        ArrayList<Patient> sorted = new ArrayList<>(patients);
        sorted.sort(Comparator.comparing(Patient::getPatientId, String.CASE_INSENSITIVE_ORDER));
        return sorted;
    }
}
