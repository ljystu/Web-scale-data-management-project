#!/bin/sh
set -e

# In case pqsl asks for a password
export PGPASSWORD="$POSTGRESQL_PASSWORD"

psql -v ON_ERROR_STOP=1 --username "$POSTGRESQL_USERNAME" --dbname "$POSTGRESQL_DATABASE" <<-EOSQL
	CREATE DATABASE "Order";
	CREATE DATABASE "Payment";
	CREATE DATABASE "Stock";
	CREATE DATABASE "seata";
	GRANT ALL PRIVILEGES ON DATABASE "Order" TO postgres;
	GRANT ALL PRIVILEGES ON DATABASE "Payment" TO postgres;
	GRANT ALL PRIVILEGES ON DATABASE "Stock" TO postgres;
	GRANT ALL PRIVILEGES ON DATABASE "seata" TO postgres;
EOSQL

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USERNAME" --dbname "Order" <<-EOSQL
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


psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USERNAME" --dbname "Payment" <<-EOSQL
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

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USERNAME" --dbname "Stock" <<-EOSQL
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

psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USERNAME" --dbname "seata" <<-EOSQL
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


#pg_basebackup -h "db-slave" -D /srv/pgsql/standby -X stream -P -U postgres -Fp -R

#pg_basebackup -D $PGDATA -S replication_slot_slave1 -X stream -P -U replicator -Fp -R