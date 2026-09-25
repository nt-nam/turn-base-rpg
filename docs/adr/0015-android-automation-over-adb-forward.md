# ADR 0015: Automation trên Android qua adb forward, ảnh chụp trả về trong phản hồi

- Trạng thái: Chấp nhận
- Ngày: 2026-09-26
- Liên quan: [ADR 0011](0011-three-layer-test-agent.md), [MASTER_PLAN §13.2](../MASTER_PLAN.md)

## Bối cảnh
Tiêu chí P2 trên Android là "chơi được từ tạo nhân vật đến hết chương 1", và phải được chứng minh bằng chính các scenario của test agent đang chạy trên desktop. Protocol automation lúc đó chỉ được bật trong launcher desktop, và `capture.screenshot` ghi PNG vào một đường dẫn trên máy chạy game. Điều này chỉ đúng khi runner và game nằm chung một máy. Với Android, runner nằm trên host còn game nằm trong thiết bị.

## Quyết định
1. `AndroidLauncher` khởi động `AutomationServer` khi `BuildFlavor.automation` bật (debug dùng QA, release dùng PILOT) và dừng nó ở `onDestroy`. Server vẫn bind `127.0.0.1:47017` bên trong thiết bị. Runner kết nối qua `adb forward tcp:<host> tcp:47017`, nên cổng không lộ ra mạng và chỉ ai có quyền adb mới điều khiển được.
2. Cổng phía host mặc định là 47117 (`HOST_PORT`) để chạy song song được với game desktop đang giữ 47017.
3. Gọi `capture.screenshot` không kèm `path` thì game trả PNG trong phản hồi (`{format, width, height, base64}`) và runner tự ghi file. Kèm `path` thì game ghi file như trước. `automation-client.mjs` luôn dùng dạng trả về, nên desktop và Android đi chung một đường. Mã hoá base64 dùng `Base64Coder` của libGDX vì `java.util.Base64` cần API 26 mà minSdk là 24.
4. `run-android.sh` build APK với cloud tắt (`-Ppxworld.apiUrl=`), tương đương `PXWORLD_API_URL=off` của desktop, để kết quả không phụ thuộc vào việc có gì đang nghe ở cổng 8080 của host.
5. Việc chuẩn bị thiết bị thuộc về script chứ không thuộc về game. Script tắt hộp thoại "Viewing full screen" (`immersive_mode_confirmations`), ẩn hộp thoại ANR và crash của hệ thống (`hide_error_dialogs`), giữ màn hình luôn sáng, rồi xoá dữ liệu app trước mỗi lượt.
6. Không nâng timeout của scenario. Trên emulator dùng GL phần mềm (swiftshader, 2400×1080), `core-loop` và `chapter1` đều qua với timeout hiện có.

## Hệ quả
- Một bộ scenario dùng chung cho mọi nền tảng, không có nhánh riêng cho Android.
- Ảnh chụp đi qua WebSocket. Một ảnh 2400×1080 nặng khoảng 50 KB–1.5 MB. Render thread chỉ đọc framebuffer; việc mã hoá PNG chạy trên thread của automation. Lý do là trên emulator vừa boot, mã hoá ngay trên render thread đã vượt giới hạn 10 giây của mỗi lệnh automation, đồng thời làm game đứng hình trong lúc đó.
- Bản release (PILOT) cũng mở automation trên loopback. Muốn gỡ khỏi bản phát hành thì đặt `PILOT.automation = false` hoặc tách flavor Gradle, và cập nhật ADR này.
- Nếu `Navigator` không dựng được một màn hình (ví dụ màn cần game đã nạp nhưng chưa có game), màn đó bị gỡ khỏi stack và lỗi trả về cho người gọi. Trước đây màn hỏng nằm lại trong stack và làm crash render thread ở lần `back()` kế tiếp.
