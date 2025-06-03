package ui.panels;

import controller.AppointmentController;
import controller.DoctorController;
import controller.PatientController;
import model.entities.Appointment;
import util.ReportGenerator;
import ui.components.ReportFilterPanel;
import ui.components.ReportDisplayPanel;

import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class ReportPanel extends JPanel {
    private final PatientController patientController;
    private final DoctorController doctorController;
    private final AppointmentController appointmentController;
    private final ReportGenerator reportGenerator;
    private ReportFilterPanel filterPanel;
    private ReportDisplayPanel displayPanel;
    private JButton generateReportButton;

    public ReportPanel(PatientController patientController, DoctorController doctorController, AppointmentController appointmentController) {
        this.patientController = patientController;
        this.doctorController = doctorController;
        this.appointmentController = appointmentController;
        setLayout(new BorderLayout());
        reportGenerator = new ReportGenerator(patientController, doctorController, appointmentController);
        initializeComponents();
        setDefaultDateRange();
    }

    private void initializeComponents() {
        filterPanel = new ReportFilterPanel(doctorController, patientController);
        displayPanel = new ReportDisplayPanel();

        generateReportButton = new JButton("Сгенерировать отчет");
        generateReportButton.addActionListener(e -> generateReport());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(generateReportButton);

        add(filterPanel, BorderLayout.NORTH);
        add(displayPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private void setDefaultDateRange() {
        List<Appointment> appointments = appointmentController.getAllAppointments();
        if (!appointments.isEmpty()) {
            // Находим самую раннюю и самую позднюю даты
            Date earliestDate = appointments.stream()
                .map(Appointment::getAppointmentDateTime)
                .min(Date::compareTo)
                .orElse(null);
            
            Date latestDate = appointments.stream()
                .map(Appointment::getAppointmentDateTime)
                .max(Date::compareTo)
                .orElse(null);

            filterPanel.setDateRange(earliestDate, latestDate);
        }
    }

    private void generateReport() {
        try {
            Date startDate = filterPanel.getStartDate();
            Date endDate = filterPanel.getEndDate();
            String reportContent = reportGenerator.generateClinicReport(
                startDate,
                endDate,
                filterPanel.getSelectedDoctor(),
                filterPanel.getSelectedPatient()
            );
            
            displayPanel.setReportContent(reportContent);
        } catch (ParseException ex) {
            JOptionPane.showMessageDialog(this,
                "Неверный формат даты. Используйте формат ГГГГ-ММ-ДД",
                "Ошибка",
                JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Ошибка при генерации отчета: " + ex.getMessage(),
                "Ошибка",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
}
