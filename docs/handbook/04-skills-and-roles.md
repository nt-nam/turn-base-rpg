# 04 · Kỹ năng, vai trò và cách nhận việc

> Dùng tài liệu này để: (1) biết một gói việc cần kỹ năng gì (mã `K-…` trong [05-work-packages.md](05-work-packages.md)); (2) biết mỗi vai trò đọc gì trước khi bắt tay vào làm; (3) dùng Claude Code đúng cách trong repo này.

## 1. Bảng kỹ năng

Cột "Mức tối thiểu" là mức cần có để nhận một WP dùng kỹ năng đó mà không phải kèm cặp. Cột "Chỗ học nhanh trong repo" là file nên đọc để quen cách dự án dùng công nghệ đó.

| Mã | Kỹ năng | Mức tối thiểu | Chỗ học nhanh trong repo |
|---|---|---|---|
| **K-KT** | Kotlin 2.x: data class, sealed, extension, coroutines cơ bản | Viết được code bất biến, không dùng `!!`, hiểu `object` và `companion` | `game/application/.../GameRules.kt`, `Cloud.kt` |
| **K-DOM** | Mô hình domain tất định: số nguyên/permille, RNG có seed, reducer thuần | Giải thích được vì sao cấm `Float` và `System.currentTimeMillis` trong domain | `game/domain/.../BattleEngine.kt`, `Pcg32.kt`, `StatFormula.kt`, `BattleGoldenTest` |
| **K-SER** | kotlinx.serialization, JSON Schema | Thêm được trường tương thích tiến; đọc được schema sinh ra | `game/content/.../ContentRecords.kt`, `ContentSchema.kt`, `SaveGameDocument.kt` |
| **K-GDX** | libGDX 1.13 + scene2d + KTX: Stage, Table, Skin, Viewport, InputProcessor | Dựng màn scene2d có layout co giãn; hiểu vòng đời `Screen` | `game/client/.../ui/UiKit.kt`, `screens/**`, `Navigation.kt` |
| **K-ECS** | Fleks 2.x (ECS) | Thêm component/system; hiểu mỗi màn có `World` riêng | `game/client/.../world/WorldEcs.kt` |
| **K-TILED** | Tiled map editor: object layer, tileset | Thêm NPC/teleport/quái vào map đúng layer | `assets/tilemap/**`, `MapLayout.kt` |
| **K-AND** | Android: AGP 8, adb, emulator, logcat, BuildConfig | Cài APK, đọc logcat, forward cổng | `game/platform-android/**`, WP-A1 |
| **K-IOS** | RoboVM, Xcode, macOS | Build được app RoboVM mẫu | ADR 0001, MASTER_PLAN §8.1 |
| **K-TEAVM** | TeaVM / gdx-teavm | Biết giới hạn: reflection, thread, `java.io` | ADR 0009, WP-A5 |
| **K-KTOR** | Ktor 3 server, JDBC, SQL tương thích H2 và PostgreSQL, migration | Viết route có phân quyền và test bằng `testApplication` | `server/app/.../Routes.kt`, `Repositories.kt`, `ServerTest.kt` |
| **K-SEC** | JWT, PBKDF2, RBAC, audit, OWASP cơ bản | Không log token; biết khi nào trả 401 hay 403 | `server/app/.../Security.kt` |
| **K-WEB** | React 19, TypeScript strict, Vite | Viết trang có loading/error state, kiểu chặt, không `any` | `web/apps/console/src/**` |
| **K-CDP** | Điều khiển trình duyệt qua Chrome DevTools Protocol, Node ESM | Thêm được bước vào console agent | `tools/test-agent/console/console-agent.mjs` |
| **K-AUTO** | Automation protocol JSON-RPC, viết kịch bản test agent | Viết kịch bản mới từ `ui.tap`, `screen.tree`, `screen.open` | `tools/test-agent/run.mjs`, `scenarios/*.mjs`, `StageAutomationDriver.kt` |
| **K-CI** | Gradle (Kotlin DSL, convention plugin), GitHub Actions | Thêm module hoặc job mà không phá build khác | `build-logic/**`, `.github/workflows/ci.yml` |
| **K-OPS** | Docker, Kubernetes/Helm, PostgreSQL vận hành, observability | Dựng môi trường staging | MASTER_PLAN §8.4 |
| **K-GD** | Thiết kế game: vòng lặp, kinh tế, độ khó, cân bằng bằng số liệu | Đọc bảng mô phỏng và chỉnh số để đạt dải mục tiêu | `content/BALANCE_NOTES.md`, `content/balance/`, `sim-cli` (WP-A2) |
| **K-NARR** | Viết truyện, hội thoại nhánh, bản địa hoá vi/en | Viết hội thoại có biến, giữ giọng nhân vật | `content/dialogues/`, `content/localization/` |
| **K-ART** | Pixel art, Aseprite, atlas, tileset | Xuất sprite theo quy ước tên ở MASTER_PLAN §9.1 | `art/`, `tools/asset-pipeline/**` |
| **K-AUD** | Âm thanh game: nhạc lặp, SFX, mức âm lượng | Xuất file đúng định dạng pipeline | `content/audio_cues/`, `AudioDirector.kt` |
| **K-QA** | Thiết kế test case, báo lỗi tái hiện được, test hồi quy | Viết test case có bước và kết quả mong đợi rõ ràng | [06-testing.md](06-testing.md) |
| **K-DATA** | SQL phân tích, funnel, cohort | Viết truy vấn tổng hợp có index phù hợp | `Repositories.telemetryByName` |

## 2. Vai trò trong đội và đường đọc

Mọi vai trò đều đọc [README.md](README.md), [01-product-and-game-design.md](01-product-and-game-design.md) và [03-engineering-rules.md](03-engineering-rules.md) trước.

| Vai trò | Kỹ năng chính | Đọc tiếp | WP phù hợp để bắt đầu |
|---|---|---|---|
| **Client engineer** | K-KT, K-GDX, K-ECS, K-AUTO | [02-architecture.md](02-architecture.md), [08-recipes-game-client.md](08-recipes-game-client.md), [06-testing.md](06-testing.md) | B5, B6, C12 (chia nhỏ theo module) |
| **Domain/gameplay engineer** | K-KT, K-DOM, K-SER | [02-architecture.md](02-architecture.md), [09-recipes-content.md](09-recipes-content.md) | C5–C9 (bước domain), C11 (pity) |
| **Backend engineer** | K-KTOR, K-SEC, K-SER | [10-recipes-server-and-console.md](10-recipes-server-and-console.md), [07-setup-and-environments.md](07-setup-and-environments.md) | B2, B3, B9, X1 |
| **Web engineer** | K-WEB, K-CDP | [10-recipes-server-and-console.md](10-recipes-server-and-console.md) | B1, B7, B8 |
| **Platform/DevOps** | K-AND, K-CI, K-OPS, K-TEAVM | [07-setup-and-environments.md](07-setup-and-environments.md) | A6, X2, F1 |
| **Game designer / creator** | K-GD, K-NARR, K-TILED | [09-recipes-content.md](09-recipes-content.md), MASTER_PLAN §2 | C1–C4 (phần nội dung) |
| **Artist / audio** | K-ART, K-AUD | MASTER_PLAN §9, `art/LICENSES.md` | C3, C4, X4 |
| **QA** | K-QA, K-AUTO, K-CDP | [06-testing.md](06-testing.md), [07-setup-and-environments.md](07-setup-and-environments.md) | Chạy bộ hồi quy thủ công; viết kịch bản agent cho WP vừa merge |

## 3. Ma trận kỹ năng theo đợt

Dấu ● là kỹ năng cần nhiều người-ngày trong đợt đó; ○ là cần ít.

| Kỹ năng | A | B | C | D | E | F |
|---|:-:|:-:|:-:|:-:|:-:|:-:|
| K-KT / K-DOM | ● | ○ | ● | ○ | ● | |
| K-GDX / K-ECS | ○ | ● | ● | ● | ● | ○ |
| K-KTOR / K-SEC | ○ | ● | ○ | ● | ● | ○ |
| K-WEB / K-CDP | ○ | ● | ● | ● | ● | |
| K-AND / K-IOS / K-TEAVM | ● | | | ● | | ● |
| K-GD / K-NARR | ● | ○ | ● | | ● | |
| K-ART / K-AUD | | | ● | ○ | ● | ○ |
| K-CI / K-OPS | ● | ○ | ○ | ● | ○ | ● |
| K-QA / K-AUTO | ● | ● | ● | ● | ● | ● |

## 4. Làm việc cùng Claude Code trong repo này

Repo có sẵn **project skill** cho Claude Code trong [`.claude/skills/`](../../.claude/skills). Mỗi skill là một quy trình đã chuẩn hoá, trỏ về đúng mục của handbook, để agent AI làm theo cùng luật với người.

| Skill | Dùng khi |
|---|---|
| `pxworld-work-package` | Nhận một WP: tạo nhánh, đọc đúng tài liệu, cổng test, commit không tag, cập nhật PROGRESS |
| `pxworld-game-screen` | Thêm hoặc hoàn thiện màn game (catalog → code → đăng ký → chuỗi UI → explorer) |
| `pxworld-content` | Thêm bản ghi hoặc loại content mới, xuất schema, kiểm cân bằng |
| `pxworld-server-endpoint` | Thêm endpoint, migration hoặc vai trò ở server kèm test |
| `pxworld-console-page` | Thêm trang Console kèm bước console agent |
| `pxworld-verify` | Chạy cổng test và đọc `report.json` của các agent |

Quy tắc khi giao việc cho agent AI:
1. Mỗi agent làm trong **một worktree riêng** tạo từ `rewrite` (`git worktree add -b rewrite-<wp> ../LVpxW-wt/<wp> rewrite`), rồi chép `local.properties` vào worktree.
2. Hai agent không dùng chung cổng: 47017 (automation desktop), 47117 (automation Android qua adb forward), 8080/18080 (server), 4173 (Vite preview). Emulator chỉ cho một agent dùng.
3. Máy 16 GB RAM chạy tối đa khoảng 4 agent build Gradle cùng lúc (`gradle.properties` đặt `-Xmx1G`, không daemon).
4. Agent không sửa `docs/PROGRESS.md`; người điều phối merge và cập nhật file này.
5. Không tạo git tag. Không push khi chưa được yêu cầu.
