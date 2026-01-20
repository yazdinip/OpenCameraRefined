# Contributing

Thanks for contributing to OpenCameraRefined Research. This repository is a
research testbed, so changes should emphasize reproducibility and clear
documentation.

## What to update
- Research documentation in `docs/`
- Experiment logs in `experiments/`
- Analysis code in `src/`
- Manuscript updates in `paper/`

## Workflow
1. Open or update an experiment folder following `docs/experiment-registry.md`.
2. Record configuration and metrics alongside results.
3. Update relevant documentation when protocols change.

## Data handling
- Do not commit raw participant data.
- Sanitize logs before adding them to the repository.
- Follow `docs/data-management.md` for retention and access rules.

## Code standards
- Keep scripts small and documented.
- Prefer clear naming over cleverness.
- Record build or runtime assumptions in the relevant README.
