package util;

import controller.AppointmentController;
import controller.DoctorController;
import controller.PatientController;
import model.Appointment;
import model.Doctor;
import model.Patient;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReportGenerator {

    private final PatientController patientController;
    private final DoctorController doctorController;
    private final AppointmentController appointmentController;

    public ReportGenerator(PatientController patientController, DoctorController doctorController, AppointmentController appointmentController) {
        this.patientController = patientController;
        this.doctorController = doctorController;
        this.appointmentController = appointmentController;
    }

    public String generateClinicReport(Date startDate, Date endDate, Doctor doctor, Patient patient) {
        StringBuilder reportContent = new StringBuilder();
        reportContent.append("Отчет о стоматологической клинике\n");
        reportContent.append("==================================\n\n");

        // Добавляем информацию о фильтрах
        if (startDate != null || endDate != null || doctor != null || patient != null) {
            reportContent.append("Параметры отчета:\n");
            if (startDate != null || endDate != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
                reportContent.append("Период: ");
                if (startDate != null) {
                    reportContent.append(dateFormat.format(startDate));
                }
                reportContent.append(" - ");
                if (endDate != null) {
                    reportContent.append(dateFormat.format(endDate));
                }
                reportContent.append("\n");
            }
            if (doctor != null) {
                reportContent.append("Врач: ").append(doctor.getLastName())
                    .append(" ").append(doctor.getFirstName())
                    .append(" - ").append(doctor.getSpecialization()).append("\n");
            }
            if (patient != null) {
                reportContent.append("Пациент: ").append(patient.toString()).append("\n");
            }
            reportContent.append("\n");
        }

        // Получаем все записи для подсчета статистики
        List<Appointment> allAppointments = appointmentController.getAllAppointments();
        if (startDate != null || endDate != null || doctor != null || patient != null) {
            allAppointments = allAppointments.stream()
                .filter(a -> {
                    if (startDate != null && a.getAppointmentDateTime().before(startDate)) {
                        return false;
                    }
                    if (endDate != null && a.getAppointmentDateTime().after(endDate)) {
                        return false;
                    }
                    if (doctor != null && a.getDoctorId() != doctor.getId()) {
                        return false;
                    }
                    if (patient != null && a.getPatientId() != patient.getId()) {
                        return false;
                    }
                    return true;
                })
                .collect(Collectors.toList());
        }

        // Patients report
        reportContent.append("Список пациентов:\n");
        reportContent.append("----------------\n");
        List<Patient> patients = patientController.getAllPatients();
        if (patient != null) {
            patients = patients.stream()
                .filter(p -> p.getId() == patient.getId())
                .collect(Collectors.toList());
        }
        for (Patient p : patients) {
            // Подсчитываем количество приемов для пациента
            long appointmentCount = allAppointments.stream()
                .filter(a -> a.getPatientId() == p.getId())
                .count();

            reportContent.append(String.format("ID: %d\n", p.getId()));
            reportContent.append(String.format("ФИО: %s %s %s\n", 
                p.getLastName(), p.getFirstName(), 
                p.getMiddleName() != null ? p.getMiddleName() : ""));
            reportContent.append(String.format("Дата рождения: %s\n", 
                new SimpleDateFormat("dd.MM.yyyy").format(p.getDateOfBirth())));
            reportContent.append(String.format("Телефон: %s\n", p.getPhoneNumber()));
            reportContent.append(String.format("Количество приемов: %d\n", appointmentCount));
            reportContent.append("----------------\n");
        }
        reportContent.append("\n");

        // Doctors report
        reportContent.append("Список врачей:\n");
        reportContent.append("----------------\n");
        List<Doctor> doctors = doctorController.getAllDoctors();
        if (doctor != null) {
            doctors = doctors.stream()
                .filter(d -> d.getId() == doctor.getId())
                .collect(Collectors.toList());
        }
        for (Doctor d : doctors) {
            // Подсчитываем количество приемов для врача
            long appointmentCount = allAppointments.stream()
                .filter(a -> a.getDoctorId() == d.getId())
                .count();

            reportContent.append(String.format("ID: %d\n", d.getId()));
            reportContent.append(String.format("ФИО: %s %s %s\n", 
                d.getLastName(), d.getFirstName(), 
                d.getMiddleName() != null ? d.getMiddleName() : ""));
            reportContent.append(String.format("Специализация: %s\n", d.getSpecialization()));
            reportContent.append(String.format("Телефон: %s\n", d.getPhoneNumber()));
            reportContent.append(String.format("Количество приемов: %d\n", appointmentCount));
            reportContent.append("----------------\n");
        }
        reportContent.append("\n");

        // Appointments report
        reportContent.append("Список записей на прием:\n");
        reportContent.append("----------------\n");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (Appointment appointment : allAppointments) {
            // Получаем информацию о пациенте и враче
            Patient appointmentPatient = patientController.getPatientById(appointment.getPatientId());
            Doctor appointmentDoctor = doctorController.getDoctorById(appointment.getDoctorId());

            reportContent.append(String.format("ID: %d\n", appointment.getId()));
            reportContent.append(String.format("Дата и время: %s\n", 
                dateFormat.format(appointment.getAppointmentDateTime())));
            reportContent.append(String.format("Пациент: %s %s\n", 
                appointmentPatient.getLastName(), appointmentPatient.getFirstName()));
            reportContent.append(String.format("Врач: %s %s (%s)\n", 
                appointmentDoctor.getLastName(), appointmentDoctor.getFirstName(),
                appointmentDoctor.getSpecialization()));
            reportContent.append(String.format("Статус: %s\n", appointment.getStatus()));
            reportContent.append("----------------\n");
        }
        reportContent.append("\n");

        // Добавляем общую статистику
        reportContent.append("Общая статистика:\n");
        reportContent.append("----------------\n");
        reportContent.append(String.format("Всего пациентов: %d\n", patients.size()));
        reportContent.append(String.format("Всего врачей: %d\n", doctors.size()));
        reportContent.append(String.format("Всего приемов: %d\n", allAppointments.size()));
        
        // Статистика по статусам приемов
        Map<String, Long> statusStats = allAppointments.stream()
            .collect(Collectors.groupingBy(Appointment::getStatus, Collectors.counting()));
        reportContent.append("\nСтатистика по статусам приемов:\n");
        statusStats.forEach((status, count) -> 
            reportContent.append(String.format("%s: %d\n", status, count)));

        return reportContent.toString();
    }
}
