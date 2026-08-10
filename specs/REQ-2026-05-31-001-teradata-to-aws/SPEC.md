# SPEC-001: Migrate legacy Teradata DW slice to AWS (Redshift + Glue PySpark)

> **This file is the single source of truth for the swarm.**
> Agents read and obey this document. They may not invent requirements not declared here.
> To change requirements, edit this file first \u2014 never modify source code directly.
>
> **Location:** `specs/REQ-2026-05-31-001-teradata-to-aws/SPEC.md`
> **Sibling file:** `target.json` declares which project under `workspace/` this SPEC targets.

---

## Motivation

A Data Engineering team operates a legacy on-premise Teradata data warehouse and must migrate
a self-contained slice (one dimension, one fact, one daily load) to AWS. The chosen target
stack is **Amazon Redshift** for the warehouse and **AWS Glue (PySpark)** for the daily ETL,
which is the most common shape for real Teradata\u2192AWS migrations.

This requirement exists to demonstrate the **One-Person Factory** pattern end-to-end on a
realistic Data Engineering use case: the SPEC is the only artefact the operator authors;
the OPF swarm \u2014 architect plus workers \u2014 produces every deliverable; and the agents
consult **ICA MCP tools** (already configured in `opencode.json`) as expert advisers during
the migration process.

> **Critical scoping rule \u2014 read carefully.**
>
> The ICA MCP tools (`ica_ica_chat_digital_workforce`, `ica_ica_chat_models`,
> `ica_ica_chat_assistants`, `ica_ica_chat_agents`, the document-collection tools, etc.)
> are tools that **the OpenCode agents themselves invoke during the swarm run**.
> They are **NOT** SDKs to be wrapped in client code.
>
> Therefore, deliverables under `target/**` must contain **only** Redshift SQL, PySpark
> code and migration documentation. They must **not** import, reference, embed, hardcode
> or in any way call any ICA endpoint, MCP tool, model id, collection id, agent id,
> assistant id, digital-worker id or `https://api.nextgen-beta.ica.ibm.com/...` URL.
> The fingerprint `ica` (case-insensitive) must not appear in any file under `target/**`
> except inside `target/docs/digital_worker_review.md`, where it is expected because that
> file is the verbatim review captured from the Digital Workforce.

---

## Acceptance Criteria

Numbered list of testable conditions that define \"done\". Each item must be objectively
verifiable (file presence, lint, test, content assertions).

### 1. Redshift DDL \u2014 customer dimension

- File `target/redshift/ddl/customer_dim.sql` exists.
- Translates `legacy/ddl/customer_dim.sql` into a `CREATE TABLE` statement valid for
  Amazon Redshift, applying these mechanical rules:
  - Drop Teradata-only clauses: `MULTISET`, `FALLBACK`, `NO BEFORE/AFTER JOURNAL`,
    `CHECKSUM = DEFAULT`, `DEFAULT MERGEBLOCKRATIO`, `COMPRESS (...)`, `CHARACTER SET LATIN`,
    `NOT CASESPECIFIC`, `COLLECT STATISTICS`.
  - Replace `PRIMARY INDEX (col)` with Redshift `DISTKEY(col)` and add a sensible
    `SORTKEY` based on the columns most likely to be filtered (`country_code`,
    `customer_segment`, or `customer_id`).
  - Replace `BYTEINT` with `SMALLINT`.
  - Preserve column names, data types (mapped: `VARCHAR(N)` \u2192 `VARCHAR(N)`,
    `CHAR(N)` \u2192 `CHAR(N)`, `INTEGER`/`BIGINT`/`DECIMAL(p,s)`/`DATE`/`TIMESTAMP(0)` \u2192
    same on Redshift) and `NOT NULL` constraints.
  - Drop secondary `INDEX (...)` clauses (Redshift has no analogue \u2014 record this in the
    mapping doc, criterion #6).
  - Add `ENCODE AUTO` at the table level **or** explicit per-column `ENCODE` directives
    (architect decides; document the choice).
- File ends with no trailing whitespace and a final newline.

### 2. Redshift DDL \u2014 sales fact

- File `target/redshift/ddl/sales_fact.sql` exists.
- Translates `legacy/ddl/sales_fact.sql` applying the same mechanical rules as criterion #1, plus:
  - Replace the Teradata `PARTITION BY RANGE_N(SALE_DATE ...)` clause with the Redshift
    equivalent: `SORTKEY(SALE_DATE)` (Redshift partitions by sort key, not by range).
  - Choose `DISTKEY(CUSTOMER_ID)` to co-locate facts with the customer dimension.
  - Document the partitioning trade-off in `target/docs/migration_mapping.md`
    (criterion #6).

### 3. AWS Glue PySpark job \u2014 daily sales load

- File `target/glue/jobs/load_sales.py` exists.
- Replaces the BTEQ script `legacy/bteq/load_sales.bteq` with a Glue PySpark job that:
  1. Accepts `--load_date` (YYYY-MM-DD) and `--landing_bucket` job arguments via
     `getResolvedOptions`.
  2. Reads landing data from
     `s3://${landing_bucket}/sales/dt=${load_date}/*.dat` using the Glue context with
     pipe (`|`) delimiter.
  3. Applies the same schema declared in the legacy BTEQ `USING (...)` block.
  4. Implements the legacy reject rule: drop rows whose `customer_id` is not present in
     the Redshift `customer_dim` table (read via the Glue Redshift connector or
     `spark.read.format(\"jdbc\")`).
  5. Adds `load_ts = current_timestamp()` to every surviving row.
  6. Writes the result by appending into the Redshift `sales_fact` table using the Glue
     Redshift connector.
  7. Uses **no** hardcoded credentials, no hardcoded bucket names, no hardcoded Redshift
     endpoints. All connection details must come from job arguments or Glue connections.
- The file must be valid Python 3.10+ syntax and pass `ruff check`.

### 4. Pytest suite

- File `tests/test_redshift_ddl.py` exists with at least these checks:
  - The two DDL files in `target/redshift/ddl/` exist.
  - Each file contains the substring `CREATE TABLE` and the substring `DISTKEY`.
  - Each file does **not** contain any of: `MULTISET`, `FALLBACK`, `PRIMARY INDEX`,
    `COMPRESS (`, `CHARACTER SET LATIN`, `COLLECT STATISTICS`.
- File `tests/test_glue_job.py` exists with at least these checks:
  - `target/glue/jobs/load_sales.py` exists, parses with `ast.parse`, and contains
    `getResolvedOptions`, `current_timestamp`, and a reference to either
    `redshift` or `jdbc`.
  - The file does not contain any literal that looks like an AWS access key
    (regex `AKIA[0-9A-Z]{16}` produces zero matches).
- The pre-existing `tests/test_seed.py` must remain untouched.

### 5. Forbidden artefacts

- No file under `target/**` (recursive, **except** `target/docs/digital_worker_review.md`)
  may contain any of the following case-insensitive substrings:
  - `ica_ica_chat_`
  - `nextgen-beta.ica.ibm.com`
  - `mcp`
  - `4fffad08-c4b8-4c24-9167-4e300fb5df5e`
  - `6305e9e6-bc31-4467-abf3-5579d3319efd`
- A passing `tests/test_no_ica_client_code.py` enforces this.

### 6. Migration documentation

- File `target/docs/migration_mapping.md` exists and includes at minimum:
  - A markdown table with one row per legacy artefact (the three files under `legacy/`),
    columns: *Legacy artefact*, *AWS replacement*, *Mechanical changes*,
    *Caveats / lossy points*.
  - A short \"Open questions\" section listing anything the swarm could not resolve from
    the SPEC alone.
- File `target/docs/digital_worker_review.md` exists and contains a verbatim transcript
  (or a faithful summary) of at least one consultation made by the architect to the
  ICA Digital Worker \"Data Engineer \u2013 Data Platforms\" via `ica_ica_chat_digital_workforce`
  during the swarm run. Empty file or `TODO` is unacceptable.

### 7. Deterministic Verification

All of the following must pass at 100% from the project root
(`workspace/legacy-teradata-migration/`) before the task is considered complete:

- `ruff check target tests` \u2014 zero errors
- `pytest tests -q` \u2014 zero failures
- `ruff check --select=E,F target tests` \u2014 zero errors

These are the exact commands declared in `.opf/project.json`.

---

## How agents must use the ICA MCP tools

The MCP server exposing ICA capabilities is already wired in `opencode.json`. The
**architect** and **workers** are expected to use these tools as **expert advisers
during the swarm run**, not as APIs to wrap in code.

The minimum mandatory consultations are:

| When | Who | MCP tool | Purpose |
|------|-----|----------|---------|
| Before partitioning the work | architect | `ica_ica_chat_digital_workforce` (Data Engineer \u2013 Data Platforms, model id `4fffad08-c4b8-4c24-9167-4e300fb5df5e`) | Validate the proposed Teradata\u2192Redshift mapping (DISTKEY/SORTKEY choices, partition replacement) and capture the response into `target/docs/digital_worker_review.md`. |
| When in doubt about Redshift idioms | any worker | `ica_ica_chat_models` with `files: [{ type: \"collection\", id: \"6305e9e6-bc31-4467-abf3-5579d3319efd\" }]` | Query the IBM Consulting Advantage product-documentation collection for AWS migration patterns. Optional but encouraged; not enforced by tests. |

The capture into `target/docs/digital_worker_review.md` is the **only** evidence that the
ICA tools were consulted, and is enforced by criterion #6.

---

## Data Contracts

### Redshift `customer_dim` columns (immutable)

| Column | Type | Constraint |
|---|---|---|
| `customer_id` | `INTEGER` | `NOT NULL` |
| `customer_name` | `VARCHAR(120)` | |
| `email` | `VARCHAR(200)` | |
| `country_code` | `CHAR(2)` | |
| `customer_segment` | `VARCHAR(20)` | |
| `created_ts` | `TIMESTAMP` | |
| `updated_ts` | `TIMESTAMP` | |
| `active_flag` | `SMALLINT` | |

### Redshift `sales_fact` columns (immutable)

| Column | Type | Constraint |
|---|---|---|
| `sale_id` | `BIGINT` | `NOT NULL` |
| `customer_id` | `INTEGER` | `NOT NULL` |
| `product_id` | `INTEGER` | `NOT NULL` |
| `store_id` | `INTEGER` | |
| `sale_date` | `DATE` | `NOT NULL` |
| `sale_ts` | `TIMESTAMP` | |
| `quantity` | `DECIMAL(10,2)` | |
| `unit_price` | `DECIMAL(12,4)` | |
| `discount_pct` | `DECIMAL(5,4)` | |
| `tax_amount` | `DECIMAL(12,4)` | |
| `total_amount` | `DECIMAL(14,4)` | |
| `currency_code` | `CHAR(3)` | |
| `channel` | `VARCHAR(20)` | |
| `load_ts` | `TIMESTAMP` | |

### Glue job arguments

```
--JOB_NAME            <Glue job name, injected by the runtime>
--load_date           YYYY-MM-DD
--landing_bucket      <S3 bucket holding sales/dt=YYYY-MM-DD/ partitions>
--redshift_connection <Glue connection name pointing at the target Redshift cluster>
```

### Environment variables

No environment variables are required by the deliverables. Job arguments are passed by
the Glue runtime; the Redshift cluster credentials live in the Glue Connection referenced
by `--redshift_connection` (no secrets land in the repo).

---

## Target Project

This requirement targets the project declared in `target.json`:

```json
{ "project": "legacy-teradata-migration" }
```

The project root resolves to `workspace/legacy-teradata-migration/`. All path references
below are **relative to the project root**, not the repository root.

---

## Files in Scope

| File (project-relative) | Jurisdiction | Transformation |
|-------------------------|-------------|----------------|
| `target/redshift/ddl/customer_dim.sql` | Worker A | Create from scratch using criterion #1 |
| `target/redshift/ddl/sales_fact.sql` | Worker A | Create from scratch using criterion #2 |
| `target/glue/jobs/load_sales.py` | Worker B | Create from scratch using criterion #3 |
| `tests/test_redshift_ddl.py` | Worker A | Create per criterion #4 |
| `tests/test_glue_job.py` | Worker B | Create per criterion #4 |
| `tests/test_no_ica_client_code.py` | Worker C | Create per criterion #5 |
| `target/docs/migration_mapping.md` | Worker C | Create per criterion #6 |
| `target/docs/digital_worker_review.md` | Architect | Capture the ICA Digital Worker review per criterion #6 |

> Worker A owns `target/redshift/ddl/` plus the corresponding tests under `tests/test_redshift_ddl.py`.
> Worker B owns `target/glue/jobs/` plus `tests/test_glue_job.py`.
> Worker C owns `target/docs/migration_mapping.md` plus `tests/test_no_ica_client_code.py`.
> The architect personally writes `target/docs/digital_worker_review.md` because it is the
> capture of the architect's own ICA consultation.

## Files Out of Scope (read-only for all agents)

- `legacy/**` \u2014 the legacy estate; read-only inputs to the migration
- `specs/REQ-.../SPEC.md` \u2014 this file; immutable contract
- `AGENTS.md` \u2014 project context; do not modify
- `.env`, `.env.*` \u2014 secrets; never touch
- `runs/**` \u2014 append-only swarm history; never modify
- `tests/test_seed.py` \u2014 pre-seeded baseline test; do not modify
- `pyproject.toml`, `.opf/project.json`, `README.md` \u2014 project skeleton; do not modify
