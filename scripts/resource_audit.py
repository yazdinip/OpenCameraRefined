#!/usr/bin/env python3
"""Audit Android resource assets for the research baseline."""
from __future__ import annotations

import argparse
import csv
import json
import sys
from pathlib import Path
from typing import Optional

REPO_ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(REPO_ROOT / "src"))

from analysis.resource_audit import audit_resources, base_type_for_directory, summarize_top_base_types


def _load_config(path: Optional[str]) -> dict:
    if not path:
        return {}
    config_path = Path(path)
    if not config_path.is_file():
        raise FileNotFoundError(f"Config not found: {config_path}")
    return json.loads(config_path.read_text(encoding="utf-8"))


def _resolve_path(path_str: str) -> Path:
    path = Path(path_str)
    if path.is_absolute():
        return path
    return (REPO_ROOT / path).resolve()


def _write_json(path: Path, data: dict) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=2, sort_keys=True), encoding="utf-8")


def _write_csv(path: Path, by_directory: dict) -> None:
    path.parent.mkdir(parents=True, exist_ok=True)
    with path.open("w", newline="", encoding="utf-8") as handle:
        writer = csv.writer(handle)
        writer.writerow(["directory", "file_count", "base_type"])
        for directory, count in sorted(by_directory.items(), key=lambda item: item[0]):
            writer.writerow([directory, count, base_type_for_directory(directory)])


def main() -> int:
    parser = argparse.ArgumentParser(description="Audit Android resource assets for OpenCameraRefined.")
    parser.add_argument("--config", help="Path to a JSON config with res_dir and outputs.")
    parser.add_argument("--res-dir", help="Resource directory to scan.")
    parser.add_argument("--output-json", help="Output JSON path.")
    parser.add_argument("--output-csv", help="Output CSV path.")
    args = parser.parse_args()

    config = _load_config(args.config)
    res_dir = args.res_dir or config.get("res_dir") or "software/opencamera/app/src/main/res"
    output_json = args.output_json or config.get("output_json") or "results/resource_audit.json"
    output_csv = args.output_csv or config.get("output_csv")

    res_dir_path = _resolve_path(res_dir)
    stats = audit_resources(str(res_dir_path), label=res_dir)
    _write_json(_resolve_path(output_json), stats)
    if output_csv:
        _write_csv(_resolve_path(output_csv), stats.get("by_directory", {}))

    top_base_types = summarize_top_base_types(stats, top_n=5)
    print(f"Resource files: {stats.get('total_files', 0)}")
    print(f"Directories scanned: {stats.get('directories_scanned', 0)}")
    if top_base_types:
        formatted = ", ".join([f"{name} ({count})" for name, count in top_base_types])
        print(f"Top resource groups: {formatted}")

    return 0


if __name__ == "__main__":
    raise SystemExit(main())
