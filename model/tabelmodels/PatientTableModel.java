package model.tabelmodels;

import java.text.SimpleDateFormat;

import model.entities.Patient;

public class PatientTableModel extends AbstractCustomTableModel<Patient> {
    private static final String[] COLUMN_NAMES = {"ID", "Фамилия", "Имя", "Отчество", "Дата рождения", "Телефон", "Заболевание"};
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public PatientTableModel() {
        super(COLUMN_NAMES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Patient patient = data.get(rowIndex);
        switch (columnIndex) {
            case 0: return patient.getId();
            case 1: return patient.getLastName();
            case 2: return patient.getFirstName();
            case 3: return patient.getMiddleName();
            case 4: return patient.getDateOfBirth() != null ? dateFormat.format(patient.getDateOfBirth()) : "";
            case 5: return patient.getPhoneNumber();
            case 6: return patient.getDisease();
            default: return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
} 