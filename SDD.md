# Sudoku Web App Software Design Description

## 1. Introduction

This document describes the software design for the Sudoku web application located in this repository. It captures functional requirements, system architecture, data model, interface expectations, and planned verification activities.

### 1.1 Purpose

The Sudoku app aims to provide a responsive, accessible, and offline-capable Sudoku gameplay experience in the browser. The design focuses on clear gameplay mechanics, difficulty levels, keyboard and pointer controls, puzzle validation, and state management.

### 1.2 Scope

The system supports:
- Puzzle generation with difficulty-adjusted clue counts.
- Board rendering and cell selection.
- Input via on-screen keypad and keyboard.
- Note-taking mode for candidates.
- Conflict highlighting and live validation.
- Hint usage and mistake tracking.
- Timer, error limits, and game completion states.
- Progressive Web App features for offline access.

## 2. Functional Requirements

1. New game generation
   - The application must create a fresh valid Sudoku board on load and when the player requests a new game.
   - Difficulty options must be available: easy (40 clues), medium (32 clues), hard (26 clues).

2. Board interaction
   - The board must display 81 cells arranged in a 9x9 grid.
   - Users can select a cell by clicking or navigating with arrow keys.
   - Selected cells are visually distinguished.

3. Number entry
   - Users may enter digits 1 through 9 using the on-screen keypad or keyboard.
   - When a digit is fully placed in the puzzle (all nine instances are present), that digit is removed from the on-screen keypad.
   - Users may erase cell values with an erase button, Backspace, Delete, or the 0 key.
   - Fixed cells from the initial puzzle cannot be changed.

4. Note mode
   - Users can toggle note mode and add or remove pencil marks in empty cells.
   - Notes must not be stored for fixed or already filled cells.

5. Validation and conflicts
   - Rows, columns, and 3x3 boxes must not contain duplicate digits.
   - Duplicate values must be highlighted as conflicts immediately.
   - Correct entries clear conflicting notes from peer cells in the same row, column, and box.

6. Hint and mistakes
   - Players start with three hints.
   - Using a hint fills the selected editable cell with the correct solution digit.
   - Mistakes increment when a wrong digit is entered; the game ends after three mistakes.

7. Completion
   - When every cell matches the solution, the game enters a solved state and locks input.
   - If the player accumulates three mistakes, the full solution is revealed and input is locked.

8. Accessibility and offline support
   - The application must provide accessible labels for the board and controls.
   - The PWA service worker must register immediately to support offline use.

## 3. Architecture Overview

The application uses a modular design with two main layers:
- Pure game logic module (`src/game.ts`)
- Browser UI module (`src/main.ts`)

### 3.1 Module responsibilities

- `src/game.ts`
  - Encapsulates puzzle generation and validation logic.
  - Exposes helper functions for peer calculation, conflict detection, timer formatting, and completion checks.

- `src/main.ts`
  - Manages UI rendering, event listeners, and game state.
  - Calls logic functions from `src/game.ts` to keep presentation separated from rules.

## 4. Data Model

The core game state includes:
- `puzzle`: `Array<number | null>` - current board values, with `null` for empty cells.
- `solution`: `number[]` - the completed board corresponding to the puzzle.
- `fixed`: `boolean[]` - indicates which cells are prefilled and immutable.
- `notes`: `Set<number>[]` - candidate annotations for each cell.
- `conflicts`: `Set<number>` - indices of cells currently in conflict.
- `selected`: `number` - index of the active cell.
- `difficulty`: `Difficulty` - selected difficulty level.
- `noteMode`: `boolean` - whether candidate mode is enabled.
- `mistakes`: `number` - count of incorrect entries.
- `hints`: `number` - remaining hints.
- `elapsedSeconds`: `number` - game timer state.
- `locked`: `boolean` - whether user input is disabled.

## 5. Interface Requirements

The UI exposes the following controls:
- Difficulty select dropdown.
- New game button.
- Hint button.
- Note mode toggle.
- 9x9 board rendered as grid buttons.
- On-screen keypad with digits 1-9 and erase; digits are hidden from the keypad once all nine instances are placed on the board.
- Live timer, mistakes counter, and hint counter.

Keyboard support:
- Numeric keys 1-9 to fill the selected cell.
- Backspace/Delete/0 to erase cell contents.
- Arrow keys to move selection.
- `n` key to toggle note mode.

## 6. Test Plan and Verification

The application is verified through automated unit tests for the game logic module and manual acceptance checks for the UI.
- Unit tests ensure correct puzzle generation, conflict detection, and completion checks.
- Manual scenarios validate game flow, UI state updates, and accessibility labels.

## 7. Traceability

Requirements are linked to source modules and tests:
- Puzzle generation: `src/game.ts`, `src/game.test.ts`
- Conflict detection: `src/game.ts`, `src/game.test.ts`
- Completion checks: `src/game.ts`, `src/game.test.ts`
- UI state and rendering: `src/main.ts`
- PWA registration: `src/main.ts`, `vite.config.ts`

## 8. Nonfunctional Requirements

- The app must render within 1 second on modern mobile browsers.
- The game must remain responsive during user interaction.
- The app should operate offline after the first successful load.
- The UI must support pointer and keyboard input.

## 9. Assumptions and Constraints

- The browser supports modern DOM APIs and the Wake Lock API.
- The app is intended for a single local player and does not require server-side storage.
- The puzzle generator does not guarantee a unique solution for every board, but it produces valid completed boards and a playable puzzle state.
