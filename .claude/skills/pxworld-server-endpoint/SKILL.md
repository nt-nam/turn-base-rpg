---
name: pxworld-server-endpoint
description: Add or change a PXWORLD Ktor server endpoint, DB migration or staff role with correct auth, audit, error mapping, H2/PostgreSQL-portable SQL and testApplication tests. Use for any change under server/app.
---

# Thêm endpoint, migration hoặc vai trò ở server

Nguồn chi tiết: `docs/handbook/10-recipes-server-and-console.md`.

## Checklist endpoint
1. Khai báo data class `@Serializable` cho request và response, đặt cạnh các data class cùng nhóm trong `Routes.kt`.
2. Truy cập DB qua `Repositories.kt` bằng `db.query`/`db.update`/`db.single`/`transaction`. SQL phải chạy được trên **cả** H2 (PostgreSQL mode) và PostgreSQL.
3. Route đặt trong nhóm phù hợp:
   - Endpoint cần đăng nhập nằm trong `authenticate(AUTH)`.
   - Staff cần `requireRole(...)` với vai trò tối thiểu.
4. Lỗi xử lý bằng exception đã được map sẵn:

   | Exception | Mã |
   |---|---|
   | `IllegalArgumentException` | 400 |
   | `Unauthenticated` | 401 |
   | `Forbidden` | 403 |
   | `IllegalStateException` | 409 |
   | `ContentRejected` | 422 |

   Tài khoản không còn tồn tại thì trả **401**, không phải 403.
5. Mọi thao tác ghi của staff đều ghi `audit_log`. Cấp phát, khôi phục hay khoá thì bắt buộc có `reason`.
6. Không log token hay mật khẩu. Secret lấy từ biến môi trường (`ServerConfig`).
7. Test trong `server/app/src/test/.../ServerTest.kt` theo mẫu `testApplication` (H2 in-memory, bản sao content trong thư mục tạm). Cần phủ: đường thành công, sai quyền, sai tham số và xung đột nếu có.
8. Nếu client game gọi endpoint, cập nhật `HttpCloudGateway` và port trong `game/application/Cloud.kt`, kèm test `CloudSyncTest`.

## Migration
- Tạo file mới `server/app/src/main/resources/migrations/V<n>__<tên>.sql` và thêm vào danh sách `MIGRATIONS` trong `Database.kt`.
- **Không sửa migration đã có.**

## Kiểm chứng
```bash
./gradlew --settings-file settings-test.gradle :server:app:test :tools:architecture:test
SCENARIO=cloud tools/test-agent/run-desktop.sh "$PWD/agent-reports/cloud"        # nếu chạm tới hợp đồng với game
tools/test-agent/console/run-console.sh --out="$PWD/agent-reports/console"      # nếu Console dùng endpoint
```
