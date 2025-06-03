package model.entities;

import java.util.Date;

public class Appointment {

    private int id;
    private Date appointmentDateTime;
    private String procedureType;
    private String status;
    private int patientId;
    private int doctorId;

    public Appointment() {}

    public Appointment(Date appointmentDateTime, String procedureType, String status, int patientId, int doctorId) {
        this.appointmentDateTime = appointmentDateTime;
        this.procedureType = procedureType;
        this.status = status;
        this.patientId = patientId;
        this.doctorId = doctorId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getAppointmentDateTime() {
        return appointmentDateTime;
    }

    public void setAppointmentDateTime(Date appointmentDateTime) {
        this.appointmentDateTime = appointmentDateTime;
    }

    public String getProcedureType() {
        return procedureType;
    }

    public void setProcedureType(String procedureType) {
        this.procedureType = procedureType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPatientId() {
        return patientId;
    }

    public void setPatientId(int patientId) {
        this.patientId = patientId;
    }

    public int getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(int doctorId) {
        this.doctorId = doctorId;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "id=" + id +
                ", appointmentDateTime=" + appointmentDateTime +
                ", procedureType='" + procedureType + '\'' +
                ", status='" + status + '\'' +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                '}';
    }
}
