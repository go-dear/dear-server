# Policy: 테스트

## 도구

- **JUnit 5** — 테스트 프레임워크
- **AssertJ** — 단정문 (`assertThat(...).isEqualTo(...)` 스타일)
- Fake/Stub/Spy는 손으로 작성 (MockK / Mockito 미사용)
- Spring Context 없이 가능한 모든 비즈니스 로직을 검증 (Service 단위 테스트)
- Spring Context가 필요한 슬라이스(JPA 쿼리, MVC 통합)는 별도 테스트 클래스로 분리

## AssertJ 사용 패턴

```kotlin
// 값 비교
assertThat(model.email).isEqualTo("alice@example.com")
assertThat(model.role).isEqualTo(UserRole.USER)
assertThat(model.id).isNotNull()

// 컬렉션
assertThat(users.findAll()).hasSize(2).extracting<String> { it.email }
    .containsExactlyInAnyOrder("a@x.com", "b@x.com")

// 예외
assertThatThrownBy { service.register("a", "n", 1L) }
    .isInstanceOfSatisfying(BadRequestException::class.java) { ex ->
        assertThat(ex.errorType).isEqualTo(UserErrorType.DUPLICATE_EMAIL)
    }
```

## Fixture 패턴

각 도메인은 테스트용 fixture 객체를 둔다 (`UserFixture`, `MedicationFixture`).
이 fixture는 **Entity 생성자를 직접 호출할 수 있는 유일한 코드**이다.
프로덕션 코드는 `Entity.create(...)` 팩토리 사용 ([도메인 계층 정책](./domain-layering.md) 참조).

```kotlin
object UserFixture {
    fun aUser(
        id: Long? = null,
        email: String = "alice@example.com",
        nickname: String = "Alice",
        role: UserRole = UserRole.USER,
        requester: Long = 1L,
    ): User = User(email, nickname, role, requester, id)
}
```

## Repository Fake

각 도메인 repository 인터페이스에 대해 `FakeXxxRepository`를 `src/test`에 둔다.
공통 CRUD는 `com.dear.common.persistence.InMemoryRepository<T, ID>` 추상에서 상속.
도메인별 derived query(`findByEmail` 등)는 fake에서 직접 구현.

## 금지

- **MockK / Mockito 미사용** — 필요시 손으로 작성한 Fake/Stub/Spy로 대체
- **Kotest 미사용** — JUnit5 DSL 그대로 사용
- **`@SpringBootTest` 남용 금지** — 비즈니스 로직 단위 테스트는 Spring 없이 한다

## Spring Context를 쓰는 경우

- JPQL / `@Query` / native query 검증 → `@DataJpaTest` 슬라이스 (Testcontainers MySQL)
- 컨트롤러 + 인터셉터 + ArgumentResolver 통합 → `@SpringBootTest` 또는 `@WebMvcTest` 1~2개
- 핵심 트랜잭션 / cascade / lazy loading 시나리오 → 통합 테스트 1~2개로 보호

## 커버리지

- 정량 목표 추구하지 않는다 — **사용자 경로 e2e + 핵심 비즈니스 로직 단위 테스트** 우선.
