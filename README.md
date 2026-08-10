# Project Template with OpenCode — One-Person Factory (OPF)

@author: Dennys Mallqui (dennys.mallqui@ibm.com)

📖 [Lee este documento en español](README.es.md)

This is a project template optimized for working with [OpenCode](https://opencode.ai), an AI-powered coding agent, implementing the **One-Person Factory (OPF)** pattern: a system where a single human operator orchestrates a swarm of specialized AI agents to develop software at scale — across **multiple projects simultaneously**.

---

## Quick Start

### 1. Install OpenCode

```bash
# Using curl
curl -fsSL https://opencode.ai/install | bash

# Using npm
npm install -g opencode-ai

# Using Homebrew (macOS/Linux)
brew install anomalyco/tap/opencode

# Using Chocolatey (Windows)
choco install opencode
```

### 2. Configure API Access (ICA)

This project connects to **IBM Consulting Advantage (ICA)** as the LLM provider. Copy the example config and fill in your credentials:

```bash
# macOS / Linux
cp opencode.json.example opencode.json

# Windows (PowerShell)
Copy-Item opencode.json.example opencode.json
```

Open `opencode.json` and replace `YOUR_ICA_API_KEY` under `provider.ica.options.apiKey` with your real ICA API key.

> **Security**: `opencode.json` is git-ignored — never commit it. Keep your keys in this local file only.

### 3. Install Dependencies (IDE type checking — one time)

```bash
# From repo root — installs devDependencies for the HITL Guard plugin type definitions
npm install
```

Then reload the TS server in VS Code: `Ctrl+Shift+P` → **"TypeScript: Restart TS Server"**.

### 4. Start Your Session

```bash
opencode
# then:
/init
```

---

## Project Structure (Multi-Project Factory)

```
.
├── .opencode/
│   ├── agents/
│   │   ├── architect.md        # Primary OPF orchestrator — reads specs/<REQ>/SPEC.md
│   │   ├── worker.md           # Jurisdiction-isolated worker — operates in workspace/<id>/
│   │   ├── code-reviewer.md    # Code quality reviewer (read-only)
│   │   ├── security-auditor.md # Security auditor (read-only)
│   │   └── docs-writer.md      # Documentation writer
│   ├── plugins/
│   │   └── hitl-guard.ts       # HITL plugin: firewall + project-aware Git/PR automation
│   ├── commands/               # Custom slash commands
│   └── tools/                  # Custom tools
│
├── workspace/                  # Multi-project root — one sub-folder per cloned project
│   └── <project-id>/
│       ├── .opf/
│       │   └── project.json    # Project metadata: id, lint_cmd, test_cmd, typecheck_cmd
│       ├── src/                # Application source code
│       ├── tests/              # Test files
│       └── package.json        # Project-level dependencies and scripts
│
├── specs/                      # Immutable functional contracts — one folder per requirement
│   └── REQ-YYYY-MM-DD-NNN-<slug>/
│       ├── SPEC.md             # The single source of truth for that requirement
│       └── target.json         # { "project": "<project-id>", "branch_prefix": "...", "title": "..." }
│
├── runs/                       # Append-only swarm execution logs
│   └── <REQ_ID>/<UTC_TIMESTAMP>/
│       ├── manifest.json       # Run metadata (status, duration, branch)
│       ├── events.jsonl        # Full SSE event stream — one JSON object per line
│       ├── workers/<N>.log     # Per-worker plain-text activity log
│       └── summary.md          # Human-readable post-run summary
│
├── infraestructura_ia/
│   ├── swarm.py                # Python swarm orchestrator (multi-project, --req-id)
│   ├── registry.py             # REQ ↔ project resolution (paths, metadata, run dirs)
│   ├── observability.py        # Structured RunLogger (JSONL + manifest + summary)
│   └── requirements.txt        # Python dependencies (opencode-ai)
│
├── docs/
│   ├── SPEC.md.template        # Template for authoring new REQ specs
│   └── guidelines.md           # Development standards (auto-loaded by opencode.json)
│
├── AGENTS.md                   # Agent context (auto-loaded by opencode.json)
├── opencode.json               # Live config (git-ignored, contains keys)
└── opencode.json.example       # Safe template with placeholder keys
```

---

## OPF Architecture: The One-Person Factory

### The Concept

The One-Person Factory (OPF) is a development pattern where **a single human operator** writes only the **functional specification** and delegates all implementation to a swarm of AI agents. The factory serves **multiple projects** simultaneously via the `workspace/` multi-project layout.

```
  Human operator
       │
       ▼  writes
  specs/REQ-YYYY-MM-DD-NNN-<slug>/SPEC.md   ← immutable contract
  specs/REQ-.../target.json                  ← points to workspace/<project-id>/
       │
       ▼  reads
  [Agent: architect]  — decomposes spec into jurisdictional tasks
       │
  ┌────┴────┬─────────┐
  ▼         ▼         ▼
[Worker A] [Worker B] [Worker C]   ← isolated by path jurisdiction
workspace/  workspace/  workspace/  (no inter-agent communication)
<id>/src/   <id>/api/   <id>/tests/
       │
       ▼  persists
  runs/<REQ_ID>/<UTC_TIMESTAMP>/
  ├── events.jsonl   ← full SSE stream
  ├── manifest.json  ← run metadata
  ├── workers/*.log  ← per-worker logs
  └── summary.md     ← human-readable report
```

### Key Principles

| Principle | Description |
|---|---|
| **Spec-Driven Development (SDD)** | `specs/<REQ>/SPEC.md` is the only artifact the human writes. Code is ephemeral; the specification is the contract. |
| **Multi-Project Factory** | Each project lives in `workspace/<project-id>/` with its own `.opf/project.json`. One swarm run targets exactly one project via `target.json`. |
| **Strict Jurisdictional Partitioning** | Each worker owns an exclusive repository-relative path. No worker may write outside its jurisdiction, preventing parallel collisions. |
| **Hub-and-Spoke Communication** | Workers do not talk to each other. All communication is vertical: orchestrator → worker. Zero peer-to-peer. |
| **HITL (Human-in-the-Loop)** | `hitl-guard.ts` blocks destructive commands, protects `specs/**` and `runs/**`, runs quality gates from the project root, and auto-commits + opens a Draft PR. |
| **Structured Observability** | Every swarm run writes `events.jsonl`, `manifest.json`, per-worker logs, and `summary.md` under `runs/<REQ_ID>/<UTC_TIMESTAMP>/`. Any past run is fully reconstructible. |

---

## REQ Identifier Format

Every requirement has a unique, time-ordered identifier:

```
REQ-YYYY-MM-DD-NNN-<slug>
    └── date ──┘ └┘ └──┘
                 │   └─ kebab-case slug describing the change
                 └─ 3-digit counter auto-incremented per day (001, 002, ...)
```

**Examples:**
- `REQ-2026-05-31-001-teradata-to-aws` — first requirement created on 31 May 2026 (the reference exercise shipped with this template)
- `REQ-2026-06-01-001-user-auth` — first requirement on 1 June 2026
- `REQ-2026-06-01-002-rate-limit` — second requirement on the same day

Generate the next ID automatically:
```bash
python -c "from infraestructura_ia.registry import new_req_id; print(new_req_id('my-feature'))"
```

---

## OPF Workflow

### Mode 1: Manual Flow with the OpenCode TUI

1. **Create `specs/REQ-.../SPEC.md`** and `target.json` for the requirement.
2. **Start Build** (`Tab`) and invoke the `@architect` agent, passing the REQ_ID.
3. The orchestrator reads the SPEC, identifies the target project, designs the plan, and delegates tasks to workers via the `task` tool.
4. Workers execute in parallel within their assigned jurisdictions under `workspace/<project-id>/`.
5. The HITL plugin runs quality gates from the project root, commits, and opens a Draft PR.

### Mode 2: Automated Swarm with Python

```bash
# Terminal 1 — start the OpenCode server
opencode serve

# Terminal 2 — first time only: create & activate venv, install deps
cd infraestructura_ia && python -m venv .venv
.\.venv\Scripts\Activate.ps1        # Windows (PowerShell)
source .venv/bin/activate           # macOS / Linux
pip install -r requirements.txt

# Terminal 2 — every run: activate venv, return to repo root, then launch
cd ..
python infraestructura_ia/swarm.py --req-id REQ-2026-05-31-001-teradata-to-aws
```

The Python orchestrator (`swarm.py`):
1. Resolves `specs/<REQ_ID>/SPEC.md` and `target.json` via `registry.py`
2. Creates a clean Git branch named `<branch_prefix>-<REQ_ID>`
3. Initialises a `RunLogger` — all events are persisted to `runs/<REQ_ID>/<TS>/`
4. Sends the SPEC to the `architect` agent for task decomposition (JSON plan)
5. Dispatches workers in parallel with `asyncio.gather()`, injecting `PROJECT_ROOT` and quality gate commands
6. Captures the full SSE event stream to `events.jsonl`
7. Closes the logger with `summary.md` and triggers the HITL plugin for Git/PR automation

Expected output:
```
[swarm] Requirement: REQ-2026-05-31-001-teradata-to-aws
[swarm] Project:     legacy-teradata-migration (Legacy Teradata DW → AWS Migration)
[swarm] Run directory: runs/REQ-2026-05-31-001-teradata-to-aws/2026-05-31T14-22-08Z
[14:22:08] run.start req_id=REQ-2026-05-31-001-teradata-to-aws project_id=legacy-teradata-migration
[14:22:09] orchestrator.session.created session_id=...
[14:22:10] orchestrator.plan.received worker_count=3
[14:22:10] worker.dispatch worker_idx=1 jurisdiction=workspace/legacy-teradata-migration/target/redshift/ddl/
[14:22:10] worker.dispatch worker_idx=2 jurisdiction=workspace/legacy-teradata-migration/target/glue/jobs/
[14:22:10] worker.dispatch worker_idx=3 jurisdiction=workspace/legacy-teradata-migration/target/docs/
[swarm] Done. Run summary: runs/REQ-2026-05-31-001-teradata-to-aws/2026-05-31T14-22-08Z/summary.md
```

### Mode 3: Adding a New Project to the Factory

```bash
# 1. Clone or scaffold the project under workspace/
git clone <repo-url> workspace/<project-id>

# 2. Create project metadata
mkdir -p workspace/<project-id>/.opf
# create workspace/<project-id>/.opf/project.json with the verification commands
# of your stack. Two real examples:

# TypeScript / Node project:
{
  "id": "<project-id>",
  "name": "Human-readable project name",
  "language": "typescript",
  "lint_cmd": "npm run lint",
  "test_cmd": "npm test",
  "typecheck_cmd": "npx tsc --noEmit"
}

# Python / SQL project (like the reference example shipped in this repo):
{
  "id": "<project-id>",
  "name": "Human-readable project name",
  "language": "python+sql",
  "lint_cmd": "ruff check src tests",
  "test_cmd": "pytest tests -q",
  "typecheck_cmd": "ruff check --select=E,F src tests"
}
```

### Mode 4: Authoring a New Requirement

```bash
# 1. Generate a unique REQ_ID
python -c "from infraestructura_ia.registry import new_req_id; print(new_req_id('my-feature'))"
# → REQ-2026-06-01-001-my-feature

# 2. Create the spec folder
mkdir specs/REQ-2026-06-01-001-my-feature
cp docs/SPEC.md.template specs/REQ-2026-06-01-001-my-feature/SPEC.md

# 3. Create target.json
# { "project": "<project-id>", "branch_prefix": "opencode/swarm", "title": "..." }

# 4. Edit SPEC.md, then dispatch the swarm
python infraestructura_ia/swarm.py --req-id REQ-2026-06-01-001-my-feature
```

---

## Reference Exercise

This template ships with **one** ready-to-run requirement that exercises the full OPF loop on a realistic Data Engineering use case. Use it as the canonical reference when authoring new SPECs.

### REQ-2026-05-31-001-teradata-to-aws — Legacy Teradata DW → AWS migration

**Target project:** `workspace/legacy-teradata-migration/`

This requirement migrates a self-contained slice of a legacy on-premise **Teradata** data warehouse (one dimension + one fact + one BTEQ daily load) to **AWS** (Amazon Redshift DDL + AWS Glue PySpark job + migration documentation).

It demonstrates the full OPF pattern end-to-end on a Data Engineering scenario:

- **Spec → architect → workers → quality gate → Draft PR** loop on a non-trivial migration.
- **3 parallel workers**, each owning an exclusive jurisdiction inside `target/` (Redshift DDL, Glue PySpark job, migration docs + prohibition test).
- **ICA MCP tools used as expert advisers during the run** — not embedded as client code:
  - The `architect` consults the **ICA Digital Worker** *Data Engineer – Data Platforms* (`ica_ica_chat_digital_workforce`, model id `4fffad08-c4b8-4c24-9167-4e300fb5df5e`) to validate DISTKEY/SORTKEY choices and partition replacement, and captures the response into `target/docs/digital_worker_review.md`.
  - Workers may consult the **IBM Consulting Advantage product-documentation collection** (`ica_ica_chat_models` with `files: [{ type: "collection", id: "6305e9e6-..." }]`) when in doubt about Redshift idioms.
- **A prohibitive test** (`tests/test_no_ica_client_code.py`) enforces that the deliverables under `target/**` contain **no** ICA endpoint, MCP tool name, model id or collection id (the only exception is `target/docs/digital_worker_review.md`, which is the verbatim Digital Worker capture).
- **Verification** (run from the project root):
  - `ruff check target tests` — zero errors
  - `pytest tests -q` — zero failures
  - `ruff check --select=E,F target tests` — zero errors

```bash
python infraestructura_ia/swarm.py --req-id REQ-2026-05-31-001-teradata-to-aws
```

> **Why this design?** The MCP tools that connect OpenCode to ICA are configured once in `opencode.json` and consumed by the agents themselves while the swarm runs. The reference exercise demonstrates the right pattern: **agents call ICA**, **deliverables don't**. Don't write SPECs that ask workers to embed ICA SDK calls in the produced artefacts.

### Discovering ICA Digital Workers for a new SPEC

When authoring a new SPEC that needs an expert reviewer, find the right Digital Worker first:

```text
# From inside an OpenCode TUI session (the ICA MCP must be wired in opencode.json):
@explore Use the ica_ica_list_digital_workforce MCP tool and list every entry whose
         description mentions <your-domain>. For each match, report id, name, and a
         one-line summary. Recommend the single best candidate.
```

Then reference the chosen Digital Worker by **id** inside your SPEC's *"How agents must use the ICA MCP tools"* section (see `specs/REQ-2026-05-31-001-teradata-to-aws/SPEC.md` for the template).

---

## Observability & Traceability

Every swarm run produces a structured audit trail under `runs/<REQ_ID>/<UTC_TIMESTAMP>/`.

### Run Directory Contents

| File | Description |
|---|---|
| `manifest.json` | Machine-readable run metadata: status, started, ended, duration_s, branch, workers |
| `events.jsonl` | Every SSE event captured, one JSON object per line, millisecond timestamps |
| `architect.decisions.md` | Architect's SPEC interpretation, jurisdictional partition table, and rollback plan (written by `@architect`) |
| `workers/<N>.log` | Plain-text activity log for each worker agent (best-effort, plugin-captured) |
| `workers/<N>.decisions.md` | Authoritative `DECISIONS:` block written by worker `<N>` itself (survives manual flows) |
| `summary.md` | Human-readable report: dates, status, statistics, files changed |

> **Both flows produce the same artefacts.** The automated `swarm.py` orchestrator, and the manual TUI flow via `@architect`, both call `node .opencode/tools/run-dir.js --json` to resolve a shared run directory and write `architect.decisions.md` + `workers/<N>.decisions.md` into it. See `AGENTS.md` → "Observability & Decision Traces" for the full audit checklist.

### Event Types in `events.jsonl`

```jsonl
{"ts":"2026-05-31T14:22:08.123Z","type":"run.start","req_id":"REQ-...","project_id":"legacy-teradata-migration"}
{"ts":"2026-05-31T14:22:09.456Z","type":"orchestrator.session.created","session_id":"abc123"}
{"ts":"2026-05-31T14:22:10.789Z","type":"orchestrator.plan.received","worker_count":1}
{"ts":"2026-05-31T14:22:11.012Z","type":"worker.dispatch","worker_idx":1,"jurisdiction":"workspace/..."}
{"ts":"2026-05-31T14:22:14.345Z","type":"sse.tool.execute.before","tool":"edit","path":"workspace/..."}
{"ts":"2026-05-31T14:22:22.678Z","type":"run.end","status":"success","duration_s":14.5}
```

### Example `summary.md`

```markdown
# Run REQ-2026-05-31-001-teradata-to-aws

**Started:** 2026-05-31T14:22:08.123Z
**Ended:**   2026-05-31T14:22:22.678Z
**Status:**  ✅ success
**Duration:** 14.5s
**Branch:**  opencode/swarm-REQ-2026-05-31-001-teradata-to-aws
**Project:** legacy-teradata-migration

## Statistics
- Events recorded: 12
- Workers active: 3
- workers: 3 | files_changed: 8
```

---

## Available Agents

| Agent | Mode | Model | Description |
|-------|------|-------|-------------|
| `architect` | primary | claude-sonnet-4-6 | OPF orchestrator: ingests `specs/<REQ>/SPEC.md`, partitions tasks by project path, delegates to workers |
| `worker` | subagent (hidden) | claude-haiku-4-5 | Isolated executor: transforms code in `workspace/<project-id>/` only |
| `code-reviewer` | subagent | (inherits) | Code quality and best-practice review (read-only) |
| `security-auditor` | subagent | (inherits) | OWASP vulnerability scanning (read-only) |
| `docs-writer` | subagent | (inherits) | Documentation creation and updates |

### Invoking Agents

```
# Invoke the orchestrator manually
@architect Analyze REQ-2026-05-31-001-teradata-to-aws and plan the implementation

# Code review
@code-reviewer Review the changes in workspace/legacy-teradata-migration/target/redshift/ddl/

# Security audit
@security-auditor Audit the Glue PySpark job in workspace/legacy-teradata-migration/target/glue/jobs/

# Switch between primary agents
Tab  →  toggle between Build and Plan
```

### Useful Commands

| Command / Key | Action |
|---|---|
| `/init` | Analyze project structure (run on first session) |
| `/compact` | Summarize conversation to preserve context |
| `/undo` | Revert last batch of changes |
| `Ctrl+P` | List all available actions |
| `Tab` | Switch between Build and Plan agents |
| `@` | Mention a subagent or reference a file |

---

## `infraestructura_ia/` — Python Tooling

### `registry.py` — REQ & Project Resolver

```python
from infraestructura_ia.registry import (
    new_req_id,           # Generate next REQ_ID for today (auto-increments NNN)
    list_requirements,    # List all REQ_IDs in specs/, sorted
    resolve_requirement,  # Load SPEC.md + target.json → RequirementMeta
    resolve_project,      # Load .opf/project.json → ProjectMeta
    new_run_dir,          # Create runs/<REQ>/<TS>/workers/ → Path
)
```

### `observability.py` — Structured Run Logger

```python
from infraestructura_ia.observability import RunLogger

logger = RunLogger(run_dir, req_id, project_id, branch)
logger.event("worker.dispatch", worker_idx=1, jurisdiction="workspace/...")
logger.worker(1, "transformation applied successfully")
logger.close("success", workers=1, files_changed=3)
# Produces: events.jsonl, manifest.json, workers/1.log, summary.md
```

### `swarm.py` — Automated Orchestrator

```bash
# Show help
python infraestructura_ia/swarm.py --help

# Run against a specific requirement
python infraestructura_ia/swarm.py --req-id REQ-2026-05-31-001-teradata-to-aws
```

---

## ICA Models & MCP ICA Server Setup

### Available Models

| Model ID | Description | OPF Role | Prefill via ICA |
|---|---|---|---|
| `claude-haiku-4-5` | Fast, lightweight **(default)** | `worker` agents, swarm dispatch | ✅ Reliable |
| `claude-sonnet-4-6` | Balanced | `architect`, manual chat | ⚠️ Intermittent |
| `claude-opus-4-7` | Most capable | Complex reasoning | ✅ Reliable |
| `meta-llama/llama-4-maverick-17b-128e-instruct-fp8` | LLaMA 4 Maverick | Conversational only | ❌ No |
| `ibm/granite-4-h-small` | IBM Granite small | Conversational only | ❌ No |
| `gemma-4-26b-a4b-it` | Google Gemma 4 | Conversational only | ❌ No |

> **Prefill via ICA**: Use ✅ models for any flow involving the `task` tool, `@architect` decomposition, or `swarm.py`. LLaMA / Granite / Gemma reject the assistant-prefill pattern OpenCode requires for structured output.

### MCP ICA Server Setup

```bash
# Clone and build
git clone https://github.ibm.com/dennys-mallqui/mcp-ica-2.0-server.git
cd mcp-ica-2.0-server && npm install && npm run build
```

Wire it in `opencode.json`:
```jsonc
"mcp": {
  "ica": {
    "type": "local",
    "command": ["node", "C:/path/to/mcp-ica-2.0-server/dist/server.js"],
    "environment": { "ICA_API_KEY": "<YOUR_MCP_ICA_API_KEY>" }
  }
}
```

### MCP ICA Legacy Server Setup

```bash
# Clone and build
git clone https://github.ibm.com/dennys-mallqui/mcp-ica-legacy-server.git
cd mcp-ica-legacy-server && npm install && npm run build
```

Wire it in `opencode.json`:
```jsonc
"mcp": {
  "icav1": {
    "type": "local",
    "command": ["node", "/path/to/mcp-ica-legacy-server/dist/index.js"],
    "environment": {
      "ICA_BASE_URL": "https://servicesessentials.ibm.com",
      "ICA_API_KEY": "YOUR_MCP_ICA_API_KEY_V1",
      "ICA_TIMEOUT_MS": "30000",
      "ICA_LOG_LEVEL": "info",
      "ICA_MAX_RETRIES": "3"
    },
    "enabled": true
  }
}
```

> **Two independent keys**: `provider.ica.options.apiKey` (chat completions) and `mcp.ica.environment.ICA_API_KEY` (MCP tools) are separate credentials. Configure each explicitly.

To create APIKey in ICA 2.0 and Legacy: https://pages.github.ibm.com/guild-of-coding-agents-at-consulting/documentation/docs/coding-agents/cline/getting-started

---

## Developer Commands

| Scope | Command | Description |
|---|---|---|
| **Repo root** | `npm run typecheck` | Type-check `hitl-guard.ts` plugin |
| **Project root** | `npm run lint` | Lint and auto-fix (run from `workspace/<id>/`) |
| **Project root** | `npm test` | Run test suite |
| **Project root** | `npx tsc --noEmit` | Type-check without emitting |
| **Repo root** | `python -m infraestructura_ia.registry` | Test registry resolution |
| **Repo root** | `python infraestructura_ia/swarm.py --help` | Show swarm CLI options |

> **Important**: Quality gate commands (`lint`, `test`, `typecheck`) run from `workspace/<project-id>/`, not from the repo root. The HITL plugin and swarm workers enforce this automatically.

---

## Testing Without Polluting the Template

Use a Git worktree to validate the factory in a disposable sandbox:

```bash
# Create sandbox (shares git history, isolated working tree)
git worktree add ../opf-test-sandbox HEAD
cd ../opf-test-sandbox

# Disconnect remote to prevent Draft PRs on the template repo
git remote remove origin

# Create a sandbox branch
git checkout -b sandbox/template-validation

# Configure credentials
Copy-Item opencode.json.example opencode.json   # Windows
cp opencode.json.example opencode.json           # macOS/Linux

# Install root devDependencies (IDE types)
npm install

# Validate Mode 1 — TUI
opencode

# Validate Mode 2 — Automated swarm
# Terminal 1:
opencode serve
# Terminal 2:
cd infraestructura_ia && python -m venv .venv
.\.venv\Scripts\Activate.ps1 && pip install -r requirements.txt
cd ..
python infraestructura_ia/swarm.py --req-id REQ-2026-05-31-001-teradata-to-aws

# Destroy sandbox (from original repo root)
cd C:\path\to\agentic-sdlc-sdd-architecture
git worktree remove ../opf-test-sandbox --force
```

Expected swarm output (sandbox):
```
[swarm] Requirement: REQ-2026-05-31-001-teradata-to-aws
[swarm] Project:     legacy-teradata-migration (Legacy Teradata DW → AWS Migration)
[swarm] Run directory: runs/REQ-2026-05-31-001-teradata-to-aws/2026-05-31T...Z
[14:22:10] orchestrator.plan.received worker_count=3
[14:22:11] worker.dispatch worker_idx=1 jurisdiction=workspace/legacy-teradata-migration/target/redshift/ddl/
[swarm] Done. Run summary: runs/.../summary.md
```

---

## Troubleshooting

### "This model does not support assistant message prefill"

**Cause:** OpenCode injects a trailing `role:"assistant"` message for structured output. ICA only honours this for Anthropic-family models.

**Fix:** Use `ica/claude-haiku-4-5` or `ica/claude-opus-4-7` for any flow involving `task` tool or `swarm.py`. Reserve LLaMA/Granite/Gemma for single-turn chat only.

### Quality gate passes but nothing was checked

**Cause:** `workspace/<project-id>/.opf/project.json` (or the underlying project) declares `echo`-style placeholder commands instead of real linters/test runners.

**Fix:** Replace with real commands in `.opf/project.json`. Two real-world examples:

```jsonc
// TypeScript / Node
{
  "lint_cmd": "npm run lint",
  "test_cmd": "npm test",
  "typecheck_cmd": "npx tsc --noEmit"
}

// Python / SQL (matches the reference example)
{
  "lint_cmd": "ruff check target tests",
  "test_cmd": "pytest tests -q",
  "typecheck_cmd": "ruff check --select=E,F target tests"
}
```

### `swarm.py` fails with `FileNotFoundError: SPEC.md not found`

**Cause:** The `--req-id` value does not match any folder under `specs/`.

**Fix:** Run `python -c "from infraestructura_ia.registry import list_requirements; print(list_requirements())"` to see valid REQ_IDs.

### Other common issues

| Problem | Fix |
|---|---|
| OpenCode won't start | `opencode --version`; reinstall: `npm install -g opencode-ai` |
| API 401/403 from ICA | Check both `apiKey` (chat) and `ICA_API_KEY` (MCP) separately |
| MCP server fails | Verify absolute path to `dist/server.js`; re-run `npm run build` |
| Windows path error | Use forward slashes in JSON: `C:/Users/...` |
| Python dependency conflict | Always run inside activated `.venv`; `pip uninstall opencode-ai` if installed globally |
| Quality gate runs from wrong dir | Set `OPF_PROJECT_PATH=workspace/<project-id>` env var or ensure only one project is in `workspace/` |

---

## Resources

- [OpenCode Official Site](https://opencode.ai)
- [OpenCode Documentation](https://opencode.ai/docs)
- [Agent Configuration](https://opencode.ai/docs/agents)
- [Plugins](https://opencode.ai/docs/plugins)
- [SDK Reference](https://opencode.ai/docs/sdk)
- [Report Issues](https://github.com/anomalyco/opencode/issues)
- [Discord Support](https://opencode.ai/discord)
- [MCP ICA Server](https://github.ibm.com/dennys-mallqui/mcp-ica-2.0-server)

---

**Last updated**: 31 May 2026
