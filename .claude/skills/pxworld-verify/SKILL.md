---
name: pxworld-verify
description: Run the PXWORLD verification gate (JVM tests, screen catalog check, desktop/cloud/explorer/console/Android test agents) and read the agents' report.json correctly. Use before any merge, after merging branches, or when asked to check that the project is green.
---

# Kiểm chứng PXWORLD

Nguồn chi tiết: `docs/handbook/06-testing.md`, `docs/handbook/07-setup-and-environments.md`.

## Thứ tự chạy
```bash
./gradlew --settings-file settings-test.gradle \
  :game:domain:test :game:application:test :game:screens:test :game:content:test \
  :game:infrastructure:test :game:content:compileContent :game:client:test \
  :server:app:test :tools:architecture:test
node tools/screen-catalog/catalog.mjs --check
tools/test-agent/run-desktop.sh "$PWD/agent-reports/scenario"
SCENARIO=chapter1 tools/test-agent/run-desktop.sh "$PWD/agent-reports/chapter1"
SCENARIO=cloud tools/test-agent/run-desktop.sh "$PWD/agent-reports/cloud"
MODE=explore tools/test-agent/run-desktop.sh "$PWD/agent-reports/explore"
tools/test-agent/console/run-console.sh --out="$PWD/agent-reports/console"
./gradlew :game:platform-android:assembleDebug
```
Nếu có `tools/test-agent/run-android.sh` (WP-A1), chạy thêm lệnh đó với emulator đang bật.

## Đọc kết quả
- **Test JVM:** khi fail, đọc `*/build/reports/tests/test/index.html`. Ghi số test theo module để so với `docs/PROGRESS.md`.
- **Test agent:** dòng cuối có dạng `PASSED|FAILED <mode> — visited X/Y registered screens`. Trong `report.json`:
  - `status` và `error`: stack lỗi;
  - `trace`: từng lời gọi;
  - `coverage.registeredNotVisited`: phải rỗng với explorer;
  - `screenshots`: đường dẫn ảnh.
- **Console agent:** `report.json` phải có `pageErrors` rỗng; số bước `ok` bằng tổng số bước.
- **Log:** `game.log` và `server.log` nằm trong thư mục report.

## Quy tắc báo cáo
- Chỉ báo PASSED khi đã **thấy** dòng PASSED.
- Test fail thì dán nguyên dòng lỗi đầu tiên, không tóm tắt lại.
- Không sửa test hay kịch bản để làm nó xanh nếu chưa hiểu nguyên nhân. Nới timeout phải ghi lý do.

## Xung đột tài nguyên khi nhiều agent chạy song song
| Tài nguyên | Chủ dùng |
|---|---|
| Cổng 47017 | Game desktop |
| Cổng 47117 | Android qua adb forward |
| Cổng 18080 | Server của test agent |
| Cổng 4173 | Vite preview |
| Emulator | Chỉ một agent |

Máy 16 GB chạy tối đa khoảng 4 build Gradle cùng lúc.
