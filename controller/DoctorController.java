package controller;

import model.entities.Doctor;
import util.ErrorHandler;
import util.exceptions.DataAccessException;
import util.exceptions.ValidationException;
import data.repository.DoctorRepository;
import java.util.List;

public class DoctorController implements BaseController<Doctor> {
    private final DoctorRepository doctorRepository;

    public DoctorController(DoctorRepository doctorRepository) {
        this.doctorRepository = doctorRepository;
    }

    @Override
    public List<Doctor> getAll() {
        return getAllDoctors();
    }

    @Override
    public Doctor getById(int id) {
        return getDoctorById(id);
    }

    @Override
    public void save(Doctor doctor) {
        saveDoctor(doctor);
    }

    @Override
    public void update(Doctor doctor) {
        updateDoctor(doctor);
    }

    @Override
    public void delete(int id) {
        deleteDoctor(id);
    }

    public List<Doctor> getAllDoctors() {
        try {
            return doctorRepository.findAll();
        } catch (Exception e) {
            throw new DataAccessException("Ошибка при получении списка врачей", e);
        }
    }

    public Doctor getDoctorById(int id) {
        try {
            return doctorRepository.findById(id);
        } catch (Exception e) {
            throw new DataAccessException("Ошибка при получении врача с ID: " + id, e);
        }
    }

    public void saveDoctor(Doctor doctor) {
        try {
            validateDoctor(doctor);
            doctorRepository.save(doctor);
            ErrorHandler.logInfo("Врач успешно сохранен: " + doctor.getLastName() + " " + doctor.getFirstName());
        } catch (ValidationException e) {
            ErrorHandler.handleError(e);
            throw e;
        } catch (Exception e) {
            ErrorHandler.handleError(new DataAccessException("Ошибка при сохранении врача", e));
            throw new DataAccessException("Ошибка при сохранении врача", e);
        }
    }

    public void updateDoctor(Doctor doctor) {
        try {
            validateDoctor(doctor);
            doctorRepository.update(doctor);
            ErrorHandler.logInfo("Врач успешно обновлен: " + doctor.getLastName() + " " + doctor.getFirstName());
        } catch (ValidationException e) {
            ErrorHandler.handleError(e);
            throw e;
        } catch (Exception e) {
            ErrorHandler.handleError(new DataAccessException("Ошибка при обновлении врача", e));
            throw new DataAccessException("Ошибка при обновлении врача", e);
        }
    }

    public void deleteDoctor(int id) {
        try {
            doctorRepository.delete(id);
            ErrorHandler.logInfo("Врач успешно удален с ID: " + id);
        } catch (Exception e) {
            ErrorHandler.handleError(new DataAccessException("Ошибка при удалении врача", e));
            throw new DataAccessException("Ошибка при удалении врача", e);
        }
    }

    private void validateDoctor(Doctor doctor) {
        if (doctor.getLastName() == null || doctor.getLastName().trim().isEmpty()) {
            throw new ValidationException("Фамилия врача не может быть пустой");
        }
        if (doctor.getFirstName() == null || doctor.getFirstName().trim().isEmpty()) {
            throw new ValidationException("Имя врача не может быть пустым");
        }
        if (doctor.getSpecialization() == null || doctor.getSpecialization().trim().isEmpty()) {
            throw new ValidationException("Специализация врача не может быть пустой");
        }
        if (doctor.getPhoneNumber() != null && !doctor.getPhoneNumber().matches("\\+?[0-9]{10,15}")) {
            throw new ValidationException("Неверный формат номера телефона");
        }
        if (doctor.getEmail() != null && !doctor.getEmail().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new ValidationException("Неверный формат email");
        }
    }

    public Doctor getDoctorByEmail(String email) {
        return doctorRepository.findByEmail(email);
    }
}