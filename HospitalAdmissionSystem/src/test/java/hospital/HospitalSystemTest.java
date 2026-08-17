package hospital;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class HospitalSystemTest {

    private HospitalSystem system;

    @BeforeEach
    void setUp() {
        // Runs before every single test so each test starts with a clean system.
        system = new HospitalSystem();
    }

    // ---------- Register / CRUD ----------

    @Test
    void testRegisterPatient() throws DuplicatePatientIdException {
        Patient p = new Patient("P001", "John", "Smith", 40, "Male", "Flu", PatientCategory.OUTPATIENT);
        system.registerPatient(p);

        assertEquals(1, system.getTotalPatients());
        assertEquals("Smith", system.findPatientById("P001").getLastName());
    }

    @Test
    void testPreventDuplicatePatientId() throws DuplicatePatientIdException {
        Patient p1 = new Patient("P001", "John", "Smith", 40, "Male", "Flu", PatientCategory.OUTPATIENT);
        Patient p2 = new Patient("P001", "Jane", "Doe", 30, "Female", "Cold", PatientCategory.OUTPATIENT);

        system.registerPatient(p1);

        // Registering a second patient with the SAME id must throw.
        assertThrows(DuplicatePatientIdException.class, () -> system.registerPatient(p2));
        assertEquals(1, system.getTotalPatients());
    }

    @Test
    void testSearchPatient() throws DuplicatePatientIdException, PatientNotFoundException {
        Patient p = new Patient("P002", "Amy", "Jacobs", 25, "Female", "Migraine", PatientCategory.EMERGENCY);
        system.registerPatient(p);

        Patient found = system.searchPatient("P002");
        assertEquals("Amy", found.getFirstName());
    }

    @Test
    void testSearchPatientNotFound() {
        assertThrows(PatientNotFoundException.class, () -> system.searchPatient("DOES_NOT_EXIST"));
    }

    @Test
    void testUpdatePatientDetails() throws DuplicatePatientIdException, PatientNotFoundException {
        Patient p = new Patient("P003", "Sam", "Nkosi", 50, "Male", "Diabetes", PatientCategory.OUTPATIENT);
        system.registerPatient(p);

        Patient toUpdate = system.searchPatient("P003");
        toUpdate.setMedicalCondition("Diabetes Type 2");
        toUpdate.setAge(51);

        Patient updated = system.searchPatient("P003");
        assertEquals("Diabetes Type 2", updated.getMedicalCondition());
        assertEquals(51, updated.getAge());
    }

    @Test
    void testDeletePatient() throws DuplicatePatientIdException, PatientNotFoundException {
        Patient p = new Patient("P004", "Lee", "Ho", 33, "Male", "Fracture", PatientCategory.EMERGENCY);
        system.registerPatient(p);

        system.deletePatient("P004");

        assertEquals(0, system.getTotalPatients());
        assertThrows(PatientNotFoundException.class, () -> system.searchPatient("P004"));
    }

    // ---------- Bed allocation ----------

    @Test
    void testAllocateBed() throws Exception {
        Inpatient ip = new Inpatient("P005", "Nora", "Adams", 60, "Female", "Surgery recovery", 1);
        system.registerPatient(ip);

        system.allocateBedToPatient("P005", "B01");

        assertTrue(system.getWard().getOccupiedBedCount() == 1);
        assertEquals("B01", ip.getBedNumber());
    }

    @Test
    void testReleaseBed() throws Exception {
        Inpatient ip = new Inpatient("P006", "Tom", "Brady", 45, "Male", "Broken leg", 1);
        system.registerPatient(ip);
        system.allocateBedToPatient("P006", "B02");

        system.releaseBed("B02");

        assertEquals(0, system.getWard().getOccupiedBedCount());
        assertNull(ip.getBedNumber());
    }

    @Test
    void testPreventAllocatingOccupiedBed() throws Exception {
        Inpatient ip1 = new Inpatient("P007", "Ali", "Khan", 29, "Male", "Appendicitis", 1);
        Inpatient ip2 = new Inpatient("P008", "Zara", "Bloom", 31, "Female", "Pneumonia", 1);
        system.registerPatient(ip1);
        system.registerPatient(ip2);

        system.allocateBedToPatient("P007", "B03");

        // Same bed, different patient -> must throw
        assertThrows(BedOccupiedException.class, () -> system.allocateBedToPatient("P008", "B03"));
    }

    @Test
    void testPreventBedAllocationWhenWardFull() throws Exception {
        // Fill all 20 beds
        for (int i = 1; i <= 20; i++) {
            Inpatient ip = new Inpatient("F" + i, "First" + i, "Last" + i, 20, "Male", "Observation", 1);
            system.registerPatient(ip);
            system.allocateBedToPatient("F" + i, String.format("B%02d", i));
        }
        assertEquals(0, system.getWard().getAvailableBedCount());

        // One more patient, ward is now full -> must throw
        Inpatient overflow = new Inpatient("F21", "Extra", "Patient", 20, "Male", "Observation", 1);
        system.registerPatient(overflow);

        assertThrows(WardFullException.class, () -> system.allocateBedToPatient("F21", "B01"));
    }

    // ---------- Sorting ----------

    @Test
    void testSortPatientsBySurname() throws DuplicatePatientIdException {
        system.registerPatient(new Patient("P010", "Bob", "Zebra", 20, "Male", "Cough", PatientCategory.OUTPATIENT));
        system.registerPatient(new Patient("P011", "Ann", "Apple", 22, "Female", "Rash", PatientCategory.OUTPATIENT));

        ArrayList<Patient> sorted = system.sortBySurname();

        assertEquals("Apple", sorted.get(0).getLastName());
        assertEquals("Zebra", sorted.get(1).getLastName());
    }

    @Test
    void testSortPatientsByPatientId() throws DuplicatePatientIdException {
        system.registerPatient(new Patient("P020", "X", "X", 20, "Male", "Cough", PatientCategory.OUTPATIENT));
        system.registerPatient(new Patient("P015", "Y", "Y", 22, "Female", "Rash", PatientCategory.OUTPATIENT));

        ArrayList<Patient> sorted = system.sortByPatientId();

        assertEquals("P015", sorted.get(0).getPatientId());
        assertEquals("P020", sorted.get(1).getPatientId());
    }
}
