# Policy: 최소 단위 PR (Atomic PR)

**PR은 하나의 논리적 단위만 포함한다.** 여러 종류의 변경을 한 PR에 묶지 않는다.

## 분리해야 할 변경 유형

다음은 서로 다른 단위로 본다 — 같은 도메인이라도 별도 PR:

- **정책 추가/변경** (`.claude/docs/policy/*.md`)
- **스키마 마이그레이션** (Flyway `V{N}__*.sql`)
- **엔티티 + Repository** (도메인 코드)
- **Service** (비즈니스 로직)
- **Controller + 외부 API contract** (REST 엔드포인트)
- **테스트** (특히 통합 테스트는 별도)
- **의존성 추가/제거**
- **인프라/배포 설정**
- **리팩터링** (기능 변경 없음)

## 의존성 처리

- 의존성 추가는 그 의존성을 **처음 사용하는 PR**에 포함 가능 (e.g., Caffeine 추가 + 첫 사용처).
- 단, 향후 여러 PR이 같은 의존성을 쓴다면 의존성 PR을 먼저 분리하는 게 깔끔.

## PR 크기 가이드

엄격한 줄 수 제한은 두지 않되, 다음 신호가 있으면 쪼갠다:

- PR 설명을 3문단 이상으로 풀어야 한다
- "그리고", "또한"이 PR 제목에 들어간다
- 다른 검토자가 한 번에 follow할 수 없다고 느낀다
- 머지 후 일부를 revert하기 어렵다

## 함께 가야 하는 변경의 묶음

원칙적으로 분리하지만 **돌리기 불가능한 함께 가는 변경**은 한 PR에:

- DB 컬럼 rename + 그 컬럼을 참조하는 코드 (배포 순서 문제 회피)
- 새 마이그레이션 + 해당 마이그레이션에 의존하는 entity (빌드 통과 보장)
- entity 새 필드 + 그 필드를 set하는 service (validation 통과)

이런 묶음도 본문에서 "why bundled" 명시.

## 병렬 PR (`/team`) 활용

PR 시퀀스 의존 그래프에서 형제(sibling) 단위는 `/team`으로 병렬 진행 가능:

```
PR-A (스키마) ─┬─ PR-B (entity)
                ├─ PR-C (다른 entity)  ← B, C 병렬 OK
                └─ PR-D (또다른 entity)
        PR-B+C+D ─→ PR-E (통합 서비스)  ← B/C/D 모두 머지 후
```

병렬은 옵션. 의심스러우면 sequential.

## /docs/develop 와의 관계

각 atomic PR은 `docs/develop/`에 1개 entry를 동반한다 (정책 변경/스키마/아키텍처 결정인 경우).
사소한 PR(타입 오류 수정 등)은 entry 불필요. 자세한 트리거는 [changelog-discipline](./changelog-discipline.md) 참조.

## Bootstrap 예외

새 저장소 첫 커밋(scaffold)이나 한 줄짜리 후속 보정(`fix:` 류)은 본 정책에서 예외다.
이미 정착된 저장소에서의 모든 신규 작업은 이 정책을 따른다.
