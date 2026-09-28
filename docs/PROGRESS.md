# Tiến độ viết lại

> Cập nhật: 2026-09-29 · Nhánh: `rewrite` · Handbook bàn giao: [handbook/README.md](handbook/README.md) · Kế hoạch: [MASTER_PLAN.md](MASTER_PLAN.md)

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

Kế hoạch chi tiết, tiêu chí nghiệm thu và lệnh test của từng việc nằm ở [handbook/05-work-packages.md](handbook/05-work-packages.md).

- [x] P3: asset pipeline — nén nền trận, cắt tileset, font OFL, `art/LICENSES.md`
- [x] P4: màn cloud save trong game (đăng nhập, đồng bộ, xử lý 409), gửi telemetry từ client, nhận thư trong game
- [x] Android: URL máy chủ và flavor qua BuildConfig (debug → `10.0.2.2:8080`, cleartext chỉ ở debug)
- [x] Console: so sánh hai bản content, lịch sử save và khôi phục revision (có audit)
- [x] Test kiến trúc: `:tools:architecture` (tự quét import, không dùng Konsist)
- [x] JSON Schema: `content/schemas` sinh từ record, Studio có biểu mẫu sinh từ schema
- [x] Âm thanh: `AudioDirector` phát nhạc theo khám phá/trận và SFX, tôn trọng cài đặt
- [x] Handbook bàn giao `docs/handbook/` và 6 project skill trong `.claude/skills/`
- [x] WP-A1 Android runtime — đã merge; đạt P2 trên emulator (còn chạy lại khi boot nguội)
- [x] WP-A2 `sim-cli` — đã merge
- [ ] WP-A3 Cân bằng theo tiến trình — số đo đã merge; làm tiếp sau WP-C0
- [x] WP-A4 Telemetry explorer — đã merge
- [ ] WP-A5 Spike TeaVM — đã merge dạng opt-in; còn lỗi reflection chặn boot
- [ ] WP-A6 Xoá `core/`, `lwjgl3/`, `android/`, `ios/`, `html/` — sẵn sàng
- [ ] WP-A7 Hợp nhất đợt A — đã merge, test JVM xanh; **chưa chạy các kịch bản test agent trên bản merge**
- [ ] WP-X8 Hoàn thiện handbook 03, 06, 09 — ghi chú nghiên cứu ở 05 §8.1

## Điểm tạm dừng (2026-09-29)

Đã bàn giao. Nhánh `rewrite` trên GitHub chứa toàn bộ công việc: 4 nhánh đợt A đã merge, cùng handbook, `AGENTS.md`, `CLAUDE.md`, `.claude/skills/` và `.claude/agents/`.

**Người làm tiếp bắt đầu từ đâu:** đọc [AGENTS.md](../AGENTS.md) §1, rồi [handbook/README.md](handbook/README.md).

**Việc tiếp theo, theo thứ tự:**
1. Chạy các kịch bản test agent trên bản merge. Đây là **phần còn lại của WP-A7**; chỉ test JVM đã được chạy. Lệnh:
   ```bash
   tools/test-agent/run-desktop.sh "$PWD/agent-reports/scenario"
   SCENARIO=chapter1 tools/test-agent/run-desktop.sh "$PWD/agent-reports/chapter1"
   SCENARIO=cloud tools/test-agent/run-desktop.sh "$PWD/agent-reports/cloud"
   MODE=explore tools/test-agent/run-desktop.sh "$PWD/agent-reports/explore"
   tools/test-agent/console/run-console.sh --out="$PWD/agent-reports/console"
   ```
   Nên chạy kỹ vì cả nhánh android lẫn nhánh web đều sửa `StageAutomationDriver.kt`, `Navigation.kt`, `automation-client.mjs` và `run.mjs`.
2. WP-A1: chạy lại `tools/test-agent/run-android.sh` khi emulator boot nguội.
3. WP-A6: xoá module legacy.
4. WP-C0, rồi WP-A3.
5. WP-A5: sửa lỗi reflection.
6. WP-X8: viết nốt handbook.
7. Các quyết định chờ chủ dự án ở [05 §8.2](handbook/05-work-packages.md).

**Phát hiện quan trọng** (danh sách đầy đủ ở [05 §9](handbook/05-work-packages.md)):
- S1:
  - Token 12 giờ không có refresh, nên tài khoản khách mất cloud save (P-30).
  - Staff bị thu hồi vẫn còn quyền tới 12 giờ (P-31).
  - Explorer báo PASSED dù từng màn lỗi (P-50).
  - CI chưa từng chạy trên nhánh `rewrite` (P-61, quyết định D-05).
- Vòng khắc chế trong content khác thiết kế (WP-C0).

**Số liệu hiệu chỉnh:** 579 key UI; content 19 test; server 10 test; sim-cli 13 test.

Emulator trên máy dev Windows chỉ khởi động được với:

```
E:/Android/Sdk/emulator/emulator -avd Medium_Phone_API_36.0 -no-window -gpu swiftshader_indirect -feature -Vulkan -no-snapshot -no-audio -no-boot-anim
```
