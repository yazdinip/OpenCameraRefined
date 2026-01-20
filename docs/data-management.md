# Data Management Plan

## Data types
- Interaction logs and timing measurements
- Device telemetry snapshots
- Optional qualitative notes and survey responses

## Data handling
- No raw photos are stored by default.
- Logs are stored locally in `data/raw/` and anonymized into `data/processed/`.
- Identifiers are replaced with randomized study IDs.

## Access control
- Only research team members can access raw logs.
- Processed data is shared through the repository with sensitive fields removed.

## Retention
- Raw logs retained for 12 months after study completion.
- Processed and aggregate data retained for replication.

## Data dictionary
A per-experiment data dictionary should be added to each entry in
`experiments/`.
