package ui.panels;

import controller.AppointmentController;
import controller.DoctorController;
import controller.PatientController;
import model.entities.Appointment;
import model.entities.AppointmentStatus;
import model.entities.Doctor;
import model.entities.Patient;
import ui.components.AppointmentTablePanel;
import ui.components.AppointmentFilterPanel;
import ui.dialogs.AppointmentDialog;

import javax.swing.*;
import javax.swing.text.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Calendar;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashSet;

public class AppointmentPanel extends JPanel {

    private final AppointmentController appointmentController;
    private final DoctorController doctorController;
    private final PatientController patientController;
    
    private AppointmentTablePanel tablePanel;
    private AppointmentFilterPanel filterPanel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton filterButton;
    private List<Appointment> allAppointments;
    private TableRowSorter<DefaultTableModel> sorter;
    private boolean isFilterPanelVisible = false;
    private JComboBox<String> procedureTypeCombo;
    private List<Patient> allPatients;
    private List<Doctor> allDoctors;

    private JComboBox<Patient> patientFilterCombo;
    private JComboBox<Doctor> doctorFilterCombo;
    private JFormattedTextField startDateFilterField;
    private JFormattedTextField endDateFilterField;

    public AppointmentPanel(AppointmentController appointmentController,
                          DoctorController doctorController,
                          PatientController patientController) {
        this.appointmentController = appointmentController;
        this.doctorController = doctorController;
        this.patientController = patientController;
        
        setLayout(new BorderLayout());
        initializeComponents();
        loadData();
    }

    private void initializeComponents() {
        // Создаем панель с кнопками
        JPanel buttonPanel = new JPanel();
        addButton = new JButton("Добавить");
        editButton = new JButton("Редактировать");
        deleteButton = new JButton("Удалить");
        filterButton = new JButton("Фильтры");

        buttonPanel.add(addButton);
        buttonPanel.add(editButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(filterButton);

        // Создаем компоненты
        tablePanel = new AppointmentTablePanel();
        filterPanel = new AppointmentFilterPanel(doctorController, patientController, appointmentController);
        filterPanel.setVisible(false);

        // Инициализируем комбобоксы для фильтров
        doctorFilterCombo = filterPanel.getDoctorFilterCombo();
        patientFilterCombo = filterPanel.getPatientFilterCombo();

        // Добавляем компоненты на панель
        add(tablePanel, BorderLayout.CENTER);
        add(filterPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);

        // Добавляем обработчики событий
        addButton.addActionListener(e -> addAppointment());
        editButton.addActionListener(e -> editAppointment());
        deleteButton.addActionListener(e -> deleteAppointment());
        filterButton.addActionListener(e -> toggleFilterPanel());
        
        filterPanel.getApplyFilterButton().addActionListener(e -> applyFilters());
        filterPanel.getClearFilterButton().addActionListener(e -> clearFilters());
    }

    private void loadData() {
        try {
            allAppointments = appointmentController.getAllAppointments();
            allPatients = patientController.getAllPatients();
            allDoctors = doctorController.getAllDoctors();
            tablePanel.updateTable(allAppointments, allPatients, allDoctors);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Ошибка при загрузке данных: " + e.getMessage(),
                "Ошибка",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleFilterPanel() {
        isFilterPanelVisible = !isFilterPanelVisible;
        filterPanel.setVisible(isFilterPanelVisible);
        revalidate();
        repaint();
    }

    private void addAppointment() {
        AppointmentDialog dialog = new AppointmentDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Новая запись",
            allPatients,
            allDoctors
        );
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            Appointment appointment = new Appointment();
            appointment.setPatientId(dialog.getSelectedPatient().getId());
            appointment.setDoctorId(dialog.getSelectedDoctor().getId());
            appointment.setAppointmentDateTime(dialog.getSelectedDate());
            appointment.setProcedureType(dialog.getSelectedProcedureType());
            appointment.setStatus(AppointmentStatus.PLANNED.getDisplayName());

            try {
                appointmentController.saveAppointment(appointment);
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Ошибка при сохранении записи: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editAppointment() {
        int selectedRow = tablePanel.getTable().getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                    "Пожалуйста, выберите запись для редактирования",
                    "Предупреждение",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }

        Appointment appointment = allAppointments.get(selectedRow);
        AppointmentDialog dialog = new AppointmentDialog(
            (Frame) SwingUtilities.getWindowAncestor(this),
            "Редактирование записи",
            allPatients,
            allDoctors
        );
        dialog.setAppointment(appointment, allPatients, allDoctors);
        dialog.setVisible(true);

        if (dialog.isConfirmed()) {
            appointment.setPatientId(dialog.getSelectedPatient().getId());
            appointment.setDoctorId(dialog.getSelectedDoctor().getId());
            appointment.setAppointmentDateTime(dialog.getSelectedDate());
            appointment.setProcedureType(dialog.getSelectedProcedureType());

            try {
                appointmentController.updateAppointment(appointment);
                loadData();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Ошибка при обновлении записи: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteAppointment() {
        int selectedRow = tablePanel.getTable().getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите запись для удаления");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Вы уверены, что хотите удалить эту запись?",
            "Подтверждение удаления",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            int appointmentId = (int) tablePanel.getTable().getValueAt(selectedRow, 0);
            appointmentController.deleteAppointment(appointmentId);
            loadData();
        }
    }

    private void applyFilters() {
        final Patient selectedPatient = (Patient) filterPanel.getPatientFilterCombo().getSelectedItem();
        final Doctor selectedDoctor = (Doctor) filterPanel.getDoctorFilterCombo().getSelectedItem();
        final Date startDate = parseDate(filterPanel.getStartDateFilterField().getText());
        final Date endDate = parseDate(filterPanel.getEndDateFilterField().getText());
        final String selectedProcedureType = (String) filterPanel.getProcedureTypeCombo().getSelectedItem();
        
        List<Appointment> filteredAppointments = allAppointments.stream()
            .filter(a -> selectedPatient == null || a.getPatientId() == selectedPatient.getId())
            .filter(a -> selectedDoctor == null || a.getDoctorId() == selectedDoctor.getId())
            .filter(a -> startDate == null || !a.getAppointmentDateTime().before(startDate))
            .filter(a -> endDate == null || !a.getAppointmentDateTime().after(endDate))
            .filter(a -> selectedProcedureType == null || selectedProcedureType.equals(a.getProcedureType()))
            .collect(Collectors.toList());
        
        tablePanel.updateTable(filteredAppointments, allPatients, allDoctors);
    }

    private void clearFilters() {
        filterPanel.getPatientFilterCombo().setSelectedItem(null);
        filterPanel.getDoctorFilterCombo().setSelectedItem(null);
        filterPanel.getProcedureTypeCombo().setSelectedItem(null);
        filterPanel.getStartDateFilterField().setText("");
        filterPanel.getEndDateFilterField().setText("");
        
        loadData();
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

    private Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return null;
        }
        try {
            return new SimpleDateFormat("dd.MM.yyyy").parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    private JComboBox<Doctor> createDoctorComboBox() {
        JComboBox<Doctor> comboBox = new JComboBox<>();
        comboBox.addItem(null); // Добавляем пустой элемент
        for (Doctor doctor : allDoctors) {
            comboBox.addItem(doctor);
        }
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Выберите врача");
                } else if (value instanceof Doctor) {
                    Doctor doctor = (Doctor) value;
                    StringBuilder sb = new StringBuilder();
                    sb.append(doctor.getLastName()).append(" ").append(doctor.getFirstName());
                    if (doctor.getMiddleName() != null && !doctor.getMiddleName().isEmpty()) {
                        sb.append(" ").append(doctor.getMiddleName());
                    }
                    if (doctor.getSpecialization() != null && !doctor.getSpecialization().isEmpty()) {
                        sb.append(" - ").append(doctor.getSpecialization());
                    }
                    if (doctor.getPhoneNumber() != null && !doctor.getPhoneNumber().isEmpty()) {
                         sb.append(" тел: ").append(doctor.getPhoneNumber());
                    }
                    setText(sb.toString().trim());
                }
                return this;
            }
        });
        return comboBox;
    }

    private JComboBox<Patient> createPatientComboBox() {
        JComboBox<Patient> comboBox = new JComboBox<>();
        comboBox.addItem(null); // Добавляем пустой элемент
        for (Patient patient : allPatients) {
            comboBox.addItem(patient);
        }
        comboBox.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Выберите пациента");
                } else if (value instanceof Patient) {
                    Patient patient = (Patient) value;
                    setText(patient.toString());
                }
                return this;
            }
        });
        return comboBox;
    }

    private JComboBox<String> createProcedureTypeComboBox(Patient patient) {
        List<String> items = new ArrayList<>();
        items.add("Осмотр пациента");
        if (patient != null && patient.getDisease() != null && !patient.getDisease().isEmpty()) {
            items.add("Лечение заболевания - " + patient.getDisease());
        }
        return new JComboBox<>(items.toArray(new String[0]));
    }

    private void updateTimeSlots(JComboBox<Date> timeCombo, Date date, int doctorId, Integer excludeAppointmentId) {
        timeCombo.removeAllItems();
        
        // Получение всех записей на выбранную дату для выбранного врача
        List<Appointment> doctorAppointments = allAppointments.stream()
            .filter(a -> a.getDoctorId() == doctorId && 
                        a.getAppointmentDateTime() != null &&
                        isSameDay(a.getAppointmentDateTime(), date))
            .collect(Collectors.toList());

        // Создание списка занятых временных слотов
        Set<Date> busySlots = doctorAppointments.stream()
            .filter(a -> excludeAppointmentId == null || a.getId() != excludeAppointmentId)
            .map(Appointment::getAppointmentDateTime)
            .collect(Collectors.toSet());

        // Генерация всех возможных временных слотов
        List<Date> allSlots = generateTimeSlots(date);
        
        // Фильтрация доступных слотов
        List<Date> availableSlots = allSlots.stream()
            .filter(slot -> !busySlots.contains(slot))
            .collect(Collectors.toList());

        // Если это редактирование существующей записи, добавляем текущее время
        if (excludeAppointmentId != null) {
            Appointment currentAppointment = allAppointments.stream()
                .filter(a -> a.getId() == excludeAppointmentId)
                .findFirst()
                .orElse(null);
                
            if (currentAppointment != null) {
                Calendar currentTime = Calendar.getInstance();
                currentTime.setTime(currentAppointment.getAppointmentDateTime());
                Calendar dateCal = Calendar.getInstance();
                dateCal.setTime(date);
                dateCal.set(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY));
                dateCal.set(Calendar.MINUTE, currentTime.get(Calendar.MINUTE));
                Date currentSlot = dateCal.getTime();
                
                if (!availableSlots.contains(currentSlot)) {
                    availableSlots.add(currentSlot);
                }
            }
        }
        
        // Сортировка временных слотов
        availableSlots.sort(Date::compareTo);
        
        for (Date slot : availableSlots) {
            timeCombo.addItem(slot);
        }
    }

    private boolean isSameDay(Date date1, Date date2) {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        cal1.setTime(date1);
        cal2.setTime(date2);
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
               cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    private List<Date> generateTimeSlots(Date date) {
        List<Date> slots = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        
        // Устанавливаем начальное время (8:00)
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        // Генерируем слоты до 17:00, пропуская обед (13:00-14:00)
        while (calendar.get(Calendar.HOUR_OF_DAY) < 17) {
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);
            
            // Пропускаем обеденное время
            if (hour == 13) {
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                continue;
            }
            
            // Добавляем слот
            slots.add(calendar.getTime());
            
            // Переходим к следующему слоту (каждые 30 минут)
            calendar.add(Calendar.MINUTE, 30);
        }
        
        return slots;
    }

    public void applyFilter(int patientId, int doctorId) {
        // Показываем панель фильтров
        if (!isFilterPanelVisible) {
            toggleFilterPanel();
        }

        // Сброс противоположного фильтра
        if (patientId > 0) {
            // Если фильтруем по пациенту, сбрасываем фильтр по врачу
            doctorFilterCombo.setSelectedIndex(0);
        } else if (doctorId > 0) {
            // Если фильтруем по врачу, сбрасываем фильтр по пациенту
            patientFilterCombo.setSelectedIndex(0);
        }

        // Устанавка значений фильтров
        if (patientId > 0) {
            for (int i = 0; i < patientFilterCombo.getItemCount(); i++) {
                Patient patient = patientFilterCombo.getItemAt(i);
                if (patient != null && patient.getId() == patientId) {
                    patientFilterCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        if (doctorId > 0) {
            for (int i = 0; i < doctorFilterCombo.getItemCount(); i++) {
                Doctor doctor = doctorFilterCombo.getItemAt(i);
                if (doctor != null && doctor.getId() == doctorId) {
                    doctorFilterCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        // Применение фильтров
        applyFilters();
    }
}