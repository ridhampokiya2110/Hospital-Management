package HospitalManagementSystem;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Doctor {
    private final Connection connection;

    public Doctor(Connection connection) {
        this.connection = connection;
    }

    public void viewDoctors() {
        String query = "select * from doctors";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();
            System.out.println("doctors: ");
            System.out.println("+-----------+-----------------+-----------------------+");
            System.out.println("| doctor Id | Name            | specialization        |");
            System.out.println("+-----------+-----------------+-----------------------+");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String name = resultSet.getString("name");
                String specialization = resultSet.getString("specialization");
                System.out.printf("|%-12s|%-18s|%-20s|\n", id, name, specialization);
                System.out.println("+-----------+-----------------+-----------------------+");

            }

        } catch (SQLException e) {
            System.err.println("An error occurred while accessing the database: " + e.getMessage());
        }
    }

    public boolean getDoctorById(int id) {
        String query = "SELECT * FROM doctors WHERE id = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            System.err.println("An error occurred while accessing the database: " + e.getMessage());

        }
        return false;
    }
}
