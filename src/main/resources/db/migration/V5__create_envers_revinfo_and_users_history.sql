-- Creates Hibernate Envers tables:
--   - revinfo : global revision metadata (one row per revision across all entities)
--   - users_history : audit history for the users table
--
-- Naming conventions are overridden in application.yml so column / table names
-- stay snake_case lowercase (envers.audit_table_suffix=_history,
-- revision_field_name=rev, revision_type_field_name=revtype).
--
-- FK constraints are intentionally omitted per project policy. Envers logic
-- maintains referential integrity at the application layer.

CREATE TABLE revinfo (
    rev      INTEGER NOT NULL AUTO_INCREMENT,
    revtstmp BIGINT  NULL,
    PRIMARY KEY (rev)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;

CREATE TABLE users_history (
    user_id    BIGINT       NOT NULL,
    rev        INTEGER      NOT NULL,
    revtype    TINYINT      NULL,
    email      VARCHAR(255) NULL,
    nickname   VARCHAR(30)  NULL,
    role       VARCHAR(10)  NULL,
    created_at TIMESTAMP(6) NULL,
    created_by BIGINT       NULL,
    updated_at TIMESTAMP(6) NULL,
    updated_by BIGINT       NULL,
    PRIMARY KEY (user_id, rev),
    INDEX ix_users_history_01 (rev)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
