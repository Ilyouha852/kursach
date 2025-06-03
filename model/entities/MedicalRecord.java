package model.entities;

import java.util.Date;

public class MedicalRecord {

    private int id;
    private Date recordDate;
    private String diagnosis;
    private String proceduresPerformed;
    private String notes;
    private int patientId;
    private int doctorId;

    public MedicalRecord() {}

    public MedicalRecord(Date recordDate, String diagnosis, String proceduresPerformed, String notes, int patientId, int doctorId) {
        this.recordDate = recordDate;
        this.diagnosis = diagnosis;
        this.proceduresPerformed = proceduresPerformed;
        this.notes = notes;
        this.patientId = patientId;
        this.doctorId = doctorId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getRecordDate() {
        return recordDate;
    }

    public void setRecordDate(Date recordDate) {
        this.recordDate = recordDate;
    }

    public String getDiagnosis() {
        return diagnosis;
    }

    public void setDiagnosis(String diagnosis) {
        this.diagnosis = diagnosis;
    }

    public String getProceduresPerformed() {
        return proceduresPerformed;
    }

    public void setProceduresPerformed(String proceduresPerformed) {
        this.proceduresPerformed = proceduresPerformed;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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
        return "MedicalRecord{" +
                "id=" + id +
                ", recordDate=" + recordDate +
                ", diagnosis='" + diagnosis + '\'' +
                ", proceduresPerformed='" + proceduresPerformed + '\'' +
                ", notes='" + notes + '\'' +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                '}';
    }
}
