# 04 - Data Design

## Domain Model
| Entity | Description | Key Fields |
| --- | --- | --- |
| Game | In-progress or completed Sudoku session | gameId, difficulty, status, startedAt, elapsedSeconds |
| Board | 9x9 board state | cells[81], fixedMask, notesMap |
| Move | One user action for undo/redo | moveId, gameId, cellIndex, oldValue, newValue, noteDelta, timestamp |
| Settings | User preferences | noteModeDefault, highlightDuplicates, mistakeLimit, theme |
| Stats | Aggregated metrics by difficulty | gamesPlayed, gamesWon, bestTimeSeconds, streak |

## Data Dictionary
| Field | Type | Constraints | Description |
| --- | --- | --- | --- |
| gameId | String (UUID) | Required, unique | Unique game identifier |
| difficulty | Enum | Easy/Medium/Hard/Expert | Selected difficulty |
| status | Enum | Active/Paused/Won/Abandoned | Current game status |
| boardValues | String | 81 chars, 0-9 | Current board values serialized |
| fixedMask | String | 81 chars, 0/1 | Indicates immutable clue cells |
| solutionValues | String | 81 chars, 1-9 | Solved board for validation/hints |
| notesJson | String | Valid JSON | Candidate notes per cell |
| elapsedSeconds | Int | >= 0 | Active elapsed time |
| mistakes | Int | >= 0 | Number of invalid committed entries |
| hintsUsed | Int | >= 0 | Number of consumed hints |
| updatedAt | Long | Unix epoch ms | Last save timestamp |

## Storage Design
- Database type: Room on top of SQLite.
- Tables:
	- games
	- moves
	- stats
- Preferences: Jetpack DataStore for lightweight settings.
- Partitioning / indexing strategy:
	- Index games(status, updatedAt) for active-game lookup.
	- Index stats(difficulty) unique.
- Backup and retention:
	- Keep last N completed games (default 100) for history.
	- Keep full stats aggregate indefinitely unless reset by user.

## Migration Strategy
- Initial schema: version 1 with games, moves, stats tables.
- Migration tooling: Room migration objects with instrumentation migration tests.
- Rollback approach:
	- Forward-only migrations in production.
	- On unrecoverable schema mismatch, preserve backup snapshot and reset local DB with user warning.
