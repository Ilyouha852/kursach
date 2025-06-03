package ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import ui.dialogs.AddDiseaseDialog;
import controller.PatientController;
import model.entities.Patient;
import model.entities.PatientDisease;
import util.ValidationUtils;
import ui.components.PatientTablePanel;
import ui.dialogs.PatientEditDialog;

public class PatientPanel extends JPanel {

    private final PatientController patientController;
    private PatientTablePanel tablePanel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private List<Patient> allPatients;

    public PatientPanel(PatientController patientController) {
        this.patientController = patientController;
        setLayout(new BorderLayout());
        tablePanel = new PatientTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        JPanel controlPanel = new JPanel();
        addButton = new JButton("Добавить");
        editButton = new JButton("Редактировать");
        deleteButton = new JButton("Удалить");
        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        add(controlPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addPatient());
        editButton.addActionListener(e -> editPatient());
        deleteButton.addActionListener(e -> deletePatient());

        loadPatients();
    }

    private void loadPatients() {
        allPatients = patientController.getAllPatients();
        tablePanel.updateTable(allPatients);
    }

    private void addPatient() {
        PatientEditDialog dialog = new PatientEditDialog(SwingUtilities.getWindowAncestor(this), "Добавление пациента", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
            try {
                Patient patient = new Patient();
                patient.setLastName(dialog.getLastName());
                patient.setFirstName(dialog.getFirstName());
                patient.setMiddleName(dialog.getMiddleName());
                patient.setDateOfBirth(dialog.getBirthDateAsDate());
                patient.setPhoneNumber(dialog.getPhone());
                patient.setDisease(dialog.getDisease());
                patientController.savePatient(patient);
                    loadPatients();
                } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка при добавлении пациента: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editPatient() {
        Patient selected = tablePanel.getSelectedPatient();
        if (selected != null) {
            PatientEditDialog dialog = new PatientEditDialog(SwingUtilities.getWindowAncestor(this), "Редактирование пациента", selected);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                try {
                    selected.setLastName(dialog.getLastName());
                    selected.setFirstName(dialog.getFirstName());
                    selected.setMiddleName(dialog.getMiddleName());
                    selected.setDateOfBirth(dialog.getBirthDateAsDate());
                    selected.setPhoneNumber(dialog.getPhone());
                    selected.setDisease(dialog.getDisease());
                    patientController.updatePatient(selected);
                    loadPatients();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Ошибка при обновлении пациента: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Выберите пациента для редактирования", "Предупреждение", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deletePatient() {
        Patient selected = tablePanel.getSelectedPatient();
        if (selected != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите удалить этого пациента?", "Подтверждение", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                patientController.deletePatient(selected.getId());
                loadPatients();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Выберите пациента для удаления", "Предупреждение", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void refreshData() {
        loadPatients();
    }
}
