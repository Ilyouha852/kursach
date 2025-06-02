package data.repository;

import data.db.PostgreSQLDatabase;
import model.MedicalRecord;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordRepository {

    public List<MedicalRecord> findAll() {
        List<MedicalRecord> medicalRecords = new ArrayList<>();
        String sql = "SELECT id, record_date, diagnosis, procedures_performed, notes, patient_id, doctor_id FROM medical_records";

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = PostgreSQLDatabase.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                MedicalRecord medicalRecord = new MedicalRecord();
                medicalRecord.setId(resultSet.getInt("id"));
                medicalRecord.setRecordDate(resultSet.getDate("record_date"));
                medicalRecord.setDiagnosis(resultSet.getString("diagnosis"));
                medicalRecord.setProceduresPerformed(resultSet.getString("procedures_performed"));
                medicalRecord.setNotes(resultSet.getString("notes"));
                medicalRecord.setPatientId(resultSet.getInt("patient_id"));
                medicalRecord.setDoctorId(resultSet.getInt("doctor_id"));
                medicalRecords.add(medicalRecord);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving medical records: " + e.getMessage());
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

        return medicalRecords;
    }

    public MedicalRecord findById(int id) {
        String sql = "SELECT id, record_date, diagnosis, procedures_performed, notes, patient_id, doctor_id FROM medical_records WHERE id = ?";
        MedicalRecord medicalRecord = null;

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = PostgreSQLDatabase.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                medicalRecord = new MedicalRecord();
                medicalRecord.setId(resultSet.getInt("id"));
                medicalRecord.setRecordDate(resultSet.getDate("record_date"));
                medicalRecord.setDiagnosis(resultSet.getString("diagnosis"));
                medicalRecord.setProceduresPerformed(resultSet.getString("procedures_performed"));
                medicalRecord.setNotes(resultSet.getString("notes"));
                medicalRecord.setPatientId(resultSet.getInt("patient_id"));
                medicalRecord.setDoctorId(resultSet.getInt("doctor_id"));
            }

        } catch (SQLException e) {
            System.err.println("Error finding medical record by id: " + e.getMessage());
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

        return medicalRecord;
    }

    public void save(MedicalRecord medicalRecord) {
        String sql = "INSERT INTO medical_records (record_date, diagnosis, procedures_performed, notes, patient_id, doctor_id) VALUES (?, ?, ?, ?, ?, ?) RETURNING id";

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            connection = PostgreSQLDatabase.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            statement.setDate(1, new java.sql.Date(medicalRecord.getRecordDate().getTime()));
            statement.setString(2, medicalRecord.getDiagnosis());
            statement.setString(3, medicalRecord.getProceduresPerformed());
            statement.setString(4, medicalRecord.getNotes());
            statement.setInt(5, medicalRecord.getPatientId());
            statement.setInt(6, medicalRecord.getDoctorId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    medicalRecord.setId(generatedKeys.getInt(1));
                }
            } else {
                throw new SQLException("Creating medical record failed, no rows affected.");
            }

        } catch (SQLException e) {
            System.err.println("Error saving medical record: " + e.getMessage());
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

    public void update(MedicalRecord medicalRecord) {
        String sql = "UPDATE medical_records SET record_date = ?, diagnosis = ?, procedures_performed = ?, notes = ?, patient_id = ?, doctor_id = ? WHERE id = ?";

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = PostgreSQLDatabase.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            statement.setDate(1, new java.sql.Date(medicalRecord.getRecordDate().getTime()));
            statement.setString(2, medicalRecord.getDiagnosis());
            statement.setString(3, medicalRecord.getProceduresPerformed());
            statement.setString(4, medicalRecord.getNotes());
            statement.setInt(5, medicalRecord.getPatientId());
            statement.setInt(6, medicalRecord.getDoctorId());
            statement.setInt(7, medicalRecord.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating medical record: " + e.getMessage());
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
        String sql = "DELETE FROM medical_records WHERE id = ?";

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = PostgreSQLDatabase.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            statement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error deleting medical record: " + e.getMessage());
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