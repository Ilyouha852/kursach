package test;

import controller.AppointmentController;
import controller.DoctorController;
import controller.PatientController;
import model.Appointment;
import model.AppointmentStatus;
import model.Doctor;
import model.Patient;
import model.DentalSpecialty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class TestDataGenerator {

    private final PatientController patientController = new PatientController();
    private final DoctorController doctorController = new DoctorController();
    private final AppointmentController appointmentController = new AppointmentController();

    private final Random random = ThreadLocalRandom.current();

    public void generateAndInsertData() {
        System.out.println("Generating test data...");

        // Генерация пациентов
        List<Patient> patients = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            patients.add(generateRandomPatient());
        }
        patients.forEach(patientController::savePatient);
        System.out.println("Generated and saved 100 patients.");

        // Генерация врачей
        List<Doctor> doctors = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            doctors.add(generateRandomDoctor());
        }
        doctors.forEach(doctorController::saveDoctor);
        System.out.println("Generated and saved 15 doctors.");

        // Перезагружаем пациентов и врачей для получения их ID после сохранения
        List<Patient> savedPatients = patientController.getAllPatients();
        List<Doctor> savedDoctors = doctorController.getAllDoctors();

        // Генерация записей на прием
        List<Appointment> appointments = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            // Убедимся, что у нас достаточно пациентов и врачей для создания записи
            if (savedPatients.isEmpty() || savedDoctors.isEmpty()) {
                System.err.println("Недостаточно пациентов или врачей для создания записей.");
                break;
            }
            appointments.add(generateRandomAppointment(savedPatients, savedDoctors));
        }
        appointments.forEach(appointmentController::saveAppointment);
        System.out.println("Generated and saved 100 appointments.");

        System.out.println("Test data generation complete.");
    }

    // Вспомогательные методы для генерации данных
    private Patient generateRandomPatient() {
        Patient patient = new Patient();
        patient.setFirstName("Имя_" + random.nextInt(1000));
        patient.setLastName("Фамилия_" + random.nextInt(1000));
        patient.setMiddleName("Отчество_" + random.nextInt(1000));
        patient.setDateOfBirth(generateRandomDate(1950, 2020));
        patient.setPhoneNumber("+79" + (100000000 + random.nextInt(900000000))); // Пример простого номера
        patient.setAddress("Адрес_" + random.nextInt(1000));
        
        // Случайное заболевание из предопределенных, или без заболевания
        if (random.nextBoolean()) {
             String[] diseases = {DentalSpecialty.CARIES, DentalSpecialty.PULPITIS, DentalSpecialty.PERIODONTITIS,
                               DentalSpecialty.GINGIVITIS, DentalSpecialty.TOOTH_ABSCESS, DentalSpecialty.IMPACTED_TOOTH,
                               DentalSpecialty.MALOCCLUSION, DentalSpecialty.DENTAL_TRAUMA, DentalSpecialty.ENAMEL_HYPOPLASIA,
                               DentalSpecialty.TOOTH_SENSITIVITY};
             patient.setDisease(diseases[random.nextInt(diseases.length)]);
        } else {
            patient.setDisease("");
        }

        patient.setChronicDiseases("Хрон._" + random.nextInt(10));
        patient.setAllergies("Аллерг._" + random.nextInt(10));
        patient.setPreviousDiseases(""); // История заболеваний будет заполняться при завершении приемов
        patient.setHereditaryDiseases("Наслед._" + random.nextInt(10));

        return patient;
    }

    private Doctor generateRandomDoctor() {
        Doctor doctor = new Doctor();
        doctor.setFirstName("Врач_Имя_" + random.nextInt(100));
        doctor.setLastName("Врач_Фамилия_" + random.nextInt(100));
        doctor.setMiddleName("Врач_Отчество_" + random.nextInt(100));
        
        // Случайная специализация из предопределенных
        String[] specialties = {DentalSpecialty.THERAPIST, DentalSpecialty.SURGEON, DentalSpecialty.ORTHODONTIST,
                                DentalSpecialty.ENDODONTIST, DentalSpecialty.PERIODONTIST, DentalSpecialty.PROSTHODONTIST,
                                DentalSpecialty.PEDIATRIC_DENTIST};
        doctor.setSpecialization(specialties[random.nextInt(specialties.length)]);

        doctor.setPhoneNumber("+79" + (100000000 + random.nextInt(900000000))); // Пример простого номера
        doctor.setEmail("doctor" + random.nextInt(100) + "@example.com"); // Пример email

        return doctor;
    }

    private Appointment generateRandomAppointment(List<Patient> patients, List<Doctor> doctors) {
        // Выбираем случайного пациента
        Patient patient = patients.get(random.nextInt(patients.size()));

        // Выбираем врача, который может лечить текущее заболевание пациента (если есть)
        Doctor doctor = null;
        String patientDisease = patient.getDisease();
        
        // Попытка найти подходящего врача
        List<Doctor> suitableDoctors = new ArrayList<>();
        if (patientDisease != null && !patientDisease.isEmpty()) {
            for (Doctor d : doctors) {
                if (DentalSpecialty.canTreat(d.getSpecialization(), patientDisease)) {
                    suitableDoctors.add(d);
                }
            }
        }
        
        if (!suitableDoctors.isEmpty()) {
            doctor = suitableDoctors.get(random.nextInt(suitableDoctors.size()));
        } else {
             // Если нет подходящего врача для лечения заболевания, выбираем любого врача для осмотра
             doctor = doctors.get(random.nextInt(doctors.size()));
             patientDisease = ""; // Сбрасываем заболевание для типа процедуры "Осмотр"
        }

        Appointment appointment = new Appointment();
        
        // Генерируем случайную дату и время в рабочем диапазоне
        Date appointmentDateTime = generateRandomAppointmentDateTime();
        appointment.setAppointmentDateTime(appointmentDateTime);
        
        // Определяем тип процедуры
        if (patientDisease != null && !patientDisease.isEmpty()) {
             appointment.setProcedureType("Лечение заболевания - " + patientDisease);
        } else {
             appointment.setProcedureType("Осмотр пациента");
        }

        // Случайный статус (например, Запланировано или Завершено)
        AppointmentStatus[] statuses = AppointmentStatus.values();
        appointment.setStatus(statuses[random.nextInt(statuses.length)].toString());

        appointment.setPatientId(patient.getId());
        appointment.setDoctorId(doctor.getId());

        return appointment;
    }
    
    private Date generateRandomDate(int startYear, int endYear) {
        Calendar calendar = Calendar.getInstance();
        int year = startYear + random.nextInt(endYear - startYear + 1);
        int dayOfYear = random.nextInt(calendar.getActualMaximum(Calendar.DAY_OF_YEAR)) + 1;
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.DAY_OF_YEAR, dayOfYear);
        return calendar.getTime();
    }
    
    private Date generateRandomAppointmentDateTime() {
         Calendar calendar = Calendar.getInstance();
         calendar.setTime(new Date()); // Начинаем с текущей даты
         calendar.add(Calendar.DAY_OF_YEAR, random.nextInt(365)); // В течение следующего года

         int hour, minute;
         do {
             hour = 8 + random.nextInt(9); // с 8 до 16
             minute = random.nextBoolean() ? 0 : 30; // 00 или 30 минут
         } while (hour == 13); // Исключаем 13:00-14:00

         calendar.set(Calendar.HOUR_OF_DAY, hour);
         calendar.set(Calendar.MINUTE, minute);
         calendar.set(Calendar.SECOND, 0);
         calendar.set(Calendar.MILLISECOND, 0);

         return calendar.getTime();
    }
} 