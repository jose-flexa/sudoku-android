import './style.css'
import { registerSW } from 'virtual:pwa-register'
import {
  type Difficulty,
  generatePuzzle,
  generateSolution,
  getConflicts,
  getPeers,
  isSolved,
  toMMSS,
} from './game'

const app = document.querySelector<HTMLDivElement>('#app')

if (!app) {
  throw new Error('App container not found')
}
let wakeLock: WakeLockSentinel | null = null;

async function requestWakeLock() {
  try {
    wakeLock = await navigator.wakeLock.request('screen');
    console.log('Screen Wake Lock is active');
  } catch (err: any) {
    console.error(`${err.name}, ${err.message}`);
  }
}

// Re-request wake lock if the app was minimized and brought back
document.addEventListener('visibilitychange', async () => {
  if (wakeLock !== null && document.visibilityState === 'visible') {
    await requestWakeLock();
  }
});
requestWakeLock()
registerSW({ immediate: true })

function setupFullscreenOnFirstInteraction(): void {
  const isStandalone =
    window.matchMedia('(display-mode: standalone)').matches ||
    window.matchMedia('(display-mode: fullscreen)').matches

  if (isStandalone || document.fullscreenElement || !document.documentElement.requestFullscreen) {
    return
  }

  let attempted = false
  const tryFullscreen = async () => {
    if (attempted) return
    attempted = true
    document.removeEventListener('pointerdown', tryFullscreen)
    document.removeEventListener('keydown', tryFullscreen)

    try {
      await document.documentElement.requestFullscreen()
    } catch {
      // Browser can reject fullscreen requests depending on user settings/policies.
    }
  }

  document.addEventListener('pointerdown', tryFullscreen, { once: true })
  document.addEventListener('keydown', tryFullscreen, { once: true })
}

setupFullscreenOnFirstInteraction()

const state = {
  puzzle: [] as Array<number | null>,
  solution: [] as number[],
  fixed: [] as boolean[],
  notes: Array.from({ length: 81 }, () => new Set<number>()),
  conflicts: new Set<number>(),
  selected: 0,
  difficulty: 'medium' as Difficulty,
  noteMode: false,
  mistakes: 0,
  hints: 3,
  elapsedSeconds: 0,
  timer: 0 as number | undefined,
  locked: false,
  highlightDigit: null as number | null,
}

app.innerHTML = `
  <main class="shell">
    <header class="topbar">
      <div>
        <h1>Sudoku</h1>
      </div>
      <div class="stats">
        <div><span>Tempo</span><strong id="time">00:00</strong></div>
        <div><span>Erros</span><strong id="mistakes">0/3</strong></div>
        <div><span>Dicas</span><strong id="hints">3</strong></div>
      </div>
    </header>

    <section class="panel">
      <div class="toolbar">
        <label>
          Dificuldade
          <select id="difficulty" aria-label="Selecionar dificuldade">
            <option value="easy">Fácil</option>
            <option value="medium" selected>Médio</option>
            <option value="hard">Difícil</option>
          </select>
        </label>
        <button id="new-game" type="button">Novo Jogo</button>
        <button id="hint" type="button">Dica</button>
        <button id="note-mode" type="button" aria-pressed="false">Notas: Desligadas</button>
      </div>

      <div id="board" class="board" role="grid" aria-label="Tabuleiro de Sudoku"></div>

      <div class="keypad" id="keypad"></div>

      <p class="message" id="message" aria-live="polite">Preencha a grade para que cada linha, coluna e bloco tenha os números de 1 a 9.</p>
    </section>
  </main>
`

function mustSelect<T extends Element>(selector: string): T {
  const element = document.querySelector<T>(selector)
  if (!element) {
    throw new Error(`Missing required element: ${selector}`)
  }
  return element
}

const boardEl = mustSelect<HTMLDivElement>('#board')
const keypadEl = mustSelect<HTMLDivElement>('#keypad')
const timeEl = mustSelect<HTMLElement>('#time')
const mistakesEl = mustSelect<HTMLElement>('#mistakes')
const hintsEl = mustSelect<HTMLElement>('#hints')
const messageEl = mustSelect<HTMLElement>('#message')
const difficultyEl = mustSelect<HTMLSelectElement>('#difficulty')
const newGameEl = mustSelect<HTMLButtonElement>('#new-game')
const hintEl = mustSelect<HTMLButtonElement>('#hint')
const noteModeEl = mustSelect<HTMLButtonElement>('#note-mode')

function setMessage(text: string): void {
  messageEl.textContent = text
}

function recalculateConflicts(): void {
  state.conflicts = getConflicts(state.puzzle)
}

function updateTopStats(): void {
  timeEl.textContent = toMMSS(state.elapsedSeconds)
  mistakesEl.textContent = `${state.mistakes}/3`
  hintsEl.textContent = String(state.hints)
  noteModeEl.textContent = state.noteMode ? 'Notas: Ligadas' : 'Notas: Desligadas'
  noteModeEl.setAttribute('aria-pressed', String(state.noteMode))
}

function renderBoard(): void {
  boardEl.innerHTML = ''

  for (let index = 0; index < 81; index += 1) {
    const cell = document.createElement('button')
    const value = state.puzzle[index]
    const notes = state.notes[index]
    const row = Math.floor(index / 9)
    const col = index % 9
    const selected = state.selected === index
    const related = getPeers(state.selected).has(index)
    const sameDigit = value !== null && state.highlightDigit === value

    cell.type = 'button'
    cell.className = 'cell'
    cell.dataset.index = String(index)
    cell.setAttribute('role', 'gridcell')
    cell.setAttribute('aria-label', `Linha ${row + 1}, Coluna ${col + 1}`)

    if (state.fixed[index]) cell.classList.add('fixed')
    if (selected) cell.classList.add('selected')
    if (related) cell.classList.add('related')
    if (sameDigit) cell.classList.add('same-digit')
    if (state.conflicts.has(index)) cell.classList.add('conflict')
    if (row % 3 === 2) cell.classList.add('box-bottom')
    if (col % 3 === 2) cell.classList.add('box-right')

    if (value) {
      cell.innerHTML = `<span class="value">${value}</span>`
    } else {
      const notesMarkup = Array.from({ length: 9 }, (_, n) => {
        const note = n + 1
        return `<span class="note ${notes.has(note) ? 'on' : ''}">${note}</span>`
      }).join('')
      cell.innerHTML = `<span class="notes">${notesMarkup}</span>`
    }

    cell.addEventListener('click', () => {
      if (state.locked) return
      state.selected = index
      state.highlightDigit = state.puzzle[index]
      renderBoard()
    })

    boardEl.appendChild(cell)
  }
}

function renderKeypad(): void {
  keypadEl.innerHTML = ''
  const digitCounts = state.puzzle.reduce<Record<number, number>>((counts, value) => {
    if (value !== null) {
      counts[value] = (counts[value] || 0) + 1
    }
    return counts
  }, {})
  const keys: Array<number | 'erase'> = [1, 2, 3, 4, 5, 6, 7, 8, 9, 'erase']

  keys.forEach((key) => {
    if (typeof key === 'number' && digitCounts[key] >= 9) {
      return
    }

    const button = document.createElement('button')
    button.type = 'button'
    button.className = 'key'
    button.textContent = key === 'erase' ? 'Apagar' : String(key)
    button.addEventListener('click', () => {
      if (state.locked) return
      inputCell(key === 'erase' ? null : key)
    })
    keypadEl.appendChild(button)
  })
}

function revealLoss(): void {
  state.locked = true
  state.puzzle = [...state.solution]
  state.notes = Array.from({ length: 81 }, () => new Set<number>())
  recalculateConflicts()
  renderBoard()
  renderKeypad()
  setMessage('Sem vidas restantes. Solução revelada. Toque em Novo Jogo para tentar novamente.')
}

function inputCell(value: number | null): void {
  const index = state.selected
  if (state.fixed[index]) return

  state.highlightDigit = value

  if (value === null) {
    state.puzzle[index] = null
    state.notes[index].clear()
    recalculateConflicts()
    renderBoard()
    renderKeypad()
    return
  }

  if (state.noteMode) {
    if (state.puzzle[index]) return
    if (state.notes[index].has(value)) {
      state.notes[index].delete(value)
    } else {
      state.notes[index].add(value)
    }
    renderBoard()
    return
  }

  if (value !== state.solution[index]) {
    state.mistakes += 1
    updateTopStats()
    setMessage(`Jogada errada. Restam ${Math.max(3 - state.mistakes, 0)} vidas.`)
    renderBoard()
    if (state.mistakes >= 3) {
      revealLoss()
    }
    return
  }

  state.puzzle[index] = value
  state.notes[index].clear()

  for (const peer of getPeers(index)) {
    state.notes[peer].delete(value)
  }

  recalculateConflicts()
  renderBoard()
  renderKeypad()

  if (isSolved(state.puzzle, state.solution)) {
    state.locked = true
    setMessage(`Resolvido em ${toMMSS(state.elapsedSeconds)} com ${state.mistakes} erros. Ótima partida.`)
  }
}

function startTimer(): void {
  if (state.timer) {
    window.clearInterval(state.timer)
  }
  state.timer = window.setInterval(() => {
    if (state.locked) return
    state.elapsedSeconds += 1
    updateTopStats()
  }, 1000)
}

function startGame(difficulty: Difficulty): void {
  state.solution = generateSolution()
  state.puzzle = generatePuzzle(state.solution, difficulty)
  state.fixed = state.puzzle.map((value) => value !== null)
  state.notes = Array.from({ length: 81 }, () => new Set<number>())
  state.conflicts.clear()
  state.selected = state.puzzle.findIndex((value) => value === null)
  state.selected = state.selected === -1 ? 0 : state.selected
  state.difficulty = difficulty
  state.noteMode = false
  state.mistakes = 0
  state.hints = 3
  state.elapsedSeconds = 0
  state.locked = false
  state.highlightDigit = null

  recalculateConflicts()
  updateTopStats()
  renderBoard()
  renderKeypad()
  setMessage('Novo tabuleiro gerado. Boa sorte.')
  startTimer()
}

function giveHint(): void {
  if (state.locked || state.hints <= 0) return
  const index = state.selected
  if (state.fixed[index]) {
    setMessage('Selecione uma célula editável para usar uma dica.')
    return
  }
  if (state.puzzle[index] === state.solution[index]) {
    setMessage('A célula selecionada já está correta.')
    return
  }

  state.puzzle[index] = state.solution[index]
  state.notes[index].clear()
  state.hints -= 1
  recalculateConflicts()
  updateTopStats()
  renderBoard()
  renderKeypad()
  setMessage('Dica usada.')

  if (isSolved(state.puzzle, state.solution)) {
    state.locked = true
    setMessage(`Resolvido em ${toMMSS(state.elapsedSeconds)}. Mandou bem.`)
  }
}

document.addEventListener('keydown', (event) => {
  if (state.locked) return

  if (event.key >= '1' && event.key <= '9') {
    inputCell(Number(event.key))
    return
  }

  if (event.key === 'Backspace' || event.key === 'Delete' || event.key === '0') {
    inputCell(null)
    return
  }

  if (event.key.toLowerCase() === 'n') {
    state.noteMode = !state.noteMode
    updateTopStats()
    return
  }

  const row = Math.floor(state.selected / 9)
  const col = state.selected % 9

  if (event.key === 'ArrowUp') {
    state.selected = ((row + 8) % 9) * 9 + col
    renderBoard()
  } else if (event.key === 'ArrowDown') {
    state.selected = ((row + 1) % 9) * 9 + col
    renderBoard()
  } else if (event.key === 'ArrowLeft') {
    state.selected = row * 9 + ((col + 8) % 9)
    renderBoard()
  } else if (event.key === 'ArrowRight') {
    state.selected = row * 9 + ((col + 1) % 9)
    renderBoard()
  }
})

difficultyEl.addEventListener('change', () => {
  startGame(difficultyEl.value as Difficulty)
})

newGameEl.addEventListener('click', () => {
  startGame(state.difficulty)
})

hintEl.addEventListener('click', () => {
  giveHint()
})

noteModeEl.addEventListener('click', () => {
  state.noteMode = !state.noteMode
  updateTopStats()
})

renderKeypad()
startGame('medium')
