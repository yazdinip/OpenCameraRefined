# OpenCameraRefined Research

OpenCameraRefined Research is a study-oriented refactor of the Open Camera
application, built to investigate accessible mobile capture workflows and
on-device vision features. The repository is organized as a research program
with clear documentation, experiment tracking, and reproducibility guidance.

## Research focus
- Accessible capture interactions (gesture-first, voice prompts, large-touch UI)
- On-device enhancement and feedback (HDR, stabilization, real-time hints)
- Performance and usability trade-offs for resource-constrained devices

## Repository layout
- `docs/` research documentation and protocols
- `paper/` manuscript draft and publication assets
- `software/opencamera/` Android application artifact
- `src/` analysis and experiment code (new research code lives here)
- `data/`, `experiments/`, `results/` research artifacts and outputs
- `docs/legacy/` archived coursework materials

## Getting started (software artifact)
1. Install Android Studio and JDK 8+.
2. `cd software/opencamera`
3. `./gradlew assembleDebug` (Windows: `gradlew.bat assembleDebug`)

## Reproducibility
See `docs/reproducibility.md` for experiment logging, environment capture, and
data-handling expectations.

## Citation
If you reference this work, use `CITATION.cff`.

## Attribution
This repository includes code from the Open Camera project:
https://sourceforge.net/p/opencamera/code/ci/master/tree/

See `software/opencamera/gpl-3.0.txt` and `NOTICE.md` for details.
