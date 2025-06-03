package ui.dialogs;

import util.ValidationUtils;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

import model.entities.Patient;
import model.entities.PatientDisease;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PatientEditDialog extends JDialog {
    private JTextField lastNameField;
    private JTextField firstNameField;
    private JTextField middleNameField;
    private JFormattedTextField birthDateField;
    private JFormattedTextField phoneField;
    private JComboBox<String> diseaseComboBox;
    private boolean confirmed = false;

    public PatientEditDialog(Window owner, String title, Patient patient) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        initializeComponents(patient);
        pack();
        setLocationRelativeTo(owner);
    }

    private void initializeComponents(Patient patient) {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lastNameLabel = new JLabel("Фамилия:");
        lastNameField = new JTextField(patient != null ? patient.getLastName() : "");
        JLabel firstNameLabel = new JLabel("Имя:");
        firstNameField = new JTextField(patient != null ? patient.getFirstName() : "");
        JLabel middleNameLabel = new JLabel("Отчество:");
        middleNameField = new JTextField(patient != null ? (patient.getMiddleName() != null ? patient.getMiddleName() : "") : "");
        JLabel birthDateLabel = new JLabel("Дата рождения:");
        birthDateField = createDateField();
        if (patient != null && patient.getDateOfBirth() != null) {
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            birthDateField.setText(df.format(patient.getDateOfBirth()));
        }
        JLabel phoneLabel = new JLabel("Телефон:");
        phoneField = createPhoneField();
        if (patient != null) {
            phoneField.setText(patient.getPhoneNumber());
        }
        JLabel diseaseLabel = new JLabel("Заболевание:");
        diseaseComboBox = new JComboBox<>(getDiseaseOptions());
        if (patient != null && patient.getDisease() != null) {
            diseaseComboBox.setSelectedItem(patient.getDisease());
        }

        mainPanel.add(lastNameLabel);
        mainPanel.add(lastNameField);
        mainPanel.add(firstNameLabel);
        mainPanel.add(firstNameField);
        mainPanel.add(middleNameLabel);
        mainPanel.add(middleNameField);
        mainPanel.add(birthDateLabel);
        mainPanel.add(birthDateField);
        mainPanel.add(phoneLabel);
        mainPanel.add(phoneField);
        mainPanel.add(diseaseLabel);
        mainPanel.add(diseaseComboBox);

        JPanel buttonPanel = new JPanel();
        JButton confirmButton = new JButton("Подтвердить");
        JButton cancelButton = new JButton("Отмена");
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        add(mainPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        confirmButton.addActionListener(e -> {
            if (validateFields()) {
                confirmed = true;
                dispose();
            }
        });
        cancelButton.addActionListener(e -> dispose());
    }

    private JFormattedTextField createDateField() {
        try {
            MaskFormatter maskFormatter = new MaskFormatter("##.##.####");
            maskFormatter.setPlaceholderCharacter('_');
            return new JFormattedTextField(maskFormatter);
        } catch (ParseException e) {
            return new JFormattedTextField();
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
            return new JFormattedTextField();
        }
    }

    private String[] getDiseaseOptions() {
        return new String[] {
            "Кариес", "Пульпит", "Пародонтит", "Гингивит", "Абсцесс зуба", "Ретинированный зуб", "Неправильный прикус", "Травма зуба", "Гипоплазия эмали", "Повышенная чувствительность"
        };
    }

    private boolean validateFields() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String middleName = middleNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String birthDate = birthDateField.getText().trim();
        if (!ValidationUtils.isValidName(firstName)) {
            JOptionPane.showMessageDialog(this, "Имя должно содержать только русские буквы и быть не длиннее 50 символов", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!ValidationUtils.isValidName(lastName)) {
            JOptionPane.showMessageDialog(this, "Фамилия должна содержать только русские буквы и быть не длиннее 50 символов", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!middleName.isEmpty() && !ValidationUtils.isValidName(middleName)) {
            JOptionPane.showMessageDialog(this, "Отчество должно содержать только русские буквы и быть не длиннее 50 символов", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!ValidationUtils.isValidPhone(phone)) {
            JOptionPane.showMessageDialog(this, "Неверный формат номера телефона. Используйте формат: +7 (XXX) XXX-XX-XX или 8 (XXX) XXX-XX-XX", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        if (!isValidDate(birthDate)) {
            JOptionPane.showMessageDialog(this, "Неверный формат даты рождения. Используйте формат: ДД.ММ.ГГГГ", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    private boolean isValidDate(String dateStr) {
        try {
            SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
            df.setLenient(false);
            df.parse(dateStr);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public boolean isConfirmed() { return confirmed; }
    public String getLastName() { return lastNameField.getText().trim(); }
    public String getFirstName() { return firstNameField.getText().trim(); }
    public String getMiddleName() { return middleNameField.getText().trim(); }
    public String getPhone() { return phoneField.getText().trim(); }
    public String getBirthDate() { return birthDateField.getText().trim(); }
    public String getDisease() { return (String) diseaseComboBox.getSelectedItem(); }
    public Date getBirthDateAsDate() throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        df.setLenient(false);
        return df.parse(getBirthDate());
    }
} 