#!/bin/sh
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
	CREATE DATABASE "Payment";
	GRANT ALL PRIVILEGES ON DATABASE "Payment" TO postgres;
EOSQL


psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "Payment" <<-EOSQL
CREATE TABLE   "payment"
(
    userid bigint NOT NULL,
    credit double precision DEFAULT 0,
    CONSTRAINT payment_p_pkey PRIMARY KEY (userid),
    CONSTRAINT creditcheck CHECK (credit >= 0::double precision) NOT VALID
) PARTITION BY HASH (userid);



-- Partitions SQL

CREATE TABLE "payment1" PARTITION OF "payment"
    FOR VALUES WITH (modulus 4, remainder 0);

ALTER TABLE IF EXISTS "payment1"
    OWNER to postgres;
CREATE TABLE "payment2" PARTITION OF "payment"
    FOR VALUES WITH (modulus 4, remainder 1);

ALTER TABLE IF EXISTS "payment2"
    OWNER to postgres;
CREATE TABLE "payment3" PARTITION OF "payment"
    FOR VALUES WITH (modulus 4, remainder 2);

ALTER TABLE IF EXISTS "payment3"
    OWNER to postgres;
CREATE TABLE "payment4" PARTITION OF "payment"
    FOR VALUES WITH (modulus 4, remainder 3);



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
    CONSTRAINT pk_undo_log_1 PRIMARY KEY (id),
    CONSTRAINT ux_undo_log_2 UNIQUE (xid, branch_id)
);

CREATE SEQUENCE IF NOT EXISTS undo_log_id_seq_1 INCREMENT BY 1 MINVALUE 1;
EOSQL
