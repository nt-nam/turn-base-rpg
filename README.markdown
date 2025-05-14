# LVpxW - Game RPG Turn-Based với LibGDX

## Tổng quan

**LVpxW** là một game nhập vai (RPG) turn-based 2D được phát triển bằng framework **LibGDX**, sử dụng mô hình kiến trúc **Hybrid MVC-ECS-Event-Driven**. Game lấy bối cảnh giả tưởng (fantasy), nơi người chơi vào vai một anh hùng trẻ tuổi với nhiệm vụ tìm kiếm cổ vật huyền thoại để ngăn chặn Dark Lord. Với cốt truyện phi tuyến tính, hệ thống chiến đấu turn-based, bản đồ khám phá, nhiệm vụ đa dạng, và các tính năng sáng tạo như chu kỳ ngày/đêm hay kỹ năng môi trường, game mang đến trải nghiệm nhập vai sâu sắc với thời gian chơi từ **5-10 giờ**.

Dự án được thiết kế để:
- **Hiệu suất cao**: Sử dụng **Ashley** (ECS) để quản lý hàng chục thực thể, **Box2D** cho va chạm.
- **Khả năng mở rộng**: Mô hình Hybrid MVC-ECS-Event-Driven cho phép dễ dàng thêm tính năng (multiplayer, crafting).
- **Khả năng làm việc nhóm**: Cấu trúc mã rõ ràng, dễ phân công (UI, logic, events).
- **Chạy đa nền tảng**: Desktop (Windows, macOS, Linux) và Android, với kích thước nhẹ (<100MB).

---

## Tính năng chính

### 1. Khám phá bản đồ
- **Bản đồ Tiled**: 3-5 khu vực (village, forest, dungeon) với các layer ground, walls, objects.
- **Di chuyển**: Người chơi điều khiển nhân vật bằng phím WASD, không đi qua tường (va chạm dùng Box2D).
- **Trigger**: Kích hoạt sự kiện như chiến đấu, đối thoại, hoặc nhiệm vụ khi vào khu vực cụ thể.
- **Camera**: Theo dõi nhân vật, hỗ trợ phóng to/thu nhỏ khi vào khu vực đặc biệt (như dungeon).

### 2. Hệ thống chiến đấu Turn-Based
- **Menu hành động**: Attack, Skill (Fireball, Heal), Item (Potion, Antidote), Run.
- **Cơ chế sát thương**:
  - Dựa trên chỉ số: `Attack - Defense`.
  - Yếu tố ngẫu nhiên: 10% cơ hội critical hit.
- **Quy tắc lượt**: Thực thể có Speed cao đi trước.
- **Hiệu ứng trạng thái**:
  - Poison: Mất 5 HP mỗi lượt.
  - Heal: Hồi 20 HP.
- **Đồng đội**: Tối đa 2 NPC tham gia chiến đấu, mỗi người có kỹ năng riêng (Tank, Healer).
- **Kết thúc chiến đấu**:
  - Thắng: Nhận EXP, vàng, vật phẩm.
  - Thua: Chuyển sang GameOverScreen.

### 3. Nhân vật và thực thể
- **Player**:
  - Chỉ số: HP, MP, Attack, Defense, Speed.
  - Inventory: Potion, vũ khí, vật phẩm nhiệm vụ.
  - Kỹ năng: Fireball, Heal.
- **Enemy**: 5-10 loại (Goblin, Dragon) với chỉ số và kỹ năng riêng.
- **NPC**: 10-15 nhân vật (trưởng làng, thương nhân, thợ săn) cung cấp đối thoại và nhiệm vụ.
- **Item**:
  - Potion: Hồi HP.
  - Antidote: Xóa trạng thái Poison.
  - Vũ khí: Tăng Attack.
  - Vật phẩm nhiệm vụ: Key.
- **Đồng đội**: 2-3 NPC có thể tuyển mộ, mỗi người có kỹ năng đặc biệt.

### 4. Hệ thống nhiệm vụ
- **Số lượng**: 10-15 nhiệm vụ.
- **Loại nhiệm vụ**:
  - Chính: Tìm cổ vật, đánh bại boss.
  - Phụ: Giết 5 Goblin, giao thư, khám phá dungeon.
- **Quest Log**: Hiển thị trong HUD, cập nhật trạng thái (đang thực hiện, hoàn thành).
- **Phần thưởng**: EXP, vàng, vật phẩm, hoặc mở khóa khu vực mới.

### 5. Cốt truyện phi tuyến tính
- **Lựa chọn đối thoại**: Ảnh hưởng đến cốt truyện (giúp vương quốc A hay B, từ chối nhiệm vụ chính).
- **Kết thúc**: 2-3 kết thúc khác nhau (anh hùng, phản bội, thợ săn).
- **Cây đối thoại**: Hệ thống branching dialogue tăng tính tương tác.

### 6. Lưu trữ
- **Lưu tự động**: Sau mỗi nhiệm vụ hoặc chiến đấu.
- **Lưu thủ công**: Qua menu Save.
- **Dữ liệu lưu**: Vị trí, chỉ số, inventory, quest log, tiến độ cốt truyện.
- **Slot save**: 2-3 slot, tải từ menu chính.

### 7. Giao diện người dùng (UI)
- **HUD**: Hiển thị HP, MP, vàng, cấp độ, quest log.
- **Menu chính**: New Game, Load Game, Settings (âm lượng Sixty, độ khó).
- **Battle Menu**: Hiển thị hành động và chỉ số thực thể.
- **Dialogue Box**: Nội dung đối thoại với 2-4 lựa chọn.
- **Inventory UI**: Danh sách vật phẩm và nút sử dụng.

### 8. Âm thanh và hình ảnh
- **Hình ảnh**:
  - Sprites: 16x16 hoặc 32x32 cho Player, Enemy, NPC.
  - Tileset: 16x16 cho bản đồ (village, forest, dungeon).
- **Animation**: Chuyển động cho Player (đi, tấn công), Enemy (tấn công, chết).
- **Âm thanh**:
  - Nhạc nền: Village, battle.
  - Hiệu ứng: Tấn công, nhặt vật phẩm, đối thoại.
- **Font**: Font pixel cho HUD và đối thoại.

### 9. Networking
- **Leaderboard**: Hiển thị top 10 người chơi dựa trên điểm số (tổng nhiệm vụ hoàn thành, kẻ thù tiêu diệt).
- **Gửi điểm số**: Qua HttpRequest khi hoàn thành nhiệm vụ hoặc chiến đấu.
- **Hiển thị**: Trong GameOverScreen hoặc menu chính.

### 10. Tính năng sáng tạo
- **Kỹ năng môi trường**: Ví dụ, Tidal Strike tăng sát thương nếu kẻ thù đứng trên ô nước.
- **Chu kỳ ngày/đêm**: NPC và sự kiện thay đổi theo thời gian (thương nhân xuất hiện ban ngày, quái vật mạnh hơn ban đêm).
- **Đồng đội**: Tuyển mộ NPC với kỹ năng riêng, quản lý qua PartyComponent.
- **Mini-game**: Câu cá hoặc chế tạo vật phẩm để nghỉ ngơi giữa các nhiệm vụ.
- **Tùy chỉnh nhân vật**: Chọn ngoại hình và phân bổ điểm chỉ số khi lên cấp.

---

## Trải nghiệm người chơi

### Giai đoạn mở đầu (0-30 phút)
- **Hoạt động**: Làm quen với di chuyển (WASD), đối thoại với NPC (trưởng làng), và chiến đấu đơn giản (giết 3 Goblin).
- **Trải nghiệm**: Nhạc nền nhẹ nhàng, hướng dẫn ngắn gọn giúp người chơi dễ tiếp cận.
- **Mục tiêu**: Hiểu cơ chế cơ bản và bắt đầu cốt truyện.

### Giai đoạn giữa (1-5 giờ)
- **Hoạt động**: Khám phá bản đồ đa dạng (village, forest, dungeon), nhận nhiệm vụ phức tạp (tìm key, đánh boss nhỏ).
- **Trải nghiệm**: Lựa chọn đối thoại ảnh hưởng đến cốt truyện, tạo cảm giác tự do. Chiến đấu tăng độ khó với kẻ thù có kỹ năng (Poison, Stun).
- **Mục tiêu**: Xây dựng nhân vật (lên cấp, thu thập vật phẩm) và tiến sâu vào cốt truyện.

### Giai đoạn kết thúc (5-10 giờ)
- **Hoạt động**: Đối đầu boss cuối qua nhiều giai đoạn, yêu cầu chiến thuật (quản lý MP, dùng đồng đội).
- **Trải nghiệm**: Kết thúc phụ thuộc vào lựa chọn trước đó, tăng giá trị chơi lại.
- **Mục tiêu**: Hoàn thành nhiệm vụ chính và khám phá các kết thúc khác nhau.

### Pacing
- **Khám phá**: 5-10 phút mỗi khu vực, xen kẽ với chiến đấu (1-2 phút/trận) và nghỉ ngơi (village với mini-game, đối thoại hài hước).
- **Phần thưởng**: EXP, vật phẩm phân bổ đều để duy trì động lực.
- **Sáng tạo**: Kỹ năng môi trường, chu kỳ ngày/đêm, và đồng đội làm gameplay phong phú.

### Giá trị chơi lại
- **Cốt truyện phi tuyến tính**: 2-3 kết thúc khác nhau.
- **Tùy chỉnh nhân vật**: Phân bổ chỉ số và lựa chọn đồng đội.
- **Leaderboard**: Cạnh tranh điểm số.
- **Nhiệm vụ ngẫu nhiên**: Mục tiêu phụ thay đổi (giết X quái vật, tìm Y vật phẩm).

---

## Kiến trúc và mô hình

Dự án sử dụng mô hình **Hybrid MVC-ECS-Event-Driven**, kết hợp:
- **MVC cải tiến**:
  - **Model**: Dữ liệu như PlayerData, EnemyData, QuestData.
  - **View**: Giao diện như GameScreen, Hud, DialogueBox.
  - **Controller**: Logic như InputManager, BattleManager.
- **ECS (Entity-Component-System)**:
  - **Entity**: Player, Enemy, NPC, Item.
  - **Component**: Position, Sprite, Stats, Skill, Quest, Party.
  - **System**: Rendering, Battle, AI, Quest, Animation.
  - Sử dụng **Ashley** để quản lý hiệu suất cao với hàng chục thực thể.
- **Event-Driven**:
  - **Events**: InteractionEvent, BattleEvent, QuestEvent, NetworkEvent, TimeEvent.
  - **EventManager**: Quản lý phát/nhận sự kiện bất đồng bộ.
  - Hỗ trợ tương tác (nhặt vật phẩm, đối thoại) và networking (leaderboard).

### Cấu trúc thư mục
```
LVpxW/
├── assets/                  # Texture, Tileset, Sound, Font
├── components/              # PositionComponent, SpriteComponent, StatsComponent, ...
├── systems/                 # RenderingSystem, BattleSystem, AISystem, ...
├── entities/                # EntityFactory, Player, Enemy, NPC, Item
├── screens/                 # MainMenuScreen, GameScreen, BattleScreen, GameOverScreen
├── ui/                      # Hud, BattleMenu, DialogueBox, InventoryUI
├── managers/                # GameStateManager, InputManager, ECSEngine, ...
├── handlers/                # InputHandler, InteractionHandler, BattleHandler, NetworkHandler
├── events/                  # InteractionEvent, BattleEvent, QuestEvent, NetworkEvent
├── data/                    # GameConfig, PlayerData, EnemyData, QuestData
├── utils/                   # Constants, MapLoader, Logger, Box2DDebugRenderer
```

---

## Công nghệ và nền tảng

### Công nghệ
- **LibGDX**: Framework chính cho rendering, input, assets.
- **Ashley**: Hệ thống ECS để quản lý thực thể.
- **Box2D**: Xử lý va chạm và physics.
- **Tiled Map Editor**: Thiết kế bản đồ (village, forest, dungeon).

### Nền tảng
- **Chạy chính**: Desktop (Windows, macOS, Linux), Android.
- **Mở rộng**: iOS (yêu cầu cấu hình bổ sung).
- **Kích thước**: Dưới 100MB, phù hợp thiết bị cấu hình trung bình.

---

## Dữ liệu chính

### Model
- **GameConfig**: Độ khó, âm lượng.
- **PlayerData**: Vị trí, chỉ số, inventory, quest log.
- **EnemyData**: Chỉ số, kỹ năng, AI.
- **NPCData**: Đối thoại, nhiệm vụ.
- **QuestData**: Mô tả, mục tiêu, phần thưởng.
- **StoryData**: Tiến độ cốt truyện, lựa chọn.
- **MapData**: Layer, trigger của bản đồ Tiled.
- **BattleState**: Lượt, thực thể tham gia.
- **SaveData**: Vị trí, chỉ số, quest log.

### View
- **ScreenData**: Trạng thái màn hình (MainMenu, Game, Battle, GameOver).
- **UIData**: Nội dung HUD, menu, đối thoại.
- **DialogueChoiceData**: Lựa chọn và kết quả.

### Controller
- **Managers**: GameStateManager, InputManager, EventManager, LevelManager, BattleManager, SaveManager, ECSEngine.
- **Systems**: Rendering, Battle, AI, Quest, Animation.
- **Handlers**: Input, Interaction, Battle, Network.

### Events
- **InteractionEvent**: Nhặt vật phẩm, đối thoại.
- **BattleEvent**: Bắt đầu, chọn hành động.
- **QuestEvent**: Nhận, hoàn thành nhiệm vụ.
- **NetworkEvent**: Gửi/nhận leaderboard.
- **TimeEvent**: Thay đổi ngày/đêm.

---

## Hiệu suất và khả năng mở rộng

### Hiệu suất
- **ECS (Ashley)**: Quản lý hiệu quả hàng chục thực thể (Player, Enemy, NPC, Item) trong dungeon đông đúc.
- **Box2D**: Xử lý va chạm nhanh chóng.
- **AssetManager**: Tải tài nguyên bất đồng bộ, giảm thời gian chờ.

### Khả năng mở rộng
- **Hybrid MVC-ECS-Event-Driven**: Dễ dàng thêm tính năng như multiplayer (với KryoNet) hoặc hệ thống crafting.
- **Cấu trúc mã**: Thư mục rõ ràng (components/, systems/, screens/) hỗ trợ mở rộng và bảo trì.
- **Nhiệm vụ ngẫu nhiên**: Tăng sự mới mẻ cho các lần chơi sau.

### Làm việc nhóm
- **Phân công**:
  - **UI**: screens/, ui/.
  - **Logic**: entities/, components/, systems/.
  - **Events**: events/, handlers/.
  - **Tools**: utils/, assets/.
- **Quản lý mã**: Sử dụng Git để đồng bộ và phân chia công việc.

---

## Kết luận

**LVpxW** là một game RPG turn-based hoàn chỉnh, mang đến trải nghiệm nhập vai phong phú với:
- **Cốt truyện phi tuyến tính**: Lựa chọn đối thoại dẫn đến các kết thúc khác nhau.
- **Gameplay đa dạng**: Khám phá bản đồ, chiến đấu turn-based, nhiệm vụ, và mini-game.
- **Tính năng sáng tạo**: Kỹ năng môi trường, chu kỳ ngày/đêm, đồng đội, tùy chỉnh nhân vật.
- **Giá trị chơi lại**: Nhiều kết thúc, nhiệm vụ ngẫu nhiên, và leaderboard cạnh tranh.

Dự án không chỉ là một sản phẩm game mà còn là một nền tảng học tập và phát triển, thể hiện khả năng kết hợp các mô hình kiến trúc (MVC, ECS, Event-Driven) trong phát triển game 2D. Với hiệu suất cao, khả năng mở rộng, và cấu trúc mã rõ ràng, **LVpxW** là một dự án lý tưởng cho cả người chơi và nhà phát triển muốn khám phá thế giới RPG.
