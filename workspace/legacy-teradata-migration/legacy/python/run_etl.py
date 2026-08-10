"""Legacy Python wrapper that triggers the BTEQ daily-sales load.

Runs on `etl-server-01` (RHEL 7) under cron at 02:00.
Uses `teradatasql` directly because the legacy team never adopted Airflow.

This file is part of the LEGACY ESTATE \u2014 it is the artefact being migrated, not
the artefact being produced. OPF workers must read it but must NOT modify it.
"""
from __future__ import annotations

import datetime as _dt
import os
import subprocess
import sys

LANDING_DIR = "/mnt/td/landing"
BTEQ_SCRIPT = "/opt/etl/bteq/load_sales.bteq"
TD_HOST = "tdprod"
TD_USER = "etl_user"


def _today_load_date() -> str:
    return _dt.date.today().strftime("%Y%m%d")


def _ensure_landing_file(load_date: str) -> str:
    path = os.path.join(LANDING_DIR, f"sales_{load_date}.dat")
    if not os.path.exists(path):
        raise FileNotFoundError(f"missing landing file: {path}")
    return path


def main() -> int:
    load_date = os.environ.get("LOAD_DATE") or _today_load_date()
    _ensure_landing_file(load_date)

    env = os.environ.copy()
    env["LOAD_DATE"] = load_date
    env["TD_PASSWORD"] = os.environ["TD_PASSWORD"]  # provided by cron wrapper

    # bteq invocation \u2014 yes, it shells out. yes, it has been like this since 2014.
    proc = subprocess.run(
        ["bteq", f"< {BTEQ_SCRIPT}"],
        env=env,
        shell=True,
        check=False,
    )
    return proc.returncode


if __name__ == "__main__":
    sys.exit(main())
