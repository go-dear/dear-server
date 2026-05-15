-- Schema-only migration: adds role column and the audit user columns.
-- See .claude/docs/policy/audit-columns.md and CLAUDE.md.
--
-- Notes:
--   * No FK constraints by project policy (정합성은 애플리케이션 레이어에서).
--   * created_by / updated_by start as NULL; V3 backfills with the admin
--     user's id and then tightens the columns to NOT NULL.

ALTER TABLE users
    ADD COLUMN role       VARCHAR(10) NOT NULL DEFAULT 'USER',
    ADD COLUMN created_by BIGINT      NULL,
    ADD COLUMN updated_by BIGINT      NULL;
