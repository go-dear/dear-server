---
seq: 003
title: Hibernate Envers 도입 — 모든 엔티티 변경 이력 자동 보존
date: 2026-05-15
related_pr: 12
related_issue: 11
status: proposed
---

## 결정

Hibernate Envers를 도입하여 `BaseEntity`를 상속한 모든 엔티티의 변경(insert / update / delete)을
`{table}_history` 테이블에 자동 기록한다.

- 의존성: `org.hibernate.orm:hibernate-envers` (Boot BOM 버전 관리)
- 어노테이션: `BaseEntity`에 `@Audited` + 각 도메인 entity 클래스에도 `@Audited` (Envers 동작상 `@MappedSuperclass`만으로는 자식 entity가 audit되지 않음)
- 명명: `application.yml`에서 envers 기본 UPPERCASE를 lowercase/snake_case로 override (`audit_table_suffix=_history`)
- 첫 audit 테이블: `users_history` + 전역 `revinfo` (V5)

## 맥락

- 정책: 원장 테이블에서는 hard delete가 기본 ([audit-columns.md](../../.claude/docs/policy/audit-columns.md))
- 그러나 "모든 테이블 history 보존"이 정책 요구사항
- 직접 history 테이블 + 트리거를 짜는 대안 대비 Envers는 어노테이션 한 줄로 동등 효과 + `AuditReader`로 시점 조회 API 제공

## 대안

- **커스텀 EntityListener + history 테이블 직접 작성**: 더 단순한 스키마 + 자유로운 커스터마이징.
  단, Envers가 이미 제공하는 revision 번호 부여 / 시점 조회 / `@NotAudited` 제외 등을 모두 다시 짜야 함.
  학습 가치는 있으나 유지 비용이 가치를 압도. — 거부.
- **DB 트리거**: 애플리케이션과 분리되어 운영 면에서 깨끗하지만, JPA 컨텍스트 정보(누가 변경했는가)가
  반영되기 어렵고 Flyway 마이그레이션 외에 DB 객체가 늘어남. — 거부.
- **Envers를 안 쓰고 원장 테이블에 `version` 컬럼만 두기**: 시점 조회 불가. 단순 낙관적 잠금 용도면
  몰라도 history 보존은 아님. — 정책 요구 미충족.

## 영향

- 신규 의존성 `hibernate-envers` (Spring Boot 3.5와 호환되는 Hibernate 6.6 라인)
- `BaseEntity`에 `@Audited` 추가 → 향후 모든 `Entity extends BaseEntity`는 자동으로 audit됨
- `application.yml`에 envers naming override:
  - `audit_table_suffix=_history`, `revision_field_name=rev`, `revision_type_field_name=revtype`, `store_data_at_delete=true`
- V5 마이그레이션:
  - 전역 `revinfo` 테이블 (`rev INTEGER AUTO_INCREMENT`, `revtstmp BIGINT`)
  - `users_history` (users 컬럼 전부 nullable + `rev`, `revtype` 추가, PK `(user_id, rev)`)
- 향후 모든 도메인 테이블은 동일 패턴으로 `{table}_history`를 V{N}에서 함께 생성 — 정책 체크리스트에 명시
- 기존 row(admin, alice, bob)는 audit row가 없음 — Envers는 `@Audited` 적용 이후 변경에 대해서만 기록.
  필요 시 baseline rev를 한 번 수동으로 적재 가능하나 본 PR 범위에는 포함하지 않음.

## 참조

- 정책: [`.claude/docs/policy/audit-columns.md`](../../.claude/docs/policy/audit-columns.md) "변경 이력" 섹션
- [Hibernate Envers User Guide](https://docs.jboss.org/hibernate/orm/6.6/userguide/html_single/Hibernate_User_Guide.html#envers)
- Issue #11, PR #12 (생성 예정)
- 앞 entry: [002-id-column-rename](./002-id-column-rename.md)
