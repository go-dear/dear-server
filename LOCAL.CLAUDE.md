# Dear Server — Local Conventions

이 파일은 `dear-server` 저장소에서 Claude/에이전트가 따라야 할 프로젝트 로컬 규칙을 모은다.
글로벌 `~/.claude/CLAUDE.md` 에 덧붙는 형태로 적용되며, 충돌 시 이 파일이 우선한다.

## DB 네이밍 컨벤션

테이블의 인덱스/제약은 시퀀스 기반 짧은 이름을 쓴다. 컬럼명을 키 이름에 박지 않는다.

| 종류 | 패턴 | 예시 |
|---|---|---|
| UNIQUE KEY | `ux_<table>_<seq>` | `ux_users_01`, `ux_users_02` |
| INDEX | `ix_<table>_<seq>` | `ix_users_01`, `ix_orders_03` |

- `<seq>`는 해당 테이블 내에서 순번 두 자리 (`01`, `02`, ...).
- 컬럼 변경이 있어도 키 이름은 바꾸지 않는다 (시퀀스 안정성).
- 의미를 알아야 할 때는 마이그레이션 SQL 주석으로 컬럼을 명시한다.

```sql
-- ux_users_01: (email)
UNIQUE KEY ux_users_01 (email)
```

## 마이그레이션

- 위치: `src/main/resources/db/migration`
- Flyway 명명: `V{n}__{snake_case_description}.sql` (예: `V1__create_users.sql`)
- 한 마이그레이션 = 한 변경 단위 (테이블 생성, 컬럼 추가 등)
- 이미 배포된 마이그레이션은 **절대 수정하지 않는다** — 새 마이그레이션으로 보정한다.

## 기타 (추후 추가 예정)

- 패키지 구조: `com.dear.<domain>.{controller,service,repository,domain,dto}`
- 도메인 간 의존은 service 레이어를 거친다 (repository 직접 호출 금지)
