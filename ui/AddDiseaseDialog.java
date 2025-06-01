package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import model.DentalSpecialty;

public class AddDiseaseDialog extends JDialog {
    private JTextField diseaseNameField;
    private JComboBox<String> specializationCombo;
    private String diseaseName;
    private String specialization;

    public AddDiseaseDialog(Frame parent) {
        super(parent, "Добавить новое заболевание", true);
        setLayout(new BorderLayout());

        // Создаем панель с полями ввода
        JPanel inputPanel = new JPanel(new GridLayout(2, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel diseaseNameLabel = new JLabel("Название заболевания:");
        diseaseNameField = new JTextField(20);

        JLabel specializationLabel = new JLabel("Специальность врача:");
        specializationCombo = new JComboBox<>(new String[] {
            DentalSpecialty.THERAPIST,
            DentalSpecialty.SURGEON,
            DentalSpecialty.ORTHODONTIST,
            DentalSpecialty.ENDODONTIST,
            DentalSpecialty.PERIODONTIST,
            DentalSpecialty.PROSTHODONTIST,
            DentalSpecialty.PEDIATRIC_DENTIST
        });

        inputPanel.add(diseaseNameLabel);
        inputPanel.add(diseaseNameField);
        inputPanel.add(specializationLabel);
        inputPanel.add(specializationCombo);

        // Создаем панель с кнопками
        JPanel buttonPanel = new JPanel();
        JButton saveButton = new JButton("Сохранить");
        JButton cancelButton = new JButton("Отмена");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        // Добавляем обработчики событий
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (validateInput()) {
                    diseaseName = diseaseNameField.getText();
                    specialization = (String) specializationCombo.getSelectedItem();
                    dispose();
                }
            }
        });

        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                diseaseName = null;
                specialization = null;
                dispose();
            }
        });

        // Добавляем панели в диалог
        add(inputPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Настраиваем диалог
        pack();
        setLocationRelativeTo(parent);
        setResizable(false);
    }

    private boolean validateInput() {
        if (diseaseNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this,
                "Пожалуйста, введите название заболевания",
                "Ошибка",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
        return true;
    }

    public String getDiseaseName() {
        return diseaseName;
    }

    public String getSpecialization() {
        return specialization;
    }
} 