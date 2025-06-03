package ui;

import javax.swing.*;
import java.awt.*;
import controller.AppointmentController;
import controller.DoctorController;
import controller.PatientController;
import data.repository.AppointmentRepository;
import data.repository.DoctorRepository;
import data.repository.PatientRepository;
import util.LogConfig;
import ui.panels.PatientPanel;
import ui.panels.DoctorPanel;
import ui.panels.AppointmentPanel;
import ui.panels.ReportPanel;

public class MainFrame extends JFrame {

    private JTabbedPane tabbedPane;
    private PatientPanel patientPanel;
    private DoctorPanel doctorPanel;
    private AppointmentPanel appointmentPanel;
    private ReportPanel reportPanel;

    private final PatientRepository patientRepository = new PatientRepository();
    private final DoctorRepository doctorRepository = new DoctorRepository();
    private final AppointmentRepository appointmentRepository = new AppointmentRepository();
    
    private final PatientController patientController = new PatientController(patientRepository);
    private final DoctorController doctorController = new DoctorController(doctorRepository);
    private final AppointmentController appointmentController = new AppointmentController(
        appointmentRepository, doctorController, patientController);

    public MainFrame() {
        // Инициализация логирования
        LogConfig.configure();
        
        setTitle("Панель администратора");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 600);
        setLocationRelativeTo(null);

        tabbedPane = new JTabbedPane();
        patientPanel = new PatientPanel(patientController);
        doctorPanel = new DoctorPanel(doctorController, appointmentController);
        appointmentPanel = new AppointmentPanel(appointmentController, doctorController, patientController);
        reportPanel = new ReportPanel(patientController, doctorController, appointmentController);

        tabbedPane.addTab("Пациенты", patientPanel);
        tabbedPane.addTab("Врачи", doctorPanel);
        tabbedPane.addTab("Записи на прием", appointmentPanel);
        tabbedPane.addTab("Отчеты", reportPanel);

        add(tabbedPane, BorderLayout.CENTER);
    }

    public void showAppointmentsWithFilter(int patientId, int doctorId) {
        tabbedPane.setSelectedIndex(2);
        appointmentPanel.applyFilter(patientId, doctorId);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainFrame().setVisible(true));
    }
}
