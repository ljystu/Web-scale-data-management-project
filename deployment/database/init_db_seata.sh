#!/bin/sh
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
	CREATE DATABASE "seata";
	GRANT ALL PRIVILEGES ON DATABASE "seata" TO postgres;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "seata" <<-EOSQL
CREATE TABLE   "distributed_lock"
(
    lock_key character varying(20) COLLATE pg_catalog."default" NOT NULL,
    lock_value character varying(20) COLLATE pg_catalog."default" NOT NULL,
    expire bigint NOT NULL,
    CONSTRAINT pk_distributed_lock_table PRIMARY KEY (lock_key)
);



-- Table: branch_table

-- DROP TABLE IF EXISTS branch_table;

CREATE TABLE "branch_table"
(
    branch_id bigint NOT NULL,
    xid character varying(128) COLLATE pg_catalog."default" NOT NULL,
    transaction_id bigint,
    resource_group_id character varying(32) COLLATE pg_catalog."default",
    resource_id character varying(256) COLLATE pg_catalog."default",
    branch_type character varying(8) COLLATE pg_catalog."default",
    status smallint,
    client_id character varying(64) COLLATE pg_catalog."default",
    application_data character varying(2000) COLLATE pg_catalog."default",
    gmt_create timestamp(6) without time zone,
    gmt_modified timestamp(6) without time zone,
    CONSTRAINT pk_branch_table PRIMARY KEY (branch_id)
);



-- ALTER TABLE IF EXISTS "branch_table"
--     OWNER to postgres;
-- Index: idx_xid

-- DROP INDEX IF EXISTS idx_xid;

CREATE INDEX   "idx_xid"
    ON "branch_table" USING btree
    (xid COLLATE pg_catalog."default" ASC NULLS LAST)
    TABLESPACE pg_default;

-- Table: undo_log

-- DROP TABLE IF EXISTS undo_log;

CREATE TABLE   "undo_log"
(
    id            SERIAL       NOT NULL,
    branch_id     BIGINT       NOT NULL,
    xid           VARCHAR(128) NOT NULL,
    context       VARCHAR(128) NOT NULL,
    rollback_info BYTEA        NOT NULL,
    log_status    INT          NOT NULL,
    log_created   TIMESTAMP(0) NOT NULL,
    log_modified  TIMESTAMP(0) NOT NULL,
    CONSTRAINT pk_undo_log_3 PRIMARY KEY (id),
    CONSTRAINT ux_undo_log_4 UNIQUE (xid, branch_id)
);

CREATE SEQUENCE IF NOT EXISTS undo_log_id_seq_2 INCREMENT BY 1 MINVALUE 1 ;

-- ALTER TABLE IF EXISTS "undo_log"
--     OWNER to postgres;

-- Table: global_table

-- DROP TABLE IF EXISTS global_table;

CREATE TABLE   "global_table"
(
    xid character varying(128) COLLATE pg_catalog."default" NOT NULL,
    transaction_id bigint,
    status smallint NOT NULL,
    application_id character varying(32) COLLATE pg_catalog."default",
    transaction_service_group character varying(32) COLLATE pg_catalog."default",
    transaction_name character varying(128) COLLATE pg_catalog."default",
    timeout integer,
    begin_time bigint,
    application_data character varying(2000) COLLATE pg_catalog."default",
    gmt_create timestamp(0) without time zone,
    gmt_modified timestamp(0) without time zone,
    CONSTRAINT pk_global_table PRIMARY KEY (xid)
);


ALTER TABLE IF EXISTS "global_table"
    OWNER to postgres;
-- Index: idx_status_gmt_modified

-- DROP INDEX IF EXISTS idx_status_gmt_modified;

CREATE INDEX   "idx_status_gmt_modified"
    ON "global_table" USING btree
    (status ASC NULLS LAST, gmt_modified ASC NULLS LAST)
    TABLESPACE pg_default;
-- Index: idx_transaction_id

-- DROP INDEX IF EXISTS idx_transaction_id;

CREATE INDEX   "idx_transaction_id"
    ON "global_table" USING btree
    (transaction_id ASC NULLS LAST)
    TABLESPACE pg_default;
    
-- Table: lock_table

-- DROP TABLE IF EXISTS lock_table;

CREATE TABLE   "lock_table"
(
    row_key character varying(128) COLLATE pg_catalog."default" NOT NULL,
    xid character varying(128) COLLATE pg_catalog."default",
    transaction_id bigint,
    branch_id bigint NOT NULL,
    resource_id character varying(256) COLLATE pg_catalog."default",
    table_name character varying(32) COLLATE pg_catalog."default",
    pk character varying(36) COLLATE pg_catalog."default",
    status smallint NOT NULL DEFAULT 0,
    gmt_create timestamp(0) without time zone,
    gmt_modified timestamp(0) without time zone,
    CONSTRAINT pk_lock_table PRIMARY KEY (row_key)
);



-- ALTER TABLE IF EXISTS "lock_table"
--     OWNER to postgres;

COMMENT ON COLUMN "lock_table".status
    IS '0:locked ,1:rollbacking';
-- Index: idx_branch_id

-- DROP INDEX IF EXISTS idx_branch_id;

CREATE INDEX   "idx_branch_id"
    ON "lock_table" USING btree
    (branch_id ASC NULLS LAST)
    TABLESPACE pg_default;
-- Index: idx_status

-- DROP INDEX IF EXISTS idx_status;

CREATE INDEX   "idx_status"
    ON "lock_table" USING btree
    (status ASC NULLS LAST)
    TABLESPACE pg_default;
-- Index: idx_xid_and_branch_id

-- DROP INDEX IF EXISTS idx_xid_and_branch_id;

CREATE INDEX   "idx_xid_and_branch_id"
    ON "lock_table" USING btree
    (xid COLLATE pg_catalog."default" ASC NULLS LAST, branch_id ASC NULLS LAST)
    TABLESPACE pg_default;
EOSQL
