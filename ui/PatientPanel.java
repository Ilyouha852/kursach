package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import model.Patient;
import model.PatientDisease;
import controller.PatientController;
import util.ValidationUtils;

public class PatientPanel extends JPanel {

    private JTable patientTable;
    private DefaultTableModel tableModel;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JTextField searchField;
    private List<Patient> allPatients;

    private final PatientController patientController = new PatientController();

    public PatientPanel() {
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
        String[] columnNames = {"ID", "Фамилия", "Имя", "Отчество", "Дата рождения", "Телефон", "Заболевание"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        patientTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(patientTable);

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
        patientTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = patientTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        patientTable.setRowSelectionInterval(row, row);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });

        // Обработчики для пунктов меню
        editMenuItem.addActionListener(e -> {
            int selectedRow = patientTable.getSelectedRow();
            if (selectedRow != -1) {
                editPatient();
            }
        });

        deleteMenuItem.addActionListener(e -> {
            int selectedRow = patientTable.getSelectedRow();
            if (selectedRow != -1) {
                deletePatient();
            }
        });

        showAppointmentsMenuItem.addActionListener(e -> {
            int selectedRow = patientTable.getSelectedRow();
            if (selectedRow != -1) {
                int patientId = (int) tableModel.getValueAt(selectedRow, 0);
                MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
                mainFrame.showAppointmentsWithFilter(patientId, 0);
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

        // Загружаем пациентов из базы данных
        loadPatients();

        // Обработчики событий для кнопок
        addButton.addActionListener(e -> addPatient());
        editButton.addActionListener(e -> {
            int selectedRow = patientTable.getSelectedRow();
            if (selectedRow != -1) {
                editPatient();
            }
        });
        deleteButton.addActionListener(e -> {
            int selectedRow = patientTable.getSelectedRow();
            if (selectedRow != -1) {
                deletePatient();
            }
        });

        // Добавляем слушатель выбора строки
        patientTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                editButton.setEnabled(patientTable.getSelectedRow() != -1);
                deleteButton.setEnabled(patientTable.getSelectedRow() != -1);
            }
        });
    }

    private void loadPatients() {
        // Загружаем всех пациентов
        allPatients = patientController.getAllPatients();
        // Отображаем их в таблице
        updateTable(allPatients);
    }

    private void search() {
        String searchText = searchField.getText().toLowerCase().trim();
        
        if (searchText.isEmpty()) {
            // Если поле поиска пустое, показываем всех пациентов
            updateTable(allPatients);
        } else {
            // Фильтруем пациентов по поисковому запросу
            List<Patient> filteredPatients = allPatients.stream()
                .filter(patient -> {
                    String fullName = (patient.getLastName() + " " + 
                                     patient.getFirstName() + " " + 
                                     (patient.getMiddleName() != null ? patient.getMiddleName() : ""))
                                     .toLowerCase();
                    return fullName.contains(searchText);
                })
                .collect(Collectors.toList());
            
            updateTable(filteredPatients);
        }
    }

    private void updateTable(List<Patient> patients) {
        // Очищаем таблицу
        tableModel.setRowCount(0);
        
        // Добавляем отфильтрованных пациентов
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        
        for (Patient patient : patients) {
            Object[] rowData = {
                patient.getId(),
                patient.getLastName(),
                patient.getFirstName(),
                patient.getMiddleName(),
                patient.getDateOfBirth() != null ? dateFormat.format(patient.getDateOfBirth()) : "",
                patient.getPhoneNumber(),
                patient.getDisease()
            };
            tableModel.addRow(rowData);
        }
    }

    private JFormattedTextField createDateField(Date initialDate) {
        MaskFormatter maskFormatter = null;
        try {
            maskFormatter = new MaskFormatter("####-##-##");
            maskFormatter.setPlaceholderCharacter('_');
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JFormattedTextField dateField = new JFormattedTextField(maskFormatter);
        dateField.setColumns(10);
        
        if (initialDate != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateField.setText(dateFormat.format(initialDate));
        }

        // Добавляем валидацию ввода
        dateField.addPropertyChangeListener("value", new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                validateDate(dateField);
            }
        });

        return dateField;
    }

    private void validateDate(JFormattedTextField dateField) {
        String text = dateField.getText();
        if (text.length() == 10) { // Проверяем только когда введена полная дата
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                dateFormat.setLenient(false);
                Date date = dateFormat.parse(text);
                
                // Проверяем, что дата не в будущем
                if (date.after(new Date())) {
                    dateField.setBackground(new Color(255, 200, 200)); // Красноватый фон для неверной даты
                    return;
                }
                
                dateField.setBackground(Color.WHITE);
            } catch (ParseException e) {
                dateField.setBackground(new Color(255, 200, 200));
            }
        } else {
            dateField.setBackground(Color.WHITE);
        }
    }

    private Date parseDate(String dateStr) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            return dateFormat.parse(dateStr);
        } catch (ParseException e) {
            return null;
        }
    }

    private JComboBox<Object> createDiseaseComboBox() {
        List<Object> items = new ArrayList<>();
        items.add("Добавить заболевание...");
        items.addAll(List.of(PatientDisease.values()));
        
        JComboBox<Object> comboBox = new JComboBox<>(items.toArray());
        comboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (comboBox.getSelectedIndex() == 0) {
                    // Показываем диалог добавления заболевания
                    AddDiseaseDialog dialog = new AddDiseaseDialog((JFrame) SwingUtilities.getWindowAncestor(PatientPanel.this));
                    dialog.setVisible(true);
                    
                    if (dialog.getDiseaseName() != null) {
                        // Добавляем новое заболевание в список
                        comboBox.insertItemAt(dialog.getDiseaseName(), 1);
                        comboBox.setSelectedItem(dialog.getDiseaseName());
                    } else {
                        // Возвращаемся к пункту "Добавить заболевание..."
                        comboBox.setSelectedIndex(0);
                    }
                }
            }
        });
        
        return comboBox;
    }

    private void addPatient() {
        JDialog addDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Добавить пациента", true);
        addDialog.setLayout(new BorderLayout());

        // Основная информация
        JPanel mainInfoPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        JLabel firstNameLabel = new JLabel("Имя:");
        JTextField firstNameField = new JTextField();
        JLabel lastNameLabel = new JLabel("Фамилия:");
        JTextField lastNameField = new JTextField();
        JLabel middleNameLabel = new JLabel("Отчество:");
        JTextField middleNameField = new JTextField();
        JLabel dateOfBirthLabel = new JLabel("Дата рождения:");
        JFormattedTextField dateField = createDateField(null);
        JLabel phoneNumberLabel = new JLabel("Номер телефона:");
        JTextField phoneNumberField = new JTextField();
        JLabel addressLabel = new JLabel("Адрес:");
        JTextField addressField = new JTextField();
        JLabel diseaseLabel = new JLabel("Заболевание:");
        JComboBox<Object> diseaseCombo = createDiseaseComboBox();

        mainInfoPanel.add(lastNameLabel);
        mainInfoPanel.add(lastNameField);
        mainInfoPanel.add(firstNameLabel);
        mainInfoPanel.add(firstNameField);
        mainInfoPanel.add(middleNameLabel);
        mainInfoPanel.add(middleNameField);
        mainInfoPanel.add(dateOfBirthLabel);
        mainInfoPanel.add(dateField);
        mainInfoPanel.add(phoneNumberLabel);
        mainInfoPanel.add(phoneNumberField);
        mainInfoPanel.add(addressLabel);
        mainInfoPanel.add(addressField);
        mainInfoPanel.add(diseaseLabel);
        mainInfoPanel.add(diseaseCombo);

        // Медицинская история
        JPanel medicalHistoryPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        medicalHistoryPanel.setBorder(BorderFactory.createTitledBorder("Медицинская история"));

        JLabel chronicDiseasesLabel = new JLabel("Хронические заболевания:");
        JTextArea chronicDiseasesArea = new JTextArea(3, 20);
        JScrollPane chronicDiseasesScroll = new JScrollPane(chronicDiseasesArea);

        JLabel allergiesLabel = new JLabel("Аллергии:");
        JTextArea allergiesArea = new JTextArea(3, 20);
        JScrollPane allergiesScroll = new JScrollPane(allergiesArea);

        JLabel previousDiseasesLabel = new JLabel("Предыдущие заболевания:");
        JTextArea previousDiseasesArea = new JTextArea(3, 20);
        JScrollPane previousDiseasesScroll = new JScrollPane(previousDiseasesArea);

        JLabel hereditaryDiseasesLabel = new JLabel("Наследственные заболевания:");
        JTextArea hereditaryDiseasesArea = new JTextArea(3, 20);
        JScrollPane hereditaryDiseasesScroll = new JScrollPane(hereditaryDiseasesArea);

        medicalHistoryPanel.add(chronicDiseasesLabel);
        medicalHistoryPanel.add(chronicDiseasesScroll);
        medicalHistoryPanel.add(allergiesLabel);
        medicalHistoryPanel.add(allergiesScroll);
        medicalHistoryPanel.add(previousDiseasesLabel);
        medicalHistoryPanel.add(previousDiseasesScroll);
        medicalHistoryPanel.add(hereditaryDiseasesLabel);
        medicalHistoryPanel.add(hereditaryDiseasesScroll);

        // Кнопки
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Добавляем все панели в диалог
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPanel.add(mainInfoPanel, BorderLayout.NORTH);
        contentPanel.add(medicalHistoryPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        addDialog.add(contentPanel);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Валидация имени
                    String firstName = firstNameField.getText().trim();
                    if (!ValidationUtils.isValidName(firstName)) {
                        JOptionPane.showMessageDialog(addDialog,
                            "Имя должно содержать только русские буквы",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Валидация фамилии
                    String lastName = lastNameField.getText().trim();
                    if (!ValidationUtils.isValidName(lastName)) {
                        JOptionPane.showMessageDialog(addDialog,
                            "Фамилия должна содержать только русские буквы",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Валидация отчества
                    String middleName = middleNameField.getText().trim();
                    if (!middleName.isEmpty() && !ValidationUtils.isValidName(middleName)) {
                        JOptionPane.showMessageDialog(addDialog,
                            "Отчество должно содержать только русские буквы",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Валидация даты рождения
                    Date selectedDate = parseDate(dateField.getText());
                    if (selectedDate == null) {
                        JOptionPane.showMessageDialog(addDialog,
                            "Пожалуйста, введите корректную дату рождения в формате ГГГГ-ММ-ДД",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Валидация номера телефона
                    String phoneNumber = phoneNumberField.getText().trim();
                    if (!ValidationUtils.isValidPhone(phoneNumber)) {
                        JOptionPane.showMessageDialog(addDialog,
                            "Пожалуйста, введите корректный номер телефона",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    phoneNumber = ValidationUtils.formatPhoneNumber(phoneNumber);

                    // Валидация адреса
                    String address = addressField.getText().trim();
                    if (!ValidationUtils.isValidAddress(address)) {
                        JOptionPane.showMessageDialog(addDialog,
                            "Адрес должен содержать минимум 5 символов",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    Patient newPatient = new Patient();
                    newPatient.setFirstName(firstName);
                    newPatient.setLastName(lastName);
                    newPatient.setMiddleName(middleName);
                    newPatient.setDateOfBirth(selectedDate);
                    newPatient.setPhoneNumber(phoneNumber);
                    newPatient.setAddress(address);
                    
                    Object selectedDisease = diseaseCombo.getSelectedItem();
                    if (selectedDisease instanceof PatientDisease) {
                        newPatient.setDisease(((PatientDisease) selectedDisease).toString());
                    } else if (selectedDisease instanceof String) {
                        newPatient.setDisease((String) selectedDisease);
                    }
                    
                    newPatient.setChronicDiseases(chronicDiseasesArea.getText());
                    newPatient.setAllergies(allergiesArea.getText());
                    newPatient.setPreviousDiseases(previousDiseasesArea.getText());
                    newPatient.setHereditaryDiseases(hereditaryDiseasesArea.getText());

                    patientController.savePatient(newPatient);
                    loadPatients();
                    addDialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(addDialog,
                        "Ошибка при сохранении пациента: " + ex.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addDialog.dispose();
            }
        });

        addDialog.setSize(500, 600);
        addDialog.setLocationRelativeTo((JFrame) SwingUtilities.getWindowAncestor(this));
        addDialog.setVisible(true);
    }

    private void editPatient() {
        int selectedIndex = patientTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this,
                "Пожалуйста, выберите пациента для редактирования",
                "Предупреждение",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        Patient patientToEdit = patientController.getAllPatients().get(selectedIndex);

        JDialog editDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Редактировать пациента", true);
        editDialog.setLayout(new BorderLayout());

        // Основная информация
        JPanel mainInfoPanel = new JPanel(new GridLayout(7, 2, 5, 5));
        JLabel firstNameLabel = new JLabel("Имя:");
        JTextField firstNameField = new JTextField(patientToEdit.getFirstName());
        JLabel lastNameLabel = new JLabel("Фамилия:");
        JTextField lastNameField = new JTextField(patientToEdit.getLastName());
        JLabel middleNameLabel = new JLabel("Отчество:");
        JTextField middleNameField = new JTextField(patientToEdit.getMiddleName());
        JLabel dateOfBirthLabel = new JLabel("Дата рождения:");
        JFormattedTextField dateField = createDateField(patientToEdit.getDateOfBirth());
        JLabel phoneNumberLabel = new JLabel("Номер телефона:");
        JTextField phoneNumberField = new JTextField(patientToEdit.getPhoneNumber());
        JLabel addressLabel = new JLabel("Адрес:");
        JTextField addressField = new JTextField(patientToEdit.getAddress());
        JLabel diseaseLabel = new JLabel("Заболевание:");
        JComboBox<Object> diseaseCombo = createDiseaseComboBox();
        String currentDisease = patientToEdit.getDisease();
        if (currentDisease != null) {
            PatientDisease disease = PatientDisease.fromDisplayName(currentDisease);
            if (disease != null) {
                diseaseCombo.setSelectedItem(disease);
            } else {
                // Если заболевание не найдено в enum, добавляем его в список
                diseaseCombo.insertItemAt(currentDisease, 1);
                diseaseCombo.setSelectedItem(currentDisease);
            }
        }

        mainInfoPanel.add(lastNameLabel);
        mainInfoPanel.add(lastNameField);
        mainInfoPanel.add(firstNameLabel);
        mainInfoPanel.add(firstNameField);
        mainInfoPanel.add(middleNameLabel);
        mainInfoPanel.add(middleNameField);
        mainInfoPanel.add(dateOfBirthLabel);
        mainInfoPanel.add(dateField);
        mainInfoPanel.add(phoneNumberLabel);
        mainInfoPanel.add(phoneNumberField);
        mainInfoPanel.add(addressLabel);
        mainInfoPanel.add(addressField);
        mainInfoPanel.add(diseaseLabel);
        mainInfoPanel.add(diseaseCombo);

        // Медицинская история
        JPanel medicalHistoryPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        medicalHistoryPanel.setBorder(BorderFactory.createTitledBorder("Медицинская история"));

        JLabel chronicDiseasesLabel = new JLabel("Хронические заболевания:");
        JTextArea chronicDiseasesArea = new JTextArea(patientToEdit.getChronicDiseases(), 3, 20);
        JScrollPane chronicDiseasesScroll = new JScrollPane(chronicDiseasesArea);

        JLabel allergiesLabel = new JLabel("Аллергии:");
        JTextArea allergiesArea = new JTextArea(patientToEdit.getAllergies(), 3, 20);
        JScrollPane allergiesScroll = new JScrollPane(allergiesArea);

        JLabel previousDiseasesLabel = new JLabel("Предыдущие заболевания:");
        JTextArea previousDiseasesArea = new JTextArea(patientToEdit.getPreviousDiseases(), 3, 20);
        JScrollPane previousDiseasesScroll = new JScrollPane(previousDiseasesArea);

        JLabel hereditaryDiseasesLabel = new JLabel("Наследственные заболевания:");
        JTextArea hereditaryDiseasesArea = new JTextArea(patientToEdit.getHereditaryDiseases(), 3, 20);
        JScrollPane hereditaryDiseasesScroll = new JScrollPane(hereditaryDiseasesArea);

        medicalHistoryPanel.add(chronicDiseasesLabel);
        medicalHistoryPanel.add(chronicDiseasesScroll);
        medicalHistoryPanel.add(allergiesLabel);
        medicalHistoryPanel.add(allergiesScroll);
        medicalHistoryPanel.add(previousDiseasesLabel);
        medicalHistoryPanel.add(previousDiseasesScroll);
        medicalHistoryPanel.add(hereditaryDiseasesLabel);
        medicalHistoryPanel.add(hereditaryDiseasesScroll);

        // Кнопки
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Добавляем все панели в диалог
        JPanel contentPanel = new JPanel(new BorderLayout(5, 5));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        contentPanel.add(mainInfoPanel, BorderLayout.NORTH);
        contentPanel.add(medicalHistoryPanel, BorderLayout.CENTER);
        contentPanel.add(buttonPanel, BorderLayout.SOUTH);

        editDialog.add(contentPanel);

        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    // Валидация имени
                    String firstName = firstNameField.getText().trim();
                    if (!ValidationUtils.isValidName(firstName)) {
                        JOptionPane.showMessageDialog(editDialog,
                            "Имя должно содержать только русские буквы",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Валидация фамилии
                    String lastName = lastNameField.getText().trim();
                    if (!ValidationUtils.isValidName(lastName)) {
                        JOptionPane.showMessageDialog(editDialog,
                            "Фамилия должна содержать только русские буквы",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Валидация отчества
                    String middleName = middleNameField.getText().trim();
                    if (!middleName.isEmpty() && !ValidationUtils.isValidName(middleName)) {
                        JOptionPane.showMessageDialog(editDialog,
                            "Отчество должно содержать только русские буквы",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Валидация даты рождения
                    Date selectedDate = parseDate(dateField.getText());
                    if (selectedDate == null) {
                        JOptionPane.showMessageDialog(editDialog,
                            "Пожалуйста, введите корректную дату рождения в формате ГГГГ-ММ-ДД",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // Валидация номера телефона
                    String phoneNumber = phoneNumberField.getText().trim();
                    if (!ValidationUtils.isValidPhone(phoneNumber)) {
                        JOptionPane.showMessageDialog(editDialog,
                            "Пожалуйста, введите корректный номер телефона",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    phoneNumber = ValidationUtils.formatPhoneNumber(phoneNumber);

                    // Валидация адреса
                    String address = addressField.getText().trim();
                    if (!ValidationUtils.isValidAddress(address)) {
                        JOptionPane.showMessageDialog(editDialog,
                            "Адрес должен содержать минимум 5 символов",
                            "Ошибка", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    patientToEdit.setFirstName(firstName);
                    patientToEdit.setLastName(lastName);
                    patientToEdit.setMiddleName(middleName);
                    patientToEdit.setDateOfBirth(selectedDate);
                    patientToEdit.setPhoneNumber(phoneNumber);
                    patientToEdit.setAddress(address);
                    
                    Object selectedDisease = diseaseCombo.getSelectedItem();
                    if (selectedDisease instanceof PatientDisease) {
                        patientToEdit.setDisease(((PatientDisease) selectedDisease).toString());
                    } else if (selectedDisease instanceof String) {
                        patientToEdit.setDisease((String) selectedDisease);
                    }
                    
                    patientToEdit.setChronicDiseases(chronicDiseasesArea.getText());
                    patientToEdit.setAllergies(allergiesArea.getText());
                    patientToEdit.setPreviousDiseases(previousDiseasesArea.getText());
                    patientToEdit.setHereditaryDiseases(hereditaryDiseasesArea.getText());

                    patientController.updatePatient(patientToEdit);
                    loadPatients();
                    editDialog.dispose();
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(editDialog,
                        "Ошибка при сохранении пациента: " + ex.getMessage(),
                        "Ошибка", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editDialog.dispose();
            }
        });

        editDialog.setSize(500, 600);
        editDialog.setLocationRelativeTo((JFrame) SwingUtilities.getWindowAncestor(this));
        editDialog.setVisible(true);
    }

    private void deletePatient() {
        int selectedIndex = patientTable.getSelectedRow();
        if (selectedIndex == -1) {
            JOptionPane.showMessageDialog(this,
                "Пожалуйста, выберите пациента для удаления",
                "Предупреждение",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        Patient patient = patientController.getAllPatients().get(selectedIndex);
        int choice = JOptionPane.showConfirmDialog(this,
            "Вы уверены, что хотите удалить пациента " + patient.getLastName() + " " + patient.getFirstName() + "?",
            "Подтверждение удаления",
            JOptionPane.YES_NO_OPTION);

        if (choice == JOptionPane.YES_OPTION) {
            try {
                patientController.deletePatient(patient.getId());
                loadPatients();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Ошибка при удалении пациента: " + e.getMessage(),
                    "Ошибка",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    public void refreshData() {
        loadPatients();
    }
}
