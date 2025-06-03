package ui.components;

import controller.DoctorController;
import controller.PatientController;
import model.entities.Appointment;
import model.entities.Doctor;
import model.entities.Patient;
import controller.AppointmentController;
import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AppointmentFilterPanel extends JPanel {
    private JComboBox<Patient> patientFilterCombo;
    private JComboBox<Doctor> doctorFilterCombo;
    private JFormattedTextField startDateFilterField;
    private JFormattedTextField endDateFilterField;
    private JComboBox<String> procedureTypeCombo;
    private JButton applyFilterButton;
    private JButton clearFilterButton;
    private final DoctorController doctorController;
    private final PatientController patientController;
    private final AppointmentController appointmentController;

    public AppointmentFilterPanel(DoctorController doctorController, PatientController patientController, AppointmentController appointmentController) {
        this.doctorController = doctorController;
        this.patientController = patientController;
        this.appointmentController = appointmentController;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Фильтры"));
        initializeComponents();
        loadData();
        setDefaultDateRange();
    }

    private void setDefaultDateRange() {
        List<Appointment> appointments = appointmentController.getAllAppointments();
        if (!appointments.isEmpty()) {
            Date earliestDate = appointments.stream()
                .map(Appointment::getAppointmentDateTime)
                .min(Date::compareTo)
                .orElse(null);
            
            Date latestDate = appointments.stream()
                .map(Appointment::getAppointmentDateTime)
                .max(Date::compareTo)
                .orElse(null);

            if (earliestDate != null) {
                startDateFilterField.setValue(earliestDate);
            }
            if (latestDate != null) {
                endDateFilterField.setValue(latestDate);
            }
        }
    }

    private void loadData() {
        List<Doctor> doctors = doctorController.getAllDoctors();
        List<Patient> patients = patientController.getAllPatients();
        List<Appointment> appointments = appointmentController.getAllAppointments();

        // Загрузка врачей
        doctorFilterCombo.removeAllItems();
        doctorFilterCombo.addItem(null);
        for (Doctor doctor : doctors) {
            doctorFilterCombo.addItem(doctor);
        }

        // Загрузка пациентов
        patientFilterCombo.removeAllItems();
        patientFilterCombo.addItem(null);
        for (Patient patient : patients) {
            patientFilterCombo.addItem(patient);
        }

        // Загрузка типов процедур
        procedureTypeCombo.removeAllItems();
        procedureTypeCombo.addItem(null);
        Set<String> procedureTypes = appointments.stream()
            .map(Appointment::getProcedureType)
            .filter(type -> type != null && !type.isEmpty())
            .collect(Collectors.toSet());
        for (String type : procedureTypes) {
            procedureTypeCombo.addItem(type);
        }
    }

    private void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Фильтр по дате
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Период:"), gbc);

        gbc.gridx = 1;
        startDateFilterField = createDateField();
        add(startDateFilterField, gbc);

        gbc.gridx = 2;
        add(new JLabel(" - "), gbc);

        gbc.gridx = 3;
        endDateFilterField = createDateField();
        add(endDateFilterField, gbc);

        // Фильтр по пациенту
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Пациент:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        patientFilterCombo = new JComboBox<>();
        patientFilterCombo.addItem(null);
        add(patientFilterCombo, gbc);

        // Фильтр по врачу
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(new JLabel("Врач:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        doctorFilterCombo = new JComboBox<>();
        doctorFilterCombo.addItem(null);
        add(doctorFilterCombo, gbc);

        // Фильтр по типу процедуры
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        add(new JLabel("Тип процедуры:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        procedureTypeCombo = new JComboBox<>();
        procedureTypeCombo.addItem(null);
        add(procedureTypeCombo, gbc);

        // Кнопки фильтрации
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        JPanel filterButtonPanel = new JPanel();
        applyFilterButton = new JButton("Применить фильтры");
        clearFilterButton = new JButton("Сбросить фильтры");
        filterButtonPanel.add(applyFilterButton);
        filterButtonPanel.add(clearFilterButton);
        add(filterButtonPanel, gbc);
    }

    private JFormattedTextField createDateField() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        JFormattedTextField dateField = new JFormattedTextField(dateFormat);
        dateField.setColumns(10);
        return dateField;
    }

    public JComboBox<Patient> getPatientFilterCombo() {
        return patientFilterCombo;
    }

    public JComboBox<Doctor> getDoctorFilterCombo() {
        return doctorFilterCombo;
    }

    public JFormattedTextField getStartDateFilterField() {
        return startDateFilterField;
    }

    public JFormattedTextField getEndDateFilterField() {
        return endDateFilterField;
    }

    public JComboBox<String> getProcedureTypeCombo() {
        return procedureTypeCombo;
    }

    public JButton getApplyFilterButton() {
        return applyFilterButton;
    }

    public JButton getClearFilterButton() {
        return clearFilterButton;
    }
} 