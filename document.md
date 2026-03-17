# Tài Liệu Cấu Trúc Dự Án LibGDX (Turn-Based RPG)

Dự án này là một game nhập vai theo lượt (Turn-Based RPG) được xây dựng trên framework LibGDX, sử dụng kiến trúc Entity Component System (ECS) thông qua thư viện Ashley.

## 1. Phân Tích Cấu Trúc Thư Mục (core/src)

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

## 2. Các Thành Phần Hệ Thống Chính

### 2.1 Core Logic (`com.game.core`)
Đây là bộ não của hệ thống chiến đấu, tách biệt hoàn toàn khỏi logic hiển thị:
- **`BattleSimulator`**: Mô phỏng và tính toán kết quả trận đấu dựa trên chỉ số.
- **`TurnExecution`**: Quản lý vòng đời của một lượt đánh, từ việc chọn kỹ năng đến khi xác nhận kết quả.
- **`StatCalculator`**: Xử lý các công thức tính toán chỉ số (Damage, Speed, HP).

### 2.2 Managers & Infrastructure
- **`MainGame`**: Central Hub kế thừa `com.badlogic.gdx.Game`, khởi tạo các singleton managers.
- **`ScreenManager`**: Điều hướng giữa các màn hình với hỗ trợ chuyển cảnh và cache.
- **`EventManager`**: Hệ thống giao tiếp decoupled (tách rời), cho phép các thành phần gửi thông điệp mà không cần tham chiếu trực tiếp.
- **`GameSession`**: Lưu trữ trạng thái bền vững của người chơi trong suốt phiên làm việc.

### 2.3 Hệ Thống ECS (Ashley)
Hệ thống sử dụng Ashley để quản lý logic thực thể phức tạp:
- **Components**: Đơn vị chứa dữ liệu thuần túy (e.g., `PositionComponent`, `CombatComponent`).
- **Systems**: Chứa logic thực thi xử lý các Entity có Component tương ứng.

## 3. Luồng Dữ Liệu & Vòng Đời

1. **Khởi tạo**: `MainGame` thiết lập môi trường đồ họa (`Viewport`, `Stage`) và nạp tài nguyên cơ bản.
2. **Nạp dữ liệu**: `DataHelper` giải mã (parse) các file JSON từ `assets/data` để cấu hình quái vật, kỹ năng và vật phẩm.
3. **Luồng màn hình**: 
   - `MenuScreen` -> `WorldMapScreen` -> `BattleScreen`.
   - `ScreenManager` xử lý việc chuyển đổi và quản lý tài nguyên cần thiết cho từng màn hình.
4. **Logic chiến đấu**: 
   - `BattleScreen` khởi tạo `BattleSimulator`.
   - Kết quả từ simulator được gửi về thông qua `EventManager` để UI/Animation cập nhật.

## 4. Ghi Chú Kỹ Thuật & Nợ Kỹ Thuật (Technical Debt)

Hiện tại dự án đang trong quá trình hoàn thiện, một số điểm cần lưu ý:
- **Placeholder Screens**: Các màn hình như `CharacterScreen`, `QuestScreen` trong gói `service` hiện đang là khung mẫu (boilerplate) chờ triển khai logic chi tiết.
- **Resource Management**: Đang chuyển đổi dần sang việc sử dụng `GAssetManager` tập trung thay vì nạp thủ công tại các Screen.
- **Debug Tools**: `CheckRegionScreen` là công cụ hỗ trợ kiểm tra Atlas, có thể loại bỏ khi xuất bản (build release).
