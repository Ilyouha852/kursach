package ui;

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

import model.Doctor;
import controller.DoctorController;
import model.DentalSpecialty;
import util.ValidationUtils;
import model.Appointment;
import controller.AppointmentController;

public class DoctorPanel extends JPanel {

    private JTable doctorTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JTextField searchField;
    private List<Doctor> allDoctors;

    private final DoctorController doctorController = new DoctorController();
    private final AppointmentController appointmentController = new AppointmentController();

    public DoctorPanel() {
        setLayout(new BorderLayout());

        // Создаем панель поиска
        JPanel searchPanel = new JPanel(new BorderLayout(5, 0));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        JLabel searchLabel = new JLabel("Поиск по ФИО:");
        searchField = new JTextField();
        searchPanel.add(searchLabel, BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);

        // Добавляем слушатель для поиска при вводе
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { search(); }
            public void removeUpdate(DocumentEvent e) { search(); }
            public void insertUpdate(DocumentEvent e) { search(); }
        });

        // Создаем модель таблицы
        String[] columnNames = {"ID", "Фамилия", "Имя", "Отчество", "Специализация", "Телефон", "Email"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Запрещаем редактирование ячеек напрямую
            }
        };

        doctorTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(doctorTable);

        // Создаем контекстное меню
        JPopupMenu popupMenu = new JPopupMenu();
        JMenuItem editMenuItem = new JMenuItem("Редактировать");
        JMenuItem deleteMenuItem = new JMenuItem("Удалить");
        JMenuItem showAppointmentsMenuItem = new JMenuItem("Показать записи приемов");

        popupMenu.add(editMenuItem);
        popupMenu.add(deleteMenuItem);
        popupMenu.addSeparator();
        popupMenu.add(showAppointmentsMenuItem);

        // Добавляем обработчик правой кнопки мыши
        doctorTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = doctorTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        doctorTable.setRowSelectionInterval(row, row);
                        showContextMenu(e, row);
                    }
                }
            }
        });

        // Обработчики для пунктов меню
        editMenuItem.addActionListener(e -> {
            int selectedRow = doctorTable.getSelectedRow();
            if (selectedRow != -1) {
                editDoctor();
            }
        });

        deleteMenuItem.addActionListener(e -> {
            int selectedRow = doctorTable.getSelectedRow();
            if (selectedRow != -1) {
                deleteDoctor();
            }
        });

        showAppointmentsMenuItem.addActionListener(e -> {
            int selectedRow = doctorTable.getSelectedRow();
            if (selectedRow != -1) {
                int doctorId = (int) tableModel.getValueAt(selectedRow, 0);
                MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
                mainFrame.showAppointmentsWithFilter(0, doctorId);
            }
        });

        // Панель кнопок управления
        JPanel controlPanel = new JPanel();
        addButton = new JButton("Добавить");
        editButton = new JButton("Редактировать");
        deleteButton = new JButton("Удалить");

        controlPanel.add(addButton);
        controlPanel.add(editButton);
        controlPanel.add(deleteButton);

        // Добавляем все компоненты на панель
        add(searchPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(controlPanel, BorderLayout.SOUTH);

        // Загружаем врачей из базы данных
        loadDoctors();

        // Обработчики событий для кнопок
        addButton.addActionListener(e -> addDoctor());
        editButton.addActionListener(e -> {
            int selectedRow = doctorTable.getSelectedRow();
            if (selectedRow != -1) {
                editDoctor();
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = doctorTable.getSelectedRow();
            if (selectedRow != -1) {
                deleteDoctor();
            }
        });

        // Добавляем слушатель выбора строки
        doctorTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                editButton.setEnabled(doctorTable.getSelectedRow() != -1);
                deleteButton.setEnabled(doctorTable.getSelectedRow() != -1);
            }
        });
    }

    private void loadDoctors() {
        // Загружаем всех врачей
        allDoctors = doctorController.getAllDoctors();
        // Отображаем их в таблице
        updateTable(allDoctors);
    }

    private void search() {
        String searchText = searchField.getText().toLowerCase().trim();
        
        if (searchText.isEmpty()) {
            // Если поле поиска пустое, показываем всех врачей
            updateTable(allDoctors);
        } else {
            // Фильтруем врачей по поисковому запросу
            List<Doctor> filteredDoctors = allDoctors.stream()
                .filter(doctor -> {
                    String fullName = (doctor.getLastName() + " " + 
                                     doctor.getFirstName() + " " + 
                                     (doctor.getMiddleName() != null ? doctor.getMiddleName() : ""))
                                     .toLowerCase();
                    return fullName.contains(searchText);
                })
                .collect(Collectors.toList());
            
            updateTable(filteredDoctors);
        }
    }

    private void updateTable(List<Doctor> doctors) {
        // Очищаем таблицу
        tableModel.setRowCount(0);
        
        // Добавляем отфильтрованных врачей
        for (Doctor doctor : doctors) {
            Object[] rowData = {
                doctor.getId(),
                doctor.getLastName(),
                doctor.getFirstName(),
                doctor.getMiddleName(),
                doctor.getSpecialization(),
                doctor.getPhoneNumber(),
                doctor.getEmail()
            };
            tableModel.addRow(rowData);
        }
    }

    private JFormattedTextField createPhoneField() {
        try {
            MaskFormatter maskFormatter = new MaskFormatter("+7 (###) ###-##-##");
            maskFormatter.setPlaceholderCharacter('_');
            JFormattedTextField phoneField = new JFormattedTextField(maskFormatter);
            phoneField.setColumns(20);
            return phoneField;
        } catch (ParseException e) {
            // В случае ошибки возвращаем обычное текстовое поле
            return new JFormattedTextField();
        }
    }

    private void addDoctor() {
        JDialog addDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Добавить врача", true);
        addDialog.setLayout(new BorderLayout());

        JPanel mainInfoPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        mainInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lastNameLabel = new JLabel("Фамилия:");
        JTextField lastNameField = new JTextField();
        JLabel firstNameLabel = new JLabel("Имя:");
        JTextField firstNameField = new JTextField();
        JLabel middleNameLabel = new JLabel("Отчество:");
        JTextField middleNameField = new JTextField();
        JLabel specialtyLabel = new JLabel("Специализация:");
        JComboBox<String> specialtyComboBox = new JComboBox<>(new String[] {
            DentalSpecialty.THERAPIST,
            DentalSpecialty.SURGEON,
            DentalSpecialty.ORTHODONTIST,
            DentalSpecialty.ENDODONTIST,
            DentalSpecialty.PERIODONTIST,
            DentalSpecialty.PROSTHODONTIST,
            DentalSpecialty.PEDIATRIC_DENTIST
        });
        specialtyComboBox.setSelectedItem(DentalSpecialty.THERAPIST);
        JLabel phoneLabel = new JLabel("Телефон:");
        JFormattedTextField phoneField = createPhoneField();
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField();

        mainInfoPanel.add(lastNameLabel);
        mainInfoPanel.add(lastNameField);
        mainInfoPanel.add(firstNameLabel);
        mainInfoPanel.add(firstNameField);
        mainInfoPanel.add(middleNameLabel);
        mainInfoPanel.add(middleNameField);
        mainInfoPanel.add(specialtyLabel);
        mainInfoPanel.add(specialtyComboBox);
        mainInfoPanel.add(phoneLabel);
        mainInfoPanel.add(phoneField);
        mainInfoPanel.add(emailLabel);
        mainInfoPanel.add(emailField);

        int result = JOptionPane.showConfirmDialog(this, mainInfoPanel, "Добавить врача",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String middleName = middleNameField.getText().trim();
            String specialty = (String) specialtyComboBox.getSelectedItem();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            // Валидация полей
            if (!ValidationUtils.isValidName(firstName)) {
                JOptionPane.showMessageDialog(this,
                    "Имя должно содержать только русские буквы и быть не длиннее 50 символов",
                    "Ошибка валидации",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ValidationUtils.isValidName(lastName)) {
                JOptionPane.showMessageDialog(this,
                    "Фамилия должна содержать только русские буквы и быть не длиннее 50 символов",
                    "Ошибка валидации",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!middleName.isEmpty() && !ValidationUtils.isValidName(middleName)) {
                JOptionPane.showMessageDialog(this,
                    "Отчество должно содержать только русские буквы и быть не длиннее 50 символов",
                    "Ошибка валидации",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ValidationUtils.isValidPhone(phone)) {
                JOptionPane.showMessageDialog(this,
                    "Неверный формат номера телефона. Используйте формат: +7 (XXX) XXX-XX-XX или 8 (XXX) XXX-XX-XX",
                    "Ошибка валидации",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ValidationUtils.isValidEmail(email)) {
                JOptionPane.showMessageDialog(this,
                    "Неверный формат email адреса",
                    "Ошибка валидации",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Проверка уникальности email
            Doctor existingDoctor = doctorController.getDoctorByEmail(email);
            if (existingDoctor != null) {
                JOptionPane.showMessageDialog(this,
                    "Врач с таким email уже существует",
                    "Ошибка валидации",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Форматируем номер телефона
                phone = ValidationUtils.formatPhoneNumber(phone);
                
                Doctor doctor = new Doctor();
                doctor.setFirstName(firstName);
                doctor.setLastName(lastName);
                doctor.setMiddleName(middleName.isEmpty() ? null : middleName);
                doctor.setSpecialization(specialty);
                doctor.setPhoneNumber(phone);
                doctor.setEmail(email);

                doctorController.saveDoctor(doctor);
                loadDoctors();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Ошибка при добавлении врача: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void editDoctor() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Пожалуйста, выберите врача для редактирования",
                "Предупреждение",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int doctorId = (int) tableModel.getValueAt(selectedRow, 0);
        Doctor doctorToEdit = doctorController.getDoctorById(doctorId);

        if (doctorToEdit == null) {
            JOptionPane.showMessageDialog(this,
                "Врач не найден",
                "Ошибка",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog editDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Редактировать врача", true);
        editDialog.setLayout(new BorderLayout());

        JPanel mainInfoPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        mainInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lastNameLabel = new JLabel("Фамилия:");
        JTextField lastNameField = new JTextField(doctorToEdit.getLastName());
        JLabel firstNameLabel = new JLabel("Имя:");
        JTextField firstNameField = new JTextField(doctorToEdit.getFirstName());
        JLabel middleNameLabel = new JLabel("Отчество:");
        JTextField middleNameField = new JTextField(doctorToEdit.getMiddleName() != null ? doctorToEdit.getMiddleName() : "");
        JLabel specialtyLabel = new JLabel("Специализация:");
        JComboBox<String> specialtyComboBox = new JComboBox<>(new String[] {
            DentalSpecialty.THERAPIST,
            DentalSpecialty.SURGEON,
            DentalSpecialty.ORTHODONTIST,
            DentalSpecialty.ENDODONTIST,
            DentalSpecialty.PERIODONTIST,
            DentalSpecialty.PROSTHODONTIST,
            DentalSpecialty.PEDIATRIC_DENTIST
        });
        specialtyComboBox.setSelectedItem(doctorToEdit.getSpecialization());
        JLabel phoneLabel = new JLabel("Телефон:");
        JFormattedTextField phoneField = createPhoneField();
        phoneField.setText(doctorToEdit.getPhoneNumber());
        JLabel emailLabel = new JLabel("Email:");
        JTextField emailField = new JTextField(doctorToEdit.getEmail());

        mainInfoPanel.add(lastNameLabel);
        mainInfoPanel.add(lastNameField);
        mainInfoPanel.add(firstNameLabel);
        mainInfoPanel.add(firstNameField);
        mainInfoPanel.add(middleNameLabel);
        mainInfoPanel.add(middleNameField);
        mainInfoPanel.add(specialtyLabel);
        mainInfoPanel.add(specialtyComboBox);
        mainInfoPanel.add(phoneLabel);
        mainInfoPanel.add(phoneField);
        mainInfoPanel.add(emailLabel);
        mainInfoPanel.add(emailField);

        int result = JOptionPane.showConfirmDialog(this, mainInfoPanel, "Редактировать врача",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String firstName = firstNameField.getText().trim();
            String lastName = lastNameField.getText().trim();
            String middleName = middleNameField.getText().trim();
            String specialty = (String) specialtyComboBox.getSelectedItem();
            String phone = phoneField.getText().trim();
            String email = emailField.getText().trim();

            // Валидация полей
            if (!ValidationUtils.isValidName(firstName)) {
                JOptionPane.showMessageDialog(this,
                    "Имя должно содержать только русские буквы и быть не длиннее 50 символов",
                    "Ошибка валидации",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ValidationUtils.isValidName(lastName)) {
                JOptionPane.showMessageDialog(this,
                    "Фамилия должна содержать только русские буквы и быть не длиннее 50 символов",
                    "Ошибка валидации",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!middleName.isEmpty() && !ValidationUtils.isValidName(middleName)) {
                JOptionPane.showMessageDialog(this,
                    "Отчество должно содержать только русские буквы и быть не длиннее 50 символов",
                    "Ошибка валидации",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ValidationUtils.isValidPhone(phone)) {
                JOptionPane.showMessageDialog(this,
                    "Неверный формат номера телефона. Используйте формат: +7 (XXX) XXX-XX-XX или 8 (XXX) XXX-XX-XX",
                    "Ошибка валидации",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!ValidationUtils.isValidEmail(email)) {
                JOptionPane.showMessageDialog(this,
                    "Неверный формат email адреса",
                    "Ошибка валидации",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Проверка уникальности email (исключая текущего врача)
            Doctor existingDoctor = doctorController.getDoctorByEmail(email);
            if (existingDoctor != null && existingDoctor.getId() != doctorToEdit.getId()) {
                JOptionPane.showMessageDialog(this,
                    "Врач с таким email уже существует",
                    "Ошибка валидации",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            try {
                // Форматируем номер телефона
                phone = ValidationUtils.formatPhoneNumber(phone);
                
                doctorToEdit.setFirstName(firstName);
                doctorToEdit.setLastName(lastName);
                doctorToEdit.setMiddleName(middleName.isEmpty() ? null : middleName);
                doctorToEdit.setSpecialization(specialty);
                doctorToEdit.setPhoneNumber(phone);
                doctorToEdit.setEmail(email);

                doctorController.updateDoctor(doctorToEdit);
                loadDoctors();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Ошибка при обновлении врача: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteDoctor() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Пожалуйста, выберите врача для удаления",
                "Предупреждение",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int doctorId = (int) tableModel.getValueAt(selectedRow, 0);
        Doctor doctorToDelete = doctorController.getDoctorById(doctorId);

        if (doctorToDelete == null) {
            JOptionPane.showMessageDialog(this,
                "Врач не найден",
                "Ошибка",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
            "Вы уверены, что хотите удалить этого врача?",
            "Подтверждение удаления",
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                doctorController.deleteDoctor(doctorId);
                loadDoctors();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Ошибка при удалении врача: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showContextMenu(MouseEvent e, int row) {
        if (row >= 0) {
            JPopupMenu popup = new JPopupMenu();
            JMenuItem editItem = new JMenuItem("Редактировать");
            JMenuItem deleteItem = new JMenuItem("Удалить");
            JMenuItem showAppointmentsItem = new JMenuItem("Показать записи");
            JMenuItem rescheduleItem = new JMenuItem("Перенос записей");

            editItem.addActionListener(_ -> editDoctor());
            deleteItem.addActionListener(_ -> deleteDoctor());
            showAppointmentsItem.addActionListener(_ -> {
                int doctorId = (int) tableModel.getValueAt(row, 0);
                MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
                mainFrame.showAppointmentsWithFilter(0, doctorId);
            });
            rescheduleItem.addActionListener(_ -> rescheduleAppointments());

            popup.add(editItem);
            popup.add(deleteItem);
            popup.addSeparator();
            popup.add(showAppointmentsItem);
            popup.add(rescheduleItem);

            popup.show(e.getComponent(), e.getX(), e.getY());
        }
    }

    private void rescheduleAppointments() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this,
                "Пожалуйста, выберите врача",
                "Предупреждение",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        int doctorId = (int) tableModel.getValueAt(selectedRow, 0);
        Doctor selectedDoctor = doctorController.getDoctorById(doctorId);

        // Получаем все записи врача
        List<Appointment> allDoctorAppointments = appointmentController.getAllAppointments().stream()
            .filter(a -> a.getDoctorId() == doctorId)
            .collect(Collectors.toList());

        if (allDoctorAppointments.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "У выбранного врача нет записей",
                "Информация",
                JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Находим самую раннюю и самую позднюю даты
        Date minDate = allDoctorAppointments.stream()
            .map(Appointment::getAppointmentDateTime)
            .min(Date::compareTo)
            .orElse(null);
        Date maxDate = allDoctorAppointments.stream()
            .map(Appointment::getAppointmentDateTime)
            .max(Date::compareTo)
            .orElse(null);

        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Перенос записей", true);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Поля для периода
        JLabel startDateLabel = new JLabel("Начало периода:");
        JFormattedTextField startDateField = createDateField();
        JLabel endDateLabel = new JLabel("Конец периода:");
        JFormattedTextField endDateField = createDateField();

        // Заполняем поля датами
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (minDate != null) {
            startDateField.setText(dateFormat.format(minDate));
        }
        if (maxDate != null) {
            endDateField.setText(dateFormat.format(maxDate));
        }

        // Поле для смещения
        JLabel offsetLabel = new JLabel("Смещение (дней):");
        JSpinner offsetSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));

        mainPanel.add(startDateLabel);
        mainPanel.add(startDateField);
        mainPanel.add(endDateLabel);
        mainPanel.add(endDateField);
        mainPanel.add(offsetLabel);
        mainPanel.add(offsetSpinner);

        JPanel buttonPanel = new JPanel();
        JButton confirmButton = new JButton("Подтвердить");
        JButton cancelButton = new JButton("Отмена");

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        confirmButton.addActionListener(e -> {
            try {
                Date startDate = parseDate(startDateField.getText());
                Date endDate = parseDate(endDateField.getText());
                int offset = (int) offsetSpinner.getValue();

                if (startDate == null || endDate == null) {
                    JOptionPane.showMessageDialog(dialog,
                        "Пожалуйста, введите корректные даты",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (endDate.before(startDate)) {
                    JOptionPane.showMessageDialog(dialog,
                        "Дата окончания не может быть раньше даты начала",
                        "Ошибка",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Получаем записи для переноса
                List<Appointment> appointments = appointmentController.getAppointmentsByDoctorAndPeriod(
                    doctorId, startDate, endDate);

                if (appointments.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "В выбранный период нет записей для переноса",
                        "Информация",
                        JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                // Подтверждение
                int confirm = JOptionPane.showConfirmDialog(dialog,
                    String.format("Будет перенесено %d записей на %d дней вперед. Продолжить?",
                        appointments.size(), offset),
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION);

                if (confirm == JOptionPane.YES_OPTION) {
                    // Переносим записи
                    Calendar calendar = Calendar.getInstance();
                    for (Appointment appointment : appointments) {
                        calendar.setTime(appointment.getAppointmentDateTime());
                        calendar.add(Calendar.DAY_OF_MONTH, offset);
                        appointment.setAppointmentDateTime(calendar.getTime());
                    }

                    // Сохраняем изменения
                    appointmentController.updateAppointments(appointments);

                    JOptionPane.showMessageDialog(dialog,
                        "Записи успешно перенесены",
                        "Успех",
                        JOptionPane.INFORMATION_MESSAGE);

                    dialog.dispose();
                }
            } catch (ParseException ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Ошибка при обработке дат: " + ex.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo((JFrame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);
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

    private Date parseDate(String dateStr) throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        return dateFormat.parse(dateStr);
    }

    public void refreshData() {
        loadDoctors();
    }
}
