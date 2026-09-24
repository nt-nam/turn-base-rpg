# ADR 0007: Backend Ktor dùng chung module domain

- Trạng thái: Chấp nhận
- Ngày: 2026-09-25
- Liên quan: [MASTER_PLAN §0 D7](../MASTER_PLAN.md)

## Bối cảnh
Cần cloud save, LiveOps, anti-cheat, analytics; muốn xác thực trận ở server.

## Quyết định
Ktor + Postgres + Redis + ClickHouse + S3 (MinIO) + Keycloak; module battle chạy lại BattleEngine để xác thực replay.

## Hệ quả
Một nguồn luật game cho client và server. Vận hành nhiều hạ tầng hơn (xem infra/).
