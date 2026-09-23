# Tài Liệu Nội Bộ: Hướng Dẫn Phát Triển Game RPG Turn-based 2D

Tài liệu này là cẩm nang dành cho nhóm phát triển dự án. Tất cả cấu trúc, lô-trình phát triển, và kiến trúc gốc được tổng hợp tại đây để kỹ sư tham khảo.

---

## 1. Phân Tích Cấu Trúc Dự Án (`core/src`)

Dự án này là một game nhập vai theo lượt (Turn-Based RPG) được xây dựng trên framework LibGDX, sử dụng kiến trúc **Hybrid MVC-ECS-Event-Driven** qua thư viện Ashley.

```text
com.game/
├── core/                # Logic cốt lõi của hệ thống chiến đấu (BattleSimulator, TurnExecution)
├── ecs/                 # Hệ thống Entity Component (Ashley ECS)
│   ├── component/       # Các định nghĩa dữ liệu (Stats, Animation, State...)
│   ├── factory/         # Khởi tạo Entity từ dữ liệu
│   ├── systems/         # Các hệ thống xử lý logic (TurnProcessor, Rendering...)
├── managers/            # Quản lý tài nguyên và luồng sự kiện
│   ├── event/           # Event Bus quản lý giao tiếp giữa các thành phần
│   └── GAssetManager/   # Quản lý vòng đời tài nguyên (Textures, Sounds, Fonts)
├── screens/             # Quản lý các trạng thái màn hình (Game State Management)
│   ├── battle/          # Logic và UI cho màn hình chiến đấu
│   ├── main/            # Màn hình khám phá thế giới (World Map)
│   └── service/         # Các màn hình chức năng (Inventory, Quest, Character)
├── ui/                  # Các Widget và Overlay giao diện người dùng
└── utils/               # Tiện ích bổ trợ (Constants, DataHelper, GameSession)
```

Ngoài ra gốc dự án cũng có những thư mục dành cho xử lí khác:
- `managers/`, `handlers/`: Logic như InputManager, BattleManager...
- `events/`: InteractionEvent, BattleEvent, QuestEvent.
- `data/`: GameConfig, PlayerData, EnemyData, QuestData...

---

## 2. Luồng Dữ Liệu & Vòng Đời Ứng Dụng

1.  **Khởi tạo**: `MainGame` thiết lập môi trường đồ họa (`Viewport`, `Stage`) và nạp tài nguyên cơ bản.
2.  **Nạp dữ liệu**: `DataHelper` giải mã (parse) các file JSON từ `assets/data` để cấu hình quái vật, kỹ năng và vật phẩm.
3.  **Luồng màn hình**:
    -   `MenuScreen` -> `WorldMapScreen` -> `BattleScreen`.
    -   `ScreenManager` xử lý việc chuyển đổi và quản lý tài nguyên cần thiết cho từng màn hình.
4.  **Logic chiến đấu**:
    -   `BattleScreen` khởi tạo `BattleSimulator`.
    -   Kết quả từ simulator được gửi về thông qua `EventManager` để UI/Animation cập nhật.

---

## 3. Lộ trình phát triển đề xuất (Roadmap & Step by Step)

Lộ trình phát triển được thiết kế để tạo ra các phần nhỏ đều có thể kiểm chứng được ngay lập tức (phát triển lặp). Tổng thời gian dự tính khoảng 2-3 tháng. 

- **Giai đoạn 1 (Thiết lập dự án):** Setup ECS (Ashley), Screen Manager, chia Module `core/desktop/android`. (1 tuần)
- **Giai đoạn 2 (Core Loop):** Load Asset, tạo camera, hệ thống Render System, Input, Load map từ Tiled. (1 tuần)
- **Giai đoạn 3 (Di chuyển & Khám phá):** Movement, Pathfinding, trigger sự kiện qua Tile, Tương tác (NPC, Rương). (1-2 tuần)
- **Giai đoạn 4 (Hệ thống battle):** Battle Flow (PlayerTurn -> EnemyTurn), Damage System, Skill System, UI chiến trận. (2-3 tuần)
- **Giai đoạn 5 (Tính năng RPG):** Level Up, Trang Bị (Inventory), State/Effect, Hệ thống Quest, Save/Load Game. (2-4 tuần)
- **Giai đoạn 6 (Polish):** Thêm BGM/SFX, Trau chuốt UI/Animation, Debug & Tối ưu, Release. (2 tuần)

---

## 4. Checklist Kiểm Tra Chất Lượng (QA)

Khi phát triển/thêm tính năng mới hoặc Review code:
*   [ ] **Core Gameplay:** Nhân vật di chuyển mượt mà, có collision (Box2D), trigger tương tác đúng điểm.
*   [ ] **Battle:** Damage/Heal System hoạt động đứng như logic, Turn Order chính xác, Game Over / Victory trả về đúng thưởng.
*   [ ] **RPG & Progression:** Tăng level/stats đúng bảng exp, sử dụng đúng tài nguyên (HP/MP) khi buff/skill, trang bị hiển thị.
*   [ ] **Quest & World:** Quest lưu được log (Chưa làm, đang làm, hoàn thành). Map có camera hoạt động tốt.
*   [ ] **UI & Menu:** Fade hiệu ứng hiển thị êm, text báo damage chuẩn rõ ràng. Menu Start, Pause.
*   [ ] **Hệ Thống Phụ:** Load/Save file JSON ghi đè tốt. Memory Leak đã check sạch (AssetManager dispose). Target 60fps.
*   [ ] Lỗi "Null pointer" trong asset loaders hoặc event queues. Đảm bảo flow chơi bình thường ko crash.

---

## 5. Quy tắc chuẩn hóa lập trình và Git (Git Guidelines)

*   **Nhánh `main`:** Mã gốc chuẩn không lỗi, có thể Release.
*   **Nhánh `dev`:** Code đang tích cực phát triển nhưng chưa hoàn chỉnh.
*   **Nhánh `feature/<tên>` / `bugfix/<tên>`:** Tách tính năng và sửa lỗi. Phải tạo Pull Request vào `dev`.

### Commits Message Rules
Đặt tên theo chuẩn conventional:
*   `feat: add boss level with new attack pattern`
*   `fix: resolve null pointer on map loading`
*   `refactor: split GameScreen into separate classes`
*   `chore: update gradle wrapper`

### Trước khi Commit:
*   [ ] Dự án phải Build chạy được bằng `./gradlew lwjgl3:run` không lỗi hệ thống.
*   [ ] Không chứa `System.out.println()` dư thừa. Không commit `.DS_Store`, `.iml`, `local.properties`. 
*   [ ] Đã phân tách code rõ ràng các file `*UI.java` vs `*System.java`.

---

## 6. Ghi Chú Kỹ Thuật (Nợ kỹ thuật - Tech Debt)
- **Placeholder Screens:** Các màn hình như `CharacterScreen`, `QuestScreen` trong gói `service` hiện đang là khung mẫu (boilerplate) chờ triển khai logic chi tiết.
- **Resource Management:** Đang chuyển đổi dần sang việc sử dụng `GAssetManager` tập trung thay vì nạp thủ công tại các Screen.
- **Debug Tools:** Dọn sạch class công cụ bổ trợ (như CheckRegionScreen) trước khi build bản Release cho người dùng.
