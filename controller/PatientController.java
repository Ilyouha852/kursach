package controller;

import model.entities.Patient;
import util.ErrorHandler;
import util.exceptions.DataAccessException;
import util.exceptions.ValidationException;
import data.repository.PatientRepository;
import java.util.List;

public class PatientController implements BaseController<Patient> {
    private final PatientRepository patientRepository;

    public PatientController(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    @Override
    public List<Patient> getAll() {
        return getAllPatients();
    }

    @Override
    public Patient getById(int id) {
        return getPatientById(id);
    }

    @Override
    public void save(Patient patient) {
        savePatient(patient);
    }

    @Override
    public void update(Patient patient) {
        updatePatient(patient);
    }

    @Override
    public void delete(int id) {
        deletePatient(id);
    }

    public List<Patient> getAllPatients() {
        try {
            return patientRepository.findAll();
        } catch (Exception e) {
            throw new DataAccessException("Ошибка при получении списка пациентов", e);
        }
    }

    public Patient getPatientById(int id) {
        try {
            return patientRepository.findById(id);
        } catch (Exception e) {
            throw new DataAccessException("Ошибка при получении пациента с ID: " + id, e);
        }
    }

    public void savePatient(Patient patient) {
        try {
            validatePatient(patient);
            patientRepository.save(patient);
            ErrorHandler.logInfo("Пациент успешно сохранен: " + patient.getLastName() + " " + patient.getFirstName());
        } catch (ValidationException e) {
            ErrorHandler.handleError(e);
            throw e;
        } catch (Exception e) {
            ErrorHandler.handleError(new DataAccessException("Ошибка при сохранении пациента", e));
            throw new DataAccessException("Ошибка при сохранении пациента", e);
        }
    }

    public void updatePatient(Patient patient) {
        try {
            validatePatient(patient);
            patientRepository.update(patient);
            ErrorHandler.logInfo("Пациент успешно обновлен: " + patient.getLastName() + " " + patient.getFirstName());
        } catch (ValidationException e) {
            ErrorHandler.handleError(e);
            throw e;
        } catch (Exception e) {
            ErrorHandler.handleError(new DataAccessException("Ошибка при обновлении пациента", e));
            throw new DataAccessException("Ошибка при обновлении пациента", e);
        }
    }

    public void deletePatient(int id) {
        try {
            patientRepository.delete(id);
            ErrorHandler.logInfo("Пациент успешно удален с ID: " + id);
        } catch (Exception e) {
            ErrorHandler.handleError(new DataAccessException("Ошибка при удалении пациента", e));
            throw new DataAccessException("Ошибка при удалении пациента", e);
        }
    }

    private void validatePatient(Patient patient) {
        if (patient.getLastName() == null || patient.getLastName().trim().isEmpty()) {
            throw new ValidationException("Фамилия пациента не может быть пустой");
        }
        if (patient.getFirstName() == null || patient.getFirstName().trim().isEmpty()) {
            throw new ValidationException("Имя пациента не может быть пустым");
        }
        if (patient.getPhoneNumber() != null && !patient.getPhoneNumber().matches("\\+?[0-9]{10,15}")) {
            throw new ValidationException("Неверный формат номера телефона");
        }
    }

    public Patient getPatientByPhone(String phone) {
        return patientRepository.getPatientByPhone(phone);
    }
}
