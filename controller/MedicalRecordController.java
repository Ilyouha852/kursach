package controller;

import data.repository.MedicalRecordRepository;
import model.entities.MedicalRecord;

import java.util.List;

public class MedicalRecordController {

    private final MedicalRecordRepository medicalRecordRepository;

    public MedicalRecordController() {
        this.medicalRecordRepository = new MedicalRecordRepository();
    }

    public List<MedicalRecord> getAllMedicalRecords() {
        return medicalRecordRepository.findAll();
    }

    public MedicalRecord getMedicalRecordById(int id) {
        return medicalRecordRepository.findById(id);
    }

    public void saveMedicalRecord(MedicalRecord medicalRecord) {
        medicalRecordRepository.save(medicalRecord);
    }

    public void updateMedicalRecord(MedicalRecord medicalRecord) {
        medicalRecordRepository.update(medicalRecord);
    }

    public void deleteMedicalRecord(int id) {
        medicalRecordRepository.delete(id);
    }
}
