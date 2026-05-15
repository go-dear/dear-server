# Policy: DB 네이밍

## ID (PK) 컬럼

- 모든 테이블의 PK 컬럼은 `{table}_id` 패턴을 따른다.
- 예: `users.user_id`, `families.family_id`, `family_members.family_member_id`, `family_invites.family_invite_id`
- 타입은 `BIGINT NOT NULL AUTO_INCREMENT`로 통일.
- Kotlin 엔티티 프로퍼티는 가독성 위해 그대로 `id`를 유지하되, `@Column(name = "user_id")` 등으로 매핑한다.

```kotlin
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "user_id")
var id: Long?,
```

## 인덱스 / 제약

테이블의 인덱스/제약은 **시퀀스 기반 짧은 이름**을 쓴다. 컬럼명을 키 이름에 박지 않는다.

| 종류 | 패턴 | 예시 |
|---|---|---|
| UNIQUE KEY | `ux_<table>_<seq>` | `ux_users_01`, `ux_users_02` |
| INDEX | `ix_<table>_<seq>` | `ix_users_01`, `ix_orders_03` |

- `<seq>`: 해당 테이블 내 순번 두 자리 (`01`, `02`, ...).
- 컬럼 변경이 있어도 키 이름은 **바꾸지 않는다** (시퀀스 안정성).
- 의미를 알아야 할 때는 마이그레이션 SQL 주석으로 컬럼을 명시한다.

```sql
CREATE TABLE users (
    user_id     BIGINT       NOT NULL AUTO_INCREMENT,
    email       VARCHAR(255) NOT NULL,
    -- ...
    PRIMARY KEY (user_id),
    -- ux_users_01: (email)
    UNIQUE KEY ux_users_01 (email)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
```

## FK 제약

**사용하지 않는다.** 정합성은 애플리케이션 레이어에서 검증한다 (별도 운영 정책).

## 이유

- `{table}_id` 패턴: 멀티 테이블 JOIN 시 `u.user_id = m.user_id` 형태가 자연스러움. 단순 `id` 컬럼은 JOIN 결과 출력에서 어느 테이블의 id인지 모호.
- 컬럼명을 키 이름에 박지 않음으로써 컬럼 리네임/추가 시 키 이름 변경 부담이 없다.
- 시퀀스 번호는 안정적이라 인덱스 힌트, 모니터링 쿼리, 운영팀 커뮤니케이션에서 그대로 사용 가능.
- 토스 등 큰 규모 서비스에서 자주 보이는 패턴 — 사이드에서도 동일하게 다져둔다.
