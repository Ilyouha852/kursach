package ui.dialogs;

import javax.swing.*;

import model.entities.Appointment;
import model.entities.Doctor;
import model.entities.Patient;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AppointmentDialog extends JDialog {
    private JComboBox<Patient> patientCombo;
    private JComboBox<Doctor> doctorCombo;
    private JComboBox<Date> dateCombo;
    private JComboBox<Date> timeCombo;
    private JComboBox<String> procedureTypeCombo;
    private JButton saveButton;
    private JButton cancelButton;
    private boolean confirmed = false;

    public AppointmentDialog(Frame owner, String title, List<Patient> patients, List<Doctor> doctors) {
        super(owner, title, true);
        initializeComponents(patients, doctors);
        pack();
        setLocationRelativeTo(owner);
    }

    private void initializeComponents(List<Patient> patients, List<Doctor> doctors) {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Пациент
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Пациент:"), gbc);

        gbc.gridx = 1;
        patientCombo = new JComboBox<>(patients.toArray(new Patient[0]));
        mainPanel.add(patientCombo, gbc);

        // Врач
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Врач:"), gbc);

        gbc.gridx = 1;
        doctorCombo = new JComboBox<>(doctors.toArray(new Doctor[0]));
        mainPanel.add(doctorCombo, gbc);

        // Дата
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Дата:"), gbc);

        gbc.gridx = 1;
        dateCombo = new JComboBox<>();
        mainPanel.add(dateCombo, gbc);

        // Время
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Время:"), gbc);

        gbc.gridx = 1;
        timeCombo = new JComboBox<>();
        mainPanel.add(timeCombo, gbc);

        // Тип процедуры
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Тип процедуры:"), gbc);

        gbc.gridx = 1;
        procedureTypeCombo = new JComboBox<>();
        mainPanel.add(procedureTypeCombo, gbc);

        // Кнопки
        JPanel buttonPanel = new JPanel();
        saveButton = new JButton("Сохранить");
        cancelButton = new JButton("Отмена");
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Обработчики событий
        saveButton.addActionListener(e -> {
            confirmed = true;
            dispose();
        });

        cancelButton.addActionListener(e -> dispose());
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Patient getSelectedPatient() {
        return (Patient) patientCombo.getSelectedItem();
    }

    public Doctor getSelectedDoctor() {
        return (Doctor) doctorCombo.getSelectedItem();
    }

    public Date getSelectedDate() {
        return (Date) dateCombo.getSelectedItem();
    }

    public Date getSelectedTime() {
        return (Date) timeCombo.getSelectedItem();
    }

    public String getSelectedProcedureType() {
        return (String) procedureTypeCombo.getSelectedItem();
    }

    public void setAppointment(Appointment appointment, List<Patient> patients, List<Doctor> doctors) {
        Patient patient = patients.stream()
            .filter(p -> p.getId() == appointment.getPatientId())
            .findFirst()
            .orElse(null);
            
        Doctor doctor = doctors.stream()
            .filter(d -> d.getId() == appointment.getDoctorId())
            .findFirst()
            .orElse(null);
            
        patientCombo.setSelectedItem(patient);
        doctorCombo.setSelectedItem(doctor);
        dateCombo.setSelectedItem(appointment.getAppointmentDateTime());
        timeCombo.setSelectedItem(appointment.getAppointmentDateTime());
        procedureTypeCombo.setSelectedItem(appointment.getProcedureType());
    }
} 