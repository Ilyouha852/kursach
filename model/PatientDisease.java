package model;

public enum PatientDisease {
    CARIES("Кариес"),
    PULPITIS("Пульпит"),
    PERIODONTITIS("Пародонтит"),
    GINGIVITIS("Гингивит"),
    TOOTH_ABSCESS("Абсцесс зуба"),
    IMPACTED_TOOTH("Ретинированный зуб"),
    MALOCCLUSION("Неправильный прикус"),
    DENTAL_TRAUMA("Травма зуба"),
    ENAMEL_HYPOPLASIA("Гипоплазия эмали"),
    TOOTH_SENSITIVITY("Повышенная чувствительность");

    private final String displayName;

    PatientDisease(String displayName) {
        this.displayName = displayName;
    }

    @Override
    public String toString() {
        return displayName;
    }

    /**
     * Получить заболевание по его отображаемому названию
     * @param displayName отображаемое название заболевания
     * @return соответствующее значение enum или null, если не найдено
     */
    public static PatientDisease fromDisplayName(String displayName) {
        for (PatientDisease disease : values()) {
            if (disease.displayName.equals(displayName)) {
                return disease;
            }
        }
        return null;
    }
} 