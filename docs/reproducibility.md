# Reproducibility

## Environment capture
Record the following for each experiment:
- Device model and OS version
- App build hash and configuration
- Study protocol version

## Experiment logging
Create a new folder in `experiments/` for each run with:
- `README.md` summary of the run
- `config.json` or equivalent settings
- `metrics.csv` or `metrics.json` outputs

## Determinism and variance
- Note any non-deterministic components (auto exposure, focus behavior).
- Repeat runs across at least two devices.

## Data availability
Data is stored under `data/` and may be redacted for privacy. See
`docs/data-management.md` for rules.
