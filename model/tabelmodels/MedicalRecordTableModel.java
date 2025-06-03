package model.tabelmodels;

import java.text.SimpleDateFormat;

import model.entities.MedicalRecord;

public class MedicalRecordTableModel extends AbstractCustomTableModel<MedicalRecord> {
    private static final String[] COLUMN_NAMES = {"ID", "Дата", "ID пациента", "ID врача", "Диагноз", "Процедуры", "Заметки"};
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

    public MedicalRecordTableModel() {
        super(COLUMN_NAMES);
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        MedicalRecord record = data.get(rowIndex);
        switch (columnIndex) {
            case 0: return record.getId();
            case 1: return record.getRecordDate() != null ? dateFormat.format(record.getRecordDate()) : "";
            case 2: return record.getPatientId();
            case 3: return record.getDoctorId();
            case 4: return record.getDiagnosis();
            case 5: return record.getProceduresPerformed();
            case 6: return record.getNotes();
            default: return null;
        }
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
} 