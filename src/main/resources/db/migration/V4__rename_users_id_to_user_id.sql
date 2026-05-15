-- Rename `users.id` to `users.user_id` per the `{table}_id` PK naming convention
-- (.claude/docs/policy/db-naming.md, docs/develop/002-id-column-rename.md).
--
-- Auto-increment, PK, and existing data are preserved by RENAME COLUMN.

ALTER TABLE users RENAME COLUMN id TO user_id;
