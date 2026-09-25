# Tiến độ viết lại

> Cập nhật: 2026-09-25 · Nhánh: `rewrite` · Kế hoạch: [MASTER_PLAN.md](MASTER_PLAN.md)

## Trạng thái theo phase

| Phase | Trạng thái | Bằng chứng |
|---|---|---|
| P0 Nền móng | **Xong** | `build-logic` convention plugins, version catalog (Kotlin 2.4.20), ADR 0001–0013, CI (JVM + desktop agent + Android), codegen `GameScreenId` + `screenIds.ts` có `--check` |
| P1 Domain + nội dung | **Xong phần lõi** | Battle engine tất định (35 test, 6 golden); `content/` 331 bản ghi, 0 lỗi; compiler + mô phỏng cân bằng; luật game 24 test; save v2 tương thích tiến + importer legacy |
| P2 Vertical slice client | **Đạt tiêu chí trên desktop** | 128 màn launch chạy thật; chương 1 chơi hết bằng test agent; save legacy tự nhập; Android APK build được |
| P3 Asset pipeline | **Xong phần lõi** | `:tools:asset-pipeline:buildAssets` chỉ đóng gói file được tham chiếu, cắt tileset theo map, nền trận JPG theo mùa, font Be Vietnam Pro (OFL); APK debug 18,8 MB (trước 75 MB) |
| P4 Backend + Console | **Lát đầu chạy thật** | `server/app` (Ktor 3.6): 7 test trên H2; Console React chạy thật với test agent qua CDP (9/9 bước, 17 màn); game có đăng nhập, sao lưu/khôi phục cloud, xử lý xung đột, hộp thư, telemetry có đồng ý — kịch bản `cloud` PASSED với server thật; ADR 0007 cập nhật |
| P5–P8, S1–S4 | Chưa bắt đầu | — |

## Tiêu chí P2 (MASTER_PLAN §15)

| Tiêu chí | Kết quả |
|---|---|
| Chơi được từ tạo nhân vật đến hết chương 1 trên desktop | **Đạt** — kịch bản `chapter1` của test agent PASSED (gặp trưởng làng → thắng kẻ đột nhập → thợ rèn → lữ khách → Hoang Mạc Tro) |
| … và trên Android | **Một phần** — APK debug build được (`:game:platform-android:assembleDebug`); chưa chạy thử vì emulator trên máy này crash (thiếu OpenGL phần mềm) |
| Scenario lõi xanh trong CI | **Đã cấu hình** — job `desktop-agent` chạy `core-loop`, `chapter1`, explorer dưới xvfb; chưa push nên chưa có lượt chạy CI thật |
| Save cũ import được | **Đạt** — `LegacySaveMigration` chạy khi desktop khởi động, test trên 3 save thật |

## Số liệu

- **Màn hình:** 133/357 màn game của launch đã chạy thật (37,3%); explorer phủ 133/133 màn đăng ký, 0 lỗi, invariant giữ trên mọi màn.
- **Test JVM:** domain 35 · application 30 · screens 3 · content 7 · infrastructure 8 · client 2 · server 7 · legacy core 46.
- **Test agent:** `core-loop` PASSED, `chapter1` PASSED, `cloud` PASSED, explorer PASSED, `console` PASSED (`tools/test-agent/console/run-console.sh`).
- **Backend:** đăng ký/khách/đăng nhập, cloud save có revision (409 khi xung đột) + 30 bản lịch sử, phát hành/đẩy content theo kênh, xác thực replay trận, telemetry, dashboard, `/metrics`, tìm người chơi, tặng qua thư, khoá/gỡ khoá, audit, tải báo cáo agent, Studio sửa bản ghi có kiểm tra.
- **Chuỗi UI:** 529 key × 2 ngôn ngữ (vi, en), kiểm tra tự động.

## Lỗi legacy đã sửa (có test hoặc kịch bản chứng minh)

Chí mạng không bao giờ xảy ra · kỹ năng 2/3 gần như không dùng · hòa bị tính là thua · trang bị không cộng chỉ số và không vào trận · điểm danh không trả quà · mua hàng không lưu · ghép sao xóa anh hùng trong đội hình · save ghi vào thư mục repo · `.gitignore` giấu 7 file loader · không có âm thanh/nhạc (đã sửa: `AudioDirector` + `content/audio_cues`).

## Phát hiện và quyết định trong lúc làm

1. Kỹ năng tốn năng lượng làm tuyệt kỹ gần như không dùng được → hồi chiêu cho kỹ năng, năng lượng chỉ cho tuyệt kỹ.
2. Fleks 2.15 cần Kotlin 2.4 và phát bytecode Java 17 → client nhắm JVM 17; rủi ro iOS/RoboVM ghi trong ADR 0001.
3. Checksum save phải tính trên cây JSON đã lưu, không mã hóa lại — nếu không mọi bản lưu cũ sẽ bị báo hỏng khi thêm trường mới.
4. Migrator từng xóa sạch `content/` → nay từ chối chạy khi `content/` tồn tại.
5. Automation phải cuộn tới control và kiểm tra bị che trước khi chạm, và bấm ở khung hình sau (ScrollPane chỉ cập nhật vị trí khi vẽ).
6. `local.properties` trỏ tới Android SDK không tồn tại → đã sửa cục bộ thành `E:/Android/Sdk` (file không nằm trong git).
7. Audit ghi cùng mili-giây bị đảo thứ tự → thêm cột `seq` tự tăng, sắp theo `seq`.
8. Kiểu `screen` của trang Console là union `WebScreenId` sinh từ catalog → trang dùng ID ngoài registry không biên dịch được.
10. Kết quả trận thay màn *đang ở trên cùng*: nếu modal (tạm dừng, nhật ký) đang mở, modal bị thay còn màn trận cũ kẹt lại trong stack → `Navigator.replaceFrom(screen, …)` thay đúng màn trận.
11. Màn đăng nhập chỉ đọc chữ qua sự kiện gõ phím → chữ dán/tự điền bị mất và bị xoá khi màn dựng lại; nay đọc thẳng từ ô nhập. Sai mật khẩu từng hiện "phiên đã hết" → nay báo đúng.
12. Token của tài khoản không còn tồn tại trả 403 nên client không tự đăng xuất → nay 401. Thông tin đăng nhập cloud lưu theo từng URL máy chủ, không dùng chung giữa môi trường.
13. Explorer từng dựa vào dữ liệu của save legacy (không có trang bị) → bỏ sót 8 màn; nay tự tạo dữ liệu bằng cheat debug.
9. Console chưa có SSO; staff đăng nhập bằng mật khẩu PBKDF2 do server giữ (Keycloak hoãn, ghi trong ADR 0007).

## Việc còn lại gần nhất

- [x] P3: asset pipeline — nén nền trận, cắt tileset, font OFL, `art/LICENSES.md`
- [x] P4: màn cloud save trong game (đăng nhập, đồng bộ, xử lý 409), gửi telemetry từ client, nhận thư trong game
- [x] Android: URL máy chủ và flavor qua BuildConfig (debug → `10.0.2.2:8080`, cleartext chỉ ở debug)
- [x] Console: so sánh hai bản content, lịch sử save và khôi phục revision (có audit)
- [x] Test kiến trúc: `:tools:architecture` (tự quét import, không dùng Konsist)
- [x] JSON Schema: `content/schemas` sinh từ record, Studio có biểu mẫu sinh từ schema
- [x] Âm thanh: `AudioDirector` phát nhạc theo khám phá/trận và SFX, tôn trọng cài đặt
- [ ] **Android runtime (đang làm dở):** APK debug đã cài và chạy trên emulator, hiện màn `boot.legal_notice` (chưa có crash). Việc tiếp theo: bật `AutomationServer` trong `AndroidLauncher` khi flavor cho phép automation, `adb forward tcp:47017 tcp:47017`, chạy `core-loop` trên Android.
- [ ] Lọc telemetry theo thời gian trong Console
- [ ] `sim-cli` tách riêng: chạy đội hình tuỳ ý (`--encounter --heroes --seeds`), in tỉ lệ thắng, số vòng, sát thương theo đơn vị
- [ ] Cân bằng: mô phỏng hiện chỉ so "đủ 6 lớp" với "starter một mình"; cần đội hình theo tiến trình chương (xem `content/BALANCE_NOTES.md`)
- [ ] Spike TeaVM (web client) — P6
- [ ] Xoá `core/` và `lwjgl3/` trong một commit riêng "remove legacy core" sau khi Android đạt tiêu chí P2

## Điểm tạm dừng (2026-09-25)

Làm tiếp từ mục **Android runtime** ở trên. Emulator trên máy này chỉ khởi động được với:

```
E:/Android/Sdk/emulator/emulator -avd Medium_Phone_API_36.0 -no-window -gpu swiftshader_indirect -feature -Vulkan -no-snapshot -no-audio -no-boot-anim
```

(không có `-feature -Vulkan` thì emulator crash khi dò Vulkan). Sau khi khởi động: `adb install -r game/platform-android/build/outputs/apk/debug/platform-android-debug.apk`, `adb shell am start -n com.game.pxworld/com.pxworld.android.AndroidLauncher`. Lần đầu Android hiện hộp thoại hệ thống "Viewing full screen" che game.

Kiểm chứng gần nhất: JVM test xanh (gồm server, application, content, client, architecture); test agent `core-loop`, `chapter1`, `cloud`, explorer 133/133 và `console` 10/10 đều PASSED.
