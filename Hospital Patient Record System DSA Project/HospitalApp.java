import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

class Patient {
    private String patientId;
    private String name;
    private int age;
    private String disease;

    public Patient(String patientId, String name, int age, String disease) {
        this.patientId = patientId;
        this.name = name;
        this.age = age;
        this.disease = disease;
    }

    public String getPatientId() {
        return patientId;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getDisease() {
        return disease;
    }

    @Override
    public String toString() {
        return patientId + " | " + name + " | " + age + " | " + disease;
    }
}

public class HospitalApp {
    private static final String FILE_NAME = "patients.txt";
    private static final ArrayList<Patient> patients = new ArrayList<>();
    private static int nextPatientNumber = 101;
    private static final Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {
        loadPatients();

        int choice;
        do {
            System.out.println("\n===== HOSPITAL PATIENT RECORD SYSTEM =====");
            System.out.println("1. Register Patient");
            System.out.println("2. Display All Patients");
            System.out.println("3. Search by Name");
            System.out.println("4. Sort by Age");
            System.out.println("5. Exit");
            System.out.print("Enter your choice: ");

            String choiceText = sc.nextLine().trim();
            try {
                choice = Integer.parseInt(choiceText);
            } catch (NumberFormatException e) {
                System.out.println("Invalid choice. Please enter a number from 1 to 5.");
                choice = 0;
                continue;
            }

            switch (choice) {
                case 1:
                    registerPatient();
                    break;
                case 2:
                    displayAllPatients();
                    break;
                case 3:
                    searchPatientByName();
                    break;
                case 4:
                    sortPatientsByAge();
                    break;
                case 5:
                    savePatients();
                    System.out.println("Patient records saved. Exiting program.");
                    break;
                default:
                    System.out.println("Invalid choice. Please enter a number from 1 to 5.");
            }
        } while (choice != 5);
    }

    private static void registerPatient() {
        System.out.print("Enter patient name: ");
        String name = sc.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("Invalid name. Name cannot be empty.");
            return;
        }

        System.out.print("Enter patient age: ");
        String ageText = sc.nextLine().trim();
        int age;

        try {
            age = Integer.parseInt(ageText);
        } catch (NumberFormatException e) {
            System.out.println("Invalid age. Age must be between 0 and 120.");
            return;
        }

        if (age < 0 || age > 120) {
            System.out.println("Invalid age. Age must be between 0 and 120.");
            return;
        }

        System.out.print("Enter disease: ");
        String disease = sc.nextLine().trim();

        if (disease.isEmpty()) {
            System.out.println("Invalid disease. Disease cannot be empty.");
            return;
        }

        String patientId = "PT" + nextPatientNumber;
        Patient patient = new Patient(patientId, name, age, disease);
        patients.add(patient);
        nextPatientNumber++;

        // Save immediately so a registered record is not lost if the program closes.
        savePatients();

        System.out.println("Patient registered. ID: " + patient);
    }

    private static void displayAllPatients() {
        if (patients.isEmpty()) {
            System.out.println("No patients found.");
            return;
        }

        System.out.println("All Patient Records:");
        for (Patient patient : patients) {
            System.out.println(patient);
        }
    }

    private static void searchPatientByName() {
        System.out.print("Enter name to search: ");
        String searchName = sc.nextLine().trim();

        if (searchName.isEmpty()) {
            System.out.println("Search name cannot be empty.");
            return;
        }

        boolean found = false;
        for (Patient patient : patients) {
            if (patient.getName().toLowerCase().contains(searchName.toLowerCase())) {
                System.out.println("Found: " + patient);
                found = true;
            }
        }

        if (!found) {
            System.out.println("Patient not found.");
        }
    }

    private static void sortPatientsByAge() {
        if (patients.isEmpty()) {
            System.out.println("No patients found to sort.");
            return;
        }

        // Bubble sort in ascending order; no built-in sorting method is used.
        for (int i = 0; i < patients.size() - 1; i++) {
            for (int j = 0; j < patients.size() - 1 - i; j++) {
                if (patients.get(j).getAge() > patients.get(j + 1).getAge()) {
                    Patient temp = patients.get(j);
                    patients.set(j, patients.get(j + 1));
                    patients.set(j + 1, temp);
                }
            }
        }

        System.out.println("Patients sorted by age (ascending):");
        displayAllPatients();
        savePatients();
    }

    private static void loadPatients() {
        File file = new File(FILE_NAME);
        if (!file.exists()) {
            return;
        }

        int highestPatientNumber = 100;

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // File format: patientId|name|age|disease
                String[] parts = line.split("\\|", -1);
                if (parts.length != 4) {
                    continue;
                }

                try {
                    String patientId = parts[0].trim();
                    String name = parts[1].trim();
                    int age = Integer.parseInt(parts[2].trim());
                    String disease = parts[3].trim();

                    if (!patientId.isEmpty() && !name.isEmpty()
                            && age >= 0 && age <= 120 && !disease.isEmpty()) {
                        patients.add(new Patient(patientId, name, age, disease));

                        if (patientId.startsWith("PT")) {
                            int number = Integer.parseInt(patientId.substring(2));
                            if (number > highestPatientNumber) {
                                highestPatientNumber = number;
                            }
                        }
                    }
                } catch (NumberFormatException ignored) {
                    // Ignore a malformed record and continue loading valid records.
                }
            }
        } catch (IOException e) {
            System.out.println("Unable to load patient records: " + e.getMessage());
        }

        nextPatientNumber = highestPatientNumber + 1;
    }

    private static void savePatients() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME))) {
            for (Patient patient : patients) {
                writer.write(patient.getPatientId() + "|"
                        + patient.getName() + "|"
                        + patient.getAge() + "|"
                        + patient.getDisease());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Unable to save patient records: " + e.getMessage());
        }
    }
}
