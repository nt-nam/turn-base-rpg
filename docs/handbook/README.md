# Handbook PXWORLD — tài liệu bàn giao

> Viết cho người **nhận làm tiếp** dự án: kỹ sư client, backend, web, platform, creator, artist và QA. Đọc xong handbook, bạn phải tự làm được một gói việc (WP) và tự chứng minh nó chạy đúng, không cần hỏi lại đội cũ.
>
> Nhánh làm việc: `rewrite`. Tài liệu chiến lược: [MASTER_PLAN.md](../MASTER_PLAN.md). Tiến độ thật: [PROGRESS.md](../PROGRESS.md).

## 1. Bản đồ tài liệu

| # | Tài liệu | Trả lời câu hỏi | Ai đọc |
|---|---|---|---|
| 01 | [Sản phẩm và thiết kế game](01-product-and-game-design.md) | Game là gì? Luật thiết kế nào không được phá? | Tất cả |
| 02 | [Kiến trúc bắt buộc](02-architecture.md) | Code nằm ở đâu, được phụ thuộc vào đâu, luật nào được test tự động? | Kỹ sư |
| 03 | [Luật kỹ thuật và quy trình](03-engineering-rules.md) | Viết code, commit, review, "xong" nghĩa là gì? | Kỹ sư, QA |
| 04 | [Kỹ năng, vai trò, Claude Code](04-skills-and-roles.md) | WP này cần kỹ năng gì? Vai trò của tôi đọc gì? Giao việc cho agent AI thế nào? | Tất cả, trưởng nhóm |
| 05 | [Kế hoạch gói việc](05-work-packages.md) | Làm gì tiếp theo, theo thứ tự nào, nghiệm thu ra sao? | Tất cả |
| 06 | [Hướng dẫn test và bộ hồi quy](06-testing.md) | Chạy test nào, viết kịch bản agent thế nào, test tay những gì? | QA, kỹ sư |
| 07 | [Cài đặt, chạy, môi trường, CI](07-setup-and-environments.md) | Dựng máy, chạy game/server/console/Android, biến môi trường, CI | Tất cả |
| 08 | [Công thức: game client](08-recipes-game-client.md) | Thêm màn, luật, chuỗi UI, map, hiệu ứng trận, cheat debug | Client engineer |
| 09 | [Công thức: nội dung](09-recipes-content.md) | Thêm anh hùng, quái, trận, quest, hội thoại; cân bằng; phát hành content | Creator, designer |
| 10 | [Công thức: server và Console](10-recipes-server-and-console.md) | Thêm endpoint, migration, vai trò, trang Console, bước console agent | Backend, web |
| — | [AGENTS.md](../../AGENTS.md) | Cách làm việc: bắt đầu phiên, mức tự quyết, git, báo cáo, chạy song song, tạm dừng | Tất cả, agent AI |
| — | [Mẫu biểu](templates/) | [WP](templates/work-package.md) · [Báo lỗi](templates/bug-report.md) · [Test case](templates/test-case.md) · [ADR](templates/adr.md) | Tất cả |
| — | [ADR](../adr/) | Vì sao kiến trúc như hiện tại | Kỹ sư |
| — | [Screen catalog](../screens/SCREEN_CATALOG.md) | Danh sách 1.029 màn launch (2.094 sau 4 mùa) | Tất cả |

## 2. Ba mươi phút đầu tiên
1. Đọc [01](01-product-and-game-design.md) mục 1–3 (5 phút).
2. Dựng máy theo [07](07-setup-and-environments.md), chạy cổng test chung ở [05 §0.3](05-work-packages.md) (15 phút, lần đầu tải dependency lâu hơn).
3. Chạy game desktop, chơi qua màn pháp lý tới bản đồ làng. Mở menu debug (flavor DEV) để xem các cheat (5 phút).
4. Mở [05](05-work-packages.md), chọn một WP **Sẵn sàng** hợp với kỹ năng ở [04](04-skills-and-roles.md) (5 phút).

## 3. Mười luật quan trọng nhất
Luật đầy đủ có ở tài liệu 02 và 03. Nếu chỉ nhớ được 10 điều:

1. `domain` và `application` là Kotlin thuần, **tất định**: không libGDX, không I/O, không `Random()`, không đồng hồ hệ thống. Test kiến trúc sẽ chặn nếu vi phạm.
2. Mọi màn hình phải có `ScreenId` trong catalog trước khi có code. CI chạy `catalog.mjs --check`.
3. Nội dung là dữ liệu trong `content/`, được compiler kiểm tra. Không hard-code nội dung trong Kotlin.
4. Mọi chuỗi hiển thị là key bản địa hoá, có đủ vi và en (`ClientTextTest`).
5. Mọi thay đổi tiền tệ đi qua `Wallet` và ghi ledger. Mọi thao tác ghi của staff đều vào `audit_log`, cấp phát tài nguyên phải có lý do.
6. Save phải tương thích tiến: save cũ luôn đọc được. Mỗi thay đổi cấu trúc save cần thêm fixture test.
7. Không comment trong code. Tên phải tự giải thích; lý do thiết kế ghi vào ADR.
8. Commit theo Conventional Commits, **không tạo git tag**. Làm việc trên nhánh `rewrite-<wp>` rồi merge về `rewrite`.
9. "Xong" nghĩa là cổng test chung xanh **và** kịch bản test agent liên quan PASSED, có số liệu ghi vào `PROGRESS.md`.
10. Muốn phá một luật thì viết ADR trước và được duyệt trước.

## 4. Thuật ngữ
| Thuật ngữ | Nghĩa |
|---|---|
| **WP** | Work package, gói việc trong [05](05-work-packages.md) |
| **ScreenId** | ID một màn trong catalog, ví dụ `game.battle.battle_main`. Code sinh `GameScreenId` (Kotlin) và `WebScreenId` (TypeScript) |
| **Content kind** | Một loại dữ liệu nội dung (heroes, encounters…), đăng ký trong `ContentKinds` |
| **Content pack** | File JSON đã kiểm tra và đóng gói từ `content/`, được game nạp lúc chạy |
| **Release / channel** | Bản content đã đóng gói trên server, được đẩy qua các kênh `dev → qa → staging → prod`. Kênh `pilot` có trong kế hoạch nhưng chưa có (P-37). Game hiện dùng content đóng gói sẵn trong build, chưa tải release được promote (P-19) |
| **Flavor** | Cấu hình build client: `DEV`, `QA`, `PILOT`, `RELEASE` (bật hay tắt debug và automation) |
| **Automation protocol** | WebSocket JSON-RPC trong client (cổng 47017) cho test agent điều khiển game |
| **Test agent** | `tools/test-agent`: kịch bản tất định (`core-loop`, `chapter1`, `cloud`), explorer (ghé mọi màn), console agent (trình duyệt headless) |
| **Golden test** | Trận có seed và lệnh cố định, event log so với file `.txt` mẫu. Đổi công thức phải cập nhật golden và được review |
| **Permille (‰)** | Đơn vị phần nghìn dùng cho mọi tỉ lệ trong domain và content |
| **Dải tỉ lệ thắng** | Khoảng tỉ lệ thắng mục tiêu theo vai trò encounter, kiểm bằng mô phỏng (WP-A3) |
| **Legacy** | Code cũ Java trong `core/`, `lwjgl3/`, `android/`, `ios/`, `html/`, `dashboard/`. Không sửa, sẽ bị xoá (WP-A6) |
