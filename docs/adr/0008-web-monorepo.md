# ADR 0008: Monorepo web: console, pilot, site

- Trạng thái: Chấp nhận
- Ngày: 2026-09-25
- Liên quan: [MASTER_PLAN §0 D8](../MASTER_PLAN.md)

## Bối cảnh
Dashboard React hiện tại chỉ sửa JSON local; cần Console đa vai trò, Pilot Portal, Site.

## Quyết định
pnpm workspace: apps/console (Admin, LiveOps, Studio, DevTools, QA Hub theo quyền), apps/pilot, apps/site (Astro); packages ui, api-client (OpenAPI), design-tokens, screen-catalog.

## Hệ quả
Dashboard được nâng cấp thành Studio thay vì bỏ. Một shell chung cho nhiều bề mặt.
