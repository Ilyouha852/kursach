package util;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class LogConfig {
    private static final String LOG_FILE = "clinic.log";
    private static final int MAX_FILE_SIZE = 1024 * 1024; // 1 MB
    private static final int MAX_FILES = 5;

    public static void configure() {
        try {
            // Создаем директорию для логов, если её нет
            File logDir = new File("logs");
            if (!logDir.exists()) {
                logDir.mkdir();
            }

            // Настраиваем FileHandler
            FileHandler fileHandler = new FileHandler(
                "logs/" + LOG_FILE,
                MAX_FILE_SIZE,
                MAX_FILES,
                true // append
            );
            fileHandler.setFormatter(new SimpleFormatter());
            fileHandler.setLevel(Level.ALL);

            // Получаем корневой логгер
            Logger rootLogger = Logger.getLogger("");
            rootLogger.addHandler(fileHandler);
            rootLogger.setLevel(Level.ALL);

            // Отключаем вывод в консоль
            rootLogger.getHandlers()[0].setLevel(Level.OFF);

        } catch (IOException e) {
            System.err.println("Ошибка при настройке логирования: " + e.getMessage());
        }
    }
} 