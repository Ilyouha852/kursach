package model.entities;

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

    public static PatientDisease fromDisplayName(String displayName) {
        for (PatientDisease disease : values()) {
            if (disease.displayName.equals(displayName)) {
                return disease;
            }
        }
        return null;
    }
} 