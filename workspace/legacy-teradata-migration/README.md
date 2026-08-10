# Legacy Teradata DW \u2192 AWS Migration (sample project)

This is a **sample One-Person Factory project** that demonstrates how the OPF swarm migrates
legacy data-warehouse code from **Teradata** to **AWS (Amazon Redshift + AWS Glue PySpark)**.

It is intentionally tiny but realistic: it captures the four shapes of artefacts you typically
find in a legacy Teradata estate.

## Layout

```
legacy/                       # ── INPUT, read-only for swarm workers
\u251c\u2500\u2500 ddl/                      # Teradata DDL (CREATE MULTISET TABLE \u2026 PRIMARY INDEX \u2026)
\u2502   \u251c\u2500\u2500 customer_dim.sql
\u2502   \u2514\u2500\u2500 sales_fact.sql
\u251c\u2500\u2500 bteq/                     # Teradata BTEQ ETL scripts
\u2502   \u2514\u2500\u2500 load_sales.bteq
\u2514\u2500\u2500 python/                   # Legacy Python wrapper using `teradatasql`
    \u2514\u2500\u2500 run_etl.py

target/                       # ── OUTPUT, written by the swarm
\u251c\u2500\u2500 redshift/ddl/             # Translated Redshift DDL (DISTKEY/SORTKEY/ENCODE)
\u251c\u2500\u2500 glue/jobs/                # AWS Glue PySpark job(s) replacing the BTEQ ETL
\u2514\u2500\u2500 docs/                     # Migration notes (table-by-table mapping, caveats)

tests/                        # Light pytest suite that validates the generated artefacts
.opf/project.json             # OPF project metadata: lint_cmd, test_cmd, typecheck_cmd
pyproject.toml                # Minimal Python project config (ruff + pytest)
```

## Migration target

| Legacy artefact            | New AWS artefact                                    |
|----------------------------|-----------------------------------------------------|
| `legacy/ddl/*.sql`         | `target/redshift/ddl/*.sql`                         |
| `legacy/bteq/*.bteq`       | `target/glue/jobs/*.py` (AWS Glue PySpark)          |
| `legacy/python/run_etl.py` | replaced by Glue job orchestration (no equivalent)  |

## Rules for OPF agents

1. `legacy/**` is **input only**. Workers read it, never write to it.
2. All new artefacts go under `target/**`.
3. The swarm uses ICA MCP tools (Digital Workforce \"Data Engineer\" + the IBM Consulting KB
   collection) **as advisers** to validate migration choices. The swarm does **not** generate
   client code that calls ICA \u2014 ICA is consumed by the agents themselves at orchestration
   time, not embedded in the deliverables.
4. Verification commands are declared in `.opf/project.json`.

## Running the swarm against this project

The active requirement that targets this project is:

```
specs/REQ-2026-05-31-001-teradata-to-aws/SPEC.md
```

Dispatch with:

```bash
python infraestructura_ia/swarm.py --req-id REQ-2026-05-31-001-teradata-to-aws
```
