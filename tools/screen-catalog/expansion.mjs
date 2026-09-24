export const EXPANSION_SURFACES = {
  companion: "Companion App (PWA mobile)",
  stream: "Stream Suite (streamer dashboard + viewer extension)",
  observer: "Esports Observer (caster/observer)",
  workshop: "Workshop (mod + creator marketplace)",
  locportal: "Community Translation Portal",
  partner: "Partner Portal (influencer/affiliate/creator program)",
  gm: "Live Game Master Console",
};

export const SEASONS = {
  launch: "Launch v1.0",
  s1: "Mùa 1 — Lễ Hội Đèn Lồng",
  s2: "Mùa 2 — Biên Niên Sử",
  s3: "Mùa 3 — Vết Nứt Thời Gian",
  s4: "Mùa 4 — Bên Kia Bình Minh",
};

export const expansionGroups = [
  {
    season: "s1", surface: "game", module: "festivals", roles: ["gamer", "pilot"], screens: `
festival_calendar|Lịch lễ hội|Lễ hội theo âm lịch: Tết, Trung Thu, Hội Đèn, Đoan Ngọ
festival_hub|Quảng trường lễ hội|Làng đổi trang trí, NPC, nhạc theo lễ hội
lantern_craft|Làm đèn lồng|Ghép hình + màu, đèn thành vật trang trí
lantern_release|Thả đèn|Gửi lời chúc; đèn bay trên map của người chơi khác
lucky_envelope|Lì xì|Tặng lì xì vàng cho bạn bè (giới hạn chống lạm phát)
mooncake_kitchen|Bếp bánh trung thu|Minigame nấu theo công thức
lion_dance|Múa lân|Minigame nhịp điệu (input thành command tất định)
dragon_boat|Đua thuyền rồng|Minigame phối hợp đội
festival_quests|Nhiệm vụ lễ hội|Chuỗi nhiệm vụ theo lễ
festival_shop|Chợ phiên|Cửa hàng token lễ hội
festival_album|Sổ lưu niệm lễ hội|Kỷ vật đã sưu tầm mỗi năm
festival_rewards|Quà lễ hội|Mốc quà theo điểm tham gia
`},
  {
    season: "s1", surface: "game", module: "class_evolution", roles: ["gamer", "pilot"], screens: `
evolution_tree|Cây tiến hóa lớp|6 lớp gốc → 12 lớp nâng cao
evolution_path_detail|Chi tiết nhánh|So sánh 2 nhánh của một lớp
evolution_trial|Thử thách thăng lớp|Trận thử thách riêng mỗi nhánh
evolution_confirm|Xác nhận thăng lớp|Chọn nhánh, cảnh báo đổi nhánh tốn phí
evolution_ceremony|Lễ thăng lớp|Cutscene đổi ngoại hình/kỹ năng
evolution_respec|Đổi nhánh|Hoàn nguyên tài nguyên có phí
evolution_counter_chart|Bảng khắc chế nâng cao|Vòng khắc chế 12 lớp
evolution_codex|Bách khoa lớp nâng cao|Lore từng lớp
`},
  {
    season: "s1", surface: "game", module: "academy", roles: ["gamer", "pilot"], screens: `
academy_home|Học viện anh hùng|Trung tâm huấn luyện
talent_tree|Cây thiên phú|Điểm thiên phú theo cấp
talent_node_detail|Chi tiết thiên phú|Hiệu ứng từng nút
talent_reset|Tẩy điểm thiên phú|Hoàn điểm có phí
talent_presets|Bộ thiên phú lưu sẵn|Đổi nhanh theo chế độ
training_dummy|Hình nộm tập luyện|Thử DPS trong sandbox tất định
sparring|Đấu tập|Đấu 2 đội của mình, không mất gì
lesson_library|Thư viện bài học|Bài học chiến thuật tương tác
lesson_play|Học bài|Tình huống có hướng dẫn
academy_exam|Thi tốt nghiệp|Nhận danh hiệu và thưởng
`},
  {
    season: "s1", surface: "game", module: "wardrobe", roles: ["gamer", "user", "pilot"], screens: `
wardrobe_home|Tủ đồ|Trang phục anh hùng
skin_detail|Chi tiết trang phục|Xem 360°, hoạt ảnh riêng
dye_studio|Xưởng nhuộm|Đổi palette sprite bằng shader
dye_collection|Bộ màu nhuộm|Màu đã mở khóa
outfit_presets|Bộ phối đồ|Lưu phối đồ
skin_try_on|Thử đồ|Thử trong trận mẫu
emote_collection|Bộ biểu cảm|Emote đã có
voice_pack_select|Gói giọng nói|Đổi giọng anh hùng
`},
  {
    season: "s1", surface: "game", module: "research", roles: ["gamer", "pilot"], screens: `
research_board|Bảng nghiên cứu quái|Tiến độ nghiên cứu mỗi loài
research_entry|Hồ sơ loài|Điểm yếu mở theo số lần đánh bại/quan sát
field_notes|Sổ tay thực địa|Ghi chú tự động từ BattleEvent
research_milestone|Mốc nghiên cứu|Mở khóa gợi ý chiến thuật
habitat_map|Bản đồ môi trường sống|Quái xuất hiện ở đâu, khi nào
specimen_gallery|Bộ sưu tập mẫu vật|Mô hình quái 3 sao
`},
  {
    season: "s1", surface: "game", module: "puzzles", roles: ["gamer", "pilot"], screens: `
puzzle_home|Câu đố chiến thuật|Thắng trong N lượt với đội cho sẵn
puzzle_daily|Câu đố hằng ngày|Một câu đố chung toàn server
puzzle_pack_list|Bộ câu đố|Theo chủ đề/độ khó
puzzle_play|Giải câu đố|Trận có giới hạn lượt, hoàn tác không giới hạn
puzzle_hint|Gợi ý|Lời giải từng bước (tốn điểm gợi ý)
puzzle_result|Kết quả câu đố|Số lượt, sao, thời gian
puzzle_leaderboard|Xếp hạng câu đố|Lời giải ít lượt nhất
puzzle_share|Chia sẻ lời giải|Mã replay của lời giải
`},
  {
    season: "s1", surface: "game", module: "seasons_weather", roles: ["gamer", "pilot"], screens: `
season_overview|Mùa trong năm|Xuân/Hạ/Thu/Đông đổi theo tuần thật
season_effects|Hiệu ứng mùa|Buff/debuff theo mùa lên lớp nhân vật
weather_forecast|Dự báo thời tiết|Thời tiết 3 ngày tới trên mỗi vùng
weather_battle_preview|Thời tiết trong trận|Mưa giảm lửa, sương tăng né…
season_collectibles|Vật phẩm theo mùa|Chỉ thu được trong mùa
season_journal|Nhật ký bốn mùa|Hoạt động đã làm trong mùa
`},
  {
    season: "s1", surface: "game", module: "accessibility_plus", roles: ["gamer", "user"], screens: `
one_hand_mode|Chế độ một tay|Bố cục nút cho một tay
colorblind_preview|Xem trước chế độ mù màu|3 bộ lọc + palette an toàn
text_to_speech|Đọc văn bản|Đọc thoại/menu bằng TTS nền tảng
battle_pace_assist|Trợ giúp nhịp độ|Tắt giới hạn thời gian, gợi ý mục tiêu
motion_comfort|Giảm chuyển động|Tắt rung, flash, parallax
input_remap_gamepad|Gán nút tay cầm|Hỗ trợ gamepad đầy đủ
subtitle_style|Kiểu phụ đề|Cỡ, nền, màu người nói
difficulty_assist|Hỗ trợ độ khó|Chế độ kể chuyện
`},
  {
    season: "s2", surface: "game", module: "chronicle", roles: ["gamer", "pilot"], screens: `
chronicle_home|Biên niên sử|Nhật ký hành trình sinh từ DomainEvent
chronicle_chapter|Chương biên niên|Tóm tắt từng chương đã chơi
chronicle_bard|Người kể chuyện|Bard viết lại hành trình thành truyện (AI, có kiểm duyệt)
chronicle_export|Xuất biên niên|Xuất thành sách ảnh/PDF
rewind_prompt|Tua ngược lượt|Dùng Cát Thời Gian quay lại 1 lượt (replay tất định)
battle_timeline_branch|Nhánh dòng thời gian|Xem các nhánh đã thử trong một trận
what_if_sandbox|Giả định "Nếu như"|Rẽ nhánh từ một lượt bất kỳ trong replay
ghost_compare|So với bóng ma|Chạy song song lời giải của người khác
time_sand_wallet|Cát Thời Gian|Nguồn và số dư tài nguyên tua ngược
chronicle_milestones|Cột mốc|Khoảnh khắc đáng nhớ được đánh dấu tự động
`},
  {
    season: "s2", surface: "game", module: "theater", roles: ["gamer", "pilot"], screens: `
theater_home|Nhà hát replay|Highlight tự động từ BattleEvent
highlight_reel|Cuộn highlight|Clip chí mạng, lật kèo, one-shot
highlight_editor|Sửa highlight|Cắt, tốc độ, camera
highlight_share|Chia sẻ highlight|Xuất GIF/MP4 hoặc mã replay
theater_featured|Replay nổi bật|Tuyển chọn cộng đồng
theater_commentary|Bình luận replay|Bình luận theo mốc thời gian
director_camera|Camera đạo diễn|Góc máy tự do khi phát lại
theater_playlist|Danh sách phát|Gom replay theo chủ đề
`},
  {
    season: "s2", surface: "game", module: "world_voice", roles: ["gamer", "pilot"], screens: `
story_vote_home|Bỏ phiếu cốt truyện|Toàn server chọn hướng đi sự kiện mùa
story_vote_detail|Chi tiết lựa chọn|Hệ quả dự kiến của từng phương án
story_vote_result|Kết quả bỏ phiếu|Tỉ lệ và cutscene kết quả
world_goal_home|Mục tiêu thế giới|Cả server cùng góp tài nguyên xây cầu, đẩy lùi bóng tối
world_goal_contribute|Đóng góp mục tiêu|Góp vật phẩm/trận thắng
world_goal_rewards|Thưởng mục tiêu thế giới|Mốc thưởng chung
world_state_map|Bản đồ trạng thái thế giới|Vùng bị bóng tối lan/đẩy lùi theo hoạt động toàn server
echo_glyph_place|Khắc dấu ấn|Để lại ký hiệu gợi ý cho người chơi khác (từ vựng giới hạn)
echo_glyph_read|Đọc dấu ấn|Xem, đánh giá dấu ấn
echo_ghost_path|Vệt bóng ma|Thấy đường đi của người chơi khác trên map
`},
  {
    season: "s3", surface: "game", module: "rift", roles: ["gamer", "pilot"], screens: `
rift_home|Vết Nứt|Hầm ngục sinh thủ tục theo seed hằng ngày
rift_seed_select|Chọn seed|Seed ngày, seed tuần, seed tùy chỉnh
rift_floor_map|Bản đồ tầng|Phòng sinh từ tileset + luật ghép
rift_room_event|Phòng sự kiện|Sự kiện ngẫu nhiên tất định
rift_modifier_select|Chọn biến đổi|Chấp nhận biến đổi khó để tăng thưởng
rift_treasure|Kho báu Vết Nứt|Chọn 1 trong 3 phần thưởng
rift_merchant|Thương nhân lạc lối|Mua bằng mảnh vỡ tạm thời
rift_boss_gate|Cổng boss|Boss ghép từ pha ngẫu nhiên
rift_summary|Tổng kết Vết Nứt|Điểm, tầng, seed để chia sẻ
rift_leaderboard|Xếp hạng Vết Nứt|Theo seed
`},
  {
    season: "s3", surface: "game", module: "dreams", roles: ["gamer", "pilot"], screens: `
dream_gate|Cổng giấc mơ|Mỗi anh hùng có một hầm ngục ký ức
dream_map|Bản đồ giấc mơ|Map đảo màu/đảo chiều từ map gốc
dream_memory|Mảnh ký ức|Cảnh truyện mở khóa
dream_nightmare|Ác mộng|Phiên bản khó của boss từng đánh
dream_reward|Thưởng giấc mơ|Nội tại riêng của anh hùng
dream_collection|Bộ sưu tập ký ức|Ký ức đã thu thập
dream_lucid|Giấc mơ tỉnh|Người chơi tự chọn luật trận
dream_wake|Tỉnh giấc|Tổng kết và quay về
`},
  {
    season: "s3", surface: "game", module: "soul_cards", roles: ["gamer", "pilot"], screens: `
cards_home|Thẻ Linh Hồn|Chế độ đấu thẻ chạy trên cùng BattleEngine
card_collection|Bộ sưu tập thẻ|Thẻ rơi từ trận và nghiên cứu
card_detail|Chi tiết thẻ|Hiệu ứng, từ khóa
deck_list|Danh sách bộ bài|Bộ đã lưu
deck_builder|Xây bộ bài|30 thẻ, giới hạn theo lớp
deck_stats|Thống kê bộ bài|Đường cong năng lượng, tỉ lệ thắng
card_battle|Trận đấu thẻ|Rút thẻ thay cho chọn kỹ năng
card_mulligan|Đổi bài đầu|Chọn giữ/đổi bài
card_draft|Draft|Chọn thẻ luân phiên xây bộ tạm
card_ladder|Bậc xếp hạng thẻ|PvP bất đồng bộ
card_craft|Chế thẻ|Dùng bụi linh hồn
card_puzzles|Câu đố thẻ|Câu đố riêng cho chế độ thẻ
`},
  {
    season: "s3", surface: "game", module: "challenges", roles: ["gamer", "pilot"], screens: `
boss_rush_home|Boss liên hoàn|Đánh liên tiếp boss đã gặp
boss_rush_result|Kết quả boss liên hoàn|Thời gian, số lượt
weekly_modifiers|Luật tuần|Luật biến đổi áp dụng mọi trận trong tuần
speedrun_home|Speedrun|Hạng mục speedrun chính thức
speedrun_timer|Đồng hồ speedrun|Split theo chương, xác minh bằng replay
speedrun_board|Bảng speedrun|Top theo hạng mục
ironman_start|Chế độ Thiết Nhân|Chết là mất anh hùng vĩnh viễn
ironman_memorial|Đài tưởng niệm|Anh hùng đã ngã
ironman_leaderboard|Xếp hạng Thiết Nhân|Tiến độ xa nhất
challenge_badges|Huy hiệu thử thách|Huy hiệu hoàn thành
`},
  {
    season: "s4", surface: "game", module: "act_two", roles: ["gamer", "pilot"], screens: `
act_two_prologue|Mở đầu Hồi II|Thế giới bên kia bình minh
dual_world_toggle|Chuyển thế giới|Đổi giữa Bình Minh và Hoàng Hôn trên cùng map
dual_world_diff|Khác biệt hai thế giới|Vật cản/lối đi chỉ có ở một phía
mirror_npc|NPC phản chiếu|Phiên bản ngược của NPC cũ
act_two_region_map|Bản đồ Hồi II|6 vùng mới
artifact_shards|Mảnh cổ vật|12 mảnh (6 Hồi I + 6 Hồi II)
artifact_forge|Rèn cổ vật|Ghép mảnh mở quyền năng toàn đội
relic_list|Di vật|Di vật trang bị cho đội
relic_detail|Chi tiết di vật|Hiệu ứng, lore
relic_upgrade|Nâng di vật|Nâng cấp
ending_gallery|Thư viện kết thúc|6 kết thúc đã mở
true_ending_path|Con đường kết thúc thật|Điều kiện kết thúc thứ 6
`},
  {
    season: "s4", surface: "game", module: "caravan", roles: ["gamer", "pilot"], screens: `
caravan_home|Đoàn buôn|Tuyến buôn giữa các vùng
caravan_routes|Tuyến đường|Tuyến đã mở, rủi ro, lợi nhuận
caravan_load|Chất hàng|Chọn hàng theo sức chứa
caravan_escort|Hộ tống|Chọn đội hộ tống (trận phục kích)
caravan_travel|Hành trình|Tiến độ thời gian thực
caravan_ambush|Phục kích|Trận khi bị chặn đường
caravan_market_prices|Giá vùng|Giá thay đổi theo cung cầu toàn server
caravan_ledger|Sổ buôn|Lãi/lỗ từng chuyến
caravan_upgrade|Nâng đoàn buôn|Xe, ngựa, bảo vệ
`},
  {
    season: "s4", surface: "game", module: "professions", roles: ["gamer", "pilot"], screens: `
profession_hub|Nghề nghiệp|Rèn, may, luyện kim, khắc ấn, nấu ăn, trồng trọt
profession_detail|Chi tiết nghề|Cấp nghề, công thức mở
profession_workbench|Bàn làm việc|Chế tạo theo nghề
profession_orders|Đơn đặt hàng|NPC/người chơi đặt hàng
profession_mastery|Tinh thông|Chọn chuyên môn
farm_plots|Ruộng vườn|Trồng nguyên liệu theo mùa
farm_harvest|Thu hoạch|Kết quả thu hoạch
alchemy_lab|Phòng luyện kim|Pha thuốc thử nghiệm công thức ẩn
`},
  {
    season: "s4", surface: "game", module: "raids", roles: ["gamer", "pilot"], screens: `
raid_home|Đột kích|Raid 4 đội × 9 anh hùng, bất đồng bộ
raid_boss_intel|Tình báo boss|Cơ chế từng pha
raid_squad_assign|Phân đội|Gán anh hùng cho 4 đội
raid_attempt|Đánh raid|Mỗi đội một lượt tấn công
raid_damage_board|Bảng sát thương|Đóng góp của từng thành viên
raid_loot_council|Chia chiến lợi phẩm|Bỏ phiếu chia đồ
raid_history|Lịch sử raid|Các lần đánh
`},
  {
    season: "s4", surface: "game", module: "guild_hall", roles: ["gamer"], screens: `
guild_hall|Sảnh bang|Nhà chung trang trí được
guild_hall_decorate|Trang trí sảnh|Đặt vật phẩm
guild_trophy_room|Phòng cúp|Cúp raid, giải đấu
guild_research|Nghiên cứu bang|Buff chung
guild_banner_editor|Thiết kế cờ bang|Ghép biểu tượng/màu
guild_events|Sự kiện bang|Lịch hoạt động nội bộ
`},
  {
    season: "s4", surface: "game", module: "mounts_travel", roles: ["gamer", "pilot"], screens: `
mount_stable|Chuồng thú cưỡi|Thú cưỡi sở hữu
mount_detail|Chi tiết thú cưỡi|Tốc độ, địa hình
mount_train|Huấn luyện thú cưỡi|Tăng chỉ số
mount_race|Đua thú cưỡi|Minigame đua
airship_dock|Bến khí cầu|Di chuyển giữa Hồi I và Hồi II
travel_journal|Nhật ký lữ hành|Quãng đường, nơi đã qua
`},
  {
    season: "s4", surface: "game", module: "mentorship", roles: ["gamer"], screens: `
mentor_home|Sư phụ & đệ tử|Kết nối người chơi lâu năm với người mới
mentor_find|Tìm sư phụ|Ghép theo giờ chơi, ngôn ngữ
mentor_tasks|Nhiệm vụ thầy trò|Nhiệm vụ làm chung
mentor_graduation|Xuất sư|Thưởng cho cả hai
mentor_rating|Đánh giá sư phụ|Phản hồi chất lượng
mentor_hall|Sảnh sư phụ|Sư phụ tiêu biểu
`},
  {
    season: "s4", surface: "game", module: "npc_life", roles: ["gamer", "pilot"], screens: `
npc_schedule|Lịch sinh hoạt NPC|NPC đi lại theo giờ trong game
npc_relationship|Quan hệ NPC|Mức thân thiết với NPC
npc_gift|Tặng quà NPC|Sở thích từng NPC
npc_story|Truyện NPC|Chuỗi truyện riêng
bounty_board|Bảng truy nã|Nhiệm vụ săn quái do NPC treo
bounty_detail|Chi tiết truy nã|Mục tiêu, vị trí gợi ý
bounty_turn_in|Nộp truy nã|Nhận thưởng
town_gossip|Tin đồn|Gợi ý bí mật từ NPC
`},
  {
    season: "s4", surface: "game", module: "cross_play", roles: ["gamer", "user"], screens: `
cross_progress_link|Liên kết tiến trình|Chơi tiếp trên thiết bị khác
cross_device_list|Thiết bị liên kết|Quản lý thiết bị
cross_handoff|Chuyển thiết bị|Mã QR tiếp tục trên máy khác
cross_play_settings|Cài đặt chơi chéo|Ghép trận chéo nền tảng
cloud_save_history|Lịch sử cloud save|Khôi phục bản cũ
`},
  {
    season: "s1", surface: "game", module: "museum", roles: ["gamer", "pilot"], screens: `
hero_museum|Bảo tàng anh hùng|Trưng bày anh hùng + trang phục
diorama_editor|Dựng diorama|Sắp đặt cảnh với nhân vật, đạo cụ
diorama_gallery|Triển lãm diorama|Diorama của cộng đồng
diorama_like|Bình chọn diorama|Thích, lưu
museum_visitors|Khách tham quan|Ai đã xem bảo tàng
treasure_hunt|Truy tìm kho báu|Câu đố bản đồ theo mảnh giấy
treasure_map_piece|Mảnh bản đồ kho báu|Ghép mảnh
treasure_dig|Đào kho báu|Kết quả
lore_quiz|Đố vui truyền thuyết|Câu hỏi về thế giới
`},
  {
    season: "s2", surface: "game", module: "stream_interact", roles: ["gamer"], screens: `
stream_link|Liên kết kênh stream|Kết nối Twitch/YouTube
stream_mode|Chế độ streamer|Ẩn thông tin cá nhân, overlay an toàn
viewer_vote_live|Khán giả bỏ phiếu|Khán giả chọn kỹ năng/nhánh
viewer_gift_receive|Quà khán giả|Nhận buff vui do khán giả tặng
stream_recap|Tổng kết buổi stream|Highlight, lượt tương tác
`},
  {
    season: "s1", surface: "companion", module: "companion", roles: ["gamer", "user", "pilot"], screens: `
companion_login|Đăng nhập companion|Dùng tài khoản game
companion_home|Trang chủ companion|Tình trạng làng, năng lượng, thám hiểm
companion_notifications|Thông báo|Thám hiểm xong, năng lượng đầy
companion_expeditions|Thám hiểm|Gửi/nhận thám hiểm từ điện thoại
companion_village|Làng|Thu hoạch tài nguyên
companion_farm|Ruộng vườn|Tưới, thu hoạch
companion_caravan|Đoàn buôn|Theo dõi chuyến buôn
companion_mail|Hộp thư|Nhận quà
companion_chat_guild|Chat bang|Chat khi không vào game
companion_chat_private|Chat riêng|Tin nhắn bạn bè
companion_friends|Bạn bè|Danh sách, lời mời
companion_guild|Bang hội|Thông báo bang, điểm danh bang
companion_raid_plan|Kế hoạch raid|Phân đội raid trên điện thoại
companion_heroes|Anh hùng|Xem chỉ số, trang bị
companion_hero_detail|Chi tiết anh hùng|Build hiện tại
companion_lineup_planner|Lên đội hình|Soạn đội hình, đồng bộ vào game
companion_build_planner|Lên build|Thiên phú, trang bị giả định
companion_codex|Bách khoa|Tra cứu offline
companion_map|Bản đồ thế giới|Bản đồ tương tác, đánh dấu
companion_events|Sự kiện|Lịch sự kiện, nhắc nhở
companion_festival|Lễ hội|Hoạt động lễ hội ngoài game
companion_checkin|Điểm danh|Điểm danh từ điện thoại
companion_shop|Cửa hàng|Mua gói (web payment nơi được phép)
companion_market|Chợ|Theo dõi giá, đặt lệnh
companion_leaderboards|Xếp hạng|Các bảng xếp hạng
companion_replays|Replay|Xem replay trên điện thoại
companion_highlights|Highlight|Clip của tôi
companion_puzzle_daily|Câu đố ngày|Giải câu đố ngày trên điện thoại
companion_cards_deck|Bộ bài thẻ|Sửa bộ bài
companion_research|Nghiên cứu|Tiến độ bestiary
companion_chronicle|Biên niên sử|Đọc biên niên
companion_account|Tài khoản|Bảo mật, thiết bị
companion_parental|Phụ huynh|Giới hạn thời gian/chi tiêu
companion_support|Hỗ trợ|Ticket
companion_settings|Cài đặt|Thông báo, ngôn ngữ
companion_widgets|Widget|Cấu hình widget màn hình chính
companion_qr_login|Đăng nhập bằng QR|Quét để đăng nhập máy tính
companion_2fa|Xác thực 2 lớp|Duyệt đăng nhập mới
companion_offline|Ngoại tuyến|Dữ liệu cache
companion_onboarding|Giới thiệu companion|Hướng dẫn lần đầu
`},
  {
    season: "s2", surface: "stream", module: "streamer", roles: ["gamer", "creator"], screens: `
streamer_home|Bảng điều khiển streamer|Trạng thái kết nối, khán giả
streamer_connect|Kết nối nền tảng|Twitch/YouTube OAuth
streamer_overlay_builder|Tạo overlay|Kéo thả widget overlay
streamer_overlay_preview|Xem trước overlay|Mô phỏng trên nền game
streamer_vote_config|Cấu hình bỏ phiếu|Loại phiếu khán giả được phép
streamer_vote_live|Phiếu đang chạy|Theo dõi phiếu
streamer_gift_rules|Luật quà khán giả|Buff nào được bật
streamer_safety|An toàn|Chặn spoiler, lọc từ
streamer_highlights|Highlight buổi stream|Clip tự động
streamer_analytics|Phân tích kênh|Tương tác theo buổi
streamer_drops|Drops|Phần thưởng khán giả xem stream
streamer_drops_campaign|Chiến dịch drops|Điều kiện nhận
streamer_schedule|Lịch stream|Đồng bộ lịch sự kiện game
streamer_challenges|Thử thách cho streamer|Thử thách do cộng đồng đặt
streamer_coop_invite|Mời khán giả co-op|Khán giả vào đội
streamer_settings|Cài đặt streamer|Độ trễ, quyền
`},
  {
    season: "s2", surface: "stream", module: "viewer", roles: ["gamer"], screens: `
viewer_panel|Bảng khán giả|Extension trong trang stream
viewer_vote|Bỏ phiếu|Chọn kỹ năng/nhánh
viewer_predictions|Dự đoán|Đoán kết quả trận
viewer_hero_inspect|Soi anh hùng|Xem build của streamer
viewer_gift|Tặng buff|Dùng điểm kênh tặng buff vui
viewer_leaderboard|Xếp hạng khán giả|Khán giả tích cực
viewer_drops_progress|Tiến độ drops|Thời gian xem
viewer_link_account|Liên kết tài khoản game|Nhận drops vào game
viewer_replay_moment|Xem lại khoảnh khắc|Tua lại lượt vừa xảy ra
viewer_settings|Cài đặt khán giả|Ẩn/hiện panel
`},
  {
    season: "s2", surface: "observer", module: "casting", roles: ["admin", "gamer"], screens: `
observer_lobby|Sảnh quan sát|Trận giải đấu đang diễn ra
observer_match|Quan sát trận|Góc nhìn toàn cảnh, không trễ với caster
observer_stats_overlay|Overlay thống kê|Sát thương, năng lượng, lực chiến
observer_predictions|Dự đoán thắng|Mô phỏng Monte Carlo từ trạng thái hiện tại
observer_timeline|Dòng thời gian trận|Đánh dấu sự kiện chính
observer_instant_replay|Replay tức thì|Phát lại lượt quan trọng
observer_camera_director|Đạo diễn camera|Chuyển góc nhìn cho luồng phát
observer_caster_notes|Ghi chú caster|Dữ liệu đối đầu, lịch sử
observer_head_to_head|Đối đầu|Thành tích hai đội
observer_draft_view|Xem cấm/chọn|Pha cấm chọn anh hùng
observer_bracket_overlay|Overlay nhánh đấu|Hiển thị nhánh
observer_break_screen|Màn giải lao|Quảng cáo, thống kê
observer_mvp|MVP|Chọn và hiển thị MVP
observer_broadcast_settings|Cài đặt phát sóng|Độ phân giải, trễ
observer_spoiler_delay|Trễ chống lộ|Trễ luồng công khai
observer_multi_view|Đa màn|Xem nhiều trận cùng lúc
observer_graphics_package|Gói đồ họa|Chủ đề giải đấu
observer_sponsor_slots|Vị trí nhà tài trợ|Logo, thời lượng
observer_referee|Trọng tài|Tạm dừng, xử tranh chấp
observer_match_report|Biên bản trận|Xuất biên bản
`},
  {
    season: "s3", surface: "workshop", module: "mods", roles: ["creator", "gamer"], screens: `
workshop_home|Workshop|Mod dữ liệu và level cộng đồng
mod_browse|Duyệt mod|Lọc theo loại, phiên bản
mod_detail|Chi tiết mod|Mô tả, ảnh, tương thích
mod_install|Cài mod|Tải content pack đã ký
mod_manager|Quản lý mod|Bật/tắt, thứ tự ưu tiên
mod_conflicts|Xung đột mod|Mod ghi đè cùng ID
mod_reviews|Đánh giá mod|Nhận xét cộng đồng
mod_report|Báo cáo mod|Vi phạm
mod_collections|Bộ sưu tập mod|Gói nhiều mod
mod_changelog|Nhật ký mod|Các phiên bản
creator_dashboard|Bảng điều khiển creator|Lượt tải, đánh giá, thu nhập
mod_upload|Tải lên mod|Content pack + metadata
mod_validation|Kiểm tra mod|Chạy content-compiler + quét an ninh
mod_versions|Phiên bản mod|Quản lý phát hành
mod_pricing|Định giá|Miễn phí hoặc trả bằng creator token
creator_earnings|Thu nhập creator|Doanh thu chia sẻ
creator_payout|Rút tiền|Yêu cầu thanh toán
creator_tax_info|Thông tin thuế|Hồ sơ thuế
creator_guidelines|Quy tắc creator|Nội dung được phép
creator_sdk_docs|Tài liệu SDK mod|Schema, ví dụ
mod_template_gallery|Mẫu mod|Mẫu khởi đầu
mod_playtest|Chơi thử mod|Chạy mod trong sandbox
mod_analytics|Phân tích mod|Tỉ lệ hoàn thành, gỡ cài
creator_followers|Người theo dõi|Theo dõi creator
creator_jams|Game jam|Cuộc thi làm mod theo chủ đề
creator_jam_detail|Chi tiết jam|Luật, giải thưởng
creator_jam_submit|Nộp bài jam|Nộp mod dự thi
creator_jam_results|Kết quả jam|Xếp hạng
`},
  {
    season: "s4", surface: "locportal", module: "community_translation", roles: ["creator", "gamer"], screens: `
loc_home|Cổng dịch cộng đồng|Ngôn ngữ đang cần người dịch
loc_join|Tham gia dịch|Chọn ngôn ngữ, bài test
loc_placement_test|Bài kiểm tra dịch|Đánh giá trình độ
loc_workspace|Không gian dịch|Dịch theo key, có ảnh ngữ cảnh
loc_context_viewer|Ngữ cảnh|Ảnh chụp ScreenId chứa key
loc_suggestions|Đề xuất bản dịch|Bình chọn bản dịch tốt nhất
loc_glossary|Thuật ngữ|Thuật ngữ bắt buộc
loc_discussions|Thảo luận|Thảo luận theo key
loc_progress|Tiến độ ngôn ngữ|% hoàn thành
loc_reviewer_queue|Hàng đợi duyệt|Người duyệt cộng đồng
loc_contributors|Người đóng góp|Bảng ghi công
loc_rewards|Thưởng dịch giả|Quà trong game
loc_style_guide|Hướng dẫn văn phong|Theo từng ngôn ngữ
loc_ingame_preview|Xem trong game|Mở build với bản dịch nháp
loc_report_issue|Báo lỗi dịch|Báo từ người chơi
loc_leaderboard|Xếp hạng dịch giả|Đóng góp nhiều nhất
loc_new_language|Đề xuất ngôn ngữ mới|Bỏ phiếu ngôn ngữ
loc_certificates|Chứng nhận dịch giả|Ghi nhận
`},
  {
    season: "s4", surface: "partner", module: "partners", roles: ["creator", "admin"], screens: `
partner_apply|Đăng ký đối tác|Streamer, YouTuber, fansite
partner_status|Trạng thái hồ sơ|Duyệt/từ chối
partner_home|Trang chủ đối tác|Tổng quan quyền lợi
partner_codes|Mã giới thiệu|Mã creator hỗ trợ
partner_code_stats|Thống kê mã|Lượt dùng, doanh thu
partner_assets|Tài nguyên truyền thông|Ảnh, video, logo
partner_early_access|Truy cập sớm|Build/thông tin trước ra mắt
partner_embargo|Lịch cấm đăng|Thời điểm được công bố
partner_campaigns|Chiến dịch|Chiến dịch đang mở
partner_campaign_detail|Chi tiết chiến dịch|Yêu cầu, thù lao
partner_deliverables|Sản phẩm bàn giao|Nộp link nội dung
partner_payouts|Thanh toán đối tác|Lịch sử thanh toán
partner_contract|Hợp đồng|Ký điện tử
partner_giveaways|Giveaway|Mã quà cho cộng đồng
partner_support|Hỗ trợ đối tác|Liên hệ quản lý đối tác
partner_analytics|Phân tích đối tác|Hiệu quả nội dung
partner_tiers|Hạng đối tác|Điều kiện lên hạng
partner_events|Sự kiện đối tác|Gặp mặt, livestream chung
`},
  {
    season: "s2", surface: "gm", module: "live_gm", roles: ["admin"], screens: `
gm_home|Bàn điều khiển GM|Tình trạng thế giới trực tiếp
gm_world_state|Trạng thái thế giới|Điều chỉnh bóng tối lan/lùi
gm_spawn_world_event|Tạo sự kiện thế giới|Boss bất ngờ, mưa sao băng
gm_event_preview|Xem trước sự kiện|Mô phỏng tác động kinh tế
gm_live_announcement|Thông báo trực tiếp|Hiển thị banner trong game
gm_npc_puppet|Điều khiển NPC|NPC nói lời thoại trực tiếp trong lễ hội
gm_story_vote_control|Điều khiển bỏ phiếu|Mở/đóng phiếu, công bố
gm_world_goal_control|Điều khiển mục tiêu thế giới|Điều chỉnh ngưỡng
gm_weather_override|Ghi đè thời tiết|Đặt thời tiết theo vùng
gm_festival_control|Điều khiển lễ hội|Bật hoạt động lễ hội
gm_rift_seed_publish|Phát seed Vết Nứt|Seed ngày/tuần đã kiểm tra
gm_puzzle_publish|Phát câu đố ngày|Câu đố đã có lời giải xác minh
gm_raid_schedule|Lịch raid|Mở/đóng raid
gm_compensation|Bồi thường|Gửi quà bồi thường sau sự cố
gm_rollback_request|Yêu cầu rollback|Rollback có phê duyệt 2 người
gm_live_metrics|Chỉ số trực tiếp|Tác động của hành động GM
gm_action_log|Nhật ký GM|Mọi hành động GM
gm_approval_queue|Hàng đợi phê duyệt|Hành động cần người thứ hai
gm_playbooks|Kịch bản vận hành|Quy trình chuẩn cho sự kiện
gm_safety_limits|Giới hạn an toàn|Trần phần thưởng GM được phát
gm_shift_handover|Bàn giao ca|Ghi chú giữa các ca
gm_chat_broadcast|Phát chat|Tin nhắn hệ thống
gm_region_focus|Tập trung vùng|Quan sát một vùng
gm_player_spotlight|Tôn vinh người chơi|Đưa người chơi lên bảng vinh danh
`},
  {
    season: "s2", surface: "console", module: "narrative_ops", roles: ["admin", "creator"], screens: `
bard_policy|Chính sách Bard|Giới hạn nội dung AI kể chuyện
bard_review_queue|Duyệt nội dung Bard|Mẫu ngẫu nhiên + bị gắn cờ
bard_cost_dashboard|Chi phí Bard|Token/chi phí mỗi ngày
bard_quality|Chất lượng Bard|Điểm đánh giá của người chơi
bard_incidents|Sự cố Bard|Nội dung vi phạm đã chặn
story_vote_ops|Vận hành bỏ phiếu|Chống gian lận phiếu
world_goal_ops|Vận hành mục tiêu thế giới|Tiến độ, điều chỉnh
echo_moderation|Kiểm duyệt dấu ấn|Dấu ấn bị báo cáo
`},
  {
    season: "s4", surface: "console", module: "economy_bank", roles: ["admin"], screens: `
central_bank|Ngân hàng trung ương|Tổng cung tiền tệ theo thời gian
money_supply|Cung tiền|Vàng/ngọc tạo ra vs tiêu hủy
sink_designer|Thiết kế điểm tiêu|Đề xuất sink khi lạm phát
market_regulation|Điều tiết chợ|Biên độ giá, thuế giao dịch
market_manipulation|Thao túng chợ|Phát hiện gom hàng
caravan_price_monitor|Giá tuyến buôn|Giá vùng theo cung cầu
price_index|Chỉ số giá|Rổ hàng chuẩn theo tuần
wealth_distribution|Phân bố tài sản|Gini, top 1%
economy_forecast|Dự báo kinh tế|Mô phỏng 90 ngày
economy_interventions|Can thiệp|Lịch sử can thiệp và hiệu quả
`},
  {
    season: "s3", surface: "console", module: "workshop_ops", roles: ["admin"], screens: `
mod_queue|Hàng đợi duyệt mod|Mod chờ duyệt
mod_review|Duyệt mod|Kết quả quét, chơi thử
mod_takedowns|Gỡ mod|Yêu cầu gỡ bản quyền
creator_verification|Xác minh creator|KYC cho creator có thu nhập
creator_payout_approvals|Duyệt chi trả|Duyệt thanh toán
workshop_featured|Nổi bật workshop|Chọn mod nổi bật
jam_management|Quản lý game jam|Tổ chức, chấm điểm
workshop_analytics|Phân tích workshop|Tải, gỡ, doanh thu
`},
  {
    season: "s2", surface: "console", module: "stream_ops", roles: ["admin"], screens: `
stream_integrations|Tích hợp stream|Trạng thái API Twitch/YouTube
drops_campaigns_ops|Vận hành drops|Chiến dịch drops toàn cục
stream_abuse|Lạm dụng stream|Phiếu bot, gian lận drops
streamer_directory|Danh bạ streamer|Streamer đã liên kết
esports_ops|Vận hành esports|Lịch giải, trọng tài
esports_integrity|Liêm chính esports|Nghi dàn xếp
`},
  {
    season: "s1", surface: "console", module: "festival_ops", roles: ["admin"], screens: `
festival_scheduler|Lịch lễ hội|Âm lịch ↔ dương lịch theo khu vực
festival_regional|Lễ hội theo khu vực|Lễ hội riêng từng thị trường
festival_kpi|KPI lễ hội|Tham gia, doanh thu
lantern_moderation|Kiểm duyệt lời chúc|Lời chúc trên đèn lồng
season_rotation|Luân chuyển mùa|Lịch xuân/hạ/thu/đông
`},
  {
    season: "s1", surface: "studio", module: "creative_editors", roles: ["creator"], screens: `
evolution_tree_editor|Sửa cây tiến hóa|Nhánh lớp, điều kiện
talent_tree_editor|Sửa cây thiên phú|Nút, liên kết, chi phí
festival_designer|Thiết kế lễ hội|Hoạt động, trang trí, lịch
minigame_tuner|Chỉnh minigame|Nhịp, độ khó minigame
puzzle_designer|Thiết kế câu đố|Dựng bàn cờ, luật
puzzle_solver|Bộ giải câu đố|Xác minh câu đố có lời giải, đếm số lời giải
weather_editor|Sửa thời tiết|Mẫu thời tiết, hiệu ứng trận
season_editor|Sửa mùa|Hiệu ứng mùa theo lớp
dye_palette_editor|Sửa bảng nhuộm|Palette swap cho sprite
research_editor|Sửa nghiên cứu quái|Mốc, gợi ý
museum_prop_editor|Sửa đạo cụ diorama|Vật phẩm trưng bày
treasure_route_editor|Sửa tuyến kho báu|Manh mối, vị trí
`},
  {
    season: "s2", surface: "studio", module: "narrative_ai", roles: ["creator"], screens: `
bard_template_editor|Sửa mẫu Bard|Khung prompt theo loại sự kiện
bard_guardrails|Rào chắn Bard|Từ cấm, lore bắt buộc, độ dài
bard_playground|Thử Bard|Chạy thử trên biên niên mẫu
bard_eval_set|Bộ đánh giá Bard|Tập kiểm tra chất lượng
story_vote_designer|Thiết kế bỏ phiếu|Phương án + hệ quả nội dung
world_goal_designer|Thiết kế mục tiêu thế giới|Ngưỡng, thưởng, cutscene
highlight_rules_editor|Luật highlight|Điều kiện một khoảnh khắc là highlight
echo_vocabulary|Từ vựng dấu ấn|Từ/cụm được phép khắc
`},
  {
    season: "s3", surface: "studio", module: "procedural", roles: ["creator"], screens: `
rift_generator|Bộ sinh Vết Nứt|Luật ghép phòng từ tileset
rift_seed_explorer|Duyệt seed|Xem trước tầng theo seed
rift_seed_audit|Kiểm tra seed|Loại seed không thể thắng/quá dễ
rift_room_templates|Mẫu phòng|Phòng thủ công dùng trong sinh thủ tục
dream_map_transform|Biến đổi map giấc mơ|Luật đảo màu/đảo chiều map
card_designer|Thiết kế thẻ|Thẻ + từ khóa + giá năng lượng
card_balance_lab|Cân bằng thẻ|Mô phỏng bộ bài
boss_phase_composer|Ghép pha boss|Pha ngẫu nhiên có ràng buộc
modifier_designer|Thiết kế luật biến đổi|Luật tuần/Vết Nứt
speedrun_category_editor|Sửa hạng mục speedrun|Luật, điều kiện hợp lệ
`},
  {
    season: "s4", surface: "studio", module: "act_two_tools", roles: ["creator"], screens: `
dual_world_linker|Liên kết hai thế giới|Ghép map Bình Minh ↔ Hoàng Hôn
dual_world_diff_editor|Sửa khác biệt thế giới|Đối tượng chỉ ở một phía
caravan_route_editor|Sửa tuyến buôn|Tuyến, rủi ro, hàng
trade_goods_economy|Kinh tế hàng buôn|Giá cơ sở, co giãn cung cầu
profession_editor|Sửa nghề|Cây nghề, công thức
npc_schedule_editor|Sửa lịch NPC|Lộ trình theo giờ
raid_designer|Thiết kế raid|Boss, pha, luật chia đội
artifact_designer|Thiết kế cổ vật|Mảnh, quyền năng
ending_flow_editor|Sửa luồng kết thúc|Biến karma → 6 kết thúc
mount_editor|Sửa thú cưỡi|Tốc độ, địa hình
`},
  {
    season: "s3", surface: "devtools", module: "platform_integrity", roles: ["dev"], screens: `
determinism_matrix|Ma trận tất định|Golden replay trên JVM/ART/RoboVM/TeaVM
determinism_diff|Diff tất định|Lượt đầu tiên lệch giữa nền tảng
mod_security_scanner|Quét an ninh mod|Content pack không chứa mã, giới hạn kích thước
mod_compat_matrix|Tương thích mod|Mod × phiên bản client
procgen_performance|Hiệu năng sinh thủ tục|Thời gian sinh tầng theo thiết bị
ai_bard_latency|Độ trễ Bard|Thời gian phản hồi, tỉ lệ fallback
ai_bard_cost|Chi phí AI|Theo môi trường/tính năng
stream_api_quota|Quota API stream|Giới hạn gọi Twitch/YouTube
companion_sync_monitor|Đồng bộ companion|Độ trễ đồng bộ PWA ↔ game
cross_save_conflicts|Xung đột cross-save|Tỉ lệ xung đột theo thiết bị
shader_compat|Tương thích shader|Shader nhuộm màu theo GPU
feature_kill_switches|Công tắc tắt tính năng|Tắt nhanh tính năng lỗi
`},
  {
    season: "s3", surface: "qa", module: "expansion_qa", roles: ["qa"], screens: `
puzzle_verification|Xác minh câu đố|Solver xác nhận lời giải tối ưu
rift_seed_qa|QA seed Vết Nứt|Kiểm tra seed trước khi phát
mod_qa|QA mod nổi bật|Test mod trước khi đưa lên nổi bật
festival_qa|QA lễ hội|Checklist trước lễ hội
minigame_qa|QA minigame|Cảm giác nhịp, độ trễ input
card_mode_qa|QA chế độ thẻ|Tương tác từ khóa
dual_world_qa|QA hai thế giới|Kẹt vật cản khi chuyển
bard_red_team|Red team Bard|Thử phá rào chắn AI
companion_qa|QA companion|Đồng bộ, thông báo
stream_qa|QA stream|Overlay, phiếu khán giả
accessibility_per_screen|Trợ năng theo màn|Checklist trợ năng từng ScreenId
agent_expansion_coverage|Phủ màn mở rộng|Độ phủ agent theo mùa
`},
  {
    season: "s1", surface: "pilot", module: "season_preview", roles: ["pilot"], screens: `
season_preview|Xem trước mùa|Nội dung mùa sắp tới
feature_vote|Bình chọn tính năng|Ưu tiên tính năng
prototype_lab|Phòng thí nghiệm|Thử prototype chưa chắc ra mắt
prototype_feedback|Góp ý prototype|Giữ/bỏ/sửa
balance_sandbox_pilot|Sandbox cân bằng|Thử chỉ số mới trước khi phát
minigame_feel_test|Thử cảm giác minigame|Đánh giá nhịp
puzzle_beta|Câu đố beta|Giải câu đố chưa phát hành
card_beta|Thẻ beta|Thử thẻ mới
dual_world_beta|Hai thế giới beta|Thử cơ chế chuyển thế giới
bard_beta|Bard beta|Đánh giá truyện AI
pilot_season_report|Báo cáo mùa pilot|Đóng góp của tôi trong mùa
pilot_mentor|Pilot kỳ cựu|Hướng dẫn pilot mới
`},
  {
    season: "s2", surface: "site", module: "community_web", roles: ["gamer", "user"], screens: `
lore_encyclopedia|Bách khoa truyền thuyết|Wiki chính thức sinh từ content
interactive_world_map|Bản đồ thế giới tương tác|12 vùng, 2 thế giới
hero_builder_web|Lên build trên web|Thiên phú + trang bị + chia sẻ link
replay_viewer_web|Xem replay trên web|Phát replay bằng bản web TeaVM
highlight_gallery_web|Thư viện highlight|Clip cộng đồng
puzzle_web|Câu đố ngày trên web|Giải câu đố ngay trên trình duyệt
festival_pages|Trang lễ hội|Giới thiệu lễ hội đang diễn ra
world_state_live|Trạng thái thế giới trực tiếp|Tiến độ mục tiêu thế giới
story_vote_web|Bỏ phiếu trên web|Bỏ phiếu cốt truyện
chronicle_share_page|Trang biên niên chia sẻ|Biên niên công khai của người chơi
speedrun_web|Speedrun trên web|Bảng + video
esports_hub|Trung tâm esports|Lịch, kết quả, VOD
card_deck_share|Chia sẻ bộ bài|Link bộ bài
mod_showcase|Giới thiệu mod|Mod nổi bật
diorama_showcase|Triển lãm diorama|Diorama cộng đồng
`},
  {
    season: "s1", surface: "game", module: "debug_expansion", roles: ["dev", "qa"], screens: `
debug_festival_clock|Đồng hồ lễ hội|Giả lập ngày âm lịch bất kỳ
debug_season_override|Ghi đè mùa|Đặt mùa/thời tiết cục bộ
debug_evolution_unlock|Mở khóa lớp nâng cao|Thử nhanh nhánh tiến hóa
debug_talent_sandbox|Sandbox thiên phú|Cộng điểm tùy ý
debug_puzzle_loader|Nạp câu đố|Mở câu đố theo ID
debug_rift_seed|Seed Vết Nứt|Nhập seed, nhảy tầng
debug_card_spawner|Tạo thẻ|Thêm thẻ vào tay
debug_dream_jump|Nhảy giấc mơ|Mở giấc mơ bất kỳ
debug_dual_world|Hai thế giới|Hiện đồng thời lớp va chạm hai phía
debug_caravan_time|Thời gian đoàn buôn|Tua nhanh hành trình
debug_market_prices|Giá chợ|Đặt giá cục bộ
debug_npc_schedule|Lịch NPC|Tua giờ trong game
debug_raid_sim|Mô phỏng raid|Chạy raid với đội mẫu
debug_bard_mock|Bard giả lập|Dùng phản hồi mẫu, không gọi AI
debug_stream_mock|Stream giả lập|Giả phiếu khán giả
debug_world_goal|Mục tiêu thế giới|Đặt tiến độ cục bộ
debug_rewind_trace|Truy vết tua ngược|So sánh trạng thái trước/sau rewind
debug_companion_sync|Đồng bộ companion|Ép đồng bộ, xem gói
debug_mod_loader|Nạp mod|Nạp content pack cục bộ
debug_accessibility_overlay|Overlay trợ năng|Vùng chạm, tương phản, thứ tự đọc
`},
  {
    season: "s2", surface: "qa", module: "automation_lab", roles: ["qa", "dev"], screens: `
agent_personas|Persona agent|Người chơi mới, hardcore, chi tiêu, trợ năng
agent_persona_editor|Sửa persona|Mục tiêu, phong cách, ngân sách
scenario_recorder|Ghi kịch bản|Ghi thao tác tay thành kịch bản YAML
scenario_diff|So sánh kịch bản|Thay đổi giữa các phiên bản
agent_live_view|Xem agent trực tiếp|Màn hình + suy luận của agent
agent_intervention|Can thiệp agent|Tạm dừng, gợi ý, tiếp tục
agent_bug_candidates|Lỗi ứng viên|Lỗi agent tìm, chờ QA xác nhận
agent_false_positives|Báo động giả|Huấn luyện lại oracle
oracle_catalog|Danh mục oracle|Invariant đang kiểm tra
oracle_editor|Sửa oracle|Thêm invariant mới
fixture_builder|Tạo fixture|Trạng thái game cho từng ScreenId
screen_fixture_matrix|Ma trận fixture|ScreenId × fixture
fuzz_campaigns|Chiến dịch fuzz|Input ngẫu nhiên có seed
monkey_runs|Monkey test|Chạm ngẫu nhiên lâu dài
soak_tests|Soak test|Chạy 24 giờ, rò rỉ bộ nhớ
`},
  {
    season: "s3", surface: "qa", module: "live_quality", roles: ["qa"], screens: `
live_bug_radar|Radar lỗi trực tiếp|Lỗi mới sau release theo phút
player_reported_bugs|Lỗi người chơi báo|Gom từ ticket, bug_report, pilot
repro_queue|Hàng đợi tái hiện|Lỗi cần tái hiện
hotfix_verification|Xác minh hotfix|Checklist hotfix
rollout_health|Sức khỏe rollout|Chỉ số theo % rollout
store_review_monitor|Theo dõi đánh giá store|Đánh giá 1–2 sao theo chủ đề
qa_calendar|Lịch QA|Lịch test theo mùa
qa_capacity|Năng lực QA|Phân công theo mùa
test_debt|Nợ test|Màn chưa có test case
regression_heatmap|Heatmap hồi quy|Module hay hỏng
cert_checklist_ios|Checklist duyệt App Store|Yêu cầu Apple
cert_checklist_android|Checklist Google Play|Yêu cầu Google
cert_checklist_steam|Checklist Steam|Yêu cầu Steam Deck
age_rating_qa|QA xếp hạng tuổi|Nội dung nhạy cảm theo vùng
payment_qa|QA thanh toán|Sandbox giao dịch
`},
  {
    season: "s2", surface: "devtools", module: "data_platform", roles: ["dev"], screens: `
event_schema_registry|Registry sự kiện telemetry|Schema TelemetryEvent có version
event_schema_diff|Diff schema sự kiện|Thay đổi phá vỡ
event_debugger|Debug sự kiện|Sự kiện từ một thiết bị theo thời gian thực
pipeline_lag|Độ trễ pipeline|Ingest → ClickHouse
data_quality|Chất lượng dữ liệu|Sự kiện thiếu trường, trùng
warehouse_tables|Bảng dữ liệu|Bảng ClickHouse, dung lượng
query_notebooks|Notebook truy vấn|SQL chỉ đọc có lưu
data_retention_jobs|Job lưu trữ|Xóa theo chính sách
privacy_scrubber|Làm sạch dữ liệu cá nhân|Kiểm tra PII
experiment_assignment_debug|Debug gán A/B|Người chơi thuộc nhánh nào
feature_store|Feature store|Đặc trưng cho mô hình churn
ml_models|Mô hình ML|Churn, gian lận: phiên bản, độ chính xác
`},
  {
    season: "s3", surface: "devtools", module: "sdk_platform", roles: ["dev", "creator"], screens: `
mod_sdk_releases|Phát hành SDK mod|Phiên bản SDK, changelog
schema_docs_generator|Sinh tài liệu schema|Từ JSON Schema sang tài liệu
content_api_keys|Key API nội dung|Cho công cụ bên thứ ba
public_api_usage|Sử dụng API công khai|Theo ứng dụng
public_api_apps|Ứng dụng bên thứ ba|Đăng ký, duyệt
oauth_clients|OAuth client|Client đăng nhập bằng tài khoản game
sandbox_servers|Server sandbox|Server cho người làm tool
sdk_samples|Ví dụ SDK|Dự án mẫu
sdk_issue_tracker|Lỗi SDK|Lỗi cộng đồng báo
api_changelog|Changelog API|Thay đổi API công khai
`},
  {
    season: "s4", surface: "devtools", module: "runtime_ops", roles: ["dev"], screens: `
build_flavor_matrix|Ma trận flavor|dev/qa/pilot/release × nền tảng
remote_asset_bundles|Gói asset từ xa|Tải asset theo mùa
asset_bundle_diff|Diff gói asset|Thay đổi giữa mùa
memory_budgets|Ngân sách bộ nhớ|Theo màn và thiết bị
texture_budget|Ngân sách texture|VRAM theo màn
startup_profiler|Profiler khởi động|Thời gian từng bước boot
frame_profiler|Profiler khung hình|Khung hình chậm theo màn
network_budget|Ngân sách mạng|Dung lượng theo phiên
battery_profiler|Profiler pin|Tiêu thụ pin theo màn
thermal_reports|Báo cáo nhiệt|Throttle theo thiết bị
server_capacity|Dung lượng server|Dự báo theo sự kiện
db_slow_queries|Truy vấn chậm|Top truy vấn chậm
cache_hit_rates|Tỉ lệ cache|Redis/CDN
queue_backlogs|Tồn đọng hàng đợi|Job nền
cost_explorer|Chi phí hạ tầng|Theo dịch vụ
release_notes_builder|Sinh release note|Từ commit + catalog
screen_ownership|Chủ sở hữu màn|ScreenId ↔ đội
dead_screen_detector|Phát hiện màn chết|Màn không ai mở trong 30 ngày
`},
  {
    season: "s1", surface: "site", module: "account_plus", roles: ["user"], screens: `
family_group|Nhóm gia đình|Tài khoản gia đình
family_member|Thành viên gia đình|Quyền, giới hạn
family_spending|Chi tiêu gia đình|Duyệt mua của trẻ
subscription_manage|Quản lý thuê bao|Thẻ tháng, Battle Pass tự gia hạn
subscription_cancel|Hủy thuê bao|Hủy và giữ quyền lợi đến hết kỳ
payment_methods|Phương thức thanh toán|Thẻ, ví điện tử
invoices|Hóa đơn|Tải hóa đơn
gift_purchase|Mua quà tặng|Mua gói tặng bạn
gift_inbox|Quà được tặng|Nhận quà
wishlist|Danh sách mong muốn|Gói muốn mua
login_history|Lịch sử đăng nhập|Đăng nhập theo thiết bị/vị trí
security_alerts|Cảnh báo bảo mật|Đăng nhập lạ
recovery_codes|Mã khôi phục|Mã dự phòng 2FA
data_requests|Yêu cầu dữ liệu|Trạng thái xuất/xóa dữ liệu
consent_center|Trung tâm đồng ý|Quản lý đồng ý marketing/analytics
communication_prefs|Tùy chọn liên lạc|Email, push
account_transfer|Chuyển vùng tài khoản|Chuyển server/khu vực
linked_streams|Kênh stream liên kết|Quản lý liên kết Twitch/YouTube
creator_account_link|Liên kết tài khoản creator|Workshop, đối tác
account_badges|Huy hiệu tài khoản|Pilot, dịch giả, creator
support_history|Lịch sử hỗ trợ|Mọi ticket
refund_request|Yêu cầu hoàn tiền|Gửi yêu cầu
purchase_limits|Hạn mức mua|Tự đặt hạn mức
self_exclusion|Tạm ngưng chơi|Tự khóa tài khoản trong thời gian chọn
`},
  {
    season: "s1", surface: "game", module: "gifting", roles: ["gamer", "user"], screens: `
gift_shop|Cửa hàng quà|Gói có thể tặng
gift_select_friend|Chọn người nhận|Bạn bè đủ điều kiện
gift_message|Lời nhắn quà|Thiệp kèm quà
gift_confirm|Xác nhận tặng|Giá, người nhận
gift_received|Quà nhận được|Mở quà
gift_history|Lịch sử tặng quà|Đã tặng/đã nhận
gift_thank_you|Cảm ơn|Gửi lời cảm ơn
gift_limits|Giới hạn tặng|Hạn mức chống lạm dụng
`},
  {
    season: "s3", surface: "console", module: "trust_ops", roles: ["admin"], screens: `
gifting_abuse|Lạm dụng tặng quà|Rửa tiền qua quà tặng
account_takeover|Chiếm đoạt tài khoản|Phát hiện, khôi phục
family_disputes|Tranh chấp gia đình|Hoàn tiền giao dịch trẻ em
self_exclusion_admin|Quản lý tự ngưng|Tôn trọng yêu cầu tự khóa
minor_protection|Bảo vệ trẻ vị thành niên|Giới hạn theo luật
subscription_admin|Quản lý thuê bao|Gia hạn, hoàn tiền
localization_ops|Vận hành bản dịch|Dự án dịch cộng đồng
translator_moderation|Kiểm duyệt dịch giả|Dịch phá hoại
partner_ops|Vận hành đối tác|Duyệt hồ sơ, hợp đồng
partner_fraud|Gian lận đối tác|Mã giới thiệu ảo
esports_payouts|Chi trả esports|Tiền thưởng giải
speedrun_verification|Xác minh speedrun|Replay + video
ironman_integrity|Liêm chính Thiết Nhân|Phát hiện lách luật
raid_ops|Vận hành raid|Theo dõi, bồi thường
mentor_abuse|Lạm dụng sư phụ|Farm thưởng xuất sư
caravan_ops|Vận hành đoàn buôn|Tuyến lỗi, bồi thường
companion_ops|Vận hành companion|Push, lỗi đồng bộ
act_two_rollout|Triển khai Hồi II|Mở khu vực theo đợt
season_launch_room|Phòng chỉ huy ra mùa|Checklist + chỉ số ra mùa
season_retrospective|Tổng kết mùa|KPI mùa so với mục tiêu
cross_play_ops|Vận hành chơi chéo|Ghép trận theo nền tảng
accessibility_feedback|Phản hồi trợ năng|Yêu cầu trợ năng từ người chơi
puzzle_ops|Vận hành câu đố|Câu đố bị báo sai
card_meta_monitor|Theo dõi meta thẻ|Thẻ quá mạnh
rift_ops|Vận hành Vết Nứt|Seed lỗi, bảng xếp hạng gian lận
world_voice_ops|Vận hành tiếng nói thế giới|Tổng quan phiếu + mục tiêu
reputation_scores|Điểm uy tín|Uy tín người chơi cho tính năng xã hội
content_ratings_ops|Xếp hạng nội dung UGC|Gắn nhãn tuổi cho mod/level
`},
  {
    season: "s2", surface: "pilot", module: "research_ops", roles: ["pilot"], screens: `
diary_study|Nhật ký trải nghiệm|Ghi cảm nhận mỗi ngày trong 2 tuần
diary_entry|Viết nhật ký|Ghi chép + ảnh
card_sort|Card sorting|Sắp xếp menu theo cách hiểu
first_click_test|Thử cú chạm đầu|Tìm chức năng trên mockup
five_second_test|Thử 5 giây|Ấn tượng đầu về màn hình
naming_test|Thử đặt tên|Chọn tên cho tính năng
difficulty_rating|Chấm độ khó|Chấm từng trận/câu đố
fun_rating|Chấm độ vui|Chấm từng phiên chơi
bug_bounty|Săn lỗi có thưởng|Thưởng theo mức độ lỗi
bug_bounty_leaderboard|Xếp hạng săn lỗi|Pilot tìm lỗi giỏi nhất
`},
];

export const expansionMatrices = [
  {
    season: "s1", surface: "studio", module: "expansion_content_entities", roles: ["creator"],
    views: [
      ["list", "Danh sách", "Tìm, lọc, kiểm tra lỗi"],
      ["detail", "Chi tiết", "Xem đầy đủ + nơi được tham chiếu"],
      ["editor", "Soạn thảo", "Form theo JSON Schema + xem trước"],
      ["history", "Lịch sử", "Phiên bản, diff, hoàn tác"],
      ["bulk", "Hàng loạt", "Nhập/xuất bảng tính, sửa nhiều"],
      ["promote", "Đẩy môi trường", "So sánh dev→staging→prod và đẩy"],
    ],
    entities: `
advanced_classes|Lớp nâng cao
talent_trees|Cây thiên phú
talent_nodes|Nút thiên phú
festivals|Lễ hội
festival_activities|Hoạt động lễ hội
minigames|Minigame
puzzles|Câu đố
puzzle_packs|Bộ câu đố
weather_patterns|Mẫu thời tiết
seasons|Mùa
research_entries|Mục nghiên cứu
wardrobe_items|Trang phục
dyes|Màu nhuộm
emotes|Biểu cảm
voice_packs|Gói giọng
museum_props|Đạo cụ diorama
treasure_routes|Tuyến kho báu
bard_templates|Mẫu Bard
story_votes|Bỏ phiếu cốt truyện
world_goals|Mục tiêu thế giới
echo_glyphs|Dấu ấn
highlight_rules|Luật highlight
rift_templates|Mẫu Vết Nứt
rift_modifiers|Biến đổi Vết Nứt
dream_dungeons|Hầm ngục giấc mơ
soul_cards|Thẻ linh hồn
card_keywords|Từ khóa thẻ
starter_decks|Bộ bài khởi đầu
challenge_modifiers|Luật thử thách
speedrun_categories|Hạng mục speedrun
relics|Di vật
artifact_shards|Mảnh cổ vật
endings|Kết thúc
caravan_routes|Tuyến buôn
trade_goods|Hàng buôn
professions|Nghề
profession_recipes|Công thức nghề
raid_bosses|Boss raid
guild_hall_props|Đạo cụ sảnh bang
mounts|Thú cưỡi
npc_schedules|Lịch NPC
bounties|Truy nã
dual_world_links|Liên kết hai thế giới
`,
  },
  {
    season: "s2", surface: "console", module: "expansion_ops_entities", roles: ["admin"],
    views: [
      ["list", "Danh sách", "Tìm, lọc, sắp xếp"],
      ["detail", "Chi tiết", "Xem đầy đủ + trạng thái"],
      ["editor", "Soạn thảo", "Tạo/sửa có kiểm tra hợp lệ"],
      ["history", "Lịch sử", "Phiên bản, người sửa, diff"],
      ["bulk", "Hàng loạt", "Nhập/xuất CSV, sửa nhiều bản ghi"],
    ],
    entities: `
festival_schedules|Lịch lễ hội
world_events_live|Sự kiện thế giới trực tiếp
bard_policies|Chính sách Bard
story_vote_schedules|Lịch bỏ phiếu
world_goal_schedules|Lịch mục tiêu thế giới
rift_seed_schedules|Lịch seed Vết Nứt
puzzle_schedules|Lịch câu đố ngày
drops_campaigns|Chiến dịch drops
streamer_accounts|Tài khoản streamer
esports_matches|Trận esports
mod_listings|Mod đăng bán
creator_accounts|Tài khoản creator
creator_payouts|Chi trả creator
partner_accounts|Tài khoản đối tác
partner_campaigns|Chiến dịch đối tác
translation_projects|Dự án dịch cộng đồng
economy_policies|Chính sách kinh tế
market_price_bands|Biên độ giá chợ
ironman_leagues|Giải Thiết Nhân
speedrun_submissions|Bài nộp speedrun
raid_schedules|Lịch raid
gm_playbooks|Kịch bản GM
compensation_packages|Gói bồi thường
mentor_programs|Chương trình sư phụ
gift_catalog|Danh mục quà tặng
family_policies|Chính sách tài khoản gia đình
`,
  },
];
