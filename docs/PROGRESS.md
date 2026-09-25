# Tiến độ viết lại

> Cập nhật: 2026-09-26 · Nhánh: `rewrite` · Handbook bàn giao: [handbook/README.md](handbook/README.md) · Kế hoạch: [MASTER_PLAN.md](MASTER_PLAN.md)

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
- [ ] WP-A1 Android runtime — **đạt trên nhánh `rewrite-android`, chưa merge**
- [ ] WP-A2 `sim-cli` — **xong trên nhánh `rewrite-sim`, chưa merge**
- [ ] WP-A3 Cân bằng theo tiến trình — **đang dở trên `rewrite-sim`** (mới có số đo)
- [ ] WP-A4 Telemetry explorer — **xong trên nhánh `rewrite-telemetry`, chưa merge**
- [ ] WP-A5 Spike TeaVM — **đang dở trên `rewrite-web`** (biên dịch được, chưa boot)
- [ ] WP-A6 Xoá `core/`, `lwjgl3/`, `android/`, `ios/`, `html/` — chờ merge A1
- [ ] WP-A7 Merge đợt A và chạy hồi quy toàn phần

## Điểm tạm dừng (2026-09-26)

Người dùng cho tạm dừng. Mọi việc đã commit, không tag, chưa push. Có 4 nhánh chưa merge, mỗi nhánh nằm trong một worktree riêng ở `D:/code/libgdx/LVpxW-wt/<tên>`. Mỗi worktree đã có sẵn `local.properties`.

| Nhánh | Worktree | Commit | Trạng thái | Việc tiếp theo |
|---|---|---|---|---|
| `rewrite-android` | `LVpxW-wt/android` | `c7cfc4e` `e7ca5d6` `c93bf11` WIP `d5794ee` | `core-loop`, `chapter1` PASSED; explorer 133/133 trên emulator. Có sửa lỗi `Navigator.push` giữ lại màn build lỗi | Chạy lại khi emulator boot nguội với `d5794ee` (xem WP-A1) |
| `rewrite-sim` | `LVpxW-wt/sim` | `db5b15c`, WIP `1428621` | `sim-cli` xong (13 test); số đo cân bằng đã ghi | WP-C0, rồi WP-A3 theo thiết kế ghi trong 05 |
| `rewrite-telemetry` | `LVpxW-wt/telemetry` | `02836fa` `c8bd4af` `6932eb3` | Xong: `ServerTest` 10/10, console agent 11/11 | Merge |
| `rewrite-web` | `LVpxW-wt/web` | WIP `73a3771` | Biên dịch JS được; chặn ở reflection `GlyphLayout$GlyphRun` | Đăng ký lớp cho reflection, chạy `web-smoke.mjs` (xem WP-A5) |

**Khi merge (WP-A7) cần chú ý:**
- Xung đột dự kiến ở `ci.yml`, `settings*.gradle`, `libs.versions.toml`, `ArchitectureTest.kt`, `StageAutomationDriver.kt` (android và web cùng sửa), `automation-client.mjs` và `run.mjs`.
- ADR: 0015 có trên `rewrite-android`; 0014 được dành cho cân bằng. Thêm các ADR mới vào `docs/adr/README.md`.
- Sau merge chạy lại toàn bộ desktop agent, vì các nhánh sửa file client dùng chung.

**Dọn dẹp chờ người dùng quyết định:** trong `.claude/worktrees/` còn 4 worktree bị tạo nhầm từ `main` (đã được ignore, không ảnh hưởng build). Lệnh dọn của Claude bị chặn, nên để người dùng tự dọn.

**Phát hiện quan trọng** (danh sách đầy đủ ở [05 §9](handbook/05-work-packages.md)):
- S1:
  - Token 12 giờ không có refresh, nên tài khoản khách mất cloud save (P-30).
  - Staff bị thu hồi vẫn còn quyền tới 12 giờ (P-31).
  - Explorer báo PASSED dù từng màn lỗi (P-50).
  - CI chưa từng chạy trên nhánh `rewrite` (P-61).
- Vòng khắc chế trong content khác thiết kế (WP-C0).

**Số liệu hiệu chỉnh:** 579 key UI (không phải 529); content có 9 test. Sau khi merge nhánh sim sẽ là 19 test.

Emulator trên máy này chỉ khởi động được với:

```
E:/Android/Sdk/emulator/emulator -avd Medium_Phone_API_36.0 -no-window -gpu swiftshader_indirect -feature -Vulkan -no-snapshot -no-audio -no-boot-anim
```
