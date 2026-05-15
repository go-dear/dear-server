# Policy: API 경로

## 경로 패턴

| 패턴 | 접근 허용 | 예시 |
|---|---|---|
| `/admin/{domain}/v{N}/**` | **ADMIN role only** | `/admin/users/v1/{id}/role` |
| `/api/{domain}/v{N}/**` | ADMIN + USER (인증된 모든 사용자) | `/api/users/v1`, `/api/medications/v1/{id}/logs` |
| `/actuator/**` | 인증 없음 (헬스/메트릭) | `/actuator/health`, `/actuator/prometheus` |

- `{domain}`: 도메인명 (예: `users`, `medications`, `tasks`).
- `v{N}`: 정수. **도메인별 독립 버전**. `/api/users/v1`과 `/api/medications/v2`가 공존 가능.
- 같은 N 안에서는 backward-compatible additive change만 허용. Breaking change 시 N 증가.

## 인증

- `/admin/**`, `/api/**` 경로는 **`X-User-Id` 헤더 필수**.
- ADMIN role을 가진 사용자도 동일하게 헤더를 전송한다 (예외 없음).
- 누락/형식 오류/존재하지 않는 사용자 id → **HTTP 401 Unauthorized** (`CommonErrorType.MISSING_AUTH_HEADER` / `INVALID_AUTH_HEADER` / `UNKNOWN_USER`).
- `/admin/**`에 비-ADMIN 사용자 접근 → **HTTP 403 Forbidden** (`CommonErrorType.FORBIDDEN`).
- `/actuator/**`는 헤더 검증에서 제외 (`CurrentUserIdInterceptor`가 path pattern으로 안 잡음).

## Phase 2 인증 도입 시

- 같은 `CurrentUserId` contract를 유지한다.
- `X-User-Id` 헤더 대신 JWT 추출 결과를 같은 request attribute에 set하도록 인터셉터/필터 교체.
- 컨트롤러/서비스 코드는 변경 없음.

## 컨트롤러 매핑 규칙

- `@RequestMapping("/admin/{domain}/v{N}")` 또는 `@RequestMapping("/api/{domain}/v{N}")`로 시작.
- 도메인 안의 하위 리소스는 `/{id}/medications` 등 자유 구성.
