# Tiến độ viết lại

> Cập nhật: 2026-09-25 · Nhánh: `rewrite` · Kế hoạch: [MASTER_PLAN.md](MASTER_PLAN.md)

## Trạng thái theo phase

| Phase | Trạng thái | Bằng chứng |
|---|---|---|
| P0 Nền móng | **Xong** | `build-logic` convention plugins, version catalog (Kotlin 2.4.20), ADR 0001–0013, CI (JVM + desktop agent + Android), codegen `GameScreenId` + `screenIds.ts` có `--check` |
| P1 Domain + nội dung | **Xong phần lõi** | Battle engine tất định (35 test, 6 golden); `content/` 331 bản ghi, 0 lỗi; compiler + mô phỏng cân bằng; luật game 24 test; save v2 tương thích tiến + importer legacy |
| P2 Vertical slice client | **Đạt tiêu chí trên desktop** | 128 màn launch chạy thật; chương 1 chơi hết bằng test agent; save legacy tự nhập; Android APK build được |
| P3 Asset pipeline | Chưa bắt đầu | APK 75 MB — việc nén/cắt asset nằm ở đây |
| P4–P8, S1–S4 | Chưa bắt đầu | — |

## Tiêu chí P2 (MASTER_PLAN §15)

| Tiêu chí | Kết quả |
|---|---|
| Chơi được từ tạo nhân vật đến hết chương 1 trên desktop | **Đạt** — kịch bản `chapter1` của test agent PASSED (gặp trưởng làng → thắng kẻ đột nhập → thợ rèn → lữ khách → Hoang Mạc Tro) |
| … và trên Android | **Một phần** — APK debug build được (`:game:platform-android:assembleDebug`); chưa chạy thử vì emulator trên máy này crash (thiếu OpenGL phần mềm) |
| Scenario lõi xanh trong CI | **Đã cấu hình** — job `desktop-agent` chạy `core-loop`, `chapter1`, explorer dưới xvfb; chưa push nên chưa có lượt chạy CI thật |
| Save cũ import được | **Đạt** — `LegacySaveMigration` chạy khi desktop khởi động, test trên 3 save thật |

## Số liệu

- **Màn hình:** 128/357 màn game của launch đã chạy thật (35,9%); explorer phủ 125–127 màn/lượt, 0 lỗi, invariant giữ trên mọi màn.
- **Test JVM:** domain 35 · application 24 · screens 3 · content 7 · infrastructure 8 · client 2 · legacy core 46.
- **Test agent:** `core-loop` PASSED, `chapter1` PASSED, explorer PASSED.
- **Chuỗi UI:** 529 key × 2 ngôn ngữ (vi, en), kiểm tra tự động.

## Lỗi legacy đã sửa (có test hoặc kịch bản chứng minh)

Chí mạng không bao giờ xảy ra · kỹ năng 2/3 gần như không dùng · hòa bị tính là thua · trang bị không cộng chỉ số và không vào trận · điểm danh không trả quà · mua hàng không lưu · ghép sao xóa anh hùng trong đội hình · save ghi vào thư mục repo · `.gitignore` giấu 7 file loader · không có âm thanh/nhạc (chưa làm lại — xem bên dưới).

## Phát hiện và quyết định trong lúc làm

1. Kỹ năng tốn năng lượng làm tuyệt kỹ gần như không dùng được → hồi chiêu cho kỹ năng, năng lượng chỉ cho tuyệt kỹ.
2. Fleks 2.15 cần Kotlin 2.4 và phát bytecode Java 17 → client nhắm JVM 17; rủi ro iOS/RoboVM ghi trong ADR 0001.
3. Checksum save phải tính trên cây JSON đã lưu, không mã hóa lại — nếu không mọi bản lưu cũ sẽ bị báo hỏng khi thêm trường mới.
4. Migrator từng xóa sạch `content/` → nay từ chối chạy khi `content/` tồn tại.
5. Automation phải cuộn tới control và kiểm tra bị che trước khi chạm, và bấm ở khung hình sau (ScrollPane chỉ cập nhật vị trí khi vẽ).
6. `local.properties` trỏ tới Android SDK không tồn tại → đã sửa cục bộ thành `E:/Android/Sdk` (file không nằm trong git).

## Việc còn lại gần nhất

- [ ] P3: asset pipeline — Aseprite/TexturePacker, nén nền trận (38 MB), cắt tileset 6052×5837, font OFL thay Arial Unicode, `art/LICENSES.md`
- [ ] Âm thanh: `AudioDirector` phát nhạc theo vùng/trận và SFX, tôn trọng cài đặt
- [ ] Android: chạy thử trên thiết bị thật hoặc emulator có GPU
- [ ] Spike TeaVM (web) và test kiến trúc (Konsist)
- [ ] JSON Schema xuất cho Studio; `sim-cli` tách riêng
- [ ] Cân bằng: xem `content/BALANCE_NOTES.md`
