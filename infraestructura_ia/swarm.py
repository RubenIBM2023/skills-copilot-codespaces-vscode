"""
swarm.py
--------
OPF Swarm Orchestrator with multi-project support and structured observability.

Usage:
  1. Start the OpenCode server:  opencode serve
  2. Set env var if needed:       export OPENCODE_API_URL=http://127.0.0.1:4096
  3. Run a swarm:                 python infraestructura_ia/swarm.py --req-id REQ-2026-05-31-001-teradata-to-aws

Requires: pip install -r infraestructura_ia/requirements.txt
"""

import argparse
import asyncio
import json
import re
import subprocess
import sys
from pathlib import Path
from typing import Any

from opencode_ai import AsyncOpencode, DefaultAioHttpClient

from registry import (
    REPO_ROOT,
    ProjectMeta,
    RequirementMeta,
    new_run_dir,
    resolve_project,
    resolve_requirement,
)
from observability import RunLogger

# ── Configuration ────────────────────────────────────────────────────────────
import os

OPENCODE_API_URL = os.getenv("OPENCODE_API_URL", "http://127.0.0.1:4096")
AGENT_ORCHESTRATOR = "architect"
AGENT_WORKER = "worker"


# ── Git Utilities ─────────────────────────────────────────────────────────────

def assert_git_repo() -> None:
    """Abort if not inside a Git repository."""
    result = subprocess.run(
        ["git", "rev-parse", "--is-inside-work-tree"],
        capture_output=True,
        text=True,
        cwd=str(REPO_ROOT),
    )
    if result.returncode != 0:
        print(f"[swarm] Fatal: not a Git repo. {result.stderr.strip()}")
        sys.exit(1)


def _default_branch() -> str:
    """Return the repo's default branch name.

    Detection order (never uses the currently checked-out branch as a signal):
      1. refs/remotes/origin/HEAD  — set by 'git remote set-head' or clone.
      2. Local branch existence probe: main → master → develop.
      3. Hard-coded fallback: 'main'.
    """
    r = subprocess.run(
        ["git", "symbolic-ref", "refs/remotes/origin/HEAD", "--short"],
        capture_output=True,
        text=True,
        cwd=str(REPO_ROOT),
    )
    if r.returncode == 0:
        ref = r.stdout.strip()
        branch = ref[len("origin/"):] if ref.startswith("origin/") else ref
        print(f"[swarm] Default branch detected via remote HEAD: {branch}")
        return branch

    for name in ("main", "master", "develop"):
        r = subprocess.run(
            ["git", "show-ref", "--verify", "--quiet", f"refs/heads/{name}"],
            capture_output=True,
            cwd=str(REPO_ROOT),
        )
        if r.returncode == 0:
            print(f"[swarm] Default branch detected via local ref: {name}")
            return name

    print("[swarm] Warning: could not detect default branch; assuming 'main'.")
    return "main"


def create_feature_branch(req_id: str, branch_prefix: str = "opencode/swarm") -> str:
    """Check out a clean feature branch from the repo's default branch.

    Branch name: <branch_prefix>-<req_id>

    Idempotent: if the target branch already exists and HEAD is already on it,
    this is a no-op (safe re-run). If the branch exists but HEAD is elsewhere,
    the function switches to it.

    Aborts with a clear message if the working tree is dirty (tracked files).
    """
    default = _default_branch()
    branch = f"{branch_prefix}-{req_id}"

    # ── Guard: reject a dirty working tree (tracked files only) ─────────────
    dirty = subprocess.run(
        ["git", "status", "--porcelain", "--untracked-files=no"],
        capture_output=True,
        text=True,
        cwd=str(REPO_ROOT),
    )
    if dirty.returncode != 0:
        print(f"[swarm] Fatal: could not read git status. {dirty.stderr.strip()}")
        sys.exit(1)
    if dirty.stdout.strip():
        print(
            "[swarm] Fatal: working tree has uncommitted changes to tracked files.\n"
            "        Commit or stash them before running the swarm:\n"
            f"          git -C \"{REPO_ROOT}\" status"
        )
        sys.exit(1)

    # ── Idempotency: already on the target branch ─────────────────────────────
    current = subprocess.run(
        ["git", "branch", "--show-current"],
        capture_output=True,
        text=True,
        cwd=str(REPO_ROOT),
    )
    if current.returncode == 0 and current.stdout.strip() == branch:
        print(f"[swarm] Already on branch: {branch} — reusing it.")
        return branch

    print(f"[swarm] Creating branch: {branch} (base: {default})")

    checkout = subprocess.run(
        ["git", "checkout", default],
        capture_output=True,
        text=True,
        cwd=str(REPO_ROOT),
    )
    if checkout.returncode != 0:
        print(f"[swarm] Fatal: could not checkout '{default}'. {checkout.stderr.strip()}")
        sys.exit(1)

    new_branch = subprocess.run(
        ["git", "checkout", "-b", branch],
        capture_output=True,
        text=True,
        cwd=str(REPO_ROOT),
    )
    if new_branch.returncode != 0:
        switch = subprocess.run(
            ["git", "checkout", branch],
            capture_output=True,
            text=True,
            cwd=str(REPO_ROOT),
        )
        if switch.returncode != 0:
            print(
                f"[swarm] Fatal: could not create or checkout branch '{branch}'.\n"
                f"        {new_branch.stderr.strip()}"
            )
            sys.exit(1)
        print(f"[swarm] Branch '{branch}' already exists; switched to it.")

    return branch


# ── SSE Observability ─────────────────────────────────────────────────────────

async def observe_events(client: AsyncOpencode, logger: RunLogger) -> None:
    """
    Forward all OpenCode SSE events to the structured RunLogger.
    Captures the full event stream (no type filtering) so post-mortem auditing
    can reconstruct every action taken by the swarm.
    """
    try:
        stream = await client.event.list()
        async for event in stream:
            etype = getattr(event, "type", "unknown")
            props = getattr(event, "properties", None) or {}

            # Only forward primitive-valued fields to keep events.jsonl parseable.
            safe_fields = {
                k: v for k, v in props.items()
                if isinstance(v, (str, int, float, bool))
            }
            logger.event(f"sse.{etype}", **safe_fields)
    except asyncio.CancelledError:
        pass


# ── Session Helpers ───────────────────────────────────────────────────────────

async def create_session(client: AsyncOpencode, title: str) -> Any:
    """Create a session. Note: parent_session_id is not in the public SDK API."""
    return await client.session.create()


async def prompt_session(
    client: AsyncOpencode,
    session_id: str,
    agent: str,
    model_id: str,
    provider_id: str,
    text: str,
) -> Any:
    """
    Send a chat message to a session.

    The model is injected via extra_body to work around provider routing
    in the Python SDK. See: github.com/anomalyco/opencode-sdk-python/issues/42
    """
    return await client.session.chat(
        session_id,
        parts=[{"type": "text", "text": text}],
        agent=agent,
        extra_body={
            "model": {
                "providerID": provider_id,
                "modelID": model_id,
            }
        },
    )


# ── Worker Dispatch ───────────────────────────────────────────────────────────

async def dispatch_worker(
    client: AsyncOpencode,
    logger: RunLogger,
    index: int,
    project: ProjectMeta,
    jurisdiction: str,
    instruction: str,
) -> None:
    """Spawn a single worker session and send it its task prompt."""
    session = await create_session(client, f"Worker #{index + 1} [{jurisdiction}]")
    logger.event(
        "worker.session.created",
        worker_idx=index + 1,
        session_id=session.id,
    )

    project_root_rel = project.root.relative_to(REPO_ROOT).as_posix()

    prompt = f"""JURISDICTION: `{jurisdiction}`
PROJECT_ROOT: `{project_root_rel}`

INSTRUCTION:
{instruction}

CONSTRAINT: You may only use `edit` or `write` on files under `{jurisdiction}`.
The project's npm commands MUST run with cwd=`{project_root_rel}`.
For all other paths, use read-only operations.

VERIFICATION: After applying the edit, run from the project root:
  - {project.lint_cmd}
  - {project.test_cmd}
  - {project.typecheck_cmd}
Report results back to the architect.
"""

    logger.event(
        "worker.dispatch",
        worker_idx=index + 1,
        jurisdiction=jurisdiction,
        project_id=project.id,
    )
    logger.worker(index + 1, f"dispatched to {jurisdiction}")

    await prompt_session(
        client,
        session_id=session.id,
        agent=AGENT_WORKER,
        model_id="claude-haiku-4-5",
        provider_id="ica",
        text=prompt,
    )


# ── Main Orchestration Loop ───────────────────────────────────────────────────

async def main() -> None:
    parser = argparse.ArgumentParser(description="OPF Swarm orchestrator")
    parser.add_argument(
        "--req-id",
        required=True,
        help="Requirement ID (e.g., REQ-2026-05-31-001-teradata-to-aws)",
    )
    args = parser.parse_args()

    assert_git_repo()

    # Resolve the requirement and target project from the registry.
    req: RequirementMeta = resolve_requirement(args.req_id)
    project: ProjectMeta = resolve_project(req.project_id)
    spec_text = req.spec_path.read_text(encoding="utf-8")

    print(f"[swarm] Requirement: {req.req_id}")
    print(f"[swarm] Project:     {project.id} ({project.name})")
    print(f"[swarm] SPEC:        {req.spec_path.relative_to(REPO_ROOT).as_posix()}")

    branch = create_feature_branch(req.req_id, branch_prefix=req.branch_prefix)

    # Initialise the structured run logger BEFORE any OpenCode interaction.
    run_dir = new_run_dir(req.req_id)
    logger = RunLogger(
        run_dir=run_dir,
        req_id=req.req_id,
        project_id=project.id,
        branch=branch,
    )
    print(f"[swarm] Run directory: {run_dir.relative_to(REPO_ROOT).as_posix()}")

    # Share the run directory with the OpenCode plugin (hitl-guard.ts) so it
    # writes its observability events into the SAME directory rather than
    # spawning a parallel one. The plugin reads OPF_RUN_DIR via .opencode/tools/run-dir.js.
    os.environ["OPF_REQ_ID"] = req.req_id
    os.environ["OPF_RUN_DIR"] = str(run_dir.resolve())
    logger.event(
        "swarm.env.exported",
        OPF_REQ_ID=req.req_id,
        OPF_RUN_DIR=str(run_dir.resolve()),
    )

    try:
        async with AsyncOpencode(
            base_url=OPENCODE_API_URL,
            http_client=DefaultAioHttpClient(),
            timeout=240.0,
        ) as client:

            # Start passive SSE observer
            telemetry = asyncio.create_task(observe_events(client, logger))

            # ── Step 1: Orchestrator session ──────────────────────────────────
            orchestrator_session = await create_session(
                client, "Architect — task decomposition"
            )
            logger.event(
                "orchestrator.session.created",
                session_id=orchestrator_session.id,
            )

            decompose_prompt = f"""You are analyzing requirement {req.req_id} for project '{project.id}'.

PROJECT_ROOT: workspace/{project.id}/
SPEC_PATH: specs/{req.req_id}/SPEC.md (read-only)

All file paths in your plan MUST be relative to the repository root.
For example: "workspace/{project.id}/src/core/auth.ts" — never just "src/core/auth.ts".

Identify all files that must be changed and produce an execution plan as a JSON array.
Return ONLY valid JSON — no markdown fences, no extra text.

Schema:
[
  {{
    "jurisdiction": "workspace/{project.id}/src/core/auth.ts",
    "instruction": "..."
  }}
]

SPEC CONTENT:
{spec_text}
"""

            logger.event("orchestrator.prompt.sent", chars=len(decompose_prompt))
            result = await prompt_session(
                client,
                session_id=orchestrator_session.id,
                agent=AGENT_ORCHESTRATOR,
                model_id="claude-sonnet-4-6",
                provider_id="ica",
                text=decompose_prompt,
            )

            # ── Step 2: Parse task plan ───────────────────────────────────────
            text_parts = [
                p for p in (result.content or [])
                if getattr(p, "type", None) == "text"
            ]
            raw = text_parts[0].text.strip() if text_parts else ""

            fence_match = re.search(r"```(?:json)?\s*(\[.*?\])\s*```", raw, re.DOTALL)
            if fence_match:
                raw = fence_match.group(1).strip()
            else:
                raw = raw.strip()

            try:
                task_plan: list[dict] = json.loads(raw)
            except json.JSONDecodeError as exc:
                logger.event(
                    "orchestrator.plan.error",
                    error=str(exc),
                    raw_output_excerpt=raw[:500],
                )
                print(f"[swarm] Failed to parse orchestrator JSON: {exc}")
                print(f"[swarm] Raw output:\n{raw}")
                telemetry.cancel()
                logger.close("failed", reason="orchestrator_json_parse_error")
                return

            logger.event(
                "orchestrator.plan.received",
                worker_count=len(task_plan),
            )
            print(f"[swarm] Task plan: {len(task_plan)} worker(s) to dispatch")

            # ── Step 3: Parallel worker dispatch ─────────────────────────────
            worker_tasks = [
                dispatch_worker(
                    client,
                    logger,
                    i,
                    project,
                    item["jurisdiction"],
                    item["instruction"],
                )
                for i, item in enumerate(task_plan)
            ]

            print(f"[swarm] Releasing {len(worker_tasks)} parallel worker(s)...")
            await asyncio.gather(*worker_tasks)

            # ── Step 4: Terminate orchestrator (triggers session.idle → plugin)
            print("[swarm] All workers finished. Closing orchestrator session...")
            await client.session.abort(orchestrator_session.id)

            telemetry.cancel()
            logger.event("swarm.completed", worker_count=len(task_plan))
            logger.close(
                "success",
                workers=len(task_plan),
                branch=branch,
                run_dir=run_dir.relative_to(REPO_ROOT).as_posix(),
            )
            print(
                f"[swarm] Done. Run summary: "
                f"{(run_dir / 'summary.md').relative_to(REPO_ROOT).as_posix()}"
            )

    except Exception as exc:
        # Log failure metadata so the run is auditable even on crash.
        logger.event("swarm.error", error=str(exc), error_type=type(exc).__name__)
        logger.close("failed", error=str(exc))
        raise


if __name__ == "__main__":
    asyncio.run(main())
