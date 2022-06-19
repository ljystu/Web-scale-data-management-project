#!/bin/sh
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
	CREATE DATABASE "Stock";
	GRANT ALL PRIVILEGES ON DATABASE "Stock" TO postgres;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "Stock" <<-EOSQL
CREATE TABLE   "stock"
(
    itemid bigint NOT NULL,
    price real,
    amount integer DEFAULT 0,
    CONSTRAINT stocks_pkey PRIMARY KEY (itemid),
    CONSTRAINT "amountCheck" CHECK (amount >= 0)
) PARTITION BY HASH (itemid);



-- Partitions SQL

CREATE TABLE "stock1" PARTITION OF "stock"
    FOR VALUES WITH (modulus 4, remainder 0);

ALTER TABLE IF EXISTS "stock1"
    OWNER to postgres;
CREATE TABLE "stock2" PARTITION OF "stock"
    FOR VALUES WITH (modulus 4, remainder 1);

ALTER TABLE IF EXISTS "stock2"
    OWNER to postgres;
CREATE TABLE "stock3" PARTITION OF "stock"
    FOR VALUES WITH (modulus 4, remainder 2);

ALTER TABLE IF EXISTS "stock3"
    OWNER to postgres;
CREATE TABLE "stock4" PARTITION OF "stock"
    FOR VALUES WITH (modulus 4, remainder 3);

ALTER TABLE IF EXISTS "stock4"
    OWNER to postgres;

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
    CONSTRAINT pk_undo_log_5 PRIMARY KEY (id),
    CONSTRAINT ux_undo_log_6 UNIQUE (xid, branch_id)
);

CREATE SEQUENCE IF NOT EXISTS undo_log_id_seq_4 INCREMENT BY 1 MINVALUE 1 ;

ALTER TABLE IF EXISTS "undo_log"
    OWNER to postgres;
EOSQL
