# OpenCameraRefined Research Manuscript

## Abstract
OpenCameraRefined Research restructures the Open Camera codebase into a
research testbed for accessible mobile capture workflows and on-device
feedback. We report a baseline audit of Android resources to anchor
instrumentation and evaluation planning. The audit covers 226
files across 39 directories, dominated by drawable
assets, and confirms localization variants across 24 values
configurations. The repository pairs the software artifact with analysis
scripts, experiment logs, and reproducibility guidance.

## 1. Introduction
Mobile camera applications expose advanced capture features but can be difficult
to operate for users with motor, vision, or cognitive constraints. This project
investigates interaction patterns and feedback cues that reduce time-to-capture
while keeping performance within mobile device budgets. The work is organized as
a research program with explicit documentation, data handling policies, and
reproducible analysis tools.

## 2. Related work
The study draws on accessibility standards and mobile imaging documentation,
including WCAG 2.1, Android accessibility guidance, and the Android Camera2 and
CameraX frameworks. A curated list of sources is maintained in
`docs/related-work.md`, with BibTeX entries in `paper/bibliography.bib`.

## 3. Methodology
We combine controlled usability evaluations with performance measurement. The
initial step is a static inventory of Android resource assets to understand UI
and localization coverage before instrumentation. Subsequent experiments will
extend this baseline with runtime logging of interaction events, timing metrics,
and device telemetry as described in `docs/methodology.md`.

## 4. System design
The software artifact is kept under `software/opencamera`, preserving the
original Open Camera build system. Analysis utilities live in `src/analysis` and
are invoked through scripts under `scripts/`. The resource audit script scans the
Android `res/` tree and emits JSON and CSV outputs used in reports.

## 5. Experiments
The baseline experiment `2026-01-20_resource-audit` inventories the resource
assets using `scripts/resource_audit.py` and the configuration in
`configs/resource_audit.json`. Outputs are stored in `results/resource_audit.json`
and `data/derived/resource_audit.csv`.

## 6. Results
The resource audit identifies 226 files across
39 resource directories. The resource groups are:
drawable 166, values 46, mipmap 4, layout 3, xml 3, raw 2, filters 1, menu 1.

File types are concentrated in .png 169, .xml 55, .ogg 2. Drawable densities include
default 2, hdpi 26, mdpi 59, xhdpi 33, xxhdpi 30, xxxhdpi 16.
Values resources include the following locale and configuration variants:
az, be, cs, de, default, es, fr, hu, it, ja, ko, pt-rBR, pt-rPT, ru, sl, sw600dp, sw720dp-land, tr, uk, v11, v14, v21, zh-rCN, zh-rTW.

## 7. Discussion
The dominance of drawable assets suggests a UI-heavy surface area where
accessibility improvements can focus on scalable iconography and contrast-safe
assets. The presence of 24 values variants indicates established
localization coverage that must be preserved while introducing new interaction
patterns. The audit is static and does not capture runtime behavior; upcoming
work will focus on instrumentation and participant studies.

## 8. Conclusion
This repository now functions as a research-grade scaffold around Open Camera,
with documented methodology, reproducibility practices, and a measured baseline
inventory of resources. The next phase will integrate logging hooks and
controlled studies to evaluate accessible capture workflows.

## References
- https://www.w3.org/TR/WCAG21/
- https://developer.android.com/guide/topics/ui/accessibility
- https://m3.material.io/foundations/accessible-design/overview
- https://sourceforge.net/p/opencamera/code/ci/master/tree/
- https://developer.android.com/reference/android/hardware/camera2/package-summary
- https://developer.android.com/training/camerax
- https://developer.android.com/topic/performance
- https://developer.android.com/studio/profile
