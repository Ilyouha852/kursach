package data.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class PostgreSQLDatabase {

    private static PostgreSQLDatabase instance;
    private Connection connection;
    private static final String URL = "jdbc:postgresql://localhost:5432/clinic_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "798551432005";

    private PostgreSQLDatabase() {
        try {
            initializeConnection();
        } catch (SQLException e) {
            System.err.println("Error initializing database connection: " + e.getMessage());
        }
    }

    public static PostgreSQLDatabase getInstance() {
        if (instance == null) {
            instance = new PostgreSQLDatabase();
        }
        return instance;
    }

    private void initializeConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connected to PostgreSQL database!");
        }
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
             initializeConnection();
        }
        return connection;
    }

    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Connection to PostgreSQL database closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing connection: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
