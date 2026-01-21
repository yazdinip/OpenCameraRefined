# Research Code

This directory holds analysis modules used by the research program. Keep the
Android application in `software/opencamera`.

Current modules:
- `analysis/resource_audit.py` scans the Android `res/` tree and reports counts.

Add new analysis packages under `src/analysis/` and keep modules
dependency-light so they can run on a lab workstation without extra setup.
