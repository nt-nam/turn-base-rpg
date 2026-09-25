---
name: pxworld-work-package
description: Take and finish one PXWORLD work package (WP-xx from docs/handbook/05-work-packages.md) end to end — branch/worktree, required reading, implementation within scope, test gate, commits without tags, progress report. Use whenever asked to "làm WP-…", continue the rewrite plan, or pick the next task.
---

# Làm một gói việc PXWORLD

## 1. Chuẩn bị
1. Mở `docs/handbook/05-work-packages.md`, tìm đúng WP. Chỉ làm WP có trạng thái **Sẵn sàng** hoặc WP được giao đích danh. Nếu còn phụ thuộc chưa **Xong**, dừng lại và báo.
2. Tạo nhánh từ `rewrite`:
   - Nếu có agent khác chạy song song, làm trong worktree riêng: `git worktree add -b rewrite-<wp> ../LVpxW-wt/<wp> rewrite`, rồi chép `local.properties` vào worktree.
   - Nếu không, dùng `git switch -c rewrite-<wp> rewrite`.
3. Đọc trước:
   - `docs/handbook/02-architecture.md` và `03-engineering-rules.md` (luật bắt buộc);
   - công thức hợp với loại việc: `08-recipes-game-client.md`, `09-recipes-content.md` hoặc `10-recipes-server-and-console.md`;
   - các file được nêu trong WP.

## 2. Làm
- Làm đúng "Các bước" và "Phạm vi" của WP. Việc phát sinh thì ghi vào mục 9 của `05-work-packages.md`, không tự mở rộng phạm vi.
- Luật cứng:
  - không comment trong code;
  - không viết tắt;
  - `domain` và `application` phải tất định;
  - màn mới phải có trong catalog trước;
  - chuỗi hiển thị qua localization vi và en;
  - staff ghi thì phải audit;
  - save tương thích tiến.
- Muốn phá luật thì viết ADR (`docs/handbook/templates/adr.md`) và hỏi người điều phối trước.

## 3. Kiểm chứng (bắt buộc, dán kết quả thật vào báo cáo)
1. Cổng test chung: `docs/handbook/05-work-packages.md` §0.3.
2. Các lệnh trong mục "Cách test" và "Nghiệm thu" của WP.
3. Tick từng ô nghiệm thu. Ô nào không đạt thì ghi rõ lý do, không báo "xong".

## 4. Commit
- Dùng Conventional Commits (`feat(scope):`, `fix`, `test`, `docs`, `refactor`, `chore`), chia commit theo lát logic.
- **Không bao giờ tạo git tag.** Không push nếu chưa được yêu cầu.
- Không sửa `docs/PROGRESS.md` khi đang là agent chạy song song; người điều phối cập nhật file này sau khi merge.

## 5. Báo cáo cuối
Báo cáo gồm:
- tên nhánh và hash commit;
- file đã sửa;
- lệnh đã chạy và kết quả **quan sát được** (dòng PASSED/FAILED, số test);
- các ô nghiệm thu chưa đạt;
- file ngoài phạm vi đã chạm;
- rủi ro còn lại.
