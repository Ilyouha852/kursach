package ui.dialogs;

import javax.swing.*;
import javax.swing.text.MaskFormatter;

import model.entities.MedicalRecord;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MedicalRecordEditDialog extends JDialog {
    private JFormattedTextField recordDateField;
    private JTextField diagnosisField;
    private JTextField proceduresPerformedField;
    private JTextArea notesArea;
    private JTextField patientIdField;
    private JTextField doctorIdField;
    private boolean confirmed = false;

    public MedicalRecordEditDialog(Window owner, String title, MedicalRecord record) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        initializeComponents(record);
        pack();
        setLocationRelativeTo(owner);
    }

    private void initializeComponents(MedicalRecord record) {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel recordDateLabel = new JLabel("Дата записи (yyyy-MM-dd):");
        recordDateField = createDateField();
        if (record != null && record.getRecordDate() != null) {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            recordDateField.setText(df.format(record.getRecordDate()));
        }

        JLabel diagnosisLabel = new JLabel("Диагноз:");
        diagnosisField = new JTextField(record != null ? record.getDiagnosis() : "");

        JLabel proceduresPerformedLabel = new JLabel("Процедуры:");
        proceduresPerformedField = new JTextField(record != null ? record.getProceduresPerformed() : "");

        JLabel notesLabel = new JLabel("Заметки:");
        notesArea = new JTextArea(record != null ? record.getNotes() : "");
        JScrollPane notesScrollPane = new JScrollPane(notesArea);

        JLabel patientIdLabel = new JLabel("ID пациента:");
        patientIdField = new JTextField(record != null ? String.valueOf(record.getPatientId()) : "");

        JLabel doctorIdLabel = new JLabel("ID врача:");
        doctorIdField = new JTextField(record != null ? String.valueOf(record.getDoctorId()) : "");

        mainPanel.add(recordDateLabel);
        mainPanel.add(recordDateField);
        mainPanel.add(diagnosisLabel);
        mainPanel.add(diagnosisField);
        mainPanel.add(proceduresPerformedLabel);
        mainPanel.add(proceduresPerformedField);
        mainPanel.add(notesLabel);
        mainPanel.add(notesScrollPane);
        mainPanel.add(patientIdLabel);
        mainPanel.add(patientIdField);
        mainPanel.add(doctorIdLabel);
        mainPanel.add(doctorIdField);

        JPanel buttonPanel = new JPanel();
        JButton confirmButton = new JButton("Подтвердить");
        JButton cancelButton = new JButton("Отмена");
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        confirmButton.addActionListener(e -> {
            if (validateFields()) {
                confirmed = true;
                dispose();
            }
        });
        cancelButton.addActionListener(e -> dispose());
    }

    private JFormattedTextField createDateField() {
        try {
            MaskFormatter maskFormatter = new MaskFormatter("####-##-##");
            maskFormatter.setPlaceholderCharacter('_');
            return new JFormattedTextField(maskFormatter);
        } catch (ParseException e) {
            return new JFormattedTextField();
        }
    }

    private boolean validateFields() {
        String recordDate = recordDateField.getText().trim();
        String diagnosis = diagnosisField.getText().trim();
        String patientId = patientIdField.getText().trim();
        String doctorId = doctorIdField.getText().trim();

        if (!isValidDate(recordDate)) {
            JOptionPane.showMessageDialog(this, "Неверный формат даты. Используйте формат: ГГГГ-ММ-ДД", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        if (diagnosis.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Поле 'Диагноз' не может быть пустым", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            Integer.parseInt(patientId);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID пациента должен быть числом", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            Integer.parseInt(doctorId);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "ID врача должен быть числом", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }

    private boolean isValidDate(String dateStr) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
            df.setLenient(false);
            df.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Date getRecordDate() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.parse(recordDateField.getText().trim());
    }

    public String getDiagnosis() {
        return diagnosisField.getText().trim();
    }

    public String getProceduresPerformed() {
        return proceduresPerformedField.getText().trim();
    }

    public String getNotes() {
        return notesArea.getText().trim();
    }

    public int getPatientId() {
        return Integer.parseInt(patientIdField.getText().trim());
    }

    public int getDoctorId() {
        return Integer.parseInt(doctorIdField.getText().trim());
    }
} 