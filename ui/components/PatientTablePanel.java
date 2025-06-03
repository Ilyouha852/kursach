package ui.components;

import model.entities.Patient;
import model.tabelmodels.PatientTableModel;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.util.List;

public class PatientTablePanel extends JPanel {
    private JTable patientTable;
    private PatientTableModel tableModel;
    private TableRowSorter<PatientTableModel> sorter;

    public PatientTablePanel() {
        setLayout(new BorderLayout());
        initializeTable();
    }

    private void initializeTable() {
        tableModel = new PatientTableModel();
        patientTable = new JTable(tableModel);
        
        // Скрываем столбец ID
        patientTable.getColumnModel().getColumn(0).setMinWidth(0);
        patientTable.getColumnModel().getColumn(0).setMaxWidth(0);
        patientTable.getColumnModel().getColumn(0).setWidth(0);
        patientTable.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        sorter = new TableRowSorter<>(tableModel);
        patientTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(patientTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateTable(List<Patient> patients) {
        tableModel.setData(patients);
    }

    public Patient getSelectedPatient() {
        int selectedRow = patientTable.getSelectedRow();
        if (selectedRow == -1) {
            return null;
        }
        return tableModel.getItemAt(patientTable.convertRowIndexToModel(selectedRow));
    }

    public JTable getTable() {
        return patientTable;
    }

    public PatientTableModel getTableModel() {
        return tableModel;
    }

    public TableRowSorter<PatientTableModel> getSorter() {
        return sorter;
    }
} 