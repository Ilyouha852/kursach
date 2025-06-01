package util;

import java.util.regex.Pattern;
import java.util.Calendar;
import java.util.Date;

public class ValidationUtils {
    // Паттерн для валидации email
    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );
    
    // Паттерн для валидации номера телефона (российский формат)
    private static final Pattern PHONE_PATTERN = Pattern.compile(
        "^(\\+7|8)[-\\s]?\\(?\\d{3}\\)?[-\\s]?\\d{3}[-\\s]?\\d{2}[-\\s]?\\d{2}$"
    );

    public static boolean isValidEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        // Проверяем длину email
        if (email.length() > 100) {
            return false;
        }
        // Проверяем наличие специальных символов
        if (email.contains(" ") || email.contains("'") || email.contains("\"")) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email).matches();
    }

    public static boolean isValidPhone(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }
        // Удаляем все нецифровые символы для проверки длины
        String digits = phone.replaceAll("\\D", "");
        if (digits.length() != 11) {
            return false;
        }
        return PHONE_PATTERN.matcher(phone).matches();
    }

    public static boolean isValidName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return false;
        }
        // Проверяем длину имени
        if (name.length() > 50) {
            return false;
        }
        // Проверяем, что имя содержит только буквы, пробелы и дефисы
        return name.matches("^[А-Яа-яЁё\\s-]+$");
    }

    public static boolean isValidAddress(String address) {
        if (address == null || address.trim().isEmpty()) {
            return false;
        }
        // Проверяем минимальную и максимальную длину адреса
        return address.length() >= 5 && address.length() <= 200;
    }

    public static boolean isValidProcedureType(String procedureType) {
        if (procedureType == null || procedureType.trim().isEmpty()) {
            return false;
        }
        // Проверяем минимальную и максимальную длину названия процедуры
        return procedureType.length() >= 3 && procedureType.length() <= 100;
    }

    public static String formatPhoneNumber(String phone) {
        if (phone == null || phone.trim().isEmpty()) {
            return phone;
        }
        // Удаляем все нецифровые символы
        String digits = phone.replaceAll("\\D", "");
        // Проверяем длину номера
        if (digits.length() != 11) {
            throw new IllegalArgumentException("Номер телефона должен содержать 11 цифр");
        }
        // Если номер начинается с 8, заменяем на +7
        if (digits.startsWith("8")) {
            digits = "7" + digits.substring(1);
        }
        // Форматируем номер в формат +7 (XXX) XXX-XX-XX
        return String.format("+7 (%s) %s-%s-%s",
            digits.substring(1, 4),
            digits.substring(4, 7),
            digits.substring(7, 9),
            digits.substring(9, 11)
        );
    }

    public static boolean isValidAppointmentTime(Date dateTime) {
        if (dateTime == null) {
            return false;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(dateTime);

        // Получаем час и минуты
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        // Проверяем, что время находится в рабочем диапазоне (8:00 - 17:00)
        if (hour < 8 || hour >= 17) {
            return false;
        }

        // Проверяем, что время не попадает в обеденный перерыв (13:00 - 14:00)
        if (hour == 13) {
            return false;
        }

        // Проверяем, что минуты кратны 30 (записи только на каждые полчаса)
        if (minute != 0 && minute != 30) {
            return false;
        }

        return true;
    }
} 