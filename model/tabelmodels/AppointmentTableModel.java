package model.tabelmodels;

import java.text.SimpleDateFormat;
import java.util.List;

import model.entities.Appointment;
import model.entities.Doctor;
import model.entities.Patient;

public class AppointmentTableModel extends AbstractCustomTableModel<Appointment> {
    private static final String[] COLUMN_NAMES = {"ID", "Дата и время", "Пациент", "Врач", "Тип процедуры", "Статус"};
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm");
    private List<Patient> patients;
    private List<Doctor> doctors;

    public AppointmentTableModel() {
        super(COLUMN_NAMES);
    }

    public void setReferenceData(List<Patient> patients, List<Doctor> doctors) {
        this.patients = patients;
        this.doctors = doctors;
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        Appointment appointment = data.get(rowIndex);
        switch (columnIndex) {
            case 0: return appointment.getId();
            case 1: return appointment.getAppointmentDateTime() != null ? 
                   dateFormat.format(appointment.getAppointmentDateTime()) : "";
            case 2: return getPatientName(appointment.getPatientId());
            case 3: return getDoctorName(appointment.getDoctorId());
            case 4: return appointment.getProcedureType();
            case 5: return appointment.getStatus();
            default: return null;
        }
    }

    private String getPatientName(int patientId) {
        if (patients == null) return "Неизвестно";
        return patients.stream()
            .filter(p -> p.getId() == patientId)
            .findFirst()
            .map(p -> p.getLastName() + " " + p.getFirstName() + 
                     (p.getMiddleName() != null && !p.getMiddleName().isEmpty() ? " " + p.getMiddleName() : ""))
            .orElse("Неизвестно");
    }

    private String getDoctorName(int doctorId) {
        if (doctors == null) return "Неизвестно";
        return doctors.stream()
            .filter(d -> d.getId() == doctorId)
            .findFirst()
            .map(d -> d.getLastName() + " " + d.getFirstName() + 
                     (d.getMiddleName() != null && !d.getMiddleName().isEmpty() ? " " + d.getMiddleName() : ""))
            .orElse("Неизвестно");
    }

    public String getPatientTooltip(int patientId) {
        if (patients == null) return "Неизвестно";
        return patients.stream()
            .filter(p -> p.getId() == patientId)
            .findFirst()
            .map(p -> {
                StringBuilder sb = new StringBuilder("<html>");
                sb.append("ФИО: ").append(p.getLastName()).append(" ")
                  .append(p.getFirstName());
                if (p.getMiddleName() != null && !p.getMiddleName().isEmpty()) {
                    sb.append(" ").append(p.getMiddleName());
                }
                if (p.getPhoneNumber() != null && !p.getPhoneNumber().isEmpty()) {
                    sb.append("<br>Телефон: ").append(p.getPhoneNumber());
                }
                if (p.getDisease() != null && !p.getDisease().isEmpty()) {
                    sb.append("<br>Заболевание: ").append(p.getDisease());
                }
                sb.append("</html>");
                return sb.toString();
            })
            .orElse("Неизвестно");
    }

    public String getDoctorTooltip(int doctorId) {
        if (doctors == null) return "Неизвестно";
        return doctors.stream()
            .filter(d -> d.getId() == doctorId)
            .findFirst()
            .map(d -> {
                StringBuilder sb = new StringBuilder("<html>");
                sb.append("ФИО: ").append(d.getLastName()).append(" ")
                  .append(d.getFirstName());
                if (d.getMiddleName() != null && !d.getMiddleName().isEmpty()) {
                    sb.append(" ").append(d.getMiddleName());
                }
                if (d.getSpecialization() != null && !d.getSpecialization().isEmpty()) {
                    sb.append("<br>Специализация: ").append(d.getSpecialization());
                }
                if (d.getPhoneNumber() != null && !d.getPhoneNumber().isEmpty()) {
                    sb.append("<br>Телефон: ").append(d.getPhoneNumber());
                }
                if (d.getEmail() != null && !d.getEmail().isEmpty()) {
                    sb.append("<br>Email: ").append(d.getEmail());
                }
                sb.append("</html>");
                return sb.toString();
            })
            .orElse("Неизвестно");
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
} 