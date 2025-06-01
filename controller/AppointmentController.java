package controller;

import data.repository.AppointmentRepository;
import model.Appointment;

import java.util.List;
import java.util.Date;
import java.util.Calendar;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

public class AppointmentController {

    private final AppointmentRepository appointmentRepository;

    public AppointmentController() {
        this.appointmentRepository = new AppointmentRepository();
    }

    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    public Appointment getAppointmentById(int id) {
        return appointmentRepository.findById(id);
    }

    public void saveAppointment(Appointment appointment) {
        appointmentRepository.save(appointment);
    }

    public void updateAppointment(Appointment appointment) {
        appointmentRepository.update(appointment);
    }

    public void deleteAppointment(int id) {
        appointmentRepository.delete(id);
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
        return appointmentRepository.getAppointmentsByDoctorAndPeriod(
            doctorId, 
            new java.sql.Date(startDate.getTime()),
            new java.sql.Date(endDate.getTime())
        );
    }

    public void updateAppointments(List<Appointment> appointments) {
        for (Appointment appointment : appointments) {
            appointmentRepository.update(appointment);
        }
    }
}
