# LVpxW - Game RPG Turn-Based với LibGDX

A [libGDX](https://libgdx.com/) project generated with [gdx-liftoff](https://github.com/libgdx/gdx-liftoff).

## Tổng quan (Overview)

**LVpxW** là một game nhập vai (RPG) turn-based 2D được phát triển bằng framework **LibGDX**, sử dụng mô hình kiến trúc **Hybrid MVC-ECS-Event-Driven**. Game lấy bối cảnh giả tưởng (fantasy), nơi người chơi vào vai một anh hùng trẻ tuổi với nhiệm vụ tìm kiếm cổ vật huyền thoại để ngăn chặn Dark Lord. Với cốt truyện phi tuyến tính, hệ thống chiến đấu turn-based, bản đồ khám phá, nhiệm vụ đa dạng, và các tính năng sáng tạo như chu kỳ ngày/đêm hay kỹ năng môi trường, game mang đến trải nghiệm nhập vai sâu sắc với thời gian chơi từ **5-10 giờ**.

Dự án được thiết kế để:
- **Hiệu suất cao**: Sử dụng **Ashley** (ECS) để quản lý hàng chục thực thể, **Box2D** cho va chạm.
- **Khả năng mở rộng**: Mô hình Hybrid MVC-ECS-Event-Driven cho phép dễ dàng thêm tính năng (multiplayer, crafting).
- **Khả năng làm việc nhóm**: Cấu trúc mã rõ ràng, dễ phân công (UI, logic, events).
- **Chạy đa nền tảng**: Desktop (Windows, macOS, Linux) và Android, với kích thước nhẹ (<100MB).

---

## Tính năng chính (Features)

* **Khám phá bản đồ Tiled:** Nhiều khu vực (village, forest, dungeon) với hệ thống camera theo dõi nhân vật, tương tác vật thể và NPC.
* **Chiến đấu theo lượt (Turn-Based):** Cơ chế turn-based rõ ràng với menu hành động đa dạng (Attack, Skill, Item, Run). Cho phép tính toán chiến thuật với hệ thống sát thương, chí mạng (critical) và hiệu ứng trạng thái (buff, debuff).
* **Hệ thống nhân vật và tiến trình RPG:** Nâng cấp chỉ số (HP, MP, Attack, Defense, Speed), hệ thống kỹ năng sống động và hòm đồ (inventory). Thu phục đồng hành (NPC) gia nhập đội tuyển.
* **Hệ thống nhiệm vụ (Quest):** Nhận và hoàn thành các Side Quests, Main Quests linh hoạt với bảng thông báo Quest Log.
* **Cốt truyện phi tuyến tính:** Lựa chọn đối thoại linh hoạt ảnh hưởng đến diễn biến và kết thúc game.
* **Lưu chữ & Giao diện:** Lưu thủ công/tự động trạng thái game, hỗ trợ nhiều slot save. UI HUD trực quan, hiển thị chi tiết chỉ số.

---

## Kiến trúc và Công nghệ (Architecture)

Dự án sử dụng mô hình **Hybrid MVC-ECS-Event-Driven**, kết hợp:
- **Ngôn ngữ:** Java
- **Engine chính:** LibGDX
- **Kiến trúc ECS:** Ashley (quản lý Entity, Component, System)
- **Vật lý (Physics):** Box2D (Dùng cho phát hiện va chạm)
- **Thiết kế Map:** Tiled Map Editor (.tmx)
- **Kiến trúc Sự kiện (Event-Driven):** Quản lý qua EventManager để trao đổi logic tách biệt.

Dữ liệu (Data) chủ yếu được thiết kế dưới dạng file cấu hình (JSON) chia nhỏ cho `PlayerData`, `EnemyData`, `QuestData`, `SkillData`, `MapData`, v.v.

---

## Nền tảng và Cách chạy Game (Platforms & Build)

This project uses [Gradle](https://gradle.org/) to manage dependencies. The Gradle wrapper was included, so you can run Gradle tasks using `gradlew.bat` or `./gradlew` commands.

- `core`: Main module with the application logic shared by all platforms.
- `lwjgl3`: Primary desktop platform using LWJGL3; was called 'desktop' in older docs.
- `android`: Android mobile platform. Needs Android SDK.

### Gradle Tasks Hữu Dụng
- `lwjgl3:run`: Khởi chạy ứng dụng game dưới nền tảng Desktop.
- `lwjgl3:jar`: Builds ứng dụng dưới dạng file runnable `jar`, output được đặt tại folder `lwjgl3/build/libs`.
- `build`: Biên dịch mọi source code và built ra gói phần mềm.
- `test`: Chạy toàn bộ các unit tests tích hợp bên trong project.
- `cleanIdea` / `cleanEclipse`: Xóa dữ liệu dự án IntelliJ hoặc Eclipse.
- `idea` / `eclipse`: Re-genarate lại dữ liệu IntelliJ/Eclipse cho project.
- `android:lint`: Kiểm tra validation code của dự án Android.

(Sử dụng cờ `--continue` để bỏ qua lỗi nhỏ không dừng quy trình build, `--daemon` để chạy ngầm tiến trình trên Gradle daemon).
