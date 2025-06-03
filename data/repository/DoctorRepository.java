package data.repository;

import data.db.PostgreSQLDatabase;
import model.entities.Doctor;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DoctorRepository {

    public List<Doctor> findAll() {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, middle_name, specialization, phone_number, email FROM doctors";

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = PostgreSQLDatabase.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Doctor doctor = new Doctor();
                doctor.setId(resultSet.getInt("id"));
                doctor.setFirstName(resultSet.getString("first_name"));
                doctor.setLastName(resultSet.getString("last_name"));
                doctor.setMiddleName(resultSet.getString("middle_name"));
                doctor.setSpecialization(resultSet.getString("specialization"));
                doctor.setPhoneNumber(resultSet.getString("phone_number"));
                doctor.setEmail(resultSet.getString("email"));
                doctors.add(doctor);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving doctors: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return doctors;
    }

    public Doctor findById(int id) {
        String sql = "SELECT id, first_name, last_name, middle_name, specialization, phone_number, email FROM doctors WHERE id = ?";
        Doctor doctor = null;

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = PostgreSQLDatabase.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    doctor = new Doctor();
                    doctor.setId(resultSet.getInt("id"));
                    doctor.setFirstName(resultSet.getString("first_name"));
                    doctor.setLastName(resultSet.getString("last_name"));
                    doctor.setMiddleName(resultSet.getString("middle_name"));
                    doctor.setSpecialization(resultSet.getString("specialization"));
                    doctor.setPhoneNumber(resultSet.getString("phone_number"));
                    doctor.setEmail(resultSet.getString("email"));
            }

        } catch (SQLException e) {
            System.err.println("Error finding doctor by id: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return doctor;
    }

    public Doctor findByEmail(String email) {
        String sql = "SELECT id, first_name, last_name, middle_name, specialization, phone_number, email FROM doctors WHERE email = ?";
        Doctor doctor = null;

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = PostgreSQLDatabase.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, email);

            resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    doctor = new Doctor();
                    doctor.setId(resultSet.getInt("id"));
                    doctor.setFirstName(resultSet.getString("first_name"));
                    doctor.setLastName(resultSet.getString("last_name"));
                    doctor.setMiddleName(resultSet.getString("middle_name"));
                    doctor.setSpecialization(resultSet.getString("specialization"));
                    doctor.setPhoneNumber(resultSet.getString("phone_number"));
                    doctor.setEmail(resultSet.getString("email"));
            }

        } catch (SQLException e) {
            System.err.println("Error finding doctor by email: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return doctor;
    }

    public void save(Doctor doctor) {
        String sql = "INSERT INTO doctors (first_name, last_name, middle_name, specialization, phone_number, email) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            connection = PostgreSQLDatabase.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, doctor.getFirstName());
            statement.setString(2, doctor.getLastName());
            statement.setString(3, doctor.getMiddleName());
            statement.setString(4, doctor.getSpecialization());
            statement.setString(5, doctor.getPhoneNumber());
            statement.setString(6, doctor.getEmail());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    doctor.setId(generatedKeys.getInt(1));
                }
            } else {
                throw new SQLException("Creating doctor failed, no rows affected.");
            }

        } catch (SQLException e) {
            System.err.println("Error saving doctor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (statement != null) statement.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void update(Doctor doctor) {
        String sql = "UPDATE doctors SET first_name = ?, last_name = ?, middle_name = ?, specialization = ?, phone_number = ?, email = ? WHERE id = ?";

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = PostgreSQLDatabase.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, doctor.getFirstName());
            statement.setString(2, doctor.getLastName());
            statement.setString(3, doctor.getMiddleName());
            statement.setString(4, doctor.getSpecialization());
            statement.setString(5, doctor.getPhoneNumber());
            statement.setString(6, doctor.getEmail());
            statement.setInt(7, doctor.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating doctor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM doctors WHERE id = ?";

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = PostgreSQLDatabase.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            statement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting doctor: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) statement.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
