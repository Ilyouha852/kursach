package ui;

import controller.AppointmentController;
import controller.DoctorController;
import controller.PatientController;
import model.Appointment;
import model.AppointmentStatus;
import model.Doctor;
import model.Patient;

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

    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton filterButton;
    private List<Appointment> allAppointments;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPanel filterPanel;
    private boolean isFilterPanelVisible = false;
    private JComboBox<String> procedureTypeCombo;
    private List<Patient> allPatients;
    private List<Doctor> allDoctors;

    private final AppointmentController appointmentController = new AppointmentController();
    private final DoctorController doctorController = new DoctorController();
    private final PatientController patientController = new PatientController();

    private JComboBox<Patient> patientFilterCombo;
    private JComboBox<Doctor> doctorFilterCombo;
    private JFormattedTextField startDateFilterField;
    private JFormattedTextField endDateFilterField;

    public AppointmentPanel() {
        setLayout(new BorderLayout());
        
        // Загружаем списки пациентов и врачей один раз при создании панели
        allPatients = patientController.getAllPatients();
        allDoctors = doctorController.getAllDoctors();

        // Создаем панель фильтрации (изначально скрытую)
        filterPanel = new JPanel(new GridBagLayout());
        filterPanel.setBorder(BorderFactory.createTitledBorder("Фильтры"));
        filterPanel.setVisible(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Фильтр по дате
        gbc.gridx = 0;
        gbc.gridy = 0;
        filterPanel.add(new JLabel("Период:"), gbc);

        gbc.gridx = 1;
        startDateFilterField = createDateField();
        filterPanel.add(startDateFilterField, gbc);

        gbc.gridx = 2;
        filterPanel.add(new JLabel(" - "), gbc);

        gbc.gridx = 3;
        endDateFilterField = createDateField();
        filterPanel.add(endDateFilterField, gbc);

        // Фильтр по пациенту
        gbc.gridx = 0;
        gbc.gridy = 1;
        filterPanel.add(new JLabel("Пациент:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        patientFilterCombo = new JComboBox<>();
        patientFilterCombo.addItem(null); // Пустой элемент для отмены фильтра
        patientController.getAllPatients().forEach(patientFilterCombo::addItem);
        // Добавляем рендерер для отображения "Не выбрано" для null
        patientFilterCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Не выбрано");
                } else if (value instanceof Patient) {
                    Patient patient = (Patient) value;
                    setText(patient.toString());
                }
                return this;
            }
        });
        filterPanel.add(patientFilterCombo, gbc);

        // Фильтр по врачу
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        filterPanel.add(new JLabel("Врач:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        doctorFilterCombo = new JComboBox<>();
        doctorFilterCombo.addItem(null); // Пустой элемент для отмены фильтра
        doctorController.getAllDoctors().forEach(doctorFilterCombo::addItem);
        // Добавляем рендерер для отображения "Не выбрано" для null
        doctorFilterCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Не выбрано");
                } else if (value instanceof Doctor) {
                    Doctor doctor = (Doctor) value;
                    setText(doctor.toString());
                }
                return this;
            }
        });
        filterPanel.add(doctorFilterCombo, gbc);

        // Фильтр по типу процедуры
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 1;
        filterPanel.add(new JLabel("Тип процедуры:"), gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 3;
        procedureTypeCombo = new JComboBox<>();
        procedureTypeCombo.addItem(null); // Пустой элемент для отмены фильтра
        // Добавляем все уникальные типы процедур из существующих записей
        Set<String> uniqueProcedureTypes = new HashSet<>();
        appointmentController.getAllAppointments().forEach(appointment -> {
            if (appointment.getProcedureType() != null && !appointment.getProcedureType().isEmpty()) {
                uniqueProcedureTypes.add(appointment.getProcedureType());
            }
        });
        uniqueProcedureTypes.forEach(procedureTypeCombo::addItem);
        // Добавляем рендерер для отображения "Не выбрано" для null
        procedureTypeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value == null) {
                    setText("Не выбрано");
                } else {
                    setText(value.toString());
                }
                return this;
            }
        });
        filterPanel.add(procedureTypeCombo, gbc);

        // Кнопки фильтрации
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 4;
        JPanel filterButtonPanel = new JPanel();
        JButton applyFilterButton = new JButton("Применить фильтры");
        JButton clearFilterButton = new JButton("Сбросить фильтры");
        filterButtonPanel.add(applyFilterButton);
        filterButtonPanel.add(clearFilterButton);
        filterPanel.add(filterButtonPanel, gbc);

        // Создаем модель таблицы
        String[] columnNames = {"ID", "Дата и время", "Пациент", "Врач", "Тип процедуры", "Статус"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentTable = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        
        // Добавляем специальные компараторы для столбцов
        sorter.setComparator(2, (String s1, String s2) -> {
            // Сортировка по ФИО пациента
            if (s1.equals("Неизвестно")) return 1;
            if (s2.equals("Неизвестно")) return -1;
            return s1.compareToIgnoreCase(s2);
        });
        
        sorter.setComparator(3, (String s1, String s2) -> {
            // Сортировка по ФИО врача
            if (s1.equals("Неизвестно")) return 1;
            if (s2.equals("Неизвестно")) return -1;
            return s1.compareToIgnoreCase(s2);
        });
        
        sorter.setComparator(4, (String s1, String s2) -> {
            // Сортировка по типу процедуры
            if (s1 == null) return 1;
            if (s2 == null) return -1;
            return s1.compareToIgnoreCase(s2);
        });
        
        sorter.setComparator(5, (String s1, String s2) -> {
            // Сортировка по статусу
            AppointmentStatus status1 = AppointmentStatus.fromDisplayName(s1);
            AppointmentStatus status2 = AppointmentStatus.fromDisplayName(s2);
            if (status1 == null) return 1;
            if (status2 == null) return -1;
            return status1.ordinal() - status2.ordinal();
        });
        
        appointmentTable.setRowSorter(sorter);

        // Устанавливаем сортировку по умолчанию по дате и времени
        sorter.setSortKeys(Arrays.asList(new RowSorter.SortKey(1, SortOrder.ASCENDING)));

        // Добавляем слушатель выбора строки
        appointmentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                editButton.setEnabled(appointmentTable.getSelectedRow() != -1);
                deleteButton.setEnabled(appointmentTable.getSelectedRow() != -1);
            }
        });

        JScrollPane scrollPane = new JScrollPane(appointmentTable);

        // Панель кнопок управления
        JPanel controlPanel = new JPanel();
        addButton = new JButton("Добавить");
        editButton = new JButton("Редактировать");
        deleteButton = new JButton("Удалить");
        filterButton = new JButton("Фильтр");

        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);
        controlPanel.add(filterButton);

        // Добавляем все компоненты на панель
        add(filterPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        // Загружаем записи
        loadAppointments();

        // Устанавливаем начальный диапазон дат в фильтре
        if (allAppointments != null && !allAppointments.isEmpty()) {
            Date minDate = allAppointments.stream()
                .map(Appointment::getAppointmentDateTime)
                .min(Date::compareTo)
                .orElse(null);
            Date maxDate = allAppointments.stream()
                .map(Appointment::getAppointmentDateTime)
                .max(Date::compareTo)
                .orElse(null);

            SimpleDateFormat filterDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            if (minDate != null) {
                // Найдем поля даты в filterPanel
                Component[] components = filterPanel.getComponents();
                JFormattedTextField start = null, end = null;
                for (int i = 0; i < components.length; i++) {
                    if (components[i] instanceof JLabel && ((JLabel)components[i]).getText().equals("Период:")) {
                         if (i + 1 < components.length && components[i+1] instanceof JFormattedTextField) {
                             start = (JFormattedTextField) components[i+1];
                         }
                         if (i + 3 < components.length && components[i+3] instanceof JFormattedTextField) {
                              end = (JFormattedTextField) components[i+3];
                         }
                         break;
                    }
                }

                if (start != null) start.setText(filterDateFormat.format(minDate));
                if (end != null) end.setText(filterDateFormat.format(maxDate));
            }
        }

        // Обработчики событий
        filterButton.addActionListener(_ -> toggleFilterPanel());
        applyFilterButton.addActionListener(_ -> applyFilters(startDateFilterField, endDateFilterField, patientFilterCombo, doctorFilterCombo));
        clearFilterButton.addActionListener(_ -> clearFilters(startDateFilterField, endDateFilterField, patientFilterCombo, doctorFilterCombo));

        addButton.addActionListener(_ -> addAppointment());
        editButton.addActionListener(_ -> {
            int selectedRow = appointmentTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                    "Пожалуйста, выберите запись для редактирования",
                    "Предупреждение",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            editAppointment();
        });
        deleteButton.addActionListener(_ -> {
            int selectedRow = appointmentTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this,
                    "Пожалуйста, выберите запись для удаления",
                    "Предупреждение",
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
            deleteAppointment();
        });
    }

    private void toggleFilterPanel() {
        isFilterPanelVisible = !isFilterPanelVisible;
        filterPanel.setVisible(isFilterPanelVisible);
        
        // Перерисовываем панель
        revalidate();
        repaint();
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

    private void loadAppointments() {
        allAppointments = appointmentController.getAllAppointments();
        updateTable(allAppointments);
    }

    private void updateTable(List<Appointment> appointments) {
        tableModel.setRowCount(0);
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        for (Appointment appointment : appointments) {
            Patient patient = patientController.getPatientById(appointment.getPatientId());
            Doctor doctor = doctorController.getDoctorById(appointment.getDoctorId());
            
            String patientName = patient != null ? 
                patient.getLastName() + " " + patient.getFirstName() + " " + 
                (patient.getMiddleName() != null ? patient.getMiddleName() : "") : "Неизвестно";
            String doctorName = doctor != null ? 
                doctor.getLastName() + " " + doctor.getFirstName() + " " + 
                (doctor.getMiddleName() != null ? doctor.getMiddleName() : "") : "Неизвестно";
            
            Object[] rowData = {
                appointment.getId(),
                appointment.getAppointmentDateTime() != null ? dateFormat.format(appointment.getAppointmentDateTime()) : "",
                patientName,
                doctorName,
                appointment.getProcedureType(),
                appointment.getStatus()
            };
            tableModel.addRow(rowData);
        }
    }

    private void applyFilters(JFormattedTextField startDateField, JFormattedTextField endDateField,
                            JComboBox<Patient> patientCombo, JComboBox<Doctor> doctorCombo) {
        List<Appointment> filteredAppointments = allAppointments;

        // Фильтр по дате
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String startDateStr = startDateField.getText().trim();
            String endDateStr = endDateField.getText().trim();

            if (!startDateStr.isEmpty()) {
                final Date startDate = dateFormat.parse(startDateStr);
                filteredAppointments = filteredAppointments.stream()
                    .filter(a -> !a.getAppointmentDateTime().before(startDate))
                    .collect(Collectors.toList());
            }

            if (!endDateStr.isEmpty()) {
                Date parsedEndDate = dateFormat.parse(endDateStr);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(parsedEndDate);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                final Date endDate = calendar.getTime();
                filteredAppointments = filteredAppointments.stream()
                    .filter(a -> !a.getAppointmentDateTime().after(endDate))
                    .collect(Collectors.toList());
            }
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this,
                "Неверный формат даты. Используйте формат ГГГГ-ММ-ДД",
                "Ошибка",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Фильтр по пациенту
        Patient selectedPatient = (Patient) patientCombo.getSelectedItem();
        if (selectedPatient != null) {
            filteredAppointments = filteredAppointments.stream()
                .filter(a -> a.getPatientId() == selectedPatient.getId())
                .collect(Collectors.toList());
        }

        // Фильтр по врачу
        Doctor selectedDoctor = (Doctor) doctorCombo.getSelectedItem();
        if (selectedDoctor != null) {
            filteredAppointments = filteredAppointments.stream()
                .filter(a -> a.getDoctorId() == selectedDoctor.getId())
                .collect(Collectors.toList());
        }

        // Фильтр по типу процедуры
        String selectedProcedureType = (String) procedureTypeCombo.getSelectedItem();
        if (selectedProcedureType != null) {
            filteredAppointments = filteredAppointments.stream()
                .filter(a -> selectedProcedureType.equals(a.getProcedureType()))
                .collect(Collectors.toList());
        }

        updateTable(filteredAppointments);
    }

    private void clearFilters(JFormattedTextField startDateField, JFormattedTextField endDateField,
                            JComboBox<Patient> patientCombo, JComboBox<Doctor> doctorCombo) {
        startDateField.setText("");
        endDateField.setText("");
        patientCombo.setSelectedIndex(0);
        doctorCombo.setSelectedIndex(0);
        procedureTypeCombo.setSelectedIndex(0);
        updateTable(allAppointments);
    }

    private Date parseDate(String dateStr) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        return dateFormat.parse(dateStr);
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

    private void addAppointment() {
        JDialog addDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Добавить запись на прием", true);
        addDialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Поля для даты и времени
        JLabel dateLabel = new JLabel("Дата:");
        JFormattedTextField dateField = createDateField();
        JLabel timeLabel = new JLabel("Время:");
        JComboBox<Date> timeCombo = new JComboBox<>();
        timeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Date) {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    setText(timeFormat.format(value));
                }
                return this;
            }
        });

        JLabel procedureTypeLabel = new JLabel("Тип процедуры:");
        JComboBox<String> procedureTypeCombo = new JComboBox<>(new String[]{"Осмотр пациента"});
        JLabel statusLabel = new JLabel("Статус:");
        JComboBox<AppointmentStatus> statusCombo = new JComboBox<>(AppointmentStatus.values());
        JLabel patientLabel = new JLabel("Пациент:");
        JComboBox<Patient> patientCombo = createPatientComboBox();
        JLabel doctorLabel = new JLabel("Врач:");
        JComboBox<Doctor> doctorCombo = createDoctorComboBox();

        // Добавляем слушатель изменения даты
        dateField.addPropertyChangeListener("value", e -> {
            try {
                Date selectedDate = parseDate(dateField.getText());
                if (selectedDate != null) {
                    Doctor selectedDoctor = (Doctor) doctorCombo.getSelectedItem();
                    if (selectedDoctor != null) {
                        updateTimeSlots(timeCombo, selectedDate, selectedDoctor.getId());
                    }
                }
            } catch (ParseException ex) {
                // Игнорируем ошибку парсинга
            }
        });

        // Добавляем слушатель изменения врача
        doctorCombo.addActionListener(e -> {
            try {
                Date selectedDate = parseDate(dateField.getText());
                if (selectedDate != null) {
                    Doctor selectedDoctor = (Doctor) doctorCombo.getSelectedItem();
                    if (selectedDoctor != null) {
                        updateTimeSlots(timeCombo, selectedDate, selectedDoctor.getId());
                    }
                }
            } catch (ParseException ex) {
                // Игнорируем ошибку парсинга
            }
        });

        // Добавляем слушатель изменения выбранного пациента
        patientCombo.addActionListener(e -> {
            Patient selectedPatient = (Patient) patientCombo.getSelectedItem();
            if (selectedPatient != null) {
                procedureTypeCombo.removeAllItems();
                procedureTypeCombo.addItem("Осмотр пациента");
                if (selectedPatient.getDisease() != null && !selectedPatient.getDisease().isEmpty()) {
                    procedureTypeCombo.addItem("Лечение заболевания - " + selectedPatient.getDisease());
                }
            }
        });

        mainPanel.add(dateLabel);
        mainPanel.add(dateField);
        mainPanel.add(timeLabel);
        mainPanel.add(timeCombo);
        mainPanel.add(procedureTypeLabel);
        mainPanel.add(procedureTypeCombo);
        mainPanel.add(statusLabel);
        mainPanel.add(statusCombo);
        mainPanel.add(patientLabel);
        mainPanel.add(patientCombo);
        mainPanel.add(doctorLabel);
        mainPanel.add(doctorCombo);

        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        addDialog.add(mainPanel, BorderLayout.CENTER);
        addDialog.add(buttonPanel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> {
            try {
                // Валидация даты
                Date selectedDate = parseDate(dateField.getText());
                if (selectedDate == null) {
                        JOptionPane.showMessageDialog(addDialog, 
                        "Пожалуйста, введите корректную дату в формате ГГГГ-ММ-ДД", 
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                // Проверка выбора времени
                Date selectedTime = (Date) timeCombo.getSelectedItem();
                if (selectedTime == null) {
                        JOptionPane.showMessageDialog(addDialog,
                        "Пожалуйста, выберите время приема",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                // Объединяем дату и время
                Calendar dateCal = Calendar.getInstance();
                dateCal.setTime(selectedDate);
                Calendar timeCal = Calendar.getInstance();
                timeCal.setTime(selectedTime);
                dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
                dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
                Date selectedDateTime = dateCal.getTime();

                    // Проверка выбора пациента
                    Patient selectedPatient = (Patient) patientCombo.getSelectedItem();
                    if (selectedPatient == null) {
                        JOptionPane.showMessageDialog(addDialog,
                            "Пожалуйста, выберите пациента",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Проверка выбора врача
                    Doctor selectedDoctor = (Doctor) doctorCombo.getSelectedItem();
                    if (selectedDoctor == null) {
                        JOptionPane.showMessageDialog(addDialog,
                            "Пожалуйста, выберите врача",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Appointment newAppointment = new Appointment();
                    newAppointment.setAppointmentDateTime(selectedDateTime);
                newAppointment.setProcedureType((String) procedureTypeCombo.getSelectedItem());
                    newAppointment.setStatus(((AppointmentStatus) statusCombo.getSelectedItem()).toString());
                    newAppointment.setPatientId(selectedPatient.getId());
                    newAppointment.setDoctorId(selectedDoctor.getId());

                    appointmentController.saveAppointment(newAppointment);
                    loadAppointments();
                    addDialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(addDialog, 
                        "Ошибка при сохранении записи: " + ex.getMessage(), 
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> addDialog.dispose());

        addDialog.setSize(500, 350);
        addDialog.setLocationRelativeTo((JFrame) SwingUtilities.getWindowAncestor(this));
        addDialog.setVisible(true);
    }

    private void updateTimeSlots(JComboBox<Date> timeCombo, Date date, int doctorId) {
        timeCombo.removeAllItems();
        
        // Получаем все записи на выбранную дату для выбранного врача
        List<Appointment> doctorAppointments = allAppointments.stream()
            .filter(a -> a.getDoctorId() == doctorId && 
                        a.getAppointmentDateTime() != null &&
                        isSameDay(a.getAppointmentDateTime(), date))
            .collect(Collectors.toList());

        // Создаем список занятых временных слотов
        Set<Date> busySlots = doctorAppointments.stream()
            .filter(a -> a.getAppointmentDateTime() != null)
            .map(Appointment::getAppointmentDateTime)
            .collect(Collectors.toSet());

        // Генерируем все возможные временные слоты
        List<Date> allSlots = generateTimeSlots(date);
        
        // Фильтруем доступные слоты
        List<Date> availableSlots = allSlots.stream()
            .filter(slot -> !busySlots.contains(slot))
            .collect(Collectors.toList());
        
        // Сортируем временные слоты
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

    private void editAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // Получаем ID записи из выбранной строки
        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);
        Appointment appointmentToEdit = appointmentController.getAppointmentById(appointmentId);

            if (appointmentToEdit == null) {
            JOptionPane.showMessageDialog(this,
                "Запись на прием не найдена",
                "Ошибка",
                JOptionPane.ERROR_MESSAGE);
                return;
            }

            JDialog editDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Редактировать запись на прием", true);
            editDialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(6, 2, 5, 5));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Поля для даты и времени
        JLabel dateLabel = new JLabel("Дата:");
        JFormattedTextField dateField = createDateField();
        JLabel timeLabel = new JLabel("Время:");
        JComboBox<Date> timeCombo = new JComboBox<>();
        timeCombo.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Date) {
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");
                    setText(timeFormat.format(value));
                }
                return this;
            }
        });

        // Устанавливаем текущую дату
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateField.setText(dateFormat.format(appointmentToEdit.getAppointmentDateTime()));

        // Получаем текущие данные из уже загруженных списков
        Patient currentPatient = allPatients.stream()
            .filter(p -> p.getId() == appointmentToEdit.getPatientId())
            .findFirst()
            .orElse(null);
            
        Doctor currentDoctor = allDoctors.stream()
            .filter(d -> d.getId() == appointmentToEdit.getDoctorId())
            .findFirst()
            .orElse(null);

        // Создаем и инициализируем комбобоксы
            JLabel procedureTypeLabel = new JLabel("Тип процедуры:");
        JComboBox<String> procedureTypeCombo = new JComboBox<>();
        procedureTypeCombo.addItem("Осмотр пациента");
        if (currentPatient != null && currentPatient.getDisease() != null && !currentPatient.getDisease().isEmpty()) {
            procedureTypeCombo.addItem("Лечение заболевания - " + currentPatient.getDisease());
        }

            JLabel statusLabel = new JLabel("Статус:");
            JComboBox<AppointmentStatus> statusCombo = new JComboBox<>(AppointmentStatus.values());

            JLabel patientLabel = new JLabel("Пациент:");
            JComboBox<Patient> patientCombo = createPatientComboBox();

            JLabel doctorLabel = new JLabel("Врач:");
            JComboBox<Doctor> doctorCombo = createDoctorComboBox();

        // Устанавливаем значения в комбобоксы
        if (currentPatient != null) {
            for (int i = 0; i < patientCombo.getItemCount(); i++) {
                Patient p = patientCombo.getItemAt(i);
                if (p != null && p.getId() == currentPatient.getId()) {
                    patientCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        if (currentDoctor != null) {
            for (int i = 0; i < doctorCombo.getItemCount(); i++) {
                Doctor d = doctorCombo.getItemAt(i);
                if (d != null && d.getId() == currentDoctor.getId()) {
                    doctorCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        // Устанавливаем тип процедуры
        String currentProcedureType = appointmentToEdit.getProcedureType();
        if (currentProcedureType != null) {
            boolean found = false;
            for (int i = 0; i < procedureTypeCombo.getItemCount(); i++) {
                String item = procedureTypeCombo.getItemAt(i);
                if (currentProcedureType.equals(item)) {
                    procedureTypeCombo.setSelectedIndex(i);
                    found = true;
                    break;
                }
            }
            // Если тип процедуры не найден в списке, добавляем его
            if (!found) {
                procedureTypeCombo.addItem(currentProcedureType);
                procedureTypeCombo.setSelectedItem(currentProcedureType);
            }
        }

        // Устанавливаем статус
        AppointmentStatus currentStatus = AppointmentStatus.fromDisplayName(appointmentToEdit.getStatus());
        if (currentStatus != null) {
            for (int i = 0; i < statusCombo.getItemCount(); i++) {
                if (currentStatus.equals(statusCombo.getItemAt(i))) {
                    statusCombo.setSelectedIndex(i);
                    break;
                }
            }
        }

        // Добавляем слушатель изменения даты
        dateField.addPropertyChangeListener("value", e -> {
            try {
                Date selectedDate = parseDate(dateField.getText());
                if (selectedDate != null) {
                    Doctor selectedDoctor = (Doctor) doctorCombo.getSelectedItem();
                    if (selectedDoctor != null) {
                        updateTimeSlots(timeCombo, selectedDate, selectedDoctor.getId(), appointmentToEdit.getId());
                    }
                }
            } catch (ParseException ex) {
                // Игнорируем ошибку парсинга
            }
        });

        // Добавляем слушатель изменения врача
        doctorCombo.addActionListener(e -> {
            try {
                Date selectedDate = parseDate(dateField.getText());
                if (selectedDate != null) {
                    Doctor selectedDoctor = (Doctor) doctorCombo.getSelectedItem();
                    if (selectedDoctor != null) {
                        updateTimeSlots(timeCombo, selectedDate, selectedDoctor.getId(), appointmentToEdit.getId());
                    }
                }
            } catch (ParseException ex) {
                // Игнорируем ошибку парсинга
            }
        });

        // Добавляем слушатель изменения выбранного пациента
        patientCombo.addActionListener(e -> {
            Patient selectedPatient = (Patient) patientCombo.getSelectedItem();
            if (selectedPatient != null) {
                procedureTypeCombo.removeAllItems();
                procedureTypeCombo.addItem("Осмотр пациента");
                if (selectedPatient.getDisease() != null && !selectedPatient.getDisease().isEmpty()) {
                    procedureTypeCombo.addItem("Лечение заболевания - " + selectedPatient.getDisease());
                }
            }
        });

        mainPanel.add(dateLabel);
        mainPanel.add(dateField);
        mainPanel.add(timeLabel);
        mainPanel.add(timeCombo);
            mainPanel.add(procedureTypeLabel);
        mainPanel.add(procedureTypeCombo);
            mainPanel.add(statusLabel);
            mainPanel.add(statusCombo);
            mainPanel.add(patientLabel);
            mainPanel.add(patientCombo);
            mainPanel.add(doctorLabel);
            mainPanel.add(doctorCombo);

            JPanel buttonPanel = new JPanel();
            JButton saveButton = new JButton("Сохранить");
            JButton cancelButton = new JButton("Отмена");

            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);

            editDialog.add(mainPanel, BorderLayout.CENTER);
            editDialog.add(buttonPanel, BorderLayout.SOUTH);

        // Инициализируем временные слоты после создания всех компонентов
        try {
            Date selectedDate = parseDate(dateField.getText());
            if (selectedDate != null && currentDoctor != null) {
                updateTimeSlots(timeCombo, selectedDate, currentDoctor.getId(), appointmentToEdit.getId());
                
                // Устанавливаем текущее время записи
                Calendar currentTime = Calendar.getInstance();
                currentTime.setTime(appointmentToEdit.getAppointmentDateTime());
                Calendar dateCal = Calendar.getInstance();
                dateCal.setTime(selectedDate);
                dateCal.set(Calendar.HOUR_OF_DAY, currentTime.get(Calendar.HOUR_OF_DAY));
                dateCal.set(Calendar.MINUTE, currentTime.get(Calendar.MINUTE));
                timeCombo.setSelectedItem(dateCal.getTime());
            }
        } catch (ParseException ex) {
            // Игнорируем ошибку парсинга
        }

        saveButton.addActionListener(e -> {
            try {
                // Валидация даты
                Date selectedDate = parseDate(dateField.getText());
                if (selectedDate == null) {
                            JOptionPane.showMessageDialog(editDialog, 
                        "Пожалуйста, введите корректную дату в формате ГГГГ-ММ-ДД", 
                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                // Проверка выбора времени
                Date selectedTime = (Date) timeCombo.getSelectedItem();
                if (selectedTime == null) {
                            JOptionPane.showMessageDialog(editDialog,
                        "Пожалуйста, выберите время приема",
                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                // Объединяем дату и время
                Calendar dateCal = Calendar.getInstance();
                dateCal.setTime(selectedDate);
                Calendar timeCal = Calendar.getInstance();
                timeCal.setTime(selectedTime);
                dateCal.set(Calendar.HOUR_OF_DAY, timeCal.get(Calendar.HOUR_OF_DAY));
                dateCal.set(Calendar.MINUTE, timeCal.get(Calendar.MINUTE));
                Date selectedDateTime = dateCal.getTime();

                        // Проверка выбора пациента
                        Patient selectedPatient = (Patient) patientCombo.getSelectedItem();
                        if (selectedPatient == null) {
                            JOptionPane.showMessageDialog(editDialog,
                                "Пожалуйста, выберите пациента",
                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        // Проверка выбора врача
                        Doctor selectedDoctor = (Doctor) doctorCombo.getSelectedItem();
                        if (selectedDoctor == null) {
                            JOptionPane.showMessageDialog(editDialog,
                                "Пожалуйста, выберите врача",
                                "Ошибка", JOptionPane.ERROR_MESSAGE);
                            return;
                        }

                        appointmentToEdit.setAppointmentDateTime(selectedDateTime);
                appointmentToEdit.setProcedureType((String) procedureTypeCombo.getSelectedItem());
                String newStatus = ((AppointmentStatus) statusCombo.getSelectedItem()).toString();
                appointmentToEdit.setStatus(newStatus);
                        appointmentToEdit.setPatientId(selectedPatient.getId());
                        appointmentToEdit.setDoctorId(selectedDoctor.getId());

                        appointmentController.updateAppointment(appointmentToEdit);

                // Логика для истории лечения при завершении приема
                if (newStatus.equals(AppointmentStatus.COMPLETED.toString())) {
                    Patient patient = allPatients.stream()
                        .filter(p -> p.getId() == appointmentToEdit.getPatientId())
                        .findFirst()
                        .orElse(null);
                        
                    if (patient != null) {
                        String procedureType = appointmentToEdit.getProcedureType();
                        if (procedureType != null && procedureType.startsWith("Лечение заболевания - ")) {
                            String diseaseName = procedureType.substring("Лечение заболевания - ".length());
                            String previousDiseases = patient.getPreviousDiseases();
                            if (previousDiseases == null || previousDiseases.isEmpty()) {
                                patient.setPreviousDiseases(diseaseName);
                            } else {
                                patient.setPreviousDiseases(previousDiseases + ", " + diseaseName);
                            }
                            patient.setDisease(""); // Очищаем текущее заболевание
                            patientController.updatePatient(patient); // Обновляем данные пациента
                        }
                    }
                }

                        loadAppointments();
                        editDialog.dispose();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(editDialog, 
                            "Ошибка при сохранении записи: " + ex.getMessage(), 
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> editDialog.dispose());

        editDialog.setSize(500, 350);
            editDialog.setLocationRelativeTo((JFrame) SwingUtilities.getWindowAncestor(this));
            editDialog.setVisible(true);
    }

    private void updateTimeSlots(JComboBox<Date> timeCombo, Date date, int doctorId, Integer excludeAppointmentId) {
        timeCombo.removeAllItems();
        
        // Получаем все записи на выбранную дату для выбранного врача
        List<Appointment> doctorAppointments = allAppointments.stream()
            .filter(a -> a.getDoctorId() == doctorId && 
                        a.getAppointmentDateTime() != null &&
                        isSameDay(a.getAppointmentDateTime(), date))
            .collect(Collectors.toList());

        // Создаем список занятых временных слотов
        Set<Date> busySlots = doctorAppointments.stream()
            .filter(a -> excludeAppointmentId == null || a.getId() != excludeAppointmentId)
            .map(Appointment::getAppointmentDateTime)
            .collect(Collectors.toSet());

        // Генерируем все возможные временные слоты
        List<Date> allSlots = generateTimeSlots(date);
        
        // Фильтруем доступные слоты
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
        
        // Сортируем временные слоты
        availableSlots.sort(Date::compareTo);
        
        for (Date slot : availableSlots) {
            timeCombo.addItem(slot);
        }
    }

    private void deleteAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        // Получаем ID записи из выбранной строки
        int appointmentId = (int) tableModel.getValueAt(selectedRow, 0);
        Appointment appointmentToDelete = appointmentController.getAppointmentById(appointmentId);

        if (appointmentToDelete == null) {
            JOptionPane.showMessageDialog(this,
                "Запись на прием не найдена",
                "Ошибка",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Вы уверены, что хотите удалить эту запись на прием?",
            "Подтверждение удаления",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            appointmentController.deleteAppointment(appointmentId);
                loadAppointments();
            }
    }

    // Добавляем метод для обновления списков
    public void refreshData() {
        allPatients = patientController.getAllPatients();
        allDoctors = doctorController.getAllDoctors();
        loadAppointments();
    }

    public void applyFilter(int patientId, int doctorId) {
        // Показываем панель фильтров
        if (!isFilterPanelVisible) {
            toggleFilterPanel();
        }

        // Сбрасываем противоположный фильтр
        if (patientId > 0) {
            // Если фильтруем по пациенту, сбрасываем фильтр по врачу
            doctorFilterCombo.setSelectedIndex(0);
        } else if (doctorId > 0) {
            // Если фильтруем по врачу, сбрасываем фильтр по пациенту
            patientFilterCombo.setSelectedIndex(0);
        }

        // Устанавливаем значения фильтров
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

        // Применяем фильтры
        applyFilters(startDateFilterField, endDateFilterField, patientFilterCombo, doctorFilterCombo);
    }
}