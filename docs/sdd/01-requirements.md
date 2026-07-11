# 01 - Requirements

## Functional Requirements
| ID | Requirement | Priority | Source |
| --- | --- | --- | --- |
| FR-001 | User can start a new Sudoku game by selecting a difficulty level. | High | Product |
| FR-002 | App generates a valid 9x9 Sudoku puzzle with a unique solution. | High | Product |
| FR-003 | User can enter digits 1-9 into editable cells. | High | Product |
| FR-004 | User can add/remove pencil notes in editable cells. | High | Product |
| FR-005 | User can erase a cell value they entered. | High | Product |
| FR-006 | App prevents edits to fixed clue cells. | High | Product |
| FR-007 | User can use undo and redo during gameplay. | Medium | UX |
| FR-008 | User can request a hint that fills one valid cell. | Medium | UX |
| FR-009 | App tracks elapsed time for each game. | High | Product |
| FR-010 | App supports pause/resume and preserves timer state. | Medium | Product |
| FR-011 | App detects puzzle completion and shows result screen. | High | Product |
| FR-012 | User can save/restore in-progress games automatically. | High | Product |
| FR-013 | App records completed game statistics by difficulty. | Medium | Product |
| FR-014 | User can toggle gameplay settings (mistake limit, highlight duplicates, theme). | Medium | Product |

## Non-Functional Requirements
| ID | Requirement | Category | Target |
| --- | --- | --- | --- |
| NFR-001 | Cell input feedback latency | Performance | <= 100 ms on target device |
| NFR-002 | Puzzle generation latency | Performance | <= 2 s for Expert, <= 1 s otherwise |
| NFR-003 | App cold start time | Performance | <= 2.5 s on mid-range device |
| NFR-004 | Crash-free sessions | Reliability | >= 99.5% in beta |
| NFR-005 | Works without network | Availability | 100% core functionality offline |
| NFR-006 | Supports Android versions | Compatibility | Android 8.0 (API 26) and above |
| NFR-007 | Code quality | Maintainability | Layered architecture + test coverage >= 70% for domain/data |
| NFR-008 | Accessibility basics | Usability | Screen reader labels + contrast-compliant palette |

## Business Rules
- BR-001: Generated puzzle must have exactly one valid solution.
- BR-002: Clue cells are immutable.
- BR-003: Win state is valid only when board is fully filled and rules are satisfied.
- BR-004: Hint consumes one available hint count when hint limits are enabled.
- BR-005: Mistake counter increments only for committed values, not notes.

## User Stories / Use Cases
| ID | Actor | Description | Acceptance Criteria |
| --- | --- | --- | --- |
| US-001 | Player | Start a new game by choosing difficulty. | Puzzle loads and timer starts. |
| US-002 | Player | Enter number in a cell. | Value appears and validation updates. |
| US-003 | Player | Add pencil notes for candidate values. | Multiple note values shown in cell. |
| US-004 | Player | Undo accidental move. | Last move is reverted correctly. |
| US-005 | Player | Resume game after app restart. | Previous board and timer are restored. |
| US-006 | Player | Complete puzzle. | Completion dialog shows time, mistakes, and options. |

## Open Questions
- Q-001: Should daily challenge mode be included in MVP or post-MVP?
- Q-002: Should hint count be unlimited by default?
