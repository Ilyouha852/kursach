package ui.panels;

import controller.MedicalRecordController;
import model.entities.MedicalRecord;
import ui.components.MedicalRecordTablePanel;
import ui.dialogs.MedicalRecordEditDialog;

import javax.swing.*;
import java.awt.*;

public class MedicalRecordPanel extends JPanel {
    private final MedicalRecordController medicalRecordController = new MedicalRecordController();
    private MedicalRecordTablePanel tablePanel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;

    public MedicalRecordPanel() {
        setLayout(new BorderLayout());
        initializeComponents();
        loadMedicalRecords();
    }

    private void initializeComponents() {
        tablePanel = new MedicalRecordTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Добавить");
        editButton = new JButton("Редактировать");
        deleteButton = new JButton("Удалить");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);

        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addMedicalRecord());
        editButton.addActionListener(e -> editMedicalRecord());
        deleteButton.addActionListener(e -> deleteMedicalRecord());
    }

    private void loadMedicalRecords() {
        tablePanel.updateTable(medicalRecordController.getAllMedicalRecords());
    }

    private void addMedicalRecord() {
        MedicalRecordEditDialog dialog = new MedicalRecordEditDialog(
            SwingUtilities.getWindowAncestor(this),
            "Добавление медицинской записи",
            null
        );
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            try {
                MedicalRecord newRecord = new MedicalRecord();
                newRecord.setRecordDate(dialog.getRecordDate());
                newRecord.setDiagnosis(dialog.getDiagnosis());
                newRecord.setProceduresPerformed(dialog.getProceduresPerformed());
                newRecord.setNotes(dialog.getNotes());
                newRecord.setPatientId(dialog.getPatientId());
                newRecord.setDoctorId(dialog.getDoctorId());

                medicalRecordController.saveMedicalRecord(newRecord);
                loadMedicalRecords();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                    "Ошибка при добавлении медицинской записи: " + ex.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editMedicalRecord() {
        MedicalRecord selected = tablePanel.getSelectedMedicalRecord();
        if (selected != null) {
            MedicalRecordEditDialog dialog = new MedicalRecordEditDialog(
                SwingUtilities.getWindowAncestor(this),
                "Редактирование медицинской записи",
                selected
            );
            dialog.setVisible(true);

            if (dialog.isConfirmed()) {
                try {
                    selected.setRecordDate(dialog.getRecordDate());
                    selected.setDiagnosis(dialog.getDiagnosis());
                    selected.setProceduresPerformed(dialog.getProceduresPerformed());
                    selected.setNotes(dialog.getNotes());
                    selected.setPatientId(dialog.getPatientId());
                    selected.setDoctorId(dialog.getDoctorId());

                    medicalRecordController.updateMedicalRecord(selected);
                    loadMedicalRecords();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this,
                        "Ошибка при обновлении медицинской записи: " + ex.getMessage(),
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Выберите медицинскую запись для редактирования",
                "Предупреждение",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteMedicalRecord() {
        MedicalRecord selected = tablePanel.getSelectedMedicalRecord();
        if (selected != null) {
            int confirm = JOptionPane.showConfirmDialog(this,
                "Вы уверены, что хотите удалить эту медицинскую запись?",
                "Подтверждение",
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                medicalRecordController.deleteMedicalRecord(selected.getId());
                loadMedicalRecords();
            }
        } else {
            JOptionPane.showMessageDialog(this,
                "Выберите медицинскую запись для удаления",
                "Предупреждение",
                JOptionPane.WARNING_MESSAGE);
        }
    }

    public void refreshData() {
        loadMedicalRecords();
    }
}
