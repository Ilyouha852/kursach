package ui.components;

import model.entities.MedicalRecord;
import model.tabelmodels.MedicalRecordTableModel;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class MedicalRecordTablePanel extends JPanel {
    private JTable medicalRecordTable;
    private MedicalRecordTableModel tableModel;
    private TableRowSorter<MedicalRecordTableModel> sorter;

    public MedicalRecordTablePanel() {
        setLayout(new BorderLayout());
        initializeComponents();
    }

    private void initializeComponents() {
        tableModel = new MedicalRecordTableModel();
        medicalRecordTable = new JTable(tableModel);
        
        // Скрываем столбец ID
        medicalRecordTable.getColumnModel().getColumn(0).setMinWidth(0);
        medicalRecordTable.getColumnModel().getColumn(0).setMaxWidth(0);
        medicalRecordTable.getColumnModel().getColumn(0).setWidth(0);
        medicalRecordTable.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        sorter = new TableRowSorter<>(tableModel);
        medicalRecordTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(medicalRecordTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateTable(List<MedicalRecord> medicalRecords) {
        tableModel.setData(medicalRecords);
    }

    public JTable getMedicalRecordTable() {
        return medicalRecordTable;
    }

    public MedicalRecordTableModel getTableModel() {
        return tableModel;
    }

    public TableRowSorter<MedicalRecordTableModel> getSorter() {
        return sorter;
    }

    public MedicalRecord getSelectedMedicalRecord() {
        int selectedRow = medicalRecordTable.getSelectedRow();
        if (selectedRow == -1) {
            return null;
        }
        return tableModel.getItemAt(medicalRecordTable.convertRowIndexToModel(selectedRow));
    }
} 