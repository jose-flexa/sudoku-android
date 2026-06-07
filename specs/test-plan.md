# Sudoku Web App Test Plan

## Overview

This test plan defines the verification strategy for the Sudoku web application. It includes test objectives, scope, and detailed test cases for the pure game logic module and the user-facing behavior.

## Test Objectives

- Confirm puzzle generation creates valid Sudoku solutions.
- Ensure difficulty levels produce the correct number of clues.
- Validate peer calculation and conflict detection.
- Confirm solved-state detection and elapsed time formatting.
- Verify user interaction flows through manual acceptance and state transitions.

## Automated Test Cases

1. Format elapsed seconds
   - Input: 0, 63, 600
   - Expected: `00:00`, `01:03`, `10:00`

2. Generate valid solution
   - Ensure output length 81
   - Ensure values are between 1 and 9
   - Ensure every row, column, and 3x3 box contains unique digits

3. Generate puzzle by difficulty
   - easy: 40 clues
   - medium: 32 clues
   - hard: 26 clues
   - Ensure puzzle length is 81

4. Calculate peers correctly
   - For index 0, expect peers in first row, first column, and first box
   - Expect index 0 is excluded from its own peer set

5. Detect conflicts
   - Create a puzzle with duplicate digits in peers
   - Expect duplicate indices to appear in the conflict set

6. Solve detection
   - A board identical to the solution must be solved
   - A board with a missing or incorrect value must not be solved

## Manual Acceptance Criteria

- The game loads to a new medium difficulty board.
- The selected cell updates when the user clicks a cell or navigates with arrow keys.
- Keyboard number input and erase controls work correctly.
- Note mode toggles and allows pencil marks only in empty cells.
- Messages, timer, mistakes, and hint counters update appropriately.
- The game locks after solving or after three mistakes.
- PWA registration completes and offline caching is available.

## Traceability

The following test cases map to functional requirements:
- Puzzle generation test ↔ New game generation, difficulty rules
- Peer and conflict tests ↔ Validation and conflict highlighting
- Solve detection test ↔ Completion behavior
- Time formatting test ↔ Timer display

## Tools and Execution

- Run automated tests with `npm test`
- Use `vitest` for unit coverage of the logic module
- Confirm manual acceptance by running `npm run dev` and exercising the UI
