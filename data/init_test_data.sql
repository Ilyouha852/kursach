DELETE FROM appointments;
DELETE FROM patients;
DELETE FROM doctors;

ALTER SEQUENCE IF EXISTS patients_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS doctors_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS appointments_id_seq RESTART WITH 1;
ALTER SEQUENCE IF EXISTS medical_records_id_seq RESTART WITH 1;

INSERT INTO doctors (first_name, last_name, middle_name, specialization, phone_number, email) VALUES
('Александр', 'Иванов', 'Петрович', 'Терапевт', '+79123456789', 'ivanov@clinic.ru'),
('Елена', 'Смирнова', 'Александровна', 'Хирург', '+79234567890', 'smirnova@clinic.ru'),
('Дмитрий', 'Козлов', 'Сергеевич', 'Ортодонт', '+79345678901', 'kozlov@clinic.ru'),
('Мария', 'Новикова', 'Ивановна', 'Эндодонтист', '+79456789012', 'novikova@clinic.ru'),
('Сергей', 'Морозов', 'Андреевич', 'Пародонтолог', '+79567890123', 'morozov@clinic.ru'),
('Анна', 'Волкова', 'Дмитриевна', 'Ортопед', '+79678901234', 'volkova@clinic.ru'),
('Иван', 'Лебедев', 'Николаевич', 'Детский стоматолог', '+79789012345', 'lebedev@clinic.ru'),
('Ольга', 'Соколова', 'Владимировна', 'Терапевт', '+79890123456', 'sokolova@clinic.ru'),
('Андрей', 'Попов', 'Михайлович', 'Хирург', '+79901234567', 'popov@clinic.ru'),
('Наталья', 'Васильева', 'Сергеевна', 'Ортодонт', '+79012345678', 'vasilieva@clinic.ru'),
('Михаил', 'Петров', 'Александрович', 'Эндодонтист', '+79123456780', 'petrov@clinic.ru'),
('Екатерина', 'Семенова', 'Игоревна', 'Пародонтолог', '+79234567801', 'semenova@clinic.ru'),
('Алексей', 'Голубев', 'Дмитриевич', 'Ортопед', '+79345678902', 'golubev@clinic.ru'),
('Татьяна', 'Виноградова', 'Анатольевна', 'Детский стоматолог', '+79456789023', 'vinogradova@clinic.ru'),
('Павел', 'Богданов', 'Владимирович', 'Терапевт', '+79567890134', 'bogdanov@clinic.ru');

INSERT INTO patients (first_name, last_name, middle_name, date_of_birth, phone_number, address, disease, chronic_diseases, allergies, previous_diseases, hereditary_diseases) VALUES
('Иван', 'Сидоров', 'Александрович', '1990-05-15', '+79123456789', 'ул. Ленина, 10', 'Кариес', 'Гипертония', 'Пенициллин', '', 'Сахарный диабет'),
('Мария', 'Кузнецова', 'Ивановна', '1985-08-20', '+79234567890', 'пр. Мира, 25', 'Пульпит', '', 'Пыльца', '', ''),
('Алексей', 'Смирнов', 'Петрович', '1995-03-10', '+79345678901', 'ул. Гагарина, 5', 'Пародонтит', 'Астма', '', '', ''),
('Елена', 'Попова', 'Сергеевна', '1988-11-25', '+79456789012', 'ул. Пушкина, 15', 'Гингивит', '', '', '', ''),
('Дмитрий', 'Васильев', 'Андреевич', '1992-07-30', '+79567890123', 'пр. Победы, 30', 'Абсцесс зуба', 'Гастрит', 'Мед', '', ''),
('Анна', 'Петрова', 'Дмитриевна', '1993-09-05', '+79678901234', 'ул. Советская, 20', 'Ретинированный зуб', '', '', '', ''),
('Сергей', 'Соколов', 'Николаевич', '1987-12-15', '+79789012345', 'ул. Космонавтов, 8', 'Неправильный прикус', 'Аллергия', 'Шерсть', '', ''),
('Ольга', 'Новикова', 'Владимировна', '1991-04-20', '+79890123456', 'пр. Ленина, 40', 'Травма зуба', '', '', '', ''),
('Андрей', 'Морозов', 'Михайлович', '1994-06-25', '+79901234567', 'ул. Мира, 12', 'Гипоплазия эмали', 'Гипотония', '', '', ''),
('Наталья', 'Волкова', 'Сергеевна', '1989-10-10', '+79012345678', 'ул. Гагарина, 35', 'Повышенная чувствительность', '', 'Цитрусовые', '', '');

-- Вставка записей на прием
INSERT INTO appointments (patient_id, doctor_id, appointment_date_time, procedure_type, status) VALUES
(1, 1, '2024-03-20 09:00:00', 'Лечение кариеса', 'PLANNED'),
(2, 2, '2024-03-20 10:30:00', 'Лечение пульпита', 'PLANNED'),
(3, 3, '2024-03-20 11:00:00', 'Лечение пародонтита', 'PLANNED'),
(4, 4, '2024-03-20 14:00:00', 'Лечение гингивита', 'PLANNED'),
(5, 5, '2024-03-20 15:30:00', 'Лечение абсцесса', 'PLANNED'),
(6, 6, '2024-03-21 09:00:00', 'Удаление ретинированного зуба', 'PLANNED'),
(7, 7, '2024-03-21 10:30:00', 'Исправление прикуса', 'PLANNED'),
(8, 8, '2024-03-21 11:00:00', 'Лечение травмы зуба', 'PLANNED'),
(9, 9, '2024-03-21 14:00:00', 'Лечение гипоплазии эмали', 'PLANNED'),
(10, 10, '2024-03-21 15:30:00', 'Лечение чувствительности зубов', 'PLANNED'); 