---
name: pxworld-content
description: Add or change PXWORLD game content (heroes, skills, enemies, encounters, items, equipment, quests, dialogues, NPCs, check-in, achievements, audio cues, localization) or add a new content kind, with schema export, validation, balance bands and in-game verification. Use for any edit under content/.
---

# Làm nội dung PXWORLD

Nguồn chi tiết: `docs/handbook/09-recipes-content.md`. Luật thiết kế: `docs/handbook/01-product-and-game-design.md` §3.

## Sửa hoặc thêm bản ghi
1. ID dạng `<loại>.<slug>` (ASCII, snake_case), **không đổi sau khi phát hành**. Tên và mô tả là LocKey, thêm đủ vi và en.
2. Số liệu viết bằng số nguyên hoặc permille, không dùng số thực.
3. JSON chặt: không comment, không dấu phẩy thừa. Editor kiểm tra theo `content/schemas/*.schema.json`, đã khai báo trong `.vscode/settings.json`.
4. Kiểm tra:
   ```bash
   ./gradlew --settings-file settings-test.gradle :game:content:test :game:content:compileContent
   ```
   `compileContent` in bảng mô phỏng encounter; mọi encounter phải nằm trong dải tỉ lệ thắng của vai trò của nó (sau WP-A3).
5. Nếu đổi encounter hoặc quest chương 1, chạy `SCENARIO=chapter1 tools/test-agent/run-desktop.sh "$PWD/agent-reports/chapter1"`.
6. Xem trong game bằng cheat debug (flavor DEV). Xem trong Console qua trang Studio kinds (server chạy với `PXWORLD_CONTENT_WRITABLE=true` nếu cần sửa bằng Studio).

## Thêm content kind mới
1. Khai báo record `@Serializable` trong `game/content/.../ContentRecords.kt`.
2. Đăng ký trong `ContentKinds` (`ContentSchema.kt`), nạp trong `ContentLoader`, kiểm tham chiếu trong `ContentValidator`.
3. Chạy `./gradlew --settings-file settings-test.gradle :game:content:exportSchemas` để sinh schema mới. `ContentSchemaTest` sẽ fail nếu schema cũ.
4. Thêm ánh xạ schema vào `.vscode/settings.json`.
5. Nếu kind cần xuất hiện trong Studio, kiểm tra `/studio/kinds` có liệt kê.

## Không được
- Hard-code nội dung trong Kotlin.
- Sửa công thức trận để "chữa" cân bằng. Chỉnh số trong content; công thức chỉ đổi qua ADR và cập nhật golden.
- Tặng tài nguyên không qua reward hoặc grant chuẩn.
