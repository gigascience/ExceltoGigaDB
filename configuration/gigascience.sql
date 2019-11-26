--
-- PostgreSQL database dump
--

-- Dumped from database version 9.3.4
-- Dumped by pg_dump version 9.3.4
-- Started on 2015-04-01 10:07:41 HKT

SET statement_timeout = 0;
SET lock_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;

--
-- TOC entry 267 (class 3079 OID 12018)
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- TOC entry 2769 (class 0 OID 0)
-- Dependencies: 267
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- TOC entry 170 (class 1259 OID 36220)
-- Name: AuthAssignment; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE "AuthAssignment" (
    itemname character varying(64) NOT NULL,
    userid character varying(64) NOT NULL,
    bizrule text,
    data text
);


ALTER TABLE public."AuthAssignment" OWNER TO gigadb;

--
-- TOC entry 171 (class 1259 OID 36226)
-- Name: AuthItem; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE "AuthItem" (
    name character varying(64) NOT NULL,
    type integer NOT NULL,
    description text,
    bizrule text,
    data text
);


ALTER TABLE public."AuthItem" OWNER TO gigadb;

--
-- TOC entry 172 (class 1259 OID 36232)
-- Name: YiiSession; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE "YiiSession" (
    id character(32) NOT NULL,
    expire integer,
    data bytea
);


ALTER TABLE public."YiiSession" OWNER TO gigadb;

--
-- TOC entry 173 (class 1259 OID 36238)
-- Name: alternative_identifiers; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE alternative_identifiers (
    id integer NOT NULL,
    sample_id integer NOT NULL,
    extdb_id integer NOT NULL,
    extdb_accession character varying(100)
);


ALTER TABLE public.alternative_identifiers OWNER TO gigadb;

--
-- TOC entry 2770 (class 0 OID 0)
-- Dependencies: 173
-- Name: COLUMN alternative_identifiers.id; Type: COMMENT; Schema: public; Owner: gigadb
--

COMMENT ON COLUMN alternative_identifiers.id IS '

';


--
-- TOC entry 174 (class 1259 OID 36241)
-- Name: alternative_identifiers_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE alternative_identifiers_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.alternative_identifiers_id_seq OWNER TO gigadb;

--
-- TOC entry 2771 (class 0 OID 0)
-- Dependencies: 174
-- Name: alternative_identifiers_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE alternative_identifiers_id_seq OWNED BY alternative_identifiers.id;


--
-- TOC entry 175 (class 1259 OID 36243)
-- Name: attribute; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE attribute (
    id integer NOT NULL,
    attribute_name character varying(100),
    definition character varying(1000),
    model character varying(30),
    structured_comment_name character varying(50),
    value_syntax character varying(500),
    allowed_units character varying(100),
    occurance character varying(5),
    ontology_link character varying(1000),
    note character varying(50)
);


ALTER TABLE public.attribute OWNER TO gigadb;

--
-- TOC entry 176 (class 1259 OID 36249)
-- Name: attribute_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE attribute_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.attribute_id_seq OWNER TO gigadb;

--
-- TOC entry 2772 (class 0 OID 0)
-- Dependencies: 176
-- Name: attribute_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE attribute_id_seq OWNED BY attribute.id;


--
-- TOC entry 177 (class 1259 OID 36251)
-- Name: author; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE author (
    id integer NOT NULL,
    surname character varying(255) NOT NULL,
    middle_name character varying(255),
    first_name character varying(255),
    orcid character varying(255),
    gigadb_user_id integer
);


ALTER TABLE public.author OWNER TO gigadb;

--
-- TOC entry 178 (class 1259 OID 36257)
-- Name: author_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE author_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.author_id_seq OWNER TO gigadb;

--
-- TOC entry 2773 (class 0 OID 0)
-- Dependencies: 178
-- Name: author_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE author_id_seq OWNED BY author.id;


--
-- TOC entry 179 (class 1259 OID 36259)
-- Name: dataset; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE dataset (
    id integer NOT NULL,
    submitter_id integer NOT NULL,
    image_id integer,
    identifier character varying(32) NOT NULL,
    title character varying(300) NOT NULL,
    description text DEFAULT ''::text NOT NULL,
    dataset_size bigint NOT NULL,
    ftp_site character varying(100) NOT NULL,
    upload_status character varying(45) DEFAULT 'Pending'::character varying NOT NULL,
    excelfile character varying(50),
    excelfile_md5 character varying(32),
    publication_date date,
    modification_date date,
    publisher_id integer,
    token character varying(16) DEFAULT NULL::character varying,
    fairnuse date
);


ALTER TABLE public.dataset OWNER TO gigadb;

--
-- TOC entry 180 (class 1259 OID 36268)
-- Name: dataset_attributes; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE dataset_attributes (
    id integer NOT NULL,
    dataset_id integer,
    attribute_id integer,
    value character varying(50),
    units_id character varying(30),
    image_id integer,
    until_date date
);


ALTER TABLE public.dataset_attributes OWNER TO gigadb;

--
-- TOC entry 181 (class 1259 OID 36271)
-- Name: dataset_attributes_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE dataset_attributes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dataset_attributes_id_seq OWNER TO gigadb;

--
-- TOC entry 2774 (class 0 OID 0)
-- Dependencies: 181
-- Name: dataset_attributes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE dataset_attributes_id_seq OWNED BY dataset_attributes.id;


--
-- TOC entry 182 (class 1259 OID 36273)
-- Name: dataset_author; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE dataset_author (
    id integer NOT NULL,
    dataset_id integer NOT NULL,
    author_id integer NOT NULL,
    rank integer DEFAULT 0
);


ALTER TABLE public.dataset_author OWNER TO gigadb;

--
-- TOC entry 183 (class 1259 OID 36277)
-- Name: dataset_author_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE dataset_author_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dataset_author_id_seq OWNER TO gigadb;

--
-- TOC entry 2775 (class 0 OID 0)
-- Dependencies: 183
-- Name: dataset_author_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE dataset_author_id_seq OWNED BY dataset_author.id;


--
-- TOC entry 184 (class 1259 OID 36279)
-- Name: dataset_funder; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE dataset_funder (
    id integer NOT NULL,
    dataset_id integer NOT NULL,
    funder_id integer NOT NULL,
    grant_award text DEFAULT ''::text,
    comments text DEFAULT ''::text,
    awardee text DEFAULT ''::text
);


ALTER TABLE public.dataset_funder OWNER TO gigadb;

--
-- TOC entry 185 (class 1259 OID 36287)
-- Name: dataset_funder_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE dataset_funder_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dataset_funder_id_seq OWNER TO gigadb;

--
-- TOC entry 2776 (class 0 OID 0)
-- Dependencies: 185
-- Name: dataset_funder_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE dataset_funder_id_seq OWNED BY dataset_funder.id;


--
-- TOC entry 186 (class 1259 OID 36289)
-- Name: dataset_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE dataset_id_seq
    START WITH 33
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dataset_id_seq OWNER TO gigadb;

--
-- TOC entry 2777 (class 0 OID 0)
-- Dependencies: 186
-- Name: dataset_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE dataset_id_seq OWNED BY dataset.id;


--
-- TOC entry 187 (class 1259 OID 36291)
-- Name: dataset_log; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE dataset_log (
    id integer NOT NULL,
    dataset_id integer NOT NULL,
    message text DEFAULT ''::text,
    created_at timestamp without time zone DEFAULT now(),
    model text,
    model_id integer,
    url text DEFAULT ''::text
);


ALTER TABLE public.dataset_log OWNER TO gigadb;

--
-- TOC entry 188 (class 1259 OID 36300)
-- Name: dataset_log_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE dataset_log_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dataset_log_id_seq OWNER TO gigadb;

--
-- TOC entry 2778 (class 0 OID 0)
-- Dependencies: 188
-- Name: dataset_log_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE dataset_log_id_seq OWNED BY dataset_log.id;


--
-- TOC entry 189 (class 1259 OID 36302)
-- Name: dataset_project; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE dataset_project (
    id integer NOT NULL,
    dataset_id integer,
    project_id integer
);


ALTER TABLE public.dataset_project OWNER TO gigadb;

--
-- TOC entry 190 (class 1259 OID 36305)
-- Name: dataset_project_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE dataset_project_id_seq
    START WITH 7
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dataset_project_id_seq OWNER TO gigadb;

--
-- TOC entry 2779 (class 0 OID 0)
-- Dependencies: 190
-- Name: dataset_project_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE dataset_project_id_seq OWNED BY dataset_project.id;


--
-- TOC entry 191 (class 1259 OID 36307)
-- Name: dataset_sample; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE dataset_sample (
    id integer NOT NULL,
    dataset_id integer NOT NULL,
    sample_id integer NOT NULL
);


ALTER TABLE public.dataset_sample OWNER TO gigadb;

--
-- TOC entry 192 (class 1259 OID 36310)
-- Name: dataset_sample_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE dataset_sample_id_seq
    START WITH 211
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dataset_sample_id_seq OWNER TO gigadb;

--
-- TOC entry 2780 (class 0 OID 0)
-- Dependencies: 192
-- Name: dataset_sample_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE dataset_sample_id_seq OWNED BY dataset_sample.id;


--
-- TOC entry 193 (class 1259 OID 36312)
-- Name: dataset_session; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE dataset_session (
    id integer NOT NULL,
    identifier text NOT NULL,
    dataset text,
    dataset_id text,
    datasettypes text,
    images text,
    authors text,
    projects text,
    links text,
    "externalLinks" text,
    relations text,
    samples text
);


ALTER TABLE public.dataset_session OWNER TO gigadb;

--
-- TOC entry 194 (class 1259 OID 36318)
-- Name: dataset_session_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE dataset_session_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dataset_session_id_seq OWNER TO gigadb;

--
-- TOC entry 2782 (class 0 OID 0)
-- Dependencies: 194
-- Name: dataset_session_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE dataset_session_id_seq OWNED BY dataset_session.id;


--
-- TOC entry 195 (class 1259 OID 36320)
-- Name: dataset_type; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE dataset_type (
    id integer NOT NULL,
    dataset_id integer NOT NULL,
    type_id integer
);


ALTER TABLE public.dataset_type OWNER TO gigadb;

--
-- TOC entry 196 (class 1259 OID 36323)
-- Name: dataset_type_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE dataset_type_id_seq
    START WITH 37
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.dataset_type_id_seq OWNER TO gigadb;

--
-- TOC entry 2783 (class 0 OID 0)
-- Dependencies: 196
-- Name: dataset_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE dataset_type_id_seq OWNED BY dataset_type.id;


--
-- TOC entry 197 (class 1259 OID 36325)
-- Name: exp_attributes; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE exp_attributes (
    id integer NOT NULL,
    exp_id integer,
    attribute_id integer,
    value character varying(1000),
    units_id character varying(50)
);


ALTER TABLE public.exp_attributes OWNER TO gigadb;

--
-- TOC entry 198 (class 1259 OID 36331)
-- Name: exp_attributes_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE exp_attributes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.exp_attributes_id_seq OWNER TO gigadb;

--
-- TOC entry 2784 (class 0 OID 0)
-- Dependencies: 198
-- Name: exp_attributes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE exp_attributes_id_seq OWNED BY exp_attributes.id;


--
-- TOC entry 199 (class 1259 OID 36333)
-- Name: experiment; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE experiment (
    id integer NOT NULL,
    experiment_type character varying(100),
    experiment_name character varying(100),
    exp_description character varying(1000),
    dataset_id integer
);


ALTER TABLE public.experiment OWNER TO gigadb;

--
-- TOC entry 200 (class 1259 OID 36339)
-- Name: experiment_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE experiment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.experiment_id_seq OWNER TO gigadb;

--
-- TOC entry 2785 (class 0 OID 0)
-- Dependencies: 200
-- Name: experiment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE experiment_id_seq OWNED BY experiment.id;


--
-- TOC entry 201 (class 1259 OID 36341)
-- Name: extdb; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE extdb (
    id integer NOT NULL,
    database_name character varying(100),
    definition character varying(1000),
    database_homepage character varying(100),
    database_search_url character varying(100)
);


ALTER TABLE public.extdb OWNER TO gigadb;

--
-- TOC entry 202 (class 1259 OID 36347)
-- Name: extdb_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE extdb_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.extdb_id_seq OWNER TO gigadb;

--
-- TOC entry 2786 (class 0 OID 0)
-- Dependencies: 202
-- Name: extdb_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE extdb_id_seq OWNED BY extdb.id;


--
-- TOC entry 203 (class 1259 OID 36349)
-- Name: external_link; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE external_link (
    id integer NOT NULL,
    dataset_id integer NOT NULL,
    url character varying(128) NOT NULL,
    external_link_type_id integer NOT NULL
);


ALTER TABLE public.external_link OWNER TO gigadb;

--
-- TOC entry 204 (class 1259 OID 36352)
-- Name: external_link_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE external_link_id_seq
    START WITH 17
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.external_link_id_seq OWNER TO gigadb;

--
-- TOC entry 2787 (class 0 OID 0)
-- Dependencies: 204
-- Name: external_link_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE external_link_id_seq OWNED BY external_link.id;


--
-- TOC entry 205 (class 1259 OID 36354)
-- Name: external_link_type; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE external_link_type (
    id integer NOT NULL,
    name character varying(45) NOT NULL
);


ALTER TABLE public.external_link_type OWNER TO gigadb;

--
-- TOC entry 206 (class 1259 OID 36357)
-- Name: external_link_type_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE external_link_type_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.external_link_type_id_seq OWNER TO gigadb;

--
-- TOC entry 2788 (class 0 OID 0)
-- Dependencies: 206
-- Name: external_link_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE external_link_type_id_seq OWNED BY external_link_type.id;


--
-- TOC entry 207 (class 1259 OID 36359)
-- Name: file; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE file (
    id integer NOT NULL,
    dataset_id integer NOT NULL,
    name character varying(100) NOT NULL,
    location character varying(200) NOT NULL,
    extension character varying(100) NOT NULL,
    size bigint NOT NULL,
    description text DEFAULT ''::text NOT NULL,
    date_stamp date,
    format_id integer,
    type_id integer,
    code character varying(200) DEFAULT 'FILE_CODE'::character varying,
    index4blast character varying(50),
    download_count integer DEFAULT 0 NOT NULL
);


ALTER TABLE public.file OWNER TO gigadb;

--
-- TOC entry 208 (class 1259 OID 36368)
-- Name: file_attributes; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE file_attributes (
    id integer NOT NULL,
    file_id integer NOT NULL,
    attribute_id integer NOT NULL,
    value character varying(50),
    unit_id character varying(30)
);


ALTER TABLE public.file_attributes OWNER TO gigadb;

--
-- TOC entry 209 (class 1259 OID 36371)
-- Name: file_attributes_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE file_attributes_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.file_attributes_id_seq OWNER TO gigadb;

--
-- TOC entry 2789 (class 0 OID 0)
-- Dependencies: 209
-- Name: file_attributes_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE file_attributes_id_seq OWNED BY file_attributes.id;


--
-- TOC entry 210 (class 1259 OID 36373)
-- Name: file_experiment; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE file_experiment (
    id integer NOT NULL,
    file_id integer,
    experiment_id integer
);


ALTER TABLE public.file_experiment OWNER TO gigadb;

--
-- TOC entry 211 (class 1259 OID 36376)
-- Name: file_experiment_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE file_experiment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.file_experiment_id_seq OWNER TO gigadb;

--
-- TOC entry 2790 (class 0 OID 0)
-- Dependencies: 211
-- Name: file_experiment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE file_experiment_id_seq OWNED BY file_experiment.id;


--
-- TOC entry 212 (class 1259 OID 36378)
-- Name: file_format; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE file_format (
    id integer NOT NULL,
    name character varying(20) NOT NULL,
    description text DEFAULT ''::text NOT NULL
);


ALTER TABLE public.file_format OWNER TO gigadb;

--
-- TOC entry 213 (class 1259 OID 36385)
-- Name: file_format_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE file_format_id_seq
    START WITH 26
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.file_format_id_seq OWNER TO gigadb;

--
-- TOC entry 2791 (class 0 OID 0)
-- Dependencies: 213
-- Name: file_format_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE file_format_id_seq OWNED BY file_format.id;


--
-- TOC entry 214 (class 1259 OID 36387)
-- Name: file_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE file_id_seq
    START WITH 6716
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.file_id_seq OWNER TO gigadb;

--
-- TOC entry 2792 (class 0 OID 0)
-- Dependencies: 214
-- Name: file_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE file_id_seq OWNED BY file.id;


--
-- TOC entry 215 (class 1259 OID 36389)
-- Name: file_relationship; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE file_relationship (
    id integer NOT NULL,
    file_id integer NOT NULL,
    related_file_id integer NOT NULL,
    relationship_id integer
);


ALTER TABLE public.file_relationship OWNER TO gigadb;

--
-- TOC entry 216 (class 1259 OID 36392)
-- Name: file_relationship_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE file_relationship_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.file_relationship_id_seq OWNER TO gigadb;

--
-- TOC entry 2793 (class 0 OID 0)
-- Dependencies: 216
-- Name: file_relationship_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE file_relationship_id_seq OWNED BY file_relationship.id;


--
-- TOC entry 217 (class 1259 OID 36394)
-- Name: file_sample; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE file_sample (
    id integer NOT NULL,
    sample_id integer NOT NULL,
    file_id integer NOT NULL
);


ALTER TABLE public.file_sample OWNER TO gigadb;

--
-- TOC entry 218 (class 1259 OID 36397)
-- Name: file_sample_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE file_sample_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.file_sample_id_seq OWNER TO gigadb;

--
-- TOC entry 2794 (class 0 OID 0)
-- Dependencies: 218
-- Name: file_sample_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE file_sample_id_seq OWNED BY file_sample.id;


--
-- TOC entry 219 (class 1259 OID 36399)
-- Name: file_type; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE file_type (
    id integer NOT NULL,
    name character varying(100) NOT NULL,
    description text DEFAULT ''::text NOT NULL
);


ALTER TABLE public.file_type OWNER TO gigadb;

--
-- TOC entry 220 (class 1259 OID 36406)
-- Name: file_type_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE file_type_id_seq
    START WITH 15
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.file_type_id_seq OWNER TO gigadb;

--
-- TOC entry 2795 (class 0 OID 0)
-- Dependencies: 220
-- Name: file_type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE file_type_id_seq OWNED BY file_type.id;


--
-- TOC entry 221 (class 1259 OID 36408)
-- Name: funder_name; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE funder_name (
    id integer NOT NULL,
    uri character varying(100) NOT NULL,
    primary_name_display character varying(1000),
    country character varying(128) DEFAULT ''::character varying
);


ALTER TABLE public.funder_name OWNER TO gigadb;

--
-- TOC entry 222 (class 1259 OID 36415)
-- Name: funder_name_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE funder_name_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.funder_name_id_seq OWNER TO gigadb;

--
-- TOC entry 2796 (class 0 OID 0)
-- Dependencies: 222
-- Name: funder_name_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE funder_name_id_seq OWNED BY funder_name.id;


--
-- TOC entry 223 (class 1259 OID 36417)
-- Name: gigadb_user; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE gigadb_user (
    id integer NOT NULL,
    email character varying(64) NOT NULL,
    password character varying(64) NOT NULL,
    first_name character varying(100) NOT NULL,
    last_name character varying(100) NOT NULL,
    affiliation character varying(200) NOT NULL,
    role character varying(30) DEFAULT 'user'::character varying NOT NULL,
    is_activated boolean DEFAULT false NOT NULL,
    newsletter boolean DEFAULT true NOT NULL,
    previous_newsletter_state boolean DEFAULT false NOT NULL,
    facebook_id text,
    twitter_id text,
    linkedin_id text,
    google_id text,
    username text NOT NULL,
    orcid_id text,
    preferred_link character varying(128) DEFAULT 'EBI'::character varying
);


ALTER TABLE public.gigadb_user OWNER TO gigadb;

--
-- TOC entry 224 (class 1259 OID 36428)
-- Name: gigadb_user_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE gigadb_user_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.gigadb_user_id_seq OWNER TO gigadb;

--
-- TOC entry 2797 (class 0 OID 0)
-- Dependencies: 224
-- Name: gigadb_user_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE gigadb_user_id_seq OWNED BY gigadb_user.id;


--
-- TOC entry 225 (class 1259 OID 36430)
-- Name: image; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE image (
    id integer NOT NULL,
    location character varying(200) DEFAULT ''::character varying NOT NULL,
    tag character varying(300),
    url character varying(256),
    license text NOT NULL,
    photographer character varying(128) NOT NULL,
    source character varying(256) NOT NULL
);


ALTER TABLE public.image OWNER TO gigadb;

--
-- TOC entry 226 (class 1259 OID 36437)
-- Name: image_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE image_id_seq
    START WITH 31
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.image_id_seq OWNER TO gigadb;

--
-- TOC entry 2798 (class 0 OID 0)
-- Dependencies: 226
-- Name: image_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE image_id_seq OWNED BY image.id;


--
-- TOC entry 227 (class 1259 OID 36439)
-- Name: link; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE link (
    id integer NOT NULL,
    dataset_id integer NOT NULL,
    is_primary boolean DEFAULT false NOT NULL,
    link character varying(100) NOT NULL
);


ALTER TABLE public.link OWNER TO gigadb;

--
-- TOC entry 228 (class 1259 OID 36443)
-- Name: link_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE link_id_seq
    START WITH 66
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.link_id_seq OWNER TO gigadb;

--
-- TOC entry 2799 (class 0 OID 0)
-- Dependencies: 228
-- Name: link_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE link_id_seq OWNED BY link.id;


--
-- TOC entry 229 (class 1259 OID 36445)
-- Name: link_prefix_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE link_prefix_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.link_prefix_id_seq OWNER TO gigadb;

--
-- TOC entry 230 (class 1259 OID 36447)
-- Name: manuscript; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE manuscript (
    id integer NOT NULL,
    identifier character varying(32) NOT NULL,
    pmid integer,
    dataset_id integer NOT NULL
);


ALTER TABLE public.manuscript OWNER TO gigadb;

--
-- TOC entry 231 (class 1259 OID 36450)
-- Name: manuscript_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE manuscript_id_seq
    START WITH 27
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.manuscript_id_seq OWNER TO gigadb;

--
-- TOC entry 2800 (class 0 OID 0)
-- Dependencies: 231
-- Name: manuscript_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE manuscript_id_seq OWNED BY manuscript.id;


--
-- TOC entry 232 (class 1259 OID 36452)
-- Name: news; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE news (
    id integer NOT NULL,
    title character varying(200) NOT NULL,
    body text DEFAULT ''::text NOT NULL,
    start_date date NOT NULL,
    end_date date NOT NULL
);


ALTER TABLE public.news OWNER TO gigadb;

--
-- TOC entry 233 (class 1259 OID 36459)
-- Name: news_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE news_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.news_id_seq OWNER TO gigadb;

--
-- TOC entry 2801 (class 0 OID 0)
-- Dependencies: 233
-- Name: news_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE news_id_seq OWNED BY news.id;


--
-- TOC entry 234 (class 1259 OID 36461)
-- Name: prefix; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE prefix (
    id integer DEFAULT nextval('link_prefix_id_seq'::regclass) NOT NULL,
    prefix character(20) NOT NULL,
    url text NOT NULL,
    source character varying(128) DEFAULT ''::character varying
);


ALTER TABLE public.prefix OWNER TO gigadb;

--
-- TOC entry 235 (class 1259 OID 36469)
-- Name: project; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE project (
    id integer NOT NULL,
    url character varying(128) NOT NULL,
    name character varying(255) DEFAULT ''::character varying NOT NULL,
    image_location character varying(100)
);


ALTER TABLE public.project OWNER TO gigadb;

--
-- TOC entry 236 (class 1259 OID 36473)
-- Name: project_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE project_id_seq
    START WITH 7
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.project_id_seq OWNER TO gigadb;

--
-- TOC entry 2803 (class 0 OID 0)
-- Dependencies: 236
-- Name: project_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE project_id_seq OWNED BY project.id;


--
-- TOC entry 237 (class 1259 OID 36475)
-- Name: publisher; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE publisher (
    id integer NOT NULL,
    name character varying(45) NOT NULL,
    description text DEFAULT ''::text NOT NULL
);


ALTER TABLE public.publisher OWNER TO gigadb;

--
-- TOC entry 238 (class 1259 OID 36482)
-- Name: publisher_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE publisher_id_seq
    START WITH 3
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.publisher_id_seq OWNER TO gigadb;

--
-- TOC entry 2804 (class 0 OID 0)
-- Dependencies: 238
-- Name: publisher_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE publisher_id_seq OWNED BY publisher.id;


--
-- TOC entry 239 (class 1259 OID 36484)
-- Name: relation; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE relation (
    id integer NOT NULL,
    dataset_id integer NOT NULL,
    related_doi character varying(15) NOT NULL,
    relationship_id integer
);


ALTER TABLE public.relation OWNER TO gigadb;

--
-- TOC entry 240 (class 1259 OID 36487)
-- Name: relation_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE relation_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.relation_id_seq OWNER TO gigadb;

--
-- TOC entry 2805 (class 0 OID 0)
-- Dependencies: 240
-- Name: relation_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE relation_id_seq OWNED BY relation.id;


--
-- TOC entry 241 (class 1259 OID 36489)
-- Name: relationship; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE relationship (
    id integer NOT NULL,
    name character varying(100)
);


ALTER TABLE public.relationship OWNER TO gigadb;

--
-- TOC entry 242 (class 1259 OID 36492)
-- Name: relationship_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE relationship_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.relationship_id_seq OWNER TO gigadb;

--
-- TOC entry 2806 (class 0 OID 0)
-- Dependencies: 242
-- Name: relationship_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE relationship_id_seq OWNED BY relationship.id;


--
-- TOC entry 243 (class 1259 OID 36494)
-- Name: rss_message; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE rss_message (
    id integer NOT NULL,
    message character varying(128) NOT NULL,
    publication_date date DEFAULT ('now'::text)::date NOT NULL
);


ALTER TABLE public.rss_message OWNER TO gigadb;

--
-- TOC entry 244 (class 1259 OID 36498)
-- Name: rss_message_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE rss_message_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.rss_message_id_seq OWNER TO gigadb;

--
-- TOC entry 2807 (class 0 OID 0)
-- Dependencies: 244
-- Name: rss_message_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE rss_message_id_seq OWNED BY rss_message.id;


--
-- TOC entry 245 (class 1259 OID 36500)
-- Name: sample; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE sample (
    id integer NOT NULL,
    species_id integer NOT NULL,
    name character varying(100) DEFAULT 'SAMPLE:SRS188811'::character varying NOT NULL,
    consent_document character varying(45),
    submitted_id integer,
    submission_date date,
    contact_author_name character varying(45),
    contact_author_email character varying(100),
    sampling_protocol character varying(100)
);


ALTER TABLE public.sample OWNER TO gigadb;

--
-- TOC entry 246 (class 1259 OID 36504)
-- Name: sample_attribute; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE sample_attribute (
    id integer NOT NULL,
    sample_id integer NOT NULL,
    attribute_id integer NOT NULL,
    value character varying(5000),
    unit_id character varying(30)
);


ALTER TABLE public.sample_attribute OWNER TO gigadb;

--
-- TOC entry 247 (class 1259 OID 36510)
-- Name: sample_attribute_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE sample_attribute_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sample_attribute_id_seq OWNER TO gigadb;

--
-- TOC entry 2808 (class 0 OID 0)
-- Dependencies: 247
-- Name: sample_attribute_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE sample_attribute_id_seq OWNED BY sample_attribute.id;


--
-- TOC entry 248 (class 1259 OID 36512)
-- Name: sample_experiment; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE sample_experiment (
    id integer NOT NULL,
    sample_id integer,
    experiment_id integer
);


ALTER TABLE public.sample_experiment OWNER TO gigadb;

--
-- TOC entry 249 (class 1259 OID 36515)
-- Name: sample_experiment_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE sample_experiment_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sample_experiment_id_seq OWNER TO gigadb;

--
-- TOC entry 2809 (class 0 OID 0)
-- Dependencies: 249
-- Name: sample_experiment_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE sample_experiment_id_seq OWNED BY sample_experiment.id;


--
-- TOC entry 250 (class 1259 OID 36517)
-- Name: sample_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE sample_id_seq
    START WITH 210
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sample_id_seq OWNER TO gigadb;

--
-- TOC entry 2810 (class 0 OID 0)
-- Dependencies: 250
-- Name: sample_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE sample_id_seq OWNED BY sample.id;


--
-- TOC entry 251 (class 1259 OID 36519)
-- Name: sample_rel; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE sample_rel (
    id integer NOT NULL,
    sample_id integer NOT NULL,
    related_sample_id integer NOT NULL,
    relationship_id integer
);


ALTER TABLE public.sample_rel OWNER TO gigadb;

--
-- TOC entry 252 (class 1259 OID 36522)
-- Name: sample_rel_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE sample_rel_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.sample_rel_id_seq OWNER TO gigadb;

--
-- TOC entry 2811 (class 0 OID 0)
-- Dependencies: 252
-- Name: sample_rel_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE sample_rel_id_seq OWNED BY sample_rel.id;


--
-- TOC entry 253 (class 1259 OID 36524)
-- Name: schemup_tables; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE schemup_tables (
    table_name character varying NOT NULL,
    version character varying NOT NULL,
    is_current boolean DEFAULT false NOT NULL,
    schema text
);


ALTER TABLE public.schemup_tables OWNER TO gigadb;

--
-- TOC entry 254 (class 1259 OID 36531)
-- Name: search; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE search (
    id integer NOT NULL,
    user_id integer NOT NULL,
    name character varying(128) NOT NULL,
    query text NOT NULL,
    result text
);


ALTER TABLE public.search OWNER TO gigadb;

--
-- TOC entry 255 (class 1259 OID 36537)
-- Name: search_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE search_id_seq
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.search_id_seq OWNER TO gigadb;

--
-- TOC entry 2812 (class 0 OID 0)
-- Dependencies: 255
-- Name: search_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE search_id_seq OWNED BY search.id;


--
-- TOC entry 256 (class 1259 OID 36539)
-- Name: show_accession; Type: VIEW; Schema: public; Owner: gigadb
--

CREATE VIEW show_accession AS
 SELECT ('DOI: '::text || (dataset.identifier)::text) AS doi_number,
    link.link AS related_accessions
   FROM (dataset
   JOIN link ON ((dataset.id = link.dataset_id)));


ALTER TABLE public.show_accession OWNER TO gigadb;

--
-- TOC entry 257 (class 1259 OID 36543)
-- Name: show_externallink; Type: VIEW; Schema: public; Owner: gigadb
--

CREATE VIEW show_externallink AS
 SELECT ('DOI: '::text || (dataset.identifier)::text) AS doi_number,
    external_link.url AS additional_information
   FROM (dataset
   JOIN external_link ON ((dataset.id = external_link.dataset_id)));


ALTER TABLE public.show_externallink OWNER TO gigadb;

--
-- TOC entry 258 (class 1259 OID 36547)
-- Name: show_file; Type: VIEW; Schema: public; Owner: gigadb
--

CREATE VIEW show_file AS
 SELECT ('DOI: '::text || (dataset.identifier)::text) AS doi_number,
    file.name AS file_name
   FROM (dataset
   JOIN file ON ((dataset.id = file.dataset_id)));


ALTER TABLE public.show_file OWNER TO gigadb;

--
-- TOC entry 259 (class 1259 OID 36551)
-- Name: show_manuscript; Type: VIEW; Schema: public; Owner: gigadb
--

CREATE VIEW show_manuscript AS
 SELECT ('DOI: '::text || (dataset.identifier)::text) AS doi_number,
    manuscript.identifier AS related_manuscript
   FROM (dataset
   JOIN manuscript ON ((dataset.id = manuscript.dataset_id)));


ALTER TABLE public.show_manuscript OWNER TO gigadb;

--
-- TOC entry 260 (class 1259 OID 36555)
-- Name: show_project; Type: VIEW; Schema: public; Owner: gigadb
--

CREATE VIEW show_project AS
 SELECT ('DOI: '::text || (dataset.identifier)::text) AS doi_number,
    project.name AS project
   FROM ((dataset
   JOIN dataset_project ON ((dataset.id = dataset_project.dataset_id)))
   JOIN project ON ((dataset_project.project_id = project.id)));


ALTER TABLE public.show_project OWNER TO gigadb;

--
-- TOC entry 261 (class 1259 OID 36560)
-- Name: species; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE species (
    id integer NOT NULL,
    tax_id integer NOT NULL,
    common_name character varying(128),
    genbank_name character varying(128),
    scientific_name character varying(128) NOT NULL,
    eol_link character varying(100)
);


ALTER TABLE public.species OWNER TO gigadb;

--
-- TOC entry 262 (class 1259 OID 36563)
-- Name: species_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE species_id_seq
    START WITH 28
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.species_id_seq OWNER TO gigadb;

--
-- TOC entry 2813 (class 0 OID 0)
-- Dependencies: 262
-- Name: species_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE species_id_seq OWNED BY species.id;


--
-- TOC entry 263 (class 1259 OID 36565)
-- Name: type; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE type (
    id integer NOT NULL,
    name character varying(32) NOT NULL,
    description text DEFAULT ''::text NOT NULL
);


ALTER TABLE public.type OWNER TO gigadb;

--
-- TOC entry 264 (class 1259 OID 36572)
-- Name: type_id_seq; Type: SEQUENCE; Schema: public; Owner: gigadb
--

CREATE SEQUENCE type_id_seq
    START WITH 6
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER TABLE public.type_id_seq OWNER TO gigadb;

--
-- TOC entry 2814 (class 0 OID 0)
-- Dependencies: 264
-- Name: type_id_seq; Type: SEQUENCE OWNED BY; Schema: public; Owner: gigadb
--

ALTER SEQUENCE type_id_seq OWNED BY type.id;



CREATE TABLE curation_log (
  id serial NOT NULL,
  dataset_id integer NOT NULL,
  creation_date date,
  created_by character varying(100),
  last_modified_date date,
  last_modified_by character varying(100),
  action character varying(100),
  comments character varying(1000),
);

ALTER TABLE curation_log OWNER TO gigadb;

GRANT ALL ON TABLE curation_log TO gigadb;
GRANT ALL ON TABLE curation_log TO public;


--
-- TOC entry 265 (class 1259 OID 36574)
-- Name: unit; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE unit (
    id character varying(30) NOT NULL,
    name character varying(200),
    definition character varying(500)
);


ALTER TABLE public.unit OWNER TO gigadb;

--
-- TOC entry 2815 (class 0 OID 0)
-- Dependencies: 265
-- Name: COLUMN unit.id; Type: COMMENT; Schema: public; Owner: gigadb
--

COMMENT ON COLUMN unit.id IS 'the ID from the unit ontology';


--
-- TOC entry 2816 (class 0 OID 0)
-- Dependencies: 265
-- Name: COLUMN unit.name; Type: COMMENT; Schema: public; Owner: gigadb
--

COMMENT ON COLUMN unit.name IS 'the name of the unit (taken from the Unit Ontology)';


--
-- TOC entry 2817 (class 0 OID 0)
-- Dependencies: 265
-- Name: COLUMN unit.definition; Type: COMMENT; Schema: public; Owner: gigadb
--

COMMENT ON COLUMN unit.definition IS 'the inition taken from the unit ontology';


--
-- TOC entry 266 (class 1259 OID 36580)
-- Name: yiisession; Type: TABLE; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE TABLE yiisession (
    id character(32) NOT NULL,
    expire integer,
    data text
);


ALTER TABLE public.yiisession OWNER TO gigadb;

--
-- TOC entry 2412 (class 2604 OID 36586)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY alternative_identifiers ALTER COLUMN id SET DEFAULT nextval('alternative_identifiers_id_seq'::regclass);


--
-- TOC entry 2413 (class 2604 OID 36587)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY attribute ALTER COLUMN id SET DEFAULT nextval('attribute_id_seq'::regclass);


--
-- TOC entry 2414 (class 2604 OID 36588)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY author ALTER COLUMN id SET DEFAULT nextval('author_id_seq'::regclass);


--
-- TOC entry 2418 (class 2604 OID 36589)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset ALTER COLUMN id SET DEFAULT nextval('dataset_id_seq'::regclass);


--
-- TOC entry 2419 (class 2604 OID 36590)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_attributes ALTER COLUMN id SET DEFAULT nextval('dataset_attributes_id_seq'::regclass);


--
-- TOC entry 2421 (class 2604 OID 36591)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_author ALTER COLUMN id SET DEFAULT nextval('dataset_author_id_seq'::regclass);


--
-- TOC entry 2424 (class 2604 OID 36592)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_funder ALTER COLUMN id SET DEFAULT nextval('dataset_funder_id_seq'::regclass);


--
-- TOC entry 2428 (class 2604 OID 36593)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_log ALTER COLUMN id SET DEFAULT nextval('dataset_log_id_seq'::regclass);


--
-- TOC entry 2429 (class 2604 OID 36594)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_project ALTER COLUMN id SET DEFAULT nextval('dataset_project_id_seq'::regclass);


--
-- TOC entry 2430 (class 2604 OID 36595)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_sample ALTER COLUMN id SET DEFAULT nextval('dataset_sample_id_seq'::regclass);


--
-- TOC entry 2431 (class 2604 OID 36596)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_session ALTER COLUMN id SET DEFAULT nextval('dataset_session_id_seq'::regclass);


--
-- TOC entry 2432 (class 2604 OID 36597)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_type ALTER COLUMN id SET DEFAULT nextval('dataset_type_id_seq'::regclass);


--
-- TOC entry 2433 (class 2604 OID 36598)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY exp_attributes ALTER COLUMN id SET DEFAULT nextval('exp_attributes_id_seq'::regclass);


--
-- TOC entry 2434 (class 2604 OID 36599)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY experiment ALTER COLUMN id SET DEFAULT nextval('experiment_id_seq'::regclass);


--
-- TOC entry 2435 (class 2604 OID 36600)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY extdb ALTER COLUMN id SET DEFAULT nextval('extdb_id_seq'::regclass);


--
-- TOC entry 2436 (class 2604 OID 36601)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY external_link ALTER COLUMN id SET DEFAULT nextval('external_link_id_seq'::regclass);


--
-- TOC entry 2437 (class 2604 OID 36602)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY external_link_type ALTER COLUMN id SET DEFAULT nextval('external_link_type_id_seq'::regclass);


--
-- TOC entry 2441 (class 2604 OID 36603)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY file ALTER COLUMN id SET DEFAULT nextval('file_id_seq'::regclass);


--
-- TOC entry 2442 (class 2604 OID 36604)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY file_attributes ALTER COLUMN id SET DEFAULT nextval('file_attributes_id_seq'::regclass);


--
-- TOC entry 2443 (class 2604 OID 36605)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY file_experiment ALTER COLUMN id SET DEFAULT nextval('file_experiment_id_seq'::regclass);


--
-- TOC entry 2445 (class 2604 OID 36606)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY file_format ALTER COLUMN id SET DEFAULT nextval('file_format_id_seq'::regclass);


--
-- TOC entry 2446 (class 2604 OID 36607)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY file_relationship ALTER COLUMN id SET DEFAULT nextval('file_relationship_id_seq'::regclass);


--
-- TOC entry 2447 (class 2604 OID 36608)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY file_sample ALTER COLUMN id SET DEFAULT nextval('file_sample_id_seq'::regclass);


--
-- TOC entry 2449 (class 2604 OID 36609)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY file_type ALTER COLUMN id SET DEFAULT nextval('file_type_id_seq'::regclass);


--
-- TOC entry 2451 (class 2604 OID 36610)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY funder_name ALTER COLUMN id SET DEFAULT nextval('funder_name_id_seq'::regclass);


--
-- TOC entry 2457 (class 2604 OID 36611)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY gigadb_user ALTER COLUMN id SET DEFAULT nextval('gigadb_user_id_seq'::regclass);


--
-- TOC entry 2459 (class 2604 OID 36612)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY image ALTER COLUMN id SET DEFAULT nextval('image_id_seq'::regclass);


--
-- TOC entry 2461 (class 2604 OID 36613)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY link ALTER COLUMN id SET DEFAULT nextval('link_id_seq'::regclass);


--
-- TOC entry 2462 (class 2604 OID 36614)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY manuscript ALTER COLUMN id SET DEFAULT nextval('manuscript_id_seq'::regclass);


--
-- TOC entry 2464 (class 2604 OID 36615)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY news ALTER COLUMN id SET DEFAULT nextval('news_id_seq'::regclass);


--
-- TOC entry 2468 (class 2604 OID 36616)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY project ALTER COLUMN id SET DEFAULT nextval('project_id_seq'::regclass);


--
-- TOC entry 2470 (class 2604 OID 36617)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY publisher ALTER COLUMN id SET DEFAULT nextval('publisher_id_seq'::regclass);


--
-- TOC entry 2471 (class 2604 OID 36618)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY relation ALTER COLUMN id SET DEFAULT nextval('relation_id_seq'::regclass);


--
-- TOC entry 2472 (class 2604 OID 36619)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY relationship ALTER COLUMN id SET DEFAULT nextval('relationship_id_seq'::regclass);


--
-- TOC entry 2474 (class 2604 OID 36620)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY rss_message ALTER COLUMN id SET DEFAULT nextval('rss_message_id_seq'::regclass);


--
-- TOC entry 2476 (class 2604 OID 36621)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY sample ALTER COLUMN id SET DEFAULT nextval('sample_id_seq'::regclass);


--
-- TOC entry 2477 (class 2604 OID 36622)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY sample_attribute ALTER COLUMN id SET DEFAULT nextval('sample_attribute_id_seq'::regclass);


--
-- TOC entry 2478 (class 2604 OID 36623)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY sample_experiment ALTER COLUMN id SET DEFAULT nextval('sample_experiment_id_seq'::regclass);


--
-- TOC entry 2479 (class 2604 OID 36624)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY sample_rel ALTER COLUMN id SET DEFAULT nextval('sample_rel_id_seq'::regclass);


--
-- TOC entry 2481 (class 2604 OID 36625)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY search ALTER COLUMN id SET DEFAULT nextval('search_id_seq'::regclass);


--
-- TOC entry 2482 (class 2604 OID 36626)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY species ALTER COLUMN id SET DEFAULT nextval('species_id_seq'::regclass);


--
-- TOC entry 2484 (class 2604 OID 36627)
-- Name: id; Type: DEFAULT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY type ALTER COLUMN id SET DEFAULT nextval('type_id_seq'::regclass);


--
-- TOC entry 2486 (class 2606 OID 36635)
-- Name: AuthAssignment_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY "AuthAssignment"
    ADD CONSTRAINT "AuthAssignment_pkey" PRIMARY KEY (itemname, userid);


--
-- TOC entry 2488 (class 2606 OID 36637)
-- Name: AuthItem_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY "AuthItem"
    ADD CONSTRAINT "AuthItem_pkey" PRIMARY KEY (name);


--
-- TOC entry 2490 (class 2606 OID 36639)
-- Name: YiiSession_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY "YiiSession"
    ADD CONSTRAINT "YiiSession_pkey" PRIMARY KEY (id);


ALTER TABLE ONLY curation_log
	ADD CONSTRAINT curation_log_pkey PRIMARY KEY (id);
	
 
--
-- TOC entry 2492 (class 2606 OID 36641)
-- Name: alternative_identifiers_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY alternative_identifiers
    ADD CONSTRAINT alternative_identifiers_pkey PRIMARY KEY (id);


--
-- TOC entry 2494 (class 2606 OID 36643)
-- Name: attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY attribute
    ADD CONSTRAINT attribute_pkey PRIMARY KEY (id);


--
-- TOC entry 2496 (class 2606 OID 36645)
-- Name: author_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY author
    ADD CONSTRAINT author_pkey PRIMARY KEY (id);


--
-- TOC entry 2501 (class 2606 OID 36647)
-- Name: dataset_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY dataset_attributes
    ADD CONSTRAINT dataset_attributes_pkey PRIMARY KEY (id);


--
-- TOC entry 2503 (class 2606 OID 36649)
-- Name: dataset_author_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY dataset_author
    ADD CONSTRAINT dataset_author_pkey PRIMARY KEY (id);


--
-- TOC entry 2505 (class 2606 OID 36651)
-- Name: dataset_funder_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY dataset_funder
    ADD CONSTRAINT dataset_funder_pkey PRIMARY KEY (id);


--
-- TOC entry 2509 (class 2606 OID 36653)
-- Name: dataset_log_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY dataset_log
    ADD CONSTRAINT dataset_log_pkey PRIMARY KEY (id);


--
-- TOC entry 2498 (class 2606 OID 36655)
-- Name: dataset_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY dataset
    ADD CONSTRAINT dataset_pkey PRIMARY KEY (id);


--
-- TOC entry 2511 (class 2606 OID 36657)
-- Name: dataset_project_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY dataset_project
    ADD CONSTRAINT dataset_project_pkey PRIMARY KEY (id);


--
-- TOC entry 2513 (class 2606 OID 36659)
-- Name: dataset_sample_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY dataset_sample
    ADD CONSTRAINT dataset_sample_pkey PRIMARY KEY (id);


--
-- TOC entry 2515 (class 2606 OID 36661)
-- Name: dataset_session_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY dataset_session
    ADD CONSTRAINT dataset_session_pkey PRIMARY KEY (id);


--
-- TOC entry 2517 (class 2606 OID 36663)
-- Name: dataset_type_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY dataset_type
    ADD CONSTRAINT dataset_type_pkey PRIMARY KEY (id);


--
-- TOC entry 2545 (class 2606 OID 36665)
-- Name: email_unique; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY gigadb_user
    ADD CONSTRAINT email_unique UNIQUE (email);


--
-- TOC entry 2519 (class 2606 OID 36667)
-- Name: exp_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY exp_attributes
    ADD CONSTRAINT exp_attributes_pkey PRIMARY KEY (id);


--
-- TOC entry 2521 (class 2606 OID 36669)
-- Name: experiment_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY experiment
    ADD CONSTRAINT experiment_pkey PRIMARY KEY (id);


--
-- TOC entry 2523 (class 2606 OID 36671)
-- Name: extdb_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY extdb
    ADD CONSTRAINT extdb_pkey PRIMARY KEY (id);


--
-- TOC entry 2525 (class 2606 OID 36673)
-- Name: external_link_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY external_link
    ADD CONSTRAINT external_link_pkey PRIMARY KEY (id);


--
-- TOC entry 2527 (class 2606 OID 36675)
-- Name: external_link_type_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY external_link_type
    ADD CONSTRAINT external_link_type_pkey PRIMARY KEY (id);


--
-- TOC entry 2531 (class 2606 OID 36677)
-- Name: file_attributes_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY file_attributes
    ADD CONSTRAINT file_attributes_pkey PRIMARY KEY (id);


--
-- TOC entry 2533 (class 2606 OID 36679)
-- Name: file_experiment_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY file_experiment
    ADD CONSTRAINT file_experiment_pkey PRIMARY KEY (id);


--
-- TOC entry 2535 (class 2606 OID 36681)
-- Name: file_format_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY file_format
    ADD CONSTRAINT file_format_pkey PRIMARY KEY (id);


--
-- TOC entry 2529 (class 2606 OID 36683)
-- Name: file_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY file
    ADD CONSTRAINT file_pkey PRIMARY KEY (id);


--
-- TOC entry 2537 (class 2606 OID 36685)
-- Name: file_relationship_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY file_relationship
    ADD CONSTRAINT file_relationship_pkey PRIMARY KEY (id);


--
-- TOC entry 2539 (class 2606 OID 36687)
-- Name: file_sample_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY file_sample
    ADD CONSTRAINT file_sample_pkey PRIMARY KEY (id);


--
-- TOC entry 2541 (class 2606 OID 36689)
-- Name: file_type_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY file_type
    ADD CONSTRAINT file_type_pkey PRIMARY KEY (id);


--
-- TOC entry 2543 (class 2606 OID 36691)
-- Name: funder_name_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY funder_name
    ADD CONSTRAINT funder_name_pkey PRIMARY KEY (id);


--
-- TOC entry 2547 (class 2606 OID 36693)
-- Name: gigadb_user_facebook_id_key; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY gigadb_user
    ADD CONSTRAINT gigadb_user_facebook_id_key UNIQUE (facebook_id);


--
-- TOC entry 2549 (class 2606 OID 36695)
-- Name: gigadb_user_google_id_key; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY gigadb_user
    ADD CONSTRAINT gigadb_user_google_id_key UNIQUE (google_id);


--
-- TOC entry 2551 (class 2606 OID 36697)
-- Name: gigadb_user_linked_id_key; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY gigadb_user
    ADD CONSTRAINT gigadb_user_linked_id_key UNIQUE (linkedin_id);


--
-- TOC entry 2553 (class 2606 OID 36699)
-- Name: gigadb_user_orcid_id_key; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY gigadb_user
    ADD CONSTRAINT gigadb_user_orcid_id_key UNIQUE (orcid_id);


--
-- TOC entry 2555 (class 2606 OID 36701)
-- Name: gigadb_user_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY gigadb_user
    ADD CONSTRAINT gigadb_user_pkey PRIMARY KEY (id);


--
-- TOC entry 2557 (class 2606 OID 36703)
-- Name: gigadb_user_twitter_id_key; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY gigadb_user
    ADD CONSTRAINT gigadb_user_twitter_id_key UNIQUE (twitter_id);


--
-- TOC entry 2559 (class 2606 OID 36705)
-- Name: gigadb_user_username_key; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY gigadb_user
    ADD CONSTRAINT gigadb_user_username_key UNIQUE (username);


--
-- TOC entry 2561 (class 2606 OID 36707)
-- Name: image_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY image
    ADD CONSTRAINT image_pkey PRIMARY KEY (id);


--
-- TOC entry 2563 (class 2606 OID 36709)
-- Name: link_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY link
    ADD CONSTRAINT link_pkey PRIMARY KEY (id);


--
-- TOC entry 2569 (class 2606 OID 36711)
-- Name: link_prefix_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY prefix
    ADD CONSTRAINT link_prefix_pkey PRIMARY KEY (id);


--
-- TOC entry 2565 (class 2606 OID 36713)
-- Name: manuscript_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY manuscript
    ADD CONSTRAINT manuscript_pkey PRIMARY KEY (id);


--
-- TOC entry 2567 (class 2606 OID 36715)
-- Name: news_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY news
    ADD CONSTRAINT news_pkey PRIMARY KEY (id);


--
-- TOC entry 2571 (class 2606 OID 36717)
-- Name: project_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY project
    ADD CONSTRAINT project_pkey PRIMARY KEY (id);


--
-- TOC entry 2573 (class 2606 OID 36719)
-- Name: publisher_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY publisher
    ADD CONSTRAINT publisher_pkey PRIMARY KEY (id);


--
-- TOC entry 2575 (class 2606 OID 36721)
-- Name: relation_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY relation
    ADD CONSTRAINT relation_pkey PRIMARY KEY (id);


--
-- TOC entry 2577 (class 2606 OID 36723)
-- Name: relationship_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY relationship
    ADD CONSTRAINT relationship_pkey PRIMARY KEY (id);


--
-- TOC entry 2579 (class 2606 OID 36725)
-- Name: rss_message_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY rss_message
    ADD CONSTRAINT rss_message_pkey PRIMARY KEY (id);


--
-- TOC entry 2584 (class 2606 OID 36727)
-- Name: sample_attribute_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY sample_attribute
    ADD CONSTRAINT sample_attribute_pkey PRIMARY KEY (id);


--
-- TOC entry 2586 (class 2606 OID 36729)
-- Name: sample_experiment_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY sample_experiment
    ADD CONSTRAINT sample_experiment_pkey PRIMARY KEY (id);


--
-- TOC entry 2581 (class 2606 OID 36731)
-- Name: sample_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY sample
    ADD CONSTRAINT sample_pkey PRIMARY KEY (id);


--
-- TOC entry 2588 (class 2606 OID 36733)
-- Name: sample_rel_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY sample_rel
    ADD CONSTRAINT sample_rel_pkey PRIMARY KEY (id);


--
-- TOC entry 2590 (class 2606 OID 36735)
-- Name: search_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY search
    ADD CONSTRAINT search_pkey PRIMARY KEY (id);


--
-- TOC entry 2592 (class 2606 OID 36737)
-- Name: species_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY species
    ADD CONSTRAINT species_pkey PRIMARY KEY (id);


--
-- TOC entry 2594 (class 2606 OID 36739)
-- Name: type_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY type
    ADD CONSTRAINT type_pkey PRIMARY KEY (id);


--
-- TOC entry 2507 (class 2606 OID 36741)
-- Name: un_dataset_funder; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY dataset_funder
    ADD CONSTRAINT un_dataset_funder UNIQUE (dataset_id, funder_id);


--
-- TOC entry 2596 (class 2606 OID 36743)
-- Name: unit_pkey; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY unit
    ADD CONSTRAINT unit_pkey PRIMARY KEY (id);


--
-- TOC entry 2598 (class 2606 OID 36745)
-- Name: yiisession_pkey1; Type: CONSTRAINT; Schema: public; Owner: gigadb; Tablespace: 
--

ALTER TABLE ONLY yiisession
    ADD CONSTRAINT yiisession_pkey1 PRIMARY KEY (id);


--
-- TOC entry 2582 (class 1259 OID 36746)
-- Name: fki_sample_attribute_fkey; Type: INDEX; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE INDEX fki_sample_attribute_fkey ON sample_attribute USING btree (attribute_id);


--
-- TOC entry 2499 (class 1259 OID 36747)
-- Name: identifier_idx; Type: INDEX; Schema: public; Owner: gigadb; Tablespace: 
--

CREATE UNIQUE INDEX identifier_idx ON dataset USING btree (identifier);


--
-- TOC entry 2599 (class 2606 OID 36748)
-- Name: AuthAssignment_itemname_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY "AuthAssignment"
    ADD CONSTRAINT "AuthAssignment_itemname_fkey" FOREIGN KEY (itemname) REFERENCES "AuthItem"(name) ON UPDATE CASCADE ON DELETE CASCADE;


ALTER TABLE ONLY curation_log
	ADD CONSTRAINT curation_log_dataset_id_fkey FOREIGN KEY (dataset_id) REFERENCES dataset (id) ON UPDATE NO ACTION ON DELETE CASCADE;
--
-- TOC entry 2600 (class 2606 OID 36753)
-- Name: alternative_identifiers_extdb_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY alternative_identifiers
    ADD CONSTRAINT alternative_identifiers_extdb_id_fkey FOREIGN KEY (extdb_id) REFERENCES extdb(id);


--
-- TOC entry 2601 (class 2606 OID 36758)
-- Name: alternative_identifiers_sample_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY alternative_identifiers
    ADD CONSTRAINT alternative_identifiers_sample_id_fkey FOREIGN KEY (sample_id) REFERENCES sample(id);


--
-- TOC entry 2605 (class 2606 OID 36763)
-- Name: dataset_attributes_attribute_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_attributes
    ADD CONSTRAINT dataset_attributes_attribute_id_fkey FOREIGN KEY (attribute_id) REFERENCES attribute(id);


--
-- TOC entry 2606 (class 2606 OID 36768)
-- Name: dataset_attributes_dataset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_attributes
    ADD CONSTRAINT dataset_attributes_dataset_id_fkey FOREIGN KEY (dataset_id) REFERENCES dataset(id);


--
-- TOC entry 2607 (class 2606 OID 36773)
-- Name: dataset_attributes_units_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_attributes
    ADD CONSTRAINT dataset_attributes_units_id_fkey FOREIGN KEY (units_id) REFERENCES unit(id);


--
-- TOC entry 2608 (class 2606 OID 36778)
-- Name: dataset_author_author_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_author
    ADD CONSTRAINT dataset_author_author_id_fkey FOREIGN KEY (author_id) REFERENCES author(id) ON DELETE CASCADE;


--
-- TOC entry 2609 (class 2606 OID 36783)
-- Name: dataset_author_dataset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_author
    ADD CONSTRAINT dataset_author_dataset_id_fkey FOREIGN KEY (dataset_id) REFERENCES dataset(id) ON DELETE CASCADE;


--
-- TOC entry 2610 (class 2606 OID 36788)
-- Name: dataset_funder_dataset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_funder
    ADD CONSTRAINT dataset_funder_dataset_id_fkey FOREIGN KEY (dataset_id) REFERENCES dataset(id) ON DELETE CASCADE;


--
-- TOC entry 2611 (class 2606 OID 36793)
-- Name: dataset_funder_funder_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_funder
    ADD CONSTRAINT dataset_funder_funder_id_fkey FOREIGN KEY (funder_id) REFERENCES funder_name(id) ON DELETE CASCADE;


--
-- TOC entry 2602 (class 2606 OID 36798)
-- Name: dataset_image_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset
    ADD CONSTRAINT dataset_image_id_fkey FOREIGN KEY (image_id) REFERENCES image(id) ON DELETE SET NULL;


--
-- TOC entry 2612 (class 2606 OID 36803)
-- Name: dataset_log_dataset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_log
    ADD CONSTRAINT dataset_log_dataset_id_fkey FOREIGN KEY (dataset_id) REFERENCES dataset(id) ON DELETE CASCADE;


--
-- TOC entry 2613 (class 2606 OID 36808)
-- Name: dataset_project_dataset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_project
    ADD CONSTRAINT dataset_project_dataset_id_fkey FOREIGN KEY (dataset_id) REFERENCES dataset(id) ON DELETE CASCADE;


--
-- TOC entry 2614 (class 2606 OID 36813)
-- Name: dataset_project_project_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_project
    ADD CONSTRAINT dataset_project_project_id_fkey FOREIGN KEY (project_id) REFERENCES project(id) ON DELETE CASCADE;


--
-- TOC entry 2603 (class 2606 OID 36818)
-- Name: dataset_publisher_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset
    ADD CONSTRAINT dataset_publisher_id_fkey FOREIGN KEY (publisher_id) REFERENCES publisher(id) ON DELETE SET NULL;


--
-- TOC entry 2615 (class 2606 OID 36823)
-- Name: dataset_sample_dataset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_sample
    ADD CONSTRAINT dataset_sample_dataset_id_fkey FOREIGN KEY (dataset_id) REFERENCES dataset(id) ON DELETE CASCADE;


--
-- TOC entry 2616 (class 2606 OID 36828)
-- Name: dataset_sample_sample_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_sample
    ADD CONSTRAINT dataset_sample_sample_id_fkey FOREIGN KEY (sample_id) REFERENCES sample(id) ON DELETE CASCADE;


--
-- TOC entry 2604 (class 2606 OID 36833)
-- Name: dataset_submitter_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset
    ADD CONSTRAINT dataset_submitter_id_fkey FOREIGN KEY (submitter_id) REFERENCES gigadb_user(id) ON DELETE RESTRICT;


--
-- TOC entry 2617 (class 2606 OID 36838)
-- Name: dataset_type_dataset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_type
    ADD CONSTRAINT dataset_type_dataset_id_fkey FOREIGN KEY (dataset_id) REFERENCES dataset(id) ON DELETE CASCADE;


--
-- TOC entry 2618 (class 2606 OID 36843)
-- Name: dataset_type_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY dataset_type
    ADD CONSTRAINT dataset_type_type_id_fkey FOREIGN KEY (type_id) REFERENCES type(id) ON DELETE CASCADE;


--
-- TOC entry 2619 (class 2606 OID 36848)
-- Name: exp_attributes_attribute_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY exp_attributes
    ADD CONSTRAINT exp_attributes_attribute_id_fkey FOREIGN KEY (attribute_id) REFERENCES attribute(id);


--
-- TOC entry 2620 (class 2606 OID 36853)
-- Name: exp_attributes_exp_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY exp_attributes
    ADD CONSTRAINT exp_attributes_exp_id_fkey FOREIGN KEY (exp_id) REFERENCES experiment(id);


--
-- TOC entry 2621 (class 2606 OID 36858)
-- Name: exp_attributes_units_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY exp_attributes
    ADD CONSTRAINT exp_attributes_units_id_fkey FOREIGN KEY (units_id) REFERENCES unit(id);


--
-- TOC entry 2622 (class 2606 OID 36863)
-- Name: experiment_dataset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY experiment
    ADD CONSTRAINT experiment_dataset_id_fkey FOREIGN KEY (dataset_id) REFERENCES dataset(id);


--
-- TOC entry 2623 (class 2606 OID 36868)
-- Name: external_link_dataset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY external_link
    ADD CONSTRAINT external_link_dataset_id_fkey FOREIGN KEY (dataset_id) REFERENCES dataset(id) ON DELETE CASCADE;


--
-- TOC entry 2624 (class 2606 OID 36873)
-- Name: external_link_external_link_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY external_link
    ADD CONSTRAINT external_link_external_link_type_id_fkey FOREIGN KEY (external_link_type_id) REFERENCES external_link_type(id) ON DELETE CASCADE;


--
-- TOC entry 2628 (class 2606 OID 36878)
-- Name: file_attributes_attribute_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY file_attributes
    ADD CONSTRAINT file_attributes_attribute_id_fkey FOREIGN KEY (attribute_id) REFERENCES attribute(id);


--
-- TOC entry 2629 (class 2606 OID 36883)
-- Name: file_attributes_file_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY file_attributes
    ADD CONSTRAINT file_attributes_file_id_fkey FOREIGN KEY (file_id) REFERENCES file(id);


--
-- TOC entry 2630 (class 2606 OID 36888)
-- Name: file_attributes_unit_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY file_attributes
    ADD CONSTRAINT file_attributes_unit_id_fkey FOREIGN KEY (unit_id) REFERENCES unit(id);


--
-- TOC entry 2625 (class 2606 OID 36893)
-- Name: file_dataset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY file
    ADD CONSTRAINT file_dataset_id_fkey FOREIGN KEY (dataset_id) REFERENCES dataset(id) ON DELETE CASCADE;


--
-- TOC entry 2631 (class 2606 OID 36898)
-- Name: file_experiment_experiment_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY file_experiment
    ADD CONSTRAINT file_experiment_experiment_id_fkey FOREIGN KEY (experiment_id) REFERENCES experiment(id);


--
-- TOC entry 2632 (class 2606 OID 36903)
-- Name: file_experiment_file_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY file_experiment
    ADD CONSTRAINT file_experiment_file_id_fkey FOREIGN KEY (file_id) REFERENCES file(id);


--
-- TOC entry 2626 (class 2606 OID 36908)
-- Name: file_format_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY file
    ADD CONSTRAINT file_format_id_fkey FOREIGN KEY (format_id) REFERENCES file_format(id) ON DELETE CASCADE;


--
-- TOC entry 2633 (class 2606 OID 36913)
-- Name: file_relationship_file_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY file_relationship
    ADD CONSTRAINT file_relationship_file_id_fkey FOREIGN KEY (file_id) REFERENCES file(id);


--
-- TOC entry 2634 (class 2606 OID 36918)
-- Name: file_relationship_relationship_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY file_relationship
    ADD CONSTRAINT file_relationship_relationship_id_fkey FOREIGN KEY (relationship_id) REFERENCES relationship(id);


--
-- TOC entry 2635 (class 2606 OID 36923)
-- Name: file_sample_file_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY file_sample
    ADD CONSTRAINT file_sample_file_id_fkey FOREIGN KEY (file_id) REFERENCES file(id);


--
-- TOC entry 2636 (class 2606 OID 36928)
-- Name: file_sample_sample_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY file_sample
    ADD CONSTRAINT file_sample_sample_id_fkey FOREIGN KEY (sample_id) REFERENCES sample(id);


--
-- TOC entry 2627 (class 2606 OID 36933)
-- Name: file_type_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY file
    ADD CONSTRAINT file_type_id_fkey FOREIGN KEY (type_id) REFERENCES file_type(id) ON DELETE CASCADE;


--
-- TOC entry 2637 (class 2606 OID 36938)
-- Name: link_dataset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY link
    ADD CONSTRAINT link_dataset_id_fkey FOREIGN KEY (dataset_id) REFERENCES dataset(id) ON DELETE CASCADE;


--
-- TOC entry 2638 (class 2606 OID 36943)
-- Name: manuscript_dataset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY manuscript
    ADD CONSTRAINT manuscript_dataset_id_fkey FOREIGN KEY (dataset_id) REFERENCES dataset(id) ON DELETE CASCADE;


--
-- TOC entry 2639 (class 2606 OID 36948)
-- Name: relation_dataset_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY relation
    ADD CONSTRAINT relation_dataset_id_fkey FOREIGN KEY (dataset_id) REFERENCES dataset(id) ON DELETE CASCADE;


--
-- TOC entry 2640 (class 2606 OID 36953)
-- Name: relation_relationship_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY relation
    ADD CONSTRAINT relation_relationship_fkey FOREIGN KEY (relationship_id) REFERENCES relationship(id);


--
-- TOC entry 2643 (class 2606 OID 36958)
-- Name: sample_attribute_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY sample_attribute
    ADD CONSTRAINT sample_attribute_fkey FOREIGN KEY (attribute_id) REFERENCES attribute(id);


--
-- TOC entry 2644 (class 2606 OID 36963)
-- Name: sample_attribute_unit_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY sample_attribute
    ADD CONSTRAINT sample_attribute_unit_id_fkey FOREIGN KEY (unit_id) REFERENCES unit(id);


--
-- TOC entry 2645 (class 2606 OID 36968)
-- Name: sample_experiment_experiment_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY sample_experiment
    ADD CONSTRAINT sample_experiment_experiment_id_fkey FOREIGN KEY (experiment_id) REFERENCES experiment(id);


--
-- TOC entry 2646 (class 2606 OID 36973)
-- Name: sample_experiment_sample_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY sample_experiment
    ADD CONSTRAINT sample_experiment_sample_id_fkey FOREIGN KEY (sample_id) REFERENCES sample(id);


--
-- TOC entry 2647 (class 2606 OID 36978)
-- Name: sample_rel_relationship_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY sample_rel
    ADD CONSTRAINT sample_rel_relationship_id_fkey FOREIGN KEY (relationship_id) REFERENCES relationship(id);


--
-- TOC entry 2648 (class 2606 OID 36983)
-- Name: sample_rel_sample_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY sample_rel
    ADD CONSTRAINT sample_rel_sample_id_fkey FOREIGN KEY (sample_id) REFERENCES sample(id);


--
-- TOC entry 2641 (class 2606 OID 36988)
-- Name: sample_species_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY sample
    ADD CONSTRAINT sample_species_id_fkey FOREIGN KEY (species_id) REFERENCES species(id) ON DELETE CASCADE;


--
-- TOC entry 2642 (class 2606 OID 36993)
-- Name: sample_submitted_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY sample
    ADD CONSTRAINT sample_submitted_id_fkey FOREIGN KEY (submitted_id) REFERENCES gigadb_user(id);


--
-- TOC entry 2649 (class 2606 OID 36998)
-- Name: search_user_id_fkey; Type: FK CONSTRAINT; Schema: public; Owner: gigadb
--

ALTER TABLE ONLY search
    ADD CONSTRAINT search_user_id_fkey FOREIGN KEY (user_id) REFERENCES gigadb_user(id) ON DELETE RESTRICT;


--
-- TOC entry 2768 (class 0 OID 0)
-- Dependencies: 6
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- TOC entry 2781 (class 0 OID 0)
-- Dependencies: 193
-- Name: dataset_session; Type: ACL; Schema: public; Owner: gigadb
--

REVOKE ALL ON TABLE dataset_session FROM PUBLIC;
REVOKE ALL ON TABLE dataset_session FROM gigadb;
GRANT ALL ON TABLE dataset_session TO gigadb;


--
-- TOC entry 2802 (class 0 OID 0)
-- Dependencies: 234
-- Name: prefix; Type: ACL; Schema: public; Owner: gigadb
--

REVOKE ALL ON TABLE prefix FROM PUBLIC;
REVOKE ALL ON TABLE prefix FROM gigadb;
GRANT ALL ON TABLE prefix TO gigadb;
GRANT ALL ON TABLE prefix TO PUBLIC;


-- Completed on 2015-04-01 10:07:42 HKT

--
-- PostgreSQL database dump complete
--

