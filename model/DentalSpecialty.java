package model;

public class DentalSpecialty {
    // Константы специализаций
    public static final String THERAPIST = "Терапевт";
    public static final String SURGEON = "Хирург";
    public static final String ORTHODONTIST = "Ортодонт";
    public static final String ENDODONTIST = "Эндодонтист";
    public static final String PERIODONTIST = "Пародонтолог";
    public static final String PROSTHODONTIST = "Ортопед";
    public static final String PEDIATRIC_DENTIST = "Детский стоматолог";
    
    // Константы заболеваний
    public static final String CARIES = "Кариес";
    public static final String PULPITIS = "Пульпит";
    public static final String PERIODONTITIS = "Пародонтит";
    public static final String GINGIVITIS = "Гингивит";
    public static final String TOOTH_ABSCESS = "Абсцесс зуба";
    public static final String IMPACTED_TOOTH = "Ретинированный зуб";
    public static final String MALOCCLUSION = "Неправильный прикус";
    public static final String DENTAL_TRAUMA = "Травма зуба";
    public static final String ENAMEL_HYPOPLASIA = "Гипоплазия эмали";
    public static final String TOOTH_SENSITIVITY = "Повышенная чувствительность";
    
    /**
     * Проверяет, может ли стоматолог с указанной специализацией лечить указанное заболевание
     * 
     * @param specialization специализация стоматолога
     * @param disease заболевание
     * @return true если может лечить, false в противном случае
     */
    public static boolean canTreat(String specialization, String disease) {
        switch (specialization) {
            case THERAPIST:
                return disease.equals(CARIES) || 
                       disease.equals(PULPITIS) || 
                       disease.equals(TOOTH_SENSITIVITY) ||
                       disease.equals(ENAMEL_HYPOPLASIA);
                       
            case SURGEON:
                return disease.equals(PERIODONTITIS) || 
                       disease.equals(TOOTH_ABSCESS) || 
                       disease.equals(IMPACTED_TOOTH) ||
                       disease.equals(DENTAL_TRAUMA);
                       
            case ORTHODONTIST:
                return disease.equals(MALOCCLUSION); 
                
            case ENDODONTIST:
                return disease.equals(PULPITIS) || 
                       disease.equals(TOOTH_ABSCESS);
                       
            case PERIODONTIST:
                return disease.equals(PERIODONTITIS) || 
                       disease.equals(GINGIVITIS);
                       
            case PROSTHODONTIST:
                return disease.equals(DENTAL_TRAUMA) || 
                       disease.equals(ENAMEL_HYPOPLASIA);
                       
            case PEDIATRIC_DENTIST:
                return disease.equals(CARIES) || 
                       disease.equals(ENAMEL_HYPOPLASIA) || 
                       disease.equals(DENTAL_TRAUMA);
                       
            default:
                return false;
        }
    }
} 