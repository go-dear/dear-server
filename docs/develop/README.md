# Develop Changelog

`docs/develop/`는 본 저장소의 **변경/결정 이력**을 모은다. PR 메시지가 "무엇을 바꿨는가"라면,
여기 entry는 **"왜 바꿨고, 어떤 대안을 거부했고, 어떤 영향을 끼치는가"**를 남긴다.

규칙은 [`.claude/docs/policy/changelog-discipline.md`](../../.claude/docs/policy/changelog-discipline.md) 참조.

## Entries

| Seq | Title | PR | Status | Date |
|---|---|---|---|---|
| [001](./001-docs-develop-scaffold.md) | docs/develop 스캐폴드 + atomic PR / changelog-discipline 정책 | #8 | implemented | 2026-05-15 |
| [002](./002-id-column-rename.md) | ID 컬럼명을 `{table}_id` 패턴으로 통일 + users.id → user_id rename | #10 | proposed | 2026-05-15 |
