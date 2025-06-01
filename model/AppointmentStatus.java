package model;

public enum AppointmentStatus {
    PLANNED("Запланировано"),
    COMPLETED("Завершено"),
    CANCELLED("Отменено");

    private final String displayName;

    AppointmentStatus(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    public static AppointmentStatus fromDisplayName(String displayName) {
        for (AppointmentStatus status : values()) {
            if (status.displayName.equals(displayName)) {
                return status;
            }
        }
        return null;
    }
} 