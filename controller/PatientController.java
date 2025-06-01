package controller;

import data.repository.PatientRepository;
import model.Patient;

import java.util.List;

public class PatientController {

    private final PatientRepository patientRepository;

    public PatientController() {
        this.patientRepository = new PatientRepository();
    }

    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    public Patient getPatientById(int id) {
        return patientRepository.findById(id);
    }

    public void savePatient(Patient patient) {
        patientRepository.save(patient);
    }

    public void updatePatient(Patient patient) {
        patientRepository.update(patient);
    }

    public void deletePatient(int id) {
        patientRepository.delete(id);
    }
}
