package controller;

import data.repository.AppointmentRepository;
import model.entities.Appointment;
import model.entities.AppointmentStatus;
import util.ErrorHandler;
import util.exceptions.DataAccessException;
import util.exceptions.ValidationException;

import java.util.List;
import java.util.Date;
import java.util.Calendar;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class AppointmentController implements BaseController<Appointment> {

    private final AppointmentRepository appointmentRepository;
    private final DoctorController doctorController;
    private final PatientController patientController;

    public AppointmentController(AppointmentRepository appointmentRepository,
                               DoctorController doctorController,
                               PatientController patientController) {
        this.appointmentRepository = appointmentRepository;
        this.doctorController = doctorController;
        this.patientController = patientController;
    }

    @Override
    public List<Appointment> getAll() {
        return getAllAppointments();
    }

    @Override
    public Appointment getById(int id) {
        return getAppointmentById(id);
    }

    @Override
    public void save(Appointment appointment) {
        saveAppointment(appointment);
    }

    @Override
    public void update(Appointment appointment) {
        updateAppointment(appointment);
    }

    @Override
    public void delete(int id) {
        deleteAppointment(id);
    }

    public List<Appointment> getAllAppointments() {
        try {
            return appointmentRepository.findAll();
        } catch (Exception e) {
            throw new DataAccessException("Ошибка при получении списка приемов", e);
        }
    }

    public List<Appointment> getAppointmentsByDoctor(int doctorId) {
        return appointmentRepository.findAll().stream()
            .filter(a -> a.getDoctorId() == doctorId)
            .collect(Collectors.toList());
    }

    public Appointment getAppointmentById(int id) {
        try {
            return appointmentRepository.findById(id);
        } catch (Exception e) {
            throw new DataAccessException("Ошибка при получении приема с ID: " + id, e);
        }
    }

    public void saveAppointment(Appointment appointment) {
        try {
            validateAppointment(appointment);
            appointmentRepository.save(appointment);
            ErrorHandler.logInfo("Прием успешно сохранен: " + appointment.getAppointmentDateTime());
        } catch (ValidationException e) {
            ErrorHandler.handleError(e);
            throw e;
        } catch (Exception e) {
            ErrorHandler.handleError(new DataAccessException("Ошибка при сохранении приема", e));
            throw new DataAccessException("Ошибка при сохранении приема", e);
        }
    }

    public void updateAppointment(Appointment appointment) {
        try {
            validateAppointment(appointment);
            appointmentRepository.update(appointment);
            ErrorHandler.logInfo("Прием успешно обновлен: " + appointment.getAppointmentDateTime());
        } catch (ValidationException e) {
            ErrorHandler.handleError(e);
            throw e;
        } catch (Exception e) {
            ErrorHandler.handleError(new DataAccessException("Ошибка при обновлении приема", e));
            throw new DataAccessException("Ошибка при обновлении приема", e);
        }
    }

    public void deleteAppointment(int id) {
        try {
            appointmentRepository.delete(id);
            ErrorHandler.logInfo("Прием успешно удален с ID: " + id);
        } catch (Exception e) {
            ErrorHandler.handleError(new DataAccessException("Ошибка при удалении приема", e));
            throw new DataAccessException("Ошибка при удалении приема", e);
        }
    }

    public List<Date> getAvailableTimeSlots(Date date, int doctorId) {
        List<Date> availableSlots = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        
        // Устанавливаем начальное время (8:00)
        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        
        // Получаем все записи врача на выбранную дату
        List<Appointment> doctorAppointments = appointmentRepository.findByDoctorIdAndDate(doctorId, new java.sql.Date(date.getTime()));
        Set<Date> busyTimes = new HashSet<>();
        for (Appointment appointment : doctorAppointments) {
            busyTimes.add(appointment.getAppointmentDateTime());
        }
        
        // Генерируем временные слоты с 8:00 до 17:00, кроме 13:00-14:00
        while (calendar.get(Calendar.HOUR_OF_DAY) < 17) {
            // Пропускаем обеденное время (13:00-14:00)
            if (calendar.get(Calendar.HOUR_OF_DAY) == 13) {
                calendar.add(Calendar.HOUR_OF_DAY, 1);
                continue;
            }
            
            Date timeSlot = calendar.getTime();
            if (!busyTimes.contains(timeSlot)) {
                availableSlots.add(timeSlot);
            }
            
            // Добавляем 30 минут
            calendar.add(Calendar.MINUTE, 30);
        }
        
        return availableSlots;
    }

    public List<Appointment> getAppointmentsByDoctorAndPeriod(int doctorId, Date startDate, Date endDate) {
        return appointmentRepository.findAll().stream()
            .filter(a -> a.getDoctorId() == doctorId)
            .filter(a -> !a.getAppointmentDateTime().before(startDate) && !a.getAppointmentDateTime().after(endDate))
            .collect(Collectors.toList());
    }

    public void updateAppointments(List<Appointment> appointments) {
        for (Appointment appointment : appointments) {
            appointmentRepository.update(appointment);
        }
    }

    public void rescheduleAppointments(int doctorId, Date startDate, Date endDate, int offset) {
        List<Appointment> appointments = getAppointmentsByDoctorAndPeriod(doctorId, startDate, endDate);
        Calendar calendar = Calendar.getInstance();
        for (Appointment appointment : appointments) {
            calendar.setTime(appointment.getAppointmentDateTime());
            calendar.add(Calendar.DAY_OF_MONTH, offset);
            appointment.setAppointmentDateTime(calendar.getTime());
        }
        updateAppointments(appointments);
    }

    public List<Appointment> getAppointmentsByPatient(int patientId) {
        // Реализация
        return null;
    }

    private void validateAppointment(Appointment appointment) {
        if (appointment.getAppointmentDateTime() == null) {
            throw new ValidationException("Дата и время приема не могут быть пустыми");
        }
        if (appointment.getPatientId() <= 0) {
            throw new ValidationException("Не выбран пациент");
        }
        if (appointment.getDoctorId() <= 0) {
            throw new ValidationException("Не выбран врач");
        }
        if (appointment.getProcedureType() == null || appointment.getProcedureType().trim().isEmpty()) {
            throw new ValidationException("Тип процедуры не может быть пустым");
        }
        if (appointment.getStatus() == null || appointment.getStatus().trim().isEmpty()) {
            throw new ValidationException("Статус приема не может быть пустым");
        }

        // Проверяем существование пациента и врача
        if (patientController.getById(appointment.getPatientId()) == null) {
            throw new ValidationException("Выбранный пациент не существует");
        }
        if (doctorController.getById(appointment.getDoctorId()) == null) {
            throw new ValidationException("Выбранный врач не существует");
        }
    }
}
