# 02 - System Architecture

## Context Overview
The application is a standalone Android client. It contains all gameplay logic, puzzle generation, validation, persistence, and analytics storage on-device. No backend dependency is required for core gameplay.

## High-Level Components
| Component | Responsibility | Interfaces |
| --- | --- | --- |
| UI Layer (Compose Screens) | Render board, controls, settings, and stats; collect user events | ViewModel state/events |
| Presentation Layer (ViewModels) | UI state management, orchestrate game use cases, expose state via StateFlow | Domain use cases, navigation events |
| Domain Layer (Use Cases + Models) | Business rules for moves, validation, completion, timer, hint | Repositories, pure Kotlin logic |
| Puzzle Engine | Generate valid puzzles, solve board, enforce unique solution | Domain logic |
| Data Layer (Repositories) | Persist active game state | Room DAO |
| Local Storage | SQLite via Room | DAO APIs |

## Architecture Diagram
Add diagrams in docs/diagrams/ and reference them here.

Suggested files:
- docs/diagrams/context.mmd
- docs/diagrams/component.mmd
- docs/diagrams/sequence-make-move.mmd

## Data Flow
1. User taps a cell and selects a number in UI.
2. ViewModel processes the move, validates it against the solution, and updates the game session.
3. ViewModel dispatches the updated game to the Repository.
4. Repository persists the updated game snapshot to the database.
5. ViewModel emits new immutable UI state to Compose.

## Technology Choices
| Layer | Technology | Rationale |
| --- | --- | --- |
| Frontend | Kotlin + Jetpack Compose | Modern declarative Android UI, state-driven rendering |
| Presentation | AndroidX ViewModel + StateFlow | Lifecycle-aware state management |
| Domain | Kotlin packages + Coroutines | Testable business logic and async operations |
| Data | Room | Reliable local persistence |
| DI | Hilt | Standard Android dependency injection |
| Testing | JUnit, MockK, Turbine, Espresso/Compose UI Test | Unit + integration + UI verification |

## Security Considerations
- Authentication: Not required for offline MVP.
- Authorization: Not applicable in single-user local mode.
- Data protection:
	- No sensitive personal data stored.
	- Use private app storage only.
	- Avoid logging board state in release logs.

## Scalability and Reliability
- Scaling strategy: Not server-scaled; optimize local compute for puzzle generation and rendering.
- Availability targets: Game fully usable offline with graceful app lifecycle restore.
- Reliability controls:
	- Persist game after each committed move.
	- Recover active game after process death.
	- Defensive checks for corrupted save data fallback.
