---
seq: 001
title: docs/develop 스캐폴드 + atomic PR / changelog-discipline 정책
date: 2026-05-15
related_pr: 8
related_issue: 7
status: implemented
---

## 결정

본 저장소의 변경/결정 이력 추적 인프라를 도입한다.

1. `docs/develop/` 디렉토리를 만들어 PR 단위로 entry를 작성한다.
2. 파일명은 `{seq:03d}-{slug}.md` 컨벤션을 따른다 (`001-...`, `002-...`).
3. `docs/develop/README.md`가 인덱스 — 모든 entry를 시간순으로 표 형태로 나열.
4. 새 정책 `changelog-discipline.md`이 작성 규칙을, `atomic-pr.md`가 PR 분할 규칙을 정의.
5. CLAUDE.md에 두 정책 및 인덱스 링크 추가 — 에이전트가 새 작업 전에 참조한다.

## 맥락

PR1(#6)에서 기반 컨벤션(감사 컬럼, CurrentUserId, 도메인 계층, 예외 계층 등)을 한꺼번에 도입했다.
이후 Family 도메인을 비롯한 후속 작업이 줄지어 들어올 예정이며, 결정 이유와 거부된 대안이
PR 본문에만 남으면 시간이 흐를수록 검색 비용이 커진다.

또한 사용자가 "PR은 항상 최소 단위로 분할해서 진행한다"고 명시했으므로 — 이 규칙도 정책으로 codify해
앞으로의 모든 작업에 적용되도록 한다.

## 대안

- **CHANGELOG.md 단일 파일**: 버전 기반 changelog. 디자인 결정 기록보다는 "출시 노트" 톤이라
  목적과 맞지 않음 — 거부.
- **GitHub Discussions / Wiki**: 외부 의존, 검색·grep 어려움 — 거부.
- **PR 메시지로만 추적**: 좋은 PR 메시지가 1차 정보원이지만, 시간이 지나면 "이 정책은 왜 이렇게
  되어 있나"를 풀어볼 때 PR 본문을 일일이 열어야 함. 코드 옆에 있는 markdown이 검색·참조에 유리.
- **ADR (Architecture Decision Records) 형식만**: ADR은 형식이 강해 진입 장벽 ↑. `docs/develop`는
  좀 더 가벼운 변형 — 트리거를 명시해 작성 부담은 낮추고, 형식은 frontmatter + 4섹션으로 통일.

## 영향

- 신규 정책 문서 2개: `changelog-discipline.md`, `atomic-pr.md`
- 신규 디렉토리: `docs/develop/` (`README.md` 인덱스 + 본 entry)
- CLAUDE.md 인덱스에 두 정책 + `docs/develop/` 링크 추가
- **후속 PR**부터는 entry 작성이 의무 — 위반 시 review에서 차단 대상
- 기존 PR(#2, #4, #6)에 대한 retroactive entry는 작성하지 않는다 (이미 정책 문서로 codify되어 있음).
  새 작업부터 entry를 누적해 나간다.

## 참조

- [`.claude/docs/policy/changelog-discipline.md`](../../.claude/docs/policy/changelog-discipline.md)
- [`.claude/docs/policy/atomic-pr.md`](../../.claude/docs/policy/atomic-pr.md)
- Issue #7, PR #8 (생성 예정)
- 후속 entry 002: ID 컬럼 `{table}_id` 통일 (예정)
- 후속 entry 003: Hibernate Envers 도입 (예정)
