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

## Cập nhật P4 (2026-09-25)
Triển khai giai đoạn đầu thu gọn hạ tầng, giữ nguyên hướng:
- JDBC + HikariCP, migration SQL thuần (`server/app/src/main/resources/migrations/V*.sql`, bảng `schema_version`). Dev/test chạy H2 chế độ PostgreSQL; môi trường thật trỏ `PXWORLD_DB_URL` tới Postgres, cùng file migration.
- Xác thực bằng JWT HS256 do server tự phát, mật khẩu PBKDF2-SHA256 120k vòng. Keycloak hoãn tới khi cần SSO cho staff; vai trò (`player`, `staff_admin|support|liveops|creator|qa|dev`) nằm trong claim `roles`, nên đổi nhà cung cấp danh tính không đổi route.
- Redis, ClickHouse, S3 hoãn: telemetry ghi vào bảng `telemetry_events`, gói content lưu trong `content_releases.body`. Tách ra khi đo được tải thực.
- Xác thực trận: `POST /battles/validate` nhận `ReplayDocument` (cùng `ReplayCodec` với client), chặn đội hình bất hợp lệ, chạy lại `BattleEngine.replay` và so kết quả + số hiệp.
- Phần thưởng hỗ trợ đi qua thư (`mail`) và luôn ghi `audit_log` kèm lý do; kinh tế vẫn do client giữ tới khi có save do server làm chủ.
