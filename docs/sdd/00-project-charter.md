# 00 - Project Charter

## Project Name
Sudoku Android Game

## Purpose
Deliver a polished, offline-first Sudoku game for Android using Kotlin, with multiple difficulties, smooth gameplay, and progress persistence.

## Scope
- In scope:
	- Native Android app written in Kotlin.
	- Sudoku gameplay with 9x9 board and pencil notes.
	- Puzzle generation and solution validation.
	- Difficulty levels (Easy, Medium, Hard, Expert).
	- Timer, undo/redo, hints, and mistake tracking.
	- Local persistence for active games, completed games, and settings.
	- Basic statistics (wins, best time per difficulty, streak).
- Out of scope:
	- Multiplayer and online leaderboards.
	- User accounts and cloud sync.
	- iOS or cross-platform support.
	- In-app purchases and ads in initial release.

## Stakeholders
| Role | Name | Responsibility |
| --- | --- | --- |
| Product Owner | TBD | Defines gameplay priorities and acceptance criteria |
| Tech Lead | TBD | Owns architecture and implementation quality |
| QA Lead | TBD | Defines test strategy and release quality gates |

## Success Criteria
- [ ] App installs and runs on Android 8.0+ without crashes in core flows.
- [ ] Player can start, play, pause, and complete puzzles at all difficulty levels.
- [ ] Saved game state restores correctly after app restart.
- [ ] Core UX actions respond in under 100 ms on target mid-range device.
- [ ] Unit/integration/UI test suite passes in CI with agreed quality gate.

## Constraints
- Timeline: MVP in 8 weeks.
- Budget: Small team, no paid backend services.
- Technology:
	- Kotlin
	- Android Jetpack libraries
	- Jetpack Compose UI
	- Room database
	- Coroutines/Flow

## Assumptions
- Primary target is portrait phone layout; tablet support is best effort for MVP.
- App is fully offline; all puzzle generation is local.
- Team is familiar with Kotlin and MVVM architecture.

## Revision History
| Date | Version | Author | Notes |
| --- | --- | --- | --- |
| 2026-07-05 | 1.0 | Copilot | Initial charter for Kotlin Android Sudoku project |
