import { describe, expect, it } from 'vitest'
import {
  Difficulty,
  generatePuzzle,
  generateSolution,
  getConflicts,
  getPeers,
  isSolved,
  toMMSS,
} from './game'

describe('Sudoku game utilities', () => {
  it('formats elapsed seconds as MM:SS', () => {
    expect(toMMSS(0)).toBe('00:00')
    expect(toMMSS(63)).toBe('01:03')
    expect(toMMSS(600)).toBe('10:00')
  })

  it('generates a valid solution with 81 values and unique row/column/box groups', () => {
    const solution = generateSolution()

    expect(solution).toHaveLength(81)
    expect(solution.every((value) => value >= 1 && value <= 9)).toBe(true)

    for (let row = 0; row < 9; row += 1) {
      const rowValues = solution.slice(row * 9, row * 9 + 9)
      expect(new Set(rowValues).size).toBe(9)
    }

    for (let col = 0; col < 9; col += 1) {
      const colValues = solution.filter((_, index) => index % 9 === col)
      expect(new Set(colValues).size).toBe(9)
    }

    for (let boxRow = 0; boxRow < 3; boxRow += 1) {
      for (let boxCol = 0; boxCol < 3; boxCol += 1) {
        const boxValues: number[] = []
        for (let row = 0; row < 3; row += 1) {
          for (let col = 0; col < 3; col += 1) {
            const index = (boxRow * 3 + row) * 9 + boxCol * 3 + col
            boxValues.push(solution[index])
          }
        }
        expect(new Set(boxValues).size).toBe(9)
      }
    }
  })

  it('generates puzzles with the expected number of clues for each difficulty', () => {
    const solution = generateSolution()
    const difficulties: Record<Difficulty, number> = {
      easy: 40,
      medium: 32,
      hard: 26,
    }

    for (const difficulty of Object.keys(difficulties) as Difficulty[]) {
      const puzzle = generatePuzzle(solution, difficulty)
      const empties = puzzle.filter((value) => value === null).length
      expect(empties).toBe(81 - difficulties[difficulty])
      expect(puzzle.length).toBe(81)
    }
  })

  it('returns the correct peers for a board index', () => {
    const peers = getPeers(0)
    expect(peers.has(1)).toBe(true)
    expect(peers.has(9)).toBe(true)
    expect(peers.has(10)).toBe(true)
    expect(peers.has(0)).toBe(false)
    expect(peers.size).toBe(20)
  })

  it('detects conflicts correctly', () => {
    const puzzle = Array(81).fill(null)
    puzzle[0] = 1
    puzzle[1] = 1
    puzzle[9] = 1

    const conflicts = getConflicts(puzzle)

    expect(conflicts.has(0)).toBe(true)
    expect(conflicts.has(1)).toBe(true)
    expect(conflicts.has(9)).toBe(true)
    expect(conflicts.size).toBeGreaterThanOrEqual(3)
  })

  it('checks solved puzzles correctly', () => {
    const solution = generateSolution()
    const puzzle = [...solution]
    expect(isSolved(puzzle, solution)).toBe(true)

    puzzle[0] = null
    expect(isSolved(puzzle, solution)).toBe(false)
  })
})
