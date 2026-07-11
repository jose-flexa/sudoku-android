# 03 - Detailed Design

## Module Breakdown
| Module | Description | Owner |
| --- | --- | --- |
| app | Android app entry, navigation, DI wiring | Android Team |
| feature-game | Gameplay UI and ViewModel, input interactions | Android Team |
| feature-settings | Settings screen and preference state | Android Team |
| feature-stats | Statistics and history UI | Android Team |
| domain | Use cases, board rules, move processing, timer control | Core Team |
| puzzle-engine | Generator and solver algorithms | Core Team |
| data | Repositories and persistence implementation | Android Team |

## Component Details
### Component: GameViewModel
- Responsibilities: hold GameUiState, handle user intents, call use cases, expose one-shot events.
- Inputs: UI intents (select cell, enter value, note mode toggle, hint, undo, redo, pause).
- Outputs: StateFlow<GameUiState>, SharedFlow<GameEvent>.
- Dependencies: GameRepository, MakeMoveUseCase, UndoUseCase, HintUseCase, TimerController.
- Failure modes: invalid state transitions, repository write failure.

### Component: MakeMoveUseCase
- Responsibilities: apply one user move and enforce Sudoku rules.
- Inputs: current board, selected cell, input value, note mode flag.
- Outputs: updated board, move result (valid/invalid), optional completion event.
- Dependencies: BoardValidator, MoveHistoryManager.
- Failure modes: illegal move on fixed cell, invalid candidate outside 1-9.

### Component: PuzzleGenerator
- Responsibilities: create full valid board, remove values according to difficulty, verify uniqueness.
- Inputs: difficulty level and generation seed.
- Outputs: puzzle board + solved board.
- Dependencies: SudokuSolver.
- Failure modes: generation timeout, non-unique puzzle candidate.

### Component: GameRepository
- Responsibilities: persist and load active game, archive completed games.
- Inputs: game snapshots, settings updates.
- Outputs: Flow<GameSnapshot>, game history list.
- Dependencies: Room DAO, DataStore.
- Failure modes: data mapping mismatch, database I/O error.

## Sequence / Interaction Notes
- Scenario 1: Start new game
	- UI requests difficulty.
	- ViewModel calls StartGameUseCase.
	- Use case invokes PuzzleGenerator and creates initial GameState.
	- Repository persists game and ViewModel emits state.
- Scenario 2: Make move and auto-save
	- User enters number.
	- ViewModel invokes MakeMoveUseCase.
	- Domain returns updated state and validity feedback.
	- Repository saves snapshot.
	- UI rerenders board and status.

## Error Handling Strategy
- Validation errors:
	- Show inline feedback (conflict highlight or toast/snackbar).
	- Keep app responsive and do not crash on invalid input.
- System errors:
	- Log non-fatal exception in debug.
	- Show user-friendly fallback message.
	- Attempt to restore last known valid game snapshot.
- Retry behavior:
	- For generation failure, retry with bounded attempts and new seed.
	- For persistence failure, retry once and keep state in memory until next write.
