package ui.components;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ReportDisplayPanel extends JPanel {
    private JTextArea reportArea;
    private JButton saveToFileButton;

    public ReportDisplayPanel() {
        setLayout(new BorderLayout());
        initializeComponents();
    }

    private void initializeComponents() {
        reportArea = new JTextArea("Здесь будут генерироваться отчеты.");
        JScrollPane scrollPane = new JScrollPane(reportArea);
        add(scrollPane, BorderLayout.CENTER);

        saveToFileButton = new JButton("Сохранить в файл");
        saveToFileButton.setEnabled(false);
        saveToFileButton.addActionListener(e -> saveReportToFile());

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveToFileButton);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    public void setReportContent(String content) {
        reportArea.setText(content);
        saveToFileButton.setEnabled(true);
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