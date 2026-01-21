# Resource Audit (2026-01-20)

## Goal
Establish a baseline inventory of Android resource assets in the Open Camera
artifact used by this research program.

## Inputs
- Resource tree: software/opencamera/app/src/main/res
- Script: scripts/resource_audit.py
- Config: configs/resource_audit.json

## Outputs
- results/resource_audit.json
- data/derived/resource_audit.csv

## Summary
- Total files: 226
- Directories scanned: 39

### Resource groups
- drawable: 166
- values: 46
- mipmap: 4
- layout: 3
- xml: 3
- raw: 2
- filters: 1
- menu: 1

### File types
- .png: 169
- .xml: 55
- .ogg: 2

### Drawable densities
- default: 2
- hdpi: 26
- mdpi: 59
- xhdpi: 33
- xxhdpi: 30
- xxxhdpi: 16

### Values locales
az, be, cs, de, default, es, fr, hu, it, ja, ko, pt-rBR, pt-rPT, ru, sl, sw600dp, sw720dp-land, tr, uk, v11, v14, v21, zh-rCN, zh-rTW
