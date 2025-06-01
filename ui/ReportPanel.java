package ui;

import controller.AppointmentController;
import controller.DoctorController;
import controller.PatientController;
import model.Appointment;
import model.Doctor;
import model.Patient;
import util.ReportGenerator;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ReportPanel extends JPanel {

    private JTextArea reportArea;
    private JButton generateReportButton;
    private JButton saveToFileButton;
    private JFormattedTextField startDateField;
    private JFormattedTextField endDateField;
    private JComboBox<Doctor> doctorCombo;
    private JComboBox<Patient> patientCombo;
    private final PatientController patientController = new PatientController();
    private final DoctorController doctorController = new DoctorController();
    private final AppointmentController appointmentController = new AppointmentController();
    private ReportGenerator reportGenerator;

    public ReportPanel() {
        setLayout(new BorderLayout());

        // Создаем панель фильтров
        JPanel filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder("Параметры отчета"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Период
        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(new JLabel("Период:"), gbc);

        gbc.gridx = 1;
        startDateField = createDateField();
        filterPanel.add(startDateField, gbc);

        gbc.gridx = 2;
        filterPanel.add(new JLabel(" - "), gbc);

        gbc.gridx = 3;
        endDateField = createDateField();
        filterPanel.add(endDateField, gbc);

        // Заполняем поля дат значениями по умолчанию
        setDefaultDateRange();

        // Врач
        gbc.gridx = 0;
        gbc.gridy = 1;
        filterPanel.add(new JLabel("Врач:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        doctorCombo = new JComboBox<>();
        doctorCombo.addItem(null); // Пустой элемент для отмены фильтра
        doctorController.getAllDoctors().forEach(doctorCombo::addItem);
        doctorCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Все врачи");
                } else if (value instanceof Doctor) {
                    Doctor doctor = (Doctor) value;
                    setText(doctor.getLastName() + " " + doctor.getFirstName() + 
                           (doctor.getMiddleName() != null ? " " + doctor.getMiddleName() : "") +
                           " - " + doctor.getSpecialization());
                }
                return this;
            }
        });
        filterPanel.add(doctorCombo, gbc);

        // Пациент
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        filterPanel.add(new JLabel("Пациент:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        patientCombo = new JComboBox<>();
        patientCombo.addItem(null); // Пустой элемент для отмены фильтра
        patientController.getAllPatients().forEach(patientCombo::addItem);
        patientCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Все пациенты");
                } else if (value instanceof Patient) {
                    Patient patient = (Patient) value;
                    setText(patient.toString());
                }
                return this;
            }
        });
        filterPanel.add(patientCombo, gbc);

        reportArea = new JTextArea("Здесь будут генерироваться отчеты.");
        JScrollPane scrollPane = new JScrollPane(reportArea);

        generateReportButton = new JButton("Сгенерировать отчет");
        saveToFileButton = new JButton("Сохранить в файл");
        saveToFileButton.setEnabled(false);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(generateReportButton);
        buttonPanel.add(saveToFileButton);

        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        reportGenerator = new ReportGenerator(patientController, doctorController, appointmentController);

        generateReportButton.addActionListener(e -> generateReport());
        saveToFileButton.addActionListener(e -> saveReportToFile());
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

            // Форматируем даты и устанавливаем в поля
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (earliestDate != null) {
                startDateField.setText(dateFormat.format(earliestDate));
            }
            if (latestDate != null) {
                endDateField.setText(dateFormat.format(latestDate));
            }
        }
    }

    private void generateReport() {
        try {
            // Получаем параметры фильтрации
            Date startDate = null;
            Date endDate = null;
            Doctor selectedDoctor = (Doctor) doctorCombo.getSelectedItem();
            Patient selectedPatient = (Patient) patientCombo.getSelectedItem();

            // Парсим даты
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String startDateStr = startDateField.getText().trim();
            String endDateStr = endDateField.getText().trim();

            if (!startDateStr.isEmpty()) {
                startDate = dateFormat.parse(startDateStr);
            }
            if (!endDateStr.isEmpty()) {
                endDate = dateFormat.parse(endDateStr);
                // Устанавливаем конец дня для конечной даты
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(endDate);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                endDate = calendar.getTime();
            }

            // Генерируем отчет с учетом фильтров
            String reportContent = reportGenerator.generateClinicReport(startDate, endDate, selectedDoctor, selectedPatient);
            
            // Отображаем отчет в текстовом поле
            reportArea.setText(reportContent);
            
            // Активируем кнопку сохранения
            saveToFileButton.setEnabled(true);
            
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

    private void saveReportToFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Выберите место для сохранения отчета");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        fileChooser.setAcceptAllFileFilterUsed(false);
        fileChooser.addChoosableFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("Текстовые файлы (*.txt)", "txt"));
        fileChooser.setSelectedFile(new File("clinic_report.txt"));

        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();

            if (!filePath.toLowerCase().endsWith(".txt")) {
                filePath += ".txt";
                fileToSave = new File(filePath);
            }

            try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileToSave))) {
                writer.write(reportArea.getText());
                JOptionPane.showMessageDialog(this,
                    "Отчет сохранен в файл: " + fileToSave.getAbsolutePath(),
                    "Успех",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Ошибка при сохранении отчета в файл: " + ex.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }
}
