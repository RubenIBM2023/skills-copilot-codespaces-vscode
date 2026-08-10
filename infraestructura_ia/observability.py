"""Structured logging for OPF swarm runs.

Provides a RunLogger class that writes events, worker logs, and manifests
to a run directory. All timestamps are UTC ISO 8601 with millisecond precision.
"""

import json
import os
import sys
from datetime import datetime
from pathlib import Path
from typing import Any


def _utc_iso_ms() -> str:
    """Single-source UTC timestamp with millisecond precision and Z suffix."""
    now = datetime.utcnow()
    return now.strftime("%Y-%m-%dT%H:%M:%S.") + f"{now.microsecond // 1000:03d}Z"


def _atomic_write(path: Path, content: str) -> None:
    """Write text to path atomically via tmp + os.replace."""
    tmp = path.with_suffix(path.suffix + ".tmp")
    tmp.write_text(content, encoding="utf-8")
    os.replace(str(tmp), str(path))


class RunLogger:
    """Structured logger for a single OPF swarm run.

    Writes simultaneously to:
      - stdout (human-readable)
      - <run_dir>/events.jsonl (one JSON object per line)
      - <run_dir>/workers/<N>.log (per-worker plain text)
      - <run_dir>/manifest.json (updated atomically on lifecycle transitions)
      - <run_dir>/summary.md (written by .close())
    """

    def __init__(self, run_dir: Path, req_id: str, project_id: str, branch: str) -> None:
        """Initialize the logger and create initial manifest.json.

        run_dir must already exist (created by registry.new_run_dir()).
        run_dir/workers/ must already exist.

        Calling __init__ writes manifest.json with status='running' and
        started=current UTC timestamp. Logs the run.start event.

        Args:
            run_dir: Path to the run directory.
            req_id: Request ID for this run.
            project_id: Project ID for this run.
            branch: Git branch name for this run.
        """
        self.run_dir = Path(run_dir)
        self.req_id = req_id
        self.project_id = project_id
        self.branch = branch

        self._closed = False
        self._started = datetime.utcnow()
        self._events_count = 0
        self._workers_seen = set()

        # Ensure directories exist
        self.run_dir.mkdir(parents=True, exist_ok=True)
        workers_dir = self.run_dir / "workers"
        workers_dir.mkdir(parents=True, exist_ok=True)

        # Write initial manifest
        started_ts = _utc_iso_ms()
        manifest = {
            "req_id": req_id,
            "project_id": project_id,
            "branch": branch,
            "started": started_ts,
            "ended": None,
            "status": "running",
            "duration_s": None,
        }
        _atomic_write(self.run_dir / "manifest.json", json.dumps(manifest, ensure_ascii=False))

        # Log run.start event
        self.event(type="run.start", req_id=req_id, project_id=project_id, branch=branch)

    def event(self, type: str, **fields: Any) -> None:
        """Append a single event to events.jsonl as a JSON object.

        Automatically prepends 'ts' (UTC ISO 8601 with milliseconds) and 'type'.
        Also prints a one-line human summary to stdout.

        Args:
            type: Event type string.
            **fields: Additional fields to include in the event.
        """
        if self._closed:
            return

        ts = _utc_iso_ms()
        event_obj = {"ts": ts, "type": type, **fields}

        # Append to events.jsonl
        events_file = self.run_dir / "events.jsonl"
        line = json.dumps(event_obj, ensure_ascii=False, separators=(",", ":")) + "\n"
        with open(events_file, "a", encoding="utf-8") as f:
            f.write(line)

        self._events_count += 1

        # Print human-readable summary to stdout
        local_time = datetime.utcnow().strftime("%H:%M:%S")
        field_strs = []
        for k, v in fields.items():
            v_str = str(v)
            if len(v_str) > 80:
                v_str = v_str[:77] + "..."
            field_strs.append(f"{k}={v_str}")
        field_part = " ".join(field_strs) if field_strs else ""
        if field_part:
            print(f"[{local_time}] {type} {field_part}", file=sys.stdout)
        else:
            print(f"[{local_time}] {type}", file=sys.stdout)

    def worker(self, worker_idx: int, message: str) -> None:
        """Append a message to <run_dir>/workers/<worker_idx>.log.

        One line per call, prefixed with UTC timestamp.
        Also emits an event(type='worker.log', worker=worker_idx, message=...).

        Args:
            worker_idx: Worker index (must be non-negative).
            message: Log message.

        Raises:
            ValueError: If worker_idx is negative.
        """
        if worker_idx < 0:
            raise ValueError(f"worker_idx must be non-negative, got {worker_idx}")

        if self._closed:
            return

        self._workers_seen.add(worker_idx)

        ts = _utc_iso_ms()
        log_line = f"[{ts}] {message}\n"

        # Append to workers/<worker_idx>.log
        worker_log = self.run_dir / "workers" / f"{worker_idx}.log"
        with open(worker_log, "a", encoding="utf-8") as f:
            f.write(log_line)

        # Emit event
        self.event(type="worker.log", worker=worker_idx, message=message)

        # Print to stdout
        local_time = datetime.utcnow().strftime("%H:%M:%S")
        print(f"[{local_time}] [worker-{worker_idx}] {message}", file=sys.stdout)

    def close(self, status: str, **stats: Any) -> None:
        """Finalize the run.

        Transitions the run to a terminal state:
          - Update manifest.json with status, ended timestamp, duration_s, **stats.
          - Write <run_dir>/summary.md with a human-readable summary.
          - Log the run.end event.
          - status must be one of: "success", "failed", "aborted".

        After close() is called, further calls to event/worker are no-ops.

        Args:
            status: Terminal status: "success", "failed", or "aborted".
            **stats: Additional statistics to include in manifest and summary.
        """
        if self._closed:
            return

        self._closed = True

        ended_ts = _utc_iso_ms()
        ended_dt = datetime.utcnow()
        duration_s = (ended_dt - self._started).total_seconds()

        # Update manifest.json
        manifest = {
            "req_id": self.req_id,
            "project_id": self.project_id,
            "branch": self.branch,
            "started": self.run_dir / "manifest.json",  # Read it back
            "ended": ended_ts,
            "status": status,
            "duration_s": round(duration_s, 1),
            **stats,
        }

        # Re-read manifest to preserve started timestamp
        manifest_path = self.run_dir / "manifest.json"
        if manifest_path.exists():
            existing = json.loads(manifest_path.read_text(encoding="utf-8"))
            manifest["started"] = existing.get("started", ended_ts)

        _atomic_write(manifest_path, json.dumps(manifest, ensure_ascii=False))

        # Log run.end event
        self.event(type="run.end", status=status, duration_s=round(duration_s, 1), **stats)

        # Write summary.md
        status_emoji = {"success": "✅", "failed": "❌", "aborted": "⚠️"}.get(status, "❓")
        status_display = f"{status_emoji} {status}"

        workers_list = ", ".join(str(w) for w in sorted(self._workers_seen)) if self._workers_seen else "none"
        stats_lines = "\n".join(f"- {k}: {v}" for k, v in stats.items()) if stats else "- (none)"

        started_dt = self._started
        summary = f"""# Run {self.req_id}

**Started:** {started_dt.strftime('%Y-%m-%dT%H:%M:%S.') + f'{started_dt.microsecond // 1000:03d}Z'}
**Ended:**   {ended_ts}
**Status:**  {status_display}
**Duration:** {round(duration_s, 1)}s
**Branch:**  {self.branch}
**Project:** {self.project_id}

## Statistics

- Events recorded: {self._events_count}
- Workers active: {workers_list}
- Extra stats:
{stats_lines}

## Files

- Full event stream: `events.jsonl`
- Per-worker logs: `workers/`
- Run manifest: `manifest.json`
"""

        _atomic_write(self.run_dir / "summary.md", summary)


__all__ = ["RunLogger"]