package ui.panels;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.ParseException;
import java.util.List;
import java.util.stream.Collectors;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Date;
import java.util.Calendar;
import java.text.SimpleDateFormat;

import controller.DoctorController;
import model.entities.Appointment;
import model.entities.DentalSpecialty;
import model.entities.Doctor;
import util.ValidationUtils;
import controller.AppointmentController;
import ui.components.DoctorTablePanel;
import ui.dialogs.DoctorEditDialog;
import ui.dialogs.DoctorRescheduleDialog;

public class DoctorPanel extends JPanel {

    private DoctorController doctorController;
    private AppointmentController appointmentController;
    private DoctorTablePanel tablePanel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton rescheduleButton;
    private List<Doctor> doctors;

    public DoctorPanel(DoctorController doctorController, AppointmentController appointmentController) {
        this.doctorController = doctorController;
        this.appointmentController = appointmentController;
        initializeComponents();
        loadData();
    }

    private void initializeComponents() {
        setLayout(new BorderLayout());
        tablePanel = new DoctorTablePanel(doctorController, appointmentController);
        add(tablePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Добавить");
        editButton = new JButton("Редактировать");
        deleteButton = new JButton("Удалить");
        rescheduleButton = new JButton("Перенести записи");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(rescheduleButton);

        add(buttonPanel, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addDoctor());
        editButton.addActionListener(e -> editDoctor());
        deleteButton.addActionListener(e -> deleteDoctor());
        rescheduleButton.addActionListener(e -> rescheduleAppointments());
    }

    private void loadData() {
        doctors = doctorController.getAllDoctors();
        tablePanel.updateTable(doctors);
    }

    private void addDoctor() {
        DoctorEditDialog dialog = new DoctorEditDialog(SwingUtilities.getWindowAncestor(this), "Добавление врача", null);
        dialog.setVisible(true);
        if (dialog.isConfirmed()) {
                Doctor doctor = new Doctor();
            doctor.setLastName(dialog.getLastName());
            doctor.setFirstName(dialog.getFirstName());
            doctor.setMiddleName(dialog.getMiddleName());
            doctor.setSpecialization(dialog.getSpecialization());
            doctor.setPhoneNumber(dialog.getPhone());
            doctor.setEmail(dialog.getEmail());
                doctorController.saveDoctor(doctor);
            loadData();
        }
    }

    private void editDoctor() {
        Doctor selectedDoctor = tablePanel.getSelectedDoctor();
        if (selectedDoctor != null) {
            DoctorEditDialog dialog = new DoctorEditDialog(SwingUtilities.getWindowAncestor(this), "Редактирование врача", selectedDoctor);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                selectedDoctor.setLastName(dialog.getLastName());
                selectedDoctor.setFirstName(dialog.getFirstName());
                selectedDoctor.setMiddleName(dialog.getMiddleName());
                selectedDoctor.setSpecialization(dialog.getSpecialization());
                selectedDoctor.setPhoneNumber(dialog.getPhone());
                selectedDoctor.setEmail(dialog.getEmail());
                doctorController.updateDoctor(selectedDoctor);
                loadData();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Выберите врача для редактирования", "Предупреждение", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void deleteDoctor() {
        Doctor selectedDoctor = tablePanel.getSelectedDoctor();
        if (selectedDoctor != null) {
            int confirm = JOptionPane.showConfirmDialog(this, "Вы уверены, что хотите удалить этого врача?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
                doctorController.deleteDoctor(selectedDoctor.getId());
                loadData();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Выберите врача для удаления", "Предупреждение", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void rescheduleAppointments() {
        Doctor selectedDoctor = tablePanel.getSelectedDoctor();
        if (selectedDoctor != null) {
            List<Appointment> appointments = appointmentController.getAppointmentsByDoctor(selectedDoctor.getId());
            if (appointments.isEmpty()) {
                JOptionPane.showMessageDialog(this, "У выбранного врача нет записей", "Информация", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

            Date minDate = appointments.stream().map(Appointment::getAppointmentDateTime).min(Date::compareTo).get();
            Date maxDate = appointments.stream().map(Appointment::getAppointmentDateTime).max(Date::compareTo).get();

            DoctorRescheduleDialog dialog = new DoctorRescheduleDialog(SwingUtilities.getWindowAncestor(this), "Перенос записей", minDate, maxDate);
            dialog.setVisible(true);
            if (dialog.isConfirmed()) {
                try {
                    Date startDate = dialog.getStartDate();
                    Date endDate = dialog.getEndDate();
                    int offset = dialog.getOffset();
                    appointmentController.rescheduleAppointments(selectedDoctor.getId(), startDate, endDate, offset);
                    JOptionPane.showMessageDialog(this, "Записи успешно перенесены", "Успех", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(this, "Ошибка при переносе записей: " + e.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Выберите врача для переноса записей", "Предупреждение", JOptionPane.WARNING_MESSAGE);
        }
    }
}
