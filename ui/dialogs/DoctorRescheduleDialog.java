package ui.dialogs;

import javax.swing.*;
import javax.swing.text.MaskFormatter;

import model.entities.Appointment;

import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DoctorRescheduleDialog extends JDialog {
    private JFormattedTextField startDateField;
    private JFormattedTextField endDateField;
    private JSpinner offsetSpinner;
    private boolean confirmed = false;

    public DoctorRescheduleDialog(Window owner, String title, Date minDate, Date maxDate) {
        super(owner, title, ModalityType.APPLICATION_MODAL);
        initializeComponents(minDate, maxDate);
        pack();
        setLocationRelativeTo(owner);
    }

    private void initializeComponents(Date minDate, Date maxDate) {
        setLayout(new BorderLayout());
        JPanel mainPanel = new JPanel(new GridLayout(3, 2, 5, 5));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel startDateLabel = new JLabel("Начало периода:");
        startDateField = createDateField();
        JLabel endDateLabel = new JLabel("Конец периода:");
        endDateField = createDateField();

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        if (minDate != null) {
            startDateField.setText(dateFormat.format(minDate));
        }
        if (maxDate != null) {
            endDateField.setText(dateFormat.format(maxDate));
        }

        JLabel offsetLabel = new JLabel("Смещение (дней):");
        offsetSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 365, 1));

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
            MaskFormatter maskFormatter = new MaskFormatter("####-##-##");
            maskFormatter.setPlaceholderCharacter('_');
            return new JFormattedTextField(maskFormatter);
        } catch (ParseException e) {
            return new JFormattedTextField();
        }
    }

    private boolean validateFields() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            Date startDate = dateFormat.parse(startDateField.getText());
            Date endDate = dateFormat.parse(endDateField.getText());
            if (endDate.before(startDate)) {
                JOptionPane.showMessageDialog(this, "Дата окончания не может быть раньше даты начала", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            return true;
        } catch (ParseException e) {
            JOptionPane.showMessageDialog(this, "Неверный формат даты. Используйте формат: ГГГГ-ММ-ДД", "Ошибка валидации", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public Date getStartDate() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        return dateFormat.parse(startDateField.getText());
    }

    public Date getEndDate() throws ParseException {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        return dateFormat.parse(endDateField.getText());
    }

    public int getOffset() {
        return (int) offsetSpinner.getValue();
    }
} 