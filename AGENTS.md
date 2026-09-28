# AGENTS.md — cách làm việc trong repo PXWORLD

> Áp dụng cho **mọi người và mọi agent AI** (Claude Code, Codex, Cursor…) làm việc trong repo này. Claude Code nạp file này qua [CLAUDE.md](CLAUDE.md).
> Chi tiết kỹ thuật nằm trong [docs/handbook/](docs/handbook/README.md). File này chỉ ghi **cách làm việc**: bắt đầu một phiên thế nào, luật cứng, git, báo cáo, chạy song song và tạm dừng.

## 1. Bắt đầu mỗi phiên
1. Đọc mục **"Điểm tạm dừng"** mới nhất trong [docs/PROGRESS.md](docs/PROGRESS.md). Mục này ghi đang dừng ở đâu và bước tiếp theo là gì.
2. Mở [docs/handbook/05-work-packages.md](docs/handbook/05-work-packages.md) và chọn **một** gói việc (WP) có trạng thái Sẵn sàng. Gói việc đó phải hợp với kỹ năng ở [04-skills-and-roles.md](docs/handbook/04-skills-and-roles.md).
3. Đọc các tài liệu mà WP yêu cầu trong mục "Đọc trước". Tối thiểu gồm [02-architecture.md](docs/handbook/02-architecture.md) và phần công thức hợp với việc (08, 09 hoặc 10).
4. Nếu người giao việc chỉ nói "tiếp tục", làm theo bước tiếp theo ghi trong "Điểm tạm dừng". Không hỏi lại những gì đã có trong tài liệu.

## 2. Mức tự quyết
| Được tự làm, không cần hỏi | Phải hỏi chủ dự án trước |
|---|---|
| Mọi việc nằm trong phạm vi WP trên nhánh `rewrite-<wp>` | Phá một luật cứng ở §3, hoặc đổi quyết định trong ADR (viết ADR đề xuất rồi mới hỏi) |
| Sửa lỗi, thêm test, refactor trong vùng code của WP | Push, mở PR, publish, gửi dữ liệu ra dịch vụ ngoài |
| Tạo WP phát sinh ở [05 §9](docs/handbook/05-work-packages.md) | Xoá dữ liệu, xoá nhánh hay worktree, `reset --hard`, force push |
| Merge nhánh WP vào `rewrite` khi cổng test xanh (vai trò PM) | Việc thuộc mục "Quyết định chờ chủ dự án" trong 05 |

## 3. Luật cứng
Luật đầy đủ và cách luật được kiểm tra tự động có ở [02-architecture.md](docs/handbook/02-architecture.md) và [03-engineering-rules.md](docs/handbook/03-engineering-rules.md).

1. `game/domain` và `game/application` là Kotlin thuần và **tất định**: không libGDX, không I/O, không `Random()`, không đồng hồ hệ thống. Test `:tools:architecture` sẽ chặn nếu vi phạm.
2. Màn hình phải có `ScreenId` trong catalog (`tools/screen-catalog/catalog.mjs`) **trước khi** viết code.
3. Nội dung game là dữ liệu trong `content/`. Không hard-code nội dung trong Kotlin.
4. Mọi chữ hiển thị là key bản địa hoá, có đủ vi và en.
5. Tiền tệ chỉ đổi qua `Wallet` và ghi ledger. Mọi thao tác ghi của staff vào `audit_log`, cấp phát phải có lý do.
6. Save phải tương thích tiến: khi đổi cấu trúc save, thêm fixture save cũ vào test.
7. **Không comment trong code.** Tên phải tự giải thích; lý do thiết kế ghi vào ADR (`docs/adr/`, mẫu ở [templates/adr.md](docs/handbook/templates/adr.md)).
8. Không viết tắt; code và ID bằng tiếng Anh; không dùng `println` trong code thư viện.
9. Không đổi công thức trận chỉ để chữa cân bằng. Chỉnh số trong content; đổi công thức phải có ADR và cập nhật golden.
10. Không log hay in token, mật khẩu, secret.

## 4. Git
- Nhánh chính của đợt viết lại: **`rewrite`**. Mỗi WP làm trên một nhánh `rewrite-<wp>` (ví dụ `rewrite-b1`), rồi merge về `rewrite` bằng `--no-ff`.
- Commit theo Conventional Commits: `feat(scope):`, `fix`, `test`, `docs`, `refactor`, `chore`, `wip`. Mỗi commit là một lát logic.
- **Không bao giờ tạo git tag.** Mốc tiến độ ghi bằng commit message và bằng `docs/PROGRESS.md`.
- Không force push lên nhánh dùng chung. Không commit `local.properties`, `node_modules/`, `build/` hay `agent-reports/`.
- Commit do agent AI tạo có dòng cuối `Co-Authored-By:` ghi tên model.

## 5. "Xong" nghĩa là gì
1. Mọi ô nghiệm thu của WP được tick. Ô nào không đạt thì ghi lý do và **không báo là xong**.
2. **Cổng test chung** ở [05 §0.3](docs/handbook/05-work-packages.md) xanh, cộng với các lệnh test riêng của WP.
3. Kịch bản test agent liên quan PASSED (desktop, cloud, explorer, console hoặc Android, tuỳ phạm vi).
4. [docs/PROGRESS.md](docs/PROGRESS.md) được cập nhật số liệu mới (số màn, số test).

## 6. Báo cáo
- **Kết luận trước**: câu đầu là kết quả. Không mở đầu dài dòng, không nhắc lại đề bài.
- **Chỉ báo điều đã quan sát.** Chỉ ghi PASSED khi đã thấy dòng PASSED. Khi test fail, dán nguyên dòng lỗi đầu tiên, không diễn giải lại.
- **Không chắc thì nói rõ** "chưa chắc", kèm lý do và cách kiểm tra. Không trình bày phỏng đoán như sự thật.
- **Đề xuất một phương án tốt nhất.** Chỉ liệt kê nhiều lựa chọn khi được hỏi. Nếu không có phương án tốt, nói thẳng như vậy rồi nêu phương án ít tệ nhất và cái giá của nó.
- Báo cáo cuối một WP gồm:
  - nhánh và hash commit;
  - file đã sửa;
  - lệnh đã chạy và kết quả thật;
  - ô nghiệm thu chưa đạt;
  - file ngoài phạm vi đã chạm;
  - rủi ro còn lại.

## 7. Chạy nhiều agent song song (vai trò PM)
Khi có nhiều WP độc lập, một người (hoặc một agent) làm **PM**: viết brief cho từng agent, chạy song song, merge và kiểm chứng.

1. **Mỗi agent một worktree**, tạo từ `rewrite` và đặt **ngoài** thư mục repo:
   ```bash
   git worktree add -b rewrite-<wp> ../LVpxW-wt/<wp> rewrite
   cp local.properties ../LVpxW-wt/<wp>/
   ```
   Không dùng chế độ tự tạo worktree của công cụ nếu nó dựng từ `main` (Claude Code `isolation: "worktree"` từng tạo worktree từ `main`).
2. **Brief** của mỗi agent ghi rõ: đường dẫn worktree, phạm vi, file được sửa, lệnh test phải chạy, cổng mạng được dùng. Brief cũng nhắc "không sửa `docs/PROGRESS.md`, không tag, không push".
3. **Tài nguyên dùng chung**:

   | Tài nguyên | Chủ dùng |
   |---|---|
   | Cổng 47017 | automation game desktop |
   | Cổng 47117 | automation Android qua `adb forward` |
   | Cổng 8080 và 18080 | server |
   | Cổng 4173 | Vite preview |
   | Cổng 8095 | web client |
   | Emulator | **một** agent |

   Máy 16 GB RAM chạy tối đa khoảng 4 agent build Gradle cùng lúc. `gradle.properties` đặt daemon tắt và `-Xmx1G`; không tăng giá trị này.
4. Agent tài liệu (chỉ đọc code, viết markdown) nhẹ, chạy thêm được. Không cho agent tài liệu chạy Gradle hay git ghi.
5. **PM** merge lần lượt từng nhánh (`--no-ff`), giải xung đột, chạy toàn bộ cổng test, rồi cập nhật `PROGRESS.md`. Phát hiện của agent được ghi vào [05 §9](docs/handbook/05-work-packages.md).

## 8. Tạm dừng và bàn giao
Khi được yêu cầu dừng:
1. Mỗi agent làm xong bước đang dở, dừng mọi tiến trình đã mở (game, server, emulator, Vite), rồi commit trên nhánh của mình. Việc chưa xong thì dùng `wip(scope): …`, trong thân commit có danh sách `Remaining:`.
2. PM ghi mục **"Điểm tạm dừng (YYYY-MM-DD)"** vào `docs/PROGRESS.md`. Mục này gồm bảng nhánh, commit, trạng thái, việc tiếp theo, chỗ dễ xung đột khi merge, và các phát hiện quan trọng.
3. Cập nhật trạng thái WP trong `05-work-packages.md`.
4. Người sau chỉ cần đọc §1 của file này là làm tiếp được. Không cần gọi lại agent cũ.

## 9. Lưu ý môi trường (Windows + Git Bash)
- Lệnh adb trong Git Bash cần `MSYS_NO_PATHCONV=1`. Tham số `/` đơn lẻ bị Git Bash đổi thành `C:/Program Files/Git/`.
- Heredoc nhiều dòng có dấu nháy dễ vỡ. Viết script ra file rồi mới chạy.
- Emulator chỉ khởi động được với `-gpu swiftshader_indirect -feature -Vulkan` (xem [07](docs/handbook/07-setup-and-environments.md)).
- Build JVM nhanh hơn với `./gradlew --settings-file settings-test.gradle …`, vì không cấu hình các module Android.

## 10. Công cụ cho agent AI
- Skill quy trình: [.claude/skills/](.claude/skills/)
  - `pxworld-work-package`: nhận và hoàn thành một WP.
  - `pxworld-game-screen`: thêm hoặc hoàn thiện màn game.
  - `pxworld-content`: thêm hoặc sửa nội dung.
  - `pxworld-server-endpoint`: thêm endpoint, migration hoặc vai trò.
  - `pxworld-console-page`: thêm trang Console.
  - `pxworld-verify`: chạy cổng test và đọc báo cáo.
- Định nghĩa subagent: [.claude/agents/](.claude/agents/)
  - `pxworld-wp-agent`: làm một WP trong worktree.
  - `pxworld-doc-writer`: viết tiếp handbook.
