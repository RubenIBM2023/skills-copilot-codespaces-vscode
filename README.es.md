# Plantilla de Proyecto con OpenCode — Factoría Unipersonal (OPF)

@autor: Dennys Mallqui (dennys.mallqui@ibm.com)

📖 [Read this document in English](README.md)

Esta es una plantilla de proyecto optimizada para trabajar con [OpenCode](https://opencode.ai), un agente de codificación asistido por IA, implementando el patrón de **Factoría Unipersonal (OPF)**: un sistema en el que un solo operador humano orquesta un enjambre de agentes de IA especializados para desarrollar software a escala — sobre **múltiples proyectos simultáneamente**.

---

## Inicio Rápido

### 1. Instalar OpenCode

```bash
# Con curl
curl -fsSL https://opencode.ai/install | bash

# Con npm
npm install -g opencode-ai

# Con Homebrew (macOS/Linux)
brew install anomalyco/tap/opencode

# Con Chocolatey (Windows)
choco install opencode
```

### 2. Configurar Acceso a la API (ICA)

Este proyecto se conecta a **IBM Consulting Advantage (ICA)** como proveedor de LLM. Copia el ejemplo de configuración y rellena tus credenciales:

```bash
# macOS / Linux
cp opencode.json.example opencode.json

# Windows (PowerShell)
Copy-Item opencode.json.example opencode.json
```

Abre `opencode.json` y reemplaza `YOUR_ICA_API_KEY` en `provider.ica.options.apiKey` con tu clave ICA real.

> **Seguridad**: `opencode.json` está en `.gitignore` — nunca lo confirmes en el repositorio.

### 3. Instalar Dependencias (tipos para el IDE — una sola vez)

```bash
# Desde la raíz del repo
npm install
```

Luego recarga el servidor TS en VS Code: `Ctrl+Shift+P` → **"TypeScript: Restart TS Server"**.

### 4. Iniciar la Sesión

```bash
opencode
# luego:
/init
```

---

## Estructura del Proyecto (Factoría Multi-Proyecto)

```
.
├── .opencode/
│   ├── agents/
│   │   ├── architect.md        # Orquestador primario OPF — lee specs/<REQ>/SPEC.md
│   │   ├── worker.md           # Worker aislado — opera en workspace/<id>/
│   │   ├── code-reviewer.md    # Revisor de código (solo lectura)
│   │   ├── security-auditor.md # Auditor de seguridad (solo lectura)
│   │   └── docs-writer.md      # Escritor de documentación
│   ├── plugins/
│   │   └── hitl-guard.ts       # Plugin HITL: cortafuegos + automatización Git/PR por proyecto
│   ├── commands/               # Comandos slash personalizados
│   └── tools/                  # Herramientas personalizadas
│
├── workspace/                  # Raíz multi-proyecto — una sub-carpeta por proyecto clonado
│   └── <project-id>/
│       ├── .opf/
│       │   └── project.json    # Metadata del proyecto: id, lint_cmd, test_cmd, typecheck_cmd
│       ├── src/                # Código fuente de la aplicación
│       ├── tests/              # Archivos de prueba
│       └── package.json        # Dependencias y scripts del proyecto
│
├── specs/                      # Contratos funcionales inmutables — una carpeta por requerimiento
│   └── REQ-YYYY-MM-DD-NNN-<slug>/
│       ├── SPEC.md             # La fuente única de verdad para ese requerimiento
│       └── target.json         # { "project": "<project-id>", "branch_prefix": "...", "title": "..." }
│
├── runs/                       # Bitácoras de ejecución del enjambre (solo-agregar)
│   └── <REQ_ID>/<UTC_TIMESTAMP>/
│       ├── manifest.json       # Metadata de la corrida (estado, duración, rama)
│       ├── events.jsonl        # Stream SSE completo — un objeto JSON por línea
│       ├── workers/<N>.log     # Log de actividad por worker en texto plano
│       └── summary.md          # Informe legible por humanos
│
├── infraestructura_ia/
│   ├── swarm.py                # Orquestador Python del enjambre (multi-proyecto, --req-id)
│   ├── registry.py             # Resolución REQ ↔ proyecto (rutas, metadata, dirs de corridas)
│   ├── observability.py        # RunLogger estructurado (JSONL + manifest + summary)
│   └── requirements.txt        # Dependencias Python (opencode-ai)
│
├── docs/
│   ├── SPEC.md.template        # Plantilla para redactar nuevos SPECs
│   └── guidelines.md           # Estándares de desarrollo (cargado automáticamente)
│
├── AGENTS.md                   # Contexto de agentes (cargado automáticamente)
├── opencode.json               # Config en vivo (git-ignorado, contiene claves)
└── opencode.json.example       # Plantilla segura con claves de marcador de posición
```

---

## Arquitectura OPF: Factoría Unipersonal

### El Concepto

La Factoría Unipersonal (OPF) es un patrón de desarrollo en el que **un único operador humano** escribe solo la **especificación funcional** y delega toda la implementación a un enjambre de agentes de IA. La factoría atiende **múltiples proyectos** simultáneamente mediante el layout multi-proyecto de `workspace/`.

```
  Operador humano
       │
       ▼  escribe
  specs/REQ-YYYY-MM-DD-NNN-<slug>/SPEC.md   ← contrato inmutable
  specs/REQ-.../target.json                  ← apunta a workspace/<project-id>/
       │
       ▼  lee
  [Agente: architect]  — descompone el SPEC en tareas jurisdiccionales
       │
  ┌────┴────┬─────────┐
  ▼         ▼         ▼
[Worker A] [Worker B] [Worker C]   ← aislados por ruta de jurisdicción
workspace/  workspace/  workspace/  (sin comunicación entre agentes)
<id>/src/   <id>/api/   <id>/tests/
       │
       ▼  persiste
  runs/<REQ_ID>/<UTC_TIMESTAMP>/
  ├── events.jsonl   ← stream SSE completo
  ├── manifest.json  ← metadata de la corrida
  ├── workers/*.log  ← logs por worker
  └── summary.md     ← informe legible
```

### Principios Clave

| Principio | Descripción |
|---|---|
| **Spec-Driven Development (SDD)** | `specs/<REQ>/SPEC.md` es el único artefacto que el humano redacta. El código es efímero; la especificación es el contrato. |
| **Factoría Multi-Proyecto** | Cada proyecto vive en `workspace/<project-id>/` con su propio `.opf/project.json`. Una corrida del enjambre apunta a exactamente un proyecto via `target.json`. |
| **Partición Jurisdiccional Estricta** | Cada worker posee una ruta exclusiva relativa al repositorio. Ningún worker puede escribir fuera de su jurisdicción, evitando colisiones en paralelo. |
| **Comunicación Hub-and-Spoke** | Los workers no se comunican entre sí. Toda comunicación es vertical: orquestador → worker. Cero peer-to-peer. |
| **HITL (Human-in-the-Loop)** | `hitl-guard.ts` bloquea comandos destructivos, protege `specs/**` y `runs/**`, ejecuta quality gates desde la raíz del proyecto, y auto-confirma + abre un Draft PR. |
| **Observabilidad Estructurada** | Cada corrida del enjambre escribe `events.jsonl`, `manifest.json`, logs por worker y `summary.md` bajo `runs/<REQ_ID>/<UTC_TIMESTAMP>/`. Cualquier corrida pasada es completamente reconstruible. |

---

## Formato del Identificador REQ

Cada requerimiento tiene un identificador único y ordenado cronológicamente:

```
REQ-YYYY-MM-DD-NNN-<slug>
    └── fecha ──┘ └┘ └──┘
                 │   └─ slug en kebab-case describiendo el cambio
                 └─ contador de 3 dígitos auto-incrementado por día (001, 002, ...)
```

**Ejemplos:**
- `REQ-2026-05-31-001-teradata-to-aws` — primer requerimiento creado el 31 de mayo de 2026 (el ejercicio de referencia incluido en esta plantilla)
- `REQ-2026-06-01-001-user-auth` — primer requerimiento del 1 de junio de 2026
- `REQ-2026-06-01-002-rate-limit` — segundo requerimiento del mismo día

Genera el próximo ID automáticamente:
```bash
python -c "from infraestructura_ia.registry import new_req_id; print(new_req_id('mi-feature'))"
```

---

## Flujo de Trabajo OPF

### Modo 1: Flujo Manual con el TUI de OpenCode

1. **Crea `specs/REQ-.../SPEC.md`** y `target.json` para el requerimiento.
2. **Inicia Build** (`Tab`) y selecciona el agente `@architect`, pasando el REQ_ID.
3. El orquestador lee el SPEC, identifica el proyecto objetivo, diseña el plan y delega tareas a workers vía la herramienta `task`.
4. Los workers ejecutan en paralelo dentro de sus jurisdicciones asignadas bajo `workspace/<project-id>/`.
5. El plugin HITL ejecuta quality gates desde la raíz del proyecto, confirma y abre un Draft PR.

### Modo 2: Enjambre Automático con Python

```bash
# Terminal 1 — inicia el servidor OpenCode
opencode serve

# Terminal 2 — solo la primera vez: crea y activa el venv
cd infraestructura_ia && python -m venv .venv
.\.venv\Scripts\Activate.ps1        # Windows (PowerShell)
source .venv/bin/activate           # macOS / Linux
pip install -r requirements.txt

# Terminal 2 — cada corrida: activa el venv, regresa a la raíz y lanza
cd ..
python infraestructura_ia/swarm.py --req-id REQ-2026-05-31-001-teradata-to-aws
```

El orquestador Python (`swarm.py`):
1. Resuelve `specs/<REQ_ID>/SPEC.md` y `target.json` vía `registry.py`
2. Crea una rama Git limpia con el nombre `<branch_prefix>-<REQ_ID>`
3. Inicializa un `RunLogger` — todos los eventos se persisten en `runs/<REQ_ID>/<TS>/`
4. Envía el SPEC al agente `architect` para descomposición en tareas (plan JSON)
5. Despacha workers en paralelo con `asyncio.gather()`, inyectando `PROJECT_ROOT` y comandos de verificación
6. Captura el stream SSE completo en `events.jsonl`
7. Cierra el logger con `summary.md` y activa el plugin HITL para automatización Git/PR

Salida esperada:
```
[swarm] Requirement: REQ-2026-05-31-001-teradata-to-aws
[swarm] Project:     legacy-teradata-migration (Legacy Teradata DW → AWS Migration)
[swarm] Run directory: runs/REQ-2026-05-31-001-teradata-to-aws/2026-05-31T14-22-08Z
[14:22:08] run.start req_id=REQ-2026-05-31-001-teradata-to-aws project_id=legacy-teradata-migration
[14:22:10] orchestrator.plan.received worker_count=3
[14:22:11] worker.dispatch worker_idx=1 jurisdiction=workspace/legacy-teradata-migration/target/redshift/ddl/
[14:22:11] worker.dispatch worker_idx=2 jurisdiction=workspace/legacy-teradata-migration/target/glue/jobs/
[14:22:11] worker.dispatch worker_idx=3 jurisdiction=workspace/legacy-teradata-migration/target/docs/
[swarm] Done. Run summary: runs/REQ-2026-05-31-001-teradata-to-aws/2026-05-31T14-22-08Z/summary.md
```

### Modo 3: Agregar un Nuevo Proyecto a la Factoría

```bash
# 1. Clona o crea el proyecto bajo workspace/
git clone <url-del-repo> workspace/<project-id>

# 2. Crea la metadata del proyecto
mkdir -p workspace/<project-id>/.opf
# crea workspace/<project-id>/.opf/project.json con los comandos de verificación
# de tu stack. Dos ejemplos reales:

# Proyecto TypeScript / Node:
{
  "id": "<project-id>",
  "name": "Nombre legible del proyecto",
  "language": "typescript",
  "lint_cmd": "npm run lint",
  "test_cmd": "npm test",
  "typecheck_cmd": "npx tsc --noEmit"
}

# Proyecto Python / SQL (como el ejemplo de referencia que trae este repo):
{
  "id": "<project-id>",
  "name": "Nombre legible del proyecto",
  "language": "python+sql",
  "lint_cmd": "ruff check src tests",
  "test_cmd": "pytest tests -q",
  "typecheck_cmd": "ruff check --select=E,F src tests"
}
```

### Modo 4: Crear un Nuevo Requerimiento

```bash
# 1. Genera un REQ_ID único
python -c "from infraestructura_ia.registry import new_req_id; print(new_req_id('mi-feature'))"
# → REQ-2026-06-01-001-mi-feature

# 2. Crea la carpeta del spec
mkdir specs/REQ-2026-06-01-001-mi-feature
cp docs/SPEC.md.template specs/REQ-2026-06-01-001-mi-feature/SPEC.md

# 3. Crea target.json
# { "project": "<project-id>", "branch_prefix": "opencode/swarm", "title": "..." }

# 4. Edita SPEC.md y despacha el enjambre
python infraestructura_ia/swarm.py --req-id REQ-2026-06-01-001-mi-feature
```

---

## Ejercicio de Referencia

Esta plantilla incluye **un** requerimiento listo para ejecutar que ejercita el ciclo completo de OPF sobre un caso real de Data Engineering. Úsalo como la referencia canónica al redactar nuevos SPECs.

### REQ-2026-05-31-001-teradata-to-aws — Migración de DW Teradata legacy → AWS

**Proyecto destino:** `workspace/legacy-teradata-migration/`

Este requerimiento migra una porción autocontenida de un **data warehouse Teradata** legacy on-premise (una dimensión + un fact + una carga diaria BTEQ) hacia **AWS** (DDL de Amazon Redshift + job AWS Glue PySpark + documentación de migración).

Demuestra el patrón OPF completo de extremo a extremo sobre un escenario realista de Data Engineering:

- Ciclo **spec → architect → workers → quality gate → Draft PR** sobre una migración no trivial.
- **3 workers en paralelo**, cada uno dueño de una jurisdicción exclusiva dentro de `target/` (DDL Redshift, job Glue PySpark, docs de migración + test prohibitivo).
- **Las MCP tools de ICA se usan como asesores expertos durante la corrida** — no se embeben como código cliente:
  - El `architect` consulta al **ICA Digital Worker** *Data Engineer – Data Platforms* (`ica_ica_chat_digital_workforce`, model id `4fffad08-c4b8-4c24-9167-4e300fb5df5e`) para validar las elecciones de DISTKEY/SORTKEY y el reemplazo de partición, y captura la respuesta en `target/docs/digital_worker_review.md`.
  - Los workers pueden consultar la **colección de documentación de IBM Consulting Advantage** (`ica_ica_chat_models` con `files: [{ type: "collection", id: "6305e9e6-..." }]`) cuando duden sobre idiomas de Redshift.
- **Un test prohibitivo** (`tests/test_no_ica_client_code.py`) garantiza que los entregables bajo `target/**` no contienen **ninguna** referencia a endpoints ICA, nombres de tools MCP, model ids ni collection ids (la única excepción es `target/docs/digital_worker_review.md`, que es el volcado verbatim del Digital Worker).
- **Verificación** (desde la raíz del proyecto):
  - `ruff check target tests` — cero errores
  - `pytest tests -q` — cero fallos
  - `ruff check --select=E,F target tests` — cero errores

```bash
python infraestructura_ia/swarm.py --req-id REQ-2026-05-31-001-teradata-to-aws
```

> **¿Por qué este diseño?** Las MCP tools que conectan OpenCode con ICA están configuradas una sola vez en `opencode.json` y son consumidas por los agentes durante la corrida del enjambre. El ejercicio de referencia ilustra el patrón correcto: **los agentes llaman a ICA**, **los entregables no**. No redactes SPECs que pidan a los workers embeber llamadas a ICA dentro de los artefactos producidos.

### Descubrir ICA Digital Workers para un nuevo SPEC

Cuando redactes un nuevo SPEC que necesite un revisor experto, primero encuentra el Digital Worker adecuado:

```text
# Desde una sesión TUI de OpenCode (el MCP de ICA debe estar configurado en opencode.json):
@explore Usa la herramienta MCP ica_ica_list_digital_workforce y lista cada entrada cuya
         descripción mencione <tu-dominio>. Para cada coincidencia, reporta id, name y
         un resumen de una línea. Recomienda el mejor candidato individual.
```

Luego referencia el Digital Worker elegido por **id** dentro de la sección *"How agents must use the ICA MCP tools"* de tu SPEC (consulta `specs/REQ-2026-05-31-001-teradata-to-aws/SPEC.md` como plantilla).

---

## Observabilidad y Trazabilidad

Cada corrida del enjambre produce una bitácora de auditoría estructurada bajo `runs/<REQ_ID>/<UTC_TIMESTAMP>/`.

### Contenidos del Directorio de Corrida

| Archivo | Descripción |
|---|---|
| `manifest.json` | Metadata legible por máquina: status, started, ended, duration_s, branch, workers |
| `events.jsonl` | Cada evento SSE capturado, un objeto JSON por línea, timestamps en milisegundos |
| `architect.decisions.md` | Interpretación del SPEC, tabla de partición jurisdiccional y plan de rollback (escrito por `@architect`) |
| `workers/<N>.log` | Log de actividad en texto plano para cada agente worker (best-effort, capturado por el plugin) |
| `workers/<N>.decisions.md` | Bloque `DECISIONS:` autoritativo escrito por el worker `<N>` (sobrevive a flujos manuales) |
| `summary.md` | Informe legible: fechas, estado, estadísticas, archivos modificados |

> **Ambos flujos producen los mismos artefactos.** Tanto el orquestador automatizado `swarm.py` como el flujo manual TUI vía `@architect` invocan `node .opencode/tools/run-dir.js --json` para resolver un directorio de corrida compartido y escribir `architect.decisions.md` + `workers/<N>.decisions.md` en él. Consulta `AGENTS.md` → "Observability & Decision Traces" para el checklist completo de auditoría.

### Tipos de Eventos en `events.jsonl`

```jsonl
{"ts":"2026-05-31T14:22:08.123Z","type":"run.start","req_id":"REQ-...","project_id":"legacy-teradata-migration"}
{"ts":"2026-05-31T14:22:09.456Z","type":"orchestrator.session.created","session_id":"abc123"}
{"ts":"2026-05-31T14:22:10.789Z","type":"orchestrator.plan.received","worker_count":1}
{"ts":"2026-05-31T14:22:11.012Z","type":"worker.dispatch","worker_idx":1,"jurisdiction":"workspace/..."}
{"ts":"2026-05-31T14:22:14.345Z","type":"sse.tool.execute.before","tool":"edit","path":"workspace/..."}
{"ts":"2026-05-31T14:22:22.678Z","type":"run.end","status":"success","duration_s":14.5}
```

### Ejemplo de `summary.md`

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

## Agentes Disponibles

| Agente | Modo | Modelo | Descripción |
|--------|------|--------|-------------|
| `architect` | primary | claude-sonnet-4-6 | Orquestador OPF: ingiere `specs/<REQ>/SPEC.md`, particiona tareas por ruta de proyecto, delega a workers |
| `worker` | subagent (oculto) | claude-haiku-4-5 | Ejecutor aislado: transforma código solo en `workspace/<project-id>/` |
| `code-reviewer` | subagent | (hereda) | Revisión de calidad y buenas prácticas (solo lectura) |
| `security-auditor` | subagent | (hereda) | Auditoría de vulnerabilidades OWASP (solo lectura) |
| `docs-writer` | subagent | (hereda) | Creación y actualización de documentación |

### Usar los Agentes

```
# Invocar el orquestador manualmente
@architect Analiza REQ-2026-05-31-001-teradata-to-aws y planifica la implementación

# Revisión de código
@code-reviewer Revisa los cambios en workspace/legacy-teradata-migration/target/redshift/ddl/

# Auditoría de seguridad
@security-auditor Audita el job Glue PySpark en workspace/legacy-teradata-migration/target/glue/jobs/

# Cambiar entre agentes primarios
Tab  →  alternar entre Build y Plan
```

### Comandos Útiles

| Comando / Tecla | Acción |
|---|---|
| `/init` | Analizar la estructura del proyecto (ejecutar en la primera sesión) |
| `/compact` | Resumir conversación para preservar contexto |
| `/undo` | Revertir el último lote de cambios |
| `Ctrl+P` | Listar todas las acciones disponibles |
| `Tab` | Cambiar entre los agentes Build y Plan |
| `@` | Mencionar un subagente o referenciar un archivo |

---

## `infraestructura_ia/` — Herramientas Python

### `registry.py` — Resolución de REQ y Proyectos

```python
from infraestructura_ia.registry import (
    new_req_id,           # Genera el próximo REQ_ID para hoy (auto-incrementa NNN)
    list_requirements,    # Lista todos los REQ_IDs en specs/, ordenados
    resolve_requirement,  # Carga SPEC.md + target.json → RequirementMeta
    resolve_project,      # Carga .opf/project.json → ProjectMeta
    new_run_dir,          # Crea runs/<REQ>/<TS>/workers/ → Path
)
```

### `observability.py` — Logger Estructurado de Corridas

```python
from infraestructura_ia.observability import RunLogger

logger = RunLogger(run_dir, req_id, project_id, branch)
logger.event("worker.dispatch", worker_idx=1, jurisdiction="workspace/...")
logger.worker(1, "transformación aplicada correctamente")
logger.close("success", workers=1, files_changed=3)
# Produce: events.jsonl, manifest.json, workers/1.log, summary.md
```

### `swarm.py` — Orquestador Automatizado

```bash
# Ver ayuda
python infraestructura_ia/swarm.py --help

# Ejecutar contra un requerimiento específico
python infraestructura_ia/swarm.py --req-id REQ-2026-05-31-001-teradata-to-aws
```

---

## Configuración de ICA y el Servidor MCP

### Modelos Disponibles

| ID del Modelo | Descripción | Uso OPF | Prefill vía ICA |
|---|---|---|---|
| `claude-haiku-4-5` | Rápido, ligero **(predeterminado)** | Agentes `worker`, despacho de enjambre | ✅ Confiable |
| `claude-sonnet-4-6` | Equilibrado | `architect`, chat manual | ⚠️ Intermitente |
| `claude-opus-4-7` | Más capaz | Razonamiento complejo | ✅ Confiable |
| `meta-llama/llama-4-maverick-17b-128e-instruct-fp8` | LLaMA 4 Maverick | Solo conversacional | ❌ No |
| `ibm/granite-4-h-small` | IBM Granite pequeño | Solo conversacional | ❌ No |
| `gemma-4-26b-a4b-it` | Google Gemma 4 | Solo conversacional | ❌ No |

> **Prefill vía ICA**: Usa modelos ✅ para cualquier flujo que involucre la herramienta `task`, la descomposición de `@architect` o `swarm.py`. LLaMA/Granite/Gemma rechazan el patrón de prefill que OpenCode requiere para salida estructurada.

### Instalación del Servidor MCP ICA

```bash
git clone https://github.ibm.com/dennys-mallqui/mcp-ica-2.0-server.git
cd mcp-ica-2.0-server && npm install && npm run build
```

Configurar en `opencode.json`:
```jsonc
"mcp": {
  "ica": {
    "type": "local",
    "command": ["node", "C:/ruta/a/mcp-ica-2.0-server/dist/server.js"],
    "environment": { "ICA_API_KEY": "<TU_CLAVE_MCP_ICA>" }
  }
}
```

### Instalación del Servidor MCP ICA Legacy

```bash
# Clone and build
git clone https://github.ibm.com/dennys-mallqui/mcp-ica-legacy-server.git
cd mcp-ica-legacy-server && npm install && npm run build
```

Configurar en `opencode.json`:
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

> **Dos claves independientes**: `provider.ica.options.apiKey` (completaciones de chat) y `mcp.ica.environment.ICA_API_KEY` (herramientas MCP) son credenciales separadas.

Para crear APIKEY en ICA 2.0 e ICA Legacy: https://pages.github.ibm.com/guild-of-coding-agents-at-consulting/documentation/docs/coding-agents/cline/getting-started

---

## Comandos del Desarrollador

| Alcance | Comando | Descripción |
|---|---|---|
| **Raíz del repo** | `npm run typecheck` | Verifica tipos del plugin `hitl-guard.ts` |
| **Raíz del proyecto** | `npm run lint` | Lint y auto-fix (ejecutar desde `workspace/<id>/`) |
| **Raíz del proyecto** | `npm test` | Ejecutar suite de pruebas |
| **Raíz del proyecto** | `npx tsc --noEmit` | Verificar tipos sin compilar |
| **Raíz del repo** | `python infraestructura_ia/swarm.py --help` | Ver opciones del CLI del enjambre |

> **Importante**: Los comandos de quality gate (`lint`, `test`, `typecheck`) se ejecutan desde `workspace/<project-id>/`, no desde la raíz del repo. El plugin HITL y los workers del enjambre imponen esto automáticamente.

---

## Probar la Plantilla Sin Contaminarla

Usa un Git worktree para validar la factoría en un sandbox desechable:

```bash
# Crear sandbox (comparte historial git, árbol de trabajo aislado)
git worktree add ../opf-test-sandbox HEAD
cd ../opf-test-sandbox

# Desconectar el remote para evitar Draft PRs en el repo plantilla
git remote remove origin

# Crear una rama sandbox
git checkout -b sandbox/template-validation

# Configurar credenciales
Copy-Item opencode.json.example opencode.json   # Windows
cp opencode.json.example opencode.json           # macOS/Linux

# Instalar devDependencies raíz (tipos del IDE)
npm install

# Validar Modo 1 — TUI
opencode

# Validar Modo 2 — Enjambre automatizado
# Terminal 1:
opencode serve
# Terminal 2:
cd infraestructura_ia && python -m venv .venv
.\.venv\Scripts\Activate.ps1 && pip install -r requirements.txt
cd ..
python infraestructura_ia/swarm.py --req-id REQ-2026-05-31-001-teradata-to-aws

# Destruir sandbox (desde la raíz del repo original)
cd C:\ruta\a\agentic-sdlc-sdd-architecture
git worktree remove ../opf-test-sandbox --force
```

---

## Solución de Problemas

### "This model does not support assistant message prefill"

**Causa:** OpenCode inyecta un mensaje `role:"assistant"` al final del payload para salida estructurada. ICA solo acepta esto para modelos de la familia Anthropic.

**Solución:** Usa `ica/claude-haiku-4-5` o `ica/claude-opus-4-7` para cualquier flujo con la herramienta `task` o `swarm.py`. Reserva LLaMA/Granite/Gemma solo para chat conversacional de un turno.

### El quality gate pasa pero no verificó nada

**Causa:** `workspace/<project-id>/.opf/project.json` (o el proyecto subyacente) declara comandos `echo` de marcador en vez de linters/runners reales.

**Solución:** Reemplaza con comandos reales en `.opf/project.json`. Dos ejemplos del mundo real:

```jsonc
// TypeScript / Node
{
  "lint_cmd": "npm run lint",
  "test_cmd": "npm test",
  "typecheck_cmd": "npx tsc --noEmit"
}

// Python / SQL (igual que el ejemplo de referencia)
{
  "lint_cmd": "ruff check target tests",
  "test_cmd": "pytest tests -q",
  "typecheck_cmd": "ruff check --select=E,F target tests"
}
```

### `swarm.py` falla con `FileNotFoundError: SPEC.md not found`

**Causa:** El valor de `--req-id` no coincide con ninguna carpeta bajo `specs/`.

**Solución:** Ejecuta `python -c "from infraestructura_ia.registry import list_requirements; print(list_requirements())"` para ver los REQ_IDs válidos.

### Otros problemas comunes

| Problema | Solución |
|---|---|
| OpenCode no inicia | `opencode --version`; reinstala: `npm install -g opencode-ai` |
| API 401/403 desde ICA | Verifica `apiKey` (chat) y `ICA_API_KEY` (MCP) por separado |
| Servidor MCP falla | Verifica la ruta absoluta a `dist/server.js`; re-ejecuta `npm run build` |
| Error de ruta en Windows | Usa barras diagonales en JSON: `C:/Users/...` |
| Conflicto de dependencias Python | Ejecuta siempre desde el `.venv` activado; `pip uninstall opencode-ai` si está instalado globalmente |
| Quality gate ejecuta desde la carpeta incorrecta | Define `OPF_PROJECT_PATH=workspace/<project-id>` como variable de entorno, o asegúrate de que solo haya un proyecto en `workspace/` |

---

## Recursos

- [Documentación Oficial de OpenCode](https://opencode.ai/docs)
- [Repositorio de OpenCode](https://github.com/anomalyco/opencode)
- [Reportar Problemas](https://github.com/anomalyco/opencode/issues)
- [Discord de OpenCode](https://opencode.ai/discord)
- [Servidor MCP ICA](https://github.ibm.com/dennys-mallqui/mcp-ica-2.0-server)

---

**Última actualización**: 31 mayo 2026
