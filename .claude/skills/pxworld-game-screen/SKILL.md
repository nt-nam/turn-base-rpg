---
name: pxworld-game-screen
description: Add or complete a PXWORLD game client screen (libGDX scene2d) the project way — screen catalog ID first, screen class in the feature file, registration in DefaultScreens, localization vi/en, automation testIds, explorer coverage and tests. Use for any work on game.* ScreenIds.
---

# Thêm hoặc hoàn thiện một màn game

Nguồn chi tiết: `docs/handbook/08-recipes-game-client.md`. Skill này là checklist để không bỏ sót bước.

## Checklist
1. **ScreenId**
   - Tìm ID trong `docs/screens/screens.json` (surface `game`).
   - Nếu chưa có, thêm vào `tools/screen-catalog/catalog.mjs`, chạy `node tools/screen-catalog/catalog.mjs`. Lệnh này sinh `GameScreenId.kt`, `screenIds.ts` và `docs/screens/*`.
   - Chạy `node tools/screen-catalog/catalog.mjs --check`.
2. **Luật game trước, UI sau**
   - Hành động nào đổi state thì đặt trong `game/application` (`GameRules`/`Progression`/`Collection`…), có unit test.
   - View không chứa luật.
3. **Màn**
   - Đặt vào file tính năng tương ứng trong `game/client/src/main/kotlin/com/pxworld/client/screens/<feature>/`.
   - Theo mẫu một màn có sẵn cùng module; dùng helper của `ScreenSupport.kt`, `UiKit.kt` và `Widgets.kt`.
   - Lấy dịch vụ qua `ScreenContext`, không tạo singleton.
4. **Đăng ký:** thêm vào `DefaultScreens.kt`, và nút hoặc đường điều hướng tới màn (`Navigator`, dùng `replaceFrom` khi thay đúng màn nguồn).
5. **Chuỗi UI**
   - Thêm key `"ui.<khu>.<tên>": ["tiếng Việt", "English"]` vào `extendedStrings` trong `tools/content-migrator/ui-strings-extended.mjs`.
   - Chạy `node tools/content-migrator/ui-strings.mjs` để sinh lại `content/localization/{vi,en}.ui.json`. Script báo lỗi nếu key trùng.
   - Chạy `:game:client:test` (`ClientTextTest`).
6. **Automation:** mọi actor tương tác có testId ổn định. Nếu màn cần tham số hay dữ liệu, cho explorer cách mở được (cheat debug hoặc tham số fixture trong `tools/test-agent/run.mjs`).
7. **Không dùng `java.io.File`** trong client. Dùng `Gdx.files`, test kiến trúc sẽ chặn nếu vi phạm.
8. **Kiểm chứng**
   - `./gradlew --settings-file settings-test.gradle :game:application:test :game:client:test :tools:architecture:test`.
   - `MODE=explore tools/test-agent/run-desktop.sh "$PWD/agent-reports/explore"`: 0 lỗi và màn mới có trong `coverage.visited`.
   - Nếu màn nằm trên luồng chính, chạy thêm `core-loop` và `chapter1`.
9. **Test case thủ công:** thêm `TC-<KHU>-<NN>` vào `docs/handbook/06-testing.md` theo `templates/test-case.md`.
