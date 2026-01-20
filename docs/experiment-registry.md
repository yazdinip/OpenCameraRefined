# Experiment Registry

Each experiment lives in `experiments/<experiment-id>/` and must include:
- `README.md` with goal, hypothesis, and protocol version
- `config.*` for app and instrumentation settings
- `metrics.*` for raw or aggregated outputs
- `notes.md` for anomalies and deviations

## Naming convention
Use `YYYY-MM-DD_short-title` (example: `2026-01-20_accessibility-gesture`).

## Review checklist
- Protocol version matches `docs/methodology.md`
- Consent procedure followed
- Data dictionary updated
