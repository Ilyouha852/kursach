package data.repository;

import data.db.PostgreSQLDatabase;
import model.Appointment;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentRepository {

    public List<Appointment> findAll() {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT id, appointment_date_time, procedure_type, status, patient_id, doctor_id FROM appointments";

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = PostgreSQLDatabase.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Appointment appointment = new Appointment();
                appointment.setId(resultSet.getInt("id"));
                appointment.setAppointmentDateTime(resultSet.getTimestamp("appointment_date_time"));
                appointment.setProcedureType(resultSet.getString("procedure_type"));
                appointment.setStatus(resultSet.getString("status"));
                appointment.setPatientId(resultSet.getInt("patient_id"));
                appointment.setDoctorId(resultSet.getInt("doctor_id"));
                appointments.add(appointment);
            }

        } catch (SQLException e) {
            System.err.println("Error retrieving appointments: " + e.getMessage());
            e.printStackTrace();
            // Убираем попытку переподключения здесь
            // if (e.getMessage().contains("connection") || e.getMessage().contains("closed")) {
            //     try {
            //         PostgreSQLDatabase.getInstance().getConnection();
            //     } catch (SQLException ex) {
            //         System.err.println("Failed to reconnect: " + ex.getMessage());
            //     }
            // }
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

        return appointments;
    }

    public Appointment findById(int id) {
        String sql = "SELECT id, appointment_date_time, procedure_type, status, patient_id, doctor_id FROM appointments WHERE id = ?";
        Appointment appointment = null;

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            connection = PostgreSQLDatabase.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            statement.setInt(1, id);

            resultSet = statement.executeQuery();

                if (resultSet.next()) {
                    appointment = new Appointment();
                    appointment.setId(resultSet.getInt("id"));
                    appointment.setAppointmentDateTime(resultSet.getTimestamp("appointment_date_time"));
                    appointment.setProcedureType(resultSet.getString("procedure_type"));
                    appointment.setStatus(resultSet.getString("status"));
                    appointment.setPatientId(resultSet.getInt("patient_id"));
                    appointment.setDoctorId(resultSet.getInt("doctor_id"));
            }

        } catch (SQLException e) {
            System.err.println("Error finding appointment by id: " + e.getMessage());
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

        return appointment;
    }

    public void save(Appointment appointment) {
        String sql = "INSERT INTO appointments (appointment_date_time, procedure_type, status, patient_id, doctor_id) VALUES (?, ?, ?, ?, ?) RETURNING id";

        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet generatedKeys = null;

        try {
            connection = PostgreSQLDatabase.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            statement.setTimestamp(1, new Timestamp(appointment.getAppointmentDateTime().getTime()));
            statement.setString(2, appointment.getProcedureType());
            statement.setString(3, appointment.getStatus());
            statement.setInt(4, appointment.getPatientId());
            statement.setInt(5, appointment.getDoctorId());

            int affectedRows = statement.executeUpdate();

            if (affectedRows > 0) {
                generatedKeys = statement.getGeneratedKeys();
                if (generatedKeys.next()) {
                    appointment.setId(generatedKeys.getInt(1));
                }
            } else {
                throw new SQLException("Creating appointment failed, no rows affected.");
            }

        } catch (SQLException e) {
            System.err.println("Error saving appointment: " + e.getMessage());
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

    public void update(Appointment appointment) {
        String sql = "UPDATE appointments SET appointment_date_time = ?, procedure_type = ?, status = ?, patient_id = ?, doctor_id = ? WHERE id = ?";

        Connection connection = null;
        PreparedStatement statement = null;

        try {
            connection = PostgreSQLDatabase.getInstance().getConnection();
            statement = connection.prepareStatement(sql);
            statement.setTimestamp(1, new Timestamp(appointment.getAppointmentDateTime().getTime()));
            statement.setString(2, appointment.getProcedureType());
            statement.setString(3, appointment.getStatus());
            statement.setInt(4, appointment.getPatientId());
            statement.setInt(5, appointment.getDoctorId());
            statement.setInt(6, appointment.getId());

            statement.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error updating appointment: " + e.getMessage());
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
        String sql = "DELETE FROM appointments WHERE id = ?";
        try (Connection conn = PostgreSQLDatabase.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Appointment> findByDoctorIdAndDate(int doctorId, Date date) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE doctor_id = ? AND DATE(appointment_date_time) = DATE(?)";
        
        try (Connection conn = PostgreSQLDatabase.getInstance().getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, doctorId);
            pstmt.setDate(2, new java.sql.Date(date.getTime()));
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Appointment appointment = new Appointment();
                    appointment.setId(rs.getInt("id"));
                    appointment.setAppointmentDateTime(rs.getTimestamp("appointment_date_time"));
                    appointment.setProcedureType(rs.getString("procedure_type"));
                    appointment.setStatus(rs.getString("status"));
                    appointment.setPatientId(rs.getInt("patient_id"));
                    appointment.setDoctorId(rs.getInt("doctor_id"));
                    appointments.add(appointment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return appointments;
    }

    public List<Appointment> getAppointmentsByDoctorAndPeriod(int doctorId, Date startDate, Date endDate) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE doctor_id = ? AND appointment_date_time BETWEEN ? AND ?";

        try (Connection connection = PostgreSQLDatabase.getInstance().getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, doctorId);
            statement.setTimestamp(2, new Timestamp(startDate.getTime()));
            statement.setTimestamp(3, new Timestamp(endDate.getTime()));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    Appointment appointment = new Appointment();
                    appointment.setId(resultSet.getInt("id"));
                    appointment.setAppointmentDateTime(resultSet.getTimestamp("appointment_date_time"));
                    appointment.setPatientId(resultSet.getInt("patient_id"));
                    appointment.setDoctorId(resultSet.getInt("doctor_id"));
                    appointment.setProcedureType(resultSet.getString("procedure_type"));
                    appointment.setStatus(resultSet.getString("status"));
                    appointments.add(appointment);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }
}
