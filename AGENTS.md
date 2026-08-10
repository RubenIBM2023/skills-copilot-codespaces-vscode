# AGENTS.md

Guidance for OpenCode agents working in this repository.

## Quick Start

- Run `opencode` in the project root to start
- Press `Tab` to switch between **Build** (full access) and **Plan** (read-only analysis) agents
- Use `@explore` to search the codebase, `@general` for multi-step tasks, `@scout` for external dependencies
- Press `/init` if this is your first time to let OpenCode analyze the project structure

## Project Structure (Multi-Project Factory)

```
.
├── .opencode/
│   ├── agents/             # architect (OPF orchestrator), worker (OPF swarm), code-reviewer, security-auditor, docs-writer
│   ├── plugins/            # hitl-guard.ts (HITL firewall + Git automation, project-aware)
│   ├── commands/           # Custom slash commands
│   └── tools/              # Custom tools
│
├── workspace/              # ── Multi-project root. One sub-folder per cloned project.
│   └── <project-id>/
│       ├── .opf/project.json    # Project metadata (id, name, lint_cmd, test_cmd, typecheck_cmd)
│       ├── src/                 # Application code
│       ├── tests/               # Test files
│       └── package.json
│
├── specs/                  # ── Immutable functional contracts. One folder per requirement.
│   └── REQ-YYYY-MM-DD-NNN-<slug>/
│       ├── SPEC.md              # The single source of truth for that requirement
│       └── target.json          # { "project": "<project-id>", "branch_prefix": "...", "title": "..." }
│
├── runs/                   # ── Append-only swarm run logs.
│   └── <REQ_ID>/<UTC_TIMESTAMP>/
│       ├── manifest.json        # Run metadata
│       ├── events.jsonl         # Full SSE event stream (one JSON per line)
│       ├── workers/<N>.log      # Per-worker plain text logs
│       └── summary.md           # Human-readable post-run summary
│
├── infraestructura_ia/
│   ├── swarm.py            # Python swarm orchestrator (requires `opencode serve`)
│   ├── registry.py         # REQ ↔ project resolution (paths, metadata, run dirs)
│   ├── observability.py    # Structured RunLogger (JSONL + manifest + summary)
│   └── requirements.txt
│
├── docs/
│   ├── guidelines.md       # Code standards (auto-loaded by opencode.json)
│   └── SPEC.md.template    # Template for new REQ specs
│
├── opencode.json           # Live config (git-ignored)
└── opencode.json.example   # Safe template with placeholder keys
```

## OPF Swarm Architecture

This project implements the **One-Person Factory (OPF)** pattern as a multi-project software factory:

1. **Create a SPEC** — The human operator writes a new requirement at `specs/REQ-YYYY-MM-DD-NNN-<slug>/SPEC.md` plus a `target.json` declaring which project under `workspace/` it modifies. SPECs are the only thing the operator directly authors. **Never change source code structure without updating the spec first.**
2. **`@architect`** reads the active SPEC, identifies the target project root via `target.json`, partitions work spatially, and delegates isolated tasks to `@worker` agents via the `task` tool.
3. **`@worker`** agents execute exclusively within their assigned jurisdiction under `workspace/<project-id>/`. No cross-jurisdiction writes. No peer-to-peer communication.
4. **`hitl-guard.ts`** blocks destructive commands, protects `specs/**`, `runs/**`, and `.env*`, runs lint/test from the active project root, and auto-commits + opens a Draft PR on success.
5. **Observability** — Every swarm run writes structured logs under `runs/<REQ_ID>/<UTC_TIMESTAMP>/`. Reconstruct any past execution from `events.jsonl` + `summary.md`.

### Run the automated swarm

```bash
# Terminal 1 — start the OpenCode server
opencode serve

# Terminal 2 — first time only: create & activate venv, install deps
cd infraestructura_ia && python -m venv .venv
source .venv/bin/activate           # macOS/Linux
# .\.venv\Scripts\Activate.ps1      # Windows PowerShell
pip install -r requirements.txt

# Terminal 2 — every run: activate venv, return to repo root, then launch
# Pass the REQ_ID of the requirement you want the swarm to work on:
python infraestructura_ia/swarm.py --req-id REQ-2026-05-31-001-teradata-to-aws
```

### Authoring a new requirement

```bash
# 1. Pick a REQ_ID using the registry helper:
python -c "from infraestructura_ia.registry import new_req_id; print(new_req_id('my-feature'))"
#   → REQ-2026-06-01-001-my-feature

# 2. Create the spec folder and files:
mkdir -p specs/REQ-2026-06-01-001-my-feature
cp docs/SPEC.md.template specs/REQ-2026-06-01-001-my-feature/SPEC.md

# 3. Author target.json:
cat > specs/REQ-2026-06-01-001-my-feature/target.json <<EOF
{ "project": "<project-id>", "branch_prefix": "opencode/swarm", "title": "..." }
EOF

# 4. Edit SPEC.md, then dispatch the swarm.
```

### Adding a new project to the factory

```bash
# 1. Clone or scaffold under workspace/
git clone <repo> workspace/<project-id>

# 2. Create .opf/project.json with the verification commands
mkdir -p workspace/<project-id>/.opf
cat > workspace/<project-id>/.opf/project.json <<EOF
{
  "id": "<project-id>",
  "name": "<Human readable name>",
  "language": "typescript",
  "lint_cmd": "npm run lint",
  "test_cmd": "npm test",
  "typecheck_cmd": "npx tsc --noEmit"
}
EOF
```

## Agent Roles

### Primary Agents
- **Build** — Full file and bash access. Default implementation agent.
- **Plan** — Read-only analysis. No edits or bash. Use for strategy and review.
- **@architect** — OPF orchestrator. Reads `specs/<REQ_ID>/SPEC.md`, plans the task partition for the target project, delegates to workers.

### Subagents (`@mention` to invoke)
- **@worker** — Jurisdiction-isolated code mutator. Hidden; invoked by architect only. Operates within `workspace/<project-id>/` only.
- **@explore** — Fast codebase search. No modifications.
- **@general** — Multi-step researcher. Full access except todos.
- **@scout** — External dependency inspector.
- **@code-reviewer** — Code quality review (read-only).
- **@security-auditor** — OWASP vulnerability scanning (read-only).
- **@docs-writer** — Documentation creation.

## Developer Commands

Project-level commands run from `workspace/<project-id>/`:

```bash
cd workspace/<project-id>
npm run lint          # Lint and auto-fix
npm run test          # Run test suite
npm run build         # Compile
npx tsc --noEmit      # Type-check without emitting
```

Repo-level commands run from the repository root:

```bash
npm run typecheck     # Type-check the hitl-guard plugin
```

## Conventions

- **Secret management** — Never paste API keys in messages. Use `.env.local` (git-ignored).
- **SDD rule** — All requirement changes go into a new `specs/REQ-.../SPEC.md` first. Code follows spec.
- **Immutability** — `specs/**` is immutable once a REQ is created. `runs/**` is append-only. Both are enforced by `hitl-guard.ts`.
- **Permissions** — Agents run with `"*": "ask"` by default. Lint, test, and read-only git commands are pre-approved.
- **Generated code** — Mark generated files in `.gitignore`; document the generation command here.

## Observability & Decision Traces

Every swarm execution — **automated or manual** — must produce the same set of artefacts under `runs/<REQ_ID>/<UTC_TIMESTAMP>/`. This is non-negotiable: it is what allows any past run to be reconstructed and audited.

### Run directory layout

```
runs/<REQ_ID>/<UTC_TIMESTAMP>/
├── manifest.json              # Run metadata: status, started, ended, duration, branch, project
├── events.jsonl               # Append-only SSE event stream — one JSON per line
├── summary.md                 # Human-readable post-run report
├── architect.decisions.md     # Architect's SPEC interpretation, partition table, rollback plan
└── workers/
    ├── <N>.log                # Per-worker plain-text activity log (best-effort, plugin-captured)
    └── <N>.decisions.md       # Per-worker authoritative DECISIONS block (worker-written)
```

### How each mode produces these artefacts

| Mode | Trigger | What writes the trace |
|---|---|---|
| **Automated** (`swarm.py --req-id ...`) | The Python orchestrator owns the run dir | `RunLogger` writes `manifest.json`, `events.jsonl`, `summary.md`. Architect writes `architect.decisions.md`. Workers write `workers/<N>.decisions.md`. The plugin appends to `workers/<N>.log` from session-idle output. |
| **Manual** (TUI, `@architect ...`) | Architect resolves the run dir on first action | The architect runs `node .opencode/tools/run-dir.js --json` to get (or lazily create) the active run dir, then writes `architect.decisions.md`. Each worker repeats the same resolver call and writes its own `workers/<N>.decisions.md`. The plugin still runs quality gates and Git automation on session idle. |

### Resolving the active run directory

In **both** modes, agents call:

```bash
node .opencode/tools/run-dir.js --json
```

The resolver returns:

```json
{
  "run_dir": "C:/.../runs/REQ-2026-05-31-001-teradata-to-aws/2026-05-31T14-22-08Z",
  "req_id": "REQ-2026-05-31-001-teradata-to-aws",
  "mode": "env|git-branch|fallback",
  "override": true|false
}
```

Resolution order: `OPF_RUN_DIR` env var (set by `swarm.py`) → `OPF_REQ_ID` env var → Git branch parse `opencode/swarm-<REQ_ID>` → fallback to `runs/manual/<TS>/`.

### Authoring trace files

- **Architect** writes `<run_dir>/architect.decisions.md` (template lives in `.opencode/agents/architect.md`, step 6).
- **Workers** write `<run_dir>/workers/<N>.decisions.md` with the `DECISIONS:` block defined in `.opencode/agents/worker.md`.
- Both files are version-controlled via Git (committed by the HITL plugin together with code changes).

### Post-run audit checklist

After every swarm run, before merging the Draft PR, verify:

```bash
# Replace <REQ_ID> and <TS> with the real values printed by the swarm/architect.
ls runs/<REQ_ID>/<TS>/
#  manifest.json      ← status: success
#  events.jsonl       ← non-empty, last event is run.end
#  summary.md         ← human report
#  architect.decisions.md  ← partition + justification
#  workers/1.decisions.md, 2.decisions.md, ...  ← one per dispatched worker
#  workers/1.log, 2.log, ...                    ← plugin-captured stdout
```

If any of `architect.decisions.md` or `workers/<N>.decisions.md` is missing, the run is **not auditable** — re-run or annotate manually before merging.

## Best Practices

1. Ask Plan agent to review before committing: `Review this diff for best practices`
2. Run `npm run lint && npm test` from the project root before every commit
3. Verify no secrets in `git diff`
4. Inspect `runs/<REQ_ID>/<TIMESTAMP>/summary.md` after every swarm run

---

**Last updated**: 31 May 2026
