# Dear Server — Project Instructions

이 파일은 `dear-server` 저장소에서 Claude/에이전트가 따라야 할 프로젝트 정책의 **진입점**이다.
글로벌 `~/.claude/CLAUDE.md`에 덧붙는 형태로 적용된다. 정책 충돌 시 이 파일 및 하위 정책이 글로벌보다 우선한다.

## 정책 (Policies)

세부 규칙은 [`.claude/docs/policy/`](./.claude/docs/policy/)에 있다. 새 작업 전 관련 정책을 먼저 읽는다.
정책은 사용자가 명시적으로 지정한 항목만 추가된다.

- [DB 네이밍](./.claude/docs/policy/db-naming.md) — UNIQUE/INDEX 키 명명 규칙
- [감사 컬럼](./.claude/docs/policy/audit-columns.md) — 모든 테이블 필수 `created_at` / `created_by` / `updated_at` / `updated_by`
- [도메인 계층](./.claude/docs/policy/domain-layering.md) — Entity/Service/Model/Response 분리, 팩토리 패턴, suspend 미사용
- [API 경로](./.claude/docs/policy/api-paths.md) — `/admin/{domain}/v{N}/**` / `/api/{domain}/v{N}/**` 패턴, 인증 흐름
- [테스트](./.claude/docs/policy/testing.md) — JUnit5 + AssertJ + 손으로 작성한 Fake, MockK/Kotest 미사용

## 로컬 오버라이드

개인용 로컬 지시는 `CLAUDE.local.md`에 작성한다. 이 파일은 `.gitignore` 처리되어 공유되지 않는다.
정책과 충돌할 때 `CLAUDE.local.md`가 우선한다 — 단, **이 우선순위는 본인 워크스테이션에만 적용**된다.

## 자동 검증 — `.claude/` 민감 파일 차단

`.claude/`는 팀에 공유되는 디렉토리(committed)이므로 민감 파일은 포함될 수 없다.
[`.claude/settings.json`](./.claude/settings.json)에 등록된 PreToolUse 훅이 `git commit` 직전
`.claude/` 내 민감 파일명 패턴을 검사하여 매칭 시 커밋을 차단한다.

- 구현: [`.claude/hooks/check-sensitive.sh`](./.claude/hooks/check-sensitive.sh)
- 차단 패턴 예: `*.env`(단, `*.env.example` 제외), `*.pem`, `*.key`, `*credentials*`, `*secret*`, `*.token`, `id_rsa*` 등
- 로컬 시크릿은 `CLAUDE.local.md`, `.claude/settings.local.json`, 또는 프로젝트 루트의 `.env` (모두 gitignored)에 둔다

## 워크플로우

- 모든 변경은 **Issue → branch → PR → merge** (bootstrap 1회 예외는 종료됨)
- 빌드: `./gradlew build`, 테스트: `./gradlew test`
- 메인 브랜치는 항상 그린 상태 유지
