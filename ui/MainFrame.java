package ui;

import javax.swing.*;
import java.awt.*;

public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane;
    private PatientPanel patientPanel;
    private DoctorPanel doctorPanel;
    private AppointmentPanel appointmentPanel;
    private ReportPanel reportPanel;
    private StatusBar statusBar;

    public MainFrame() {
        setTitle("Панель администратора");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        patientPanel = new PatientPanel();
        doctorPanel = new DoctorPanel();
        appointmentPanel = new AppointmentPanel();
        reportPanel = new ReportPanel();
        statusBar = new StatusBar();

        tabbedPane.addTab("Пациенты", patientPanel);
        tabbedPane.addTab("Врачи", doctorPanel);
        tabbedPane.addTab("Записи на прием", appointmentPanel);
        tabbedPane.addTab("Отчеты", reportPanel);

        add(tabbedPane, BorderLayout.CENTER);
        add(statusBar, BorderLayout.SOUTH);
    }

    // Method to set status bar message (accessible from other panels)
    public void setStatus(String message) {
        statusBar.setStatus(message);
    }

    public void showAppointmentsWithFilter(int patientId, int doctorId) {
        tabbedPane.setSelectedIndex(2); // Переключаемся на панель записей
        appointmentPanel.applyFilter(patientId, doctorId);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }
}
