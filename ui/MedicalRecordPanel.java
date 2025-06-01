package ui;

import controller.MedicalRecordController;
import model.MedicalRecord;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MedicalRecordPanel extends JPanel {

    private JTextArea medicalRecordListArea;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    private final MedicalRecordController medicalRecordController = new MedicalRecordController();

    public MedicalRecordPanel() {
        setLayout(new BorderLayout());

        medicalRecordListArea = new JTextArea("Список медицинских записей:\n");
        JScrollPane scrollPane = new JScrollPane(medicalRecordListArea);

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Добавить");
        editButton = new JButton("Редактировать");
        deleteButton = new JButton("Удалить");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        loadMedicalRecords();

        addButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addMedicalRecord();
            }
        });

        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editMedicalRecord();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteMedicalRecord();
            }
        });
    }

    private void loadMedicalRecords() {
        medicalRecordListArea.setText("Список медицинских записей:\n");
        List<MedicalRecord> medicalRecords = medicalRecordController.getAllMedicalRecords();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        for (MedicalRecord record : medicalRecords) {
            medicalRecordListArea.append(record.getId() + ": " + dateFormat.format(record.getRecordDate()) +
                    ", Patient ID: " + record.getPatientId() + ", Диагноз: " + record.getDiagnosis() + "\n");
        }
    }

    private void addMedicalRecord() {
        JDialog addDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Добавить медицинскую запись", true);
        addDialog.setLayout(new GridLayout(6, 2));

        JLabel recordDateLabel = new JLabel("Дата записи (yyyy-MM-dd):");
        JTextField recordDateField = new JTextField();
        JLabel diagnosisLabel = new JLabel("Диагноз:");
        JTextField diagnosisField = new JTextField();
        JLabel proceduresPerformedLabel = new JLabel("Процедуры:");
        JTextField proceduresPerformedField = new JTextField();
        JLabel notesLabel = new JLabel("Заметки:");
        JTextArea notesArea = new JTextArea();
        JScrollPane notesScrollPane = new JScrollPane(notesArea);  // Added scroll pane
        JLabel patientIdLabel = new JLabel("ID пациента:");
        JTextField patientIdField = new JTextField();

        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        addDialog.add(recordDateLabel);
        addDialog.add(recordDateField);
        addDialog.add(diagnosisLabel);
        addDialog.add(diagnosisField);
        addDialog.add(proceduresPerformedLabel);
        addDialog.add(proceduresPerformedField);
        addDialog.add(notesLabel);
        addDialog.add(notesScrollPane);
        addDialog.add(patientIdLabel);
        addDialog.add(patientIdField);
        addDialog.add(saveButton);
        addDialog.add(cancelButton);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    MedicalRecord newMedicalRecord = new MedicalRecord();

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Date parsedDate = null;
                    try {
                        parsedDate = dateFormat.parse(recordDateField.getText());
                    } catch (ParseException ex) {
                        JOptionPane.showMessageDialog(addDialog, "Неверный формат даты (yyyy-MM-dd)", "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    newMedicalRecord.setRecordDate(parsedDate);

                    newMedicalRecord.setDiagnosis(diagnosisField.getText());
                    newMedicalRecord.setProceduresPerformed(proceduresPerformedField.getText());
                    newMedicalRecord.setNotes(notesArea.getText());

                    try {
                        newMedicalRecord.setPatientId(Integer.parseInt(patientIdField.getText()));
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(addDialog, "Неверный формат ID пациента", "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    medicalRecordController.saveMedicalRecord(newMedicalRecord);
                    loadMedicalRecords();
                    addDialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(addDialog, "Ошибка при сохранении медицинской записи: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDialog.dispose();
            }
        });

        addDialog.setSize(400, 300);
        addDialog.setLocationRelativeTo((JFrame) SwingUtilities.getWindowAncestor(this));
        addDialog.setVisible(true);
    }

    private void editMedicalRecord() {
        String idString = JOptionPane.showInputDialog(this, "Введите ID медицинской записи для редактирования:");
        if (idString == null || idString.isEmpty()) {
            return;
        }

        try {
            int id = Integer.parseInt(idString);
            MedicalRecord medicalRecordToEdit = medicalRecordController.getMedicalRecordById(id);

            if (medicalRecordToEdit == null) {
                JOptionPane.showMessageDialog(this, "Медицинская запись с ID " + id + " не найдена.", "Ошибка", JOptionPane.ERROR_MESSAGE);
                return;
            }

            JDialog editDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Редактировать медицинскую запись", true);
            editDialog.setLayout(new GridLayout(6, 2));

            JLabel recordDateLabel = new JLabel("Дата записи (yyyy-MM-dd):");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            JTextField recordDateField = new JTextField(dateFormat.format(medicalRecordToEdit.getRecordDate()));
            JLabel diagnosisLabel = new JLabel("Диагноз:");
            JTextField diagnosisField = new JTextField(medicalRecordToEdit.getDiagnosis());
            JLabel proceduresPerformedLabel = new JLabel("Процедуры:");
            JTextField proceduresPerformedField = new JTextField(medicalRecordToEdit.getProceduresPerformed());
            JLabel notesLabel = new JLabel("Заметки:");
            JTextArea notesArea = new JTextArea(medicalRecordToEdit.getNotes());
             JScrollPane notesScrollPane = new JScrollPane(notesArea);  // Added scroll pane
            JLabel patientIdLabel = new JLabel("ID пациента:");
            JTextField patientIdField = new JTextField(String.valueOf(medicalRecordToEdit.getPatientId()));

            JButton saveButton = new JButton("Сохранить");
            JButton cancelButton = new JButton("Отмена");

            editDialog.add(recordDateLabel);
            editDialog.add(recordDateField);
            editDialog.add(diagnosisLabel);
            editDialog.add(diagnosisField);
            editDialog.add(proceduresPerformedLabel);
            editDialog.add(proceduresPerformedField);
            editDialog.add(notesLabel);
            editDialog.add(notesScrollPane);
            editDialog.add(patientIdLabel);
            editDialog.add(patientIdField);
            editDialog.add(saveButton);
            editDialog.add(cancelButton);

            saveButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    try {
                        Date parsedDate = null;
                        try {
                            parsedDate = dateFormat.parse(recordDateField.getText());
                        } catch (ParseException ex) {
                            JOptionPane.showMessageDialog(editDialog, "Неверный формат даты (yyyy-MM-dd)", "Ошибка", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                        medicalRecordToEdit.setRecordDate(parsedDate);
                        medicalRecordToEdit.setDiagnosis(diagnosisField.getText());
                        medicalRecordToEdit.setProceduresPerformed(proceduresPerformedField.getText());
                        medicalRecordToEdit.setNotes(notesArea.getText());

                        try {
                            medicalRecordToEdit.setPatientId(Integer.parseInt(patientIdField.getText()));
                        } catch (NumberFormatException ex) {
                            JOptionPane.showMessageDialog(editDialog, "Неверный формат ID пациента", "Ошибка", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        medicalRecordController.updateMedicalRecord(medicalRecordToEdit);
                        loadMedicalRecords();
                        editDialog.dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(editDialog, "Ошибка при сохранении медицинской записи: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            cancelButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    editDialog.dispose();
                }
            });

            editDialog.setSize(400, 300);
            editDialog.setLocationRelativeTo((JFrame) SwingUtilities.getWindowAncestor(this));
            editDialog.setVisible(true);

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Неверный формат ID медицинской записи.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteMedicalRecord() {
        String idString = JOptionPane.showInputDialog(this, "Введите ID медицинской записи для удаления:");
        if (idString == null || idString.isEmpty()) {
            return;
        }

        try {
            int id = Integer.parseInt(idString);
            int choice = JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите удалить медицинскую запись с ID " + id + "?", "Подтверждение удаления", JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION) {
                medicalRecordController.deleteMedicalRecord(id);
                loadMedicalRecords();
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Неверный формат ID медицинской записи.", "Ошибка", JOptionPane.ERROR_MESSAGE);
        }
    }
}
