# 03 - Detailed Design

## Package Breakdown
| Package | Description | Owner |
| --- | --- | --- |
| app | Android app entry, DI wiring | Android Team |
| ui.game | Gameplay UI and ViewModel, input interactions | Android Team |
| domain.usecase | Core game use cases | Core Team |
| domain.model | Domain entities and rules | Core Team |
| puzzle | Generator and solver algorithms | Core Team |
| data | Repositories and local persistence | Android Team |

## Component Details
### Component: GameViewModel
- Responsibilities: hold GameUiState, handle user intents, manage game timer, delegate to use cases and repositories.
- Inputs: UI intents (select cell, enter number).
- Outputs: StateFlow<GameUiState>.
- Dependencies: GameRepository, StartGameUseCase.
- Failure modes: repository write failure, unexpected state.

### Component: StartGameUseCase
- Responsibilities: initialize a new game session with generated puzzle.
- Inputs: difficulty level.
- Outputs: GameSession.
- Dependencies: PuzzleGenerator.
- Failure modes: generation failure.

### Component: PuzzleGenerator
- Responsibilities: create full valid board, remove values according to difficulty, verify uniqueness.
- Inputs: difficulty level and generation seed.
- Outputs: puzzle board + solved board.
- Dependencies: SudokuSolver.
- Failure modes: generation timeout, non-unique puzzle candidate.

### Component: GameRepository
- Responsibilities: persist and load active game, retrieve game history.
- Inputs: game sessions.
- Outputs: Flow<GameSession>, game history list.
- Dependencies: Room DAO.
- Failure modes: data mapping mismatch, database I/O error.

## Sequence / Interaction Notes
- Scenario 1: Start new game
	- UI requests new game with difficulty.
	- ViewModel calls StartGameUseCase.
	- Use case invokes PuzzleGenerator and creates initial GameSession.
	- ViewModel updates state and starts timer.
	- ViewModel calls Repository to save the new game.
- Scenario 2: Make move and auto-save
	- User enters number for a selected cell.
	- ViewModel validates move against solution and updates session state.
	- ViewModel calls Repository to save updated snapshot.
	- UI rerenders board and status from emitted StateFlow.

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
