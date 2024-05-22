CREATE TABLE IF NOT EXISTS wellness.account
(
    id bigint NOT NULL,
    location character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT account_pkey PRIMARY KEY (id)
)

CREATE TABLE IF NOT EXISTS wellness.profession
(
    id bigint NOT NULL,
    occupation character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT profession_pkey PRIMARY KEY (id)
)

INSERT INTO wellness.professions(
	id, occupation)
	VALUES (2, 'NUTRITIONIST');

INSERT INTO wellness.professions(
	id, occupation)
	VALUES (1, 'PSYCHOLOGIST');


CREATE TABLE IF NOT EXISTS wellness.roles
(
    id bigint NOT NULL,
    name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT roles_pkey PRIMARY KEY (id),
    CONSTRAINT roles_name_key UNIQUE (name),
    CONSTRAINT roles_name_check CHECK (name::text = ANY (ARRAY['ROLE_ADMIN'::character varying, 'ROLE_SPECIALIST'::character varying, 'ROLE_PATIENT'::character varying]::text[]))
)

INSERT INTO wellness.roles(
	id, name)
	VALUES (1, 'ROLE_ADMIN');

INSERT INTO wellness.roles(
	id, name)
	VALUES (2, 'ROLE_SPECIALIST');

INSERT INTO wellness.roles(
	id, name)
	VALUES (3, 'ROLE_PATIENT');

CREATE TABLE IF NOT EXISTS wellness.site
(
    id bigint NOT NULL,
    location character varying(255) COLLATE pg_catalog."default",
    occupation character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT site_pkey PRIMARY KEY (id)
)

CREATE TABLE IF NOT EXISTS wellness.users
(
    is_active boolean NOT NULL,
    created_at timestamp(6) without time zone,
    id bigint NOT NULL,
    role_id bigint NOT NULL,
    personal_email character varying(100) COLLATE pg_catalog."default" NOT NULL,
    contact_phone character varying(255) COLLATE pg_catalog."default" NOT NULL,
    first_name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    gender character varying(255) COLLATE pg_catalog."default" NOT NULL,
    last_name character varying(255) COLLATE pg_catalog."default" NOT NULL,
    password character varying(255) COLLATE pg_catalog."default" NOT NULL,
    work_email character varying(255) COLLATE pg_catalog."default",
    workday character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_contact_phone_key UNIQUE (contact_phone),
    CONSTRAINT users_personal_email_key UNIQUE (personal_email),
    CONSTRAINT users_role_id_key UNIQUE (role_id),
    CONSTRAINT users_workday_key UNIQUE (workday),
    CONSTRAINT fkp56c1712k691lhsyewcssf40f FOREIGN KEY (role_id)
        REFERENCES wellness.roles (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

CREATE TABLE IF NOT EXISTS wellness.specialist
(
    id bigint NOT NULL,
    profession_id bigint,
    user_id bigint,
    CONSTRAINT specialist_pkey PRIMARY KEY (id),
    CONSTRAINT fk7m2o9mws8lf8dntq38fjv777n FOREIGN KEY (profession_id)
        REFERENCES wellness.profession (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkcl18n8e8facaokqbr3ocanxnb FOREIGN KEY (user_id)
        REFERENCES wellness.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkh3w6mud9ch7yau9tqr100r0vr FOREIGN KEY (id)
        REFERENCES wellness.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)


CREATE TABLE IF NOT EXISTS wellness.patient
(
    account_id bigint,
    id bigint NOT NULL,
    user_id bigint,
    CONSTRAINT patient_pkey PRIMARY KEY (id),
    CONSTRAINT fkf0or75ex3abs31ottuqg8s301 FOREIGN KEY (id)
        REFERENCES wellness.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkie6vajiyur53rjcl5nc2pe83t FOREIGN KEY (user_id)
        REFERENCES wellness.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkn6atqtmaryxq3o213gi3or1ij FOREIGN KEY (account_id)
        REFERENCES wellness.account (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)

CREATE TABLE IF NOT EXISTS wellness.appointments
(
    appointment_number integer NOT NULL,
    attended boolean NOT NULL,
    patient_state integer NOT NULL,
    date timestamp(6) without time zone,
    id bigint NOT NULL,
    patient_id bigint,
    site_id bigint,
    specialist_id bigint,
    comments character varying(255) COLLATE pg_catalog."default",
    google_meeting character varying(255) COLLATE pg_catalog."default",
    mode character varying(255) COLLATE pg_catalog."default",
    CONSTRAINT appointments_pkey PRIMARY KEY (id),
    CONSTRAINT fk8rw63cay7q06br5j0w187cjxl FOREIGN KEY (site_id)
        REFERENCES wellness.site (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fkcl9b1a19a01yhjcdibna1gjl FOREIGN KEY (patient_id)
        REFERENCES wellness.patient (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION,
    CONSTRAINT fki6x03a1vi8bqu66kqpktnah8 FOREIGN KEY (specialist_id)
        REFERENCES wellness.specialist (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
)