#!/bin/sh
set -e

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "$POSTGRES_DB" <<-EOSQL
	CREATE DATABASE "Order";
	GRANT ALL PRIVILEGES ON DATABASE "Order" TO postgres;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" --dbname "Order" <<-EOSQL
CREATE TABLE "orderinfo"
(
    orderid bigint NOT NULL,
    userid bigint,
    paid boolean,
    CONSTRAINT orderinfos_pkey PRIMARY KEY (orderid)
) PARTITION BY HASH (orderid);

ALTER TABLE IF EXISTS "orderinfo"
    OWNER to postgres;

-- Partitions SQL

CREATE TABLE "orderinfo1" PARTITION OF "orderinfo"
    FOR VALUES WITH (modulus 4, remainder 0);

ALTER TABLE IF EXISTS "orderinfo1"
    OWNER to postgres;
CREATE TABLE "orderinfo2" PARTITION OF "orderinfo"
    FOR VALUES WITH (modulus 4, remainder 1);

ALTER TABLE IF EXISTS "orderinfo2"
    OWNER to postgres;
CREATE TABLE "orderinfo3" PARTITION OF "orderinfo"
    FOR VALUES WITH (modulus 4, remainder 2);

ALTER TABLE IF EXISTS "orderinfo3"
    OWNER to postgres;
CREATE TABLE "orderinfo4" PARTITION OF "orderinfo"
    FOR VALUES WITH (modulus 4, remainder 3);

ALTER TABLE IF EXISTS "orderinfo4"
    OWNER to postgres;

-- Table: orderitem

-- DROP TABLE IF EXISTS orderitem;

CREATE TABLE "orderitem"
(
    id bigint NOT NULL,
    itemid bigint,
    orderid bigint,
    price real,
    amount integer DEFAULT 1,
    CONSTRAINT orderitemss_pkey PRIMARY KEY (id)
) PARTITION BY HASH (id);

ALTER TABLE IF EXISTS "orderitem"
    OWNER to postgres;

-- Partitions SQL

CREATE TABLE "orderitem1" PARTITION OF "orderitem"
    FOR VALUES WITH (modulus 4, remainder 0);

ALTER TABLE IF EXISTS "orderitem1"
    OWNER to postgres;
CREATE TABLE "orderitem2" PARTITION OF "orderitem"
    FOR VALUES WITH (modulus 4, remainder 1);

ALTER TABLE IF EXISTS "orderitem2"
    OWNER to postgres;
CREATE TABLE "orderitem3" PARTITION OF "orderitem"
    FOR VALUES WITH (modulus 4, remainder 2);

ALTER TABLE IF EXISTS "orderitem3"
    OWNER to postgres;
CREATE TABLE "orderitem4" PARTITION OF "orderitem"
    FOR VALUES WITH (modulus 4, remainder 3);

ALTER TABLE IF EXISTS "orderitem4"
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
    CONSTRAINT pk_undo_log PRIMARY KEY (id),
    CONSTRAINT ux_undo_log UNIQUE (xid, branch_id)
);

CREATE SEQUENCE IF NOT EXISTS undo_log_id_seq_3 INCREMENT BY 1 MINVALUE 1;
EOSQL
