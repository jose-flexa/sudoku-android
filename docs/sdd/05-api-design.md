# 05 - API Design

## API Overview
This project has no external HTTP API in MVP. This section defines internal contracts between UI, domain, and data layers.

- Protocol: In-process Kotlin interfaces and function contracts.
- Base URL: Not applicable.
- Versioning strategy: Semantic versioning for repository/domain interfaces when breaking changes occur.

## Endpoints
| Endpoint | Method | Description | Auth |
| --- | --- | --- | --- |
| GameRepository.getActiveGame() | Function | Returns current active game Flow | N/A |
| GameRepository.saveGame(session) | Function | Persists game session | N/A |
| GameRepository.getGameHistory() | Function | Returns flow of all game sessions | N/A |
| StartGameUseCase.invoke(difficulty) | Function | Creates and persists a new game session | N/A |
| PuzzleGenerator.generate(difficulty) | Function | Returns puzzle and solution arrays | N/A |
| SudokuSolver.solve(board) | Function | Solves candidate board in-place | N/A |

## Request/Response Examples
### Example Contract: startNewGame
- Request:
	- input: difficulty = Hard
- Response:
	- output:
		- gameId: UUID
		- board: initial puzzle board
		- elapsedSeconds: 0
		- status: Active
- Error codes:
	- GameError.GenerationFailed
	- GameError.PersistenceFailed

### Example Contract: saveGame
- Request:
	- input: GameState snapshot after move
- Response:
	- output: Unit (success)
- Error codes:
	- GameError.PersistenceFailed

## Contract and Validation Rules
- Input validation:
	- Cell index must be in range 0-80.
	- Input value must be 0-9 where 0 means clear.
	- Moves on fixed cells are rejected.
- Output guarantees:
	- Returned board state is immutable from caller perspective.
	- Generated puzzle must have unique solution.
- Idempotency:
	- saveGame is idempotent for same gameId + snapshot version.
	- completeGame is idempotent once status is Won.
