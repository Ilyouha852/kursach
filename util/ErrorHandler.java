package util;

import javax.swing.*;

import util.exceptions.ClinicException;
import util.exceptions.DataAccessException;
import util.exceptions.ValidationException;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ErrorHandler {
    private static final Logger LOGGER = Logger.getLogger(ErrorHandler.class.getName());

    public static void handleError(Throwable e) {
        String message;
        String title;

        if (e instanceof ValidationException) {
            message = e.getMessage();
            title = "Ошибка валидации";
            LOGGER.log(Level.WARNING, "Ошибка валидации: " + e.getMessage());
        } else if (e instanceof DataAccessException) {
            message = "Ошибка доступа к данным: " + e.getMessage();
            title = "Ошибка базы данных";
            LOGGER.log(Level.SEVERE, "Ошибка доступа к данным", e);
        } else if (e instanceof ClinicException) {
            message = e.getMessage();
            title = "Ошибка приложения";
            LOGGER.log(Level.SEVERE, "Ошибка приложения", e);
        } else {
            message = "Произошла непредвиденная ошибка: " + e.getMessage();
            title = "Системная ошибка";
            LOGGER.log(Level.SEVERE, "Непредвиденная ошибка", e);
        }

        SwingUtilities.invokeLater(() -> 
            JOptionPane.showMessageDialog(
                null,
                message,
                title,
                JOptionPane.ERROR_MESSAGE
            )
        );
    }

    public static void logError(String message, Throwable e) {
        LOGGER.log(Level.SEVERE, message, e);
    }

    public static void logWarning(String message) {
        LOGGER.log(Level.WARNING, message);
    }

    public static void logInfo(String message) {
        LOGGER.log(Level.INFO, message);
    }
} 