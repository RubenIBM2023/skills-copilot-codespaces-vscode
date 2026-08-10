#!/usr/bin/env node
/**
 * OPF Run Directory Resolver
 * --------------------------
 * Resolves (and lazily creates) the active swarm run directory.
 *
 * Resolution order:
 *   1. process.env.OPF_RUN_DIR  → explicit override (used by swarm.py)
 *   2. process.env.OPF_REQ_ID   → REQ identifier
 *   3. Git branch matching /opencode\/swarm-(REQ-[\w-]+)$/ → extracted REQ_ID
 *   4. Fallback: "manual"        → exploratory TUI session
 *
 * Idempotency (manual/TUI sessions):
 *   The first call writes the resolved path to <REPO_ROOT>/.opencode/run-dir.current
 *   (a git-ignored temp file). Subsequent calls within SESSION_TTL_MS reuse that path
 *   instead of creating a new timestamped directory. The automated path (swarm.py)
 *   always uses OPF_RUN_DIR and bypasses this file entirely.
 *
 * Output (single line on stdout):
 *   <absolute_path_with_forward_slashes>
 *
 * Directory tree created (idempotent):
 *   runs/<REQ_ID|"manual">/<UTC-TIMESTAMP>/workers/
 *
 * Timestamp format: YYYY-MM-DDTHH-mm-ssZ (filesystem-safe; colons replaced).
 *
 * Usage:
 *   node .opencode/tools/run-dir.js              # prints active run dir
 *   node .opencode/tools/run-dir.js --json       # { run_dir, req_id, mode, override }
 *   node .opencode/tools/run-dir.js --reset      # clears the session cache
 *
 * Exit code: 0 on success, 1 on error.
 */

"use strict";

const fs = require("node:fs");
const path = require("node:path");
const { execSync } = require("node:child_process");

// Anchor REPO_ROOT to the script's own location (.opencode/tools/ → ../../ = repo root).
// NEVER use process.cwd() here — agents may invoke this from workspace subdirectories.
const REPO_ROOT = path.resolve(__dirname, "..", "..");

// Path to the session-scoped cache file (git-ignored).
const CACHE_FILE = path.join(REPO_ROOT, ".opencode", "run-dir.current");

// How long (ms) the cache file is considered valid. One hour covers any realistic session.
const SESSION_TTL_MS = 60 * 60 * 1000;

// Valid REQ_ID pattern — must match registry.py's _REQ_ID_RE.
const REQ_ID_RE = /^REQ-\d{4}-\d{2}-\d{2}-\d{3}-[a-z0-9][a-z0-9-]*$/;

// Timeout for the git subprocess — prevents indefinite hang on index lock or GCM prompt.
const GIT_TIMEOUT_MS = 3000;

// ── Utilities ────────────────────────────────────────────────────────────────

function utcTimestamp() {
  return new Date()
    .toISOString()
    .replace(/\.\d{3}Z$/, "Z")
    .replace(/:/g, "-");
}

function toForwardSlash(p) {
  return p.replace(/\\/g, "/");
}

/**
 * Validate and sanitize a REQ_ID coming from env or git branch.
 * Throws if the value is neither "manual" nor a well-formed REQ-... identifier.
 * Prevents path-traversal attacks via crafted env vars or branch names.
 *
 * @param {string} reqId
 * @returns {string} the same value if valid
 */
function sanitizeReqId(reqId) {
  if (reqId === "manual") return reqId;
  if (REQ_ID_RE.test(reqId)) return reqId;
  throw new Error(
    `[run-dir] Unsafe or invalid REQ_ID; expected REQ-YYYY-MM-DD-NNN-<slug> or "manual". Got: ${JSON.stringify(reqId)}`
  );
}

// ── Core Logic ────────────────────────────────────────────────────────────────

function detectReqId() {
  // 1. Explicit env var — highest priority.
  const envReq = process.env.OPF_REQ_ID;
  if (envReq && envReq.trim()) {
    return { reqId: sanitizeReqId(envReq.trim()), source: "env" };
  }

  // 2. Git branch parse — tightened regex, with timeout to prevent hang.
  try {
    const branch = execSync("git branch --show-current", {
      cwd: REPO_ROOT,
      encoding: "utf8",
      stdio: ["ignore", "pipe", "ignore"],
      timeout: GIT_TIMEOUT_MS, // critical: prevents indefinite hang on index lock / GCM prompt
    }).trim();
    // Use [\w-]+ instead of .+ to reject branch names containing path separators.
    const match = branch.match(/opencode\/swarm-(REQ-[\w-]+)$/);
    if (match) {
      return { reqId: sanitizeReqId(match[1]), source: "git-branch" };
    }
  } catch {
    // git unavailable, timed out, not a repo, or index locked → fall through.
  }

  // 3. Fallback.
  return { reqId: "manual", source: "fallback" };
}

/**
 * Resolve the active run directory, creating the directory tree if needed.
 *
 * Idempotency strategy:
 *  - If OPF_RUN_DIR is set (automated swarm.py path): use it as-is, validate it
 *    stays inside REPO_ROOT.
 *  - Otherwise (manual TUI path): check CACHE_FILE for a recent session. If the
 *    cached path was written within SESSION_TTL_MS and the directory still exists,
 *    reuse it. Otherwise create a fresh timestamped directory and write the cache.
 *
 * @returns {{ runDir: string, reqId: string, mode: string, override: boolean }}
 */
function ensureRunDir() {
  // ── Automated path: OPF_RUN_DIR set by swarm.py ──────────────────────────
  const override = process.env.OPF_RUN_DIR;
  if (override && override.trim()) {
    // Resolve relative to REPO_ROOT (not cwd) and validate containment.
    const abs = path.resolve(REPO_ROOT, override.trim());
    if (!abs.startsWith(REPO_ROOT + path.sep) && abs !== REPO_ROOT) {
      throw new Error(
        `[run-dir] OPF_RUN_DIR escapes the repository root: ${abs}`
      );
    }
    fs.mkdirSync(path.join(abs, "workers"), { recursive: true });
    const { reqId, source } = detectReqId();
    return { runDir: abs, reqId, mode: source, override: true };
  }

  // ── Manual / TUI path: idempotent cache ──────────────────────────────────
  // Check for a still-valid cached run directory from this session.
  try {
    const stat = fs.statSync(CACHE_FILE);
    const ageMs = Date.now() - stat.mtimeMs;
    if (ageMs < SESSION_TTL_MS) {
      const cached = fs.readFileSync(CACHE_FILE, "utf8").trim();
      if (cached && fs.existsSync(cached)) {
        const { reqId, source } = detectReqId();
        return { runDir: cached, reqId, mode: source + "+cached", override: false };
      }
    }
  } catch {
    // Cache absent or unreadable → create fresh.
  }

  const { reqId, source } = detectReqId();
  const ts = utcTimestamp();
  const runDir = path.join(REPO_ROOT, "runs", reqId, ts);
  fs.mkdirSync(path.join(runDir, "workers"), { recursive: true });

  // Persist the resolved path for this session.
  try {
    fs.writeFileSync(CACHE_FILE, runDir, "utf8");
  } catch {
    // Best-effort: if the cache write fails, continue anyway.
  }

  return { runDir, reqId, mode: source, override: false };
}

// ── CLI Entry Point ────────────────────────────────────────────────────────────

function main() {
  // --reset: clear the session cache and exit.
  if (process.argv.includes("--reset")) {
    try { fs.unlinkSync(CACHE_FILE); } catch { /* already absent */ }
    process.stdout.write("[run-dir] Session cache cleared.\n");
    process.exit(0);
  }

  try {
    const result = ensureRunDir();
    const wantJson = process.argv.includes("--json");
    if (wantJson) {
      process.stdout.write(
        JSON.stringify({
          run_dir: toForwardSlash(result.runDir),
          req_id: result.reqId,
          mode: result.mode,
          override: result.override,
        }) + "\n"
      );
    } else {
      process.stdout.write(toForwardSlash(result.runDir) + "\n");
    }
    process.exit(0);
  } catch (err) {
    process.stderr.write(
      `[run-dir] ${err && err.message ? err.message : String(err)}\n`
    );
    process.exit(1);
  }
}

// ── Module Guard ──────────────────────────────────────────────────────────────
// Only execute main() when invoked directly as a script.
// If require()'d by another module, this block is skipped entirely — preventing
// process.exit() from killing the host process (e.g., OpenCode itself).
if (require.main === module) {
  main();
}

// Export core functions so other modules can use them without spawning a child process.
module.exports = { ensureRunDir, detectReqId, sanitizeReqId, utcTimestamp };
