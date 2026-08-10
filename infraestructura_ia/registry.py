"""
registry.py
-----------
REQ ↔ project resolution module for the OPF swarm orchestrator.

This module provides a pure resolver for:
  - Generating new REQ_IDs (requirement identifiers) with auto-incrementing counters
  - Listing all requirements under specs/
  - Resolving requirement metadata (SPEC.md, target.json, project link)
  - Resolving project metadata (language, commands, workspace location)
  - Creating append-only run directories

All paths are absolute Path objects. All resolutions are from REPO_ROOT.
No external dependencies. No logging or print() calls.
"""

import json
import re
from dataclasses import dataclass
from datetime import datetime
from pathlib import Path
from typing import Union

# ── Path Resolution ──────────────────────────────────────────────────────────
# Compute REPO_ROOT from this script's location: parent of infraestructura_ia/

_SCRIPT_DIR = Path(__file__).resolve().parent
REPO_ROOT = _SCRIPT_DIR.parent

SPECS_DIR = REPO_ROOT / "specs"
RUNS_DIR = REPO_ROOT / "runs"
WORKSPACE_DIR = REPO_ROOT / "workspace"

# ── Validation Patterns ──────────────────────────────────────────────────────

_SLUG_RE = re.compile(r"^[a-z0-9][a-z0-9-]*$")
_REQ_ID_RE = re.compile(r"^REQ-\d{4}-\d{2}-\d{2}-\d{3}-[a-z0-9][a-z0-9-]*$")


# ── Data Classes ─────────────────────────────────────────────────────────────

@dataclass(frozen=True)
class ProjectMeta:
    """Metadata for a project loaded from workspace/<id>/.opf/project.json"""
    id: str
    name: str
    language: str
    lint_cmd: str
    test_cmd: str
    typecheck_cmd: str
    root: Path


@dataclass(frozen=True)
class RequirementMeta:
    """Metadata for a requirement loaded from specs/<req_id>/"""
    req_id: str
    spec_path: Path
    target_path: Path
    project_id: str
    branch_prefix: str
    title: str


# ── Public API ───────────────────────────────────────────────────────────────

def new_req_id(slug: str, today: Union[datetime, None] = None) -> str:
    """
    Generate a new REQ_ID for today, auto-incrementing the NNN counter
    based on existing folders under specs/ with today's date.

    Format: REQ-YYYY-MM-DD-NNN-<slug>
    - Slug must match ^[a-z0-9][a-z0-9-]*$ (kebab-case). Raises ValueError otherwise.
    - NNN is zero-padded to 3 digits, starting at 001.
    - Counter is computed by scanning specs/REQ-<today>-*-* folders.

    Args:
        slug: The requirement slug (e.g., "teradata-to-aws", "user-auth").
        today: The date to use; defaults to datetime.now() if not provided.

    Returns:
        A new REQ_ID string.

    Raises:
        ValueError: If slug does not match the required pattern.
    """
    if not _SLUG_RE.fullmatch(slug):
        raise ValueError(
            f"Slug must match ^[a-z0-9][a-z0-9-]*$; got {slug!r}"
        )

    if today is None:
        today = datetime.now()

    date_prefix = today.strftime("REQ-%Y-%m-%d-")

    # Scan existing requirement directories for today's date
    existing = [
        p.name for p in SPECS_DIR.glob(f"{date_prefix}*-*") if p.is_dir()
    ]

    # Extract NNN counters from existing REQ_IDs
    counters = []
    for name in existing:
        parts = name.split("-")
        if len(parts) >= 6:
            try:
                counters.append(int(parts[4]))
            except ValueError:
                pass

    nnn = (max(counters) + 1) if counters else 1
    return f"{date_prefix}{nnn:03d}-{slug}"


def list_requirements() -> list[str]:
    """
    Return all REQ_IDs found under specs/, sorted lexicographically
    (which is chronological by ID format since dates are YYYY-MM-DD).

    Returns:
        A sorted list of REQ_ID strings (e.g., ["REQ-2026-05-31-001-teradata-to-aws", ...]).
    """
    if not SPECS_DIR.exists():
        return []

    req_ids = []
    for item in SPECS_DIR.iterdir():
        if item.is_dir() and _REQ_ID_RE.fullmatch(item.name):
            req_ids.append(item.name)

    return sorted(req_ids)


def resolve_requirement(req_id: str) -> RequirementMeta:
    """
    Load specs/<req_id>/SPEC.md and specs/<req_id>/target.json
    and return a RequirementMeta object.

    Args:
        req_id: The REQ_ID to resolve (e.g., "REQ-2026-05-31-001-teradata-to-aws").

    Returns:
        RequirementMeta with all fields populated.

    Raises:
        ValueError: If req_id format is invalid or target.json lacks 'project'.
        FileNotFoundError: If SPEC.md or target.json is missing.
    """
    if not _REQ_ID_RE.fullmatch(req_id):
        raise ValueError(f"Invalid REQ_ID format: {req_id!r}")

    req_dir = SPECS_DIR / req_id
    spec_path = req_dir / "SPEC.md"
    target_path = req_dir / "target.json"

    if not spec_path.exists():
        raise FileNotFoundError(f"SPEC.md not found at {spec_path}")

    if not target_path.exists():
        raise FileNotFoundError(f"target.json not found at {target_path}")

    # Parse target.json
    with open(target_path, "r", encoding="utf-8") as f:
        target_data = json.load(f)

    if "project" not in target_data:
        raise ValueError(
            f"target.json at {target_path} is missing required 'project' field"
        )

    project_id = target_data["project"]
    branch_prefix = target_data.get("branch_prefix", "opencode/swarm")
    title = target_data.get("title", "")

    return RequirementMeta(
        req_id=req_id,
        spec_path=spec_path.resolve(),
        target_path=target_path.resolve(),
        project_id=project_id,
        branch_prefix=branch_prefix,
        title=title,
    )


def resolve_project(project_id: str) -> ProjectMeta:
    """
    Load workspace/<project_id>/.opf/project.json
    and return a ProjectMeta object.

    Args:
        project_id: The project identifier (e.g., "main").

    Returns:
        ProjectMeta with all fields populated.

    Raises:
        FileNotFoundError: If project.json is missing.
        ValueError: If project.json lacks required fields.
    """
    project_file = WORKSPACE_DIR / project_id / ".opf" / "project.json"

    if not project_file.exists():
        raise FileNotFoundError(f"project.json not found at {project_file}")

    with open(project_file, "r", encoding="utf-8") as f:
        proj_data = json.load(f)

    required_fields = {
        "id", "name", "language", "lint_cmd", "test_cmd", "typecheck_cmd"
    }
    missing = required_fields - set(proj_data.keys())

    if missing:
        raise ValueError(
            f"project.json at {project_file} is missing required fields: {missing}"
        )

    project_root = (WORKSPACE_DIR / project_id).resolve()

    return ProjectMeta(
        id=proj_data["id"],
        name=proj_data["name"],
        language=proj_data["language"],
        lint_cmd=proj_data["lint_cmd"],
        test_cmd=proj_data["test_cmd"],
        typecheck_cmd=proj_data["typecheck_cmd"],
        root=project_root,
    )


def new_run_dir(req_id: str, when: Union[datetime, None] = None) -> Path:
    """
    Create runs/<req_id>/<UTC_TIMESTAMP>/ and return the path.

    Timestamp format: YYYY-MM-DDTHH-MM-SSZ (filesystem-safe ISO 8601).
    Also creates the workers/ subdirectory inside the run dir.

    If a collision occurs (same second), retries once with +1 second.

    Args:
        req_id: The REQ_ID for which to create a run directory.
        when: The timestamp to use; defaults to datetime.utcnow() if not provided.

    Returns:
        The absolute path to the newly created run directory (with workers/ inside).

    Raises:
        FileExistsError: If a collision persists after retry.
        ValueError: If req_id format is invalid.
    """
    if not _REQ_ID_RE.fullmatch(req_id):
        raise ValueError(f"Invalid REQ_ID format: {req_id!r}")

    if when is None:
        when = datetime.utcnow()

    # Try to create the run directory with timestamp
    for attempt in range(2):
        ts = when.strftime("%Y-%m-%dT%H-%M-%SZ")
        run_dir = RUNS_DIR / req_id / ts
        workers_dir = run_dir / "workers"

        try:
            workers_dir.mkdir(parents=True, exist_ok=False)
            return run_dir
        except FileExistsError:
            # Retry with +1 second on collision
            if attempt < 1:
                when = datetime.utcfromtimestamp(when.timestamp() + 1)
                continue
            raise

    # Unreachable, but appease type checker
    raise FileExistsError(f"Failed to create run directory after retries")


__all__ = [
    "REPO_ROOT",
    "SPECS_DIR",
    "RUNS_DIR",
    "WORKSPACE_DIR",
    "ProjectMeta",
    "RequirementMeta",
    "new_req_id",
    "list_requirements",
    "resolve_requirement",
    "resolve_project",
    "new_run_dir",
]