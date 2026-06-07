export type Difficulty = 'easy' | 'medium' | 'hard'

export const DIFFICULTY_CLUES: Record<Difficulty, number> = {
  easy: 40,
  medium: 32,
  hard: 26,
}

export function shuffle<T>(items: T[]): T[] {
  const copy = [...items]
  for (let i = copy.length - 1; i > 0; i -= 1) {
    const j = Math.floor(Math.random() * (i + 1))
    ;[copy[i], copy[j]] = [copy[j], copy[i]]
  }
  return copy
}

export function baseValue(row: number, col: number): number {
  return ((row * 3 + Math.floor(row / 3) + col) % 9) + 1
}

export function generateSolution(): number[] {
  const digits = shuffle([1, 2, 3, 4, 5, 6, 7, 8, 9])
  const rowBands = shuffle([0, 1, 2])
  const colStacks = shuffle([0, 1, 2])
  const rows = rowBands.flatMap((band) => shuffle([0, 1, 2]).map((row) => band * 3 + row))
  const cols = colStacks.flatMap((stack) =>
    shuffle([0, 1, 2]).map((col) => stack * 3 + col),
  )

  return rows.flatMap((row) =>
    cols.map((col) => {
      const value = baseValue(row, col)
      return digits[value - 1]
    }),
  )
}

export function generatePuzzle(solution: number[], difficulty: Difficulty): Array<number | null> {
  const puzzle = [...solution]
  const clues = DIFFICULTY_CLUES[difficulty]
  const removable = shuffle(Array.from({ length: 81 }, (_, i) => i)).slice(0, 81 - clues)
  removable.forEach((index) => {
    puzzle[index] = 0
  })
  return puzzle.map((value) => (value === 0 ? null : value))
}

export function toMMSS(total: number): string {
  const minutes = Math.floor(total / 60)
  const seconds = total % 60
  return `${String(minutes).padStart(2, '0')}:${String(seconds).padStart(2, '0')}`
}

export function getPeers(index: number): Set<number> {
  const row = Math.floor(index / 9)
  const col = index % 9
  const boxRow = Math.floor(row / 3) * 3
  const boxCol = Math.floor(col / 3) * 3
  const peers = new Set<number>()

  for (let c = 0; c < 9; c += 1) {
    peers.add(row * 9 + c)
  }
  for (let r = 0; r < 9; r += 1) {
    peers.add(r * 9 + col)
  }
  for (let r = boxRow; r < boxRow + 3; r += 1) {
    for (let c = boxCol; c < boxCol + 3; c += 1) {
      peers.add(r * 9 + c)
    }
  }

  peers.delete(index)
  return peers
}

export function getConflicts(puzzle: Array<number | null>): Set<number> {
  const conflicts = new Set<number>()

  for (let index = 0; index < puzzle.length; index += 1) {
    const value = puzzle[index]
    if (!value) continue

    for (const peer of getPeers(index)) {
      if (puzzle[peer] === value) {
        conflicts.add(index)
        conflicts.add(peer)
      }
    }
  }

  return conflicts
}

export function isSolved(puzzle: Array<number | null>, solution: number[]): boolean {
  if (puzzle.length !== solution.length) {
    return false
  }
  return puzzle.every((value, index) => value === solution[index])
}
