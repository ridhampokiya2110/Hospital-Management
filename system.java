package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class system {

    private static final String URL = "jdbc:mysql://127.0.0.1:3306/hospital";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "ridham#123";

    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Database driver not found: " + e.getMessage());
        }
        @SuppressWarnings("resource")
        Scanner scanner = new Scanner(System.in);
        try {
            Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
            patient Patient = new patient(connection, scanner);
            Doctor doctor = new Doctor(connection);
            while (true) {
                System.out.println("HOSPITAL MANAGEMENT SYSTEM");
                System.out.println("1. ADD PATIENT");
                System.out.println("2. VIEW PATIENTS ");
                System.out.println("3. VIEW DOCTORS");
                System.out.println("4. BOOK APPOINTMENT");
                System.out.println("5. EXIT");
                System.out.println("ENTER YOUR CHOICE: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1 -> {
                        Patient.addPatient();
                        System.out.println();
                    }
                    case 2 -> {
                        Patient.viewPatients();
                        System.out.println();
                    }
                    case 3 -> {
                        doctor.viewDoctors();
                        System.out.println();
                    }
                    case 4 -> {
                        bookAppointment(Patient, doctor, connection, scanner);
                        System.out.println();
                    }
                    case 5 -> {
                        return;
                    }
                    default -> System.out.println("ENTER VALID CHOICE...");
                }

            }

        } catch (SQLException e) {
            System.err.println("An error occurred while connecting to the database: " + e.getMessage());
        }
    }

    public static void bookAppointment(patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        System.out.println("ENTER PATIENT ID: ");
        int patientId = scanner.nextInt();
        System.out.println("ENTER DOCTOR ID: ");
        int doctorId = scanner.nextInt();
        System.out.println("ENTER APPOINTMENT DATE (YYYY-MM-DD) ");
        String appoitmentDate = scanner.next();
        if (patient.getPatientById(patientId) && doctor.getDoctorById(doctorId)) {
            if (checkDoctorAvailability(doctorId, appoitmentDate, connection)) {
                String appointmentQuery = "INSERT INTO appointments(patient_id,doctor_id,appointment_id) VALUES(?,?,?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patientId);
                    preparedStatement.setInt(2, doctorId);
                    preparedStatement.setString(3, appoitmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("appointment booked...");
                    } else {
                        System.out.println("failed to book appointment..");
                    }
                } catch (SQLException e) {
                    System.err.println("An error occurred while booking the appointment: " + e.getMessage());
                }
            } else {
                System.out.println("doctor not available on this date...");
            }
        } else {
            System.out.println("either doctor or patient does not exist...");
        }
    }

    public static boolean checkDoctorAvailability(int doctorId, String appointmentDate, Connection connection) {
        String query = "SELECT COUNT(*) FROM appointments WHERE doctor_id=? AND appointment_date=?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, doctorId);
            preparedStatement.setString(2, appointmentDate);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                int count = resultSet.getInt(1);
                return count == 0;
            }
        } catch (SQLException e) {
            System.err.println("An error occurred while checking doctor availability: " + e.getMessage());
        }
        return false;
    }
}
