# 10 · Công thức: server và web Console

> Viết cho kỹ sư backend, kỹ sư web và QA nhận làm tiếp `server/app` và `web/apps/console`. Mọi khẳng định dưới đây đã đối chiếu với code ở nhánh `rewrite`, commit `af8fd71`. Khi đổi code, sửa chương này trong cùng commit.
>
> Quy ước trong chương: đường dẫn tính từ gốc repo. `ADMIN` là role `staff_admin` và **luôn qua mọi kiểm tra vai trò** (cả server lẫn Console). Code mẫu tuân luật "không comment trong code" của [README §3](README.md), nên mọi giải thích nằm ở phần chữ.

**Mục lục**
1. [Giải phẫu server](#1-giải-phẫu-server)
2. [Tham chiếu API (39 endpoint)](#2-tham-chiếu-api-39-endpoint)
3. [Công thức: thêm endpoint](#3-công-thức-thêm-endpoint)
4. [Công thức: thêm migration DB](#4-công-thức-thêm-migration-db)
5. [Công thức: thêm staff role](#5-công-thức-thêm-staff-role)
6. [Hợp đồng game ↔ server](#6-hợp-đồng-game--server)
7. [Giải phẫu Console](#7-giải-phẫu-console)
8. [Công thức: thêm trang Console](#8-công-thức-thêm-trang-console)
9. [Công thức: Studio sửa theo schema](#9-công-thức-studio-sửa-theo-schema)
10. [Chạy và test cục bộ](#10-chạy-và-test-cục-bộ)
11. [Checklist bảo mật, lỗi thường gặp, vấn đề đã biết](#11-checklist-bảo-mật-lỗi-thường-gặp-vấn-đề-đã-biết)
12. [Khoảng trống so với MASTER_PLAN §10/§11](#12-khoảng-trống-so-với-master_plan-1011)

Mục 3, 4, 5 và 8 dùng chung **một ví dụ xuyên suốt**: tính năng "duyệt trận bị từ chối" (anti-cheat) trên màn `console.anticheat.battle_validation_failures`. Tính năng này **chưa có trong code**; nó được chọn vì chạm đủ mọi lớp (migration, repository, route có audit, test, trang Console, bước console agent). Đây là bản rút gọn để dạy công thức. Khi làm [WP-B9](05-work-packages.md) thật, đọc nghiệm thu của WP-B9 trước (WP-B9 yêu cầu thêm cờ `anticheat_flags`).

---

## 1. Giải phẫu server

### 1.1 Bản đồ file

| File | Nội dung |
|---|---|
| [Server.kt](../../server/app/src/main/kotlin/com/pxworld/server/Server.kt) | `Services` (ghép DB, repository, token, content), `ApiJson`, `ErrorView`, `Application.pxworld` (cài plugin và route), `AUTH`, `main()` |
| [Routes.kt](../../server/app/src/main/kotlin/com/pxworld/server/Routes.kt) | Mọi route, data class request/response, hằng số giới hạn (cuối file) |
| [Repositories.kt](../../server/app/src/main/kotlin/com/pxworld/server/Repositories.kt) | Mọi câu SQL và các row class (`AccountRow`, `SaveRow`, `AuditRow`…) |
| [Database.kt](../../server/app/src/main/kotlin/com/pxworld/server/Database.kt) | Pool Hikari, bộ chạy migration, `transaction/update/query/single`, `Connection.prepared` |
| [Security.kt](../../server/app/src/main/kotlin/com/pxworld/server/Security.kt) | `Roles`, `Passwords`, `Tokens`, `Caller`, `caller()`, `requireRole()`, exception `Forbidden`, `Unauthenticated` |
| [ContentService.kt](../../server/app/src/main/kotlin/com/pxworld/server/ContentService.kt) | Đọc `content/`, Studio upsert có kiểm tra, xác thực replay trận, `ValidationView`, `ReplayVerdict`, `ContentRejected` |
| [ContentDiff.kt](../../server/app/src/main/kotlin/com/pxworld/server/ContentDiff.kt) | So sánh hai content pack |
| [ServerConfig.kt](../../server/app/src/main/kotlin/com/pxworld/server/ServerConfig.kt) | Đọc biến môi trường |
| [V1__init.sql](../../server/app/src/main/resources/migrations/V1__init.sql) | Schema ban đầu (10 bảng) |
| [ServerTest.kt](../../server/app/src/test/kotlin/com/pxworld/server/ServerTest.kt) | 7 test tích hợp qua HTTP thật trên H2 in-memory |
| [build.gradle.kts](../../server/app/build.gradle.kts) | Plugin `application`, `mainClass = com.pxworld.server.ServerKt`, system property `contentDir`/`legacyAssetsDir` cho test, task `run` chạy ở gốc repo |

Thư viện: Ktor 3.6.0 (Netty), kotlinx.serialization, `com.auth0:java-jwt` 4.6.0 (kéo theo từ `ktor-server-auth-jwt`), HikariCP 6.3.0, H2 2.3.232, PostgreSQL JDBC 42.7.13, Logback 1.5.18, JVM 17. Server phụ thuộc `:game:infrastructure` nên dùng **chung** `BattleEngine`, `ContentLoader`, `ContentValidator`, `ReplayCodec` với game ([ADR 0007](../adr/0007-ktor-backend-shared-domain.md)).

Luật kiến trúc (`:tools:architecture:test`): mọi file trong `server/app` phải thuộc package `com.pxworld.server` và không được import `com.pxworld.client.`, `com.badlogic.`, `com.github.quillraven.`.

### 1.2 Khởi động: `Services`

`Services(config, clock)` chạy theo thứ tự:

1. `Database(url, user, password).also { it.migrate() }` — tạo pool và chạy migration còn thiếu.
2. `Repositories(database, clock)`.
3. `Tokens(config.jwtSecret, config.jwtIssuer)`.
4. `ContentService(contentDir, legacyAssetsDir, contentWritable)` — nạp `content/` ngay. Content sai định dạng thì server không khởi động.
5. `bootstrapAdmin()` — chỉ tạo khi có đủ `adminEmail` + `adminPassword`, **chưa có tài khoản staff nào** và email chưa tồn tại. Tài khoản tạo ra: `kind = "staff"`, `roles = {staff_admin}`, tên `Administrator`.
6. `publishCurrentContent("system")` — `version = sha256(pack).take(12)`. `addRelease` bỏ qua nếu version đã có. Nếu kênh `config.env` **chưa có** bản nào thì promote bản này. Kênh đã có bản thì **không** tự đổi: sửa content rồi khởi động lại, `/content/manifest` vẫn trả bản cũ tới khi có người promote.

`clock: () -> Long` (mặc định `System::currentTimeMillis`) cấp mọi timestamp trong repository và biến `now` trong route. **Ngoại lệ:** xác minh JWT dùng đồng hồ thật của java-jwt, và `schema_version.applied_at` dùng `System.currentTimeMillis()`. Hệ quả cho test xem [§11.2](#112-lỗi-thường-gặp-rút-từ-các-lần-sửa-thật).

Hằng loại tài khoản: `Services.KIND_STAFF = "staff"`, `KIND_PLAYER = "player"`, `KIND_GUEST = "guest"`.

### 1.3 `Application.pxworld(services)`: plugin

| Plugin | Cấu hình thật |
|---|---|
| `ContentNegotiation` | `json(ApiJson)`, với `ApiJson = Json { ignoreUnknownKeys = true; encodeDefaults = true; explicitNulls = false }` |
| `CallLogging` | Mặc định (log method + path, không log body) |
| `CORS` | `allowHost` cho từng host trong `PXWORLD_CORS_HOSTS`, scheme `http`/`https`; header `Authorization`, `Content-Type`; thêm method `PUT`, `DELETE` |
| `Authentication` | `jwt(AUTH)` với `AUTH = "pxworld"`; `verifier(services.tokens.verifier)`; `validate` chấp nhận khi token có `sub`; `challenge` trả 401 `{"error":"authentication required"}` |
| `StatusPages` | Bảng [§1.10](#110-quy-ước-lỗi-statuspages) |

Hệ quả của `ApiJson` mà ai viết client cũng phải biết:
- `explicitNulls = false`: field có giá trị `null` **bị bỏ khỏi JSON**. Phía TypeScript khai báo `field?: T`, không phải `field: T | null`.
- `ignoreUnknownKeys = true`: gõ sai tên field trong request không báo lỗi. Field có giá trị mặc định sẽ lặng lẽ nhận mặc định; field không có mặc định thì trả 400.

### 1.4 `Security.kt`

| Hằng trong `Roles` | Giá trị | Ghi chú |
|---|---|---|
| `PLAYER` | `player` | Mọi tài khoản đăng ký hoặc khách |
| `ADMIN` | `staff_admin` | `Caller.has(x)` luôn `true` |
| `SUPPORT` | `staff_support` | |
| `LIVEOPS` | `staff_liveops` | |
| `CREATOR` | `staff_creator` | |
| `QA` | `staff_qa` | |
| `DEV` | `staff_dev` | |
| `STAFF` | tập 6 role `staff_*` | Dùng cho `isStaff`, kiểm tra `/admin/staff`, chặn khoá staff |

- **`Passwords`**: chuỗi lưu dạng `pbkdf2$<iterations>$<saltBase64>$<hashBase64>`, thuật toán `PBKDF2WithHmacSHA256`, 120.000 vòng, khoá 256 bit, salt 16 byte, Base64 không padding. `verify` so sánh hằng thời gian (`MessageDigest.isEqual`); chuỗi sai định dạng trả `false`. Số vòng nằm trong chuỗi nên tăng `ITERATIONS` không làm hỏng hash cũ.
- **`Tokens`**: HS256, issuer `pxworld-<env>`, `sub` = account id, claim `roles` (list) và `name` (displayName), `iat`, `exp = iat + 12h` (`DEFAULT_LIFETIME`). **Không có refresh token.** Verifier kiểm `exp`, và từ chối `iat` nằm ở tương lai, theo **đồng hồ thật**. Issuer gắn với env nên token của `dev` không dùng được ở `qa`.
- **`Caller(accountId, roles)`**: `has(role)` = `role in roles || staff_admin in roles`; `isStaff` = có ít nhất một role thuộc `Roles.STAFF`.
- **`ApplicationCall.caller()`**: đọc `JWTPrincipal`. Không có principal thì ném `Forbidden` (403); việc này chỉ xảy ra khi gọi ngoài khối `authenticate(AUTH)`.
- **`ApplicationCall.requireRole(vararg roles)`**: trả `Caller` hoặc ném `Forbidden("requires one of …")`. Hàm này **chỉ đọc claim trong JWT, không tra DB**.
- **`activeCaller()`** (hàm cục bộ trong `Routes.kt`): `caller()` rồi tra bảng `accounts`. Tài khoản không còn → `Unauthenticated` (401). Đang bị khoá → `Forbidden("account suspended until <ms>")` (403). Mọi route của người chơi phải dùng hàm này.

### 1.5 `Database.kt`

| Thành phần | Hành vi |
|---|---|
| Pool | `HikariDataSource`, `maximumPoolSize = 8` |
| `transaction { connection -> … }` | Tắt autocommit, commit khi khối chạy xong, rollback rồi ném lại nếu lỗi |
| `update(sql, vararg p): Int` | Một transaction, trả số dòng bị ảnh hưởng |
| `query(sql, vararg p, map = { rs -> T }): List<T>` | Một transaction, map từng dòng |
| `single(...)` | Dòng đầu hoặc `null` |
| `Connection.prepared(sql, Array<out Any?>)` | Bind theo kiểu: `null`, `String`, `Int`, `Long`, `Boolean`, `Double`; kiểu khác gọi `toString()` |
| `Database.newId()` | UUID ngẫu nhiên. Mọi khoá chính là `VARCHAR(36)` do code sinh, không do DB sinh |
| `migrate()` | Tạo `schema_version(version INT PRIMARY KEY, applied_at BIGINT)` nếu chưa có; chạy các cặp trong `MIGRATIONS` chưa áp dụng, theo thứ tự danh sách, **trong một transaction**; mỗi file bị tách theo dấu `;` và chạy từng câu |
| `MIGRATIONS` | `listOf(1 to "/migrations/V1__init.sql")`. Runner **không quét thư mục**: file không đăng ký thì không bao giờ chạy |

### 1.6 Bảng dữ liệu hiện có (V1)

| Bảng | Khoá | Cột chính | Ai ghi |
|---|---|---|---|
| `accounts` | `id` | `email` (UNIQUE, chữ thường, null với khách), `password_hash`, `display_name` ≤64, `kind`, `roles` (chuỗi phân cách dấu phẩy, đã sort), `banned_until` (ms, null), `created_at` | register, guest, `/admin/staff`, bootstrap, sanction/lift |
| `saves` | (`account_id`, `slot`) | `revision`, `body` TEXT, `updated_at` | `PUT /saves/{slot}`, restore |
| `save_history` | `id` | `account_id`, `slot`, `revision`, `body`, `created_at`; giữ 30 revision gần nhất (`HISTORY_DEPTH`) | `putSave` |
| `content_releases` | `version` | `sha256`, `body` (pack JSON), `bytes` (UTF-8), `records`, `published_by`, `created_at` | khởi động, `POST /content/releases` |
| `content_channels` | `env` | `version`, `promoted_by`, `promoted_at` | promote |
| `telemetry_events` | `id` | `account_id` (null), `name` ≤64, `payload` TEXT (JSON), `client_version` ≤32, `created_at` | `POST /telemetry` |
| `audit_log` | `id` | `seq` (IDENTITY, UNIQUE, dùng để sắp xếp), `actor_id`, `action` ≤64, `target` ≤128, `reason` ≤512, `payload` TEXT, `created_at` | mọi thao tác ghi của staff |
| `mail` | `id` | `account_id`, `subject` ≤128, `grants` TEXT (JSON object), `claimed`, `created_at` | grant, claim |
| `battle_validations` | `id` | `account_id`, `encounter_id` ≤128, `claimed_outcome`, `replayed_outcome` ≤16, `valid`, `created_at` | `POST /battles/validate` |
| `agent_runs` | `id` | `mode`, `status`, `visited`, `registered`, `launch_percent`, `report` TEXT, `uploaded_by`, `created_at` | `POST /qa/agent-runs` |

Index: `telemetry_events(name)`, `audit_log(target)`, `mail(account_id)`. Mọi thời điểm là **epoch millis kiểu `BIGINT`**, lấy từ `clock()`; không có cột `TIMESTAMP`, không dùng `now()` của DB. JSON lưu trong cột `TEXT`, không dùng `JSONB`.

### 1.7 `Repositories`: hàm có sẵn

| Nhóm | Hàm |
|---|---|
| Tài khoản | `createAccount(email?, passwordHash?, displayName, kind, roles)`, `accountByEmail(email)`, `account(id)`, `staffCount()`, `searchAccounts(query, limit)`, `setBan(accountId, until?)` |
| Save | `saves(accountId)`, `save(accountId, slot)`, `putSave(accountId, slot, expectedRevision, body): SaveRow?` (trả `null` khi lệch revision), `saveHistory(accountId, slot)` (mới nhất trước) |
| Content | `addRelease(...)` (idempotent theo version), `releases()` (mới nhất trước), `releaseBody(version)`, `promote(env, version, actorId)`, `channels(): Map<env, version>` |
| Telemetry | `addTelemetry(accountId?, name, payload, clientVersion?)`, `telemetryByName(since): Map<name, count>` |
| Đếm | `count(table, where = "1 = 1", vararg params)` — `table` phải thuộc `COUNTABLE`; `where` là **SQL thô**, chỉ được truyền chuỗi viết trong code, không bao giờ từ input |
| Audit | `audit(actorId, action, target, reason, payload)`, `auditLog(target?, limit)` (sắp `seq DESC`) |
| Thư | `addMail(accountId, subject, grants): id`, `mail(accountId)`, `claimMail(accountId, mailId): MailRow?` |
| Trận | `addBattleValidation(accountId, encounterId, claimed, replayed, valid)` |
| QA | `addAgentRun(...)`, `agentRuns(limit)`, `agentRunReport(id)` |

Hai idiom SQL cần chép đúng:
- **Một câu**: `db.update(...)`, `db.query(...) { rs -> … }`, `db.single(...)`. Mỗi lời gọi là một transaction.
- **Nhiều câu phải cùng thành công**: `db.transaction { connection -> connection.prepared(sql, arrayOf<Any?>(...)).use { it.executeUpdate() } }`, xem `putSave`, `promote`, `claimMail`.

### 1.8 `ContentService` và `ContentDiff`

| Hàm | Hành vi |
|---|---|
| `bundle` | `ContentBundle` đang nạp (`@Volatile`), đổi sau mỗi lần Studio lưu |
| `pack` | `ContentCompilation.packJson(bundle)` |
| `kinds()` | Tên thư mục cấp một chứa file JSON **dạng mảng**, bỏ `localization/`, đã sort. Hiện là 17 kind: `achievements, audio_cues, balance, checkin_tables, currencies, dialogues, encounters, enemies, equipment, hero_classes, heroes, items, maps, npcs, quests, skills, statuses` |
| `records(kind)` | Nối bản ghi của mọi file thuộc kind. Kind không tồn tại trả danh sách rỗng |
| `validate(tree)` | `ContentValidator.validate(ContentLoader.load(tree), assets)` → `ValidationView(errors, warnings, records, issues)` |
| `upsert(kind, record, dryRun)` | `@Synchronized`. Chi tiết ở [§9.2](#92-server-phục-vụ-schema-và-lưu-bản-ghi) |
| `validateReplay(document)` | Chi tiết ở [§6.4](#64-xác-thực-trận-postbattlesvalidate) |

`ContentDiff.between(from, to, before, after)` so hai pack JSON: mỗi khoá cấp một có giá trị là mảng được so theo `id` (`added`, `removed`, `changed` kèm danh sách field cấp một đã đổi); khoá `localization` so theo từng locale; các object khác so theo khoá. `TableDiff.sample` tối đa 20 khoá (`ContentDiff.SAMPLE`).

### 1.9 `ServerConfig`: biến môi trường

| Biến | Mặc định | Ghi chú |
|---|---|---|
| `PXWORLD_ENV` | `dev` | Tên env; issuer JWT là `pxworld-<env>`; `dev` bật các mặc định bên dưới |
| `PXWORLD_PORT` | `8080` | |
| `PXWORLD_DB_URL` | `jdbc:h2:file:./build/pxworld-dev;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE` | Đường dẫn tương đối theo thư mục làm việc |
| `PXWORLD_DB_USER`, `PXWORLD_DB_PASSWORD` | không có | |
| `PXWORLD_JWT_SECRET` | `pxworld-dev-secret-change-me` | Ngoài `dev` mà để mặc định thì server từ chối khởi động |
| `PXWORLD_CONTENT_DIR` | `content` | |
| `PXWORLD_LEGACY_ASSETS_DIR` | `assets` | Validator kiểm tra asset có tồn tại |
| `PXWORLD_CONTENT_WRITABLE` | `true` ở `dev`, `false` ở env khác | Studio lưu thật chỉ khi `true` |
| `PXWORLD_ADMIN_EMAIL` | `admin@pxworld.local` ở `dev` | |
| `PXWORLD_ADMIN_PASSWORD` | `admin-dev-password` ở `dev` | |
| `PXWORLD_CORS_HOSTS` | `localhost:5173` | Danh sách phân cách dấu phẩy |

### 1.10 Quy ước lỗi (StatusPages)

| Code ném ra hoặc trả về | HTTP | Body | Dùng khi |
|---|---|---|---|
| Không có token, token sai chữ ký, hết hạn, sai issuer | 401 | `{"error":"authentication required"}` | Plugin JWT tự xử lý |
| `throw Unauthenticated(msg)` | 401 | `{"error":msg}` | Token hợp lệ nhưng tài khoản không còn |
| `throw Forbidden(msg)` | 403 | `{"error":msg}` | Sai vai trò, tài khoản bị khoá |
| `throw ContentRejected(validation)` | 422 | `{"error":"content rejected","detail":ValidationView}` | Studio lưu bản ghi làm content có lỗi |
| `require(...)`, `IllegalArgumentException`, `NumberFormatException` | 400 | `{"error":msg}` | Input sai; đối tượng admin trỏ tới không tồn tại (`unknown account`) |
| `check(...)`, `error(...)`, `IllegalStateException` | 409 | `{"error":msg}` | Xung đột trạng thái (email đã có, thay đổi đồng thời, content chỉ đọc) |
| `SerializationException`, `BadRequestException` | 400 | `{"error":msg}` | Body JSON sai kiểu hoặc thiếu field |
| `call.respond(HttpStatusCode.X, ErrorView(...))` trực tiếp | 401 / 404 / 409 | `{"error":msg}` | Sai mật khẩu (401), tài nguyên của chính mình không có (404), mail đã nhận (409) |
| `call.respond(Conflict, SaveConflictView(...))` | 409 | `{"error":"revision conflict","current":SaveMetaView}` | Chỉ `PUT /saves/{slot}` |
| Mọi exception khác (SQL, `IndexOutOfBounds`…) | 500 | rỗng | **Là bug**: phải chặn bằng validate trước |

---

## 2. Tham chiếu API (39 endpoint)

### 2.1 Ký hiệu cột "Quyền"

| Ký hiệu | Nghĩa trong code |
|---|---|
| `—` | Công khai, không cần token |
| `tuỳ chọn` | `authenticate(AUTH, optional = true)`: không gửi token vẫn được; **gửi token sai hoặc hết hạn thì 401** |
| `JWT` | Trong `authenticate(AUTH)`, chỉ cần token hợp lệ |
| `player` | `activeCaller()`: token hợp lệ, tài khoản còn tồn tại (401 nếu không) và không bị khoá (403) |
| `staff` | `call.caller().isStaff` |
| Tên role | `requireRole(...)`, ví dụ `SUPPORT, LIVEOPS` nghĩa là một trong hai; `ADMIN` luôn qua |

### 2.2 Kiểu dữ liệu

| Kiểu | Field |
|---|---|
| `ErrorView` | `error`, `detail?: ValidationView` |
| `SessionView` | `token`, `account: AccountView` |
| `AccountView` | `id`, `email?`, `displayName`, `kind` (`player`/`guest`/`staff`), `roles[]` (đã sort), `bannedUntil?` (ms), `createdAt` |
| `SaveMetaView` | `slot`, `revision`, `updatedAt`, `bytes` (thực ra là **số ký tự** của `body`) |
| `SaveRow` | `slot`, `revision`, `updatedAt`, `body` (chuỗi JSON) |
| `SaveConflictView` | `error`, `current: SaveMetaView` |
| `ManifestView` | `env`, `version`, `sha256`, `bytes`, `records` |
| `ContentReleaseRow` | `version`, `sha256`, `bytes`, `records`, `publishedBy`, `createdAt` |
| `ContentDiffView` | `from`, `to`, `kinds[]: {kind, added[], removed[], changed[]: {id, fields[]}}`, `tables[]: {table, added, removed, changed, sample[]}` |
| `MailRow` | `id`, `subject`, `grants` (**chuỗi** JSON object `id → số lượng`), `claimed`, `createdAt` |
| `AuditRow` | `id`, `actorId`, `action`, `target`, `reason`, `payload` (chuỗi JSON), `createdAt` |
| `AgentRunRow` | `id`, `mode`, `status`, `visited`, `registered`, `launchPercent`, `createdAt` |
| `PlayerDetailView` | `account`, `saves[]: SaveMetaView`, `mail[]: MailRow`, `audit[]: AuditRow` |
| `DashboardView` | `env`, `contentVersion?`, `accounts`, `players` (kind khác `staff`, gồm khách), `saves`, `telemetryLastDay: {name: count}`, `battleValidations`, `rejectedBattles`, `agentRuns` |
| `ValidationView` | `errors`, `warnings`, `records`, `issues[]: {severity: "ERROR"/"WARNING", recordId, message}` |
| `ReplayDocument` | `id`, `encounterId`, `seed`, `lineup[]: {heroId, level, star, lane, depth, bonus: {StatKind: Int}}`, `commands[]: {actor, skill, target?}`, `outcome` (`VICTORY`/`DEFEAT`/`DRAW`), `rounds`, `recordedAtMillis` |
| `ReplayVerdict` | `valid`, `replayedOutcome`, `replayedRounds`, `reasons[]` |
| `IdView` | `id` |

### 2.3 Hệ thống và xác thực

| Method | Path | Quyền | Request | Response | Lỗi |
|---|---|---|---|---|---|
| GET | `/health` | — | | `HealthView {status:"ok", env, contentVersion?}` (version đang ở kênh `config.env`) | |
| GET | `/metrics` | — | | `text/plain`, 6 dòng: `pxworld_accounts_total`, `pxworld_saves_total`, `pxworld_telemetry_events_total`, `pxworld_battle_validations_total`, `pxworld_battle_validations_rejected_total`, `pxworld_agent_runs_total` | |
| POST | `/auth/register` | — | `RegisterRequest {email, password, displayName}` | 201 `SessionView` (kind `player`, roles `[player]`) | 400 email sai, mật khẩu < 10 ký tự, tên không nằm trong 2–32 ký tự sau trim; 409 `email already registered` |
| POST | `/auth/guest` | — | `GuestRequest {displayName}` | 201 `SessionView` (kind `guest`, không email, không mật khẩu) | 400 tên |
| POST | `/auth/login` | — | `LoginRequest {email, password}` | 200 `SessionView` | 401 `wrong email or password`; 403 `account suspended until <ms>` |
| GET | `/me` | JWT | | `AccountView` | 401 tài khoản không còn (không kiểm tra khoá) |

Email luôn được đổi sang chữ thường khi lưu và khi tra.

### 2.4 Content (công khai)

| Method | Path | Quyền | Request | Response | Lỗi |
|---|---|---|---|---|---|
| GET | `/content/manifest?env=<env>` | — | `env` mặc định `config.env` | `ManifestView` của bản đang ở kênh | 404 `no release promoted to <env>` |
| GET | `/content/packs/{version}` | — | | Pack JSON thô (`application/json`), SHA-256 trùng `manifest.sha256` | 404 `unknown release` |

### 2.5 Người chơi

| Method | Path | Quyền | Request | Response | Lỗi |
|---|---|---|---|---|---|
| POST | `/telemetry` | tuỳ chọn | `TelemetryBatch {clientVersion?, events[]: {name, payload: object = {}}}` | 202 `{"accepted": n}`; có token thì ghi `account_id` | 400 quá 200 sự kiện; tên không khớp `^[a-z][a-z0-9_.]{1,63}$`; 401 token sai/hết hạn |
| GET | `/saves` | player | | `SaveMetaView[]` theo slot | |
| GET | `/saves/{slot}` | player | `slot` khớp `^[a-z0-9_-]{1,32}$` | `SaveRow` | 400 slot sai; 404 `no save in slot` |
| PUT | `/saves/{slot}` | player | `PutSaveRequest {expectedRevision, body}`; `body` là chuỗi chứa **JSON object**, tối đa 2.000.000 ký tự; slot mới dùng `expectedRevision: 0` | 200 `SaveMetaView` với `revision = expectedRevision + 1` | 400 slot, quá cỡ, body không phải JSON object; 409 `SaveConflictView` (slot chưa có thì `current` = `{slot, 0, 0, 0}`) |
| GET | `/saves/{slot}/history` | player | | `SaveMetaView[]`, mới nhất trước, tối đa 30 | 400 slot |
| GET | `/saves/{slot}/history/{revision}` | player | | `SaveRow` của revision đó | 400 revision không phải số; 404 `revision not kept` |
| POST | `/battles/validate` | player | `ReplayDocument` | `ReplayVerdict`; mọi verdict được ghi vào `battle_validations` | 400 JSON sai, `outcome` hoặc tên stat trong `bonus` không hợp lệ, `lane`/`depth` ngoài 0..2 |
| GET | `/mail` | player | | `MailRow[]` mới nhất trước | |
| POST | `/mail/{id}/claim` | player | | `MailRow` (xem lỗi đã biết ở [§11.3](#113-vấn-đề-đã-biết-trong-code-hiện-tại-chưa-sửa)) | 409 `mail missing or already claimed` |

### 2.6 Phát hành content (staff)

| Method | Path | Quyền | Request | Response | Lỗi / audit |
|---|---|---|---|---|---|
| GET | `/content/releases` | `LIVEOPS, DEV, CREATOR, QA` | | `ContentReleaseRow[]` mới nhất trước | 403 |
| GET | `/content/releases/{from}/diff/{to}` | `LIVEOPS, DEV, CREATOR, QA` | | `ContentDiffView` | 400 `unknown release <v>` |
| GET | `/content/channels` | `LIVEOPS, DEV, CREATOR, QA` | | `{ "<env>": "<version>" }` | |
| POST | `/content/releases` | `LIVEOPS, DEV` | không body | 201 `IdView {id: version}` | audit `content.publish`, target `content:<version>`, reason `publish current content` |
| POST | `/content/promote` | `LIVEOPS` | `PromoteRequest {env, version}`, `env ∈ {dev, qa, staging, prod}` | `ManifestView` của kênh mới | 400 env hoặc version sai; audit `content.promote`, target `channel:<env>`, reason `promote <version>` |

Promote một bản cũ hơn chính là rollback; chưa có endpoint rollback riêng.

### 2.7 Admin (staff)

| Method | Path | Quyền | Request | Response | Lỗi / audit |
|---|---|---|---|---|---|
| GET | `/admin/dashboard` | staff | | `DashboardView` (telemetry tính 24 giờ theo `clock`) | 403 |
| GET | `/admin/players?q=` | `SUPPORT, LIVEOPS` | `q` tìm chuỗi con trong email/tên (không phân biệt hoa thường) hoặc khớp đúng id; `q` rỗng trả mọi tài khoản | `AccountView[]` tối đa 50, mới nhất trước (gồm cả staff) | 403 |
| GET | `/admin/players/{id}` | `SUPPORT, LIVEOPS` | | `PlayerDetailView` (audit của target `account:<id>`, tối đa 100) | 400 `unknown account` |
| POST | `/admin/players/{id}/grant` | `SUPPORT, LIVEOPS` | `GrantRequest {subject, grants: {contentId: 1..1000000}, reason}` | 201 `IdView {id: mailId}` | 400 reason rỗng, grants rỗng hoặc ngoài khoảng, id không có trong content; audit `player.grant`, target `account:<id>`, payload = grants |
| GET | `/admin/players/{id}/saves/{slot}/history` | `SUPPORT` | | `SaveMetaView[]` | 400 |
| POST | `/admin/players/{id}/saves/{slot}/restore` | `SUPPORT` | `RestoreRequest {revision, reason}` | `SaveMetaView` của revision **mới** (bản khôi phục được ghi như một lần lưu mới) | 400 reason rỗng, `revision <n> is not kept`; 409 save đổi giữa chừng; audit `player.save_restore`, payload `{slot, from, to}` |
| POST | `/admin/players/{id}/sanction` | `SUPPORT` | `SanctionRequest {hours: 1..8760, reason}` | `AccountView` | 400 reason, hours, target là staff; audit `player.sanction`, payload `{"until": ms}` |
| POST | `/admin/players/{id}/lift` | `SUPPORT` | `LiftRequest {reason}` | `AccountView` | 400 reason; audit `player.lift` |
| GET | `/admin/audit?target=` | `SUPPORT, LIVEOPS` | `target` khớp đúng (vd `account:<id>`), bỏ trống lấy tất cả | `AuditRow[]` tối đa 100, sắp theo `seq` giảm dần | |
| POST | `/admin/staff` | `ADMIN` | `StaffRequest {email, password, displayName, roles[]}`; roles khác rỗng và đều thuộc `Roles.STAFF`; mật khẩu ≥ 10 | 201 `AccountView` (kind `staff`) | 400; 409 email đã có; audit `staff.create`, reason `create staff`, payload `{"roles":"a,b"}` |

### 2.8 QA

| Method | Path | Quyền | Request | Response | Lỗi |
|---|---|---|---|---|---|
| POST | `/qa/agent-runs` | `QA, DEV` | Báo cáo agent thô (JSON object, ≤ 5.000.000 ký tự); server đọc `mode`, `status`, `coverage.visitedCount`, `coverage.registeredCount`, `coverage.launchPercent` | 201 `IdView` | 400 không phải JSON object hoặc quá cỡ. Không ghi audit |
| GET | `/qa/agent-runs` | `QA, DEV` | | `AgentRunRow[]` tối đa 50, mới nhất trước | |
| GET | `/qa/agent-runs/{id}` | `QA, DEV` | | Báo cáo thô đúng như lúc tải lên | 404 `unknown run` |

### 2.9 Studio

| Method | Path | Quyền | Request | Response | Lỗi / audit |
|---|---|---|---|---|---|
| GET | `/studio/kinds` | `CREATOR, DEV` | | `string[]` | |
| GET | `/studio/kinds/{kind}` | `CREATOR, DEV` | | `StudioRecordsView {kind, records[]}` | Kind lạ trả 200 với `records: []` |
| GET | `/studio/kinds/{kind}/schema` | `CREATOR, DEV` | | JSON Schema draft 2020-12 của **một** bản ghi | 400 `unknown kind` |
| PUT | `/studio/kinds/{kind}?dryRun=true` | `CREATOR, DEV` | Bản ghi JSON object, bắt buộc có `id` | `ValidationView` (errors = 0) | 400 thiếu id, kind lạ; 422 content lỗi; 409 `content is read-only in this environment` (chỉ khi không dry-run). Không dry-run thì audit `studio.upsert`, target `record:<id>`, reason `edit <kind>`, payload `{}` |

### 2.10 Danh mục `audit_log.action`

`content.publish`, `content.promote`, `player.grant`, `player.save_restore`, `player.sanction`, `player.lift`, `staff.create`, `studio.upsert`. Action mới đặt tên `<vùng>.<động_từ>` chữ thường, và thêm vào danh sách này.

---

## 3. Công thức: thêm endpoint

Ví dụ xuyên suốt: staff xem các trận bị server từ chối và **duyệt** từng trận kèm kết luận. Hai endpoint mới:

| Method | Path | Quyền | Request | Response | Lỗi / audit |
|---|---|---|---|---|---|
| GET | `/admin/battles/rejected` | `SUPPORT, LIVEOPS` | | `BattleValidationRow[]` tối đa 100 | |
| POST | `/admin/battles/{id}/review` | `SUPPORT` | `BattleReviewRequest {reason}` | `BattleValidationRow` đã duyệt | 400 reason rỗng hoặc > 512, id lạ; 409 trận hợp lệ hoặc đã duyệt; audit `battle.review`, target `battle:<id>` |

Code Kotlin trong mục này **chưa được biên dịch** (đội viết tài liệu không được chạy Gradle lúc soạn). Dán xong phải chạy `:server:app:test` như bước 7; nếu lỗi biên dịch, sửa code cho đúng rồi sửa luôn chương này.

### Bước 0 · Quyết định trước khi code

| Câu hỏi | Trả lời cho ví dụ | Quy tắc |
|---|---|---|
| Nhóm route | `route("/admin")` trong `authenticate(AUTH)` | Người chơi: route gốc + `activeCaller()`. Staff: `/admin`, `/content`, `/qa`, `/studio` + `requireRole` |
| Role đọc / ghi | Đọc: `SUPPORT, LIVEOPS`. Ghi: `SUPPORT` | Ghi hẹp hơn đọc. Không cần thêm `ADMIN` vì admin luôn qua |
| Có ghi audit? | Có | **Mọi** thao tác ghi của staff |
| Bắt buộc lý do? | Có | Bắt buộc khi thao tác ảnh hưởng người chơi hoặc kinh tế (grant, restore, sanction, lift đang làm vậy) |
| Cần đổi schema? | Có: 3 cột + 1 index | Làm [§4](#4-công-thức-thêm-migration-db) trước |
| Lỗi nào ra mã nào | Id lạ → 400 (giống `unknown account`); đã duyệt → 409 | Bảng [§1.10](#110-quy-ước-lỗi-statuspages) |

### Bước 1 · Data class

Request/response của route đặt đầu `Routes.kt`, cạnh các data class khác:

```kotlin
@Serializable data class BattleReviewRequest(val reason: String)
```

Row class đặt đầu `Repositories.kt`. Field có thể `null` phải có mặc định `null` và kiểu nullable; nhờ `explicitNulls = false`, field `null` sẽ không xuất hiện trong JSON:

```kotlin
@Serializable
data class BattleValidationRow(
    val id: String,
    val accountId: String,
    val encounterId: String,
    val claimedOutcome: String,
    val replayedOutcome: String,
    val valid: Boolean,
    val createdAt: Long,
    val reviewedBy: String? = null,
    val reviewedAt: Long? = null,
    val reviewNote: String? = null,
)
```

Luật: thời điểm là `Long` epoch millis; không dùng `java.time` trong API; tên field camelCase, tên cột snake_case.

### Bước 2 · Migration

Làm theo [§4](#4-công-thức-thêm-migration-db) (file `V2__battle_review.sql`).

### Bước 3 · Hàm repository

Thêm vào trong `class Repositories`:

```kotlin
    fun rejectedBattles(limit: Int): List<BattleValidationRow> =
        db.query("SELECT * FROM battle_validations WHERE valid = ? ORDER BY created_at DESC, id LIMIT ?", false, limit, map = ::validationRow)

    fun battleValidation(id: String): BattleValidationRow? =
        db.single("SELECT * FROM battle_validations WHERE id = ?", id, map = ::validationRow)

    fun reviewBattle(id: String, reviewerId: String, note: String): Boolean =
        db.update(
            "UPDATE battle_validations SET reviewed_by = ?, reviewed_at = ?, review_note = ? WHERE id = ? AND valid = ? AND reviewed_at IS NULL",
            reviewerId, clock(), note, id, false,
        ) == 1

    private fun validationRow(rows: java.sql.ResultSet) = BattleValidationRow(
        id = rows.getString("id"),
        accountId = rows.getString("account_id"),
        encounterId = rows.getString("encounter_id"),
        claimedOutcome = rows.getString("claimed_outcome"),
        replayedOutcome = rows.getString("replayed_outcome"),
        valid = rows.getBoolean("valid"),
        createdAt = rows.getLong("created_at"),
        reviewedBy = rows.getString("reviewed_by"),
        reviewedAt = rows.getLong("reviewed_at").takeIf { !rows.wasNull() },
        reviewNote = rows.getString("review_note"),
    )
```

Vì sao viết như vậy:
- `UPDATE … WHERE … AND reviewed_at IS NULL` rồi so `== 1`: kiểm tra và ghi trong **một câu**, nên hai staff bấm cùng lúc thì chỉ một người thắng, không cần transaction nhiều câu.
- `ORDER BY created_at DESC, id`: thêm `id` để thứ tự tất định khi trùng mili-giây. Nếu cần thứ tự chèn tuyệt đối (log), dùng cột `seq` như `audit_log` ([§11.2](#112-lỗi-thường-gặp-rút-từ-các-lần-sửa-thật)).
- `getLong(...)` rồi `wasNull()` là cách đọc cột số nullable (giống `banned_until`).

SQL chạy được trên **cả** H2 (chế độ PostgreSQL) và PostgreSQL khi tuân các luật sau:

| Làm | Không làm |
|---|---|
| Tham số `?` cho mọi giá trị, kể cả `LIMIT ?` | Nối chuỗi input vào SQL |
| Boolean truyền `Boolean` của Kotlin, hoặc literal `TRUE`/`FALSE` | `1`/`0` cho cột `BOOLEAN` |
| Thời điểm lấy từ `clock()`, cột `BIGINT` | `now()`, `CURRENT_TIMESTAMP`, cột `TIMESTAMP` (phá đồng hồ tất định của test) |
| Khoá chính `VARCHAR(36)` từ `Database.newId()` | `SERIAL`, id do DB sinh làm khoá |
| JSON lưu `TEXT`, parse trong Kotlin | `JSONB`, toán tử `->>`, hàm JSON của DB |
| Tìm không phân biệt hoa thường bằng `LOWER(col) LIKE ?` | `ILIKE` |
| Upsert bằng `db.transaction` gồm SELECT rồi INSERT hoặc UPDATE (như `putSave`), hoặc DELETE rồi INSERT (như `promote`) | `ON CONFLICT`, `MERGE` (cú pháp hai DB khác nhau) |
| Định danh chữ thường, không nháy kép | Tên hoa/thường lẫn lộn, tên trùng từ khoá như `user`, `order`, `key`, `value` |

URL H2 **phải** có `MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE` để tên cột trả về là chữ thường như PostgreSQL.

### Bước 4 · Route

Trong `Routes.kt`, bên trong `route("/admin") { … }`:

```kotlin
                get("/battles/rejected") {
                    call.requireRole(Roles.SUPPORT, Roles.LIVEOPS)
                    call.respond(repositories.rejectedBattles(REJECTED_BATTLE_LIMIT))
                }
                post("/battles/{id}/review") {
                    val caller = call.requireRole(Roles.SUPPORT)
                    val id = call.parameters["id"].orEmpty()
                    val request = call.receive<BattleReviewRequest>()
                    require(request.reason.isNotBlank()) { "a reason is required" }
                    require(request.reason.length <= MAX_REASON) { "reason needs at most $MAX_REASON characters" }
                    val battle = repositories.battleValidation(id) ?: throw IllegalArgumentException("unknown battle validation")
                    check(!battle.valid && battle.reviewedAt == null) { "battle validation is valid or already reviewed" }
                    check(repositories.reviewBattle(id, caller.accountId, request.reason)) { "battle validation changed during review, retry" }
                    val payload = JsonObject(mapOf("replayed" to JsonPrimitive(battle.replayedOutcome))).toString()
                    repositories.audit(caller.accountId, "battle.review", "battle:$id", request.reason, payload)
                    call.respond(repositories.battleValidation(id) ?: error("review did not persist"))
                }
```

Thêm hằng ở cuối `Routes.kt`:

```kotlin
private const val REJECTED_BATTLE_LIMIT = 100
private const val MAX_REASON = 512
```

Thứ tự bắt buộc trong một route ghi của staff (chép đúng thứ tự của `grant`, `restore`, `sanction`):
1. `requireRole(...)` ở **dòng đầu**, trước khi đọc body.
2. `call.receive<...>()`.
3. `require(...)` cho mọi input, gồm **độ dài không vượt cột `VARCHAR`** (thiếu bước này thì DB ném lỗi và trả 500).
4. Tra đối tượng; không có → `IllegalArgumentException` (400) cho đối tượng admin trỏ tới, hoặc `call.respond(NotFound, ErrorView(...))` cho tài nguyên của chính người gọi.
5. `check(...)` cho trạng thái (409).
6. Ghi dữ liệu.
7. `repositories.audit(actorId, action, target, reason, payload)` **sau** khi ghi thành công. Payload dựng bằng `JsonObject(...)`, không dựng bằng string template chứa input.
8. `call.respond(...)`: tạo mới trả `HttpStatusCode.Created`; sửa trả 200 kèm đối tượng sau khi sửa.

Audit và thao tác chính nằm ở hai transaction riêng (giống code hiện có). Nếu cần "cả hai hoặc không gì", gộp vào một `db.transaction` và ghi `INSERT INTO audit_log` bằng `connection.prepared` trong cùng khối.

### Bước 5 · Kiểm tra lại quyền phía người chơi

Route của người chơi lấy danh tính **chỉ** từ `call.activeCaller().accountId`, không bao giờ từ body hay path. Route admin lấy đối tượng từ path bằng `call.targetAccount(repositories)` hoặc tra như bước 4.

### Bước 6 · Test theo kiểu `ServerTest`

`ServerTest` tạo cho **mỗi test** một H2 in-memory mới (`jdbc:h2:mem:<uuid>`), bản sao `content/` trong thư mục tạm, và đồng hồ giả `clock` bắt đầu từ giờ thật. Helper có sẵn: `serve { }`, `client.send(method, path, token?, body?)`, `.json()`, `.text(key)`, `client.token(path, body)`, `client.admin()`, `client.staff(adminToken, email, role)`.

Thêm test vào `ServerTest`:

```kotlin
    @Test
    fun supportReviewsRejectedBattlesOnceWithAReason() = serve {
        val player = client.token("/auth/guest", """{"displayName":"Cheater"}""")
        val forged = """{"id":"r1","encounterId":"encounter.none","seed":1,"lineup":[{"heroId":"hero.none","level":999,"star":0,"lane":0,"depth":0,"bonus":{}}],"commands":[],"outcome":"VICTORY","rounds":1,"recordedAtMillis":0}"""
        assertEquals("REJECTED", client.send("POST", "/battles/validate", player, forged).json().text("replayedOutcome"))
        val admin = client.admin()
        val support = client.staff(admin, "support@pxworld.local", Roles.SUPPORT)
        val liveops = client.staff(admin, "liveops@pxworld.local", Roles.LIVEOPS)
        val qa = client.staff(admin, "qa@pxworld.local", Roles.QA)
        assertEquals(HttpStatusCode.Forbidden, client.send("GET", "/admin/battles/rejected", qa).status)
        val rejected = ApiJson.parseToJsonElement(client.send("GET", "/admin/battles/rejected", liveops).bodyAsText()).jsonArray
        val id = rejected.single().jsonObject.text("id")
        assertEquals(HttpStatusCode.Forbidden, client.send("POST", "/admin/battles/$id/review", liveops, """{"reason":"ok"}""").status)
        assertEquals(HttpStatusCode.BadRequest, client.send("POST", "/admin/battles/$id/review", support, """{"reason":" "}""").status)
        assertEquals(HttpStatusCode.BadRequest, client.send("POST", "/admin/battles/nope/review", support, """{"reason":"x"}""").status)
        clock += 1_000
        val reviewed = client.send("POST", "/admin/battles/$id/review", support, """{"reason":"false positive, ticket 7"}""")
        assertEquals(HttpStatusCode.OK, reviewed.status)
        assertEquals(clock.toString(), reviewed.json().text("reviewedAt"))
        assertEquals(HttpStatusCode.Conflict, client.send("POST", "/admin/battles/$id/review", support, """{"reason":"again"}""").status)
        val audit = ApiJson.parseToJsonElement(client.send("GET", "/admin/audit?target=battle:$id", support).bodyAsText()).jsonArray
        assertEquals(listOf("battle.review"), audit.map { it.jsonObject.text("action") })
    }
```

Mỗi endpoint mới cần tối thiểu: một đường thành công, một 403 với role sai, một 400 với input sai, một 409 nếu có trạng thái, và kiểm tra dòng audit. Replay giả ở trên bị từ chối nhờ `level: 999` nên không cần chạy engine.

Hai luật về đồng hồ trong test:
- Đổi `clock` **sau** khi đã lấy mọi token cần dùng. Token phát khi `clock` đang ở tương lai có `iat` tương lai và bị từ chối (401).
- Không đặt `clock` thành giá trị cố định trong quá khứ: token phát ra sẽ đã hết hạn theo giờ thật.

### Bước 7 · Chạy

```bash
./gradlew --settings-file settings-test.gradle :server:app:test --tests "com.pxworld.server.ServerTest"
./gradlew --settings-file settings-test.gradle :tools:architecture:test
```

`ServerTest` đọc system property `contentDir` và `legacyAssetsDir` do task `test` của Gradle đặt; chạy thẳng trong IDE mà không qua Gradle thì phải tự thêm hai `-D` này.

### Bước 8 · Cập nhật tài liệu và Console

Thêm dòng endpoint vào [§2](#2-tham-chiếu-api-39-endpoint) và action vào [§2.10](#210-danh-mục-audit_logaction). Phía Console làm tiếp [§8](#8-công-thức-thêm-trang-console).

---

## 4. Công thức: thêm migration DB

### Các bước

1. Tạo file mới `server/app/src/main/resources/migrations/V<n>__<ten_ngan>.sql`, với `n` = số lớn nhất hiện có + 1.
2. Đăng ký trong `Database.MIGRATIONS`. Số nguyên trong danh sách là giá trị ghi vào `schema_version`; tên file chỉ là quy ước.
3. Chạy `:server:app:test` (mỗi test chạy **toàn bộ** migration trên H2 mới).
4. Chạy thử một lần trên PostgreSQL thật ([§10.2](#102-postgresql-thay-vì-h2)); hiện **chưa có** test tự động trên PostgreSQL.

Ví dụ xuyên suốt, file `server/app/src/main/resources/migrations/V2__battle_review.sql`:

```sql
ALTER TABLE battle_validations ADD COLUMN reviewed_by VARCHAR(36);
ALTER TABLE battle_validations ADD COLUMN reviewed_at BIGINT;
ALTER TABLE battle_validations ADD COLUMN review_note VARCHAR(512);
CREATE INDEX battle_validations_valid_created ON battle_validations (valid, created_at);
```

`Database.kt`:

```kotlin
        val MIGRATIONS: List<Pair<Int, String>> = listOf(
            1 to "/migrations/V1__init.sql",
            2 to "/migrations/V2__battle_review.sql",
        )
```

### Luật

| Luật | Lý do |
|---|---|
| **Không bao giờ sửa migration đã merge.** Muốn sửa thì viết migration mới | DB đã chạy bản cũ (dev, qa, prod) không chạy lại, nên các môi trường sẽ lệch nhau |
| Không có dấu `;` trong chuỗi, comment hay thân hàm | Runner tách file theo `;` một cách thô |
| Không viết comment sau câu lệnh cuối | Đoạn thừa sau `;` cuối thành một "câu" riêng gửi xuống DB |
| Mỗi `ALTER TABLE … ADD COLUMN` một câu | Cú pháp thêm nhiều cột trong một câu khác nhau giữa H2 và PostgreSQL |
| Cột mới trên bảng đã có dữ liệu: nullable, hoặc `NOT NULL DEFAULT <giá trị>` | Dòng cũ phải hợp lệ |
| Thời điểm: `BIGINT` epoch millis. JSON: `TEXT`. Khoá: `VARCHAR(36)` | Giống V1, chạy được trên cả hai DB |
| Cột chỉ dùng để giữ thứ tự chèn: `seq BIGINT GENERATED BY DEFAULT AS IDENTITY UNIQUE` | Cú pháp identity chuẩn SQL, H2 2.x và PostgreSQL ≥ 10 đều hỗ trợ (V1 đã dùng cho `audit_log`) |
| Backfill bằng `UPDATE` trong migration dùng literal, không gọi hàm thời gian | Tất định |
| Mọi `VARCHAR(n)` mới phải có `require(... .length <= n)` tương ứng trong route | Tránh 500 |

Runner chạy mọi migration còn thiếu trong **một** transaction. PostgreSQL rollback được DDL; H2 thì tự commit từng câu DDL, nên migration lỗi giữa chừng có thể để H2 ở trạng thái dở. Trên máy dev, xoá file `build/pxworld-dev.mv.db` (ở `build/` của gốc repo khi chạy bằng task `run`) để làm lại từ đầu. Test luôn dùng DB in-memory mới nên không bị ảnh hưởng.

---

## 5. Công thức: thêm staff role

### 5.1 Vai trò được lưu và truyền như thế nào

| Chỗ | Dạng |
|---|---|
| Bảng `accounts.roles` | Chuỗi các role nối bằng dấu phẩy, đã sort (`createAccount` sort), `VARCHAR(512)` |
| JWT | Claim `roles` (list) chép từ tài khoản lúc đăng nhập. **Đổi role chỉ có hiệu lực khi đăng nhập lại** (tối đa 12 giờ) |
| Server | `Caller.roles` đọc từ JWT; `requireRole` không tra DB |
| Console | `session.account.roles` lấy từ response đăng nhập, lưu trong `localStorage` |
| Tạo staff | Chỉ có `POST /admin/staff` (ADMIN). **Chưa có** API liệt kê, sửa role, vô hiệu hoá staff |

Role catalog trong [screen catalog](../screens/SCREEN_CATALOG.md) (`admin`, `dev`, `qa`, `creator`…) là **vai trò sản phẩm** dùng để phân loại màn, không phải role RBAC. Danh sách role RBAC mục tiêu nằm ở [MASTER_PLAN §3](../MASTER_PLAN.md#3-đối-tượng-sử-dụng-7-vai-trò-và-bề-mặt-sản-phẩm); code hiện có 6 trong 11 role staff của launch.

### 5.2 Các bước (ví dụ: thêm `staff_moderator` cho màn duyệt trận)

1. **Server, `Security.kt`**: thêm hằng và đưa vào `STAFF`. Tên role **phải bắt đầu bằng `staff_`** vì Console chỉ cho đăng nhập khi có role mang tiền tố này (`isStaff` trong `session.ts`).

```kotlin
object Roles {
    const val PLAYER = "player"
    const val ADMIN = "staff_admin"
    const val SUPPORT = "staff_support"
    const val LIVEOPS = "staff_liveops"
    const val CREATOR = "staff_creator"
    const val QA = "staff_qa"
    const val DEV = "staff_dev"
    const val MODERATOR = "staff_moderator"
    val STAFF: Set<String> = setOf(ADMIN, SUPPORT, LIVEOPS, CREATOR, QA, DEV, MODERATOR)
}
```

   Có trong `STAFF` thì tự động: tạo được qua `/admin/staff`, xem được `/admin/dashboard`, và không bị khoá qua `/admin/players/{id}/sanction`.

2. **Server, `Routes.kt`**: thêm role vào đúng các `requireRole`, ví dụ `call.requireRole(Roles.SUPPORT, Roles.LIVEOPS, Roles.MODERATOR)` cho danh sách và `call.requireRole(Roles.SUPPORT, Roles.MODERATOR)` cho duyệt. Duyệt lại **mọi** `requireRole` và quyết định có thêm role mới vào không; không thêm thì mặc định là cấm.

3. **Console, `session.ts`**: thêm vào object `Roles`:

```ts
export const Roles = {
  admin: "staff_admin",
  support: "staff_support",
  liveops: "staff_liveops",
  creator: "staff_creator",
  qa: "staff_qa",
  dev: "staff_dev",
  moderator: "staff_moderator",
} as const;
```

   Tự động theo: checkbox role trong trang Staff (`STAFF_ROLES = Object.values(Roles)`) và mục "Tổng quan" (roles = `Object.values(Roles)`).

4. **Console, `App.tsx`**: thêm role vào `roles` của mục `NAV` liên quan **và** vào `guard(...)` của route tương ứng. Hai chỗ phải khớp nhau và khớp server:

```tsx
  { path: "/anticheat", label: "Chống gian lận", roles: [Roles.support, Roles.liveops, Roles.moderator] },
```

```tsx
    case "anticheat":
      return guard(account, [...support, Roles.moderator], <BattleReviewPage canReview={hasAny(account, Roles.support, Roles.moderator)} />);
```

5. **Test server**: `client.staff(admin, "mod@pxworld.local", Roles.MODERATOR)` rồi khẳng định: được gọi endpoint mới, bị 403 ở `/admin/players?q=x`.

6. **Tài khoản staff đang có** (chưa có API): cập nhật trực tiếp DB rồi yêu cầu người đó đăng nhập lại. Giữ danh sách đã sort:

```sql
UPDATE accounts SET roles = 'staff_moderator,staff_support' WHERE email = 'someone@pxworld.local';
```

   Ghi lại thao tác tay này vào ticket, vì nó không đi qua `audit_log`.

7. Cập nhật bảng role ở [§1.4](#14-securitykt).

Ẩn menu ở Console **không phải** là bảo vệ. Bảo vệ thật là `requireRole` ở server; Console chỉ ẩn cho gọn.

---

## 6. Hợp đồng game ↔ server

### 6.1 Ai gọi gì

Game dùng port `CloudGateway` ([Cloud.kt](../../game/application/src/main/kotlin/com/pxworld/application/Cloud.kt)), adapter `HttpCloudGateway` ([HttpCloudGateway.kt](../../game/client/src/main/kotlin/com/pxworld/client/core/HttpCloudGateway.kt), `Gdx.net`, timeout 10 giây), use case `CloudSync`.

| `CloudGateway` | HTTP | Client đọc field |
|---|---|---|
| `guest(displayName)` | `POST /auth/guest` | `token`, `account.id`, `account.displayName`, `account.kind` |
| `login(email, password)` | `POST /auth/login` | như trên |
| `register(email, password, displayName)` | `POST /auth/register` | như trên |
| `saves(token)` | `GET /saves` | `slot`, `revision`, `updatedAt` |
| `download(token, slot)` | `GET /saves/{slot}` | `revision`, `updatedAt`, `body` |
| `upload(token, slot, expectedRevision, body)` | `PUT /saves/{slot}` | `slot`, `revision`, `updatedAt`; khi 409 đọc `current` |
| `mail(token)` | `GET /mail` | `id`, `subject`, `grants` (parse chuỗi thành object), `claimed` |
| `claimMail(token, mailId)` | `POST /mail/{id}/claim` | như trên |
| `telemetry(token?, clientVersion, events)` | `POST /telemetry` | không đọc body |

**Game hiện không gọi** `/battles/validate`, `/content/manifest`, `/content/packs/{version}` (đã grep toàn bộ `game/`). Ba endpoint này mới được `ServerTest` và console agent dùng. Khi nối vào game, giữ hợp đồng ở §6.4.

URL server: desktop đọc `PXWORLD_API_URL` (`off` để tắt; flavor `DEV` mặc định `http://localhost:8080`); Android lấy từ `BuildConfig.PXWORLD_API_URL` (debug mặc định `http://10.0.2.2:8080`). Thông tin đăng nhập lưu theo từng URL server (`PreferencesCredentialStore.preferencesFor(url)`).

### 6.2 Ánh xạ kết quả

`HttpCloudGateway.send` đổi mọi response thành `CloudResult`:

| Server trả | `CloudResult` |
|---|---|
| 2xx | `Ok(parse(body))`; body rỗng được coi là `{}` |
| 409 **và** path bắt đầu `/saves/` | `Conflict(current)` từ `SaveConflictView.current` |
| Mã khác | `Rejected(status, body.error ?: "HTTP <status>")` |
| Body không parse được (`IllegalArgumentException`) | `Unreachable("malformed response: …")` |
| Lỗi mạng, huỷ | `Unreachable(message)` |

`CloudSync.authorized` (dùng cho saves, download, upload, mail, claim): **401 → xoá thông tin đăng nhập** (`store.credentials = null`, xoá luôn các revision đã đồng bộ). 403 (bị khoá) không đăng xuất. Đây là lý do tài khoản đã bị xoá phải trả **401, không phải 403** ([§11.2](#112-lỗi-thường-gặp-rút-từ-các-lần-sửa-thật)).

Hợp đồng server không được phá (đổi thì phải sửa client cùng lúc):
- Tên field ở bảng 6.1. Client dùng `getValue(key)`: thiếu field là exception không được bắt.
- `MailRow.grants` là **chuỗi** JSON, không phải object.
- `ErrorView.error` là thông điệp hiển thị cho người chơi.
- Token sai, hết hạn, tài khoản không còn: 401. Bị khoá: 403. Lệch revision save: 409 kèm `current`.

### 6.3 Save có revision

| Bước | Client | Server |
|---|---|---|
| Tải lên | `upload(slot)` gửi `expectedRevision = store.syncedRevision(slot)` (0 nếu chưa từng đồng bộ) | Transaction: đọc revision hiện tại; khác `expectedRevision` → `null` → 409; bằng → ghi `revision + 1`, thêm `save_history`, xoá bản cũ hơn 30 revision |
| Thành công | `markSynced(slot, revision)` | |
| Xung đột | Mở màn `game.boot.save_conflict` | Trả `current` |
| Giữ bản máy | `keepLocal(slot, cloud)` gửi lại với `expectedRevision = cloud.revision` (ghi đè cloud) | |
| Lấy bản cloud | `takeCloud(slot)` tải, giải mã, lưu local, `markSynced` | |

Support khôi phục save (`/admin/players/{id}/saves/{slot}/restore`) tạo revision **mới**. Lần tải lên tiếp theo của game sẽ gặp 409 và người chơi chọn ở màn xung đột. Đây là hành vi mong muốn.

### 6.4 Xác thực trận (`POST /battles/validate`)

`ContentService.validateReplay(document)`:
1. `ReplayCodec.record(document)` đổi JSON thành `ReplayRecord`. Lỗi ở đây (outcome lạ, stat lạ, `lane`/`depth` ngoài 0..2) là exception → 400 và **không** ghi `battle_validations`.
2. Kiểm tra hợp lệ đội hình: 1–6 slot, không trùng ô, không trùng hero, `level` 1..100, `star` 0..6, mỗi `bonus` 0..100.000. Vi phạm → `ReplayVerdict(false, "REJECTED", 0, reasons)`.
3. Chạy lại: `BattleEngine.replay(BattleContentAssembler(bundle).battle(seed, lineup, encounterId), commands)`. Engine ném lỗi (hero, trận, kỹ năng không tồn tại, lệnh sai luật) → `ReplayVerdict(false, "ILLEGAL", 0, [message])`.
4. So `outcome` và `rounds` khai báo với kết quả chạy lại; khác → `valid = false`, `replayedOutcome` là kết quả thật (`VICTORY`/`DEFEAT`/`DRAW`, hoặc `UNFINISHED` nếu trận chưa kết thúc).

Route ghi mọi verdict vào `battle_validations`. Định dạng lệnh: `actor`/`target` là `ally#<slot>` hoặc `enemy#<slot>` (`UnitId.toString()`).

Lưu ý hợp đồng: server chạy lại bằng content **đang nạp trong server** (`services.content.bundle`, đổi được qua Studio), không phải bản release mà client đang chơi, và `ReplayDocument` không mang version content. Khi nối game vào endpoint này, cần thêm version content vào hợp đồng.

### 6.5 Telemetry chỉ khi người chơi đồng ý

| Chỗ | Luật |
|---|---|
| `GameApp.track` | Chỉ ghi vào `TelemetryBuffer` khi có cloud **và** `context.preferences.analyticsConsent` (màn `game.boot.privacy_consent` và cài đặt) |
| `TelemetryMapping.of` | 6 sự kiện: `battle.end`, `quest.complete`, `hero.recruit`, `profile.level_up`, `map.enter`, `checkin.claim`; payload chỉ gồm chuỗi |
| `TelemetryBuffer` | Tối đa 200, đầy thì bỏ sự kiện cũ nhất |
| `GameApp.flushTelemetry` | Mỗi 30 giây gửi tối đa 50 sự kiện, mỗi lần một request; thất bại thì trả lại buffer |
| Server | Tối đa 200 sự kiện/batch, tên khớp `^[a-z][a-z0-9_.]{1,63}$`, token tuỳ chọn |

Thêm sự kiện mới: thêm nhánh trong `TelemetryMapping`, tên chữ thường có dấu chấm. Không đưa email, tên hiển thị hay dữ liệu cá nhân vào payload.

Telemetry explorer trong Console (`/admin/telemetry/*`) **đang làm — WP-TELEMETRY** (WP-A4 trong [05](05-work-packages.md), nhánh khác). Chương này không mô tả giao diện của nó; khi merge, thêm vào §2.

---

## 7. Giải phẫu Console

### 7.1 Cấu trúc

| File | Nội dung |
|---|---|
| [vite.config.ts](../../web/apps/console/vite.config.ts) | Plugin React; dev server cổng 5173; proxy `/api` → `process.env.PXWORLD_API ?? "http://localhost:8080"`, bỏ tiền tố `/api`. `vite preview` dùng lại proxy này |
| [package.json](../../web/apps/console/package.json) | `dev`, `build` (`tsc --noEmit && vite build`), `typecheck`, `preview`. React 19, Vite 7, TypeScript 5.9 |
| [tsconfig.json](../../web/apps/console/tsconfig.json) | `strict`, `noUnusedLocals`, `noUnusedParameters`, `noFallthroughCasesInSwitch` |
| [main.tsx](../../web/apps/console/src/main.tsx) | `createRoot` + `StrictMode` + `styles.css` |
| [App.tsx](../../web/apps/console/src/App.tsx) | Session, danh sách `NAV`, router theo hash, `guard` |
| [session.ts](../../web/apps/console/src/session.ts) | `Roles`, lưu session trong `localStorage` (khoá `pxworld.console.session`), bỏ session có `exp` đã qua, `hasAny`, `isStaff` |
| [api.ts](../../web/apps/console/src/api.ts) | Kiểu dữ liệu, `ApiError`, `request()`, object `api` |
| [ui.tsx](../../web/apps/console/src/ui.tsx) | `Page`, `useLoad`, `Status`, `Notice`, `useAction`, `IssueList`, `Stat`, `when`, `link`, `messageOf` |
| [styles.css](../../web/apps/console/src/styles.css) | Token màu sáng/tối và mọi class |
| `src/pages/*.tsx` | Các trang (bảng 7.5) |
| [web/package.json](../../web/package.json) | **npm workspaces** (`packages/*`, `apps/*`); script `console:dev`, `console:build`, `typecheck` |

Package `@pxworld/screen-catalog` ([screenIds.ts](../../web/packages/screen-catalog/src/screenIds.ts)) export `webScreens` và union `WebScreenId`, sinh từ [catalog.mjs](../../tools/screen-catalog/catalog.mjs). **Không sửa tay** file này.

### 7.2 Session, router và phân quyền

- Khởi động: `loadSession()` đọc `localStorage`, bỏ nếu `exp` trong JWT đã qua, rồi `setToken(...)`.
- `api.request` gặp **401 khi đang có token** → gọi mọi listener của `onUnauthorized` → `App` đặt `expired = true` và đăng xuất; trang đăng nhập hiện "Phiên đã hết hạn".
- `LoginPage` gọi `api.login`; tài khoản không có role `staff_*` bị từ chối ngay ở client.
- Router: `route(path)` tách `location.hash` theo `/` và `decodeURIComponent` từng đoạn, `switch` theo đoạn đầu.

| Hash | Trang | Quyền (`guard`) |
|---|---|---|
| `#/` | `DashboardPage` | mọi staff |
| `#/players`, `#/players/<id>` | `PlayersPage`, `PlayerDetailPage` | support, liveops |
| `#/audit` | `AuditPage` | support, liveops |
| `#/content` | `ContentReleasesPage` (`canPublish` = liveops/dev, `canPromote` = liveops) | liveops, dev, creator, qa |
| `#/studio`, `#/studio/<kind>`, `#/studio/<kind>/<id>`, `#/studio/<kind>/__new` | `StudioHomePage`, `StudioKindPage` | creator, dev |
| `#/qa/runs`, `#/qa/runs/<id>` | `AgentRunsPage`, `AgentRunDetailPage` | qa, dev |
| `#/staff` | `StaffPage` | admin |
| khác | trang "Không tìm thấy" (`console.auth.no_access`) | |

`hasAny(account, ...roles)` trả `true` nếu có `staff_admin` hoặc một trong các role, giống `Caller.has` ở server. `guard` hiện trang "Không có quyền" (`console.auth.no_access`) khi sai role. Quyền phải khai **ba chỗ khớp nhau**: `NAV[].roles`, `guard(...)` trong `route`, và `requireRole` ở server. Nút hành động bên trong trang (ví dụ nút Khoá) cũng phải gate bằng `hasAny` theo đúng role ghi của server.

### 7.3 `api.ts`

- `base = import.meta.env.VITE_PXWORLD_API ?? "/api"`. Mặc định đi qua proxy của Vite nên không cần CORS. Đặt `VITE_PXWORLD_API` lúc build để gọi thẳng server; khi đó host của Console phải có trong `PXWORLD_CORS_HOSTS`.
- `request<T>(method, path, body?, raw = false)`: gắn `Authorization: Bearer`; có body thì gắn `Content-Type: application/json`; lỗi thì ném `ApiError(status, message = body.error, validation = body.detail)`; `raw = true` trả text thô (dùng cho báo cáo agent).
- Mọi lời gọi server nằm trong object `api`; trang **không** gọi `fetch` trực tiếp. Tham số path luôn qua `q = encodeURIComponent`.

### 7.4 `ui.tsx`

| Thành phần | Hợp đồng |
|---|---|
| `Page({ screen, title, actions?, children })` | `screen: WebScreenId` (ID ngoài catalog **không biên dịch được**); đặt `document.title`; render `<section class="page" data-screen-id=…>` |
| `useLoad(load, deps)` | `{ data, error, loading, reload }`; chạy lại khi `deps` hoặc `reload()` đổi; bỏ kết quả của request cũ |
| `Status({ state, children })` | Lỗi → `Notice` đỏ; `data === undefined` → "Đang tải…"; còn lại gọi `children(data)`. Khi `reload`, dữ liệu cũ vẫn hiện tới khi có dữ liệu mới |
| `useAction()` | `{ busy, run, feedback, setResult }`. `run(async () => "thông báo")`: trả chuỗi → thông báo xanh; ném lỗi → thông báo đỏ, kèm `IssueList` nếu là 422 |
| `Notice({ tone })` | `error` / `ok` / `info`; `role="alert"` cho lỗi |
| `IssueList`, `Stat`, `when(ms)` (định dạng `vi-VN`), `link(path)` (`#` + path), `messageOf(e)` | tiện ích |

`data-screen-id` có thể đặt thêm trên phần tử con để đánh dấu màn con (ví dụ `console.players.player_grant` trên form tặng quà). Console agent ghi nhận **mọi** `data-screen-id` đang hiển thị.

### 7.5 Trang hiện có

| File | Component | ScreenId | Gọi API |
|---|---|---|---|
| `LoginPage.tsx` | `LoginPage` | `console.auth.login` (trên `<main>`, không dùng `Page`) | `login` |
| `DashboardPage.tsx` | `DashboardPage` | `console.dashboards.overview` | `dashboard` |
| `PlayersPage.tsx` | `PlayersPage`, `banState` | `console.players.player_search` | `players` (gọi khi bấm Tìm, không dùng `useLoad`) |
| `PlayerDetailPage.tsx` | `PlayerDetailPage`, `SaveHistory`, `GrantForm`, `SanctionForm`, `AuditTable` | `console.players.player_overview`; con: `player_saves`, `player_grant`, `player_sanctions`, `player_audit` | `player`, `saveHistory`, `restoreSave`, `grant`, `sanction`, `lift` |
| `OperationsPages.tsx` | `AuditPage` | `console.operations.audit_log` | `audit` |
| | `ContentReleasesPage` + `ReleaseDiff` | `console.operations.deployments`; con: `console.operations.content_release_diff` | `releases`, `channels`, `publish`, `promote` (hỏi xác nhận khi lên prod), `diff` |
| | `AgentRunsPage`, `AgentRunDetailPage` | `qa.quality.agent_runs`, `qa.quality.agent_run_detail` | `agentRuns`, `agentRun` (raw) |
| | `StaffPage` | `console.liveops_entities.staff_users.editor` | `createStaff` |
| `StudioPages.tsx` | `StudioHomePage`, `StudioKindPage`, `RecordEditor` | `studio.editors.studio_home`; `studio.content_entities.<kind>.list` / `.editor` | `kinds`, `records`, `schema`, `upsert` |
| `SchemaForm.tsx` | `SchemaField`, `ObjectField`, `ArrayField`, `MapField`, `unwrap`, `defaultFor` | | |

### 7.6 `styles.css`

Token trên `:root` (sáng), ghi đè trong `@media (prefers-color-scheme: dark)`:

| Token | Dùng cho |
|---|---|
| `--bg`, `--surface`, `--surface-2` | Nền trang, thẻ/bảng, nền phụ (header bảng, chip, tab đang chọn) |
| `--text`, `--muted`, `--border` | Chữ, chữ phụ, viền |
| `--accent`, `--accent-text` | Link, nút `primary` |
| `--ok`/`--ok-bg`, `--bad`/`--bad-bg`, `--info`/`--info-bg`, `--warn` | Trạng thái |
| `--radius` (8px), `--gap` (16px) | Bo góc, khoảng cách |

Class dùng lại, không tạo class trùng chức năng: `.page-head`, `.page-actions`, `.buttons`, `.stats`/`.stat`, `.notice-{error,ok,info}`, `.badge`, `.badge-{ok,bad,info}`, `.inline-form`, `.card`, `.narrow`, `.row`, `.columns`, `.facts`, `.plain`, `.tiles`, `.chips`, `.split`, `.record-list`, `.editor`, `.tabs`, `.schema-form`, `.field-row`, `.meter`, `.error-block`, `.diff-added`, `.diff-removed`, `.num` (cột số căn phải), `.muted`, `button.primary`, `button.danger`, `button.icon`, `label.check`.

Màn hẹp: `@media (max-width: 860px)` xếp sidebar lên trên, `.columns`, `.split`, `.row`, `.object-row` thành một cột, bảng cuộn ngang. Trang mới dùng các class trên thì tự chạy được ở bề rộng điện thoại; màu **chỉ** lấy từ token để tự có chế độ tối.

---

## 8. Công thức: thêm trang Console

Ví dụ xuyên suốt: trang "Trận bị từ chối". Code TypeScript trong mục này đã được kiểm bằng `tsc --noEmit` trên một bản sao của `web/apps/console`; bước console agent đã kiểm cú pháp bằng `node --check`.

### Bước 1 · Chọn `WebScreenId`

1. Tìm ID có sẵn trong [SCREEN_CATALOG.md](../screens/SCREEN_CATALOG.md) hoặc `screenIds.ts`. Ví dụ dùng `console.anticheat.battle_validation_failures` (có sẵn, mùa `launch`).
2. Chỉ khi thật sự không có ID phù hợp: thêm dòng vào nhóm tương ứng trong [catalog.mjs](../../tools/screen-catalog/catalog.mjs) (hoặc [expansion.mjs](../../tools/screen-catalog/expansion.mjs) cho màn theo mùa), chạy `node tools/screen-catalog/catalog.mjs` để sinh lại `docs/screens/*`, `GameScreenId.kt` và `screenIds.ts`, rồi commit cả các file sinh ra. CI chạy `node tools/screen-catalog/catalog.mjs --check` và báo lỗi nếu file sinh ra bị cũ. Thêm màn làm đổi tổng số màn trong MASTER_PLAN, nên phải được duyệt.

### Bước 2 · Kiểu và hàm API trong `api.ts`

Thêm interface (field nullable của server khai báo `?`):

```ts
export interface BattleValidation {
  id: string;
  accountId: string;
  encounterId: string;
  claimedOutcome: string;
  replayedOutcome: string;
  valid: boolean;
  createdAt: number;
  reviewedBy?: string;
  reviewedAt?: number;
  reviewNote?: string;
}
```

Thêm vào cuối object `api`:

```ts
  rejectedBattles: () => request<BattleValidation[]>("GET", "/admin/battles/rejected"),
  reviewBattle: (id: string, reason: string) => request<BattleValidation>("POST", `/admin/battles/${q(id)}/review`, { reason }),
```

### Bước 3 · Viết trang

File mới `web/apps/console/src/pages/AnticheatPages.tsx`:

```tsx
import { useState } from "react";
import { api, type BattleValidation } from "../api";
import { Page, Status, link, useAction, useLoad, when } from "../ui";

export function BattleReviewPage({ canReview }: { canReview: boolean }) {
  const state = useLoad(api.rejectedBattles, []);
  const [reason, setReason] = useState("");
  const action = useAction();

  const review = (battle: BattleValidation) =>
    action.run(async () => {
      await api.reviewBattle(battle.id, reason);
      setReason("");
      state.reload();
      return `Đã duyệt trận ${battle.id.slice(0, 8)}.`;
    });

  return (
    <Page screen="console.anticheat.battle_validation_failures" title="Trận bị từ chối" actions={<button onClick={state.reload}>Làm mới</button>}>
      {canReview && (
        <div className="card narrow">
          <label>
            Kết luận khi duyệt (bắt buộc, ghi vào audit)
            <input name="review-reason" value={reason} onChange={(event) => setReason(event.target.value)} />
          </label>
        </div>
      )}
      {action.feedback}
      <Status state={state}>
        {(rows) =>
          rows.length === 0 ? (
            <p className="muted">Không có trận bị từ chối.</p>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>Lúc</th>
                  <th>Người chơi</th>
                  <th>Trận</th>
                  <th>Khai báo → server</th>
                  <th>Trạng thái</th>
                  {canReview && <th />}
                </tr>
              </thead>
              <tbody>
                {rows.map((battle) => (
                  <tr key={battle.id}>
                    <td>{when(battle.createdAt)}</td>
                    <td>
                      <a href={link(`/players/${battle.accountId}`)}>
                        <code>{battle.accountId.slice(0, 8)}</code>
                      </a>
                    </td>
                    <td>
                      <code>{battle.encounterId}</code>
                    </td>
                    <td>
                      {battle.claimedOutcome} → <span className="badge badge-bad">{battle.replayedOutcome}</span>
                    </td>
                    <td>
                      {battle.reviewedAt ? (
                        <span className="badge badge-ok" title={battle.reviewNote}>
                          đã duyệt {when(battle.reviewedAt)}
                        </span>
                      ) : (
                        <span className="badge badge-info">chờ duyệt</span>
                      )}
                    </td>
                    {canReview && (
                      <td>
                        {!battle.reviewedAt && (
                          <button disabled={action.busy || !reason.trim()} onClick={() => review(battle)}>
                            Duyệt
                          </button>
                        )}
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          )
        }
      </Status>
    </Page>
  );
}
```

Điểm bắt buộc, trang nào cũng phải có:
- **Tải dữ liệu** bằng `useLoad` và hiển thị qua `Status`, nên tự có trạng thái "Đang tải…" và lỗi.
- **Trạng thái rỗng** có câu riêng (`Không có …`).
- **Hành động** qua `useAction().run`, nút `disabled={action.busy || <input chưa đủ>}`, hiện `action.feedback`, gọi `reload()` sau khi ghi.
- **Lý do bắt buộc** ở client (nút khoá khi rỗng) **và** ở server (`require`). Client chặn chỉ để tiện; server mới là chốt chặn.
- Nút ghi chỉ hiện khi có role ghi (`canReview`), tính ở `App.tsx` bằng `hasAny`.
- Chữ hiển thị tiếng Việt; không `console.error` (console agent coi mọi `console.error` là lỗi); không dùng `dangerouslySetInnerHTML`.

### Bước 4 · Nối vào `App.tsx`

```tsx
import { BattleReviewPage } from "./pages/AnticheatPages";
```

Thêm vào `NAV` (sau mục "Nhật ký"):

```tsx
  { path: "/anticheat", label: "Chống gian lận", roles: [Roles.support, Roles.liveops] },
```

Thêm vào `switch` trong `route`:

```tsx
    case "anticheat":
      return guard(account, support, <BattleReviewPage canReview={hasAny(account, Roles.support)} />);
```

### Bước 5 · Kiểm tra kiểu và build

```bash
cd web
npm run typecheck -w @pxworld/console
npm run console:build
```

`console:build` chạy `tsc --noEmit` rồi `vite build` ra `web/apps/console/dist` (`vite preview` và `run-console.sh` phục vụ thư mục này).

### Bước 6 · Thêm bước console agent

Console agent ([console-agent.mjs](../../tools/test-agent/console/console-agent.mjs)) điều khiển Chromium/Edge headless qua CDP. API của nó:

| Hàm | Hành vi |
|---|---|
| `api(method, path, token, body)` | Gọi thẳng server (không qua trình duyệt) để chuẩn bị dữ liệu; mã khác 2xx thì ném lỗi |
| `agent.step(label, async () => …)` | Ghi một bước vào `trace`; lỗi thì dừng kịch bản, chụp `failure` |
| `agent.go(hash)` | Đặt `location.hash` |
| `agent.screen(id)` | Chờ tối đa 10 giây tới khi có `[data-screen-id=id]` và không còn chữ "Đang tải…"; ghi nhận mọi screen id đang hiển thị |
| `agent.fill(selector, value)` | Gán giá trị qua native setter rồi phát sự kiện `input` (hợp với input có kiểm soát của React) |
| `agent.click(text, scope = "body")` | Chờ rồi bấm `button` hoặc `a` **đang bật** có `textContent.trim()` **đúng bằng** `text` trong `scope` |
| `agent.expectText(text, timeout?)` | Chờ `document.body.innerText` chứa `text` |
| `agent.browser.waitFor(expression, label, timeout = 10000)` | Chờ biểu thức JS trong trang trả truthy |
| `agent.browser.evaluate(expression)` | Chạy JS trong trang, trả giá trị |
| `agent.shot(label)` | Chụp PNG vào thư mục báo cáo, đánh số thứ tự |

Thêm bước ngay **trước** bước `"staff form and sign out"` (lúc đó người chơi của kịch bản đã được gỡ khoá, và admin vẫn đang đăng nhập):

```js
  await agent.step("anticheat review", async () => {
    const forged = { id: "agent-forged", encounterId: "encounter.agent", seed: 1, lineup: [{ heroId: "hero.agent", level: 999, star: 0, lane: 0, depth: 0, bonus: {} }], commands: [], outcome: "VICTORY", rounds: 1, recordedAtMillis: 0 };
    const verdict = await api("POST", "/battles/validate", player.token, forged);
    if (verdict.replayedOutcome !== "REJECTED") throw new Error(`forged replay was not rejected: ${JSON.stringify(verdict)}`);
    await agent.go("/anticheat");
    await agent.screen("console.anticheat.battle_validation_failures");
    await agent.expectText("chờ duyệt");
    await agent.fill("input[name=review-reason]", "console agent review");
    await agent.click("Duyệt");
    await agent.expectText("Đã duyệt trận");
    await agent.shot("anticheat");
  });
```

Luật viết bước:
- Selector ưu tiên `name`, `aria-label` hoặc `[data-screen-id='…'] input`; không dựa vào class CSS hay vị trí.
- Chữ trên nút phải **duy nhất** trong `scope`, nếu không thì truyền `scope`.
- Các bước dùng chung trạng thái (người chơi tạo ở đầu kịch bản, bị khoá rồi gỡ khoá, Studio sửa `shop.price` của bản ghi `items` đầu tiên và bước phát hành kỳ vọng diff `~ item.cup_t1 shop`). Bước mới không được làm đổi dữ liệu mà bước sau kiểm tra.
- Kịch bản FAIL nếu trang có exception hoặc `console.error` (`pageErrors`).

### Bước 7 · Chạy và nghiệm thu

```bash
tools/test-agent/console/run-console.sh --out="$PWD/agent-reports/console"
```

Nghiệm thu: dòng cuối `PASSED console — N/N steps`, `report.json` có `"status": "passed"` và `pageErrors: []`, `coverage.visited` chứa screen id mới, ảnh chụp đúng giao diện ở cả chế độ sáng.

---

## 9. Công thức: Studio sửa theo schema

### 9.1 Luồng

1. `StudioHomePage` gọi `GET /studio/kinds` và hiện ô cho từng kind.
2. `StudioKindPage(kind, selected?)` gọi song song `GET /studio/kinds/{kind}` (bản ghi) và `GET /studio/kinds/{kind}/schema`. Danh sách bên trái lọc theo chuỗi con của JSON.
3. `RecordEditor` giữ **một nguồn sự thật là chuỗi JSON** (`text`). Tab "Biểu mẫu" parse `text` và render `ObjectField` theo schema; mỗi thay đổi trên form ghi lại `text` bằng `JSON.stringify(next, null, 2)`. Tab "JSON" sửa thẳng `text`. JSON đang lỗi thì tab Biểu mẫu hiện thông báo và cho sửa ở tab JSON.
4. Nút **Kiểm tra** gửi `PUT …?dryRun=true`; **Lưu** gửi `PUT` thật (chỉ bật khi `text` khác bản gốc); **Hoàn tác** trả `text` về bản gốc.
5. Mỗi khi `text` đổi, thông báo kết quả cũ bị xoá (`useEffect(() => setResult(undefined), [text, setResult])`), để người dùng không thấy "Hợp lệ" của phiên bản trước.
6. Bản ghi mới (`#/studio/<kind>/__new`): giá trị khởi tạo là `defaultFor(schema)` cộng `id: "<kind bỏ chữ s cuối>.new"`.

ScreenId: `studio.content_entities.<kind>.list` khi chưa chọn bản ghi, `.editor` khi đã chọn. Kind không có trong catalog (hiện chỉ `balance`) rơi về `studio.editors.studio_home`.

### 9.2 Server phục vụ schema và lưu bản ghi

- `GET /studio/kinds/{kind}/schema` = `ContentSchema.record(ContentKinds.byDirectory(kind))` ([ContentSchema.kt](../../game/content/src/main/kotlin/com/pxworld/content/ContentSchema.kt)). Schema sinh từ `SerialDescriptor` của kotlinx.serialization:

| Kiểu Kotlin | JSON Schema |
|---|---|
| `String`, `Char` | `{"type":"string"}` |
| `Int`, `Long`, `Short`, `Byte` | `{"type":"integer"}` |
| `Float`, `Double` | `{"type":"number"}` |
| `Boolean` | `{"type":"boolean"}` |
| `enum` | `{"type":"string","enum":[…]}` |
| `List<T>` | `{"type":"array","items":T}` |
| `Map<String, T>` | `{"type":"object","additionalProperties":T}` |
| data class | `{"type":"object","properties":{…},"required":[field không có mặc định],"additionalProperties":false}` |
| `T?` | `{"anyOf":[T, {"type":"null"}]}` |

   Cấp ngoài cùng thêm `$schema` (draft 2020-12) và `title` = tên kind. Muốn đổi schema thì đổi record class trong `game/content` (xem [09](09-recipes-content.md)); server và Studio tự theo.
- `PUT /studio/kinds/{kind}` → `ContentService.upsert`:
  1. Cần `id`.
  2. Tìm file đang chứa `id` trong thư mục kind; không có thì chọn file đầu tiên của kind (thứ tự duyệt thư mục).
  3. Thay bản ghi tại chỗ, hoặc nối vào cuối file.
  4. In lại file (thụt 2 dấu cách, xuống dòng cuối), validate **toàn bộ** cây content với file mới.
  5. Có lỗi → 422 kèm `ValidationView`. Cảnh báo không chặn.
  6. `dryRun` → trả validation, không ghi.
  7. Không dry-run: `PXWORLD_CONTENT_WRITABLE` phải `true` (không thì 409), ghi file, nạp lại `bundle`, ghi audit `studio.upsert`.
- Lưu xong **chưa** tạo release. Muốn game thấy thay đổi: `POST /content/releases` rồi promote ([§2.6](#26-phát-hành-content-staff)).
- `PXWORLD_CONTENT_WRITABLE` mặc định `true` chỉ ở `dev`. Test và console agent luôn chạy trên **bản sao** content ([§11.2](#112-lỗi-thường-gặp-rút-từ-các-lần-sửa-thật)).

### 9.3 `SchemaForm.tsx` render thế nào

| Schema (sau `unwrap`) | Control | Chi tiết |
|---|---|---|
| `anyOf` chứa `{type:"null"}` | Checkbox "có giá trị" | Giá trị `null` chỉ hiện checkbox; tích vào thì gán `defaultFor(kiểu thật)` |
| có `enum` | `<select>` | |
| `string` | `<input>` | |
| `integer` / `number` | `<input type="number">` | `integer` bị `Math.trunc`; giá trị không phải số hiển thị 0 |
| `boolean` | Checkbox "có/không" | |
| `array` | `ArrayField` | Mỗi phần tử một dòng, nút `×` xoá, "+ Thêm" nối `defaultFor(items)` |
| `object` có `properties` | `ObjectField` | Field `required` luôn hiện; field tuỳ chọn có checkbox "tuỳ chọn" (tích = thêm với giá trị mặc định, bỏ tích = xoá khoá) |
| `object` không có `properties` (map) | `MapField` | Ô tên khoá đổi tên khi rời ô (bỏ qua nếu rỗng hoặc trùng), "+ Thêm khoá" tạo `key<n>` |
| khác | `<code>` chỉ đọc | |

`defaultFor`: nullable → `null`; enum → phần tử đầu; `""`, `0`, `false`, `[]`; object → chỉ các khoá `required` với giá trị mặc định; map → `{}`. Mỗi control có `aria-label` là đường dẫn JSON (`$.shop.price`, `$.rewards[0]`); console agent dùng đúng các nhãn này làm selector.

### 9.4 Nền cho màn "matrix" (đề xuất)

Catalog có ma trận `studio.content_entities.<entity>.{list, detail, editor, history, bulk, promote}` cho 40 thực thể; 16 trong 17 kind hiện có trùng tên thực thể (thiếu `balance`). [WP-B1](05-work-packages.md) sẽ làm ma trận này. Ánh xạ đề xuất, khớp WP-B1:

| View | ScreenId | Route (đề xuất) | Dữ liệu | Hiện trạng |
|---|---|---|---|---|
| list | `studio.content_entities.<kind>.list` | `#/studio/<kind>/list` | `GET /studio/kinds/{kind}` | Có (route hiện tại `#/studio/<kind>`) |
| editor | `…editor` | `#/studio/<kind>/editor/<id>` | `…/schema` + `PUT` | Có (route hiện tại `#/studio/<kind>/<id>`) |
| detail | `…detail` | `#/studio/<kind>/detail/<id>` | Cần endpoint tham chiếu ngược (đề xuất) | Chưa có |
| history | `…history` | `#/studio/<kind>/history/<id>` | Đề xuất `GET /studio/kinds/{kind}/records/{id}/history` dựng từ các release bằng `ContentDiff` | Chưa có |
| bulk | `…bulk` | `#/studio/<kind>/bulk` | Đề xuất `POST /studio/kinds/{kind}/bulk` có `dryRun`, validate cả lô một lần | Chưa có |
| promote | `…promote` | `#/studio/<kind>/promote` | `GET /content/releases/{from}/diff/{to}` lọc theo kind + `POST /content/promote` | Chưa có |

Hàm chọn screen id tổng quát (đề xuất), mở rộng từ `entityScreen` đang có trong `StudioPages.tsx`:

```ts
export type EntityView = "list" | "detail" | "editor" | "history" | "bulk" | "promote";

const registered = new Set<string>(webScreens.map((screen) => screen.id));

export const entityScreenId = (kind: string, view: EntityView): WebScreenId => {
  const id = `studio.content_entities.${kind}.${view}`;
  return registered.has(id) ? (id as WebScreenId) : "studio.editors.studio_home";
};
```

Một trang thực thể tổng quát nhận `(kind, view, id?)`, lấy schema một lần và dùng lại `ObjectField` cho `editor`, bảng sinh từ các field cấp một kiểu chuỗi/số/enum cho `list`, và `IssueList` cho kết quả dry-run. Kind cần tên thực thể khác (ví dụ `localization` → `loc_keys`) thì đặt bảng ánh xạ cạnh hàm trên.

---

## 10. Chạy và test cục bộ

Hướng dẫn dựng máy đầy đủ ở [07](07-setup-and-environments.md). Lệnh bash dưới đây chạy trong Git Bash trên Windows hoặc shell Linux/macOS, tại gốc repo.

### 10.1 Server với H2 (mặc định)

```bash
./gradlew --settings-file settings-test.gradle :server:app:run
```

Task `run` chạy ở gốc repo, nên `content/`, `assets/` và file H2 `build/pxworld-dev.mv.db` đều tính từ gốc repo. Cổng 8080, env `dev`, admin `admin@pxworld.local` / `admin-dev-password`. Kiểm tra: `curl http://localhost:8080/health`.

Hoặc bản cài đặt (cách `run-console.sh` và `run-desktop.sh` dùng):

```bash
./gradlew --settings-file settings-test.gradle :server:app:installDist
PXWORLD_PORT=18080 server/app/build/install/app/bin/app
```

Trên Windows ngoài Git Bash dùng `server\app\build\install\app\bin\app.bat`. Phải chạy tại gốc repo, hoặc đặt `PXWORLD_CONTENT_DIR` và `PXWORLD_LEGACY_ASSETS_DIR` bằng đường dẫn tuyệt đối.

DB in-memory (mất khi tắt, giống test): `PXWORLD_DB_URL="jdbc:h2:mem:dev;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"`.

### 10.2 PostgreSQL thay vì H2

Repo **chưa có** docker-compose. Với một PostgreSQL tự dựng:

```bash
PXWORLD_DB_URL=jdbc:postgresql://localhost:5432/pxworld \
PXWORLD_DB_USER=pxworld PXWORLD_DB_PASSWORD=pxworld \
./gradlew --settings-file settings-test.gradle :server:app:run
```

Cùng file migration. Ngoài `dev` phải đặt thêm `PXWORLD_JWT_SECRET`, và `PXWORLD_ADMIN_EMAIL`/`PXWORLD_ADMIN_PASSWORD` nếu cần admin đầu tiên.

### 10.3 Console

```bash
cd web
npm ci
npm run console:dev
```

Mở `http://localhost:5173`. Proxy `/api` trỏ `http://localhost:8080`; server ở cổng khác thì chạy `PXWORLD_API=http://localhost:18080 npm run console:dev`. Bản build + preview (cổng 4173): `npm run console:build` rồi `PXWORLD_API=http://localhost:18080 npx vite preview apps/console --port 4173`.

### 10.4 Test

| Việc | Lệnh |
|---|---|
| Test server | `./gradlew --settings-file settings-test.gradle :server:app:test` |
| Một test | `./gradlew --settings-file settings-test.gradle :server:app:test --tests "com.pxworld.server.ServerTest.studioValidatesBeforeWritingAndLiveopsPromotes"` |
| Luật kiến trúc | `./gradlew --settings-file settings-test.gradle :tools:architecture:test` |
| Catalog không cũ | `node tools/screen-catalog/catalog.mjs --check` |
| Kiểu Console | `cd web && npm run typecheck -w @pxworld/console` |
| Console agent | `tools/test-agent/console/run-console.sh --out=<thư mục>` |
| Game ↔ server thật | `SCENARIO=cloud tools/test-agent/run-desktop.sh "$PWD/agent-reports/cloud"` |
| Cổng test chung trước merge | [05 §0.3](05-work-packages.md) |

`run-console.sh` làm lần lượt: `./gradlew :server:app:installDist`; `npm ci` (khi `CI` được đặt hoặc chưa có `web/node_modules`) và `npm run console:build`; chép `content/` sang thư mục tạm; chạy server trên H2 in-memory ở cổng `PXWORLD_API_PORT` (mặc định 18080) với `PXWORLD_CONTENT_WRITABLE=true` trên bản sao; chạy `vite preview` ở cổng `PXWORLD_CONSOLE_PORT` (mặc định 4173); chờ `/health`; chạy agent. Log nằm ở `${TMPDIR:-/tmp}/pxworld-console-server.log` và `pxworld-console-web.log`. Tham số thêm được chuyển thẳng cho agent: `--out=<dir>` (mặc định `tools/test-agent/reports/console-<thời điểm>`), `--browser=<đường dẫn>` (hoặc biến `PXWORLD_BROWSER`; mặc định dò Edge, Chrome, Chromium), `--email`, `--password`, `--upload` (tải báo cáo lên `/qa/agent-runs` bằng tài khoản admin). Kết quả: `report.json` và ảnh `NN-<nhãn>.png`.

Không chạy hai phiên console agent cùng lúc trên một máy: cổng 18080 và 4173 cố định, và `vite preview` dùng `--strictPort`.

---

## 11. Checklist bảo mật, lỗi thường gặp, vấn đề đã biết

### 11.1 Checklist cho mỗi thay đổi server/Console

**Server**
- [ ] Route mới nằm trong `authenticate(AUTH)`, trừ khi cố ý công khai. Công khai hiện tại: `/health`, `/metrics`, `/auth/*`, `/content/manifest`, `/content/packs/{version}`, `/telemetry` (token tuỳ chọn).
- [ ] Route staff gọi `requireRole(...)` ở dòng đầu. Không dựa vào việc Console ẩn menu.
- [ ] Route người chơi dùng `activeCaller()` (tài khoản còn tồn tại, không bị khoá) và chỉ lấy account id từ token.
- [ ] Mọi input có `require` (400), gồm độ dài theo cột `VARCHAR`, khoảng số, regex cho id/slot.
- [ ] SQL chỉ dùng tham số `?`; không truyền input vào `Repositories.count(where = …)`.
- [ ] Mọi thao tác ghi của staff gọi `repositories.audit(...)`; thao tác ảnh hưởng người chơi hoặc kinh tế bắt buộc `reason` không rỗng; payload dựng bằng `JsonObject`.
- [ ] Không log mật khẩu, token, body save.
- [ ] Giới hạn kích thước body hoặc số phần tử (tham khảo `MAX_SAVE_BYTES`, `MAX_REPORT_BYTES`, `MAX_TELEMETRY_BATCH`).
- [ ] Validate **toàn bộ** input trước khi ghi dòng đầu tiên (không ghi dở dang rồi mới báo 400).
- [ ] Không có exception nào có thể thoát ra thành 500 với input do người dùng kiểm soát.
- [ ] Test có ca 401/403 cho role sai và ca audit.
- [ ] Ngoài `dev`: `PXWORLD_JWT_SECRET` riêng, `PXWORLD_CONTENT_WRITABLE=false`, `PXWORLD_CORS_HOSTS` đúng domain Console.

**Console**
- [ ] Quyền khai đủ ba chỗ (NAV, `guard`, server) và nút ghi gate bằng `hasAny`.
- [ ] Gọi server qua `api.ts`, tham số path qua `q(...)`.
- [ ] Không lưu gì ngoài session vào `localStorage`; không `dangerouslySetInnerHTML`.
- [ ] Thao tác phá huỷ hoặc lên prod có bước xác nhận (xem promote prod dùng `window.confirm`).
- [ ] Màu chỉ lấy từ token CSS; kiểm tra ở bề rộng 360px và chế độ tối.

### 11.2 Lỗi thường gặp (rút từ các lần sửa thật)

| Lỗi | Triệu chứng | Cách đúng |
|---|---|---|
| Sắp xếp log theo `created_at` | Hai dòng audit cùng mili-giây bị đảo thứ tự (PROGRESS #7) | Cột `seq BIGINT GENERATED BY DEFAULT AS IDENTITY UNIQUE`, `ORDER BY seq DESC` |
| Tài khoản không còn trả 403 | Game không tự đăng xuất, kẹt ở trạng thái lỗi (PROGRESS #12) | `throw Unauthenticated(...)` → 401. Client chỉ xoá thông tin đăng nhập khi gặp 401. Dùng `activeCaller()` |
| Đồng hồ giả lệch giờ thật trong test | Mọi request trả 401 | JWT được kiểm theo giờ thật: `clock` khởi tạo bằng `System.currentTimeMillis()`, chỉ tăng sau khi đã phát token |
| Thông báo "Hợp lệ" cũ vẫn hiện sau khi sửa | Creator lưu bản ghi chưa kiểm tra mà tưởng đã kiểm | Xoá kết quả khi input đổi: `useEffect(() => setResult(undefined), [text, setResult])` |
| Test hoặc agent ghi vào `content/` của repo | Repo bẩn sau khi chạy agent | `ServerTest` chép content sang thư mục tạm; `run-console.sh` chép sang `mktemp -d`. Không bao giờ để `PXWORLD_CONTENT_WRITABLE=true` trỏ vào `content/` thật khi chạy tự động |
| `screen` gõ tay chuỗi ngoài catalog | Lỗi biên dịch `TS2322` (PROGRESS #8) | Chọn ID có sẵn, hoặc thêm vào `catalog.mjs` rồi sinh lại. Không sửa tay `screenIds.ts` |
| Kiểu TS khai `field: T \| null` | So sánh `=== null` không bao giờ đúng vì field vắng mặt | Khai `field?: T` (do `explicitNulls = false`) |
| Sai tên field trong request | Server lặng lẽ dùng giá trị mặc định | Test khẳng định **hiệu quả** của request, không chỉ mã 200 |
| Thiếu kiểm tra độ dài | 500 với body rỗng | `require(x.length <= n)` theo cột |
| Dùng `error()`/`check()` cho "không tìm thấy" | Trả 409 thay vì 400/404 | Không tìm thấy: `IllegalArgumentException` (đối tượng admin trỏ tới) hoặc `respond(NotFound, …)` (tài nguyên của chính mình) |
| Đổi role mà không đăng nhập lại | Staff vẫn bị 403, hoặc vẫn còn quyền cũ | Role nằm trong JWT; phải đăng nhập lại |
| Migration có `;` trong chuỗi hoặc comment | Migration vỡ giữa câu | Không dùng `;` ngoài dấu kết câu |
| `vite preview` phục vụ bản cũ | Agent thấy giao diện cũ | `npm run console:build` trước (run-console.sh tự làm) |

### 11.3 Vấn đề đã biết trong code hiện tại (chưa sửa)

| # | Vấn đề | Chỗ | Ảnh hưởng |
|---|---|---|---|
| 1 | `/telemetry` kiểm tên từng sự kiện **trong lúc** ghi: sự kiện hỏng ở giữa batch làm các sự kiện trước nó đã được ghi rồi mới trả 400; client gửi lại cả batch | `Routes.kt`, route `/telemetry` | Trùng sự kiện |
| 2 | `actor`/`target` không có `#` trong replay gây `IndexOutOfBoundsException` → 500. `lane`/`depth` ngoài lưới bị `GridCell` chặn thành 400 trước khi tới kiểm tra "cell", nên nhánh vi phạm "cell" trong `validateReplay` không bao giờ chạy. Cả hai trường hợp không được ghi vào `battle_validations` | `ReplayCodec.unit`, `ContentService.validateReplay` | Replay giả mạo kiểu này không để lại dấu vết |
| 3 | `POST /mail/{id}/claim` trả bản ghi đọc **trước** khi cập nhật, nên `claimed: false` | `Repositories.claimMail` | Response sai; client hiện không đọc field này |
| 4 | Token sống 12 giờ, không có refresh. Tài khoản khách không có mật khẩu nên **không đăng nhập lại được**: sau 12 giờ, save cloud và thư của khách không truy cập được nữa | `Tokens`, `/auth/*` | Mất dữ liệu cloud của khách |
| 5 | `/telemetry` với token hết hạn trả 401, nhưng `CloudSync.report` không xoá thông tin đăng nhập, nên client gửi lại mãi mỗi 30 giây mà không thành công | `CloudSync.report` | Telemetry ngừng tới khi người chơi làm thao tác cloud khác |
| 6 | `requireRole` không tra DB: staff bị xoá hoặc đổi role vẫn giữ quyền tới khi token hết hạn. Chưa có API vô hiệu hoá staff | `Security.kt` | Không thu hồi quyền ngay được |
| 7 | Không kiểm độ dài: `reason` (512), `subject` (128), `displayName` của staff (64), `clientVersion` (32). `/admin/staff` không kiểm định dạng email và độ dài tên như `/auth/register` | `Routes.kt` | 500 |
| 8 | Grant chấp nhận **mọi** id content (quest, trận, kỹ năng…), nhưng game chỉ nhận currency, item, equipment, hero và bỏ qua phần còn lại | `/admin/players/{id}/grant`, `ContentBundleCatalog.grantKindOf` | Support tặng "quà" mà người chơi không nhận được |
| 9 | `ENVIRONMENTS` thiếu `pilot`, trong khi MASTER_PLAN §8.2 có kênh `dev → qa → staging → pilot → prod` | `Routes.kt` | Không promote được lên pilot |
| 10 | `GET /studio/kinds/{kind}` với kind lạ trả 200 rỗng, còn `/schema` trả 400 | `ContentService.records` | Không nhất quán |
| 11 | Xác thực trận dùng content đang nạp trong server, không phải release client đang chơi | `ContentService.validateReplay` | Replay trung thực có thể bị báo sai khi hai bản lệch nhau |
| 12 | `SaveMetaView.bytes` và `MAX_SAVE_BYTES` đếm **ký tự** UTF-16, không phải byte | `SaveRow.meta()`, route `PUT /saves/{slot}` | Số liệu sai với save có ký tự ngoài ASCII |
| 13 | Id mặc định của bản ghi mới là `kind` bỏ chữ `s` cuối: `heroe.new`, `statuse.new`, `hero_classe.new` | `StudioKindPage` | Creator phải sửa tay id |
| 14 | `run-console.sh` gọi `./gradlew :server:app:installDist` với `settings.gradle` mặc định (có module Android/iOS/HTML legacy), trong khi `run-desktop.sh` và CI dùng `--settings-file settings-test.gradle` | `run-console.sh` | Có thể lỗi trên máy không có Android SDK |
| 15 | Báo cáo console agent đặt `registeredCount = visitedCount` và `launchPercent = 0` | `console-agent.mjs` | Số liệu độ phủ vô nghĩa khi tải lên QA |
| 16 | `POST /content/releases` khi content không đổi vẫn ghi audit và trả 201 với version cũ | `Services.publishCurrentContent` | Audit gây hiểu nhầm |
| 17 | `/metrics` công khai; `/content/packs/{version}` phục vụ cả bản chưa promote; `/auth/login`, `/auth/guest`, `/telemetry` không giới hạn tần suất | `Routes.kt` | Lộ số liệu, dò mật khẩu, spam tài khoản khách |
| 18 | `ReleaseDiff` chọn bản gốc mặc định theo kênh `prod` chỉ lúc mount; nếu `channels` về sau `releases` thì không cập nhật | `OperationsPages.tsx` | Mặc định so sai bản |
| 19 | Thêm bản ghi mới vào "file đầu tiên" của kind theo thứ tự duyệt thư mục, thứ tự này phụ thuộc hệ điều hành | `ContentService.upsert` | File đích không tất định |
| 20 | Lỗi SQL, trùng email do race khi đăng ký, và mọi exception không nằm trong bảng §1.10 trả 500 body rỗng | `StatusPages` | Client chỉ thấy "HTTP 500" |

Sửa mục nào thì xoá khỏi bảng trong cùng commit, kèm test chứng minh.

---

## 12. Khoảng trống so với MASTER_PLAN §10/§11

Danh sách cho người lập kế hoạch. "Hiện có" là trạng thái ở commit `af8fd71`.

### 12.1 Backend ([MASTER_PLAN §10](../MASTER_PLAN.md#10-backend--module-và-api), §8.4)

| Kế hoạch | Hiện có | Còn thiếu |
|---|---|---|
| identity: Keycloak, staff SSO, RBAC `resource:action`, đăng nhập guest/email/Google/Apple | JWT HS256 tự phát, PBKDF2, 6 role staff thô, guest + email ([ADR 0007](../adr/0007-ktor-backend-shared-domain.md) hoãn Keycloak) | Keycloak/SSO, Google/Apple, refresh token, xác minh email, quên mật khẩu, MFA, quản lý staff (liệt kê, sửa role, vô hiệu hoá), 5 role launch còn thiếu (`pilot`, `ugc_creator`, `staff_translator`, `staff_moderator`, `staff_finance`) |
| saves: Postgres JSONB + S3 | `TEXT` trong Postgres/H2, 30 bản lịch sử, khôi phục có audit | JSONB, S3 |
| content: S3 + CDN, manifest theo env, rollback | Pack lưu trong `content_releases.body`, manifest và pack công khai, promote bản cũ để rollback | S3/CDN, màn `console.operations.content_rollback`, env `pilot`, game tải pack theo manifest |
| economy: ledger tiền cao cấp, IAP, hoàn tiền | Không có (kinh tế ở client) | Toàn bộ module |
| battle: xác thực replay, lưu replay | Xác thực có, chỉ lưu verdict | Lưu replay (S3), game gửi replay, version content trong replay |
| liveops: sự kiện, banner, offer, thư, push, flag, remote config, A/B, phân khúc | Thư tặng quà từ support | Mọi phần còn lại ([WP-B2](05-work-packages.md), WP-C13) |
| social, ugc | Không có | Toàn bộ |
| telemetry: pipeline vào ClickHouse | Bảng `telemetry_events`, đếm 24 giờ trên dashboard | ClickHouse; explorer **đang làm — WP-TELEMETRY** |
| support/moderation: ticket, tố cáo, xử phạt, kháng nghị | Khoá theo giờ, gỡ khoá | Ticket, tố cáo, kháng nghị, anti-cheat flag (WP-B9) |
| qa/pilot: test run, bug, agent run, cohort, khảo sát | Tải và xem agent run | Mọi phần còn lại (WP-B7) |
| API: OpenAPI 3.1, sinh client TS và Kotlin, WebSocket, header `X-Client-Version` + 426 | Viết tay `api.ts` và `HttpCloudGateway`, không WebSocket, `clientVersion` chỉ nằm trong body telemetry | OpenAPI và client sinh tự động (WP-X1), WebSocket, kiểm tra phiên bản client |
| Hạ tầng §8.4: docker-compose, Redis, ClickHouse, MinIO, Keycloak, Grafana/Loki/Tempo, OpenTelemetry, Sentry | Một tiến trình Ktor + H2/Postgres, `/metrics` dạng text tự viết | Toàn bộ (WP-F1) |

### 12.2 Web ([MASTER_PLAN §11](../MASTER_PLAN.md#11-web--console-studio-qa-hub-pilot-site), D8, [ADR 0008](../adr/0008-web-monorepo.md))

| Kế hoạch | Hiện có | Còn thiếu |
|---|---|---|
| Monorepo pnpm: `apps/console`, `apps/pilot`, `apps/site` (Astro); `packages/ui`, `api-client`, `design-tokens`, `screen-catalog` | **npm** workspaces; `apps/console`, `packages/screen-catalog` | `pilot`, `site`, `ui`, `api-client`, `design-tokens`; ADR 0008 và D8 ghi pnpm nhưng repo dùng npm |
| Console một shell, menu theo quyền, nhóm `console.*`, `studio.*`, `devtools.*`, `qa.*` | Shell + menu theo quyền; 48 screen id có trang hoặc khu vực (16 ID riêng + list/editor của 16 kind Studio) | `devtools.*` chưa có màn nào; phần lớn `console.*` |
| Studio ghi qua API, có branch, review, promote | Ghi qua API vào thư mục content của server, có dry-run | Branch, review, promote theo thực thể, lịch sử bản ghi, bulk (WP-B1) |
| Editor đặc thù: lưới encounter 3×3, React Flow cho hội thoại/quest, map editor, simulator | Không có (lưới 3×3 vẫn ở `dashboard/` legacy) | Toàn bộ |
| Stack: TanStack Router + Query, Zod sinh từ JSON Schema, Monaco, React Flow, ECharts | Router hash tự viết, `useLoad` tự viết, `SchemaForm` tự viết, `<textarea>` cho JSON | Toàn bộ thư viện trong danh sách |
| Test: Vitest + Playwright | Console agent tự viết trên CDP, không có unit test web | Vitest, Playwright |
| Pilot app (chỉ role `pilot`), Site Astro + cổng tài khoản | Không có | Toàn bộ (WP-D4…) |
