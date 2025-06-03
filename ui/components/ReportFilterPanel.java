package ui.components;

import controller.DoctorController;
import controller.PatientController;
import model.entities.Doctor;
import model.entities.Patient;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;

public class ReportFilterPanel extends JPanel {
    private JFormattedTextField startDateField;
    private JFormattedTextField endDateField;
    private JComboBox<Doctor> doctorCombo;
    private JComboBox<Patient> patientCombo;
    private final DoctorController doctorController;
    private final PatientController patientController;

    public ReportFilterPanel(DoctorController doctorController, PatientController patientController) {
        this.doctorController = doctorController;
        this.patientController = patientController;
        setLayout(new GridBagLayout());
        setBorder(BorderFactory.createTitledBorder("Параметры отчета"));
        initializeComponents();
    }

    private void initializeComponents() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Период
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(new JLabel("Период:"), gbc);

        gbc.gridx = 1;
        startDateField = createDateField();
        add(startDateField, gbc);

        gbc.gridx = 2;
        add(new JLabel(" - "), gbc);

        gbc.gridx = 3;
        endDateField = createDateField();
        add(endDateField, gbc);

        // Врач
        gbc.gridx = 0;
        gbc.gridy = 1;
        add(new JLabel("Врач:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        doctorCombo = createDoctorComboBox();
        add(doctorCombo, gbc);

        // Пациент
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        add(new JLabel("Пациент:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        patientCombo = createPatientComboBox();
        add(patientCombo, gbc);
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

    private JComboBox<Doctor> createDoctorComboBox() {
        JComboBox<Doctor> combo = new JComboBox<>();
        combo.addItem(null); // Пустой элемент для отмены фильтра
        doctorController.getAllDoctors().forEach(combo::addItem);
        combo.setRenderer(new DefaultListCellRenderer() {
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
        return combo;
    }

    private JComboBox<Patient> createPatientComboBox() {
        JComboBox<Patient> combo = new JComboBox<>();
        combo.addItem(null); // Пустой элемент для отмены фильтра
        patientController.getAllPatients().forEach(combo::addItem);
        combo.setRenderer(new DefaultListCellRenderer() {
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
        return combo;
    }

    public void setDateRange(Date startDate, Date endDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (startDate != null) {
            startDateField.setText(dateFormat.format(startDate));
        }
        if (endDate != null) {
            endDateField.setText(dateFormat.format(endDate));
        }
    }

    public Date getStartDate() throws ParseException {
        String dateStr = startDateField.getText().trim();
        if (dateStr.isEmpty()) return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        return dateFormat.parse(dateStr);
    }

    public Date getEndDate() throws ParseException {
        String dateStr = endDateField.getText().trim();
        if (dateStr.isEmpty()) return null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateFormat.parse(dateStr);
        // Устанавливаем конец дня для конечной даты
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        return calendar.getTime();
    }

    public Doctor getSelectedDoctor() {
        return (Doctor) doctorCombo.getSelectedItem();
    }

    public Patient getSelectedPatient() {
        return (Patient) patientCombo.getSelectedItem();
    }
} 