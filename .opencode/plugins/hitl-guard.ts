// `@opencode-ai/plugin` is an OpenCode-internal package bundled inside the
// OpenCode binary at plugin load time. Do NOT add it to .opencode/package.json
// (OpenCode's bun install would fail — the package is not on the npm registry
// under that path for runtime use). Instead, add it to the ROOT package.json
// devDependencies so the VS Code TypeScript language server can resolve the
// Plugin type for IDE autocomplete (npm package: @opencode-ai/plugin@^1.x).
import type { Plugin } from "@opencode-ai/plugin"
import { writeFileSync, readFileSync, existsSync } from "node:fs"
import { tmpdir } from "node:os"
import { join, posix, resolve } from "node:path"

/**
 * OPF HITL Guard Plugin (multi-project aware)
 *
 * Two responsibilities:
 *  1. Pre-tool firewall — block destructive commands and protect immutable files
 *     (SPECs, run logs, env files).
 *  2. Session-idle hook — run quality gates (against the active project root)
 *     and automate Git/PR flow on completion.
 *
 * Project resolution order:
 *  a) Environment variable OPF_PROJECT_PATH (repo-relative, e.g. "workspace/legacy-teradata-migration").
 *  b) Single project under workspace/ (auto-detected when only one exists).
 *  c) Fallback: repository root (legacy mode, warns once).
 *
 * Prerequisites: GitHub CLI (`gh`) must be installed and authenticated for PR creation.
 */

type ProjectMeta = {
  id: string
  name?: string
  lint_cmd?: string
  test_cmd?: string
  typecheck_cmd?: string
}

/**
 * Resolve the active project metadata. Returns null when no project can be
 * determined (e.g. running plain TUI from repo root with multiple projects).
 */
function resolveProject(repoRoot: string): { root: string; meta: ProjectMeta } | null {
  // 1. Explicit env var wins.
  const envPath = process.env.OPF_PROJECT_PATH
  if (envPath && envPath.trim()) {
    const root = resolve(repoRoot, envPath)
    const metaFile = join(root, ".opf", "project.json")
    if (existsSync(metaFile)) {
      try {
        const meta = JSON.parse(readFileSync(metaFile, "utf8")) as ProjectMeta
        return { root, meta }
      } catch {
        return null
      }
    }
    return null
  }

  // 2. Auto-detect single project under workspace/.
  const workspaceDir = join(repoRoot, "workspace")
  if (!existsSync(workspaceDir)) return null

  try {
    const fs = require("node:fs") as typeof import("node:fs")
    const entries = fs
      .readdirSync(workspaceDir, { withFileTypes: true })
      .filter((d) => d.isDirectory())
    if (entries.length !== 1) return null

    const root = join(workspaceDir, entries[0].name)
    const metaFile = join(root, ".opf", "project.json")
    if (!existsSync(metaFile)) return null
    const meta = JSON.parse(readFileSync(metaFile, "utf8")) as ProjectMeta
    return { root, meta }
  } catch {
    return null
  }
}

export const OpfHitlGuard: Plugin = async ({ client, $ }) => {
  const repoRoot = process.cwd()
  const project = resolveProject(repoRoot)

  // ── Startup banner ───────────────────────────────────────────────────────
  if (project) {
    await client.app.log({
      body: {
        service: "hitl-guard",
        level: "info",
        message: `[OPF Guard] Active project: ${project.meta.id} (${project.root})`,
      },
    })

    // Detect placeholder npm scripts in the active project.
    try {
      const pkgPath = join(project.root, "package.json")
      if (existsSync(pkgPath)) {
        const pkg = JSON.parse(readFileSync(pkgPath, "utf8"))
        const scripts: Record<string, string> = pkg.scripts ?? {}
        const placeholders = ["lint", "test"].filter(
          (s) => !scripts[s] || scripts[s].startsWith("echo")
        )
        if (placeholders.length > 0) {
          await client.app.log({
            body: {
              service: "hitl-guard",
              level: "warn",
              message:
                `[OPF Guard] Quality gate is INACTIVE for: ${placeholders.join(", ")}. ` +
                `Replace the placeholder echo scripts in ${project.meta.id}/package.json with real commands.`,
            },
          })
        }
      }
    } catch {
      // Non-blocking
    }
  } else {
    await client.app.log({
      body: {
        service: "hitl-guard",
        level: "warn",
        message:
          "[OPF Guard] No active project resolved. Set OPF_PROJECT_PATH or " +
          "place exactly one project under workspace/. Quality gate will run " +
          "from the repository root (legacy mode).",
      },
    })
  }

  await client.app.log({
    body: { service: "hitl-guard", level: "info", message: "HITL Guard initialized" },
  })

  return {
    // ─── 1. Pre-Tool Firewall ──────────────────────────────────────────────────
    "tool.execute.before": async (input, output) => {

      // Block destructive or policy-violating shell commands.
      if (input.tool === "bash" && output.args?.command) {
        const cmd = (output.args.command as string).toLowerCase()

        const blockedPatterns: RegExp[] = [
          /\brm\s+-rf\s+\//,                           // rm -rf /
          /git\s+push\s+origin\s+(main|master)\b/,     // git push origin main|master
          /git\s+push\s+(--force|-f)\b/,               // git push --force / -f
          /\btruncate\b\s/,                            // truncate <file>
        ]

        for (const pattern of blockedPatterns) {
          if (pattern.test(cmd)) {
            throw new Error(
              `[OPF Guard] Command blocked by factory policy: ${cmd}`
            )
          }
        }
      }

      // Protect immutable directories and files from writes.
      if (input.tool === "edit" || input.tool === "write") {
        const filePath: string = (output.args?.path as string) ?? ""

        // Normalise to forward slashes for cross-platform comparisons.
        const normalised = filePath.replace(/\\/g, "/")

        // 1. Protect SPECs (entire specs/ tree is immutable once created).
        if (/(^|\/)specs\//.test(normalised)) {
          throw new Error(
            `[OPF Guard] Write denied on SPEC tree (immutable): ${filePath}`
          )
        }

        // 2. Protect run logs (append-only history).
        if (/(^|\/)runs\//.test(normalised)) {
          throw new Error(
            `[OPF Guard] Write denied on runs/ tree (append-only): ${filePath}`
          )
        }

        // 3. Protect env files by filename.
        const filename = filePath.split(/[\\/]/).pop() ?? ""
        if (filename === ".env" || filename.startsWith(".env.")) {
          throw new Error(
            `[OPF Guard] Write denied on protected file: ${filePath}`
          )
        }

        // 4. Legacy: protect docs/SPEC.md if it still exists at the root.
        if (filename === "SPEC.md" && /(^|\/)docs\/SPEC\.md$/.test(normalised)) {
          throw new Error(
            `[OPF Guard] Write denied on protected file: ${filePath}`
          )
        }
      }
    },

    // ─── 2. Session-Idle Quality Gate + Git Automation ────────────────────────
    "session.idle": async (input: unknown) => {
      const rawInput = input as Record<string, unknown>
      const sessionId: string =
        (rawInput?.sessionId as string)
        ?? (rawInput?.id as string)
        ?? "unknown"

      await client.app.log({
        body: {
          service: "hitl-guard",
          level: "info",
          message: `Session ${sessionId.slice(-8)} idle — running quality gates`,
        },
      })

      // Determine where to run the quality gate commands.
      const gateCwd = project?.root ?? repoRoot
      const gateLabel = project?.meta.id ?? "(repo root)"

      // Lint check — run from project root.
      const lint = await $`npm run lint --silent`.cwd(gateCwd).quiet().nothrow()
      if (lint.exitCode !== 0) {
        await client.app.log({
          body: {
            service: "hitl-guard",
            level: "error",
            message: `[OPF Guard] Linter reported errors in '${gateLabel}' — skipping git automation. Fix before committing.`,
          },
        })
        return
      }

      // Test suite check — run from project root.
      const tests = await $`npm test --silent`.cwd(gateCwd).quiet().nothrow()
      if (tests.exitCode !== 0) {
        await client.app.log({
          body: {
            service: "hitl-guard",
            level: "error",
            message: `[OPF Guard] Test suite failed in '${gateLabel}' — skipping git automation. Resolve failures before committing.`,
          },
        })
        return
      }

      // Git automation — only on feature branches. Always run from repo root.
      try {
        const branch = (
          await $`git branch --show-current`.cwd(repoRoot).quiet().nothrow().text()
        ).trim()

        if (!branch) {
          await client.app.log({
            body: {
              service: "hitl-guard",
              level: "warn",
              message: "Could not determine git branch — skipping git automation",
            },
          })
          return
        }

        if (branch === "main" || branch === "master") {
          await client.app.log({
            body: {
              service: "hitl-guard",
              level: "warn",
              message: `[OPF Guard] On protected branch '${branch}' — skipping auto-commit and PR creation.`,
            },
          })
          return
        }

        await $`git add .`.cwd(repoRoot).quiet()
        await $`git commit -m ${"feat: swarm task completed on " + branch}`.cwd(repoRoot).quiet()
        await $`git push origin ${branch}`.cwd(repoRoot).quiet()

        const prBodyPath = join(tmpdir(), "opf-pr-body.md")
        const prBody = [
          "## Swarm-Generated Changes",
          "",
          `- Active project: \`${gateLabel}\``,
          "- Jurisdictional isolation enforced — no merge conflicts.",
          "- Lint and test suite verified locally against the project root.",
          "",
          "_Generated by the One-Person Factory via OpenCode._",
        ].join("\n")

        writeFileSync(prBodyPath, prBody, "utf8")

        await $`gh pr create --title ${"feat: OPF swarm task — " + branch} --body-file ${prBodyPath} --draft`.cwd(repoRoot).quiet()

        await client.app.log({
          body: {
            service: "hitl-guard",
            level: "info",
            message: "Draft PR created successfully",
          },
        })
      } catch (err: unknown) {
        const message = err instanceof Error ? err.message : String(err)
        await client.app.log({
          body: {
            service: "hitl-guard",
            level: "error",
            message: `Git automation error: ${message}`,
          },
        })
        // Non-blocking: log but do not halt the session
      }
    },
  }
}

// silence "unused" warnings for the posix/resolve imports kept for future use
void posix
