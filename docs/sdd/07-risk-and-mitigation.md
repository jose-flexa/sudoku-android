# 07 - Risk and Mitigation

## Risks
| ID | Risk | Impact | Likelihood | Mitigation | Owner |
| --- | --- | --- | --- | --- | --- |
| R-001 | Puzzle generator produces non-unique puzzles | High | Medium | Add uniqueness verification test in generation pipeline | Core Team |
| R-002 | Performance degradation on low-end devices | High | Medium | Profile generation/rendering; optimize algorithms and recomposition | Android Team |
| R-003 | Data corruption after app/process kill | High | Low | Auto-save after each move and restore fallback snapshot | Android Team |
| R-004 | Undo/redo bugs causing inconsistent state | Medium | Medium | Immutable state model and dedicated history tests | Core Team |
| R-005 | Accessibility gaps delay release | Medium | Medium | Include accessibility checks in definition of done | QA Lead |

## Dependency Risks
- External service dependency: None for gameplay MVP.
- Team dependency:
	- Strong dependency on puzzle-engine expertise.
	- Mitigation: pair programming + design walkthroughs.

## Compliance and Security Risks
- Regulatory concerns:
	- Minimal, no account data collection in MVP.
- Security concerns:
	- Reverse engineering of local game state is low risk.
	- Mitigation: avoid storing sensitive data and hardcoded secrets.

## Monitoring Risks Over Time
- Review frequency: Weekly risk review in sprint planning/refinement.
- Trigger thresholds:
	- Crash-free sessions below 99.5%.
	- Puzzle generation p95 > 2.0 s.
	- Open Sev 1 bugs > 0 near release window.
