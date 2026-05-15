# Policy: 변경 이력 (`docs/develop/`)

`docs/develop/`는 **이 저장소의 변경/결정 이력**을 기록하는 디렉토리다.
PR 메시지가 "무엇을 바꿨는가"라면, `docs/develop/` 엔트리는 **"왜 바꿨고, 어떤 대안을 거부했고, 어떤 영향을 끼치는가"**를 남긴다.
Claude/에이전트는 새 작업 시 관련 entry를 참조하여 과거 의도와 충돌하지 않게 한다.

## 위치 & 파일명

- 디렉토리: `docs/develop/` (repo root)
- 인덱스: `docs/develop/README.md` — 모든 entry를 시간순으로 나열
- 파일명: `{seq:03d}-{slug}.md`
  - `seq`: 3자리 0-padded 시퀀스 번호 (`001`, `002`, ..., `099`, `100`, ...)
  - `slug`: 영문 kebab-case, 간결한 주제 요약
  - 예: `001-docs-develop-scaffold.md`, `002-id-column-rename.md`

## Entry 작성 트리거

다음 변경이 발생하면 **반드시** entry를 추가한다:

- 새 정책 추가 (`.claude/docs/policy/*.md`)
- 기존 정책의 의미 있는 변경
- Flyway 스키마 마이그레이션 (`V{N}__*.sql`)
- 의존성 추가/제거 (Gradle / npm)
- 아키텍처 결정 (계층 도입, 외부 시스템 통합, 권한 모델 변경 등)
- 외부 API contract 변경 (URL 패턴, 응답 형식 등)

소소한 버그픽스, 리팩터링, 단순 추가는 entry 없이 PR 메시지만으로 충분하다.

## Entry 구조

```markdown
---
seq: 001
title: 변경 한 줄 요약
date: 2026-05-15
related_pr: 7
related_issue: 7
status: implemented
---

## 결정
무엇을 결정/변경했는가 (1~3 문단)

## 맥락
왜 이 결정이 필요했는가 — 배경 / 문제 / 사용자 요구

## 대안
고려한 다른 옵션과 왜 거부했는가 — 거부 이유가 가장 가치 있음

## 영향
- 어떤 코드/문서/스키마/의존성이 바뀌는가
- 후속 PR이나 마이그레이션 의무가 생기는가
- 기존 entry를 superseded로 만드는가

## 참조
- 정책 문서 링크 (`.claude/docs/policy/*.md`)
- 외부 자료 / 라이브러리 docs
- 관련 entry (앞뒤 의존)
```

### frontmatter 필드

| 필드 | 의미 |
|---|---|
| `seq` | 시퀀스 번호. 파일명 prefix와 일치 |
| `title` | 한 줄 요약 (인덱스에 그대로 노출) |
| `date` | YYYY-MM-DD. PR 머지 일자 또는 결정 일자 |
| `related_pr` | GitHub PR 번호 (없으면 생략) |
| `related_issue` | GitHub Issue 번호 (없으면 생략) |
| `status` | `proposed` / `implemented` / `superseded` |

### status 의미

- `proposed`: 결정은 합의됐으나 구현 전 (planning entry)
- `implemented`: PR 머지 완료
- `superseded`: 더 이상 유효하지 않음 — 본문 상단에 후속 entry 링크 명시

## 인덱스 (`README.md`) 형식

```markdown
# Develop Changelog

| Seq | Title | PR | Status | Date |
|---|---|---|---|---|
| [001](./001-docs-develop-scaffold.md) | docs/develop 스캐폴드 + 정책 2개 | #7 | implemented | 2026-05-15 |
| [002](./002-id-column-rename.md) | ID 컬럼명 `{table}_id` 통일 | #N | implemented | 2026-MM-DD |
```

새 entry 추가 시 인덱스도 같은 PR에서 갱신한다.

## 워크플로우

1. PR 작업 시작 — Issue를 만들면서 `docs/develop/{seq}-{slug}.md`를 함께 작성 (status: `proposed`)
2. PR이 머지되면 status를 `implemented`로 변경 + `related_pr` 기입 + 인덱스 갱신
3. PR이 거부되거나 후속 결정으로 뒤집히면 `status: superseded` + 본문 상단에 후속 entry 링크

## Claude가 참조해야 할 때

- 새 작업 전: 관련 도메인의 entry를 빠르게 훑어 과거 결정과 충돌이 없는지 확인
- 정책 충돌 시: entry의 `대안` 섹션에서 왜 그 옵션이 거부되었는지 확인
- 회고/리팩터링 제안 전: 해당 영역의 마지막 entry를 참조
