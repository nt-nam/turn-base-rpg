# Danh mục màn hình (Screen Catalog)

> Sinh tự động bởi `node tools/screen-catalog/catalog.mjs`. Không sửa tay — sửa `catalog.mjs` rồi chạy lại.

**Tổng: 2094 màn hình** — 1321 màn hình thiết kế riêng, 773 màn hình quản trị thực thể (ma trận thực thể × view).

## Theo bề mặt sản phẩm

| Bề mặt | Unique | Matrix | Tổng |
|---|---:|---:|---:|
| Game client (Desktop/Android/iOS/Web) | 600 | 0 | 600 |
| Web Console (admin/liveops/support/BI) | 165 | 275 | 440 |
| Creator Studio (web) | 91 | 498 | 589 |
| Dev Tools (web) | 92 | 0 | 92 |
| QA Hub (web) | 82 | 0 | 82 |
| Pilot Portal (web) | 49 | 0 | 49 |
| Public Site + Account Portal (web) | 68 | 0 | 68 |
| Companion App (PWA mobile) | 40 | 0 | 40 |
| Stream Suite (streamer dashboard + viewer extension) | 26 | 0 | 26 |
| Esports Observer (caster/observer) | 20 | 0 | 20 |
| Workshop (mod + creator marketplace) | 28 | 0 | 28 |
| Community Translation Portal | 18 | 0 | 18 |
| Partner Portal (influencer/affiliate/creator program) | 18 | 0 | 18 |
| Live Game Master Console | 24 | 0 | 24 |

## Theo mùa phát hành

| Mùa | Unique | Matrix | Tổng | Lũy kế |
|---|---:|---:|---:|---:|
| Launch v1.0 | 644 | 385 | 1029 | 1029 |
| Mùa 1 — Lễ Hội Đèn Lồng | 196 | 258 | 454 | 1483 |
| Mùa 2 — Biên Niên Sử | 177 | 130 | 307 | 1790 |
| Mùa 3 — Vết Nứt Thời Gian | 163 | 0 | 163 | 1953 |
| Mùa 4 — Bên Kia Bình Minh | 141 | 0 | 141 | 2094 |

## Theo vai trò (một màn hình có thể phục vụ nhiều vai trò)

| Vai trò | Số màn hình |
|---|---:|
| Gamer — người chơi tập trung gameplay | 733 |
| User — chủ tài khoản (thanh toán, bảo mật, hỗ trợ) | 214 |
| Pilot — người chơi thử nghiệm bản beta/pilot | 377 |
| Creator — thiết kế nội dung, level, lore, art, dịch thuật (kể cả player-creator UGC) | 708 |
| QA — kiểm thử, xác nhận release | 121 |
| Dev — kỹ sư client/server/tooling | 169 |
| Admin — vận hành, LiveOps, hỗ trợ, kiểm duyệt, quản trị hệ thống | 502 |


### Game client (Desktop/Android/iOS/Web) › boot

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.boot.splash` | Splash | Hiển thị thương hiệu, khởi tạo runtime | gamer, user, pilot | launch |
| `game.boot.legal_notice` | Thông báo pháp lý | Hiển thị bản quyền/cảnh báo sức khỏe | gamer, user, pilot | launch |
| `game.boot.age_gate` | Xác nhận độ tuổi | Chặn/giới hạn tính năng theo tuổi | gamer, user, pilot | launch |
| `game.boot.tos_accept` | Chấp nhận điều khoản | Ghi nhận đồng ý ToS theo phiên bản | gamer, user, pilot | launch |
| `game.boot.privacy_consent` | Đồng ý quyền riêng tư | Chọn đồng ý analytics/crash/marketing | gamer, user, pilot | launch |
| `game.boot.language_pick` | Chọn ngôn ngữ lần đầu | Đặt locale trước khi tải nội dung | gamer, user, pilot | launch |
| `game.boot.content_download` | Tải gói nội dung | Tải/giải nén content pack theo phiên bản | gamer, user, pilot | launch |
| `game.boot.patch_notes` | Ghi chú cập nhật | Giới thiệu thay đổi bản mới | gamer, user, pilot | launch |
| `game.boot.maintenance` | Bảo trì | Thông báo thời gian bảo trì server | gamer, user, pilot | launch |
| `game.boot.force_update` | Bắt buộc cập nhật | Chặn client cũ không tương thích protocol | gamer, user, pilot | launch |
| `game.boot.server_select` | Chọn máy chủ | Chọn region/cụm server | gamer, user, pilot | launch |
| `game.boot.login_hub` | Cổng đăng nhập | Chọn khách/email/Google/Apple | gamer, user, pilot | launch |
| `game.boot.login_email` | Đăng nhập email | Xác thực bằng email + mật khẩu | gamer, user, pilot | launch |
| `game.boot.register_email` | Đăng ký email | Tạo tài khoản mới | gamer, user, pilot | launch |
| `game.boot.verify_email` | Xác minh email | Nhập mã OTP xác minh | gamer, user, pilot | launch |
| `game.boot.forgot_password` | Quên mật khẩu | Gửi yêu cầu đặt lại mật khẩu | gamer, user, pilot | launch |
| `game.boot.reset_password` | Đặt lại mật khẩu | Nhập mật khẩu mới | gamer, user, pilot | launch |
| `game.boot.guest_warning` | Cảnh báo tài khoản khách | Nhắc liên kết để tránh mất dữ liệu | gamer, user, pilot | launch |
| `game.boot.account_link` | Liên kết tài khoản | Nâng cấp khách thành tài khoản đầy đủ | gamer, user, pilot | launch |
| `game.boot.account_switch` | Đổi tài khoản | Đăng xuất và chuyển tài khoản | gamer, user, pilot | launch |
| `game.boot.slot_list` | Danh sách nhân vật | Chọn slot lưu (thay SelectPlayerScreen) | gamer, user, pilot | launch |
| `game.boot.slot_delete_confirm` | Xác nhận xóa slot | Xóa slot có xác nhận 2 bước | gamer, user, pilot | launch |
| `game.boot.cloud_restore` | Khôi phục từ cloud | Kéo bản lưu cloud về thiết bị | gamer, user, pilot | launch |
| `game.boot.save_conflict` | Xung đột bản lưu | So sánh local vs cloud và chọn bản giữ | gamer, user, pilot | launch |
| `game.boot.ban_notice` | Thông báo khóa tài khoản | Hiển thị lý do và cách kháng nghị | gamer, user, pilot | launch |
| `game.boot.reconnect` | Kết nối lại | Xử lý mất mạng giữa phiên | gamer, user, pilot | launch |
| `game.boot.offline_mode` | Chế độ ngoại tuyến | Thông báo tính năng bị giới hạn khi offline | gamer, user, pilot | launch |
| `game.boot.main_menu` | Menu chính | Vào game, cài đặt, tin tức, thoát | gamer, user, pilot | launch |

### Game client (Desktop/Android/iOS/Web) › onboarding

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.onboarding.hero_create_class` | Tạo nhân vật — chọn lớp | Chọn 1 trong 6 lớp khởi đầu | gamer, pilot | launch |
| `game.onboarding.hero_create_look` | Tạo nhân vật — ngoại hình | Chọn skin/màu khởi đầu | gamer, pilot | launch |
| `game.onboarding.hero_create_name` | Tạo nhân vật — đặt tên | Nhập tên, kiểm tra hợp lệ/trùng | gamer, pilot | launch |
| `game.onboarding.hero_create_confirm` | Tạo nhân vật — xác nhận | Tóm tắt lựa chọn trước khi tạo | gamer, pilot | launch |
| `game.onboarding.prologue` | Mở đầu cốt truyện | Cutscene giới thiệu Dark Lord và cổ vật | gamer, pilot | launch |
| `game.onboarding.tutorial_move` | Hướng dẫn di chuyển | Joystick/bàn phím trên bản đồ | gamer, pilot | launch |
| `game.onboarding.tutorial_interact` | Hướng dẫn tương tác | Nói chuyện NPC, mở rương | gamer, pilot | launch |
| `game.onboarding.tutorial_battle` | Hướng dẫn chiến đấu | Trận đầu có chỉ dẫn từng bước | gamer, pilot | launch |
| `game.onboarding.tutorial_skill` | Hướng dẫn kỹ năng | Năng lượng, kỹ năng 2/3, tuyệt kỹ | gamer, pilot | launch |
| `game.onboarding.tutorial_lineup` | Hướng dẫn đội hình | Lưới 3x3, hàng trước/giữa/sau | gamer, pilot | launch |
| `game.onboarding.tutorial_equip` | Hướng dẫn trang bị | Mặc trang bị, so sánh chỉ số | gamer, pilot | launch |
| `game.onboarding.tutorial_shop` | Hướng dẫn cửa hàng | Mua vật phẩm đầu tiên | gamer, pilot | launch |
| `game.onboarding.tutorial_reward` | Hoàn thành hướng dẫn | Nhận quà hoàn thành tutorial | gamer, pilot | launch |

### Game client (Desktop/Android/iOS/Web) › world

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.world.world_explore` | Khám phá bản đồ | Di chuyển, va chạm, trigger (thay WorldMapScreen) | gamer, pilot | launch |
| `game.world.region_map` | Bản đồ vùng | Xem toàn vùng, điểm đã mở khóa | gamer, pilot | launch |
| `game.world.fast_travel` | Dịch chuyển nhanh | Chọn điểm dịch chuyển đã mở | gamer, pilot | launch |
| `game.world.map_transition` | Chuyển bản đồ | Hiệu ứng + tải bản đồ kế tiếp | gamer, pilot | launch |
| `game.world.npc_dialogue` | Hội thoại NPC | Hiển thị lời thoại có chân dung | gamer, pilot | launch |
| `game.world.dialogue_choice` | Lựa chọn hội thoại | Chọn nhánh ảnh hưởng cốt truyện | gamer, pilot | launch |
| `game.world.inn_rest` | Nhà trọ | Hồi phục, lưu game, qua ngày | gamer, pilot | launch |
| `game.world.chest_open` | Mở rương | Hiệu ứng mở và nhận vật phẩm | gamer, pilot | launch |
| `game.world.encounter_preview` | Xem trước trận | Đội hình địch 3x3, lực chiến (thay BattleDetailPP) | gamer, pilot | launch |
| `game.world.encounter_locked` | Trận bị khóa | Hiển thị điều kiện mở khóa | gamer, pilot | launch |
| `game.world.minimap` | Bản đồ nhỏ | Overlay vị trí, NPC, cổng | gamer, pilot | launch |
| `game.world.quest_tracker` | Theo dõi nhiệm vụ | Overlay mục tiêu hiện tại | gamer, pilot | launch |
| `game.world.day_night_info` | Thông tin ngày/đêm | Thời gian trong game và hiệu ứng | gamer, pilot | launch |
| `game.world.weather_info` | Thời tiết | Hiệu ứng môi trường lên trận đấu | gamer, pilot | launch |
| `game.world.photo_mode` | Chế độ chụp ảnh | Ẩn HUD, camera tự do, lưu ảnh | gamer, pilot | launch |
| `game.world.emote_wheel` | Vòng biểu cảm | Chọn emote nhân vật | gamer, pilot | launch |
| `game.world.interaction_prompt` | Gợi ý tương tác | Nút ngữ cảnh khi đứng gần đối tượng | gamer, pilot | launch |
| `game.world.puzzle_switch` | Câu đố công tắc | Giải đố bật/tắt trên bản đồ | gamer, pilot | launch |
| `game.world.puzzle_sequence` | Câu đố trình tự | Giải đố ghi nhớ thứ tự | gamer, pilot | launch |
| `game.world.signpost_read` | Đọc biển báo | Hiển thị chữ trên biển báo | gamer, pilot | launch |
| `game.world.lore_book` | Đọc sách truyền thuyết | Mở khóa mục codex | gamer, pilot | launch |
| `game.world.save_point` | Điểm lưu | Lưu thủ công | gamer, pilot | launch |
| `game.world.pause_menu` | Tạm dừng | Menu tạm dừng khi khám phá | gamer, pilot | launch |
| `game.world.hud_customize` | Tùy chỉnh HUD | Kéo thả vị trí nút, joystick | gamer, pilot | launch |

### Game client (Desktop/Android/iOS/Web) › village

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.village.village_overview` | Tổng quan làng | Mức phục hồi làng, tài nguyên | gamer | launch |
| `game.village.building_list` | Danh sách công trình | Công trình đã/chưa xây | gamer | launch |
| `game.village.building_detail` | Chi tiết công trình | Chức năng, cấp, sản lượng | gamer | launch |
| `game.village.building_upgrade` | Nâng cấp công trình | Chi phí, thời gian, xác nhận | gamer | launch |
| `game.village.building_construct` | Xây công trình mới | Chọn vị trí đặt công trình | gamer | launch |
| `game.village.resource_production` | Sản xuất tài nguyên | Thu hoạch tài nguyên theo giờ | gamer | launch |
| `game.village.worker_assign` | Phân công dân làng | Gán NPC vào công trình | gamer | launch |
| `game.village.decoration_mode` | Chế độ trang trí | Đặt vật trang trí | gamer | launch |
| `game.village.village_visitors` | Khách đến làng | NPC ghé thăm mang nhiệm vụ | gamer | launch |
| `game.village.village_quests` | Nhiệm vụ làng | Nhiệm vụ phục hồi làng | gamer | launch |

### Game client (Desktop/Android/iOS/Web) › gathering

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.gathering.fishing` | Câu cá | Minigame câu cá | gamer | launch |
| `game.gathering.mining` | Khai khoáng | Minigame đào quặng | gamer | launch |
| `game.gathering.herb_gathering` | Hái thảo dược | Minigame thu thập | gamer | launch |
| `game.gathering.gather_result` | Kết quả thu thập | Vật phẩm nhận được | gamer | launch |
| `game.gathering.cooking` | Nấu ăn | Chế món ăn buff | gamer | launch |
| `game.gathering.cookbook` | Sách công thức | Công thức đã mở khóa | gamer | launch |

### Game client (Desktop/Android/iOS/Web) › battle

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.battle.battle_intro` | Mở màn trận | Giới thiệu đội địch, boss | gamer, pilot | launch |
| `game.battle.lineup_confirm` | Xác nhận đội hình | Chỉnh nhanh đội hình trước trận | gamer, pilot | launch |
| `game.battle.battle_main` | Trận đấu | Lưới 3x3 hai phe, HUD, thanh lượt | gamer, pilot | launch |
| `game.battle.skill_select` | Chọn kỹ năng | Chọn kỹ năng cho đơn vị đang tới lượt | gamer, pilot | launch |
| `game.battle.target_select` | Chọn mục tiêu | Chọn mục tiêu hợp lệ theo kỹ năng | gamer, pilot | launch |
| `game.battle.item_select` | Dùng vật phẩm | Dùng vật phẩm trong trận | gamer, pilot | launch |
| `game.battle.auto_battle` | Tự động chiến đấu | Bật/tắt AI điều khiển phe ta | gamer, pilot | launch |
| `game.battle.battle_speed` | Tốc độ trận | x1/x2/x4 | gamer, pilot | launch |
| `game.battle.battle_pause` | Tạm dừng trận | Tiếp tục/thoát/cài đặt | gamer, pilot | launch |
| `game.battle.battle_log` | Nhật ký trận | Danh sách sự kiện từng lượt | gamer, pilot | launch |
| `game.battle.unit_inspect` | Xem đơn vị | Chỉ số, hiệu ứng đang có | gamer, pilot | launch |
| `game.battle.turn_timeline` | Dòng thời gian lượt | Thứ tự hành động sắp tới | gamer, pilot | launch |
| `game.battle.weakness_hint` | Gợi ý khắc chế | Chỉ ra lớp khắc chế/bị khắc | gamer, pilot | launch |
| `game.battle.ultimate_cutin` | Cut-in tuyệt kỹ | Hoạt cảnh tuyệt kỹ | gamer, pilot | launch |
| `game.battle.boss_phase` | Chuyển pha boss | Thông báo pha mới và cơ chế | gamer, pilot | launch |
| `game.battle.revive_prompt` | Hồi sinh | Dùng vật phẩm hồi sinh khi thua | gamer, pilot | launch |
| `game.battle.battle_victory` | Chiến thắng | Kết quả thắng | gamer, pilot | launch |
| `game.battle.battle_defeat` | Thất bại | Kết quả thua + gợi ý cải thiện | gamer, pilot | launch |
| `game.battle.battle_draw` | Hòa | Kết quả khi hết số lượt tối đa | gamer, pilot | launch |
| `game.battle.battle_rewards` | Phần thưởng trận | EXP, vàng, rơi đồ | gamer, pilot | launch |
| `game.battle.level_up` | Lên cấp | Chỉ số tăng khi lên cấp | gamer, pilot | launch |
| `game.battle.flee_confirm` | Xác nhận bỏ chạy | Rời trận có hậu quả | gamer, pilot | launch |
| `game.battle.retry_confirm` | Đánh lại | Đánh lại với cùng đội | gamer, pilot | launch |
| `game.battle.damage_breakdown` | Phân tích sát thương | Sát thương/hồi máu mỗi đơn vị | gamer, pilot | launch |
| `game.battle.battle_quest_progress` | Tiến độ nhiệm vụ trong trận | Mục tiêu phụ của trận | gamer, pilot | launch |
| `game.battle.replay_list` | Danh sách replay | Các trận đã lưu | gamer, pilot | launch |
| `game.battle.replay_viewer` | Xem replay | Phát lại từ seed + input | gamer, pilot | launch |
| `game.battle.replay_share` | Chia sẻ replay | Tạo mã/đường dẫn chia sẻ | gamer, pilot | launch |

### Game client (Desktop/Android/iOS/Web) › heroes

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.heroes.hero_roster` | Danh sách anh hùng | Lọc, sắp xếp, khóa (thay HerosPP) | gamer | launch |
| `game.heroes.hero_overview` | Anh hùng — tổng quan | Chân dung, sao, cấp, lực chiến | gamer | launch |
| `game.heroes.hero_stats` | Anh hùng — chỉ số | Chỉ số gốc + trang bị + buff | gamer | launch |
| `game.heroes.hero_skills` | Anh hùng — kỹ năng | 3 kỹ năng + nội tại | gamer | launch |
| `game.heroes.hero_equipment` | Anh hùng — trang bị | 4 ô trang bị, set bonus | gamer | launch |
| `game.heroes.hero_lore` | Anh hùng — tiểu sử | Cốt truyện, lồng tiếng | gamer | launch |
| `game.heroes.hero_level_up` | Nâng cấp anh hùng | Dùng EXP/vật phẩm | gamer | launch |
| `game.heroes.hero_star_up` | Tăng sao | Ghép anh hùng cùng loại (thay PotentialPP) | gamer | launch |
| `game.heroes.hero_merge_confirm` | Xác nhận ghép | Cảnh báo anh hùng đang trong đội hình | gamer | launch |
| `game.heroes.hero_skill_upgrade` | Nâng kỹ năng | Dùng sách kỹ năng | gamer | launch |
| `game.heroes.hero_awaken` | Thức tỉnh | Mở khóa tuyệt kỹ mới ở 5 sao | gamer | launch |
| `game.heroes.hero_compare` | So sánh anh hùng | So sánh 2 anh hùng cạnh nhau | gamer | launch |
| `game.heroes.hero_dismiss` | Giải tán anh hùng | Đổi anh hùng lấy tài nguyên | gamer | launch |
| `game.heroes.lineup_editor` | Sửa đội hình | Kéo thả trên lưới 3x3 | gamer | launch |
| `game.heroes.lineup_presets` | Đội hình lưu sẵn | Lưu/đổi preset theo chế độ | gamer | launch |
| `game.heroes.lineup_analysis` | Phân tích đội hình | Lực chiến, cân bằng vai trò | gamer | launch |
| `game.heroes.synergy_view` | Cộng hưởng | Bonus khi đủ lớp/hệ | gamer | launch |
| `game.heroes.class_counter_chart` | Bảng khắc chế lớp | Vòng khắc chế 6 lớp | gamer | launch |

### Game client (Desktop/Android/iOS/Web) › companions

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.companions.pet_list` | Danh sách thú cưng | Thú cưng sở hữu | gamer | launch |
| `game.companions.pet_detail` | Chi tiết thú cưng | Chỉ số hỗ trợ | gamer | launch |
| `game.companions.pet_feed` | Cho ăn | Tăng thân thiết | gamer | launch |
| `game.companions.pet_evolve` | Tiến hóa thú cưng | Đổi hình dạng, tăng chỉ số | gamer | launch |
| `game.companions.pet_skill` | Kỹ năng thú cưng | Kỹ năng hỗ trợ trận | gamer | launch |
| `game.companions.pet_expedition` | Thám hiểm thú cưng | Gửi đi nhận tài nguyên | gamer | launch |

### Game client (Desktop/Android/iOS/Web) › inventory

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.inventory.bag_equipment` | Túi — trang bị | Lưới trang bị (thay BagPP) | gamer | launch |
| `game.inventory.bag_materials` | Túi — nguyên liệu | Nguyên liệu chế tạo | gamer | launch |
| `game.inventory.bag_consumables` | Túi — tiêu hao | Thuốc, sách EXP | gamer | launch |
| `game.inventory.bag_key_items` | Túi — vật phẩm nhiệm vụ | Chìa khóa, cổ vật | gamer | launch |
| `game.inventory.item_detail` | Chi tiết vật phẩm | Mô tả, nguồn nhận | gamer | launch |
| `game.inventory.item_use_target` | Chọn mục tiêu dùng | Chọn anh hùng nhận hiệu ứng | gamer | launch |
| `game.inventory.item_use_result` | Kết quả sử dụng | Hiệu ứng đã áp dụng | gamer | launch |
| `game.inventory.equipment_detail` | Chi tiết trang bị | Chỉ số, cấp, set | gamer | launch |
| `game.inventory.equipment_compare` | So sánh trang bị | So với đồ đang mặc | gamer | launch |
| `game.inventory.equipment_assign` | Mặc trang bị | Chọn anh hùng mặc | gamer | launch |
| `game.inventory.equipment_upgrade` | Cường hóa | Tăng cấp trang bị | gamer | launch |
| `game.inventory.equipment_refine` | Tinh luyện | Đổi dòng chỉ số phụ | gamer | launch |
| `game.inventory.equipment_salvage` | Phân rã | Đổi trang bị lấy nguyên liệu | gamer | launch |
| `game.inventory.set_bonus` | Bộ trang bị | Hiệu ứng bộ | gamer | launch |
| `game.inventory.bag_expand` | Mở rộng túi | Tăng sức chứa | gamer | launch |
| `game.inventory.bag_filter` | Lọc túi | Lọc/sắp xếp | gamer | launch |
| `game.inventory.sell_confirm` | Xác nhận bán | Bán 1 vật phẩm | gamer | launch |
| `game.inventory.bulk_sell` | Bán hàng loạt | Chọn nhiều để bán | gamer | launch |

### Game client (Desktop/Android/iOS/Web) › smithy

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.smithy.smithy_home` | Lò rèn | Trung tâm chế tạo (thay SmithyPP) | gamer | launch |
| `game.smithy.recipe_list` | Danh sách công thức | Công thức chế tạo | gamer | launch |
| `game.smithy.recipe_detail` | Chi tiết công thức | Nguyên liệu cần | gamer | launch |
| `game.smithy.craft_confirm` | Xác nhận chế tạo | Số lượng, chi phí | gamer | launch |
| `game.smithy.craft_result` | Kết quả chế tạo | Vật phẩm nhận | gamer | launch |
| `game.smithy.enchant` | Phù phép | Thêm hiệu ứng trang bị | gamer | launch |
| `game.smithy.socket_gem` | Khảm ngọc | Gắn ngọc vào ô | gamer | launch |
| `game.smithy.gem_combine` | Ghép ngọc | Ghép ngọc cấp cao | gamer | launch |

### Game client (Desktop/Android/iOS/Web) › progression

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.progression.quest_main` | Nhật ký — chính tuyến | Nhiệm vụ cốt truyện | gamer | launch |
| `game.progression.quest_side` | Nhật ký — phụ tuyến | Nhiệm vụ phụ | gamer | launch |
| `game.progression.quest_daily` | Nhật ký — hằng ngày | Nhiệm vụ reset mỗi ngày | gamer | launch |
| `game.progression.quest_weekly` | Nhật ký — hằng tuần | Nhiệm vụ reset mỗi tuần | gamer | launch |
| `game.progression.quest_detail` | Chi tiết nhiệm vụ | Mục tiêu, phần thưởng, dẫn đường | gamer | launch |
| `game.progression.quest_accept` | Nhận nhiệm vụ | Xác nhận nhận nhiệm vụ | gamer | launch |
| `game.progression.quest_complete` | Hoàn thành nhiệm vụ | Thông báo hoàn thành | gamer | launch |
| `game.progression.quest_claim` | Nhận thưởng nhiệm vụ | Nhận phần thưởng | gamer | launch |
| `game.progression.chapter_select` | Chọn chương | Chương cốt truyện đã mở | gamer | launch |
| `game.progression.chapter_intro` | Giới thiệu chương | Mở đầu chương mới | gamer | launch |
| `game.progression.story_recap` | Tóm tắt cốt truyện | Nhắc lại sự kiện đã qua | gamer | launch |
| `game.progression.cutscene_player` | Phát cutscene | Phát cutscene theo kịch bản | gamer | launch |
| `game.progression.cutscene_skip` | Bỏ qua cutscene | Xác nhận bỏ qua | gamer | launch |
| `game.progression.ending_select` | Chọn kết thúc | Kết thúc theo lựa chọn | gamer | launch |
| `game.progression.ending_credits` | Danh đề | Credits cuối game | gamer | launch |
| `game.progression.new_game_plus` | Chơi lại nâng cao | Bắt đầu NG+ giữ tiến trình | gamer | launch |
| `game.progression.achievement_list` | Thành tựu | Danh sách thành tựu | gamer | launch |
| `game.progression.achievement_detail` | Chi tiết thành tựu | Điều kiện, tiến độ | gamer | launch |
| `game.progression.title_collection` | Danh hiệu | Danh hiệu mở khóa | gamer | launch |
| `game.progression.codex_heroes` | Bách khoa — anh hùng | Anh hùng đã gặp | gamer | launch |
| `game.progression.codex_enemies` | Bách khoa — quái vật | Quái đã đánh bại | gamer | launch |
| `game.progression.codex_items` | Bách khoa — vật phẩm | Vật phẩm đã nhận | gamer | launch |
| `game.progression.codex_maps` | Bách khoa — vùng đất | Bản đồ đã khám phá | gamer | launch |
| `game.progression.codex_lore` | Bách khoa — truyền thuyết | Mục lore đã mở | gamer | launch |
| `game.progression.cg_album` | Album hình minh họa | CG đã mở khóa | gamer | launch |
| `game.progression.jukebox` | Máy nghe nhạc | Nhạc nền đã mở khóa | gamer | launch |
| `game.progression.tips_library` | Thư viện mẹo | Mẹo chơi | gamer | launch |
| `game.progression.glossary` | Thuật ngữ | Giải thích thuật ngữ game | gamer | launch |

### Game client (Desktop/Android/iOS/Web) › economy

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.economy.shop_home` | Cửa hàng | Trung tâm mua bán (thay ShopPP) | gamer, user | launch |
| `game.economy.shop_items` | Cửa hàng — vật phẩm | Vật phẩm bán bằng vàng | gamer, user | launch |
| `game.economy.shop_equipment` | Cửa hàng — trang bị | Trang bị bán bằng ngọc | gamer, user | launch |
| `game.economy.shop_rotating` | Cửa hàng xoay vòng | Hàng đổi mỗi 6 giờ | gamer, user | launch |
| `game.economy.offer_detail` | Chi tiết gói | Nội dung gói ưu đãi | gamer, user | launch |
| `game.economy.purchase_confirm` | Xác nhận mua | Giá, số lượng | gamer, user | launch |
| `game.economy.purchase_result` | Kết quả mua | Vật phẩm nhận | gamer, user | launch |
| `game.economy.iap_store` | Nạp ngọc | Gói nạp tiền thật | gamer, user | launch |
| `game.economy.iap_receipt` | Hóa đơn | Chi tiết giao dịch | gamer, user | launch |
| `game.economy.iap_restore` | Khôi phục giao dịch | Khôi phục mua trên store | gamer, user | launch |
| `game.economy.recruit_home` | Chiêu mộ | Trung tâm chiêu mộ (thay RecruitPP) | gamer, user | launch |
| `game.economy.banner_detail` | Chi tiết banner | Anh hùng nổi bật, thời hạn | gamer, user | launch |
| `game.economy.recruit_rates` | Tỉ lệ chiêu mộ | Công bố tỉ lệ theo luật | gamer, user | launch |
| `game.economy.recruit_animation` | Hoạt cảnh chiêu mộ | Hiệu ứng mở | gamer, user | launch |
| `game.economy.recruit_result_single` | Kết quả chiêu mộ x1 | Anh hùng nhận được | gamer, user | launch |
| `game.economy.recruit_result_multi` | Kết quả chiêu mộ x10 | 10 anh hùng nhận được | gamer, user | launch |
| `game.economy.recruit_history` | Lịch sử chiêu mộ | Các lần chiêu mộ | gamer, user | launch |
| `game.economy.pity_tracker` | Bảo hiểm chiêu mộ | Số lượt tới đảm bảo | gamer, user | launch |
| `game.economy.daily_checkin` | Điểm danh | Lịch 30 ngày (thay DailyPP) | gamer, user | launch |
| `game.economy.checkin_claim` | Nhận quà điểm danh | Nhận và cộng thưởng thật | gamer, user | launch |
| `game.economy.monthly_pass` | Thẻ tháng | Quà mỗi ngày trong 30 ngày | gamer, user | launch |
| `game.economy.battle_pass` | Battle Pass | Cấp pass, nhiệm vụ pass | gamer, user | launch |
| `game.economy.battle_pass_rewards` | Quà Battle Pass | Bảng quà miễn phí/trả phí | gamer, user | launch |
| `game.economy.energy_refill` | Nạp năng lượng | Mua/đợi hồi năng lượng | gamer, user | launch |
| `game.economy.currency_exchange` | Đổi tiền tệ | Đổi ngọc lấy vàng | gamer, user | launch |
| `game.economy.idle_rewards` | Thưởng treo máy | Thưởng tích lũy khi offline | gamer, user | launch |

### Game client (Desktop/Android/iOS/Web) › social

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.social.profile_self` | Hồ sơ của tôi | Thông tin, thành tích (thay RolePP) | gamer | launch |
| `game.social.profile_other` | Hồ sơ người khác | Xem người chơi khác | gamer | launch |
| `game.social.profile_edit` | Sửa hồ sơ | Đổi tên, lời giới thiệu | gamer | launch |
| `game.social.avatar_select` | Chọn avatar | Avatar đã mở khóa | gamer | launch |
| `game.social.frame_select` | Chọn khung | Khung avatar | gamer | launch |
| `game.social.friend_list` | Bạn bè | Danh sách bạn | gamer | launch |
| `game.social.friend_requests` | Lời mời kết bạn | Chấp nhận/từ chối | gamer | launch |
| `game.social.friend_search` | Tìm bạn | Tìm theo tên/ID | gamer | launch |
| `game.social.support_hero` | Anh hùng hỗ trợ | Mượn anh hùng của bạn | gamer | launch |
| `game.social.guild_home` | Bang hội | Trang chủ bang | gamer | launch |
| `game.social.guild_search` | Tìm bang | Tìm và xin vào | gamer | launch |
| `game.social.guild_create` | Lập bang | Tạo bang mới | gamer | launch |
| `game.social.guild_members` | Thành viên bang | Vai trò, đóng góp | gamer | launch |
| `game.social.guild_chat` | Chat bang | Chat nội bộ | gamer | launch |
| `game.social.guild_raid` | Raid bang | Boss chung của bang | gamer | launch |
| `game.social.guild_shop` | Cửa hàng bang | Đổi điểm đóng góp | gamer | launch |
| `game.social.guild_settings` | Cài đặt bang | Quyền, điều kiện vào | gamer | launch |
| `game.social.chat_world` | Chat thế giới | Kênh chat chung | gamer | launch |
| `game.social.chat_private` | Chat riêng | Tin nhắn 1-1 | gamer | launch |
| `game.social.mail_inbox` | Hộp thư | Thư hệ thống, quà | gamer | launch |
| `game.social.mail_detail` | Chi tiết thư | Nội dung, đính kèm | gamer | launch |
| `game.social.mail_claim_all` | Nhận tất cả | Nhận quà mọi thư | gamer | launch |
| `game.social.report_player` | Tố cáo người chơi | Gửi báo cáo vi phạm | gamer | launch |
| `game.social.block_list` | Danh sách chặn | Quản lý người bị chặn | gamer | launch |
| `game.social.notification_center` | Trung tâm thông báo | Thông báo trong game | gamer | launch |
| `game.social.notification_detail` | Chi tiết thông báo | Nội dung thông báo | gamer | launch |
| `game.social.leaderboard_power` | Xếp hạng lực chiến | Top lực chiến | gamer | launch |
| `game.social.leaderboard_arena` | Xếp hạng đấu trường | Top đấu trường | gamer | launch |
| `game.social.leaderboard_story` | Xếp hạng cốt truyện | Top tiến độ | gamer | launch |

### Game client (Desktop/Android/iOS/Web) › modes

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.modes.arena_home` | Đấu trường | PvP bất đồng bộ | gamer | launch |
| `game.modes.arena_matchmaking` | Tìm đối thủ | Chọn đối thủ | gamer | launch |
| `game.modes.arena_defense` | Đội phòng thủ | Đội hình phòng thủ | gamer | launch |
| `game.modes.arena_history` | Lịch sử đấu trường | Trận đã đấu | gamer | launch |
| `game.modes.arena_rewards` | Thưởng đấu trường | Thưởng theo mùa | gamer | launch |
| `game.modes.tower_home` | Tháp thử thách | Leo tầng | gamer | launch |
| `game.modes.tower_floor` | Chọn tầng | Chi tiết tầng | gamer | launch |
| `game.modes.dungeon_list` | Hầm ngục | Danh sách hầm ngục nguyên liệu | gamer | launch |
| `game.modes.dungeon_detail` | Chi tiết hầm ngục | Độ khó, rơi đồ | gamer | launch |
| `game.modes.world_boss` | Boss thế giới | Boss chung toàn server | gamer | launch |
| `game.modes.event_hub` | Sự kiện | Danh sách sự kiện đang chạy | gamer | launch |
| `game.modes.event_detail` | Chi tiết sự kiện | Luật, thời hạn | gamer | launch |
| `game.modes.event_shop` | Cửa hàng sự kiện | Đổi token sự kiện | gamer | launch |
| `game.modes.event_ranking` | Xếp hạng sự kiện | Top sự kiện | gamer | launch |
| `game.modes.limited_tasks` | Nhiệm vụ giới hạn | Nhiệm vụ có thời hạn (thay LimitedTaskPP) | gamer | launch |
| `game.modes.expedition_send` | Gửi thám hiểm | Gửi đội đi thám hiểm | gamer | launch |
| `game.modes.expedition_result` | Kết quả thám hiểm | Thưởng thám hiểm | gamer | launch |
| `game.modes.coop_lobby` | Phòng co-op | Tạo/vào phòng đánh chung | gamer | launch |
| `game.modes.coop_invite` | Mời co-op | Mời bạn | gamer | launch |
| `game.modes.coop_ready` | Sẵn sàng co-op | Chờ mọi người sẵn sàng | gamer | launch |
| `game.modes.coop_results` | Kết quả co-op | Thưởng chia theo đóng góp | gamer | launch |
| `game.modes.spectate_list` | Danh sách xem trận | Trận đang diễn ra | gamer | launch |
| `game.modes.spectate_view` | Xem trận | Chế độ khán giả | gamer | launch |
| `game.modes.tournament_list` | Giải đấu | Giải đấu đang mở | gamer | launch |
| `game.modes.tournament_detail` | Chi tiết giải | Luật, thưởng | gamer | launch |
| `game.modes.tournament_register` | Đăng ký giải | Đăng ký đội | gamer | launch |
| `game.modes.tournament_bracket` | Nhánh đấu | Sơ đồ nhánh đấu | gamer | launch |
| `game.modes.tournament_checkin` | Check-in giải | Xác nhận tham gia | gamer | launch |
| `game.modes.tournament_results` | Kết quả giải | Xếp hạng cuối | gamer | launch |

### Game client (Desktop/Android/iOS/Web) › ugc

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.ugc.ugc_home` | Xưởng sáng tạo | Trung tâm level do người chơi tạo | creator, gamer | launch |
| `game.ugc.ugc_my_levels` | Level của tôi | Level đã tạo | creator, gamer | launch |
| `game.ugc.ugc_editor_grid` | Trình sửa lưới | Đặt quái trên lưới 3x3 | creator, gamer | launch |
| `game.ugc.ugc_editor_enemy` | Chọn quái | Chọn quái được phép dùng | creator, gamer | launch |
| `game.ugc.ugc_editor_rules` | Luật level | Giới hạn lượt, điều kiện thắng | creator, gamer | launch |
| `game.ugc.ugc_editor_rewards` | Thưởng level | Thưởng trong giới hạn cho phép | creator, gamer | launch |
| `game.ugc.ugc_test_play` | Chơi thử | Chơi thử bắt buộc trước khi đăng | creator, gamer | launch |
| `game.ugc.ugc_publish` | Đăng level | Đăng công khai | creator, gamer | launch |
| `game.ugc.ugc_browse` | Duyệt level | Tìm level cộng đồng | creator, gamer | launch |
| `game.ugc.ugc_level_detail` | Chi tiết level | Đánh giá, tỉ lệ thắng | creator, gamer | launch |
| `game.ugc.ugc_rating` | Đánh giá level | Chấm điểm sau khi chơi | creator, gamer | launch |
| `game.ugc.ugc_report` | Báo cáo level | Báo cáo vi phạm | creator, gamer | launch |
| `game.ugc.ugc_featured` | Level nổi bật | Tuyển chọn của đội vận hành | creator, gamer | launch |
| `game.ugc.ugc_creator_profile` | Hồ sơ creator | Level của một creator | creator, gamer | launch |
| `game.ugc.ugc_creator_stats` | Thống kê creator | Lượt chơi, đánh giá | creator, gamer | launch |

### Game client (Desktop/Android/iOS/Web) › bonds

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.bonds.side_story_list` | Truyện phụ | Truyện riêng của từng anh hùng | gamer | launch |
| `game.bonds.side_story_detail` | Chi tiết truyện phụ | Các hồi, điều kiện mở | gamer | launch |
| `game.bonds.bond_overview` | Thân thiết | Mức thân thiết với anh hùng | gamer | launch |
| `game.bonds.bond_gift` | Tặng quà | Tặng quà tăng thân thiết | gamer | launch |
| `game.bonds.bond_level_up` | Tăng mức thân thiết | Mở khóa chỉ số/cảnh truyện | gamer | launch |
| `game.bonds.bond_scene` | Cảnh thân thiết | Cảnh truyện riêng | gamer | launch |
| `game.bonds.hero_quarters` | Khu nghỉ anh hùng | Anh hùng sinh hoạt trong làng | gamer | launch |

### Game client (Desktop/Android/iOS/Web) › abyss

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.abyss.abyss_home` | Vực Thẳm | Chế độ roguelike theo mùa | gamer | launch |
| `game.abyss.abyss_path` | Bản đồ đường đi | Chọn nhánh node | gamer | launch |
| `game.abyss.abyss_event` | Sự kiện node | Lựa chọn rủi ro/phần thưởng | gamer | launch |
| `game.abyss.abyss_relic_select` | Chọn di vật | Di vật thay đổi luật trận | gamer | launch |
| `game.abyss.abyss_shop` | Thương nhân Vực Thẳm | Mua bằng tiền tạm thời | gamer | launch |
| `game.abyss.abyss_rest` | Lửa trại | Hồi máu hoặc nâng di vật | gamer | launch |
| `game.abyss.abyss_result` | Kết quả Vực Thẳm | Điểm, thưởng mùa | gamer | launch |

### Game client (Desktop/Android/iOS/Web) › market

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.market.market_home` | Chợ giao dịch | Mua bán nguyên liệu giữa người chơi | gamer | launch |
| `game.market.market_listing` | Chi tiết rao bán | Giá, số lượng, người bán | gamer | launch |
| `game.market.market_sell` | Đăng bán | Đặt giá trong biên độ cho phép | gamer | launch |
| `game.market.market_orders` | Đơn của tôi | Đơn đang treo | gamer | launch |
| `game.market.market_history` | Lịch sử giao dịch | Giao dịch đã khớp | gamer | launch |

### Game client (Desktop/Android/iOS/Web) › guild_war

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.guild_war.guild_war_home` | Chiến tranh bang | Mùa, lịch, đối thủ | gamer | launch |
| `game.guild_war.guild_war_map` | Bản đồ chiến tranh | Cứ điểm cần chiếm | gamer | launch |
| `game.guild_war.guild_war_attack` | Tấn công cứ điểm | Chọn đội tấn công | gamer | launch |
| `game.guild_war.guild_war_results` | Kết quả chiến tranh | Điểm, thưởng | gamer | launch |
| `game.guild_war.guild_donate` | Quyên góp bang | Đóng góp tài nguyên | gamer | launch |
| `game.guild_war.guild_log` | Nhật ký bang | Hoạt động thành viên | gamer | launch |

### Game client (Desktop/Android/iOS/Web) › settings

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.settings.settings_home` | Cài đặt | Trung tâm cài đặt (thay SettingPP) | gamer, user | launch |
| `game.settings.settings_audio` | Âm thanh | Nhạc, hiệu ứng, lồng tiếng (được lưu) | gamer, user | launch |
| `game.settings.settings_graphics` | Đồ họa | Chất lượng, FPS, độ phân giải | gamer, user | launch |
| `game.settings.settings_controls` | Điều khiển | Joystick, độ nhạy | gamer, user | launch |
| `game.settings.settings_keybind` | Phím tắt | Gán phím (desktop) | gamer, user | launch |
| `game.settings.settings_language` | Ngôn ngữ | Đổi locale | gamer, user | launch |
| `game.settings.settings_accessibility` | Trợ năng | Cỡ chữ, mù màu, giảm chuyển động | gamer, user | launch |
| `game.settings.settings_notifications` | Thông báo đẩy | Bật/tắt từng loại | gamer, user | launch |
| `game.settings.settings_account` | Tài khoản | Liên kết, đăng xuất | gamer, user | launch |
| `game.settings.settings_privacy` | Quyền riêng tư | Quản lý đồng ý | gamer, user | launch |
| `game.settings.data_download` | Tải dữ liệu cá nhân | Yêu cầu xuất dữ liệu (GDPR) | gamer, user | launch |
| `game.settings.delete_account` | Xóa tài khoản | Xóa có thời gian chờ | gamer, user | launch |
| `game.settings.parental_controls` | Kiểm soát phụ huynh | Giới hạn chi tiêu/thời gian | gamer, user | launch |
| `game.settings.spending_limit` | Giới hạn chi tiêu | Đặt hạn mức nạp | gamer, user | launch |
| `game.settings.playtime_report` | Báo cáo thời gian chơi | Thời gian chơi theo tuần | gamer, user | launch |
| `game.settings.credits` | Danh đề | Thông tin đội phát triển | gamer, user | launch |
| `game.settings.help_center` | Trung tâm trợ giúp | Bài viết trợ giúp | gamer, user | launch |
| `game.settings.faq` | Câu hỏi thường gặp | FAQ trong game | gamer, user | launch |
| `game.settings.contact_support` | Liên hệ hỗ trợ | Tạo ticket | gamer, user | launch |
| `game.settings.ticket_list` | Ticket của tôi | Ticket đã gửi | gamer, user | launch |
| `game.settings.ticket_detail` | Chi tiết ticket | Trao đổi với CSKH | gamer, user | launch |
| `game.settings.bug_report` | Báo lỗi | Gửi lỗi kèm log + ảnh | gamer, user | launch |
| `game.settings.feedback_survey` | Khảo sát | Khảo sát trong game | gamer, user | launch |

### Game client (Desktop/Android/iOS/Web) › pilot

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.pilot.pilot_welcome` | Chào mừng pilot | Giới thiệu chương trình thử nghiệm | pilot | launch |
| `game.pilot.pilot_build_info` | Thông tin bản build | Phiên bản, môi trường, commit | pilot | launch |
| `game.pilot.pilot_tasks` | Nhiệm vụ pilot | Kịch bản cần chơi thử | pilot | launch |
| `game.pilot.pilot_task_detail` | Chi tiết nhiệm vụ pilot | Các bước và điều cần quan sát | pilot | launch |
| `game.pilot.pilot_quick_feedback` | Góp ý nhanh | Đánh giá 1 chạm tại màn hiện tại | pilot | launch |
| `game.pilot.pilot_bug_capture` | Chụp lỗi | Chụp màn hình, khoanh vùng, gửi kèm log | pilot | launch |
| `game.pilot.pilot_survey` | Khảo sát pilot | Khảo sát sau phiên chơi | pilot | launch |
| `game.pilot.pilot_known_issues` | Lỗi đã biết | Tránh báo trùng | pilot | launch |
| `game.pilot.pilot_changelog` | Thay đổi bản build | Khác biệt so với bản trước | pilot | launch |
| `game.pilot.pilot_rewards` | Quà pilot | Quà ghi nhận đóng góp | pilot | launch |

### Game client (Desktop/Android/iOS/Web) › debug

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.debug.debug_menu` | Menu debug | Chỉ có ở build dev/qa | dev, qa | launch |
| `game.debug.debug_screen_jump` | Nhảy màn hình | Mở bất kỳ ScreenId với fixture | dev, qa | launch |
| `game.debug.debug_cheats` | Cheat | Cộng tiền, EXP, mở khóa | dev, qa | launch |
| `game.debug.debug_battle_sandbox` | Sandbox trận | Dựng trận tùy ý với seed | dev, qa | launch |
| `game.debug.debug_atlas_browser` | Duyệt atlas | Xem region (thay CheckRegionScreen) | dev, qa | launch |
| `game.debug.debug_map_inspector` | Soi bản đồ | Collider, trigger, spawn | dev, qa | launch |
| `game.debug.debug_save_editor` | Sửa save | Sửa trực tiếp bản lưu | dev, qa | launch |
| `game.debug.debug_flags` | Feature flags | Bật/tắt cờ cục bộ | dev, qa | launch |
| `game.debug.debug_perf_overlay` | Hiệu năng | FPS, draw call, bộ nhớ | dev, qa | launch |
| `game.debug.debug_logs` | Log | Xem log runtime | dev, qa | launch |
| `game.debug.debug_network` | Mạng | Request/response, giả lập lag | dev, qa | launch |
| `game.debug.debug_locale_preview` | Xem bản dịch | Kiểm tra tràn chữ theo locale | dev, qa | launch |
| `game.debug.debug_automation` | Trạng thái automation | Kết nối test agent | dev, qa | launch |

### Web Console (admin/liveops/support/BI) › auth

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `console.auth.login` | Đăng nhập nội bộ | SSO nhân viên | admin, dev, qa, creator | launch |
| `console.auth.sso_callback` | SSO callback | Xử lý phản hồi OIDC | admin, dev, qa, creator | launch |
| `console.auth.mfa` | Xác thực 2 lớp | Bắt buộc với staff | admin, dev, qa, creator | launch |
| `console.auth.session_expired` | Hết phiên | Đăng nhập lại | admin, dev, qa, creator | launch |
| `console.auth.no_access` | Không có quyền | Giải thích quyền còn thiếu | admin, dev, qa, creator | launch |
| `console.auth.workspace_switch` | Đổi môi trường | Chọn dev/qa/staging/pilot/prod | admin, dev, qa, creator | launch |

### Web Console (admin/liveops/support/BI) › dashboards

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `console.dashboards.overview` | Tổng quan | KPI chính | admin | launch |
| `console.dashboards.realtime` | Thời gian thực | CCU, phiên, lỗi | admin | launch |
| `console.dashboards.economy` | Kinh tế | Nguồn/chi tiền tệ | admin | launch |
| `console.dashboards.retention` | Giữ chân | D1/D7/D30 | admin | launch |
| `console.dashboards.monetization` | Doanh thu | ARPU, ARPPU, tỉ lệ trả phí | admin | launch |
| `console.dashboards.battle_balance` | Cân bằng trận | Tỉ lệ thắng theo trận/đội | admin | launch |
| `console.dashboards.errors` | Lỗi | Crash, exception theo phiên bản | admin | launch |
| `console.dashboards.performance` | Hiệu năng | FPS, thời gian tải theo thiết bị | admin | launch |

### Web Console (admin/liveops/support/BI) › analytics

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `console.analytics.funnels` | Phễu | Phễu onboarding và mua hàng | admin | launch |
| `console.analytics.cohorts` | Cohort | Phân tích cohort | admin | launch |
| `console.analytics.ltv` | LTV | Giá trị vòng đời | admin | launch |
| `console.analytics.churn` | Rời bỏ | Dự báo rời bỏ | admin | launch |
| `console.analytics.session_heatmap` | Heatmap phiên | Giờ chơi theo ngày | admin | launch |
| `console.analytics.map_heatmap` | Heatmap bản đồ | Vị trí người chơi trên từng map | admin | launch |
| `console.analytics.encounter_winrates` | Tỉ lệ thắng theo trận | Phát hiện trận quá khó/dễ | admin | launch |
| `console.analytics.hero_pickrates` | Tỉ lệ chọn anh hùng | Meta đội hình | admin | launch |
| `console.analytics.item_usage` | Sử dụng vật phẩm | Vật phẩm dùng/bỏ phí | admin | launch |
| `console.analytics.recruit_analytics` | Phân tích chiêu mộ | Lượt quay, pity | admin | launch |
| `console.analytics.economy_sankey` | Dòng tiền | Sankey nguồn→chi | admin | launch |
| `console.analytics.report_builder` | Tạo báo cáo | Truy vấn tùy biến | admin | launch |
| `console.analytics.report_list` | Danh sách báo cáo | Báo cáo đã lưu | admin | launch |
| `console.analytics.scheduled_reports` | Báo cáo định kỳ | Gửi email/Slack định kỳ | admin | launch |
| `console.analytics.kpi_alerts` | Cảnh báo KPI | Ngưỡng cảnh báo | admin | launch |

### Web Console (admin/liveops/support/BI) › players

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `console.players.player_search` | Tìm người chơi | Theo ID, email, tên | admin | launch |
| `console.players.player_overview` | Người chơi — tổng quan | Tài khoản, trạng thái | admin | launch |
| `console.players.player_saves` | Người chơi — bản lưu | Phiên bản save, khôi phục | admin | launch |
| `console.players.player_heroes` | Người chơi — anh hùng | Anh hùng sở hữu | admin | launch |
| `console.players.player_inventory` | Người chơi — túi đồ | Vật phẩm, trang bị | admin | launch |
| `console.players.player_ledger` | Người chơi — sổ cái tiền tệ | Mọi giao dịch tiền tệ | admin | launch |
| `console.players.player_purchases` | Người chơi — giao dịch | IAP, hoàn tiền | admin | launch |
| `console.players.player_battles` | Người chơi — trận đấu | Replay, kết quả | admin | launch |
| `console.players.player_sessions` | Người chơi — phiên | Đăng nhập, thiết bị | admin | launch |
| `console.players.player_devices` | Người chơi — thiết bị | Thiết bị đã dùng | admin | launch |
| `console.players.player_mail_send` | Gửi thư | Gửi thư/quà cho 1 người | admin | launch |
| `console.players.player_grant` | Cấp phát | Cấp tài nguyên có lý do bắt buộc | admin | launch |
| `console.players.player_sanctions` | Xử phạt | Khóa chat, khóa tài khoản | admin | launch |
| `console.players.player_notes` | Ghi chú nội bộ | Ghi chú CSKH | admin | launch |
| `console.players.player_audit` | Lịch sử thao tác | Mọi thao tác admin lên người chơi | admin | launch |
| `console.players.player_gdpr_export` | Xuất dữ liệu | Thực hiện yêu cầu GDPR | admin | launch |
| `console.players.player_gdpr_delete` | Xóa dữ liệu | Xóa theo yêu cầu | admin | launch |
| `console.players.player_merge` | Gộp tài khoản | Gộp khách vào tài khoản chính | admin | launch |

### Web Console (admin/liveops/support/BI) › moderation

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `console.moderation.reports_queue` | Hàng đợi tố cáo | Tố cáo chờ xử lý | admin | launch |
| `console.moderation.report_detail` | Chi tiết tố cáo | Bằng chứng, quyết định | admin | launch |
| `console.moderation.chat_review` | Duyệt chat | Tin nhắn bị gắn cờ | admin | launch |
| `console.moderation.name_review` | Duyệt tên | Tên người chơi/bang | admin | launch |
| `console.moderation.ugc_review` | Duyệt level UGC | Level bị báo cáo/chờ nổi bật | admin | launch |
| `console.moderation.sanctions_list` | Danh sách xử phạt | Xử phạt đang hiệu lực | admin | launch |
| `console.moderation.appeals_queue` | Hàng đợi kháng nghị | Kháng nghị chờ xử lý | admin | launch |
| `console.moderation.appeal_detail` | Chi tiết kháng nghị | Quyết định kháng nghị | admin | launch |

### Web Console (admin/liveops/support/BI) › support

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `console.support.tickets_queue` | Hàng đợi ticket | Ticket theo SLA | admin | launch |
| `console.support.ticket_detail` | Chi tiết ticket | Trả lời, gắn nhãn | admin | launch |
| `console.support.csat` | Mức hài lòng | CSAT theo nhân viên | admin | launch |
| `console.support.refund_requests` | Yêu cầu hoàn tiền | Duyệt hoàn tiền | admin | launch |

### Web Console (admin/liveops/support/BI) › liveops

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `console.liveops.liveops_calendar` | Lịch LiveOps | Sự kiện, banner, bảo trì trên 1 lịch | admin | launch |
| `console.liveops.remote_config` | Remote config | Tham số runtime theo môi trường | admin | launch |
| `console.liveops.segment_builder` | Tạo phân khúc | Điều kiện nhóm người chơi | admin | launch |
| `console.liveops.ab_test_results` | Kết quả A/B | Ý nghĩa thống kê | admin | launch |
| `console.liveops.store_pricing_matrix` | Bảng giá | Giá theo quốc gia/store | admin | launch |
| `console.liveops.promo_redemptions` | Lượt đổi mã | Thống kê mã khuyến mãi | admin | launch |
| `console.liveops.economy_simulator` | Mô phỏng kinh tế | Giả lập lạm phát tiền tệ | admin | launch |
| `console.liveops.drop_rate_audit` | Kiểm toán tỉ lệ rơi | Tỉ lệ thực tế vs công bố | admin | launch |

### Web Console (admin/liveops/support/BI) › operations

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `console.operations.service_health` | Sức khỏe dịch vụ | Trạng thái từng service | admin, dev | launch |
| `console.operations.live_ccu_map` | Bản đồ CCU | Người chơi online theo vùng | admin, dev | launch |
| `console.operations.live_battle_feed` | Luồng trận trực tiếp | Trận đang diễn ra | admin, dev | launch |
| `console.operations.live_purchase_feed` | Luồng giao dịch | Giao dịch thời gian thực | admin, dev | launch |
| `console.operations.incident_list` | Sự cố | Danh sách sự cố | admin, dev | launch |
| `console.operations.incident_detail` | Chi tiết sự cố | Diễn biến, hành động | admin, dev | launch |
| `console.operations.oncall_schedule` | Lịch trực | Phân công trực | admin, dev | launch |
| `console.operations.alert_rules` | Luật cảnh báo | Ngưỡng hạ tầng | admin, dev | launch |
| `console.operations.status_page_editor` | Sửa trang trạng thái | Cập nhật status công khai | admin, dev | launch |
| `console.operations.deployments` | Triển khai | Lịch sử deploy theo môi trường | admin, dev | launch |
| `console.operations.environments` | Môi trường | Cấu hình từng môi trường | admin, dev | launch |
| `console.operations.content_release_diff` | So sánh bản nội dung | Diff giữa 2 content pack | admin, dev | launch |
| `console.operations.content_rollback` | Hoàn tác nội dung | Rollback content pack | admin, dev | launch |
| `console.operations.backups` | Sao lưu | Sao lưu/khôi phục CSDL | admin, dev | launch |
| `console.operations.audit_log` | Nhật ký kiểm toán | Mọi thao tác staff | admin, dev | launch |
| `console.operations.integrations` | Tích hợp | Store, thanh toán, Slack | admin, dev | launch |
| `console.operations.localization_status` | Tiến độ dịch | % dịch theo locale | admin, dev | launch |
| `console.operations.system_settings` | Cài đặt hệ thống | Cấu hình chung | admin, dev | launch |

### Web Console (admin/liveops/support/BI) › finance

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `console.finance.revenue_report` | Báo cáo doanh thu | Theo ngày/store/quốc gia | admin | launch |
| `console.finance.store_reconciliation` | Đối soát store | Khớp giao dịch với Apple/Google | admin | launch |
| `console.finance.tax_report` | Báo cáo thuế | Theo khu vực | admin | launch |
| `console.finance.chargebacks` | Chargeback | Giao dịch bị đòi lại | admin | launch |
| `console.finance.fraud_detection` | Phát hiện gian lận | Giao dịch bất thường | admin | launch |
| `console.finance.fraud_case` | Hồ sơ gian lận | Xử lý một vụ | admin | launch |

### Web Console (admin/liveops/support/BI) › anticheat

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `console.anticheat.anticheat_flags` | Cờ gian lận | Tài khoản bị gắn cờ | admin, dev | launch |
| `console.anticheat.battle_validation_failures` | Trận không hợp lệ | Kết quả client ≠ mô phỏng server | admin, dev | launch |
| `console.anticheat.anomaly_detail` | Chi tiết bất thường | Bằng chứng, replay | admin, dev | launch |
| `console.anticheat.device_fingerprints` | Dấu vân thiết bị | Nhiều tài khoản cùng thiết bị | admin, dev | launch |
| `console.anticheat.ban_waves` | Đợt khóa | Lên lịch khóa hàng loạt | admin, dev | launch |

### Web Console (admin/liveops/support/BI) › compliance

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `console.compliance.gacha_disclosure` | Công bố tỉ lệ | Báo cáo tỉ lệ theo luật từng nước | admin | launch |
| `console.compliance.age_rating` | Xếp hạng tuổi | Cấu hình theo khu vực | admin | launch |
| `console.compliance.data_retention` | Lưu trữ dữ liệu | Chính sách thời hạn lưu | admin | launch |
| `console.compliance.consent_records` | Hồ sơ đồng ý | Lịch sử đồng ý của người chơi | admin | launch |

### Creator Studio (web) › editors

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `studio.editors.studio_home` | Trang chủ Studio | Việc cần làm, thay đổi gần đây | creator | launch |
| `studio.editors.map_editor` | Sửa bản đồ | Lớp nền, collider, vùng (tích hợp Tiled) | creator | launch |
| `studio.editors.map_object_editor` | Sửa đối tượng bản đồ | NPC, rương, biển báo | creator | launch |
| `studio.editors.encounter_placer` | Đặt trận trên bản đồ | Gắn encounter vào vùng | creator | launch |
| `studio.editors.teleport_graph` | Đồ thị cổng dịch chuyển | Liên kết map↔map, phát hiện cổng hỏng | creator | launch |
| `studio.editors.world_graph` | Đồ thị thế giới | Vùng, chương, điều kiện mở khóa | creator | launch |
| `studio.editors.dialogue_graph` | Sửa hội thoại dạng đồ thị | Nhánh, điều kiện, biến | creator | launch |
| `studio.editors.dialogue_preview` | Xem trước hội thoại | Chạy thử hội thoại | creator | launch |
| `studio.editors.cutscene_timeline` | Timeline cutscene | Camera, thoại, hoạt ảnh | creator | launch |
| `studio.editors.quest_flow` | Sửa luồng nhiệm vụ | Chuỗi nhiệm vụ, điều kiện | creator | launch |
| `studio.editors.quest_preview` | Xem trước nhiệm vụ | Chạy thử luồng nhiệm vụ | creator | launch |
| `studio.editors.encounter_grid` | Sửa trận 3x3 | Kế thừa Level Editor hiện có | creator | launch |
| `studio.editors.encounter_simulator` | Mô phỏng trận | Chạy N trận với seed, tỉ lệ thắng | creator | launch |
| `studio.editors.balance_lab` | Phòng cân bằng | So sánh chỉ số, lực chiến toàn roster | creator | launch |
| `studio.editors.stat_curve_editor` | Đường cong chỉ số | Tăng trưởng theo cấp/sao | creator | launch |
| `studio.editors.formula_playground` | Thử công thức | Công thức sát thương/phòng thủ | creator | launch |
| `studio.editors.loot_simulator` | Mô phỏng rơi đồ | Phân phối rơi đồ | creator | launch |
| `studio.editors.gacha_simulator` | Mô phỏng chiêu mộ | Tỉ lệ + pity | creator | launch |
| `studio.editors.economy_flow` | Dòng kinh tế | Nguồn/chi theo tiến trình | creator | launch |
| `studio.editors.skill_vfx_preview` | Xem VFX kỹ năng | Ghép kỹ năng với hiệu ứng | creator | launch |
| `studio.editors.animation_preview` | Xem hoạt ảnh | Idle/walk/attack… | creator | launch |
| `studio.editors.atlas_packer` | Đóng gói atlas | Chạy TexturePacker, xem kết quả | creator | launch |
| `studio.editors.sprite_import` | Nhập sprite | Nhập từ Aseprite | creator | launch |
| `studio.editors.sprite_slicer` | Cắt sprite | Cắt sheet thành frame | creator | launch |
| `studio.editors.palette_manager` | Bảng màu | Palette chuẩn | creator | launch |
| `studio.editors.music_playlist` | Danh sách nhạc | Nhạc theo vùng/trạng thái | creator | launch |
| `studio.editors.localization_editor` | Sửa bản dịch | Theo key/locale | creator | launch |
| `studio.editors.localization_review` | Duyệt bản dịch | Quy trình duyệt | creator | launch |
| `studio.editors.translator_queue` | Hàng đợi dịch | Key chưa dịch | creator | launch |
| `studio.editors.translation_memory` | Bộ nhớ dịch | Gợi ý từ bản dịch cũ | creator | launch |
| `studio.editors.glossary_editor` | Sửa thuật ngữ | Thuật ngữ bắt buộc | creator | launch |
| `studio.editors.lore_wiki` | Wiki lore | Tài liệu thế giới | creator | launch |
| `studio.editors.validation_report` | Báo cáo kiểm tra nội dung | Lỗi schema, tham chiếu hỏng | creator | launch |
| `studio.editors.content_diff` | So sánh nội dung | Diff giữa nhánh nội dung | creator | launch |
| `studio.editors.content_branches` | Nhánh nội dung | Làm việc song song | creator | launch |
| `studio.editors.content_publish` | Xuất bản nội dung | Build content pack | creator | launch |
| `studio.editors.asset_usage_graph` | Đồ thị sử dụng asset | Asset nào dùng ở đâu | creator | launch |
| `studio.editors.unused_assets` | Asset không dùng | Danh sách để dọn | creator | launch |
| `studio.editors.ui_layout_editor` | Sửa bố cục UI | Layout màn hình theo token | creator | launch |
| `studio.editors.theme_tokens` | Design tokens | Màu, font, khoảng cách | creator | launch |
| `studio.editors.screen_catalog` | Danh mục màn hình | Duyệt catalog này | creator | launch |

### Creator Studio (web) › production

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `studio.production.character_bible` | Hồ sơ nhân vật | Tính cách, giọng, quan hệ | creator | launch |
| `studio.production.voice_lines` | Danh sách thoại | Câu thoại cần thu âm | creator | launch |
| `studio.production.voice_tracker` | Tiến độ thu âm | Trạng thái từng câu | creator | launch |
| `studio.production.story_timeline` | Dòng thời gian cốt truyện | Sự kiện theo niên đại | creator | launch |
| `studio.production.art_requests` | Bảng yêu cầu art | Yêu cầu vẽ từ design | creator | launch |
| `studio.production.art_request_detail` | Chi tiết yêu cầu art | Brief, tham khảo, hạn | creator | launch |
| `studio.production.art_review` | Duyệt art | Duyệt/yêu cầu sửa | creator | launch |
| `studio.production.concept_gallery` | Thư viện concept | Concept art đã duyệt | creator | launch |
| `studio.production.map_playtest_heatmap` | Heatmap playtest | Người chơi kẹt ở đâu | creator | launch |
| `studio.production.difficulty_curve` | Đường cong độ khó | Độ khó theo tiến trình | creator | launch |

### Dev Tools (web) › engineering

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `devtools.engineering.builds` | Danh sách build | Build theo nền tảng/kênh | dev | launch |
| `devtools.engineering.build_detail` | Chi tiết build | Artifact, log, kích thước | dev | launch |
| `devtools.engineering.build_trigger` | Chạy build | Kích hoạt pipeline | dev | launch |
| `devtools.engineering.pipelines` | Pipeline | Trạng thái CI/CD | dev | launch |
| `devtools.engineering.test_results` | Kết quả test | Unit/integration/e2e | dev | launch |
| `devtools.engineering.flaky_tests` | Test chập chờn | Phát hiện test không ổn định | dev | launch |
| `devtools.engineering.coverage` | Độ phủ | Coverage theo module | dev | launch |
| `devtools.engineering.crashes` | Crash | Nhóm crash theo chữ ký | dev | launch |
| `devtools.engineering.crash_detail` | Chi tiết crash | Stack, thiết bị, breadcrumb | dev | launch |
| `devtools.engineering.log_search` | Tìm log | Log tập trung | dev | launch |
| `devtools.engineering.traces` | Trace | Trace phân tán | dev | launch |
| `devtools.engineering.perf_benchmarks` | Benchmark | Hiệu năng theo build | dev | launch |
| `devtools.engineering.perf_regression` | Suy giảm hiệu năng | So sánh giữa build | dev | launch |
| `devtools.engineering.app_size` | Kích thước app | Theo nền tảng | dev | launch |
| `devtools.engineering.asset_size` | Kích thước asset | Asset lớn nhất | dev | launch |
| `devtools.engineering.api_explorer` | API explorer | Gọi thử API | dev | launch |
| `devtools.engineering.api_schema_diff` | Diff schema API | Thay đổi phá vỡ | dev | launch |
| `devtools.engineering.db_migrations` | Migration CSDL | Trạng thái migration | dev | launch |
| `devtools.engineering.db_readonly_query` | Truy vấn chỉ đọc | SQL chỉ đọc có kiểm toán | dev | launch |
| `devtools.engineering.env_config_diff` | Diff cấu hình | So sánh giữa môi trường | dev | launch |
| `devtools.engineering.secrets_audit` | Kiểm toán secret | Secret sắp hết hạn | dev | launch |
| `devtools.engineering.dependency_updates` | Cập nhật thư viện | Thư viện lỗi thời/lỗ hổng | dev | launch |
| `devtools.engineering.replay_debugger` | Debug replay | Kiểm tra tính tất định của trận | dev | launch |
| `devtools.engineering.save_migration_tester` | Kiểm tra migrate save | Chạy migrator trên save mẫu | dev | launch |
| `devtools.engineering.protocol_inspector` | Soi protocol | Gói tin client↔server | dev | launch |
| `devtools.engineering.screen_registry` | Registry màn hình | ScreenId ↔ lớp code | dev | launch |
| `devtools.engineering.screenshot_diff` | So ảnh màn hình | Khác biệt giữa build | dev | launch |
| `devtools.engineering.missing_loc_keys` | Key dịch thiếu | Key dùng trong code chưa có bản dịch | dev | launch |
| `devtools.engineering.load_tests` | Kiểm thử tải | Kết quả load test | dev | launch |
| `devtools.engineering.chaos_experiments` | Chaos test | Thử lỗi hạ tầng | dev | launch |
| `devtools.engineering.cdn_cache` | CDN | Trạng thái cache content pack | dev | launch |
| `devtools.engineering.content_pack_versions` | Phiên bản content pack | Tương thích client | dev | launch |
| `devtools.engineering.client_config_matrix` | Ma trận cấu hình client | Build × môi trường × flag | dev | launch |
| `devtools.engineering.developer_portal` | Cổng nhà phát triển | API công khai cho bên thứ ba | dev | launch |
| `devtools.engineering.webhooks` | Webhook | Đăng ký sự kiện | dev | launch |

### Dev Tools (web) › release

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `devtools.release.code_ownership` | Chủ sở hữu mã | Module ↔ đội phụ trách | dev | launch |
| `devtools.release.architecture_rules` | Luật kiến trúc | Vi phạm phụ thuộc giữa module | dev | launch |
| `devtools.release.release_train` | Chuyến tàu release | Lịch cắt nhánh, freeze | dev | launch |
| `devtools.release.hotfix_request` | Yêu cầu hotfix | Quy trình hotfix | dev | launch |
| `devtools.release.sdk_versions` | Phiên bản SDK | SDK bên thứ ba theo nền tảng | dev | launch |

### QA Hub (web) › insights

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `qa.insights.bug_duplicates` | Lỗi trùng | Gộp lỗi trùng | qa | launch |
| `qa.insights.test_charter` | Charter kiểm thử | Mục tiêu phiên khám phá | qa | launch |
| `qa.insights.quality_trend` | Xu hướng chất lượng | Lỗi theo release | qa | launch |
| `qa.insights.agent_failure_clusters` | Nhóm lỗi agent | Gom lỗi agent theo nguyên nhân | qa | launch |
| `qa.insights.agent_budget` | Ngân sách agent | Chi phí mỗi lượt chạy agent | qa | launch |

### QA Hub (web) › quality

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `qa.quality.qa_home` | Trang chủ QA | Tình trạng chất lượng bản hiện tại | qa | launch |
| `qa.quality.test_plans` | Kế hoạch test | Theo release | qa | launch |
| `qa.quality.test_plan_detail` | Chi tiết kế hoạch | Phạm vi, người phụ trách | qa | launch |
| `qa.quality.test_cases` | Test case | Kho test case | qa | launch |
| `qa.quality.test_case_edit` | Sửa test case | Bước, kỳ vọng, ScreenId liên quan | qa | launch |
| `qa.quality.test_run_create` | Tạo lượt test | Chọn case × thiết bị | qa | launch |
| `qa.quality.test_run_execute` | Thực thi test | Đánh dấu pass/fail từng bước | qa | launch |
| `qa.quality.test_run_results` | Kết quả lượt test | Tổng hợp | qa | launch |
| `qa.quality.bugs` | Danh sách lỗi | Lọc theo mức độ | qa | launch |
| `qa.quality.bug_detail` | Chi tiết lỗi | Tái hiện, log, video | qa | launch |
| `qa.quality.bug_create` | Tạo lỗi | Mẫu báo lỗi chuẩn | qa | launch |
| `qa.quality.bug_triage` | Phân loại lỗi | Họp triage | qa | launch |
| `qa.quality.regression_suite` | Bộ hồi quy | Case hồi quy tự động | qa | launch |
| `qa.quality.device_matrix` | Ma trận thiết bị | Thiết bị × hệ điều hành | qa | launch |
| `qa.quality.device_farm` | Trang trại thiết bị | Trạng thái thiết bị thật | qa | launch |
| `qa.quality.agent_runs` | Lượt chạy test agent | Kết quả agent tự động | qa | launch |
| `qa.quality.agent_run_detail` | Chi tiết lượt agent | Hành động, ảnh, lỗi | qa | launch |
| `qa.quality.agent_scenarios` | Kịch bản agent | Mục tiêu agent phải đạt | qa | launch |
| `qa.quality.agent_coverage` | Độ phủ màn hình | ScreenId đã/chưa được agent ghé | qa | launch |
| `qa.quality.visual_regression` | Hồi quy giao diện | Duyệt khác biệt ảnh | qa | launch |
| `qa.quality.balance_regression` | Hồi quy cân bằng | Tỉ lệ thắng thay đổi bất thường | qa | launch |
| `qa.quality.smoke_checklist` | Smoke test | Checklist nhanh mỗi build | qa | launch |
| `qa.quality.release_signoff` | Ký duyệt release | Điều kiện phát hành | qa | launch |
| `qa.quality.compatibility_report` | Tương thích | Kết quả theo thiết bị | qa | launch |
| `qa.quality.accessibility_audit` | Kiểm tra trợ năng | Tương phản, cỡ chữ | qa | launch |
| `qa.quality.localization_qa` | QA bản dịch | Tràn chữ, sai ngữ cảnh | qa | launch |
| `qa.quality.performance_qa` | QA hiệu năng | FPS, bộ nhớ, pin | qa | launch |
| `qa.quality.save_compat_matrix` | Tương thích bản lưu | Save cũ × build mới | qa | launch |
| `qa.quality.known_issues` | Lỗi đã biết | Bảng lỗi đã biết | qa | launch |
| `qa.quality.exploratory_session` | Phiên kiểm thử khám phá | Ghi chú theo thời gian | qa | launch |
| `qa.quality.session_replay` | Xem lại phiên | Video + log đồng bộ | qa | launch |
| `qa.quality.crash_repro` | Tái hiện crash | Bước tái hiện | qa | launch |
| `qa.quality.test_data_generator` | Sinh dữ liệu test | Tạo tài khoản/save theo mẫu | qa | launch |
| `qa.quality.save_fixtures` | Thư viện save mẫu | Save ở từng mốc tiến trình | qa | launch |
| `qa.quality.network_conditions` | Giả lập mạng | Mất gói, độ trễ | qa | launch |

### Pilot Portal (web) › program

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `pilot.program.pilot_signup` | Đăng ký pilot | Đăng ký tham gia thử nghiệm | pilot | launch |
| `pilot.program.pilot_nda` | Thỏa thuận bảo mật | Ký NDA điện tử | pilot | launch |
| `pilot.program.pilot_profile` | Hồ sơ pilot | Kinh nghiệm, sở thích thể loại | pilot | launch |
| `pilot.program.pilot_devices` | Thiết bị của tôi | Thiết bị dùng để test | pilot | launch |
| `pilot.program.pilot_builds` | Tải build | Build được phép tải | pilot | launch |
| `pilot.program.pilot_build_notes` | Ghi chú build | Trọng tâm cần test | pilot | launch |
| `pilot.program.pilot_missions` | Nhiệm vụ pilot | Kịch bản được giao | pilot | launch |
| `pilot.program.pilot_mission_detail` | Chi tiết nhiệm vụ | Hướng dẫn, hạn chót | pilot | launch |
| `pilot.program.pilot_feedback` | Góp ý đã gửi | Trạng thái góp ý | pilot | launch |
| `pilot.program.pilot_feedback_new` | Gửi góp ý | Mẫu góp ý | pilot | launch |
| `pilot.program.pilot_bugs` | Lỗi đã báo | Trạng thái xử lý | pilot | launch |
| `pilot.program.pilot_bug_new` | Báo lỗi | Mẫu báo lỗi + tải video | pilot | launch |
| `pilot.program.pilot_surveys` | Khảo sát | Khảo sát cần làm | pilot | launch |
| `pilot.program.pilot_survey_take` | Làm khảo sát | Trả lời khảo sát | pilot | launch |
| `pilot.program.pilot_leaderboard` | Bảng đóng góp | Pilot đóng góp nhiều nhất | pilot | launch |
| `pilot.program.pilot_rewards` | Quà pilot | Quà đã nhận/có thể nhận | pilot | launch |
| `pilot.program.pilot_forum` | Diễn đàn pilot | Thảo luận kín | pilot | launch |
| `pilot.program.pilot_forum_thread` | Chủ đề diễn đàn | Chi tiết thảo luận | pilot | launch |
| `pilot.program.pilot_calendar` | Lịch playtest | Buổi playtest sắp tới | pilot | launch |
| `pilot.program.pilot_playtest_booking` | Đặt lịch playtest | Đăng ký buổi có quan sát | pilot | launch |
| `pilot.program.pilot_live_room` | Phòng playtest trực tiếp | Chia sẻ màn hình, thoại | pilot | launch |
| `pilot.program.pilot_build_vote` | Bình chọn phương án | So sánh 2 phương án thiết kế | pilot | launch |
| `pilot.program.pilot_telemetry_consent` | Đồng ý telemetry | Quyền ghi heatmap/video | pilot | launch |
| `pilot.program.pilot_exit` | Rời chương trình | Rời và xóa dữ liệu pilot | pilot | launch |

### Pilot Portal (web) › onboarding

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `pilot.onboarding.pilot_cohort` | Nhóm của tôi | Thông tin cohort pilot | pilot | launch |
| `pilot.onboarding.pilot_guide` | Hướng dẫn pilot | Cách test và báo lỗi hiệu quả | pilot | launch |
| `pilot.onboarding.pilot_certificate` | Chứng nhận pilot | Ghi nhận đóng góp | pilot | launch |

### Public Site + Account Portal (web) › public

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `site.public.landing` | Trang chủ | Giới thiệu game, tải về | user, gamer | launch |
| `site.public.news` | Tin tức | Danh sách tin | user, gamer | launch |
| `site.public.news_detail` | Chi tiết tin | Bài viết | user, gamer | launch |
| `site.public.patch_notes_web` | Ghi chú cập nhật | Theo phiên bản | user, gamer | launch |
| `site.public.guides` | Cẩm nang | Danh sách hướng dẫn | user, gamer | launch |
| `site.public.guide_detail` | Chi tiết cẩm nang | Bài hướng dẫn | user, gamer | launch |
| `site.public.heroes_gallery` | Thư viện anh hùng | Toàn bộ anh hùng | user, gamer | launch |
| `site.public.hero_page` | Trang anh hùng | Chi tiết một anh hùng | user, gamer | launch |
| `site.public.media_kit` | Media kit | Tài nguyên báo chí | user, gamer | launch |
| `site.public.faq_web` | FAQ | Câu hỏi thường gặp | user, gamer | launch |
| `site.public.support_portal` | Cổng hỗ trợ | Gửi/xem ticket | user, gamer | launch |
| `site.public.status_page` | Trạng thái dịch vụ | Tình trạng server công khai | user, gamer | launch |
| `site.public.legal_terms` | Điều khoản | ToS | user, gamer | launch |
| `site.public.legal_privacy` | Chính sách riêng tư | Privacy policy | user, gamer | launch |
| `site.public.community_forum` | Diễn đàn | Cộng đồng người chơi | user, gamer | launch |
| `site.public.forum_thread` | Chủ đề diễn đàn | Thảo luận | user, gamer | launch |
| `site.public.fan_art` | Fan art | Thư viện sáng tác cộng đồng | user, gamer | launch |
| `site.public.creator_program` | Chương trình creator | Quyền lợi cho UGC creator | user, gamer | launch |
| `site.public.ugc_web_browser` | Duyệt level UGC trên web | Tìm và gửi level vào game | user, gamer | launch |
| `site.public.account_login` | Đăng nhập tài khoản | Cổng tài khoản | user, gamer | launch |
| `site.public.account_overview` | Tổng quan tài khoản | Thông tin, slot | user, gamer | launch |
| `site.public.account_security` | Bảo mật | Mật khẩu, 2FA, thiết bị | user, gamer | launch |
| `site.public.account_purchases` | Lịch sử mua | Giao dịch | user, gamer | launch |
| `site.public.account_linked` | Tài khoản liên kết | Google/Apple | user, gamer | launch |
| `site.public.account_delete` | Xóa tài khoản | Xóa từ web | user, gamer | launch |
| `site.public.redeem_code` | Đổi mã quà | Nhập mã khuyến mãi | user, gamer | launch |
| `site.public.esports` | Esports | Giải đấu chính thức | user, gamer | launch |
| `site.public.events_public` | Lịch sự kiện | Sự kiện sắp tới | user, gamer | launch |
| `site.public.roadmap_public` | Lộ trình phát triển | Tính năng sắp ra mắt | user, gamer | launch |

### Game client (Desktop/Android/iOS/Web) › festivals

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.festivals.festival_calendar` | Lịch lễ hội | Lễ hội theo âm lịch: Tết, Trung Thu, Hội Đèn, Đoan Ngọ | gamer, pilot | s1 |
| `game.festivals.festival_hub` | Quảng trường lễ hội | Làng đổi trang trí, NPC, nhạc theo lễ hội | gamer, pilot | s1 |
| `game.festivals.lantern_craft` | Làm đèn lồng | Ghép hình + màu, đèn thành vật trang trí | gamer, pilot | s1 |
| `game.festivals.lantern_release` | Thả đèn | Gửi lời chúc; đèn bay trên map của người chơi khác | gamer, pilot | s1 |
| `game.festivals.lucky_envelope` | Lì xì | Tặng lì xì vàng cho bạn bè (giới hạn chống lạm phát) | gamer, pilot | s1 |
| `game.festivals.mooncake_kitchen` | Bếp bánh trung thu | Minigame nấu theo công thức | gamer, pilot | s1 |
| `game.festivals.lion_dance` | Múa lân | Minigame nhịp điệu (input thành command tất định) | gamer, pilot | s1 |
| `game.festivals.dragon_boat` | Đua thuyền rồng | Minigame phối hợp đội | gamer, pilot | s1 |
| `game.festivals.festival_quests` | Nhiệm vụ lễ hội | Chuỗi nhiệm vụ theo lễ | gamer, pilot | s1 |
| `game.festivals.festival_shop` | Chợ phiên | Cửa hàng token lễ hội | gamer, pilot | s1 |
| `game.festivals.festival_album` | Sổ lưu niệm lễ hội | Kỷ vật đã sưu tầm mỗi năm | gamer, pilot | s1 |
| `game.festivals.festival_rewards` | Quà lễ hội | Mốc quà theo điểm tham gia | gamer, pilot | s1 |

### Game client (Desktop/Android/iOS/Web) › class_evolution

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.class_evolution.evolution_tree` | Cây tiến hóa lớp | 6 lớp gốc → 12 lớp nâng cao | gamer, pilot | s1 |
| `game.class_evolution.evolution_path_detail` | Chi tiết nhánh | So sánh 2 nhánh của một lớp | gamer, pilot | s1 |
| `game.class_evolution.evolution_trial` | Thử thách thăng lớp | Trận thử thách riêng mỗi nhánh | gamer, pilot | s1 |
| `game.class_evolution.evolution_confirm` | Xác nhận thăng lớp | Chọn nhánh, cảnh báo đổi nhánh tốn phí | gamer, pilot | s1 |
| `game.class_evolution.evolution_ceremony` | Lễ thăng lớp | Cutscene đổi ngoại hình/kỹ năng | gamer, pilot | s1 |
| `game.class_evolution.evolution_respec` | Đổi nhánh | Hoàn nguyên tài nguyên có phí | gamer, pilot | s1 |
| `game.class_evolution.evolution_counter_chart` | Bảng khắc chế nâng cao | Vòng khắc chế 12 lớp | gamer, pilot | s1 |
| `game.class_evolution.evolution_codex` | Bách khoa lớp nâng cao | Lore từng lớp | gamer, pilot | s1 |

### Game client (Desktop/Android/iOS/Web) › academy

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.academy.academy_home` | Học viện anh hùng | Trung tâm huấn luyện | gamer, pilot | s1 |
| `game.academy.talent_tree` | Cây thiên phú | Điểm thiên phú theo cấp | gamer, pilot | s1 |
| `game.academy.talent_node_detail` | Chi tiết thiên phú | Hiệu ứng từng nút | gamer, pilot | s1 |
| `game.academy.talent_reset` | Tẩy điểm thiên phú | Hoàn điểm có phí | gamer, pilot | s1 |
| `game.academy.talent_presets` | Bộ thiên phú lưu sẵn | Đổi nhanh theo chế độ | gamer, pilot | s1 |
| `game.academy.training_dummy` | Hình nộm tập luyện | Thử DPS trong sandbox tất định | gamer, pilot | s1 |
| `game.academy.sparring` | Đấu tập | Đấu 2 đội của mình, không mất gì | gamer, pilot | s1 |
| `game.academy.lesson_library` | Thư viện bài học | Bài học chiến thuật tương tác | gamer, pilot | s1 |
| `game.academy.lesson_play` | Học bài | Tình huống có hướng dẫn | gamer, pilot | s1 |
| `game.academy.academy_exam` | Thi tốt nghiệp | Nhận danh hiệu và thưởng | gamer, pilot | s1 |

### Game client (Desktop/Android/iOS/Web) › wardrobe

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.wardrobe.wardrobe_home` | Tủ đồ | Trang phục anh hùng | gamer, user, pilot | s1 |
| `game.wardrobe.skin_detail` | Chi tiết trang phục | Xem 360°, hoạt ảnh riêng | gamer, user, pilot | s1 |
| `game.wardrobe.dye_studio` | Xưởng nhuộm | Đổi palette sprite bằng shader | gamer, user, pilot | s1 |
| `game.wardrobe.dye_collection` | Bộ màu nhuộm | Màu đã mở khóa | gamer, user, pilot | s1 |
| `game.wardrobe.outfit_presets` | Bộ phối đồ | Lưu phối đồ | gamer, user, pilot | s1 |
| `game.wardrobe.skin_try_on` | Thử đồ | Thử trong trận mẫu | gamer, user, pilot | s1 |
| `game.wardrobe.emote_collection` | Bộ biểu cảm | Emote đã có | gamer, user, pilot | s1 |
| `game.wardrobe.voice_pack_select` | Gói giọng nói | Đổi giọng anh hùng | gamer, user, pilot | s1 |

### Game client (Desktop/Android/iOS/Web) › research

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.research.research_board` | Bảng nghiên cứu quái | Tiến độ nghiên cứu mỗi loài | gamer, pilot | s1 |
| `game.research.research_entry` | Hồ sơ loài | Điểm yếu mở theo số lần đánh bại/quan sát | gamer, pilot | s1 |
| `game.research.field_notes` | Sổ tay thực địa | Ghi chú tự động từ BattleEvent | gamer, pilot | s1 |
| `game.research.research_milestone` | Mốc nghiên cứu | Mở khóa gợi ý chiến thuật | gamer, pilot | s1 |
| `game.research.habitat_map` | Bản đồ môi trường sống | Quái xuất hiện ở đâu, khi nào | gamer, pilot | s1 |
| `game.research.specimen_gallery` | Bộ sưu tập mẫu vật | Mô hình quái 3 sao | gamer, pilot | s1 |

### Game client (Desktop/Android/iOS/Web) › puzzles

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.puzzles.puzzle_home` | Câu đố chiến thuật | Thắng trong N lượt với đội cho sẵn | gamer, pilot | s1 |
| `game.puzzles.puzzle_daily` | Câu đố hằng ngày | Một câu đố chung toàn server | gamer, pilot | s1 |
| `game.puzzles.puzzle_pack_list` | Bộ câu đố | Theo chủ đề/độ khó | gamer, pilot | s1 |
| `game.puzzles.puzzle_play` | Giải câu đố | Trận có giới hạn lượt, hoàn tác không giới hạn | gamer, pilot | s1 |
| `game.puzzles.puzzle_hint` | Gợi ý | Lời giải từng bước (tốn điểm gợi ý) | gamer, pilot | s1 |
| `game.puzzles.puzzle_result` | Kết quả câu đố | Số lượt, sao, thời gian | gamer, pilot | s1 |
| `game.puzzles.puzzle_leaderboard` | Xếp hạng câu đố | Lời giải ít lượt nhất | gamer, pilot | s1 |
| `game.puzzles.puzzle_share` | Chia sẻ lời giải | Mã replay của lời giải | gamer, pilot | s1 |

### Game client (Desktop/Android/iOS/Web) › seasons_weather

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.seasons_weather.season_overview` | Mùa trong năm | Xuân/Hạ/Thu/Đông đổi theo tuần thật | gamer, pilot | s1 |
| `game.seasons_weather.season_effects` | Hiệu ứng mùa | Buff/debuff theo mùa lên lớp nhân vật | gamer, pilot | s1 |
| `game.seasons_weather.weather_forecast` | Dự báo thời tiết | Thời tiết 3 ngày tới trên mỗi vùng | gamer, pilot | s1 |
| `game.seasons_weather.weather_battle_preview` | Thời tiết trong trận | Mưa giảm lửa, sương tăng né… | gamer, pilot | s1 |
| `game.seasons_weather.season_collectibles` | Vật phẩm theo mùa | Chỉ thu được trong mùa | gamer, pilot | s1 |
| `game.seasons_weather.season_journal` | Nhật ký bốn mùa | Hoạt động đã làm trong mùa | gamer, pilot | s1 |

### Game client (Desktop/Android/iOS/Web) › accessibility_plus

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.accessibility_plus.one_hand_mode` | Chế độ một tay | Bố cục nút cho một tay | gamer, user | s1 |
| `game.accessibility_plus.colorblind_preview` | Xem trước chế độ mù màu | 3 bộ lọc + palette an toàn | gamer, user | s1 |
| `game.accessibility_plus.text_to_speech` | Đọc văn bản | Đọc thoại/menu bằng TTS nền tảng | gamer, user | s1 |
| `game.accessibility_plus.battle_pace_assist` | Trợ giúp nhịp độ | Tắt giới hạn thời gian, gợi ý mục tiêu | gamer, user | s1 |
| `game.accessibility_plus.motion_comfort` | Giảm chuyển động | Tắt rung, flash, parallax | gamer, user | s1 |
| `game.accessibility_plus.input_remap_gamepad` | Gán nút tay cầm | Hỗ trợ gamepad đầy đủ | gamer, user | s1 |
| `game.accessibility_plus.subtitle_style` | Kiểu phụ đề | Cỡ, nền, màu người nói | gamer, user | s1 |
| `game.accessibility_plus.difficulty_assist` | Hỗ trợ độ khó | Chế độ kể chuyện | gamer, user | s1 |

### Game client (Desktop/Android/iOS/Web) › chronicle

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.chronicle.chronicle_home` | Biên niên sử | Nhật ký hành trình sinh từ DomainEvent | gamer, pilot | s2 |
| `game.chronicle.chronicle_chapter` | Chương biên niên | Tóm tắt từng chương đã chơi | gamer, pilot | s2 |
| `game.chronicle.chronicle_bard` | Người kể chuyện | Bard viết lại hành trình thành truyện (AI, có kiểm duyệt) | gamer, pilot | s2 |
| `game.chronicle.chronicle_export` | Xuất biên niên | Xuất thành sách ảnh/PDF | gamer, pilot | s2 |
| `game.chronicle.rewind_prompt` | Tua ngược lượt | Dùng Cát Thời Gian quay lại 1 lượt (replay tất định) | gamer, pilot | s2 |
| `game.chronicle.battle_timeline_branch` | Nhánh dòng thời gian | Xem các nhánh đã thử trong một trận | gamer, pilot | s2 |
| `game.chronicle.what_if_sandbox` | Giả định "Nếu như" | Rẽ nhánh từ một lượt bất kỳ trong replay | gamer, pilot | s2 |
| `game.chronicle.ghost_compare` | So với bóng ma | Chạy song song lời giải của người khác | gamer, pilot | s2 |
| `game.chronicle.time_sand_wallet` | Cát Thời Gian | Nguồn và số dư tài nguyên tua ngược | gamer, pilot | s2 |
| `game.chronicle.chronicle_milestones` | Cột mốc | Khoảnh khắc đáng nhớ được đánh dấu tự động | gamer, pilot | s2 |

### Game client (Desktop/Android/iOS/Web) › theater

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.theater.theater_home` | Nhà hát replay | Highlight tự động từ BattleEvent | gamer, pilot | s2 |
| `game.theater.highlight_reel` | Cuộn highlight | Clip chí mạng, lật kèo, one-shot | gamer, pilot | s2 |
| `game.theater.highlight_editor` | Sửa highlight | Cắt, tốc độ, camera | gamer, pilot | s2 |
| `game.theater.highlight_share` | Chia sẻ highlight | Xuất GIF/MP4 hoặc mã replay | gamer, pilot | s2 |
| `game.theater.theater_featured` | Replay nổi bật | Tuyển chọn cộng đồng | gamer, pilot | s2 |
| `game.theater.theater_commentary` | Bình luận replay | Bình luận theo mốc thời gian | gamer, pilot | s2 |
| `game.theater.director_camera` | Camera đạo diễn | Góc máy tự do khi phát lại | gamer, pilot | s2 |
| `game.theater.theater_playlist` | Danh sách phát | Gom replay theo chủ đề | gamer, pilot | s2 |

### Game client (Desktop/Android/iOS/Web) › world_voice

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.world_voice.story_vote_home` | Bỏ phiếu cốt truyện | Toàn server chọn hướng đi sự kiện mùa | gamer, pilot | s2 |
| `game.world_voice.story_vote_detail` | Chi tiết lựa chọn | Hệ quả dự kiến của từng phương án | gamer, pilot | s2 |
| `game.world_voice.story_vote_result` | Kết quả bỏ phiếu | Tỉ lệ và cutscene kết quả | gamer, pilot | s2 |
| `game.world_voice.world_goal_home` | Mục tiêu thế giới | Cả server cùng góp tài nguyên xây cầu, đẩy lùi bóng tối | gamer, pilot | s2 |
| `game.world_voice.world_goal_contribute` | Đóng góp mục tiêu | Góp vật phẩm/trận thắng | gamer, pilot | s2 |
| `game.world_voice.world_goal_rewards` | Thưởng mục tiêu thế giới | Mốc thưởng chung | gamer, pilot | s2 |
| `game.world_voice.world_state_map` | Bản đồ trạng thái thế giới | Vùng bị bóng tối lan/đẩy lùi theo hoạt động toàn server | gamer, pilot | s2 |
| `game.world_voice.echo_glyph_place` | Khắc dấu ấn | Để lại ký hiệu gợi ý cho người chơi khác (từ vựng giới hạn) | gamer, pilot | s2 |
| `game.world_voice.echo_glyph_read` | Đọc dấu ấn | Xem, đánh giá dấu ấn | gamer, pilot | s2 |
| `game.world_voice.echo_ghost_path` | Vệt bóng ma | Thấy đường đi của người chơi khác trên map | gamer, pilot | s2 |

### Game client (Desktop/Android/iOS/Web) › rift

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.rift.rift_home` | Vết Nứt | Hầm ngục sinh thủ tục theo seed hằng ngày | gamer, pilot | s3 |
| `game.rift.rift_seed_select` | Chọn seed | Seed ngày, seed tuần, seed tùy chỉnh | gamer, pilot | s3 |
| `game.rift.rift_floor_map` | Bản đồ tầng | Phòng sinh từ tileset + luật ghép | gamer, pilot | s3 |
| `game.rift.rift_room_event` | Phòng sự kiện | Sự kiện ngẫu nhiên tất định | gamer, pilot | s3 |
| `game.rift.rift_modifier_select` | Chọn biến đổi | Chấp nhận biến đổi khó để tăng thưởng | gamer, pilot | s3 |
| `game.rift.rift_treasure` | Kho báu Vết Nứt | Chọn 1 trong 3 phần thưởng | gamer, pilot | s3 |
| `game.rift.rift_merchant` | Thương nhân lạc lối | Mua bằng mảnh vỡ tạm thời | gamer, pilot | s3 |
| `game.rift.rift_boss_gate` | Cổng boss | Boss ghép từ pha ngẫu nhiên | gamer, pilot | s3 |
| `game.rift.rift_summary` | Tổng kết Vết Nứt | Điểm, tầng, seed để chia sẻ | gamer, pilot | s3 |
| `game.rift.rift_leaderboard` | Xếp hạng Vết Nứt | Theo seed | gamer, pilot | s3 |

### Game client (Desktop/Android/iOS/Web) › dreams

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.dreams.dream_gate` | Cổng giấc mơ | Mỗi anh hùng có một hầm ngục ký ức | gamer, pilot | s3 |
| `game.dreams.dream_map` | Bản đồ giấc mơ | Map đảo màu/đảo chiều từ map gốc | gamer, pilot | s3 |
| `game.dreams.dream_memory` | Mảnh ký ức | Cảnh truyện mở khóa | gamer, pilot | s3 |
| `game.dreams.dream_nightmare` | Ác mộng | Phiên bản khó của boss từng đánh | gamer, pilot | s3 |
| `game.dreams.dream_reward` | Thưởng giấc mơ | Nội tại riêng của anh hùng | gamer, pilot | s3 |
| `game.dreams.dream_collection` | Bộ sưu tập ký ức | Ký ức đã thu thập | gamer, pilot | s3 |
| `game.dreams.dream_lucid` | Giấc mơ tỉnh | Người chơi tự chọn luật trận | gamer, pilot | s3 |
| `game.dreams.dream_wake` | Tỉnh giấc | Tổng kết và quay về | gamer, pilot | s3 |

### Game client (Desktop/Android/iOS/Web) › soul_cards

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.soul_cards.cards_home` | Thẻ Linh Hồn | Chế độ đấu thẻ chạy trên cùng BattleEngine | gamer, pilot | s3 |
| `game.soul_cards.card_collection` | Bộ sưu tập thẻ | Thẻ rơi từ trận và nghiên cứu | gamer, pilot | s3 |
| `game.soul_cards.card_detail` | Chi tiết thẻ | Hiệu ứng, từ khóa | gamer, pilot | s3 |
| `game.soul_cards.deck_list` | Danh sách bộ bài | Bộ đã lưu | gamer, pilot | s3 |
| `game.soul_cards.deck_builder` | Xây bộ bài | 30 thẻ, giới hạn theo lớp | gamer, pilot | s3 |
| `game.soul_cards.deck_stats` | Thống kê bộ bài | Đường cong năng lượng, tỉ lệ thắng | gamer, pilot | s3 |
| `game.soul_cards.card_battle` | Trận đấu thẻ | Rút thẻ thay cho chọn kỹ năng | gamer, pilot | s3 |
| `game.soul_cards.card_mulligan` | Đổi bài đầu | Chọn giữ/đổi bài | gamer, pilot | s3 |
| `game.soul_cards.card_draft` | Draft | Chọn thẻ luân phiên xây bộ tạm | gamer, pilot | s3 |
| `game.soul_cards.card_ladder` | Bậc xếp hạng thẻ | PvP bất đồng bộ | gamer, pilot | s3 |
| `game.soul_cards.card_craft` | Chế thẻ | Dùng bụi linh hồn | gamer, pilot | s3 |
| `game.soul_cards.card_puzzles` | Câu đố thẻ | Câu đố riêng cho chế độ thẻ | gamer, pilot | s3 |

### Game client (Desktop/Android/iOS/Web) › challenges

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.challenges.boss_rush_home` | Boss liên hoàn | Đánh liên tiếp boss đã gặp | gamer, pilot | s3 |
| `game.challenges.boss_rush_result` | Kết quả boss liên hoàn | Thời gian, số lượt | gamer, pilot | s3 |
| `game.challenges.weekly_modifiers` | Luật tuần | Luật biến đổi áp dụng mọi trận trong tuần | gamer, pilot | s3 |
| `game.challenges.speedrun_home` | Speedrun | Hạng mục speedrun chính thức | gamer, pilot | s3 |
| `game.challenges.speedrun_timer` | Đồng hồ speedrun | Split theo chương, xác minh bằng replay | gamer, pilot | s3 |
| `game.challenges.speedrun_board` | Bảng speedrun | Top theo hạng mục | gamer, pilot | s3 |
| `game.challenges.ironman_start` | Chế độ Thiết Nhân | Chết là mất anh hùng vĩnh viễn | gamer, pilot | s3 |
| `game.challenges.ironman_memorial` | Đài tưởng niệm | Anh hùng đã ngã | gamer, pilot | s3 |
| `game.challenges.ironman_leaderboard` | Xếp hạng Thiết Nhân | Tiến độ xa nhất | gamer, pilot | s3 |
| `game.challenges.challenge_badges` | Huy hiệu thử thách | Huy hiệu hoàn thành | gamer, pilot | s3 |

### Game client (Desktop/Android/iOS/Web) › act_two

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.act_two.act_two_prologue` | Mở đầu Hồi II | Thế giới bên kia bình minh | gamer, pilot | s4 |
| `game.act_two.dual_world_toggle` | Chuyển thế giới | Đổi giữa Bình Minh và Hoàng Hôn trên cùng map | gamer, pilot | s4 |
| `game.act_two.dual_world_diff` | Khác biệt hai thế giới | Vật cản/lối đi chỉ có ở một phía | gamer, pilot | s4 |
| `game.act_two.mirror_npc` | NPC phản chiếu | Phiên bản ngược của NPC cũ | gamer, pilot | s4 |
| `game.act_two.act_two_region_map` | Bản đồ Hồi II | 6 vùng mới | gamer, pilot | s4 |
| `game.act_two.artifact_shards` | Mảnh cổ vật | 12 mảnh (6 Hồi I + 6 Hồi II) | gamer, pilot | s4 |
| `game.act_two.artifact_forge` | Rèn cổ vật | Ghép mảnh mở quyền năng toàn đội | gamer, pilot | s4 |
| `game.act_two.relic_list` | Di vật | Di vật trang bị cho đội | gamer, pilot | s4 |
| `game.act_two.relic_detail` | Chi tiết di vật | Hiệu ứng, lore | gamer, pilot | s4 |
| `game.act_two.relic_upgrade` | Nâng di vật | Nâng cấp | gamer, pilot | s4 |
| `game.act_two.ending_gallery` | Thư viện kết thúc | 6 kết thúc đã mở | gamer, pilot | s4 |
| `game.act_two.true_ending_path` | Con đường kết thúc thật | Điều kiện kết thúc thứ 6 | gamer, pilot | s4 |

### Game client (Desktop/Android/iOS/Web) › caravan

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.caravan.caravan_home` | Đoàn buôn | Tuyến buôn giữa các vùng | gamer, pilot | s4 |
| `game.caravan.caravan_routes` | Tuyến đường | Tuyến đã mở, rủi ro, lợi nhuận | gamer, pilot | s4 |
| `game.caravan.caravan_load` | Chất hàng | Chọn hàng theo sức chứa | gamer, pilot | s4 |
| `game.caravan.caravan_escort` | Hộ tống | Chọn đội hộ tống (trận phục kích) | gamer, pilot | s4 |
| `game.caravan.caravan_travel` | Hành trình | Tiến độ thời gian thực | gamer, pilot | s4 |
| `game.caravan.caravan_ambush` | Phục kích | Trận khi bị chặn đường | gamer, pilot | s4 |
| `game.caravan.caravan_market_prices` | Giá vùng | Giá thay đổi theo cung cầu toàn server | gamer, pilot | s4 |
| `game.caravan.caravan_ledger` | Sổ buôn | Lãi/lỗ từng chuyến | gamer, pilot | s4 |
| `game.caravan.caravan_upgrade` | Nâng đoàn buôn | Xe, ngựa, bảo vệ | gamer, pilot | s4 |

### Game client (Desktop/Android/iOS/Web) › professions

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.professions.profession_hub` | Nghề nghiệp | Rèn, may, luyện kim, khắc ấn, nấu ăn, trồng trọt | gamer, pilot | s4 |
| `game.professions.profession_detail` | Chi tiết nghề | Cấp nghề, công thức mở | gamer, pilot | s4 |
| `game.professions.profession_workbench` | Bàn làm việc | Chế tạo theo nghề | gamer, pilot | s4 |
| `game.professions.profession_orders` | Đơn đặt hàng | NPC/người chơi đặt hàng | gamer, pilot | s4 |
| `game.professions.profession_mastery` | Tinh thông | Chọn chuyên môn | gamer, pilot | s4 |
| `game.professions.farm_plots` | Ruộng vườn | Trồng nguyên liệu theo mùa | gamer, pilot | s4 |
| `game.professions.farm_harvest` | Thu hoạch | Kết quả thu hoạch | gamer, pilot | s4 |
| `game.professions.alchemy_lab` | Phòng luyện kim | Pha thuốc thử nghiệm công thức ẩn | gamer, pilot | s4 |

### Game client (Desktop/Android/iOS/Web) › raids

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.raids.raid_home` | Đột kích | Raid 4 đội × 9 anh hùng, bất đồng bộ | gamer, pilot | s4 |
| `game.raids.raid_boss_intel` | Tình báo boss | Cơ chế từng pha | gamer, pilot | s4 |
| `game.raids.raid_squad_assign` | Phân đội | Gán anh hùng cho 4 đội | gamer, pilot | s4 |
| `game.raids.raid_attempt` | Đánh raid | Mỗi đội một lượt tấn công | gamer, pilot | s4 |
| `game.raids.raid_damage_board` | Bảng sát thương | Đóng góp của từng thành viên | gamer, pilot | s4 |
| `game.raids.raid_loot_council` | Chia chiến lợi phẩm | Bỏ phiếu chia đồ | gamer, pilot | s4 |
| `game.raids.raid_history` | Lịch sử raid | Các lần đánh | gamer, pilot | s4 |

### Game client (Desktop/Android/iOS/Web) › guild_hall

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.guild_hall.guild_hall` | Sảnh bang | Nhà chung trang trí được | gamer | s4 |
| `game.guild_hall.guild_hall_decorate` | Trang trí sảnh | Đặt vật phẩm | gamer | s4 |
| `game.guild_hall.guild_trophy_room` | Phòng cúp | Cúp raid, giải đấu | gamer | s4 |
| `game.guild_hall.guild_research` | Nghiên cứu bang | Buff chung | gamer | s4 |
| `game.guild_hall.guild_banner_editor` | Thiết kế cờ bang | Ghép biểu tượng/màu | gamer | s4 |
| `game.guild_hall.guild_events` | Sự kiện bang | Lịch hoạt động nội bộ | gamer | s4 |

### Game client (Desktop/Android/iOS/Web) › mounts_travel

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.mounts_travel.mount_stable` | Chuồng thú cưỡi | Thú cưỡi sở hữu | gamer, pilot | s4 |
| `game.mounts_travel.mount_detail` | Chi tiết thú cưỡi | Tốc độ, địa hình | gamer, pilot | s4 |
| `game.mounts_travel.mount_train` | Huấn luyện thú cưỡi | Tăng chỉ số | gamer, pilot | s4 |
| `game.mounts_travel.mount_race` | Đua thú cưỡi | Minigame đua | gamer, pilot | s4 |
| `game.mounts_travel.airship_dock` | Bến khí cầu | Di chuyển giữa Hồi I và Hồi II | gamer, pilot | s4 |
| `game.mounts_travel.travel_journal` | Nhật ký lữ hành | Quãng đường, nơi đã qua | gamer, pilot | s4 |

### Game client (Desktop/Android/iOS/Web) › mentorship

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.mentorship.mentor_home` | Sư phụ & đệ tử | Kết nối người chơi lâu năm với người mới | gamer | s4 |
| `game.mentorship.mentor_find` | Tìm sư phụ | Ghép theo giờ chơi, ngôn ngữ | gamer | s4 |
| `game.mentorship.mentor_tasks` | Nhiệm vụ thầy trò | Nhiệm vụ làm chung | gamer | s4 |
| `game.mentorship.mentor_graduation` | Xuất sư | Thưởng cho cả hai | gamer | s4 |
| `game.mentorship.mentor_rating` | Đánh giá sư phụ | Phản hồi chất lượng | gamer | s4 |
| `game.mentorship.mentor_hall` | Sảnh sư phụ | Sư phụ tiêu biểu | gamer | s4 |

### Game client (Desktop/Android/iOS/Web) › npc_life

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.npc_life.npc_schedule` | Lịch sinh hoạt NPC | NPC đi lại theo giờ trong game | gamer, pilot | s4 |
| `game.npc_life.npc_relationship` | Quan hệ NPC | Mức thân thiết với NPC | gamer, pilot | s4 |
| `game.npc_life.npc_gift` | Tặng quà NPC | Sở thích từng NPC | gamer, pilot | s4 |
| `game.npc_life.npc_story` | Truyện NPC | Chuỗi truyện riêng | gamer, pilot | s4 |
| `game.npc_life.bounty_board` | Bảng truy nã | Nhiệm vụ săn quái do NPC treo | gamer, pilot | s4 |
| `game.npc_life.bounty_detail` | Chi tiết truy nã | Mục tiêu, vị trí gợi ý | gamer, pilot | s4 |
| `game.npc_life.bounty_turn_in` | Nộp truy nã | Nhận thưởng | gamer, pilot | s4 |
| `game.npc_life.town_gossip` | Tin đồn | Gợi ý bí mật từ NPC | gamer, pilot | s4 |

### Game client (Desktop/Android/iOS/Web) › cross_play

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.cross_play.cross_progress_link` | Liên kết tiến trình | Chơi tiếp trên thiết bị khác | gamer, user | s4 |
| `game.cross_play.cross_device_list` | Thiết bị liên kết | Quản lý thiết bị | gamer, user | s4 |
| `game.cross_play.cross_handoff` | Chuyển thiết bị | Mã QR tiếp tục trên máy khác | gamer, user | s4 |
| `game.cross_play.cross_play_settings` | Cài đặt chơi chéo | Ghép trận chéo nền tảng | gamer, user | s4 |
| `game.cross_play.cloud_save_history` | Lịch sử cloud save | Khôi phục bản cũ | gamer, user | s4 |

### Game client (Desktop/Android/iOS/Web) › museum

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.museum.hero_museum` | Bảo tàng anh hùng | Trưng bày anh hùng + trang phục | gamer, pilot | s1 |
| `game.museum.diorama_editor` | Dựng diorama | Sắp đặt cảnh với nhân vật, đạo cụ | gamer, pilot | s1 |
| `game.museum.diorama_gallery` | Triển lãm diorama | Diorama của cộng đồng | gamer, pilot | s1 |
| `game.museum.diorama_like` | Bình chọn diorama | Thích, lưu | gamer, pilot | s1 |
| `game.museum.museum_visitors` | Khách tham quan | Ai đã xem bảo tàng | gamer, pilot | s1 |
| `game.museum.treasure_hunt` | Truy tìm kho báu | Câu đố bản đồ theo mảnh giấy | gamer, pilot | s1 |
| `game.museum.treasure_map_piece` | Mảnh bản đồ kho báu | Ghép mảnh | gamer, pilot | s1 |
| `game.museum.treasure_dig` | Đào kho báu | Kết quả | gamer, pilot | s1 |
| `game.museum.lore_quiz` | Đố vui truyền thuyết | Câu hỏi về thế giới | gamer, pilot | s1 |

### Game client (Desktop/Android/iOS/Web) › stream_interact

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.stream_interact.stream_link` | Liên kết kênh stream | Kết nối Twitch/YouTube | gamer | s2 |
| `game.stream_interact.stream_mode` | Chế độ streamer | Ẩn thông tin cá nhân, overlay an toàn | gamer | s2 |
| `game.stream_interact.viewer_vote_live` | Khán giả bỏ phiếu | Khán giả chọn kỹ năng/nhánh | gamer | s2 |
| `game.stream_interact.viewer_gift_receive` | Quà khán giả | Nhận buff vui do khán giả tặng | gamer | s2 |
| `game.stream_interact.stream_recap` | Tổng kết buổi stream | Highlight, lượt tương tác | gamer | s2 |

### Companion App (PWA mobile) › companion

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `companion.companion.companion_login` | Đăng nhập companion | Dùng tài khoản game | gamer, user, pilot | s1 |
| `companion.companion.companion_home` | Trang chủ companion | Tình trạng làng, năng lượng, thám hiểm | gamer, user, pilot | s1 |
| `companion.companion.companion_notifications` | Thông báo | Thám hiểm xong, năng lượng đầy | gamer, user, pilot | s1 |
| `companion.companion.companion_expeditions` | Thám hiểm | Gửi/nhận thám hiểm từ điện thoại | gamer, user, pilot | s1 |
| `companion.companion.companion_village` | Làng | Thu hoạch tài nguyên | gamer, user, pilot | s1 |
| `companion.companion.companion_farm` | Ruộng vườn | Tưới, thu hoạch | gamer, user, pilot | s1 |
| `companion.companion.companion_caravan` | Đoàn buôn | Theo dõi chuyến buôn | gamer, user, pilot | s1 |
| `companion.companion.companion_mail` | Hộp thư | Nhận quà | gamer, user, pilot | s1 |
| `companion.companion.companion_chat_guild` | Chat bang | Chat khi không vào game | gamer, user, pilot | s1 |
| `companion.companion.companion_chat_private` | Chat riêng | Tin nhắn bạn bè | gamer, user, pilot | s1 |
| `companion.companion.companion_friends` | Bạn bè | Danh sách, lời mời | gamer, user, pilot | s1 |
| `companion.companion.companion_guild` | Bang hội | Thông báo bang, điểm danh bang | gamer, user, pilot | s1 |
| `companion.companion.companion_raid_plan` | Kế hoạch raid | Phân đội raid trên điện thoại | gamer, user, pilot | s1 |
| `companion.companion.companion_heroes` | Anh hùng | Xem chỉ số, trang bị | gamer, user, pilot | s1 |
| `companion.companion.companion_hero_detail` | Chi tiết anh hùng | Build hiện tại | gamer, user, pilot | s1 |
| `companion.companion.companion_lineup_planner` | Lên đội hình | Soạn đội hình, đồng bộ vào game | gamer, user, pilot | s1 |
| `companion.companion.companion_build_planner` | Lên build | Thiên phú, trang bị giả định | gamer, user, pilot | s1 |
| `companion.companion.companion_codex` | Bách khoa | Tra cứu offline | gamer, user, pilot | s1 |
| `companion.companion.companion_map` | Bản đồ thế giới | Bản đồ tương tác, đánh dấu | gamer, user, pilot | s1 |
| `companion.companion.companion_events` | Sự kiện | Lịch sự kiện, nhắc nhở | gamer, user, pilot | s1 |
| `companion.companion.companion_festival` | Lễ hội | Hoạt động lễ hội ngoài game | gamer, user, pilot | s1 |
| `companion.companion.companion_checkin` | Điểm danh | Điểm danh từ điện thoại | gamer, user, pilot | s1 |
| `companion.companion.companion_shop` | Cửa hàng | Mua gói (web payment nơi được phép) | gamer, user, pilot | s1 |
| `companion.companion.companion_market` | Chợ | Theo dõi giá, đặt lệnh | gamer, user, pilot | s1 |
| `companion.companion.companion_leaderboards` | Xếp hạng | Các bảng xếp hạng | gamer, user, pilot | s1 |
| `companion.companion.companion_replays` | Replay | Xem replay trên điện thoại | gamer, user, pilot | s1 |
| `companion.companion.companion_highlights` | Highlight | Clip của tôi | gamer, user, pilot | s1 |
| `companion.companion.companion_puzzle_daily` | Câu đố ngày | Giải câu đố ngày trên điện thoại | gamer, user, pilot | s1 |
| `companion.companion.companion_cards_deck` | Bộ bài thẻ | Sửa bộ bài | gamer, user, pilot | s1 |
| `companion.companion.companion_research` | Nghiên cứu | Tiến độ bestiary | gamer, user, pilot | s1 |
| `companion.companion.companion_chronicle` | Biên niên sử | Đọc biên niên | gamer, user, pilot | s1 |
| `companion.companion.companion_account` | Tài khoản | Bảo mật, thiết bị | gamer, user, pilot | s1 |
| `companion.companion.companion_parental` | Phụ huynh | Giới hạn thời gian/chi tiêu | gamer, user, pilot | s1 |
| `companion.companion.companion_support` | Hỗ trợ | Ticket | gamer, user, pilot | s1 |
| `companion.companion.companion_settings` | Cài đặt | Thông báo, ngôn ngữ | gamer, user, pilot | s1 |
| `companion.companion.companion_widgets` | Widget | Cấu hình widget màn hình chính | gamer, user, pilot | s1 |
| `companion.companion.companion_qr_login` | Đăng nhập bằng QR | Quét để đăng nhập máy tính | gamer, user, pilot | s1 |
| `companion.companion.companion_2fa` | Xác thực 2 lớp | Duyệt đăng nhập mới | gamer, user, pilot | s1 |
| `companion.companion.companion_offline` | Ngoại tuyến | Dữ liệu cache | gamer, user, pilot | s1 |
| `companion.companion.companion_onboarding` | Giới thiệu companion | Hướng dẫn lần đầu | gamer, user, pilot | s1 |

### Stream Suite (streamer dashboard + viewer extension) › streamer

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `stream.streamer.streamer_home` | Bảng điều khiển streamer | Trạng thái kết nối, khán giả | gamer, creator | s2 |
| `stream.streamer.streamer_connect` | Kết nối nền tảng | Twitch/YouTube OAuth | gamer, creator | s2 |
| `stream.streamer.streamer_overlay_builder` | Tạo overlay | Kéo thả widget overlay | gamer, creator | s2 |
| `stream.streamer.streamer_overlay_preview` | Xem trước overlay | Mô phỏng trên nền game | gamer, creator | s2 |
| `stream.streamer.streamer_vote_config` | Cấu hình bỏ phiếu | Loại phiếu khán giả được phép | gamer, creator | s2 |
| `stream.streamer.streamer_vote_live` | Phiếu đang chạy | Theo dõi phiếu | gamer, creator | s2 |
| `stream.streamer.streamer_gift_rules` | Luật quà khán giả | Buff nào được bật | gamer, creator | s2 |
| `stream.streamer.streamer_safety` | An toàn | Chặn spoiler, lọc từ | gamer, creator | s2 |
| `stream.streamer.streamer_highlights` | Highlight buổi stream | Clip tự động | gamer, creator | s2 |
| `stream.streamer.streamer_analytics` | Phân tích kênh | Tương tác theo buổi | gamer, creator | s2 |
| `stream.streamer.streamer_drops` | Drops | Phần thưởng khán giả xem stream | gamer, creator | s2 |
| `stream.streamer.streamer_drops_campaign` | Chiến dịch drops | Điều kiện nhận | gamer, creator | s2 |
| `stream.streamer.streamer_schedule` | Lịch stream | Đồng bộ lịch sự kiện game | gamer, creator | s2 |
| `stream.streamer.streamer_challenges` | Thử thách cho streamer | Thử thách do cộng đồng đặt | gamer, creator | s2 |
| `stream.streamer.streamer_coop_invite` | Mời khán giả co-op | Khán giả vào đội | gamer, creator | s2 |
| `stream.streamer.streamer_settings` | Cài đặt streamer | Độ trễ, quyền | gamer, creator | s2 |

### Stream Suite (streamer dashboard + viewer extension) › viewer

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `stream.viewer.viewer_panel` | Bảng khán giả | Extension trong trang stream | gamer | s2 |
| `stream.viewer.viewer_vote` | Bỏ phiếu | Chọn kỹ năng/nhánh | gamer | s2 |
| `stream.viewer.viewer_predictions` | Dự đoán | Đoán kết quả trận | gamer | s2 |
| `stream.viewer.viewer_hero_inspect` | Soi anh hùng | Xem build của streamer | gamer | s2 |
| `stream.viewer.viewer_gift` | Tặng buff | Dùng điểm kênh tặng buff vui | gamer | s2 |
| `stream.viewer.viewer_leaderboard` | Xếp hạng khán giả | Khán giả tích cực | gamer | s2 |
| `stream.viewer.viewer_drops_progress` | Tiến độ drops | Thời gian xem | gamer | s2 |
| `stream.viewer.viewer_link_account` | Liên kết tài khoản game | Nhận drops vào game | gamer | s2 |
| `stream.viewer.viewer_replay_moment` | Xem lại khoảnh khắc | Tua lại lượt vừa xảy ra | gamer | s2 |
| `stream.viewer.viewer_settings` | Cài đặt khán giả | Ẩn/hiện panel | gamer | s2 |

### Esports Observer (caster/observer) › casting

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `observer.casting.observer_lobby` | Sảnh quan sát | Trận giải đấu đang diễn ra | admin, gamer | s2 |
| `observer.casting.observer_match` | Quan sát trận | Góc nhìn toàn cảnh, không trễ với caster | admin, gamer | s2 |
| `observer.casting.observer_stats_overlay` | Overlay thống kê | Sát thương, năng lượng, lực chiến | admin, gamer | s2 |
| `observer.casting.observer_predictions` | Dự đoán thắng | Mô phỏng Monte Carlo từ trạng thái hiện tại | admin, gamer | s2 |
| `observer.casting.observer_timeline` | Dòng thời gian trận | Đánh dấu sự kiện chính | admin, gamer | s2 |
| `observer.casting.observer_instant_replay` | Replay tức thì | Phát lại lượt quan trọng | admin, gamer | s2 |
| `observer.casting.observer_camera_director` | Đạo diễn camera | Chuyển góc nhìn cho luồng phát | admin, gamer | s2 |
| `observer.casting.observer_caster_notes` | Ghi chú caster | Dữ liệu đối đầu, lịch sử | admin, gamer | s2 |
| `observer.casting.observer_head_to_head` | Đối đầu | Thành tích hai đội | admin, gamer | s2 |
| `observer.casting.observer_draft_view` | Xem cấm/chọn | Pha cấm chọn anh hùng | admin, gamer | s2 |
| `observer.casting.observer_bracket_overlay` | Overlay nhánh đấu | Hiển thị nhánh | admin, gamer | s2 |
| `observer.casting.observer_break_screen` | Màn giải lao | Quảng cáo, thống kê | admin, gamer | s2 |
| `observer.casting.observer_mvp` | MVP | Chọn và hiển thị MVP | admin, gamer | s2 |
| `observer.casting.observer_broadcast_settings` | Cài đặt phát sóng | Độ phân giải, trễ | admin, gamer | s2 |
| `observer.casting.observer_spoiler_delay` | Trễ chống lộ | Trễ luồng công khai | admin, gamer | s2 |
| `observer.casting.observer_multi_view` | Đa màn | Xem nhiều trận cùng lúc | admin, gamer | s2 |
| `observer.casting.observer_graphics_package` | Gói đồ họa | Chủ đề giải đấu | admin, gamer | s2 |
| `observer.casting.observer_sponsor_slots` | Vị trí nhà tài trợ | Logo, thời lượng | admin, gamer | s2 |
| `observer.casting.observer_referee` | Trọng tài | Tạm dừng, xử tranh chấp | admin, gamer | s2 |
| `observer.casting.observer_match_report` | Biên bản trận | Xuất biên bản | admin, gamer | s2 |

### Workshop (mod + creator marketplace) › mods

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `workshop.mods.workshop_home` | Workshop | Mod dữ liệu và level cộng đồng | creator, gamer | s3 |
| `workshop.mods.mod_browse` | Duyệt mod | Lọc theo loại, phiên bản | creator, gamer | s3 |
| `workshop.mods.mod_detail` | Chi tiết mod | Mô tả, ảnh, tương thích | creator, gamer | s3 |
| `workshop.mods.mod_install` | Cài mod | Tải content pack đã ký | creator, gamer | s3 |
| `workshop.mods.mod_manager` | Quản lý mod | Bật/tắt, thứ tự ưu tiên | creator, gamer | s3 |
| `workshop.mods.mod_conflicts` | Xung đột mod | Mod ghi đè cùng ID | creator, gamer | s3 |
| `workshop.mods.mod_reviews` | Đánh giá mod | Nhận xét cộng đồng | creator, gamer | s3 |
| `workshop.mods.mod_report` | Báo cáo mod | Vi phạm | creator, gamer | s3 |
| `workshop.mods.mod_collections` | Bộ sưu tập mod | Gói nhiều mod | creator, gamer | s3 |
| `workshop.mods.mod_changelog` | Nhật ký mod | Các phiên bản | creator, gamer | s3 |
| `workshop.mods.creator_dashboard` | Bảng điều khiển creator | Lượt tải, đánh giá, thu nhập | creator, gamer | s3 |
| `workshop.mods.mod_upload` | Tải lên mod | Content pack + metadata | creator, gamer | s3 |
| `workshop.mods.mod_validation` | Kiểm tra mod | Chạy content-compiler + quét an ninh | creator, gamer | s3 |
| `workshop.mods.mod_versions` | Phiên bản mod | Quản lý phát hành | creator, gamer | s3 |
| `workshop.mods.mod_pricing` | Định giá | Miễn phí hoặc trả bằng creator token | creator, gamer | s3 |
| `workshop.mods.creator_earnings` | Thu nhập creator | Doanh thu chia sẻ | creator, gamer | s3 |
| `workshop.mods.creator_payout` | Rút tiền | Yêu cầu thanh toán | creator, gamer | s3 |
| `workshop.mods.creator_tax_info` | Thông tin thuế | Hồ sơ thuế | creator, gamer | s3 |
| `workshop.mods.creator_guidelines` | Quy tắc creator | Nội dung được phép | creator, gamer | s3 |
| `workshop.mods.creator_sdk_docs` | Tài liệu SDK mod | Schema, ví dụ | creator, gamer | s3 |
| `workshop.mods.mod_template_gallery` | Mẫu mod | Mẫu khởi đầu | creator, gamer | s3 |
| `workshop.mods.mod_playtest` | Chơi thử mod | Chạy mod trong sandbox | creator, gamer | s3 |
| `workshop.mods.mod_analytics` | Phân tích mod | Tỉ lệ hoàn thành, gỡ cài | creator, gamer | s3 |
| `workshop.mods.creator_followers` | Người theo dõi | Theo dõi creator | creator, gamer | s3 |
| `workshop.mods.creator_jams` | Game jam | Cuộc thi làm mod theo chủ đề | creator, gamer | s3 |
| `workshop.mods.creator_jam_detail` | Chi tiết jam | Luật, giải thưởng | creator, gamer | s3 |
| `workshop.mods.creator_jam_submit` | Nộp bài jam | Nộp mod dự thi | creator, gamer | s3 |
| `workshop.mods.creator_jam_results` | Kết quả jam | Xếp hạng | creator, gamer | s3 |

### Community Translation Portal › community_translation

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `locportal.community_translation.loc_home` | Cổng dịch cộng đồng | Ngôn ngữ đang cần người dịch | creator, gamer | s4 |
| `locportal.community_translation.loc_join` | Tham gia dịch | Chọn ngôn ngữ, bài test | creator, gamer | s4 |
| `locportal.community_translation.loc_placement_test` | Bài kiểm tra dịch | Đánh giá trình độ | creator, gamer | s4 |
| `locportal.community_translation.loc_workspace` | Không gian dịch | Dịch theo key, có ảnh ngữ cảnh | creator, gamer | s4 |
| `locportal.community_translation.loc_context_viewer` | Ngữ cảnh | Ảnh chụp ScreenId chứa key | creator, gamer | s4 |
| `locportal.community_translation.loc_suggestions` | Đề xuất bản dịch | Bình chọn bản dịch tốt nhất | creator, gamer | s4 |
| `locportal.community_translation.loc_glossary` | Thuật ngữ | Thuật ngữ bắt buộc | creator, gamer | s4 |
| `locportal.community_translation.loc_discussions` | Thảo luận | Thảo luận theo key | creator, gamer | s4 |
| `locportal.community_translation.loc_progress` | Tiến độ ngôn ngữ | % hoàn thành | creator, gamer | s4 |
| `locportal.community_translation.loc_reviewer_queue` | Hàng đợi duyệt | Người duyệt cộng đồng | creator, gamer | s4 |
| `locportal.community_translation.loc_contributors` | Người đóng góp | Bảng ghi công | creator, gamer | s4 |
| `locportal.community_translation.loc_rewards` | Thưởng dịch giả | Quà trong game | creator, gamer | s4 |
| `locportal.community_translation.loc_style_guide` | Hướng dẫn văn phong | Theo từng ngôn ngữ | creator, gamer | s4 |
| `locportal.community_translation.loc_ingame_preview` | Xem trong game | Mở build với bản dịch nháp | creator, gamer | s4 |
| `locportal.community_translation.loc_report_issue` | Báo lỗi dịch | Báo từ người chơi | creator, gamer | s4 |
| `locportal.community_translation.loc_leaderboard` | Xếp hạng dịch giả | Đóng góp nhiều nhất | creator, gamer | s4 |
| `locportal.community_translation.loc_new_language` | Đề xuất ngôn ngữ mới | Bỏ phiếu ngôn ngữ | creator, gamer | s4 |
| `locportal.community_translation.loc_certificates` | Chứng nhận dịch giả | Ghi nhận | creator, gamer | s4 |

### Partner Portal (influencer/affiliate/creator program) › partners

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `partner.partners.partner_apply` | Đăng ký đối tác | Streamer, YouTuber, fansite | creator, admin | s4 |
| `partner.partners.partner_status` | Trạng thái hồ sơ | Duyệt/từ chối | creator, admin | s4 |
| `partner.partners.partner_home` | Trang chủ đối tác | Tổng quan quyền lợi | creator, admin | s4 |
| `partner.partners.partner_codes` | Mã giới thiệu | Mã creator hỗ trợ | creator, admin | s4 |
| `partner.partners.partner_code_stats` | Thống kê mã | Lượt dùng, doanh thu | creator, admin | s4 |
| `partner.partners.partner_assets` | Tài nguyên truyền thông | Ảnh, video, logo | creator, admin | s4 |
| `partner.partners.partner_early_access` | Truy cập sớm | Build/thông tin trước ra mắt | creator, admin | s4 |
| `partner.partners.partner_embargo` | Lịch cấm đăng | Thời điểm được công bố | creator, admin | s4 |
| `partner.partners.partner_campaigns` | Chiến dịch | Chiến dịch đang mở | creator, admin | s4 |
| `partner.partners.partner_campaign_detail` | Chi tiết chiến dịch | Yêu cầu, thù lao | creator, admin | s4 |
| `partner.partners.partner_deliverables` | Sản phẩm bàn giao | Nộp link nội dung | creator, admin | s4 |
| `partner.partners.partner_payouts` | Thanh toán đối tác | Lịch sử thanh toán | creator, admin | s4 |
| `partner.partners.partner_contract` | Hợp đồng | Ký điện tử | creator, admin | s4 |
| `partner.partners.partner_giveaways` | Giveaway | Mã quà cho cộng đồng | creator, admin | s4 |
| `partner.partners.partner_support` | Hỗ trợ đối tác | Liên hệ quản lý đối tác | creator, admin | s4 |
| `partner.partners.partner_analytics` | Phân tích đối tác | Hiệu quả nội dung | creator, admin | s4 |
| `partner.partners.partner_tiers` | Hạng đối tác | Điều kiện lên hạng | creator, admin | s4 |
| `partner.partners.partner_events` | Sự kiện đối tác | Gặp mặt, livestream chung | creator, admin | s4 |

### Live Game Master Console › live_gm

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `gm.live_gm.gm_home` | Bàn điều khiển GM | Tình trạng thế giới trực tiếp | admin | s2 |
| `gm.live_gm.gm_world_state` | Trạng thái thế giới | Điều chỉnh bóng tối lan/lùi | admin | s2 |
| `gm.live_gm.gm_spawn_world_event` | Tạo sự kiện thế giới | Boss bất ngờ, mưa sao băng | admin | s2 |
| `gm.live_gm.gm_event_preview` | Xem trước sự kiện | Mô phỏng tác động kinh tế | admin | s2 |
| `gm.live_gm.gm_live_announcement` | Thông báo trực tiếp | Hiển thị banner trong game | admin | s2 |
| `gm.live_gm.gm_npc_puppet` | Điều khiển NPC | NPC nói lời thoại trực tiếp trong lễ hội | admin | s2 |
| `gm.live_gm.gm_story_vote_control` | Điều khiển bỏ phiếu | Mở/đóng phiếu, công bố | admin | s2 |
| `gm.live_gm.gm_world_goal_control` | Điều khiển mục tiêu thế giới | Điều chỉnh ngưỡng | admin | s2 |
| `gm.live_gm.gm_weather_override` | Ghi đè thời tiết | Đặt thời tiết theo vùng | admin | s2 |
| `gm.live_gm.gm_festival_control` | Điều khiển lễ hội | Bật hoạt động lễ hội | admin | s2 |
| `gm.live_gm.gm_rift_seed_publish` | Phát seed Vết Nứt | Seed ngày/tuần đã kiểm tra | admin | s2 |
| `gm.live_gm.gm_puzzle_publish` | Phát câu đố ngày | Câu đố đã có lời giải xác minh | admin | s2 |
| `gm.live_gm.gm_raid_schedule` | Lịch raid | Mở/đóng raid | admin | s2 |
| `gm.live_gm.gm_compensation` | Bồi thường | Gửi quà bồi thường sau sự cố | admin | s2 |
| `gm.live_gm.gm_rollback_request` | Yêu cầu rollback | Rollback có phê duyệt 2 người | admin | s2 |
| `gm.live_gm.gm_live_metrics` | Chỉ số trực tiếp | Tác động của hành động GM | admin | s2 |
| `gm.live_gm.gm_action_log` | Nhật ký GM | Mọi hành động GM | admin | s2 |
| `gm.live_gm.gm_approval_queue` | Hàng đợi phê duyệt | Hành động cần người thứ hai | admin | s2 |
| `gm.live_gm.gm_playbooks` | Kịch bản vận hành | Quy trình chuẩn cho sự kiện | admin | s2 |
| `gm.live_gm.gm_safety_limits` | Giới hạn an toàn | Trần phần thưởng GM được phát | admin | s2 |
| `gm.live_gm.gm_shift_handover` | Bàn giao ca | Ghi chú giữa các ca | admin | s2 |
| `gm.live_gm.gm_chat_broadcast` | Phát chat | Tin nhắn hệ thống | admin | s2 |
| `gm.live_gm.gm_region_focus` | Tập trung vùng | Quan sát một vùng | admin | s2 |
| `gm.live_gm.gm_player_spotlight` | Tôn vinh người chơi | Đưa người chơi lên bảng vinh danh | admin | s2 |

### Web Console (admin/liveops/support/BI) › narrative_ops

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `console.narrative_ops.bard_policy` | Chính sách Bard | Giới hạn nội dung AI kể chuyện | admin, creator | s2 |
| `console.narrative_ops.bard_review_queue` | Duyệt nội dung Bard | Mẫu ngẫu nhiên + bị gắn cờ | admin, creator | s2 |
| `console.narrative_ops.bard_cost_dashboard` | Chi phí Bard | Token/chi phí mỗi ngày | admin, creator | s2 |
| `console.narrative_ops.bard_quality` | Chất lượng Bard | Điểm đánh giá của người chơi | admin, creator | s2 |
| `console.narrative_ops.bard_incidents` | Sự cố Bard | Nội dung vi phạm đã chặn | admin, creator | s2 |
| `console.narrative_ops.story_vote_ops` | Vận hành bỏ phiếu | Chống gian lận phiếu | admin, creator | s2 |
| `console.narrative_ops.world_goal_ops` | Vận hành mục tiêu thế giới | Tiến độ, điều chỉnh | admin, creator | s2 |
| `console.narrative_ops.echo_moderation` | Kiểm duyệt dấu ấn | Dấu ấn bị báo cáo | admin, creator | s2 |

### Web Console (admin/liveops/support/BI) › economy_bank

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `console.economy_bank.central_bank` | Ngân hàng trung ương | Tổng cung tiền tệ theo thời gian | admin | s4 |
| `console.economy_bank.money_supply` | Cung tiền | Vàng/ngọc tạo ra vs tiêu hủy | admin | s4 |
| `console.economy_bank.sink_designer` | Thiết kế điểm tiêu | Đề xuất sink khi lạm phát | admin | s4 |
| `console.economy_bank.market_regulation` | Điều tiết chợ | Biên độ giá, thuế giao dịch | admin | s4 |
| `console.economy_bank.market_manipulation` | Thao túng chợ | Phát hiện gom hàng | admin | s4 |
| `console.economy_bank.caravan_price_monitor` | Giá tuyến buôn | Giá vùng theo cung cầu | admin | s4 |
| `console.economy_bank.price_index` | Chỉ số giá | Rổ hàng chuẩn theo tuần | admin | s4 |
| `console.economy_bank.wealth_distribution` | Phân bố tài sản | Gini, top 1% | admin | s4 |
| `console.economy_bank.economy_forecast` | Dự báo kinh tế | Mô phỏng 90 ngày | admin | s4 |
| `console.economy_bank.economy_interventions` | Can thiệp | Lịch sử can thiệp và hiệu quả | admin | s4 |

### Web Console (admin/liveops/support/BI) › workshop_ops

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `console.workshop_ops.mod_queue` | Hàng đợi duyệt mod | Mod chờ duyệt | admin | s3 |
| `console.workshop_ops.mod_review` | Duyệt mod | Kết quả quét, chơi thử | admin | s3 |
| `console.workshop_ops.mod_takedowns` | Gỡ mod | Yêu cầu gỡ bản quyền | admin | s3 |
| `console.workshop_ops.creator_verification` | Xác minh creator | KYC cho creator có thu nhập | admin | s3 |
| `console.workshop_ops.creator_payout_approvals` | Duyệt chi trả | Duyệt thanh toán | admin | s3 |
| `console.workshop_ops.workshop_featured` | Nổi bật workshop | Chọn mod nổi bật | admin | s3 |
| `console.workshop_ops.jam_management` | Quản lý game jam | Tổ chức, chấm điểm | admin | s3 |
| `console.workshop_ops.workshop_analytics` | Phân tích workshop | Tải, gỡ, doanh thu | admin | s3 |

### Web Console (admin/liveops/support/BI) › stream_ops

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `console.stream_ops.stream_integrations` | Tích hợp stream | Trạng thái API Twitch/YouTube | admin | s2 |
| `console.stream_ops.drops_campaigns_ops` | Vận hành drops | Chiến dịch drops toàn cục | admin | s2 |
| `console.stream_ops.stream_abuse` | Lạm dụng stream | Phiếu bot, gian lận drops | admin | s2 |
| `console.stream_ops.streamer_directory` | Danh bạ streamer | Streamer đã liên kết | admin | s2 |
| `console.stream_ops.esports_ops` | Vận hành esports | Lịch giải, trọng tài | admin | s2 |
| `console.stream_ops.esports_integrity` | Liêm chính esports | Nghi dàn xếp | admin | s2 |

### Web Console (admin/liveops/support/BI) › festival_ops

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `console.festival_ops.festival_scheduler` | Lịch lễ hội | Âm lịch ↔ dương lịch theo khu vực | admin | s1 |
| `console.festival_ops.festival_regional` | Lễ hội theo khu vực | Lễ hội riêng từng thị trường | admin | s1 |
| `console.festival_ops.festival_kpi` | KPI lễ hội | Tham gia, doanh thu | admin | s1 |
| `console.festival_ops.lantern_moderation` | Kiểm duyệt lời chúc | Lời chúc trên đèn lồng | admin | s1 |
| `console.festival_ops.season_rotation` | Luân chuyển mùa | Lịch xuân/hạ/thu/đông | admin | s1 |

### Creator Studio (web) › creative_editors

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `studio.creative_editors.evolution_tree_editor` | Sửa cây tiến hóa | Nhánh lớp, điều kiện | creator | s1 |
| `studio.creative_editors.talent_tree_editor` | Sửa cây thiên phú | Nút, liên kết, chi phí | creator | s1 |
| `studio.creative_editors.festival_designer` | Thiết kế lễ hội | Hoạt động, trang trí, lịch | creator | s1 |
| `studio.creative_editors.minigame_tuner` | Chỉnh minigame | Nhịp, độ khó minigame | creator | s1 |
| `studio.creative_editors.puzzle_designer` | Thiết kế câu đố | Dựng bàn cờ, luật | creator | s1 |
| `studio.creative_editors.puzzle_solver` | Bộ giải câu đố | Xác minh câu đố có lời giải, đếm số lời giải | creator | s1 |
| `studio.creative_editors.weather_editor` | Sửa thời tiết | Mẫu thời tiết, hiệu ứng trận | creator | s1 |
| `studio.creative_editors.season_editor` | Sửa mùa | Hiệu ứng mùa theo lớp | creator | s1 |
| `studio.creative_editors.dye_palette_editor` | Sửa bảng nhuộm | Palette swap cho sprite | creator | s1 |
| `studio.creative_editors.research_editor` | Sửa nghiên cứu quái | Mốc, gợi ý | creator | s1 |
| `studio.creative_editors.museum_prop_editor` | Sửa đạo cụ diorama | Vật phẩm trưng bày | creator | s1 |
| `studio.creative_editors.treasure_route_editor` | Sửa tuyến kho báu | Manh mối, vị trí | creator | s1 |

### Creator Studio (web) › narrative_ai

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `studio.narrative_ai.bard_template_editor` | Sửa mẫu Bard | Khung prompt theo loại sự kiện | creator | s2 |
| `studio.narrative_ai.bard_guardrails` | Rào chắn Bard | Từ cấm, lore bắt buộc, độ dài | creator | s2 |
| `studio.narrative_ai.bard_playground` | Thử Bard | Chạy thử trên biên niên mẫu | creator | s2 |
| `studio.narrative_ai.bard_eval_set` | Bộ đánh giá Bard | Tập kiểm tra chất lượng | creator | s2 |
| `studio.narrative_ai.story_vote_designer` | Thiết kế bỏ phiếu | Phương án + hệ quả nội dung | creator | s2 |
| `studio.narrative_ai.world_goal_designer` | Thiết kế mục tiêu thế giới | Ngưỡng, thưởng, cutscene | creator | s2 |
| `studio.narrative_ai.highlight_rules_editor` | Luật highlight | Điều kiện một khoảnh khắc là highlight | creator | s2 |
| `studio.narrative_ai.echo_vocabulary` | Từ vựng dấu ấn | Từ/cụm được phép khắc | creator | s2 |

### Creator Studio (web) › procedural

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `studio.procedural.rift_generator` | Bộ sinh Vết Nứt | Luật ghép phòng từ tileset | creator | s3 |
| `studio.procedural.rift_seed_explorer` | Duyệt seed | Xem trước tầng theo seed | creator | s3 |
| `studio.procedural.rift_seed_audit` | Kiểm tra seed | Loại seed không thể thắng/quá dễ | creator | s3 |
| `studio.procedural.rift_room_templates` | Mẫu phòng | Phòng thủ công dùng trong sinh thủ tục | creator | s3 |
| `studio.procedural.dream_map_transform` | Biến đổi map giấc mơ | Luật đảo màu/đảo chiều map | creator | s3 |
| `studio.procedural.card_designer` | Thiết kế thẻ | Thẻ + từ khóa + giá năng lượng | creator | s3 |
| `studio.procedural.card_balance_lab` | Cân bằng thẻ | Mô phỏng bộ bài | creator | s3 |
| `studio.procedural.boss_phase_composer` | Ghép pha boss | Pha ngẫu nhiên có ràng buộc | creator | s3 |
| `studio.procedural.modifier_designer` | Thiết kế luật biến đổi | Luật tuần/Vết Nứt | creator | s3 |
| `studio.procedural.speedrun_category_editor` | Sửa hạng mục speedrun | Luật, điều kiện hợp lệ | creator | s3 |

### Creator Studio (web) › act_two_tools

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `studio.act_two_tools.dual_world_linker` | Liên kết hai thế giới | Ghép map Bình Minh ↔ Hoàng Hôn | creator | s4 |
| `studio.act_two_tools.dual_world_diff_editor` | Sửa khác biệt thế giới | Đối tượng chỉ ở một phía | creator | s4 |
| `studio.act_two_tools.caravan_route_editor` | Sửa tuyến buôn | Tuyến, rủi ro, hàng | creator | s4 |
| `studio.act_two_tools.trade_goods_economy` | Kinh tế hàng buôn | Giá cơ sở, co giãn cung cầu | creator | s4 |
| `studio.act_two_tools.profession_editor` | Sửa nghề | Cây nghề, công thức | creator | s4 |
| `studio.act_two_tools.npc_schedule_editor` | Sửa lịch NPC | Lộ trình theo giờ | creator | s4 |
| `studio.act_two_tools.raid_designer` | Thiết kế raid | Boss, pha, luật chia đội | creator | s4 |
| `studio.act_two_tools.artifact_designer` | Thiết kế cổ vật | Mảnh, quyền năng | creator | s4 |
| `studio.act_two_tools.ending_flow_editor` | Sửa luồng kết thúc | Biến karma → 6 kết thúc | creator | s4 |
| `studio.act_two_tools.mount_editor` | Sửa thú cưỡi | Tốc độ, địa hình | creator | s4 |

### Dev Tools (web) › platform_integrity

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `devtools.platform_integrity.determinism_matrix` | Ma trận tất định | Golden replay trên JVM/ART/RoboVM/TeaVM | dev | s3 |
| `devtools.platform_integrity.determinism_diff` | Diff tất định | Lượt đầu tiên lệch giữa nền tảng | dev | s3 |
| `devtools.platform_integrity.mod_security_scanner` | Quét an ninh mod | Content pack không chứa mã, giới hạn kích thước | dev | s3 |
| `devtools.platform_integrity.mod_compat_matrix` | Tương thích mod | Mod × phiên bản client | dev | s3 |
| `devtools.platform_integrity.procgen_performance` | Hiệu năng sinh thủ tục | Thời gian sinh tầng theo thiết bị | dev | s3 |
| `devtools.platform_integrity.ai_bard_latency` | Độ trễ Bard | Thời gian phản hồi, tỉ lệ fallback | dev | s3 |
| `devtools.platform_integrity.ai_bard_cost` | Chi phí AI | Theo môi trường/tính năng | dev | s3 |
| `devtools.platform_integrity.stream_api_quota` | Quota API stream | Giới hạn gọi Twitch/YouTube | dev | s3 |
| `devtools.platform_integrity.companion_sync_monitor` | Đồng bộ companion | Độ trễ đồng bộ PWA ↔ game | dev | s3 |
| `devtools.platform_integrity.cross_save_conflicts` | Xung đột cross-save | Tỉ lệ xung đột theo thiết bị | dev | s3 |
| `devtools.platform_integrity.shader_compat` | Tương thích shader | Shader nhuộm màu theo GPU | dev | s3 |
| `devtools.platform_integrity.feature_kill_switches` | Công tắc tắt tính năng | Tắt nhanh tính năng lỗi | dev | s3 |

### QA Hub (web) › expansion_qa

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `qa.expansion_qa.puzzle_verification` | Xác minh câu đố | Solver xác nhận lời giải tối ưu | qa | s3 |
| `qa.expansion_qa.rift_seed_qa` | QA seed Vết Nứt | Kiểm tra seed trước khi phát | qa | s3 |
| `qa.expansion_qa.mod_qa` | QA mod nổi bật | Test mod trước khi đưa lên nổi bật | qa | s3 |
| `qa.expansion_qa.festival_qa` | QA lễ hội | Checklist trước lễ hội | qa | s3 |
| `qa.expansion_qa.minigame_qa` | QA minigame | Cảm giác nhịp, độ trễ input | qa | s3 |
| `qa.expansion_qa.card_mode_qa` | QA chế độ thẻ | Tương tác từ khóa | qa | s3 |
| `qa.expansion_qa.dual_world_qa` | QA hai thế giới | Kẹt vật cản khi chuyển | qa | s3 |
| `qa.expansion_qa.bard_red_team` | Red team Bard | Thử phá rào chắn AI | qa | s3 |
| `qa.expansion_qa.companion_qa` | QA companion | Đồng bộ, thông báo | qa | s3 |
| `qa.expansion_qa.stream_qa` | QA stream | Overlay, phiếu khán giả | qa | s3 |
| `qa.expansion_qa.accessibility_per_screen` | Trợ năng theo màn | Checklist trợ năng từng ScreenId | qa | s3 |
| `qa.expansion_qa.agent_expansion_coverage` | Phủ màn mở rộng | Độ phủ agent theo mùa | qa | s3 |

### Pilot Portal (web) › season_preview

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `pilot.season_preview.season_preview` | Xem trước mùa | Nội dung mùa sắp tới | pilot | s1 |
| `pilot.season_preview.feature_vote` | Bình chọn tính năng | Ưu tiên tính năng | pilot | s1 |
| `pilot.season_preview.prototype_lab` | Phòng thí nghiệm | Thử prototype chưa chắc ra mắt | pilot | s1 |
| `pilot.season_preview.prototype_feedback` | Góp ý prototype | Giữ/bỏ/sửa | pilot | s1 |
| `pilot.season_preview.balance_sandbox_pilot` | Sandbox cân bằng | Thử chỉ số mới trước khi phát | pilot | s1 |
| `pilot.season_preview.minigame_feel_test` | Thử cảm giác minigame | Đánh giá nhịp | pilot | s1 |
| `pilot.season_preview.puzzle_beta` | Câu đố beta | Giải câu đố chưa phát hành | pilot | s1 |
| `pilot.season_preview.card_beta` | Thẻ beta | Thử thẻ mới | pilot | s1 |
| `pilot.season_preview.dual_world_beta` | Hai thế giới beta | Thử cơ chế chuyển thế giới | pilot | s1 |
| `pilot.season_preview.bard_beta` | Bard beta | Đánh giá truyện AI | pilot | s1 |
| `pilot.season_preview.pilot_season_report` | Báo cáo mùa pilot | Đóng góp của tôi trong mùa | pilot | s1 |
| `pilot.season_preview.pilot_mentor` | Pilot kỳ cựu | Hướng dẫn pilot mới | pilot | s1 |

### Public Site + Account Portal (web) › community_web

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `site.community_web.lore_encyclopedia` | Bách khoa truyền thuyết | Wiki chính thức sinh từ content | gamer, user | s2 |
| `site.community_web.interactive_world_map` | Bản đồ thế giới tương tác | 12 vùng, 2 thế giới | gamer, user | s2 |
| `site.community_web.hero_builder_web` | Lên build trên web | Thiên phú + trang bị + chia sẻ link | gamer, user | s2 |
| `site.community_web.replay_viewer_web` | Xem replay trên web | Phát replay bằng bản web TeaVM | gamer, user | s2 |
| `site.community_web.highlight_gallery_web` | Thư viện highlight | Clip cộng đồng | gamer, user | s2 |
| `site.community_web.puzzle_web` | Câu đố ngày trên web | Giải câu đố ngay trên trình duyệt | gamer, user | s2 |
| `site.community_web.festival_pages` | Trang lễ hội | Giới thiệu lễ hội đang diễn ra | gamer, user | s2 |
| `site.community_web.world_state_live` | Trạng thái thế giới trực tiếp | Tiến độ mục tiêu thế giới | gamer, user | s2 |
| `site.community_web.story_vote_web` | Bỏ phiếu trên web | Bỏ phiếu cốt truyện | gamer, user | s2 |
| `site.community_web.chronicle_share_page` | Trang biên niên chia sẻ | Biên niên công khai của người chơi | gamer, user | s2 |
| `site.community_web.speedrun_web` | Speedrun trên web | Bảng + video | gamer, user | s2 |
| `site.community_web.esports_hub` | Trung tâm esports | Lịch, kết quả, VOD | gamer, user | s2 |
| `site.community_web.card_deck_share` | Chia sẻ bộ bài | Link bộ bài | gamer, user | s2 |
| `site.community_web.mod_showcase` | Giới thiệu mod | Mod nổi bật | gamer, user | s2 |
| `site.community_web.diorama_showcase` | Triển lãm diorama | Diorama cộng đồng | gamer, user | s2 |

### Game client (Desktop/Android/iOS/Web) › debug_expansion

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.debug_expansion.debug_festival_clock` | Đồng hồ lễ hội | Giả lập ngày âm lịch bất kỳ | dev, qa | s1 |
| `game.debug_expansion.debug_season_override` | Ghi đè mùa | Đặt mùa/thời tiết cục bộ | dev, qa | s1 |
| `game.debug_expansion.debug_evolution_unlock` | Mở khóa lớp nâng cao | Thử nhanh nhánh tiến hóa | dev, qa | s1 |
| `game.debug_expansion.debug_talent_sandbox` | Sandbox thiên phú | Cộng điểm tùy ý | dev, qa | s1 |
| `game.debug_expansion.debug_puzzle_loader` | Nạp câu đố | Mở câu đố theo ID | dev, qa | s1 |
| `game.debug_expansion.debug_rift_seed` | Seed Vết Nứt | Nhập seed, nhảy tầng | dev, qa | s1 |
| `game.debug_expansion.debug_card_spawner` | Tạo thẻ | Thêm thẻ vào tay | dev, qa | s1 |
| `game.debug_expansion.debug_dream_jump` | Nhảy giấc mơ | Mở giấc mơ bất kỳ | dev, qa | s1 |
| `game.debug_expansion.debug_dual_world` | Hai thế giới | Hiện đồng thời lớp va chạm hai phía | dev, qa | s1 |
| `game.debug_expansion.debug_caravan_time` | Thời gian đoàn buôn | Tua nhanh hành trình | dev, qa | s1 |
| `game.debug_expansion.debug_market_prices` | Giá chợ | Đặt giá cục bộ | dev, qa | s1 |
| `game.debug_expansion.debug_npc_schedule` | Lịch NPC | Tua giờ trong game | dev, qa | s1 |
| `game.debug_expansion.debug_raid_sim` | Mô phỏng raid | Chạy raid với đội mẫu | dev, qa | s1 |
| `game.debug_expansion.debug_bard_mock` | Bard giả lập | Dùng phản hồi mẫu, không gọi AI | dev, qa | s1 |
| `game.debug_expansion.debug_stream_mock` | Stream giả lập | Giả phiếu khán giả | dev, qa | s1 |
| `game.debug_expansion.debug_world_goal` | Mục tiêu thế giới | Đặt tiến độ cục bộ | dev, qa | s1 |
| `game.debug_expansion.debug_rewind_trace` | Truy vết tua ngược | So sánh trạng thái trước/sau rewind | dev, qa | s1 |
| `game.debug_expansion.debug_companion_sync` | Đồng bộ companion | Ép đồng bộ, xem gói | dev, qa | s1 |
| `game.debug_expansion.debug_mod_loader` | Nạp mod | Nạp content pack cục bộ | dev, qa | s1 |
| `game.debug_expansion.debug_accessibility_overlay` | Overlay trợ năng | Vùng chạm, tương phản, thứ tự đọc | dev, qa | s1 |

### QA Hub (web) › automation_lab

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `qa.automation_lab.agent_personas` | Persona agent | Người chơi mới, hardcore, chi tiêu, trợ năng | qa, dev | s2 |
| `qa.automation_lab.agent_persona_editor` | Sửa persona | Mục tiêu, phong cách, ngân sách | qa, dev | s2 |
| `qa.automation_lab.scenario_recorder` | Ghi kịch bản | Ghi thao tác tay thành kịch bản YAML | qa, dev | s2 |
| `qa.automation_lab.scenario_diff` | So sánh kịch bản | Thay đổi giữa các phiên bản | qa, dev | s2 |
| `qa.automation_lab.agent_live_view` | Xem agent trực tiếp | Màn hình + suy luận của agent | qa, dev | s2 |
| `qa.automation_lab.agent_intervention` | Can thiệp agent | Tạm dừng, gợi ý, tiếp tục | qa, dev | s2 |
| `qa.automation_lab.agent_bug_candidates` | Lỗi ứng viên | Lỗi agent tìm, chờ QA xác nhận | qa, dev | s2 |
| `qa.automation_lab.agent_false_positives` | Báo động giả | Huấn luyện lại oracle | qa, dev | s2 |
| `qa.automation_lab.oracle_catalog` | Danh mục oracle | Invariant đang kiểm tra | qa, dev | s2 |
| `qa.automation_lab.oracle_editor` | Sửa oracle | Thêm invariant mới | qa, dev | s2 |
| `qa.automation_lab.fixture_builder` | Tạo fixture | Trạng thái game cho từng ScreenId | qa, dev | s2 |
| `qa.automation_lab.screen_fixture_matrix` | Ma trận fixture | ScreenId × fixture | qa, dev | s2 |
| `qa.automation_lab.fuzz_campaigns` | Chiến dịch fuzz | Input ngẫu nhiên có seed | qa, dev | s2 |
| `qa.automation_lab.monkey_runs` | Monkey test | Chạm ngẫu nhiên lâu dài | qa, dev | s2 |
| `qa.automation_lab.soak_tests` | Soak test | Chạy 24 giờ, rò rỉ bộ nhớ | qa, dev | s2 |

### QA Hub (web) › live_quality

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `qa.live_quality.live_bug_radar` | Radar lỗi trực tiếp | Lỗi mới sau release theo phút | qa | s3 |
| `qa.live_quality.player_reported_bugs` | Lỗi người chơi báo | Gom từ ticket, bug_report, pilot | qa | s3 |
| `qa.live_quality.repro_queue` | Hàng đợi tái hiện | Lỗi cần tái hiện | qa | s3 |
| `qa.live_quality.hotfix_verification` | Xác minh hotfix | Checklist hotfix | qa | s3 |
| `qa.live_quality.rollout_health` | Sức khỏe rollout | Chỉ số theo % rollout | qa | s3 |
| `qa.live_quality.store_review_monitor` | Theo dõi đánh giá store | Đánh giá 1–2 sao theo chủ đề | qa | s3 |
| `qa.live_quality.qa_calendar` | Lịch QA | Lịch test theo mùa | qa | s3 |
| `qa.live_quality.qa_capacity` | Năng lực QA | Phân công theo mùa | qa | s3 |
| `qa.live_quality.test_debt` | Nợ test | Màn chưa có test case | qa | s3 |
| `qa.live_quality.regression_heatmap` | Heatmap hồi quy | Module hay hỏng | qa | s3 |
| `qa.live_quality.cert_checklist_ios` | Checklist duyệt App Store | Yêu cầu Apple | qa | s3 |
| `qa.live_quality.cert_checklist_android` | Checklist Google Play | Yêu cầu Google | qa | s3 |
| `qa.live_quality.cert_checklist_steam` | Checklist Steam | Yêu cầu Steam Deck | qa | s3 |
| `qa.live_quality.age_rating_qa` | QA xếp hạng tuổi | Nội dung nhạy cảm theo vùng | qa | s3 |
| `qa.live_quality.payment_qa` | QA thanh toán | Sandbox giao dịch | qa | s3 |

### Dev Tools (web) › data_platform

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `devtools.data_platform.event_schema_registry` | Registry sự kiện telemetry | Schema TelemetryEvent có version | dev | s2 |
| `devtools.data_platform.event_schema_diff` | Diff schema sự kiện | Thay đổi phá vỡ | dev | s2 |
| `devtools.data_platform.event_debugger` | Debug sự kiện | Sự kiện từ một thiết bị theo thời gian thực | dev | s2 |
| `devtools.data_platform.pipeline_lag` | Độ trễ pipeline | Ingest → ClickHouse | dev | s2 |
| `devtools.data_platform.data_quality` | Chất lượng dữ liệu | Sự kiện thiếu trường, trùng | dev | s2 |
| `devtools.data_platform.warehouse_tables` | Bảng dữ liệu | Bảng ClickHouse, dung lượng | dev | s2 |
| `devtools.data_platform.query_notebooks` | Notebook truy vấn | SQL chỉ đọc có lưu | dev | s2 |
| `devtools.data_platform.data_retention_jobs` | Job lưu trữ | Xóa theo chính sách | dev | s2 |
| `devtools.data_platform.privacy_scrubber` | Làm sạch dữ liệu cá nhân | Kiểm tra PII | dev | s2 |
| `devtools.data_platform.experiment_assignment_debug` | Debug gán A/B | Người chơi thuộc nhánh nào | dev | s2 |
| `devtools.data_platform.feature_store` | Feature store | Đặc trưng cho mô hình churn | dev | s2 |
| `devtools.data_platform.ml_models` | Mô hình ML | Churn, gian lận: phiên bản, độ chính xác | dev | s2 |

### Dev Tools (web) › sdk_platform

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `devtools.sdk_platform.mod_sdk_releases` | Phát hành SDK mod | Phiên bản SDK, changelog | dev, creator | s3 |
| `devtools.sdk_platform.schema_docs_generator` | Sinh tài liệu schema | Từ JSON Schema sang tài liệu | dev, creator | s3 |
| `devtools.sdk_platform.content_api_keys` | Key API nội dung | Cho công cụ bên thứ ba | dev, creator | s3 |
| `devtools.sdk_platform.public_api_usage` | Sử dụng API công khai | Theo ứng dụng | dev, creator | s3 |
| `devtools.sdk_platform.public_api_apps` | Ứng dụng bên thứ ba | Đăng ký, duyệt | dev, creator | s3 |
| `devtools.sdk_platform.oauth_clients` | OAuth client | Client đăng nhập bằng tài khoản game | dev, creator | s3 |
| `devtools.sdk_platform.sandbox_servers` | Server sandbox | Server cho người làm tool | dev, creator | s3 |
| `devtools.sdk_platform.sdk_samples` | Ví dụ SDK | Dự án mẫu | dev, creator | s3 |
| `devtools.sdk_platform.sdk_issue_tracker` | Lỗi SDK | Lỗi cộng đồng báo | dev, creator | s3 |
| `devtools.sdk_platform.api_changelog` | Changelog API | Thay đổi API công khai | dev, creator | s3 |

### Dev Tools (web) › runtime_ops

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `devtools.runtime_ops.build_flavor_matrix` | Ma trận flavor | dev/qa/pilot/release × nền tảng | dev | s4 |
| `devtools.runtime_ops.remote_asset_bundles` | Gói asset từ xa | Tải asset theo mùa | dev | s4 |
| `devtools.runtime_ops.asset_bundle_diff` | Diff gói asset | Thay đổi giữa mùa | dev | s4 |
| `devtools.runtime_ops.memory_budgets` | Ngân sách bộ nhớ | Theo màn và thiết bị | dev | s4 |
| `devtools.runtime_ops.texture_budget` | Ngân sách texture | VRAM theo màn | dev | s4 |
| `devtools.runtime_ops.startup_profiler` | Profiler khởi động | Thời gian từng bước boot | dev | s4 |
| `devtools.runtime_ops.frame_profiler` | Profiler khung hình | Khung hình chậm theo màn | dev | s4 |
| `devtools.runtime_ops.network_budget` | Ngân sách mạng | Dung lượng theo phiên | dev | s4 |
| `devtools.runtime_ops.battery_profiler` | Profiler pin | Tiêu thụ pin theo màn | dev | s4 |
| `devtools.runtime_ops.thermal_reports` | Báo cáo nhiệt | Throttle theo thiết bị | dev | s4 |
| `devtools.runtime_ops.server_capacity` | Dung lượng server | Dự báo theo sự kiện | dev | s4 |
| `devtools.runtime_ops.db_slow_queries` | Truy vấn chậm | Top truy vấn chậm | dev | s4 |
| `devtools.runtime_ops.cache_hit_rates` | Tỉ lệ cache | Redis/CDN | dev | s4 |
| `devtools.runtime_ops.queue_backlogs` | Tồn đọng hàng đợi | Job nền | dev | s4 |
| `devtools.runtime_ops.cost_explorer` | Chi phí hạ tầng | Theo dịch vụ | dev | s4 |
| `devtools.runtime_ops.release_notes_builder` | Sinh release note | Từ commit + catalog | dev | s4 |
| `devtools.runtime_ops.screen_ownership` | Chủ sở hữu màn | ScreenId ↔ đội | dev | s4 |
| `devtools.runtime_ops.dead_screen_detector` | Phát hiện màn chết | Màn không ai mở trong 30 ngày | dev | s4 |

### Public Site + Account Portal (web) › account_plus

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `site.account_plus.family_group` | Nhóm gia đình | Tài khoản gia đình | user | s1 |
| `site.account_plus.family_member` | Thành viên gia đình | Quyền, giới hạn | user | s1 |
| `site.account_plus.family_spending` | Chi tiêu gia đình | Duyệt mua của trẻ | user | s1 |
| `site.account_plus.subscription_manage` | Quản lý thuê bao | Thẻ tháng, Battle Pass tự gia hạn | user | s1 |
| `site.account_plus.subscription_cancel` | Hủy thuê bao | Hủy và giữ quyền lợi đến hết kỳ | user | s1 |
| `site.account_plus.payment_methods` | Phương thức thanh toán | Thẻ, ví điện tử | user | s1 |
| `site.account_plus.invoices` | Hóa đơn | Tải hóa đơn | user | s1 |
| `site.account_plus.gift_purchase` | Mua quà tặng | Mua gói tặng bạn | user | s1 |
| `site.account_plus.gift_inbox` | Quà được tặng | Nhận quà | user | s1 |
| `site.account_plus.wishlist` | Danh sách mong muốn | Gói muốn mua | user | s1 |
| `site.account_plus.login_history` | Lịch sử đăng nhập | Đăng nhập theo thiết bị/vị trí | user | s1 |
| `site.account_plus.security_alerts` | Cảnh báo bảo mật | Đăng nhập lạ | user | s1 |
| `site.account_plus.recovery_codes` | Mã khôi phục | Mã dự phòng 2FA | user | s1 |
| `site.account_plus.data_requests` | Yêu cầu dữ liệu | Trạng thái xuất/xóa dữ liệu | user | s1 |
| `site.account_plus.consent_center` | Trung tâm đồng ý | Quản lý đồng ý marketing/analytics | user | s1 |
| `site.account_plus.communication_prefs` | Tùy chọn liên lạc | Email, push | user | s1 |
| `site.account_plus.account_transfer` | Chuyển vùng tài khoản | Chuyển server/khu vực | user | s1 |
| `site.account_plus.linked_streams` | Kênh stream liên kết | Quản lý liên kết Twitch/YouTube | user | s1 |
| `site.account_plus.creator_account_link` | Liên kết tài khoản creator | Workshop, đối tác | user | s1 |
| `site.account_plus.account_badges` | Huy hiệu tài khoản | Pilot, dịch giả, creator | user | s1 |
| `site.account_plus.support_history` | Lịch sử hỗ trợ | Mọi ticket | user | s1 |
| `site.account_plus.refund_request` | Yêu cầu hoàn tiền | Gửi yêu cầu | user | s1 |
| `site.account_plus.purchase_limits` | Hạn mức mua | Tự đặt hạn mức | user | s1 |
| `site.account_plus.self_exclusion` | Tạm ngưng chơi | Tự khóa tài khoản trong thời gian chọn | user | s1 |

### Game client (Desktop/Android/iOS/Web) › gifting

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `game.gifting.gift_shop` | Cửa hàng quà | Gói có thể tặng | gamer, user | s1 |
| `game.gifting.gift_select_friend` | Chọn người nhận | Bạn bè đủ điều kiện | gamer, user | s1 |
| `game.gifting.gift_message` | Lời nhắn quà | Thiệp kèm quà | gamer, user | s1 |
| `game.gifting.gift_confirm` | Xác nhận tặng | Giá, người nhận | gamer, user | s1 |
| `game.gifting.gift_received` | Quà nhận được | Mở quà | gamer, user | s1 |
| `game.gifting.gift_history` | Lịch sử tặng quà | Đã tặng/đã nhận | gamer, user | s1 |
| `game.gifting.gift_thank_you` | Cảm ơn | Gửi lời cảm ơn | gamer, user | s1 |
| `game.gifting.gift_limits` | Giới hạn tặng | Hạn mức chống lạm dụng | gamer, user | s1 |

### Web Console (admin/liveops/support/BI) › trust_ops

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `console.trust_ops.gifting_abuse` | Lạm dụng tặng quà | Rửa tiền qua quà tặng | admin | s3 |
| `console.trust_ops.account_takeover` | Chiếm đoạt tài khoản | Phát hiện, khôi phục | admin | s3 |
| `console.trust_ops.family_disputes` | Tranh chấp gia đình | Hoàn tiền giao dịch trẻ em | admin | s3 |
| `console.trust_ops.self_exclusion_admin` | Quản lý tự ngưng | Tôn trọng yêu cầu tự khóa | admin | s3 |
| `console.trust_ops.minor_protection` | Bảo vệ trẻ vị thành niên | Giới hạn theo luật | admin | s3 |
| `console.trust_ops.subscription_admin` | Quản lý thuê bao | Gia hạn, hoàn tiền | admin | s3 |
| `console.trust_ops.localization_ops` | Vận hành bản dịch | Dự án dịch cộng đồng | admin | s3 |
| `console.trust_ops.translator_moderation` | Kiểm duyệt dịch giả | Dịch phá hoại | admin | s3 |
| `console.trust_ops.partner_ops` | Vận hành đối tác | Duyệt hồ sơ, hợp đồng | admin | s3 |
| `console.trust_ops.partner_fraud` | Gian lận đối tác | Mã giới thiệu ảo | admin | s3 |
| `console.trust_ops.esports_payouts` | Chi trả esports | Tiền thưởng giải | admin | s3 |
| `console.trust_ops.speedrun_verification` | Xác minh speedrun | Replay + video | admin | s3 |
| `console.trust_ops.ironman_integrity` | Liêm chính Thiết Nhân | Phát hiện lách luật | admin | s3 |
| `console.trust_ops.raid_ops` | Vận hành raid | Theo dõi, bồi thường | admin | s3 |
| `console.trust_ops.mentor_abuse` | Lạm dụng sư phụ | Farm thưởng xuất sư | admin | s3 |
| `console.trust_ops.caravan_ops` | Vận hành đoàn buôn | Tuyến lỗi, bồi thường | admin | s3 |
| `console.trust_ops.companion_ops` | Vận hành companion | Push, lỗi đồng bộ | admin | s3 |
| `console.trust_ops.act_two_rollout` | Triển khai Hồi II | Mở khu vực theo đợt | admin | s3 |
| `console.trust_ops.season_launch_room` | Phòng chỉ huy ra mùa | Checklist + chỉ số ra mùa | admin | s3 |
| `console.trust_ops.season_retrospective` | Tổng kết mùa | KPI mùa so với mục tiêu | admin | s3 |
| `console.trust_ops.cross_play_ops` | Vận hành chơi chéo | Ghép trận theo nền tảng | admin | s3 |
| `console.trust_ops.accessibility_feedback` | Phản hồi trợ năng | Yêu cầu trợ năng từ người chơi | admin | s3 |
| `console.trust_ops.puzzle_ops` | Vận hành câu đố | Câu đố bị báo sai | admin | s3 |
| `console.trust_ops.card_meta_monitor` | Theo dõi meta thẻ | Thẻ quá mạnh | admin | s3 |
| `console.trust_ops.rift_ops` | Vận hành Vết Nứt | Seed lỗi, bảng xếp hạng gian lận | admin | s3 |
| `console.trust_ops.world_voice_ops` | Vận hành tiếng nói thế giới | Tổng quan phiếu + mục tiêu | admin | s3 |
| `console.trust_ops.reputation_scores` | Điểm uy tín | Uy tín người chơi cho tính năng xã hội | admin | s3 |
| `console.trust_ops.content_ratings_ops` | Xếp hạng nội dung UGC | Gắn nhãn tuổi cho mod/level | admin | s3 |

### Pilot Portal (web) › research_ops

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `pilot.research_ops.diary_study` | Nhật ký trải nghiệm | Ghi cảm nhận mỗi ngày trong 2 tuần | pilot | s2 |
| `pilot.research_ops.diary_entry` | Viết nhật ký | Ghi chép + ảnh | pilot | s2 |
| `pilot.research_ops.card_sort` | Card sorting | Sắp xếp menu theo cách hiểu | pilot | s2 |
| `pilot.research_ops.first_click_test` | Thử cú chạm đầu | Tìm chức năng trên mockup | pilot | s2 |
| `pilot.research_ops.five_second_test` | Thử 5 giây | Ấn tượng đầu về màn hình | pilot | s2 |
| `pilot.research_ops.naming_test` | Thử đặt tên | Chọn tên cho tính năng | pilot | s2 |
| `pilot.research_ops.difficulty_rating` | Chấm độ khó | Chấm từng trận/câu đố | pilot | s2 |
| `pilot.research_ops.fun_rating` | Chấm độ vui | Chấm từng phiên chơi | pilot | s2 |
| `pilot.research_ops.bug_bounty` | Săn lỗi có thưởng | Thưởng theo mức độ lỗi | pilot | s2 |
| `pilot.research_ops.bug_bounty_leaderboard` | Xếp hạng săn lỗi | Pilot tìm lỗi giỏi nhất | pilot | s2 |

### Web Console (admin/liveops/support/BI) › liveops_entities

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `console.liveops_entities.events.list` | Sự kiện — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.events.detail` | Sự kiện — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.events.editor` | Sự kiện — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.events.history` | Sự kiện — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.events.bulk` | Sự kiện — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.banners.list` | Banner chiêu mộ — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.banners.detail` | Banner chiêu mộ — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.banners.editor` | Banner chiêu mộ — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.banners.history` | Banner chiêu mộ — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.banners.bulk` | Banner chiêu mộ — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.offers.list` | Gói ưu đãi — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.offers.detail` | Gói ưu đãi — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.offers.editor` | Gói ưu đãi — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.offers.history` | Gói ưu đãi — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.offers.bulk` | Gói ưu đãi — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.bundles.list` | Combo — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.bundles.detail` | Combo — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.bundles.editor` | Combo — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.bundles.history` | Combo — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.bundles.bulk` | Combo — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.promo_codes.list` | Mã khuyến mãi — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.promo_codes.detail` | Mã khuyến mãi — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.promo_codes.editor` | Mã khuyến mãi — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.promo_codes.history` | Mã khuyến mãi — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.promo_codes.bulk` | Mã khuyến mãi — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.mail_templates.list` | Mẫu thư — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.mail_templates.detail` | Mẫu thư — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.mail_templates.editor` | Mẫu thư — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.mail_templates.history` | Mẫu thư — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.mail_templates.bulk` | Mẫu thư — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.push_campaigns.list` | Chiến dịch push — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.push_campaigns.detail` | Chiến dịch push — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.push_campaigns.editor` | Chiến dịch push — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.push_campaigns.history` | Chiến dịch push — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.push_campaigns.bulk` | Chiến dịch push — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.announcements.list` | Thông báo — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.announcements.detail` | Thông báo — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.announcements.editor` | Thông báo — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.announcements.history` | Thông báo — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.announcements.bulk` | Thông báo — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.segments.list` | Phân khúc — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.segments.detail` | Phân khúc — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.segments.editor` | Phân khúc — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.segments.history` | Phân khúc — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.segments.bulk` | Phân khúc — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.ab_tests.list` | Thử nghiệm A/B — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.ab_tests.detail` | Thử nghiệm A/B — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.ab_tests.editor` | Thử nghiệm A/B — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.ab_tests.history` | Thử nghiệm A/B — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.ab_tests.bulk` | Thử nghiệm A/B — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.feature_flags.list` | Feature flag — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.feature_flags.detail` | Feature flag — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.feature_flags.editor` | Feature flag — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.feature_flags.history` | Feature flag — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.feature_flags.bulk` | Feature flag — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.leaderboard_seasons.list` | Mùa xếp hạng — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.leaderboard_seasons.detail` | Mùa xếp hạng — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.leaderboard_seasons.editor` | Mùa xếp hạng — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.leaderboard_seasons.history` | Mùa xếp hạng — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.leaderboard_seasons.bulk` | Mùa xếp hạng — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.battle_pass_seasons.list` | Mùa Battle Pass — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.battle_pass_seasons.detail` | Mùa Battle Pass — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.battle_pass_seasons.editor` | Mùa Battle Pass — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.battle_pass_seasons.history` | Mùa Battle Pass — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.battle_pass_seasons.bulk` | Mùa Battle Pass — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.tournaments.list` | Giải đấu — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.tournaments.detail` | Giải đấu — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.tournaments.editor` | Giải đấu — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.tournaments.history` | Giải đấu — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.tournaments.bulk` | Giải đấu — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.guilds.list` | Bang hội — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.guilds.detail` | Bang hội — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.guilds.editor` | Bang hội — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.guilds.history` | Bang hội — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.guilds.bulk` | Bang hội — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.staff_users.list` | Tài khoản nhân viên — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.staff_users.detail` | Tài khoản nhân viên — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.staff_users.editor` | Tài khoản nhân viên — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.staff_users.history` | Tài khoản nhân viên — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.staff_users.bulk` | Tài khoản nhân viên — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.roles.list` | Vai trò & quyền — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.roles.detail` | Vai trò & quyền — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.roles.editor` | Vai trò & quyền — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.roles.history` | Vai trò & quyền — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.roles.bulk` | Vai trò & quyền — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.api_keys.list` | API key — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.api_keys.detail` | API key — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.api_keys.editor` | API key — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.api_keys.history` | API key — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.api_keys.bulk` | API key — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.word_filters.list` | Bộ lọc từ ngữ — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.word_filters.detail` | Bộ lọc từ ngữ — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.word_filters.editor` | Bộ lọc từ ngữ — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.word_filters.history` | Bộ lọc từ ngữ — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.word_filters.bulk` | Bộ lọc từ ngữ — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.maintenance_windows.list` | Lịch bảo trì — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.maintenance_windows.detail` | Lịch bảo trì — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.maintenance_windows.editor` | Lịch bảo trì — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.maintenance_windows.history` | Lịch bảo trì — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.maintenance_windows.bulk` | Lịch bảo trì — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.store_products.list` | Sản phẩm IAP — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.store_products.detail` | Sản phẩm IAP — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.store_products.editor` | Sản phẩm IAP — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.store_products.history` | Sản phẩm IAP — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.store_products.bulk` | Sản phẩm IAP — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.support_macros.list` | Mẫu trả lời CSKH — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.support_macros.detail` | Mẫu trả lời CSKH — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.support_macros.editor` | Mẫu trả lời CSKH — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.support_macros.history` | Mẫu trả lời CSKH — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.support_macros.bulk` | Mẫu trả lời CSKH — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.faq_articles.list` | Bài FAQ — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.faq_articles.detail` | Bài FAQ — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.faq_articles.editor` | Bài FAQ — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.faq_articles.history` | Bài FAQ — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.faq_articles.bulk` | Bài FAQ — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.news_posts.list` | Bài tin tức — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.news_posts.detail` | Bài tin tức — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.news_posts.editor` | Bài tin tức — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.news_posts.history` | Bài tin tức — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.news_posts.bulk` | Bài tin tức — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.patch_notes.list` | Ghi chú cập nhật — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.patch_notes.detail` | Ghi chú cập nhật — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.patch_notes.editor` | Ghi chú cập nhật — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.patch_notes.history` | Ghi chú cập nhật — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.patch_notes.bulk` | Ghi chú cập nhật — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.pilot_cohorts.list` | Nhóm pilot — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.pilot_cohorts.detail` | Nhóm pilot — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.pilot_cohorts.editor` | Nhóm pilot — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.pilot_cohorts.history` | Nhóm pilot — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.pilot_cohorts.bulk` | Nhóm pilot — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.pilot_builds.list` | Build pilot — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.pilot_builds.detail` | Build pilot — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.pilot_builds.editor` | Build pilot — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.pilot_builds.history` | Build pilot — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.pilot_builds.bulk` | Build pilot — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.pilot_surveys.list` | Khảo sát pilot — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.pilot_surveys.detail` | Khảo sát pilot — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.pilot_surveys.editor` | Khảo sát pilot — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.pilot_surveys.history` | Khảo sát pilot — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.pilot_surveys.bulk` | Khảo sát pilot — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |
| `console.liveops_entities.content_releases.list` | Bản phát hành nội dung — Danh sách | Tìm, lọc, sắp xếp | admin | launch |
| `console.liveops_entities.content_releases.detail` | Bản phát hành nội dung — Chi tiết | Xem đầy đủ + trạng thái | admin | launch |
| `console.liveops_entities.content_releases.editor` | Bản phát hành nội dung — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | launch |
| `console.liveops_entities.content_releases.history` | Bản phát hành nội dung — Lịch sử | Phiên bản, người sửa, diff | admin | launch |
| `console.liveops_entities.content_releases.bulk` | Bản phát hành nội dung — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | launch |

### Creator Studio (web) › content_entities

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `studio.content_entities.hero_classes.list` | Lớp nhân vật — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.hero_classes.detail` | Lớp nhân vật — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.hero_classes.editor` | Lớp nhân vật — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.hero_classes.history` | Lớp nhân vật — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.hero_classes.bulk` | Lớp nhân vật — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.hero_classes.promote` | Lớp nhân vật — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.heroes.list` | Anh hùng — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.heroes.detail` | Anh hùng — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.heroes.editor` | Anh hùng — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.heroes.history` | Anh hùng — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.heroes.bulk` | Anh hùng — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.heroes.promote` | Anh hùng — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.skills.list` | Kỹ năng — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.skills.detail` | Kỹ năng — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.skills.editor` | Kỹ năng — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.skills.history` | Kỹ năng — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.skills.bulk` | Kỹ năng — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.skills.promote` | Kỹ năng — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.skill_effects.list` | Hiệu ứng kỹ năng — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.skill_effects.detail` | Hiệu ứng kỹ năng — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.skill_effects.editor` | Hiệu ứng kỹ năng — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.skill_effects.history` | Hiệu ứng kỹ năng — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.skill_effects.bulk` | Hiệu ứng kỹ năng — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.skill_effects.promote` | Hiệu ứng kỹ năng — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.statuses.list` | Trạng thái (buff/debuff) — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.statuses.detail` | Trạng thái (buff/debuff) — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.statuses.editor` | Trạng thái (buff/debuff) — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.statuses.history` | Trạng thái (buff/debuff) — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.statuses.bulk` | Trạng thái (buff/debuff) — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.statuses.promote` | Trạng thái (buff/debuff) — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.passives.list` | Nội tại — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.passives.detail` | Nội tại — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.passives.editor` | Nội tại — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.passives.history` | Nội tại — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.passives.bulk` | Nội tại — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.passives.promote` | Nội tại — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.items.list` | Vật phẩm — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.items.detail` | Vật phẩm — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.items.editor` | Vật phẩm — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.items.history` | Vật phẩm — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.items.bulk` | Vật phẩm — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.items.promote` | Vật phẩm — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.item_categories.list` | Loại vật phẩm — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.item_categories.detail` | Loại vật phẩm — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.item_categories.editor` | Loại vật phẩm — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.item_categories.history` | Loại vật phẩm — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.item_categories.bulk` | Loại vật phẩm — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.item_categories.promote` | Loại vật phẩm — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.equipment.list` | Trang bị — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.equipment.detail` | Trang bị — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.equipment.editor` | Trang bị — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.equipment.history` | Trang bị — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.equipment.bulk` | Trang bị — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.equipment.promote` | Trang bị — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.equipment_sets.list` | Bộ trang bị — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.equipment_sets.detail` | Bộ trang bị — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.equipment_sets.editor` | Bộ trang bị — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.equipment_sets.history` | Bộ trang bị — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.equipment_sets.bulk` | Bộ trang bị — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.equipment_sets.promote` | Bộ trang bị — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.gems.list` | Ngọc khảm — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.gems.detail` | Ngọc khảm — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.gems.editor` | Ngọc khảm — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.gems.history` | Ngọc khảm — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.gems.bulk` | Ngọc khảm — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.gems.promote` | Ngọc khảm — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.recipes.list` | Công thức chế tạo — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.recipes.detail` | Công thức chế tạo — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.recipes.editor` | Công thức chế tạo — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.recipes.history` | Công thức chế tạo — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.recipes.bulk` | Công thức chế tạo — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.recipes.promote` | Công thức chế tạo — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.enemies.list` | Quái — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.enemies.detail` | Quái — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.enemies.editor` | Quái — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.enemies.history` | Quái — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.enemies.bulk` | Quái — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.enemies.promote` | Quái — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.bosses.list` | Boss — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.bosses.detail` | Boss — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.bosses.editor` | Boss — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.bosses.history` | Boss — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.bosses.bulk` | Boss — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.bosses.promote` | Boss — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.boss_phases.list` | Pha boss — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.boss_phases.detail` | Pha boss — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.boss_phases.editor` | Pha boss — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.boss_phases.history` | Pha boss — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.boss_phases.bulk` | Pha boss — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.boss_phases.promote` | Pha boss — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.encounters.list` | Trận đấu — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.encounters.detail` | Trận đấu — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.encounters.editor` | Trận đấu — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.encounters.history` | Trận đấu — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.encounters.bulk` | Trận đấu — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.encounters.promote` | Trận đấu — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.maps.list` | Bản đồ — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.maps.detail` | Bản đồ — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.maps.editor` | Bản đồ — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.maps.history` | Bản đồ — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.maps.bulk` | Bản đồ — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.maps.promote` | Bản đồ — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.regions.list` | Vùng — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.regions.detail` | Vùng — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.regions.editor` | Vùng — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.regions.history` | Vùng — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.regions.bulk` | Vùng — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.regions.promote` | Vùng — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.tilesets.list` | Tileset — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.tilesets.detail` | Tileset — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.tilesets.editor` | Tileset — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.tilesets.history` | Tileset — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.tilesets.bulk` | Tileset — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.tilesets.promote` | Tileset — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.npcs.list` | NPC — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.npcs.detail` | NPC — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.npcs.editor` | NPC — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.npcs.history` | NPC — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.npcs.bulk` | NPC — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.npcs.promote` | NPC — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.dialogues.list` | Hội thoại — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.dialogues.detail` | Hội thoại — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.dialogues.editor` | Hội thoại — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.dialogues.history` | Hội thoại — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.dialogues.bulk` | Hội thoại — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.dialogues.promote` | Hội thoại — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.quests.list` | Nhiệm vụ — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.quests.detail` | Nhiệm vụ — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.quests.editor` | Nhiệm vụ — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.quests.history` | Nhiệm vụ — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.quests.bulk` | Nhiệm vụ — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.quests.promote` | Nhiệm vụ — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.quest_chains.list` | Chuỗi nhiệm vụ — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.quest_chains.detail` | Chuỗi nhiệm vụ — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.quest_chains.editor` | Chuỗi nhiệm vụ — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.quest_chains.history` | Chuỗi nhiệm vụ — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.quest_chains.bulk` | Chuỗi nhiệm vụ — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.quest_chains.promote` | Chuỗi nhiệm vụ — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.achievements.list` | Thành tựu — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.achievements.detail` | Thành tựu — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.achievements.editor` | Thành tựu — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.achievements.history` | Thành tựu — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.achievements.bulk` | Thành tựu — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.achievements.promote` | Thành tựu — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.titles.list` | Danh hiệu — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.titles.detail` | Danh hiệu — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.titles.editor` | Danh hiệu — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.titles.history` | Danh hiệu — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.titles.bulk` | Danh hiệu — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.titles.promote` | Danh hiệu — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.checkin_tables.list` | Bảng điểm danh — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.checkin_tables.detail` | Bảng điểm danh — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.checkin_tables.editor` | Bảng điểm danh — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.checkin_tables.history` | Bảng điểm danh — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.checkin_tables.bulk` | Bảng điểm danh — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.checkin_tables.promote` | Bảng điểm danh — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.loot_tables.list` | Bảng rơi đồ — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.loot_tables.detail` | Bảng rơi đồ — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.loot_tables.editor` | Bảng rơi đồ — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.loot_tables.history` | Bảng rơi đồ — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.loot_tables.bulk` | Bảng rơi đồ — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.loot_tables.promote` | Bảng rơi đồ — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.recruit_pools.list` | Nhóm chiêu mộ — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.recruit_pools.detail` | Nhóm chiêu mộ — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.recruit_pools.editor` | Nhóm chiêu mộ — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.recruit_pools.history` | Nhóm chiêu mộ — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.recruit_pools.bulk` | Nhóm chiêu mộ — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.recruit_pools.promote` | Nhóm chiêu mộ — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.currencies.list` | Tiền tệ — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.currencies.detail` | Tiền tệ — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.currencies.editor` | Tiền tệ — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.currencies.history` | Tiền tệ — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.currencies.bulk` | Tiền tệ — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.currencies.promote` | Tiền tệ — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.shop_catalogs.list` | Danh mục cửa hàng — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.shop_catalogs.detail` | Danh mục cửa hàng — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.shop_catalogs.editor` | Danh mục cửa hàng — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.shop_catalogs.history` | Danh mục cửa hàng — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.shop_catalogs.bulk` | Danh mục cửa hàng — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.shop_catalogs.promote` | Danh mục cửa hàng — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.pets.list` | Thú cưng — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.pets.detail` | Thú cưng — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.pets.editor` | Thú cưng — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.pets.history` | Thú cưng — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.pets.bulk` | Thú cưng — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.pets.promote` | Thú cưng — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.buildings.list` | Công trình làng — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.buildings.detail` | Công trình làng — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.buildings.editor` | Công trình làng — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.buildings.history` | Công trình làng — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.buildings.bulk` | Công trình làng — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.buildings.promote` | Công trình làng — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.cutscenes.list` | Cutscene — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.cutscenes.detail` | Cutscene — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.cutscenes.editor` | Cutscene — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.cutscenes.history` | Cutscene — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.cutscenes.bulk` | Cutscene — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.cutscenes.promote` | Cutscene — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.tutorials.list` | Hướng dẫn — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.tutorials.detail` | Hướng dẫn — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.tutorials.editor` | Hướng dẫn — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.tutorials.history` | Hướng dẫn — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.tutorials.bulk` | Hướng dẫn — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.tutorials.promote` | Hướng dẫn — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.loc_keys.list` | Key bản dịch — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.loc_keys.detail` | Key bản dịch — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.loc_keys.editor` | Key bản dịch — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.loc_keys.history` | Key bản dịch — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.loc_keys.bulk` | Key bản dịch — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.loc_keys.promote` | Key bản dịch — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.audio_cues.list` | Âm thanh — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.audio_cues.detail` | Âm thanh — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.audio_cues.editor` | Âm thanh — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.audio_cues.history` | Âm thanh — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.audio_cues.bulk` | Âm thanh — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.audio_cues.promote` | Âm thanh — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.vfx.list` | Hiệu ứng hình ảnh — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.vfx.detail` | Hiệu ứng hình ảnh — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.vfx.editor` | Hiệu ứng hình ảnh — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.vfx.history` | Hiệu ứng hình ảnh — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.vfx.bulk` | Hiệu ứng hình ảnh — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.vfx.promote` | Hiệu ứng hình ảnh — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.sprite_sets.list` | Bộ sprite — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.sprite_sets.detail` | Bộ sprite — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.sprite_sets.editor` | Bộ sprite — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.sprite_sets.history` | Bộ sprite — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.sprite_sets.bulk` | Bộ sprite — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.sprite_sets.promote` | Bộ sprite — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.ui_themes.list` | Chủ đề UI — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.ui_themes.detail` | Chủ đề UI — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.ui_themes.editor` | Chủ đề UI — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.ui_themes.history` | Chủ đề UI — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.ui_themes.bulk` | Chủ đề UI — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.ui_themes.promote` | Chủ đề UI — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |
| `studio.content_entities.game_modes.list` | Chế độ chơi — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | launch |
| `studio.content_entities.game_modes.detail` | Chế độ chơi — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | launch |
| `studio.content_entities.game_modes.editor` | Chế độ chơi — Soạn thảo | Form theo JSON Schema + xem trước | creator | launch |
| `studio.content_entities.game_modes.history` | Chế độ chơi — Lịch sử | Phiên bản, diff, hoàn tác | creator | launch |
| `studio.content_entities.game_modes.bulk` | Chế độ chơi — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | launch |
| `studio.content_entities.game_modes.promote` | Chế độ chơi — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | launch |

### Creator Studio (web) › expansion_content_entities

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `studio.expansion_content_entities.advanced_classes.list` | Lớp nâng cao — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.advanced_classes.detail` | Lớp nâng cao — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.advanced_classes.editor` | Lớp nâng cao — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.advanced_classes.history` | Lớp nâng cao — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.advanced_classes.bulk` | Lớp nâng cao — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.advanced_classes.promote` | Lớp nâng cao — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.talent_trees.list` | Cây thiên phú — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.talent_trees.detail` | Cây thiên phú — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.talent_trees.editor` | Cây thiên phú — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.talent_trees.history` | Cây thiên phú — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.talent_trees.bulk` | Cây thiên phú — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.talent_trees.promote` | Cây thiên phú — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.talent_nodes.list` | Nút thiên phú — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.talent_nodes.detail` | Nút thiên phú — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.talent_nodes.editor` | Nút thiên phú — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.talent_nodes.history` | Nút thiên phú — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.talent_nodes.bulk` | Nút thiên phú — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.talent_nodes.promote` | Nút thiên phú — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.festivals.list` | Lễ hội — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.festivals.detail` | Lễ hội — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.festivals.editor` | Lễ hội — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.festivals.history` | Lễ hội — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.festivals.bulk` | Lễ hội — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.festivals.promote` | Lễ hội — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.festival_activities.list` | Hoạt động lễ hội — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.festival_activities.detail` | Hoạt động lễ hội — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.festival_activities.editor` | Hoạt động lễ hội — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.festival_activities.history` | Hoạt động lễ hội — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.festival_activities.bulk` | Hoạt động lễ hội — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.festival_activities.promote` | Hoạt động lễ hội — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.minigames.list` | Minigame — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.minigames.detail` | Minigame — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.minigames.editor` | Minigame — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.minigames.history` | Minigame — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.minigames.bulk` | Minigame — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.minigames.promote` | Minigame — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.puzzles.list` | Câu đố — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.puzzles.detail` | Câu đố — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.puzzles.editor` | Câu đố — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.puzzles.history` | Câu đố — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.puzzles.bulk` | Câu đố — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.puzzles.promote` | Câu đố — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.puzzle_packs.list` | Bộ câu đố — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.puzzle_packs.detail` | Bộ câu đố — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.puzzle_packs.editor` | Bộ câu đố — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.puzzle_packs.history` | Bộ câu đố — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.puzzle_packs.bulk` | Bộ câu đố — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.puzzle_packs.promote` | Bộ câu đố — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.weather_patterns.list` | Mẫu thời tiết — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.weather_patterns.detail` | Mẫu thời tiết — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.weather_patterns.editor` | Mẫu thời tiết — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.weather_patterns.history` | Mẫu thời tiết — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.weather_patterns.bulk` | Mẫu thời tiết — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.weather_patterns.promote` | Mẫu thời tiết — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.seasons.list` | Mùa — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.seasons.detail` | Mùa — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.seasons.editor` | Mùa — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.seasons.history` | Mùa — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.seasons.bulk` | Mùa — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.seasons.promote` | Mùa — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.research_entries.list` | Mục nghiên cứu — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.research_entries.detail` | Mục nghiên cứu — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.research_entries.editor` | Mục nghiên cứu — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.research_entries.history` | Mục nghiên cứu — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.research_entries.bulk` | Mục nghiên cứu — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.research_entries.promote` | Mục nghiên cứu — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.wardrobe_items.list` | Trang phục — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.wardrobe_items.detail` | Trang phục — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.wardrobe_items.editor` | Trang phục — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.wardrobe_items.history` | Trang phục — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.wardrobe_items.bulk` | Trang phục — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.wardrobe_items.promote` | Trang phục — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.dyes.list` | Màu nhuộm — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.dyes.detail` | Màu nhuộm — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.dyes.editor` | Màu nhuộm — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.dyes.history` | Màu nhuộm — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.dyes.bulk` | Màu nhuộm — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.dyes.promote` | Màu nhuộm — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.emotes.list` | Biểu cảm — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.emotes.detail` | Biểu cảm — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.emotes.editor` | Biểu cảm — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.emotes.history` | Biểu cảm — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.emotes.bulk` | Biểu cảm — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.emotes.promote` | Biểu cảm — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.voice_packs.list` | Gói giọng — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.voice_packs.detail` | Gói giọng — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.voice_packs.editor` | Gói giọng — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.voice_packs.history` | Gói giọng — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.voice_packs.bulk` | Gói giọng — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.voice_packs.promote` | Gói giọng — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.museum_props.list` | Đạo cụ diorama — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.museum_props.detail` | Đạo cụ diorama — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.museum_props.editor` | Đạo cụ diorama — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.museum_props.history` | Đạo cụ diorama — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.museum_props.bulk` | Đạo cụ diorama — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.museum_props.promote` | Đạo cụ diorama — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.treasure_routes.list` | Tuyến kho báu — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.treasure_routes.detail` | Tuyến kho báu — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.treasure_routes.editor` | Tuyến kho báu — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.treasure_routes.history` | Tuyến kho báu — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.treasure_routes.bulk` | Tuyến kho báu — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.treasure_routes.promote` | Tuyến kho báu — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.bard_templates.list` | Mẫu Bard — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.bard_templates.detail` | Mẫu Bard — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.bard_templates.editor` | Mẫu Bard — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.bard_templates.history` | Mẫu Bard — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.bard_templates.bulk` | Mẫu Bard — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.bard_templates.promote` | Mẫu Bard — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.story_votes.list` | Bỏ phiếu cốt truyện — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.story_votes.detail` | Bỏ phiếu cốt truyện — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.story_votes.editor` | Bỏ phiếu cốt truyện — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.story_votes.history` | Bỏ phiếu cốt truyện — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.story_votes.bulk` | Bỏ phiếu cốt truyện — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.story_votes.promote` | Bỏ phiếu cốt truyện — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.world_goals.list` | Mục tiêu thế giới — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.world_goals.detail` | Mục tiêu thế giới — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.world_goals.editor` | Mục tiêu thế giới — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.world_goals.history` | Mục tiêu thế giới — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.world_goals.bulk` | Mục tiêu thế giới — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.world_goals.promote` | Mục tiêu thế giới — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.echo_glyphs.list` | Dấu ấn — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.echo_glyphs.detail` | Dấu ấn — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.echo_glyphs.editor` | Dấu ấn — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.echo_glyphs.history` | Dấu ấn — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.echo_glyphs.bulk` | Dấu ấn — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.echo_glyphs.promote` | Dấu ấn — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.highlight_rules.list` | Luật highlight — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.highlight_rules.detail` | Luật highlight — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.highlight_rules.editor` | Luật highlight — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.highlight_rules.history` | Luật highlight — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.highlight_rules.bulk` | Luật highlight — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.highlight_rules.promote` | Luật highlight — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.rift_templates.list` | Mẫu Vết Nứt — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.rift_templates.detail` | Mẫu Vết Nứt — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.rift_templates.editor` | Mẫu Vết Nứt — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.rift_templates.history` | Mẫu Vết Nứt — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.rift_templates.bulk` | Mẫu Vết Nứt — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.rift_templates.promote` | Mẫu Vết Nứt — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.rift_modifiers.list` | Biến đổi Vết Nứt — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.rift_modifiers.detail` | Biến đổi Vết Nứt — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.rift_modifiers.editor` | Biến đổi Vết Nứt — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.rift_modifiers.history` | Biến đổi Vết Nứt — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.rift_modifiers.bulk` | Biến đổi Vết Nứt — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.rift_modifiers.promote` | Biến đổi Vết Nứt — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.dream_dungeons.list` | Hầm ngục giấc mơ — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.dream_dungeons.detail` | Hầm ngục giấc mơ — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.dream_dungeons.editor` | Hầm ngục giấc mơ — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.dream_dungeons.history` | Hầm ngục giấc mơ — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.dream_dungeons.bulk` | Hầm ngục giấc mơ — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.dream_dungeons.promote` | Hầm ngục giấc mơ — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.soul_cards.list` | Thẻ linh hồn — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.soul_cards.detail` | Thẻ linh hồn — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.soul_cards.editor` | Thẻ linh hồn — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.soul_cards.history` | Thẻ linh hồn — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.soul_cards.bulk` | Thẻ linh hồn — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.soul_cards.promote` | Thẻ linh hồn — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.card_keywords.list` | Từ khóa thẻ — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.card_keywords.detail` | Từ khóa thẻ — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.card_keywords.editor` | Từ khóa thẻ — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.card_keywords.history` | Từ khóa thẻ — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.card_keywords.bulk` | Từ khóa thẻ — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.card_keywords.promote` | Từ khóa thẻ — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.starter_decks.list` | Bộ bài khởi đầu — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.starter_decks.detail` | Bộ bài khởi đầu — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.starter_decks.editor` | Bộ bài khởi đầu — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.starter_decks.history` | Bộ bài khởi đầu — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.starter_decks.bulk` | Bộ bài khởi đầu — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.starter_decks.promote` | Bộ bài khởi đầu — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.challenge_modifiers.list` | Luật thử thách — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.challenge_modifiers.detail` | Luật thử thách — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.challenge_modifiers.editor` | Luật thử thách — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.challenge_modifiers.history` | Luật thử thách — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.challenge_modifiers.bulk` | Luật thử thách — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.challenge_modifiers.promote` | Luật thử thách — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.speedrun_categories.list` | Hạng mục speedrun — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.speedrun_categories.detail` | Hạng mục speedrun — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.speedrun_categories.editor` | Hạng mục speedrun — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.speedrun_categories.history` | Hạng mục speedrun — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.speedrun_categories.bulk` | Hạng mục speedrun — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.speedrun_categories.promote` | Hạng mục speedrun — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.relics.list` | Di vật — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.relics.detail` | Di vật — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.relics.editor` | Di vật — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.relics.history` | Di vật — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.relics.bulk` | Di vật — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.relics.promote` | Di vật — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.artifact_shards.list` | Mảnh cổ vật — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.artifact_shards.detail` | Mảnh cổ vật — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.artifact_shards.editor` | Mảnh cổ vật — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.artifact_shards.history` | Mảnh cổ vật — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.artifact_shards.bulk` | Mảnh cổ vật — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.artifact_shards.promote` | Mảnh cổ vật — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.endings.list` | Kết thúc — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.endings.detail` | Kết thúc — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.endings.editor` | Kết thúc — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.endings.history` | Kết thúc — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.endings.bulk` | Kết thúc — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.endings.promote` | Kết thúc — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.caravan_routes.list` | Tuyến buôn — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.caravan_routes.detail` | Tuyến buôn — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.caravan_routes.editor` | Tuyến buôn — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.caravan_routes.history` | Tuyến buôn — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.caravan_routes.bulk` | Tuyến buôn — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.caravan_routes.promote` | Tuyến buôn — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.trade_goods.list` | Hàng buôn — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.trade_goods.detail` | Hàng buôn — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.trade_goods.editor` | Hàng buôn — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.trade_goods.history` | Hàng buôn — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.trade_goods.bulk` | Hàng buôn — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.trade_goods.promote` | Hàng buôn — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.professions.list` | Nghề — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.professions.detail` | Nghề — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.professions.editor` | Nghề — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.professions.history` | Nghề — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.professions.bulk` | Nghề — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.professions.promote` | Nghề — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.profession_recipes.list` | Công thức nghề — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.profession_recipes.detail` | Công thức nghề — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.profession_recipes.editor` | Công thức nghề — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.profession_recipes.history` | Công thức nghề — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.profession_recipes.bulk` | Công thức nghề — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.profession_recipes.promote` | Công thức nghề — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.raid_bosses.list` | Boss raid — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.raid_bosses.detail` | Boss raid — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.raid_bosses.editor` | Boss raid — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.raid_bosses.history` | Boss raid — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.raid_bosses.bulk` | Boss raid — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.raid_bosses.promote` | Boss raid — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.guild_hall_props.list` | Đạo cụ sảnh bang — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.guild_hall_props.detail` | Đạo cụ sảnh bang — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.guild_hall_props.editor` | Đạo cụ sảnh bang — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.guild_hall_props.history` | Đạo cụ sảnh bang — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.guild_hall_props.bulk` | Đạo cụ sảnh bang — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.guild_hall_props.promote` | Đạo cụ sảnh bang — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.mounts.list` | Thú cưỡi — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.mounts.detail` | Thú cưỡi — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.mounts.editor` | Thú cưỡi — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.mounts.history` | Thú cưỡi — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.mounts.bulk` | Thú cưỡi — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.mounts.promote` | Thú cưỡi — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.npc_schedules.list` | Lịch NPC — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.npc_schedules.detail` | Lịch NPC — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.npc_schedules.editor` | Lịch NPC — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.npc_schedules.history` | Lịch NPC — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.npc_schedules.bulk` | Lịch NPC — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.npc_schedules.promote` | Lịch NPC — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.bounties.list` | Truy nã — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.bounties.detail` | Truy nã — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.bounties.editor` | Truy nã — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.bounties.history` | Truy nã — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.bounties.bulk` | Truy nã — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.bounties.promote` | Truy nã — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |
| `studio.expansion_content_entities.dual_world_links.list` | Liên kết hai thế giới — Danh sách | Tìm, lọc, kiểm tra lỗi | creator | s1 |
| `studio.expansion_content_entities.dual_world_links.detail` | Liên kết hai thế giới — Chi tiết | Xem đầy đủ + nơi được tham chiếu | creator | s1 |
| `studio.expansion_content_entities.dual_world_links.editor` | Liên kết hai thế giới — Soạn thảo | Form theo JSON Schema + xem trước | creator | s1 |
| `studio.expansion_content_entities.dual_world_links.history` | Liên kết hai thế giới — Lịch sử | Phiên bản, diff, hoàn tác | creator | s1 |
| `studio.expansion_content_entities.dual_world_links.bulk` | Liên kết hai thế giới — Hàng loạt | Nhập/xuất bảng tính, sửa nhiều | creator | s1 |
| `studio.expansion_content_entities.dual_world_links.promote` | Liên kết hai thế giới — Đẩy môi trường | So sánh dev→staging→prod và đẩy | creator | s1 |

### Web Console (admin/liveops/support/BI) › expansion_ops_entities

| ScreenId | Tên | Mục đích | Vai trò | Mùa |
|---|---|---|---|---|
| `console.expansion_ops_entities.festival_schedules.list` | Lịch lễ hội — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.festival_schedules.detail` | Lịch lễ hội — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.festival_schedules.editor` | Lịch lễ hội — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.festival_schedules.history` | Lịch lễ hội — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.festival_schedules.bulk` | Lịch lễ hội — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.world_events_live.list` | Sự kiện thế giới trực tiếp — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.world_events_live.detail` | Sự kiện thế giới trực tiếp — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.world_events_live.editor` | Sự kiện thế giới trực tiếp — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.world_events_live.history` | Sự kiện thế giới trực tiếp — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.world_events_live.bulk` | Sự kiện thế giới trực tiếp — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.bard_policies.list` | Chính sách Bard — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.bard_policies.detail` | Chính sách Bard — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.bard_policies.editor` | Chính sách Bard — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.bard_policies.history` | Chính sách Bard — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.bard_policies.bulk` | Chính sách Bard — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.story_vote_schedules.list` | Lịch bỏ phiếu — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.story_vote_schedules.detail` | Lịch bỏ phiếu — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.story_vote_schedules.editor` | Lịch bỏ phiếu — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.story_vote_schedules.history` | Lịch bỏ phiếu — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.story_vote_schedules.bulk` | Lịch bỏ phiếu — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.world_goal_schedules.list` | Lịch mục tiêu thế giới — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.world_goal_schedules.detail` | Lịch mục tiêu thế giới — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.world_goal_schedules.editor` | Lịch mục tiêu thế giới — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.world_goal_schedules.history` | Lịch mục tiêu thế giới — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.world_goal_schedules.bulk` | Lịch mục tiêu thế giới — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.rift_seed_schedules.list` | Lịch seed Vết Nứt — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.rift_seed_schedules.detail` | Lịch seed Vết Nứt — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.rift_seed_schedules.editor` | Lịch seed Vết Nứt — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.rift_seed_schedules.history` | Lịch seed Vết Nứt — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.rift_seed_schedules.bulk` | Lịch seed Vết Nứt — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.puzzle_schedules.list` | Lịch câu đố ngày — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.puzzle_schedules.detail` | Lịch câu đố ngày — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.puzzle_schedules.editor` | Lịch câu đố ngày — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.puzzle_schedules.history` | Lịch câu đố ngày — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.puzzle_schedules.bulk` | Lịch câu đố ngày — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.drops_campaigns.list` | Chiến dịch drops — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.drops_campaigns.detail` | Chiến dịch drops — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.drops_campaigns.editor` | Chiến dịch drops — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.drops_campaigns.history` | Chiến dịch drops — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.drops_campaigns.bulk` | Chiến dịch drops — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.streamer_accounts.list` | Tài khoản streamer — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.streamer_accounts.detail` | Tài khoản streamer — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.streamer_accounts.editor` | Tài khoản streamer — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.streamer_accounts.history` | Tài khoản streamer — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.streamer_accounts.bulk` | Tài khoản streamer — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.esports_matches.list` | Trận esports — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.esports_matches.detail` | Trận esports — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.esports_matches.editor` | Trận esports — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.esports_matches.history` | Trận esports — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.esports_matches.bulk` | Trận esports — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.mod_listings.list` | Mod đăng bán — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.mod_listings.detail` | Mod đăng bán — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.mod_listings.editor` | Mod đăng bán — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.mod_listings.history` | Mod đăng bán — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.mod_listings.bulk` | Mod đăng bán — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.creator_accounts.list` | Tài khoản creator — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.creator_accounts.detail` | Tài khoản creator — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.creator_accounts.editor` | Tài khoản creator — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.creator_accounts.history` | Tài khoản creator — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.creator_accounts.bulk` | Tài khoản creator — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.creator_payouts.list` | Chi trả creator — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.creator_payouts.detail` | Chi trả creator — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.creator_payouts.editor` | Chi trả creator — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.creator_payouts.history` | Chi trả creator — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.creator_payouts.bulk` | Chi trả creator — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.partner_accounts.list` | Tài khoản đối tác — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.partner_accounts.detail` | Tài khoản đối tác — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.partner_accounts.editor` | Tài khoản đối tác — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.partner_accounts.history` | Tài khoản đối tác — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.partner_accounts.bulk` | Tài khoản đối tác — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.partner_campaigns.list` | Chiến dịch đối tác — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.partner_campaigns.detail` | Chiến dịch đối tác — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.partner_campaigns.editor` | Chiến dịch đối tác — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.partner_campaigns.history` | Chiến dịch đối tác — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.partner_campaigns.bulk` | Chiến dịch đối tác — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.translation_projects.list` | Dự án dịch cộng đồng — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.translation_projects.detail` | Dự án dịch cộng đồng — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.translation_projects.editor` | Dự án dịch cộng đồng — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.translation_projects.history` | Dự án dịch cộng đồng — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.translation_projects.bulk` | Dự án dịch cộng đồng — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.economy_policies.list` | Chính sách kinh tế — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.economy_policies.detail` | Chính sách kinh tế — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.economy_policies.editor` | Chính sách kinh tế — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.economy_policies.history` | Chính sách kinh tế — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.economy_policies.bulk` | Chính sách kinh tế — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.market_price_bands.list` | Biên độ giá chợ — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.market_price_bands.detail` | Biên độ giá chợ — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.market_price_bands.editor` | Biên độ giá chợ — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.market_price_bands.history` | Biên độ giá chợ — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.market_price_bands.bulk` | Biên độ giá chợ — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.ironman_leagues.list` | Giải Thiết Nhân — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.ironman_leagues.detail` | Giải Thiết Nhân — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.ironman_leagues.editor` | Giải Thiết Nhân — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.ironman_leagues.history` | Giải Thiết Nhân — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.ironman_leagues.bulk` | Giải Thiết Nhân — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.speedrun_submissions.list` | Bài nộp speedrun — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.speedrun_submissions.detail` | Bài nộp speedrun — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.speedrun_submissions.editor` | Bài nộp speedrun — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.speedrun_submissions.history` | Bài nộp speedrun — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.speedrun_submissions.bulk` | Bài nộp speedrun — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.raid_schedules.list` | Lịch raid — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.raid_schedules.detail` | Lịch raid — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.raid_schedules.editor` | Lịch raid — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.raid_schedules.history` | Lịch raid — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.raid_schedules.bulk` | Lịch raid — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.gm_playbooks.list` | Kịch bản GM — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.gm_playbooks.detail` | Kịch bản GM — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.gm_playbooks.editor` | Kịch bản GM — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.gm_playbooks.history` | Kịch bản GM — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.gm_playbooks.bulk` | Kịch bản GM — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.compensation_packages.list` | Gói bồi thường — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.compensation_packages.detail` | Gói bồi thường — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.compensation_packages.editor` | Gói bồi thường — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.compensation_packages.history` | Gói bồi thường — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.compensation_packages.bulk` | Gói bồi thường — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.mentor_programs.list` | Chương trình sư phụ — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.mentor_programs.detail` | Chương trình sư phụ — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.mentor_programs.editor` | Chương trình sư phụ — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.mentor_programs.history` | Chương trình sư phụ — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.mentor_programs.bulk` | Chương trình sư phụ — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.gift_catalog.list` | Danh mục quà tặng — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.gift_catalog.detail` | Danh mục quà tặng — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.gift_catalog.editor` | Danh mục quà tặng — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.gift_catalog.history` | Danh mục quà tặng — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.gift_catalog.bulk` | Danh mục quà tặng — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
| `console.expansion_ops_entities.family_policies.list` | Chính sách tài khoản gia đình — Danh sách | Tìm, lọc, sắp xếp | admin | s2 |
| `console.expansion_ops_entities.family_policies.detail` | Chính sách tài khoản gia đình — Chi tiết | Xem đầy đủ + trạng thái | admin | s2 |
| `console.expansion_ops_entities.family_policies.editor` | Chính sách tài khoản gia đình — Soạn thảo | Tạo/sửa có kiểm tra hợp lệ | admin | s2 |
| `console.expansion_ops_entities.family_policies.history` | Chính sách tài khoản gia đình — Lịch sử | Phiên bản, người sửa, diff | admin | s2 |
| `console.expansion_ops_entities.family_policies.bulk` | Chính sách tài khoản gia đình — Hàng loạt | Nhập/xuất CSV, sửa nhiều bản ghi | admin | s2 |
