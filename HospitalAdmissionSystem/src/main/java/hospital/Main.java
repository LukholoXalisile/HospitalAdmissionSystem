package hospital;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Console-based, menu-driven entry point for the Hospital Patient Admission System.
 */
public class Main {

    private static final Scanner scanner = new Scanner(System.in);
    private static final HospitalSystem system = new HospitalSystem();

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            printMenu();
            int choice = readInt("Enter your choice: ");

            switch (choice) {
                case 1 -> registerPatient();
                case 2 -> searchPatient();
                case 3 -> updatePatient();
                case 4 -> deletePatient();
                case 5 -> system.displayAllPatients();
                case 6 -> allocateBed();
                case 7 -> releaseBed();
                case 8 -> system.getWard().displayWardLayout();
                case 9 -> system.getWard().displayAvailableBeds();
                case 10 -> system.getWard().displayOccupiedBeds();
                case 11 -> showReports();
                case 12 -> sortPatients();
                case 0 -> {
                    running = false;
                    System.out.println("Goodbye!");
                }
                default -> System.out.println("Invalid choice, please try again.");
            }
        }
        scanner.close();
    }

    private static void printMenu() {
        System.out.println("\n===== MediCare Hospital Patient Admission System =====");
        System.out.println("1.  Register Patient");
        System.out.println("2.  Search Patient");
        System.out.println("3.  Update Patient");
        System.out.println("4.  Delete Patient");
        System.out.println("5.  Display All Patients");
        System.out.println("6.  Allocate Bed to Inpatient");
        System.out.println("7.  Release Bed");
        System.out.println("8.  Display Ward Layout");
        System.out.println("9.  Display Available Beds");
        System.out.println("10. Display Occupied Beds");
        System.out.println("11. Ward Reports");
        System.out.println("12. Sort Patients");
        System.out.println("0.  Exit");
    }

    // ---------- Menu actions ----------

    private static void registerPatient() {
        System.out.println("\n-- Register New Patient --");
        String id = readLine("Patient ID: ");
        String first = readLine("First Name: ");
        String last = readLine("Last Name: ");
        int age = readInt("Age: ");
        String gender = readLine("Gender: ");
        String condition = readLine("Medical Condition: ");

        System.out.println("Category: 1=Inpatient 2=Outpatient 3=Emergency");
        int catChoice = readInt("Choose category: ");

        try {
            Patient patient;
            if (catChoice == 1) {
                int wardNumber = readInt("Ward Number: ");
                patient = new Inpatient(id, first, last, age, gender, condition, wardNumber);
            } else if (catChoice == 2) {
                patient = new Patient(id, first, last, age, gender, condition, PatientCategory.OUTPATIENT);
            } else {
                patient = new Patient(id, first, last, age, gender, condition, PatientCategory.EMERGENCY);
            }
            system.registerPatient(patient);
            System.out.println("Patient registered successfully.");
        } catch (DuplicatePatientIdException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void searchPatient() {
        String id = readLine("Enter Patient ID to search: ");
        try {
            Patient p = system.searchPatient(id);
            p.displayDetails();
        } catch (PatientNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void updatePatient() {
        String id = readLine("Enter Patient ID to update: ");
        try {
            Patient p = system.searchPatient(id);
            System.out.println("Leave blank to keep the current value.");

            String first = readLine("First Name [" + p.getFirstName() + "]: ");
            if (!first.isBlank()) p.setFirstName(first);

            String last = readLine("Last Name [" + p.getLastName() + "]: ");
            if (!last.isBlank()) p.setLastName(last);

            String ageStr = readLine("Age [" + p.getAge() + "]: ");
            if (!ageStr.isBlank()) p.setAge(Integer.parseInt(ageStr));

            String condition = readLine("Medical Condition [" + p.getMedicalCondition() + "]: ");
            if (!condition.isBlank()) p.setMedicalCondition(condition);

            System.out.println("Patient updated successfully.");
        } catch (PatientNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.out.println("Error: Age must be a number. Update cancelled.");
        }
    }

    private static void deletePatient() {
        String id = readLine("Enter Patient ID to delete: ");
        try {
            system.deletePatient(id);
            System.out.println("Patient deleted successfully.");
        } catch (PatientNotFoundException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void allocateBed() {
        String id = readLine("Enter Inpatient's Patient ID: ");
        String bed = readLine("Enter Bed Number (e.g. B01): ");
        try {
            system.allocateBedToPatient(id, bed);
            System.out.println("Bed " + bed + " allocated to patient " + id + ".");
        } catch (PatientNotFoundException | InvalidBedException | BedOccupiedException
                 | WardFullException | IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void releaseBed() {
        String bed = readLine("Enter Bed Number to release: ");
        try {
            system.releaseBed(bed);
            System.out.println("Bed " + bed + " released.");
        } catch (InvalidBedException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void showReports() {
        Ward ward = system.getWard();
        System.out.println("\n=== Ward Reports ===");
        System.out.println("Total registered patients : " + system.getTotalPatients());
        System.out.println("Total beds                : " + ward.getTotalBeds());
        System.out.println("Occupied beds              : " + ward.getOccupiedBedCount());
        System.out.println("Available beds             : " + ward.getAvailableBedCount());
        System.out.printf("Ward occupancy              : %.1f%%%n", ward.getOccupancyPercentage());
    }

    private static void sortPatients() {
        System.out.println("Sort by: 1=Surname 2=Patient ID");
        int choice = readInt("Choose: ");
        ArrayList<Patient> sorted = (choice == 2) ? system.sortByPatientId() : system.sortBySurname();

        if (sorted.isEmpty()) {
            System.out.println("No patients to sort.");
            return;
        }
        for (Patient p : sorted) {
            System.out.println(p);
        }
    }

    // ---------- Input helpers ----------

    private static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    private static int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }
}
