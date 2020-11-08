CREATE DATABASE mobi7
    WITH 
    OWNER = postgres
    ENCODING = 'UTF8'
    LC_COLLATE = 'en_US.utf8'
    LC_CTYPE = 'en_US.utf8'
    TABLESPACE = pg_default
    CONNECTION LIMIT = -1;

CREATE TABLE public.poi
(
    id serial NOT NULL,
    latitude character varying(255) COLLATE pg_catalog."default",
    longitude character varying(255) COLLATE pg_catalog."default",
    name character varying(255) COLLATE pg_catalog."default",
    radius integer,
    CONSTRAINT poi_pkey PRIMARY KEY (id)
);

CREATE TABLE public."position"
(
    id serial NOT NULL ,
    date timestamp with time zone,
    ignition boolean NOT NULL,
    latitude character varying(255) COLLATE pg_catalog."default",
    license_plate character varying(255) COLLATE pg_catalog."default",
    longitude character varying(255) COLLATE pg_catalog."default",
    velocity integer NOT NULL,
    CONSTRAINT position_pkey PRIMARY KEY (id)
);