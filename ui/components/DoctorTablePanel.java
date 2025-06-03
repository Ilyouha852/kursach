package ui.components;

import model.entities.Appointment;
import model.entities.Doctor;
import model.tabelmodels.DoctorTableModel;
import controller.DoctorController;
import controller.AppointmentController;
import ui.MainFrame;
import ui.dialogs.DoctorEditDialog;
import ui.dialogs.DoctorRescheduleDialog;

import javax.swing.*;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Date;

public class DoctorTablePanel extends JPanel {
    private JTable doctorTable;
    private DoctorTableModel tableModel;
    private TableRowSorter<DoctorTableModel> sorter;
    private JPopupMenu popupMenu;
    private JMenuItem editMenuItem;
    private JMenuItem deleteMenuItem;
    private JMenuItem showAppointmentsMenuItem;
    private JMenuItem rescheduleMenuItem;
    private final DoctorController doctorController;
    private final AppointmentController appointmentController;

    public DoctorTablePanel(DoctorController doctorController, AppointmentController appointmentController) {
        this.doctorController = doctorController;
        this.appointmentController = appointmentController;
        setLayout(new BorderLayout());
        initializeTable();
        initializePopupMenu();
    }

    private void initializeTable() {
        tableModel = new DoctorTableModel();
        doctorTable = new JTable(tableModel);
        
        // Скрываем столбец ID
        doctorTable.getColumnModel().getColumn(0).setMinWidth(0);
        doctorTable.getColumnModel().getColumn(0).setMaxWidth(0);
        doctorTable.getColumnModel().getColumn(0).setWidth(0);
        doctorTable.getColumnModel().getColumn(0).setPreferredWidth(0);
        
        sorter = new TableRowSorter<>(tableModel);
        doctorTable.setRowSorter(sorter);

        JScrollPane scrollPane = new JScrollPane(doctorTable);
        add(scrollPane, BorderLayout.CENTER);
    }

    private void initializePopupMenu() {
        popupMenu = new JPopupMenu();
        editMenuItem = new JMenuItem("Редактировать");
        deleteMenuItem = new JMenuItem("Удалить");
        showAppointmentsMenuItem = new JMenuItem("Показать записи приемов");
        rescheduleMenuItem = new JMenuItem("Перенос записей");
        
        popupMenu.add(editMenuItem);
        popupMenu.add(deleteMenuItem);
        popupMenu.addSeparator();
        popupMenu.add(showAppointmentsMenuItem);
        popupMenu.add(rescheduleMenuItem);

        // Добавляем обработчики событий для пунктов меню
        editMenuItem.addActionListener(e -> {
            Doctor selectedDoctor = getSelectedDoctor();
            if (selectedDoctor != null) {
                DoctorEditDialog dialog = new DoctorEditDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Редактирование врача",
                    selectedDoctor
                );
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    selectedDoctor.setLastName(dialog.getLastName());
                    selectedDoctor.setFirstName(dialog.getFirstName());
                    selectedDoctor.setMiddleName(dialog.getMiddleName());
                    selectedDoctor.setSpecialization(dialog.getSpecialization());
                    selectedDoctor.setPhoneNumber(dialog.getPhone());
                    selectedDoctor.setEmail(dialog.getEmail());
                    doctorController.updateDoctor(selectedDoctor);
                    loadData();
                }
            }
        });

        deleteMenuItem.addActionListener(e -> {
            Doctor selectedDoctor = getSelectedDoctor();
            if (selectedDoctor != null) {
                int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Вы уверены, что хотите удалить этого врача?",
                    "Подтверждение",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    doctorController.deleteDoctor(selectedDoctor.getId());
                    loadData();
                }
            }
        });

        showAppointmentsMenuItem.addActionListener(e -> {
            Doctor selectedDoctor = getSelectedDoctor();
            if (selectedDoctor != null) {
                MainFrame mainFrame = (MainFrame) SwingUtilities.getWindowAncestor(this);
                mainFrame.showAppointmentsWithFilter(0, selectedDoctor.getId());
            }
        });

        rescheduleMenuItem.addActionListener(e -> {
            Doctor selectedDoctor = getSelectedDoctor();
            if (selectedDoctor != null) {
                List<Appointment> appointments = appointmentController.getAppointmentsByDoctor(selectedDoctor.getId());
                if (appointments.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "У выбранного врача нет записей", "Информация", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }

                Date minDate = appointments.stream().map(Appointment::getAppointmentDateTime).min(Date::compareTo).get();
                Date maxDate = appointments.stream().map(Appointment::getAppointmentDateTime).max(Date::compareTo).get();

                DoctorRescheduleDialog dialog = new DoctorRescheduleDialog(
                    SwingUtilities.getWindowAncestor(this),
                    "Перенос записей",
                    minDate,
                    maxDate
                );
                dialog.setVisible(true);
                if (dialog.isConfirmed()) {
                    try {
                        Date startDate = dialog.getStartDate();
                        Date endDate = dialog.getEndDate();
                        int offset = dialog.getOffset();
                        appointmentController.rescheduleAppointments(selectedDoctor.getId(), startDate, endDate, offset);
                        JOptionPane.showMessageDialog(this, "Записи успешно перенесены", "Успех", JOptionPane.INFORMATION_MESSAGE);
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this, "Ошибка при переносе записей: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        doctorTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (e.isPopupTrigger()) {
                    int row = doctorTable.rowAtPoint(e.getPoint());
                    if (row >= 0) {
                        doctorTable.setRowSelectionInterval(row, row);
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            }
        });
    }

    public void updateTable(List<Doctor> doctors) {
        tableModel.setData(doctors);
    }

    public Doctor getSelectedDoctor() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow == -1) {
            return null;
        }
        return tableModel.getItemAt(doctorTable.convertRowIndexToModel(selectedRow));
    }

    public JTable getTable() {
        return doctorTable;
    }

    public DoctorTableModel getTableModel() {
        return tableModel;
    }

    public TableRowSorter<DoctorTableModel> getSorter() {
        return sorter;
    }

    public JMenuItem getEditMenuItem() {
        return editMenuItem;
    }

    public JMenuItem getDeleteMenuItem() {
        return deleteMenuItem;
    }

    public JMenuItem getShowAppointmentsMenuItem() {
        return showAppointmentsMenuItem;
    }

    public JMenuItem getRescheduleMenuItem() {
        return rescheduleMenuItem;
    }

    private void loadData() {
        List<Doctor> doctors = doctorController.getAllDoctors();
        tableModel.setData(doctors);
    }
} 