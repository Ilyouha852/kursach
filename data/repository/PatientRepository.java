package data.repository;

import data.db.PostgreSQLDatabase;
import model.Patient;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PatientRepository {

    public List<Patient> findAll() {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT id, first_name, last_name, middle_name, date_of_birth, phone_number, address, " +
                    "disease, chronic_diseases, allergies, previous_diseases, hereditary_diseases FROM patients";

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = PostgreSQLDatabase.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Patient patient = new Patient();
                patient.setId(resultSet.getInt("id"));
                patient.setFirstName(resultSet.getString("first_name"));
                patient.setLastName(resultSet.getString("last_name"));
                patient.setMiddleName(resultSet.getString("middle_name"));
                patient.setDateOfBirth(resultSet.getDate("date_of_birth"));
                patient.setPhoneNumber(resultSet.getString("phone_number"));
                patient.setAddress(resultSet.getString("address"));
                patient.setDisease(resultSet.getString("disease"));
                patient.setChronicDiseases(resultSet.getString("chronic_diseases"));
                patient.setAllergies(resultSet.getString("allergies"));
                patient.setPreviousDiseases(resultSet.getString("previous_diseases"));
                patient.setHereditaryDiseases(resultSet.getString("hereditary_diseases"));
                patients.add(patient);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving patients: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Не закрываем соединение здесь
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                // Не закрываем connection
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return patients;
    }

    public Patient findById(int id) {
        Patient patient = null;
        String sql = "SELECT id, first_name, last_name, middle_name, date_of_birth, phone_number, address, " +
                    "disease, chronic_diseases, allergies, previous_diseases, hereditary_diseases FROM patients WHERE id = ?";

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = PostgreSQLDatabase.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    patient = new Patient();
                    patient.setId(resultSet.getInt("id"));
                    patient.setFirstName(resultSet.getString("first_name"));
                    patient.setLastName(resultSet.getString("last_name"));
                    patient.setMiddleName(resultSet.getString("middle_name"));
                    patient.setDateOfBirth(resultSet.getDate("date_of_birth"));
                    patient.setPhoneNumber(resultSet.getString("phone_number"));
                    patient.setAddress(resultSet.getString("address"));
                    patient.setDisease(resultSet.getString("disease"));
                    patient.setChronicDiseases(resultSet.getString("chronic_diseases"));
                    patient.setAllergies(resultSet.getString("allergies"));
                    patient.setPreviousDiseases(resultSet.getString("previous_diseases"));
                    patient.setHereditaryDiseases(resultSet.getString("hereditary_diseases"));
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving patient with id " + id + ": " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Не закрываем соединение здесь
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                // Не закрываем connection
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return patient;
    }

    public void save(Patient patient) {
        String sql = "INSERT INTO patients (first_name, last_name, middle_name, date_of_birth, phone_number, address, " +
                    "disease, chronic_diseases, allergies, previous_diseases, hereditary_diseases) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) RETURNING id";

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            connection = PostgreSQLDatabase.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, patient.getFirstName());
            statement.setString(2, patient.getLastName());
            statement.setString(3, patient.getMiddleName());
            statement.setDate(4, (patient.getDateOfBirth() != null) ? new Date(patient.getDateOfBirth().getTime()) : null);
            statement.setString(5, patient.getPhoneNumber());
            statement.setString(6, patient.getAddress());
            statement.setString(7, patient.getDisease());
            statement.setString(8, patient.getChronicDiseases());
            statement.setString(9, patient.getAllergies());
            statement.setString(10, patient.getPreviousDiseases());
            statement.setString(11, patient.getHereditaryDiseases());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    patient.setId(generatedKeys.getInt(1));
                }
            } else {
                throw new SQLException("Creating patient failed, no rows affected.");
            }

        } catch (SQLException e) {
            System.err.println("Error saving patient: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Не закрываем соединение здесь
            try {
                if (generatedKeys != null) generatedKeys.close();
                if (statement != null) statement.close();
                // Не закрываем connection
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void update(Patient patient) {
        String sql = "UPDATE patients SET first_name = ?, last_name = ?, middle_name = ?, date_of_birth = ?, " +
                    "phone_number = ?, address = ?, disease = ?, chronic_diseases = ?, allergies = ?, previous_diseases = ?, " +
                    "hereditary_diseases = ? WHERE id = ?";

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = PostgreSQLDatabase.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, patient.getFirstName());
            statement.setString(2, patient.getLastName());
            statement.setString(3, patient.getMiddleName());
            statement.setDate(4, (patient.getDateOfBirth() != null) ? new Date(patient.getDateOfBirth().getTime()) : null);
            statement.setString(5, patient.getPhoneNumber());
            statement.setString(6, patient.getAddress());
            statement.setString(7, patient.getDisease());
            statement.setString(8, patient.getChronicDiseases());
            statement.setString(9, patient.getAllergies());
            statement.setString(10, patient.getPreviousDiseases());
            statement.setString(11, patient.getHereditaryDiseases());
            statement.setInt(12, patient.getId());

            statement.executeUpdate();
            System.out.println("Patient updated successfully.");

        } catch (SQLException e) {
            System.err.println("Error updating patient: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Не закрываем соединение здесь
            try {
                if (statement != null) statement.close();
                // Не закрываем connection
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM patients WHERE id = ?";

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = PostgreSQLDatabase.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            statement.executeUpdate();
            System.out.println("Patient deleted successfully.");

        } catch (SQLException e) {
            System.err.println("Error deleting patient: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Не закрываем соединение здесь
            try {
                if (statement != null) statement.close();
                // Не закрываем connection
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    public Patient getPatientByPhone(String phone) {
        String sql = "SELECT id, first_name, last_name, middle_name, date_of_birth, phone_number, address, disease, chronic_diseases, allergies, previous_diseases, hereditary_diseases FROM patients WHERE phone_number = ?";
        Patient patient = null;

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = PostgreSQLDatabase.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            statement.setString(1, phone);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                patient = new Patient();
                patient.setId(resultSet.getInt("id"));
                patient.setFirstName(resultSet.getString("first_name"));
                patient.setLastName(resultSet.getString("last_name"));
                patient.setMiddleName(resultSet.getString("middle_name"));
                patient.setDateOfBirth(resultSet.getDate("date_of_birth"));
                patient.setPhoneNumber(resultSet.getString("phone_number"));
                patient.setAddress(resultSet.getString("address"));
                patient.setDisease(resultSet.getString("disease"));
                patient.setChronicDiseases(resultSet.getString("chronic_diseases"));
                patient.setAllergies(resultSet.getString("allergies"));
                patient.setPreviousDiseases(resultSet.getString("previous_diseases"));
                patient.setHereditaryDiseases(resultSet.getString("hereditary_diseases"));
            }

        } catch (SQLException e) {
            System.err.println("Error finding patient by phone: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Не закрываем соединение здесь
            try {
                if (resultSet != null) resultSet.close();
                if (statement != null) statement.close();
                // Не закрываем connection
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }

        return patient;
    }
}
