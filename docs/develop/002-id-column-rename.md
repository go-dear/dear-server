---
seq: 002
title: ID 컬럼명을 `{table}_id` 패턴으로 통일 + users.id → user_id rename
date: 2026-05-15
related_pr: 10
related_issue: 9
status: proposed
---

## 결정

모든 테이블의 PK 컬럼명은 `{table}_id` 패턴을 따른다.

- `users.user_id`
- `families.family_id`
- `family_members.family_member_id`
- `family_invites.family_invite_id`
- 일반 형식: 단수형 테이블 의미 + `_id` (테이블명이 `users`이면 `user_id`)

타입은 `BIGINT NOT NULL AUTO_INCREMENT`로 통일.

Kotlin 엔티티 프로퍼티는 가독성 위해 그대로 `id`로 두고, `@Column(name = "user_id")` 등으로 매핑한다.

기존 `users` 테이블도 본 PR에서 retro-rename(V4) 한다.

## 맥락

PR1(#6)에서 `users.id`를 그대로 두고 진행했으나, Family 도메인부터 N개 테이블이 들어오면서
"각 테이블의 id"를 명시하는 게 가독성과 JOIN/디버깅에 큰 이득이라는 의견이 들어왔다.

토스 등 대형 서비스에서도 `{table}_id` 패턴이 표준 — 사이드에서도 통일해 두면 학습 가치가 있고
미래 추가 도메인에 일관 적용된다.

## 대안

- **그대로 `id` 유지**: 간결하지만 멀티 테이블 SELECT 결과(`SELECT u.*, m.* FROM users u JOIN ...`)에서
  컬럼명 충돌 → AS 별칭 강제. 또한 `m.user_id` 같은 FK-style 컬럼은 결국 만들어야 해서 일관성이
  깨짐. — 거부.
- **Kotlin 프로퍼티도 `userId`로**: 매핑 일치는 좋지만 도메인 객체 API가 매번 `user.userId`처럼
  중복어처럼 보임. 한 클래스 내에서는 `id`로 충분. — 거부.
- **PK는 `id`, FK만 `{table}_id`**: 일관성 약함. 새 테이블 작성 시 매번 헷갈림. — 거부.

## 영향

- `.claude/docs/policy/db-naming.md` — "ID (PK) 컬럼" 섹션 신설
- `src/main/resources/db/migration/V4__rename_users_id_to_user_id.sql` 신규 — `ALTER TABLE users RENAME COLUMN id TO user_id`. MySQL 8에서 PK·AUTO_INCREMENT 보존.
- `src/main/kotlin/com/dear/user/domain/User.kt` — `@Column(name = "user_id")` 추가
- 본 PR 머지 후 추가될 도메인 테이블(families, family_members 등)은 처음부터 `{table}_id` 적용
- 후속 V5(Envers `users_AUD`)도 `user_id` 컬럼명 사용

## 참조

- 정책: [`.claude/docs/policy/db-naming.md`](../../.claude/docs/policy/db-naming.md)
- Issue #9, PR #10 (생성 예정)
- 후속 entry 003: Hibernate Envers 도입 (예정)
