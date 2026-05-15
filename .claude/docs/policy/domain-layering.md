# Policy: 도메인 계층 + 진입점 컨벤션

## Controller

- **`suspend` 함수로 작성하지 않는다.** 코루틴은 명확한 비동기 필요(스트리밍, 외부 호출의 동시성 등)가 있을 때만 부분 도입한다.
- 진입 검증은 `@Valid @RequestBody` + Bean Validation으로.
- 헤더/세션 등 요청 메타는 별도 ArgumentResolver로 주입 (예: `CurrentUserId`).

## Entity

- 생성자는 **public**으로 둔다. (`private constructor`로 막지 않는다.)
- 그러나 도메인 코드는 `Entity.create(...)` 정적 팩토리로 인스턴스를 만든다.
- 생성자를 직접 호출하는 코드는 **PR review에서 차단**한다.
- **예외**: 테스트 코드의 `EntityFixture` (예: `UserFixture`)는 생성자 직접 호출을 허용한다.
- 생성자 파라미터에 **기본값을 두지 않는다** (Collection은 예외 — 빈 컬렉션으로 초기화 가능).
  필요한 모든 값은 호출자가 명시적으로 전달한다.

### 감사(audit) 필드 처리

- Entity 생성자는 `requester: Long`을 받아 부모 `BaseEntity(createdBy = requester, updatedBy = requester)`로 전달한다.
- 도메인 update 메서드(`rename`, `changeRole` 등)는 마지막에 `auditUpdatedBy(requester)`를 호출한다.
- 자세한 내용은 [감사 컬럼 정책](./audit-columns.md) 참조.

### 도메인 메서드

- 엔티티 필드 변경은 반드시 도메인 메서드를 거친다 (rich domain model).
- Service가 엔티티 필드를 직접 set하지 않는다. 메서드명은 의도를 드러낸다 (`user.rename(newNickname, actorId)`).

## Service

- 메서드 시그니처에 **`requester: Long`을 명시한다** (인증 컨텍스트 의존을 명시적 인자로 노출).
- Entity를 그대로 반환하지 않는다 — `XxxModel` (도메인 DTO)로 변환하여 반환.
- `@Transactional(readOnly = true)`를 클래스 레벨, 쓰기 메서드는 메서드 레벨 `@Transactional`.

## Model / Response 계층

| 계층 | 타입 | 책임 |
|---|---|---|
| Entity (`User`) | JPA mapped | 영속화 + 도메인 규칙 |
| Model (`UserModel`) | data class | Service ↔ Controller 사이의 도메인 view |
| Request (`RegisterUserRequest`) | data class + Bean Validation | API 입력 (JSON) |
| Response (`UserResponse`) | data class | API 출력 (JSON) — Model에서 변환 |

- Entity는 Service 레이어를 벗어나지 않는다.
- Controller는 Request 받음 → primitives로 Service 호출 → Model 받음 → Response 변환.

## Exception

- 클라이언트 입력/도메인 규칙 위반 → `BadRequestException(domainErrorType)` → HTTP 400
- 인증 누락/실패 → `UnauthorizedException(CommonErrorType.X)` → HTTP 401
- 권한 부족 → `ForbiddenException(CommonErrorType.X)` → HTTP 403
- 리소스 없음 → `NotFoundException(CommonErrorType.X)` → HTTP 404
- 도메인별 ErrorType은 `com.dear.common.exception` 패키지 안에 `XxxErrorType` 파일로 추가 (sealed interface 제약).
- 비-`BadRequestException` 계열은 기본적으로 `CommonErrorType`을 사용한다.

## 현재 사용자 식별

- 진입점: `X-User-Id` 헤더 (필수). 폴백 없음.
- Interceptor에서 헤더 → Repository 조회(Caffeine 캐시) → `CurrentUserId(userId, role)` 구성.
- Controller 메서드 파라미터에 `CurrentUserId`를 선언하면 ArgumentResolver가 주입.
- Service에는 `currentUserId.userId`만 `requester` 인자로 전달.
- 자세한 path 규칙은 [API 경로 정책](./api-paths.md) 참조.
