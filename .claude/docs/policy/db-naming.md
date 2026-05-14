# Policy: DB 네이밍

테이블의 인덱스/제약은 **시퀀스 기반 짧은 이름**을 쓴다. 컬럼명을 키 이름에 박지 않는다.

## 규칙

| 종류 | 패턴 | 예시 |
|---|---|---|
| UNIQUE KEY | `ux_<table>_<seq>` | `ux_users_01`, `ux_users_02` |
| INDEX | `ix_<table>_<seq>` | `ix_users_01`, `ix_orders_03` |
| PRIMARY KEY | 묵시 (선언 시 별도 이름 불필요) | — |
| FOREIGN KEY | `fk_<table>_<refs>_<seq>` | `fk_orders_users_01` |

- `<seq>`: 해당 테이블 내 순번 두 자리 (`01`, `02`, ...).
- 컬럼 변경이 있어도 키 이름은 **바꾸지 않는다** (시퀀스 안정성).
- 의미를 알아야 할 때는 마이그레이션 SQL 주석으로 컬럼을 명시한다.

## 예시

```sql
CREATE TABLE users (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    email       VARCHAR(255) NOT NULL,
    nickname    VARCHAR(30)  NOT NULL,
    created_at  TIMESTAMP(6) NOT NULL,
    updated_at  TIMESTAMP(6) NOT NULL,
    PRIMARY KEY (id),
    -- ux_users_01: (email)
    UNIQUE KEY ux_users_01 (email)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
```

## 이유

- 컬럼명을 키 이름에 박지 않음으로써 컬럼 리네임/추가 시 키 이름 변경 부담이 없다.
- 시퀀스 번호는 안정적이라 인덱스 힌트, 모니터링 쿼리, 운영팀 커뮤니케이션에서 그대로 사용 가능.
- 토스 등 큰 규모 서비스에서 자주 보이는 패턴 — 사이드에서도 동일하게 다져둔다.
