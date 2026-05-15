# Policy: 감사(Audit) 컬럼

**모든 테이블은 다음 4개 감사 컬럼을 필수로 포함한다.** 예외 없음.

| 컬럼 | 타입 (DB) | 타입 (Kotlin) | NULL | 의미 | 채움 방식 |
|---|---|---|---|---|---|
| `created_at` | `TIMESTAMP(6)` | `LocalDateTime` | NOT NULL | 행 생성 시각 | `@CreatedDate` 자동 |
| `created_by` | `BIGINT` | `Long` | NOT NULL | 생성자 유저 id | **도메인 코드 수동** (`Entity.create` 팩토리) |
| `updated_at` | `TIMESTAMP(6)` | `LocalDateTime` | NOT NULL | 마지막 수정 시각 | `@LastModifiedDate` 자동 |
| `updated_by` | `BIGINT` | `Long` | NOT NULL | 마지막 수정자 유저 id | **도메인 코드 수동** (`markUpdated(by)`) |

## 자동/수동 분리 이유

- **시간 컬럼**: 시스템 클럭에 의존하는 무도메인 값 → `@CreatedDate`/`@LastModifiedDate`로 자동
- **사용자 컬럼**: `AuditorAware`를 도입하면 도메인 로직(누가 무엇을 수정했는가)이 인프라 컴포넌트에 숨음.
  도메인 메서드(`markCreated`, `markUpdated`, `User.create` 등)가 명시적으로 set하도록 강제하여 흐름을 코드에서 바로 보이게 한다.

`@CreatedBy` / `@LastModifiedBy` 어노테이션과 `AuditorAware` 빈은 **사용하지 않는다**.
단, `@CreatedDate`/`@LastModifiedDate` 사용을 위해 `@EnableJpaAuditing`은 유지한다.

## FK 제약은 사용하지 않는다

`created_by` / `updated_by` 컬럼은 `users.id`를 참조하지만 **FK 제약은 걸지 않는다**.
정합성은 애플리케이션 레이어(서비스, validator)에서 검증한다.
이 원칙은 본 정책뿐 아니라 **모든 테이블의 모든 연관 컬럼에 적용**된다 (운영 정책).

## DDL 블록 (새 테이블 생성 시 복붙)

```sql
CREATE TABLE <table_name> (
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    -- ... 도메인 컬럼 ...
    created_at  TIMESTAMP(6) NOT NULL,
    created_by  BIGINT       NOT NULL,
    updated_at  TIMESTAMP(6) NOT NULL,
    updated_by  BIGINT       NOT NULL,
    PRIMARY KEY (id)
    -- 도메인 UNIQUE는 ux_<table>_NN, INDEX는 ix_<table>_NN
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8mb4
  COLLATE = utf8mb4_unicode_ci;
```

- ID는 별도 요구 사항이 없는 한 `BIGINT NOT NULL AUTO_INCREMENT`로 통일한다.
- FK 제약은 추가하지 않는다 ([DB 네이밍 정책](./db-naming.md) 참조).

## 코드 적용

JPA 엔티티는 [`com.dear.common.persistence.BaseEntity`](../../../src/main/kotlin/com/dear/common/persistence/BaseEntity.kt)를 상속한다.

```kotlin
@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
abstract class BaseEntity(
    createdBy: Long,
    updatedBy: Long,
) {
    @CreatedDate
    var createdAt: LocalDateTime = LocalDateTime.MIN
        protected set

    @LastModifiedDate
    var updatedAt: LocalDateTime = LocalDateTime.MIN
        protected set

    var createdBy: Long = createdBy
        protected set

    var updatedBy: Long = updatedBy
        protected set

    protected fun auditUpdatedBy(requester: Long) {
        this.updatedBy = requester
    }
}
```

**엔티티 사용 패턴**:

- 엔티티 생성자는 `requester: Long`을 받아 `BaseEntity(createdBy = requester, updatedBy = requester)`로 전달한다.
- 업데이트가 발생하는 도메인 메서드(`rename`, `changeRole` 등) 안에서 `auditUpdatedBy(requester)`를 호출한다.
- 자세한 도메인 계층 패턴은 [도메인 계층 정책](./domain-layering.md) 참조.

## 시스템 진입 사용자 (Pre-launch)

- AdminUser(role=ADMIN)를 시드해두고, **모든 요청은 `X-User-Id` 헤더로 사용자 id를 전달**한다.
- 개발/테스트 시에도 Admin id를 헤더로 명시한다 — 폴백 없음.
- 백그라운드 잡(스케줄러 등 추후 도입) 또한 명시적으로 actor id를 갖고 호출 — 자세한 흐름은 추후 별도 정책.

## 마이그레이션 추가 시 체크리스트

- [ ] 4개 감사 컬럼 포함, 모두 `NOT NULL`
- [ ] FK 제약 **없음** (어떤 연관 컬럼도)
- [ ] 엔티티 클래스가 `BaseEntity`를 상속
- [ ] UNIQUE/INDEX는 [DB 네이밍 정책](./db-naming.md) 준수 (`ux_<table>_NN`, `ix_<table>_NN`)
