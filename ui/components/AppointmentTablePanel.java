package ui.components;

import model.entities.Appointment;
import model.entities.Doctor;
import model.entities.Patient;
import model.tabelmodels.AppointmentTableModel;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class AppointmentTablePanel extends JPanel {
    private JTable appointmentTable;
    private AppointmentTableModel tableModel;
    private TableRowSorter<AppointmentTableModel> sorter;

    public AppointmentTablePanel() {
        setLayout(new BorderLayout());
        initializeTable();
    }

    private void initializeTable() {
        tableModel = new AppointmentTableModel();
        appointmentTable = new JTable(tableModel);
        
        // Скрываем столбец ID
        appointmentTable.getColumnModel().getColumn(0).setMinWidth(0);
        appointmentTable.getColumnModel().getColumn(0).setMaxWidth(0);
        appointmentTable.getColumnModel().getColumn(0).setWidth(0);
        appointmentTable.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        sorter = new TableRowSorter<>(tableModel);
        appointmentTable.setRowSorter(sorter);

        // Добавляем всплывающие подсказки
        appointmentTable.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = appointmentTable.rowAtPoint(e.getPoint());
                int col = appointmentTable.columnAtPoint(e.getPoint());
                
                if (row >= 0 && col >= 0) {
                    Appointment appointment = tableModel.getItemAt(appointmentTable.convertRowIndexToModel(row));
                    if (appointment != null) {
                        String tooltip = null;
                        if (col == 2) { // Колонка пациента
                            tooltip = tableModel.getPatientTooltip(appointment.getPatientId());
                        } else if (col == 3) { // Колонка врача
                            tooltip = tableModel.getDoctorTooltip(appointment.getDoctorId());
                        }
                        appointmentTable.setToolTipText(tooltip);
                    }
                } else {
                    appointmentTable.setToolTipText(null);
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(appointmentTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    public void updateTable(List<Appointment> appointments, List<Patient> patients, List<Doctor> doctors) {
        tableModel.setReferenceData(patients, doctors);
        tableModel.setData(appointments);
    }

    public Appointment getSelectedAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            return null;
        }
        return tableModel.getItemAt(appointmentTable.convertRowIndexToModel(selectedRow));
    }

    public JTable getTable() {
        return appointmentTable;
    }

    public AppointmentTableModel getTableModel() {
        return tableModel;
    }

    public TableRowSorter<AppointmentTableModel> getSorter() {
        return sorter;
    }
} 