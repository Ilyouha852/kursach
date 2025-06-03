package ui.dialogs;

import util.ValidationUtils;
import javax.swing.*;
import javax.swing.text.MaskFormatter;

import model.entities.DentalSpecialty;
import model.entities.Doctor;

import java.awt.*;
import java.text.ParseException;

public class DoctorEditDialog extends JDialog {
    private JTextField lastNameField;
    private JTextField firstNameField;
    private JTextField middleNameField;
    private JComboBox<String> specialtyComboBox;
    private JFormattedTextField phoneField;
    private JTextField emailField;
    private boolean confirmed = false;

    public DoctorEditDialog(Window owner, String title, Doctor doctor) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        initializeComponents(doctor);
        pack();
        setLocationRelativeTo(owner);
    }

    private void initializeComponents(Doctor doctor) {
        setLayout(new BorderLayout());
        JPanel mainInfoPanel = new JPanel(new GridLayout(6, 2, 5, 5));
        mainInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lastNameLabel = new JLabel("Фамилия:");
        lastNameField = new JTextField(doctor != null ? doctor.getLastName() : "");
        JLabel firstNameLabel = new JLabel("Имя:");
        firstNameField = new JTextField(doctor != null ? doctor.getFirstName() : "");
        JLabel middleNameLabel = new JLabel("Отчество:");
        middleNameField = new JTextField(doctor != null ? (doctor.getMiddleName() != null ? doctor.getMiddleName() : "") : "");
        JLabel specialtyLabel = new JLabel("Специализация:");
        specialtyComboBox = new JComboBox<>(new String[] {
            DentalSpecialty.THERAPIST,
            DentalSpecialty.SURGEON,
            DentalSpecialty.ORTHODONTIST,
            DentalSpecialty.ENDODONTIST,
            DentalSpecialty.PERIODONTIST,
            DentalSpecialty.PROSTHODONTIST,
            DentalSpecialty.PEDIATRIC_DENTIST
        });
        if (doctor != null) {
            specialtyComboBox.setSelectedItem(doctor.getSpecialization());
        } else {
            specialtyComboBox.setSelectedItem(DentalSpecialty.THERAPIST);
        }
        JLabel phoneLabel = new JLabel("Телефон:");
        phoneField = createPhoneField();
        if (doctor != null) {
            phoneField.setText(doctor.getPhoneNumber());
        }
        JLabel emailLabel = new JLabel("Email:");
        emailField = new JTextField(doctor != null ? doctor.getEmail() : "");

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

        JPanel buttonPanel = new JPanel();
        JButton confirmButton = new JButton("Подтвердить");
        JButton cancelButton = new JButton("Отмена");
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        add(mainInfoPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        confirmButton.addActionListener(e -> {
            if (validateFields()) {
                confirmed = true;
                dispose();
            }
        });
        cancelButton.addActionListener(e -> dispose());
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

    private boolean validateFields() {
        String firstName = firstNameField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String middleName = middleNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();

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
        if (!ValidationUtils.isValidEmail(email)) {
            JOptionPane.showMessageDialog(this, "Неверный формат email адреса", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public String getLastName() {
        return lastNameField.getText().trim();
    }

    public String getFirstName() {
        return firstNameField.getText().trim();
    }

    public String getMiddleName() {
        return middleNameField.getText().trim();
    }

    public String getSpecialization() {
        return (String) specialtyComboBox.getSelectedItem();
    }

    public String getPhone() {
        return phoneField.getText().trim();
    }

    public String getEmail() {
        return emailField.getText().trim();
    }
} 