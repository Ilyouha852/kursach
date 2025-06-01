-- Table: public.patients

-- DROP TABLE IF EXISTS public.patients;

CREATE TABLE IF NOT EXISTS public.patients
(
    id integer NOT NULL DEFAULT nextval('patients_id_seq'::regclass),
    first_name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    last_name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    middle_name character varying(255) COLLATE pg_catalog."default",
    date_of_birth date,
    phone_number character varying(20) COLLATE pg_catalog."default",
    address text COLLATE pg_catalog."default",
    chronic_diseases text COLLATE pg_catalog."default",
    allergies text COLLATE pg_catalog."default",
    previous_diseases text COLLATE pg_catalog."default",
    hereditary_diseases text COLLATE pg_catalog."default",
    disease character varying(100) COLLATE pg_catalog."default",
    CONSTRAINT patients_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.patients
    OWNER to postgres;

-- Table: public.doctors

-- DROP TABLE IF EXISTS public.doctors;

CREATE TABLE IF NOT EXISTS public.doctors
(
    id integer NOT NULL DEFAULT nextval('doctors_id_seq'::regclass),
    first_name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    last_name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    middle_name character varying(255) COLLATE pg_catalog."default",
    specialization character varying(255) COLLATE pg_catalog."default",
    phone_number character varying(20) COLLATE pg_catalog."default",
    email character varying(100) COLLATE pg_catalog."default",
    CONSTRAINT doctors_pkey PRIMARY KEY (id)
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.doctors
    OWNER to postgres;


-- Table: public.appointments

-- DROP TABLE IF EXISTS public.appointments;

CREATE TABLE IF NOT EXISTS public.appointments
(
    id integer NOT NULL DEFAULT nextval('appointments_id_seq'::regclass),
    appointment_date_time timestamp without time zone NOT NULL,
    procedure_type character varying(255) COLLATE pg_catalog."default",
    status character varying(50) COLLATE pg_catalog."default",
    patient_id integer,
    doctor_id integer,
    CONSTRAINT appointments_pkey PRIMARY KEY (id),
    CONSTRAINT appointments_doctor_id_fkey FOREIGN KEY (doctor_id)
        REFERENCES public.doctors (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT appointments_patient_id_fkey FOREIGN KEY (patient_id)
        REFERENCES public.patients (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.appointments
    OWNER to postgres;

-- Table: public.medical_records

-- DROP TABLE IF EXISTS public.medical_records;

CREATE TABLE IF NOT EXISTS public.medical_records
(
    id integer NOT NULL DEFAULT nextval('medical_records_id_seq'::regclass),
    record_date date NOT NULL,
    diagnosis text COLLATE pg_catalog."default",
    procedures_performed text COLLATE pg_catalog."default",
    notes text COLLATE pg_catalog."default",
    patient_id integer,
    CONSTRAINT medical_records_pkey PRIMARY KEY (id),
    CONSTRAINT medical_records_patient_id_fkey FOREIGN KEY (patient_id)
        REFERENCES public.patients (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

TABLESPACE pg_default;

ALTER TABLE IF EXISTS public.medical_records
    OWNER to postgres;