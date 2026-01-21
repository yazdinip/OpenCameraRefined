"""Resource inventory utilities for OpenCameraRefined Research."""
from __future__ import annotations

from collections import Counter
from pathlib import Path
from typing import Dict, Optional, Tuple


def _sorted_dict(counter: Counter) -> Dict[str, int]:
    return dict(sorted(counter.items(), key=lambda item: item[0]))


def audit_resources(res_dir: str, label: Optional[str] = None) -> Dict[str, object]:
    res_path = Path(res_dir).resolve()
    if not res_path.is_dir():
        raise FileNotFoundError(f"Resource directory not found: {res_path}")

    by_directory = Counter()
    by_base_type = Counter()
    by_extension = Counter()
    drawable_by_density = Counter()
    values_locales = Counter()
    total_files = 0
    directories_seen = set()

    for path in res_path.rglob("*"):
        if path.is_dir():
            continue
        if path.name.startswith("."):
            continue
        total_files += 1
        try:
            rel_parts = path.relative_to(res_path).parts
        except ValueError:
            continue
        if not rel_parts:
            continue
        top_dir = rel_parts[0]
        directories_seen.add(top_dir)
        by_directory[top_dir] += 1
        base = top_dir.split("-", 1)[0]
        by_base_type[base] += 1

        if base == "drawable":
            density = top_dir.split("-", 1)[1] if "-" in top_dir else "default"
            drawable_by_density[density] += 1
        if base == "values":
            locale = top_dir.split("-", 1)[1] if "-" in top_dir else "default"
            values_locales[locale] += 1

        ext = path.suffix.lower()
        by_extension[ext if ext else "(none)"] += 1

    return {
        "res_dir": label if label is not None else str(res_path),
        "total_files": total_files,
        "directories_scanned": len(directories_seen),
        "by_directory": _sorted_dict(by_directory),
        "by_base_type": _sorted_dict(by_base_type),
        "by_extension": _sorted_dict(by_extension),
        "drawable_by_density": _sorted_dict(drawable_by_density),
        "values_locales": _sorted_dict(values_locales),
    }


def base_type_for_directory(directory: str) -> str:
    return directory.split("-", 1)[0]


def summarize_top_base_types(stats: Dict[str, object], top_n: int = 5) -> Tuple[Tuple[str, int], ...]:
    base_types = stats.get("by_base_type", {})
    if not isinstance(base_types, dict):
        return ()
    items = sorted(base_types.items(), key=lambda item: (-item[1], item[0]))
    return tuple(items[:top_n])
