-- Seeds the admin user, backfills audit user columns for any pre-existing
-- rows, then tightens created_by / updated_by to NOT NULL.
--
-- Idempotency note: this migration is intended for an environment where the
-- admin email is not yet present. Running on top of an environment that already
-- has 'devbombo96@gmail.com' would fail on the INSERT (UNIQUE on email) — that
-- is the desired safety, since the admin row is the load-bearing reference for
-- audit columns.

INSERT INTO users (email, nickname, role, created_at, updated_at)
VALUES ('devbombo96@gmail.com', 'DEAR 봄보', 'ADMIN', NOW(6), NOW(6));

SET @admin_id = LAST_INSERT_ID();

UPDATE users
   SET created_by = @admin_id
 WHERE created_by IS NULL;

UPDATE users
   SET updated_by = @admin_id
 WHERE updated_by IS NULL;

ALTER TABLE users
    MODIFY COLUMN created_by BIGINT NOT NULL,
    MODIFY COLUMN updated_by BIGINT NOT NULL;
