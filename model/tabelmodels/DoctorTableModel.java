package model.tabelmodels;

import model.entities.Doctor;

public class DoctorTableModel extends AbstractCustomTableModel<Doctor> {
    private static final String[] COLUMN_NAMES = {"ID", "Фамилия", "Имя", "Отчество", "Специализация", "Телефон", "Email"};

    public DoctorTableModel() {
        super(COLUMN_NAMES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Doctor doctor = data.get(rowIndex);
        switch (columnIndex) {
            case 0: return doctor.getId();
            case 1: return doctor.getLastName();
            case 2: return doctor.getFirstName();
            case 3: return doctor.getMiddleName();
            case 4: return doctor.getSpecialization();
            case 5: return doctor.getPhoneNumber();
            case 6: return doctor.getEmail();
            default: return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
} 