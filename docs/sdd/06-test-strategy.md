# 06 - Test Strategy

## Test Levels
- Unit tests:
	- Sudoku rule validation (row/column/subgrid).
	- Puzzle solver and uniqueness checks.
	- Use cases: make move, undo/redo, hint, completion detection.
- Integration tests:
	- Repository with Room test DB.
	- DataStore settings read/write.
	- ViewModel state transitions with fake repositories.
- End-to-end tests:
	- Compose UI tests for core user journeys:
		- start game
		- play with notes
		- win flow
		- save/restore game

## Test Scope
| Area | In Scope | Out of Scope |
| --- | --- | --- |
| Domain rules | Full | N/A |
| Persistence | Full local DB/settings | Cloud sync |
| UI gameplay flows | Core happy path + key edge cases | Deep visual snapshot matrix |
| Performance | Startup and move latency smoke checks | Full benchmarking suite in MVP |

## Quality Gates
- Minimum coverage target:
	- Domain + puzzle-engine >= 80%
	- Data layer >= 70%
- Required checks before merge:
	- All unit/integration/UI tests pass.
	- Static analysis (ktlint/detekt) passes.
	- No high-severity open defects for changed feature.

## Test Data Strategy
- Test data source:
	- Deterministic seeds for generated puzzles in tests.
	- Curated board fixtures for valid/invalid scenarios.
- Data privacy considerations:
	- No PII used in test datasets.

## Defect Tracking
- Severity definition:
	- Sev 1: Crash/data loss/blocking core flow.
	- Sev 2: Major feature malfunction with workaround.
	- Sev 3: Minor issue/cosmetic defect.
- Triage workflow:
	- Daily triage during active sprint.
	- Sev 1 fixed immediately or rolled back.
