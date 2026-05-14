# dear-server

Dear 앱(부모님을 위한 돌봄 앱)의 API 서버.

## Stack

- **JDK 25** (Temurin LTS)
- **Kotlin 2.3.20**
- **Spring Boot 3.5.14**
- **Spring Cloud 2025.0.2** (OpenFeign + Resilience4j CircuitBreaker)
- **Gradle 9.5.1** (Kotlin DSL + `libs.versions.toml`)
- **MySQL 8** + **JPA/Hibernate** + **Flyway**
- **Spring Security 6** + OAuth2 Client (카카오 / 구글) + 자체 JWT (예정)
- Spring MVC + **Kotlin Coroutines** (`suspend` 컨트롤러) + Virtual Threads
- **Actuator + Micrometer + Prometheus** (Phase 1부터)
- 테스트: **JUnit 5** + 직접 작성 Fake/Stub (MockK/Kotest 미사용)

## Quick Start

### 사전 준비

- JDK 25 (`java --version` → `25.x.x`)
- MySQL 8 (홈서버 또는 로컬 컨테이너)
- DB/사용자 생성:
  ```sql
  CREATE DATABASE dear DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
  CREATE USER 'dear'@'%' IDENTIFIED BY 'changeme';
  GRANT ALL ON dear.* TO 'dear'@'%';
  ```

### 환경 변수

```bash
cp .env.example .env
# .env 안의 값을 채운다 (DEAR_DB_PASSWORD 등)
```

### 실행

```bash
./gradlew bootRun           # 개발 실행
./gradlew test              # 단위 테스트
./gradlew build             # 빌드 + 테스트 + jar
```

### 헬스체크

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8080/actuator/prometheus  # 메트릭
```

## 프로젝트 구조

```
src/main/kotlin/com/dear/
├── DearApplication.kt
├── common/
│   ├── config/          # SecurityConfig, FeignConfig, ...
│   └── exception/
└── user/
    ├── controller/
    ├── service/
    ├── repository/
    ├── domain/
    └── dto/

src/test/kotlin/com/dear/
├── common/persistence/  # InMemoryRepository (테스트 전용 베이스)
└── user/
    ├── repository/      # FakeUserRepository (= UserRepository fake)
    └── service/         # UserServiceTest (Fake 사용, Spring context 미사용)
```

## 테스트 패턴

각 도메인 repository는 `JpaRepository`를 확장하고, 테스트에서는 동일 인터페이스를 구현하는 Fake를 사용한다.

- `InMemoryRepository<T, ID>` — `JpaRepository`의 CRUD 메서드를 in-memory map으로 구현한 추상 클래스 (테스트 소스셋)
- `FakeXxxRepository` — `XxxRepository` 인터페이스 구현 + `InMemoryRepository` 상속. 도메인별 derived query만 직접 구현
- Spring context 없이 순수 객체 조립으로 service 단위 테스트 가능

## 로컬 규칙

[`LOCAL.CLAUDE.md`](./LOCAL.CLAUDE.md) 참조 — DB 네이밍, 마이그레이션 규칙 등.
