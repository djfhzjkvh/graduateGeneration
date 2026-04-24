# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

**客户购房意向跟进提醒系统** — A real estate CRM for managing customer leads, follow-ups, heat scoring, and AI-assisted sales script generation.

Triple-tier architecture:

- **`server/`** — Spring Boot 2.7.18 backend (Java 1.8, Maven)
- **Mini App** — WeChat Mini Program (uni-app, planned)
- **Admin Web** — Vue3 + Element Plus (planned; prototype at `docs/admin-prototype.html`)

## Backend: Build & Run

```powershell
# Start development server (from server/ directory)
$env:DB_USERNAME="root"
$env:DB_PASSWORD="your_password"
$env:QWEN_API_KEY="your_api_key"
mvn spring-boot:run

# Package
mvn clean package
```

- Swagger UI: `http://localhost:8080/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

**Prerequisites:** Java 1.8, Maven 3.9+, MySQL 8.0 with database `graduate_generation` initialized via `sql/schema.sql` + `sql/seed.sql`.

**Demo accounts** (after seeding): `admin / 123456`, `manager_hx / 123456`, `advisor_a / 123456` (and b, c).

## Architecture

### Request Flow

```
Controller → Service → Mapper (MyBatis-Plus + XML) → MySQL
```

- **Unified response:** All endpoints return `Result<T>` (code / message / data). Paginated results use `PageResult<T>`.
- **Exception handling:** `BusinessException` is thrown for domain errors; `GlobalExceptionHandler` converts it to `Result`.
- **Input/output separation:** DTOs for request bodies (`@Valid` required), VOs for response payloads.

### Module Layout (`src/main/java/com/graduation/crm/modules/`)

| Module      | Responsibility                                                                   |
| ----------- | -------------------------------------------------------------------------------- |
| `auth`      | JWT-based login for both app (`/api/app/auth/*`) and admin (`/api/admin/auth/*`) |
| `customer`  | Customer CRUD, tags (`CustomerTag`/`CustomerTagRel`), tag binding                |
| `follow`    | Follow-up records linked to customers                                            |
| `task`      | Task creation, completion, reminder scheduling                                   |
| `heat`      | Customer engagement heat score calculation                                       |
| `dashboard` | Aggregated KPIs — workbench/advisor view (app) and overview (admin)              |
| `ai`        | Qwen LLM integration: chat, script generation, lead extraction, call log         |
| `message`   | In-app notifications and push records                                            |
| `project`   | Real estate projects, house types, competitor projects                           |
| `system`    | Users (`SysUser`), roles (`SysRole`), departments (`SysDept`)                    |

Shared utilities live in `common/`: `Result`, `PageResult`, `BusinessException`, `GlobalExceptionHandler`, MyBatis-Plus config, OpenAPI config.

### API Naming Convention

- Mobile/app endpoints: `/api/app/<module>/...`
- Admin backend endpoints: `/api/admin/<module>/...`

### AI Integration

The `ai` module calls the Qwen API (Alibaba DashScope) via `RestTemplate`. Key config in `application.yml`:

```yaml
ai:
  model-name: qwen3.5-omni-plus
  base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
  api-key: ${QWEN_API_KEY}   # ← set via env var, never hardcode
  temperature: 0.7
  max-tokens: 2000
  timeout-seconds: 60
  save-call-log: true        # persists every AI call to ai_call_log table
```

Streaming responses use SSE. `save-call-log: true` records every request/response in the `ai_call_log` table for debugging.

### Database

Schema: `sql/schema.sql` (21 tables). Seed data: `sql/seed.sql`.

Table groups: `sys_*` (system/org), `crm_customer*` / `crm_follow*` / `crm_task*` (CRM core), `crm_project*` / `crm_competitor*` (properties), `report_*` (statistics), `ai_*` (LLM logs).

MyBatis-Plus mapper XML files live in `src/main/resources/mapper/<module>/`.

## Code Conventions

- Lombok (`@Data`, `@Builder`, etc.) on all entity/DTO/VO classes.
- Complex SQL (multi-join, conditional filtering, pagination) goes in XML mappers, not `@Select` annotations.
- Business state transitions and permission rules must be commented — see `docs/代码注释规范.md`.
- `map-underscore-to-camel-case: true` is active; table column `created_at` maps to field `createdAt` automatically.

## Key Documentation

- `docs/项目前期准备总览.md` — Full MVP spec: all 97+ API endpoints, ER diagram, role-permission matrix, data model.
- `docs/后台管理前端模块设计.md` — Admin frontend module design and menu structure.
- `docs/代码注释规范.md` — Comment standards (what to annotate and what to skip).
- `server/README.md` — Quick-start commands and full MVP API list.



# Frontend Design System Rules

## Color System

- Define CSS variables: --color-primary, --color-secondary, --color-neutral-*, --color-success, --color-warning, --color-error
- Backgrounds: white (#ffffff) or light gray (#f8f9fa, #f3f4f6) only
- No gradients on backgrounds or buttons
- No blue-purple gradients (linear-gradient with purple/violet/indigo) anywhere
- No neon colors, no rainbow palettes
- No more than 3 brand colors in any single view
- Text colors: #111827 (primary), #6b7280 (secondary), #9ca3af (tertiary)

## Typography

- Font scale: 12px / 14px / 16px / 20px / 24px / 32px
- CSS variables: --text-xs, --text-sm, --text-base, --text-lg, --text-xl, --text-2xl
- Body: font-weight 400, line-height 1.5
- Headings: font-weight 600, line-height 1.25
- Do not mix px, rem, em — pick one unit system
- No arbitrary font sizes outside the defined scale

## Spacing

- 4px base grid: 4 / 8 / 12 / 16 / 24 / 32 / 48 / 64px
- CSS variables: --space-1 through --space-16
- No magic numbers (13px, 7px, 23px, etc.)
- Consistent padding within component families

## Components

Cards:

- Use either border (1px solid #e5e7eb) OR shadow, not both
- Shadow level 1: 0 1px 3px rgba(0,0,0,0.08)
- Shadow level 2: 0 4px 12px rgba(0,0,0,0.1)
- Border radius: 6px or 8px, no 16px+

Buttons:

- Primary: solid fill, no gradient
- Secondary: outline or ghost
- Hover: darken by 10%, not color switch
- No rounded-full on rectangular buttons

Inputs:

- Border: 1px solid #d1d5db
- Border radius: 6px
- Focus: border-color change + outline, no glow

## Icons

- Use one icon set consistently: Lucide / Heroicons / Phosphor
- Size: 16px inline, 20px standalone
- No emoji as functional icons

## Forbidden Patterns

- No blue-purple gradients
- No glassmorphism unless explicitly requested
- No emoji icons
- No excessive shadows on every element
- No inline styles for color, spacing, or typography
- No magic numbers — every value must reference a design token
- No more than 2 shadow depth levels per page



## HTML 规范

- 页面骨架必须使用语义化标签：header, nav, main, section, article, aside, footer
- 禁止用 div 模拟可用语义标签替代的结构
- 交互元素必须使用原生标签：button 用于操作，a 用于导航
- 标题必须使用 h1-h6 并保持层级关系
