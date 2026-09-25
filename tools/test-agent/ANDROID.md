# Test agent trên Android

`run-android.sh` chạy cùng các scenario của desktop (`core-loop`, `chapter1`, `MODE=explore`) trên emulator hoặc trên thiết bị thật đang cắm.

```bash
tools/test-agent/run-android.sh                                   # core-loop, báo cáo ở tools/test-agent/reports/android-<thời điểm>
SCENARIO=chapter1 tools/test-agent/run-android.sh "$PWD/agent-reports/chapter1"
MODE=explore tools/test-agent/run-android.sh
```

| Biến | Mặc định | Ý nghĩa |
|---|---|---|
| `SCENARIO`, `MODE` | `core-loop`, `scenario` | giống `run-desktop.sh` |
| `HOST_PORT` | `47117` | cổng trên host được `adb forward` tới `127.0.0.1:47017` trong thiết bị; để 47017 cho game desktop |
| `SKIP_BUILD` | rỗng | đặt `1` để dùng lại APK đã build |
| `PXWORLD_API_URL` | rỗng (tắt cloud) | URL server nhúng vào APK debug; `off` cũng có nghĩa là tắt |
| `AVD`, `EMULATOR_FLAGS` | AVD đầu tiên, `-feature -Vulkan` | chỉ dùng khi chưa có thiết bị nào và script phải tự bật emulator |
| `KEEP_EMULATOR` | rỗng | đặt `1` để giữ emulator mà script đã tự bật (mặc định thì tắt nó khi xong) |
| `BOOT_TIMEOUT`, `GAME_START_TIMEOUT` | `600`, `180` giây | chờ `sys.boot_completed` và chờ game trả lời automation |
| `ANDROID_SDK_ROOT` / `ANDROID_HOME` / `ADB` | đọc `sdk.dir` trong `local.properties` | vị trí SDK và adb |

Thư mục báo cáo gồm `report.json`, ảnh chụp (game gửi PNG qua automation, xem ADR 0015), `logcat.txt` (chỉ lấy pid của app), `logcat-crash.txt` (buffer crash) và `device-start.png` (ảnh `screencap` của cả màn hình thiết bị ngay khi game trả lời, dùng để thấy hộp thoại hệ thống nếu có hộp thoại nào che game).

## Những điều đã gặp trên máy Windows này

- Emulator chỉ khởi động được khi có `-gpu swiftshader_indirect -feature -Vulkan`. Thiếu `-feature -Vulkan` thì nó crash lúc dò Vulkan. Vì vậy đây là giá trị mặc định của `EMULATOR_FLAGS`.
- Sau khi boot, GL phần mềm làm System UI chậm, và hộp thoại "System UI isn't responding" có thể hiện đè lên game. Automation không bị ảnh hưởng, vì lệnh `ui.tap` đưa thẳng vào `Stage` chứ không đi qua màn hình thật. Dù vậy, script vẫn đặt `settings put global hide_error_dialogs 1` để ảnh `screencap` sạch.
- Lần đầu mở app ở chế độ immersive, Android hiện hộp thoại "Viewing full screen". Script tắt nó bằng `settings put secure immersive_mode_confirmations confirmed`.
- Nếu màn hình tắt hoặc khoá, `GLSurfaceView` ngừng render, và mọi lệnh automation hết hạn sau 10 giây. Script giữ màn hình sáng (`svc power stayon true`, `screen_off_timeout`) và mở khoá (`wm dismiss-keyguard`).
- Trong Git Bash, MSYS tự đổi các tham số bắt đầu bằng `/` thành đường dẫn Windows. Vì vậy mọi lệnh adb đều chạy với `MSYS_NO_PATHCONV=1`, và APK được truyền bằng đường dẫn tương đối.
- Nếu không đóng `adb forward` thì cổng host vẫn bị giữ. Script tự gỡ forward khi thoát.
- Trên AVD `Medium_Phone_API_36.0` (2400×1080, swiftshader), `core-loop` chạy khoảng 2 phút và `chapter1` khoảng 2 phút 10 giây, đều không phải nâng timeout. Ảnh chụp màn battle nặng khoảng 1.5 MB.
- Cài mới thì không có save legacy để nhập như trên máy dev desktop. Vì vậy explorer tự tạo game mới khi menu chính không có nút "Tiếp tục".
- Bản đồ `map.ashwaste_01` hiện thành các dải đen xen kẽ, cả trên Android lẫn desktop. Đây là lỗi dữ liệu bản đồ hoặc art, không phải lỗi của nền tảng.
