import { writeFileSync, mkdirSync } from "node:fs";
import { dirname, resolve } from "node:path";
import { fileURLToPath } from "node:url";
import { EXPANSION_SURFACES, SEASONS, expansionGroups, expansionMatrices } from "./expansion.mjs";

const ROLES = {
  gamer: "Gamer — người chơi tập trung gameplay",
  user: "User — chủ tài khoản (thanh toán, bảo mật, hỗ trợ)",
  pilot: "Pilot — người chơi thử nghiệm bản beta/pilot",
  creator: "Creator — thiết kế nội dung, level, lore, art, dịch thuật (kể cả player-creator UGC)",
  qa: "QA — kiểm thử, xác nhận release",
  dev: "Dev — kỹ sư client/server/tooling",
  admin: "Admin — vận hành, LiveOps, hỗ trợ, kiểm duyệt, quản trị hệ thống",
};

const SURFACES = {
  game: "Game client (Desktop/Android/iOS/Web)",
  console: "Web Console (admin/liveops/support/BI)",
  studio: "Creator Studio (web)",
  devtools: "Dev Tools (web)",
  qa: "QA Hub (web)",
  pilot: "Pilot Portal (web)",
  site: "Public Site + Account Portal (web)",
  ...EXPANSION_SURFACES,
};

const groups = [
  {
    surface: "game", module: "boot", roles: ["gamer", "user", "pilot"], screens: `
splash|Splash|Hiển thị thương hiệu, khởi tạo runtime
legal_notice|Thông báo pháp lý|Hiển thị bản quyền/cảnh báo sức khỏe
age_gate|Xác nhận độ tuổi|Chặn/giới hạn tính năng theo tuổi
tos_accept|Chấp nhận điều khoản|Ghi nhận đồng ý ToS theo phiên bản
privacy_consent|Đồng ý quyền riêng tư|Chọn đồng ý analytics/crash/marketing
language_pick|Chọn ngôn ngữ lần đầu|Đặt locale trước khi tải nội dung
content_download|Tải gói nội dung|Tải/giải nén content pack theo phiên bản
patch_notes|Ghi chú cập nhật|Giới thiệu thay đổi bản mới
maintenance|Bảo trì|Thông báo thời gian bảo trì server
force_update|Bắt buộc cập nhật|Chặn client cũ không tương thích protocol
server_select|Chọn máy chủ|Chọn region/cụm server
login_hub|Cổng đăng nhập|Chọn khách/email/Google/Apple
login_email|Đăng nhập email|Xác thực bằng email + mật khẩu
register_email|Đăng ký email|Tạo tài khoản mới
verify_email|Xác minh email|Nhập mã OTP xác minh
forgot_password|Quên mật khẩu|Gửi yêu cầu đặt lại mật khẩu
reset_password|Đặt lại mật khẩu|Nhập mật khẩu mới
guest_warning|Cảnh báo tài khoản khách|Nhắc liên kết để tránh mất dữ liệu
account_link|Liên kết tài khoản|Nâng cấp khách thành tài khoản đầy đủ
account_switch|Đổi tài khoản|Đăng xuất và chuyển tài khoản
slot_list|Danh sách nhân vật|Chọn slot lưu (thay SelectPlayerScreen)
slot_delete_confirm|Xác nhận xóa slot|Xóa slot có xác nhận 2 bước
cloud_restore|Khôi phục từ cloud|Kéo bản lưu cloud về thiết bị
save_conflict|Xung đột bản lưu|So sánh local vs cloud và chọn bản giữ
ban_notice|Thông báo khóa tài khoản|Hiển thị lý do và cách kháng nghị
reconnect|Kết nối lại|Xử lý mất mạng giữa phiên
offline_mode|Chế độ ngoại tuyến|Thông báo tính năng bị giới hạn khi offline
main_menu|Menu chính|Vào game, cài đặt, tin tức, thoát
`},
  {
    surface: "game", module: "onboarding", roles: ["gamer", "pilot"], screens: `
hero_create_class|Tạo nhân vật — chọn lớp|Chọn 1 trong 6 lớp khởi đầu
hero_create_look|Tạo nhân vật — ngoại hình|Chọn skin/màu khởi đầu
hero_create_name|Tạo nhân vật — đặt tên|Nhập tên, kiểm tra hợp lệ/trùng
hero_create_confirm|Tạo nhân vật — xác nhận|Tóm tắt lựa chọn trước khi tạo
prologue|Mở đầu cốt truyện|Cutscene giới thiệu Dark Lord và cổ vật
tutorial_move|Hướng dẫn di chuyển|Joystick/bàn phím trên bản đồ
tutorial_interact|Hướng dẫn tương tác|Nói chuyện NPC, mở rương
tutorial_battle|Hướng dẫn chiến đấu|Trận đầu có chỉ dẫn từng bước
tutorial_skill|Hướng dẫn kỹ năng|Năng lượng, kỹ năng 2/3, tuyệt kỹ
tutorial_lineup|Hướng dẫn đội hình|Lưới 3x3, hàng trước/giữa/sau
tutorial_equip|Hướng dẫn trang bị|Mặc trang bị, so sánh chỉ số
tutorial_shop|Hướng dẫn cửa hàng|Mua vật phẩm đầu tiên
tutorial_reward|Hoàn thành hướng dẫn|Nhận quà hoàn thành tutorial
`},
  {
    surface: "game", module: "world", roles: ["gamer", "pilot"], screens: `
world_explore|Khám phá bản đồ|Di chuyển, va chạm, trigger (thay WorldMapScreen)
region_map|Bản đồ vùng|Xem toàn vùng, điểm đã mở khóa
fast_travel|Dịch chuyển nhanh|Chọn điểm dịch chuyển đã mở
map_transition|Chuyển bản đồ|Hiệu ứng + tải bản đồ kế tiếp
npc_dialogue|Hội thoại NPC|Hiển thị lời thoại có chân dung
dialogue_choice|Lựa chọn hội thoại|Chọn nhánh ảnh hưởng cốt truyện
inn_rest|Nhà trọ|Hồi phục, lưu game, qua ngày
chest_open|Mở rương|Hiệu ứng mở và nhận vật phẩm
encounter_preview|Xem trước trận|Đội hình địch 3x3, lực chiến (thay BattleDetailPP)
encounter_locked|Trận bị khóa|Hiển thị điều kiện mở khóa
minimap|Bản đồ nhỏ|Overlay vị trí, NPC, cổng
quest_tracker|Theo dõi nhiệm vụ|Overlay mục tiêu hiện tại
day_night_info|Thông tin ngày/đêm|Thời gian trong game và hiệu ứng
weather_info|Thời tiết|Hiệu ứng môi trường lên trận đấu
photo_mode|Chế độ chụp ảnh|Ẩn HUD, camera tự do, lưu ảnh
emote_wheel|Vòng biểu cảm|Chọn emote nhân vật
interaction_prompt|Gợi ý tương tác|Nút ngữ cảnh khi đứng gần đối tượng
puzzle_switch|Câu đố công tắc|Giải đố bật/tắt trên bản đồ
puzzle_sequence|Câu đố trình tự|Giải đố ghi nhớ thứ tự
signpost_read|Đọc biển báo|Hiển thị chữ trên biển báo
lore_book|Đọc sách truyền thuyết|Mở khóa mục codex
save_point|Điểm lưu|Lưu thủ công
pause_menu|Tạm dừng|Menu tạm dừng khi khám phá
hud_customize|Tùy chỉnh HUD|Kéo thả vị trí nút, joystick
`},
  {
    surface: "game", module: "village", roles: ["gamer"], screens: `
village_overview|Tổng quan làng|Mức phục hồi làng, tài nguyên
building_list|Danh sách công trình|Công trình đã/chưa xây
building_detail|Chi tiết công trình|Chức năng, cấp, sản lượng
building_upgrade|Nâng cấp công trình|Chi phí, thời gian, xác nhận
building_construct|Xây công trình mới|Chọn vị trí đặt công trình
resource_production|Sản xuất tài nguyên|Thu hoạch tài nguyên theo giờ
worker_assign|Phân công dân làng|Gán NPC vào công trình
decoration_mode|Chế độ trang trí|Đặt vật trang trí
village_visitors|Khách đến làng|NPC ghé thăm mang nhiệm vụ
village_quests|Nhiệm vụ làng|Nhiệm vụ phục hồi làng
`},
  {
    surface: "game", module: "gathering", roles: ["gamer"], screens: `
fishing|Câu cá|Minigame câu cá
mining|Khai khoáng|Minigame đào quặng
herb_gathering|Hái thảo dược|Minigame thu thập
gather_result|Kết quả thu thập|Vật phẩm nhận được
cooking|Nấu ăn|Chế món ăn buff
cookbook|Sách công thức|Công thức đã mở khóa
`},
  {
    surface: "game", module: "battle", roles: ["gamer", "pilot"], screens: `
battle_intro|Mở màn trận|Giới thiệu đội địch, boss
lineup_confirm|Xác nhận đội hình|Chỉnh nhanh đội hình trước trận
battle_main|Trận đấu|Lưới 3x3 hai phe, HUD, thanh lượt
skill_select|Chọn kỹ năng|Chọn kỹ năng cho đơn vị đang tới lượt
target_select|Chọn mục tiêu|Chọn mục tiêu hợp lệ theo kỹ năng
item_select|Dùng vật phẩm|Dùng vật phẩm trong trận
auto_battle|Tự động chiến đấu|Bật/tắt AI điều khiển phe ta
battle_speed|Tốc độ trận|x1/x2/x4
battle_pause|Tạm dừng trận|Tiếp tục/thoát/cài đặt
battle_log|Nhật ký trận|Danh sách sự kiện từng lượt
unit_inspect|Xem đơn vị|Chỉ số, hiệu ứng đang có
turn_timeline|Dòng thời gian lượt|Thứ tự hành động sắp tới
weakness_hint|Gợi ý khắc chế|Chỉ ra lớp khắc chế/bị khắc
ultimate_cutin|Cut-in tuyệt kỹ|Hoạt cảnh tuyệt kỹ
boss_phase|Chuyển pha boss|Thông báo pha mới và cơ chế
revive_prompt|Hồi sinh|Dùng vật phẩm hồi sinh khi thua
battle_victory|Chiến thắng|Kết quả thắng
battle_defeat|Thất bại|Kết quả thua + gợi ý cải thiện
battle_draw|Hòa|Kết quả khi hết số lượt tối đa
battle_rewards|Phần thưởng trận|EXP, vàng, rơi đồ
level_up|Lên cấp|Chỉ số tăng khi lên cấp
flee_confirm|Xác nhận bỏ chạy|Rời trận có hậu quả
retry_confirm|Đánh lại|Đánh lại với cùng đội
damage_breakdown|Phân tích sát thương|Sát thương/hồi máu mỗi đơn vị
battle_quest_progress|Tiến độ nhiệm vụ trong trận|Mục tiêu phụ của trận
replay_list|Danh sách replay|Các trận đã lưu
replay_viewer|Xem replay|Phát lại từ seed + input
replay_share|Chia sẻ replay|Tạo mã/đường dẫn chia sẻ
`},
  {
    surface: "game", module: "heroes", roles: ["gamer"], screens: `
hero_roster|Danh sách anh hùng|Lọc, sắp xếp, khóa (thay HerosPP)
hero_overview|Anh hùng — tổng quan|Chân dung, sao, cấp, lực chiến
hero_stats|Anh hùng — chỉ số|Chỉ số gốc + trang bị + buff
hero_skills|Anh hùng — kỹ năng|3 kỹ năng + nội tại
hero_equipment|Anh hùng — trang bị|4 ô trang bị, set bonus
hero_lore|Anh hùng — tiểu sử|Cốt truyện, lồng tiếng
hero_level_up|Nâng cấp anh hùng|Dùng EXP/vật phẩm
hero_star_up|Tăng sao|Ghép anh hùng cùng loại (thay PotentialPP)
hero_merge_confirm|Xác nhận ghép|Cảnh báo anh hùng đang trong đội hình
hero_skill_upgrade|Nâng kỹ năng|Dùng sách kỹ năng
hero_awaken|Thức tỉnh|Mở khóa tuyệt kỹ mới ở 5 sao
hero_compare|So sánh anh hùng|So sánh 2 anh hùng cạnh nhau
hero_dismiss|Giải tán anh hùng|Đổi anh hùng lấy tài nguyên
lineup_editor|Sửa đội hình|Kéo thả trên lưới 3x3
lineup_presets|Đội hình lưu sẵn|Lưu/đổi preset theo chế độ
lineup_analysis|Phân tích đội hình|Lực chiến, cân bằng vai trò
synergy_view|Cộng hưởng|Bonus khi đủ lớp/hệ
class_counter_chart|Bảng khắc chế lớp|Vòng khắc chế 6 lớp
`},
  {
    surface: "game", module: "companions", roles: ["gamer"], screens: `
pet_list|Danh sách thú cưng|Thú cưng sở hữu
pet_detail|Chi tiết thú cưng|Chỉ số hỗ trợ
pet_feed|Cho ăn|Tăng thân thiết
pet_evolve|Tiến hóa thú cưng|Đổi hình dạng, tăng chỉ số
pet_skill|Kỹ năng thú cưng|Kỹ năng hỗ trợ trận
pet_expedition|Thám hiểm thú cưng|Gửi đi nhận tài nguyên
`},
  {
    surface: "game", module: "inventory", roles: ["gamer"], screens: `
bag_equipment|Túi — trang bị|Lưới trang bị (thay BagPP)
bag_materials|Túi — nguyên liệu|Nguyên liệu chế tạo
bag_consumables|Túi — tiêu hao|Thuốc, sách EXP
bag_key_items|Túi — vật phẩm nhiệm vụ|Chìa khóa, cổ vật
item_detail|Chi tiết vật phẩm|Mô tả, nguồn nhận
item_use_target|Chọn mục tiêu dùng|Chọn anh hùng nhận hiệu ứng
item_use_result|Kết quả sử dụng|Hiệu ứng đã áp dụng
equipment_detail|Chi tiết trang bị|Chỉ số, cấp, set
equipment_compare|So sánh trang bị|So với đồ đang mặc
equipment_assign|Mặc trang bị|Chọn anh hùng mặc
equipment_upgrade|Cường hóa|Tăng cấp trang bị
equipment_refine|Tinh luyện|Đổi dòng chỉ số phụ
equipment_salvage|Phân rã|Đổi trang bị lấy nguyên liệu
set_bonus|Bộ trang bị|Hiệu ứng bộ
bag_expand|Mở rộng túi|Tăng sức chứa
bag_filter|Lọc túi|Lọc/sắp xếp
sell_confirm|Xác nhận bán|Bán 1 vật phẩm
bulk_sell|Bán hàng loạt|Chọn nhiều để bán
`},
  {
    surface: "game", module: "smithy", roles: ["gamer"], screens: `
smithy_home|Lò rèn|Trung tâm chế tạo (thay SmithyPP)
recipe_list|Danh sách công thức|Công thức chế tạo
recipe_detail|Chi tiết công thức|Nguyên liệu cần
craft_confirm|Xác nhận chế tạo|Số lượng, chi phí
craft_result|Kết quả chế tạo|Vật phẩm nhận
enchant|Phù phép|Thêm hiệu ứng trang bị
socket_gem|Khảm ngọc|Gắn ngọc vào ô
gem_combine|Ghép ngọc|Ghép ngọc cấp cao
`},
  {
    surface: "game", module: "progression", roles: ["gamer"], screens: `
quest_main|Nhật ký — chính tuyến|Nhiệm vụ cốt truyện
quest_side|Nhật ký — phụ tuyến|Nhiệm vụ phụ
quest_daily|Nhật ký — hằng ngày|Nhiệm vụ reset mỗi ngày
quest_weekly|Nhật ký — hằng tuần|Nhiệm vụ reset mỗi tuần
quest_detail|Chi tiết nhiệm vụ|Mục tiêu, phần thưởng, dẫn đường
quest_accept|Nhận nhiệm vụ|Xác nhận nhận nhiệm vụ
quest_complete|Hoàn thành nhiệm vụ|Thông báo hoàn thành
quest_claim|Nhận thưởng nhiệm vụ|Nhận phần thưởng
chapter_select|Chọn chương|Chương cốt truyện đã mở
chapter_intro|Giới thiệu chương|Mở đầu chương mới
story_recap|Tóm tắt cốt truyện|Nhắc lại sự kiện đã qua
cutscene_player|Phát cutscene|Phát cutscene theo kịch bản
cutscene_skip|Bỏ qua cutscene|Xác nhận bỏ qua
ending_select|Chọn kết thúc|Kết thúc theo lựa chọn
ending_credits|Danh đề|Credits cuối game
new_game_plus|Chơi lại nâng cao|Bắt đầu NG+ giữ tiến trình
achievement_list|Thành tựu|Danh sách thành tựu
achievement_detail|Chi tiết thành tựu|Điều kiện, tiến độ
title_collection|Danh hiệu|Danh hiệu mở khóa
codex_heroes|Bách khoa — anh hùng|Anh hùng đã gặp
codex_enemies|Bách khoa — quái vật|Quái đã đánh bại
codex_items|Bách khoa — vật phẩm|Vật phẩm đã nhận
codex_maps|Bách khoa — vùng đất|Bản đồ đã khám phá
codex_lore|Bách khoa — truyền thuyết|Mục lore đã mở
cg_album|Album hình minh họa|CG đã mở khóa
jukebox|Máy nghe nhạc|Nhạc nền đã mở khóa
tips_library|Thư viện mẹo|Mẹo chơi
glossary|Thuật ngữ|Giải thích thuật ngữ game
`},
  {
    surface: "game", module: "economy", roles: ["gamer", "user"], screens: `
shop_home|Cửa hàng|Trung tâm mua bán (thay ShopPP)
shop_items|Cửa hàng — vật phẩm|Vật phẩm bán bằng vàng
shop_equipment|Cửa hàng — trang bị|Trang bị bán bằng ngọc
shop_rotating|Cửa hàng xoay vòng|Hàng đổi mỗi 6 giờ
offer_detail|Chi tiết gói|Nội dung gói ưu đãi
purchase_confirm|Xác nhận mua|Giá, số lượng
purchase_result|Kết quả mua|Vật phẩm nhận
iap_store|Nạp ngọc|Gói nạp tiền thật
iap_receipt|Hóa đơn|Chi tiết giao dịch
iap_restore|Khôi phục giao dịch|Khôi phục mua trên store
recruit_home|Chiêu mộ|Trung tâm chiêu mộ (thay RecruitPP)
banner_detail|Chi tiết banner|Anh hùng nổi bật, thời hạn
recruit_rates|Tỉ lệ chiêu mộ|Công bố tỉ lệ theo luật
recruit_animation|Hoạt cảnh chiêu mộ|Hiệu ứng mở
recruit_result_single|Kết quả chiêu mộ x1|Anh hùng nhận được
recruit_result_multi|Kết quả chiêu mộ x10|10 anh hùng nhận được
recruit_history|Lịch sử chiêu mộ|Các lần chiêu mộ
pity_tracker|Bảo hiểm chiêu mộ|Số lượt tới đảm bảo
daily_checkin|Điểm danh|Lịch 30 ngày (thay DailyPP)
checkin_claim|Nhận quà điểm danh|Nhận và cộng thưởng thật
monthly_pass|Thẻ tháng|Quà mỗi ngày trong 30 ngày
battle_pass|Battle Pass|Cấp pass, nhiệm vụ pass
battle_pass_rewards|Quà Battle Pass|Bảng quà miễn phí/trả phí
energy_refill|Nạp năng lượng|Mua/đợi hồi năng lượng
currency_exchange|Đổi tiền tệ|Đổi ngọc lấy vàng
idle_rewards|Thưởng treo máy|Thưởng tích lũy khi offline
`},
  {
    surface: "game", module: "social", roles: ["gamer"], screens: `
profile_self|Hồ sơ của tôi|Thông tin, thành tích (thay RolePP)
profile_other|Hồ sơ người khác|Xem người chơi khác
profile_edit|Sửa hồ sơ|Đổi tên, lời giới thiệu
avatar_select|Chọn avatar|Avatar đã mở khóa
frame_select|Chọn khung|Khung avatar
friend_list|Bạn bè|Danh sách bạn
friend_requests|Lời mời kết bạn|Chấp nhận/từ chối
friend_search|Tìm bạn|Tìm theo tên/ID
support_hero|Anh hùng hỗ trợ|Mượn anh hùng của bạn
guild_home|Bang hội|Trang chủ bang
guild_search|Tìm bang|Tìm và xin vào
guild_create|Lập bang|Tạo bang mới
guild_members|Thành viên bang|Vai trò, đóng góp
guild_chat|Chat bang|Chat nội bộ
guild_raid|Raid bang|Boss chung của bang
guild_shop|Cửa hàng bang|Đổi điểm đóng góp
guild_settings|Cài đặt bang|Quyền, điều kiện vào
chat_world|Chat thế giới|Kênh chat chung
chat_private|Chat riêng|Tin nhắn 1-1
mail_inbox|Hộp thư|Thư hệ thống, quà
mail_detail|Chi tiết thư|Nội dung, đính kèm
mail_claim_all|Nhận tất cả|Nhận quà mọi thư
report_player|Tố cáo người chơi|Gửi báo cáo vi phạm
block_list|Danh sách chặn|Quản lý người bị chặn
notification_center|Trung tâm thông báo|Thông báo trong game
notification_detail|Chi tiết thông báo|Nội dung thông báo
leaderboard_power|Xếp hạng lực chiến|Top lực chiến
leaderboard_arena|Xếp hạng đấu trường|Top đấu trường
leaderboard_story|Xếp hạng cốt truyện|Top tiến độ
`},
  {
    surface: "game", module: "modes", roles: ["gamer"], screens: `
arena_home|Đấu trường|PvP bất đồng bộ
arena_matchmaking|Tìm đối thủ|Chọn đối thủ
arena_defense|Đội phòng thủ|Đội hình phòng thủ
arena_history|Lịch sử đấu trường|Trận đã đấu
arena_rewards|Thưởng đấu trường|Thưởng theo mùa
tower_home|Tháp thử thách|Leo tầng
tower_floor|Chọn tầng|Chi tiết tầng
dungeon_list|Hầm ngục|Danh sách hầm ngục nguyên liệu
dungeon_detail|Chi tiết hầm ngục|Độ khó, rơi đồ
world_boss|Boss thế giới|Boss chung toàn server
event_hub|Sự kiện|Danh sách sự kiện đang chạy
event_detail|Chi tiết sự kiện|Luật, thời hạn
event_shop|Cửa hàng sự kiện|Đổi token sự kiện
event_ranking|Xếp hạng sự kiện|Top sự kiện
limited_tasks|Nhiệm vụ giới hạn|Nhiệm vụ có thời hạn (thay LimitedTaskPP)
expedition_send|Gửi thám hiểm|Gửi đội đi thám hiểm
expedition_result|Kết quả thám hiểm|Thưởng thám hiểm
coop_lobby|Phòng co-op|Tạo/vào phòng đánh chung
coop_invite|Mời co-op|Mời bạn
coop_ready|Sẵn sàng co-op|Chờ mọi người sẵn sàng
coop_results|Kết quả co-op|Thưởng chia theo đóng góp
spectate_list|Danh sách xem trận|Trận đang diễn ra
spectate_view|Xem trận|Chế độ khán giả
tournament_list|Giải đấu|Giải đấu đang mở
tournament_detail|Chi tiết giải|Luật, thưởng
tournament_register|Đăng ký giải|Đăng ký đội
tournament_bracket|Nhánh đấu|Sơ đồ nhánh đấu
tournament_checkin|Check-in giải|Xác nhận tham gia
tournament_results|Kết quả giải|Xếp hạng cuối
`},
  {
    surface: "game", module: "ugc", roles: ["creator", "gamer"], screens: `
ugc_home|Xưởng sáng tạo|Trung tâm level do người chơi tạo
ugc_my_levels|Level của tôi|Level đã tạo
ugc_editor_grid|Trình sửa lưới|Đặt quái trên lưới 3x3
ugc_editor_enemy|Chọn quái|Chọn quái được phép dùng
ugc_editor_rules|Luật level|Giới hạn lượt, điều kiện thắng
ugc_editor_rewards|Thưởng level|Thưởng trong giới hạn cho phép
ugc_test_play|Chơi thử|Chơi thử bắt buộc trước khi đăng
ugc_publish|Đăng level|Đăng công khai
ugc_browse|Duyệt level|Tìm level cộng đồng
ugc_level_detail|Chi tiết level|Đánh giá, tỉ lệ thắng
ugc_rating|Đánh giá level|Chấm điểm sau khi chơi
ugc_report|Báo cáo level|Báo cáo vi phạm
ugc_featured|Level nổi bật|Tuyển chọn của đội vận hành
ugc_creator_profile|Hồ sơ creator|Level của một creator
ugc_creator_stats|Thống kê creator|Lượt chơi, đánh giá
`},
  {
    surface: "game", module: "bonds", roles: ["gamer"], screens: `
side_story_list|Truyện phụ|Truyện riêng của từng anh hùng
side_story_detail|Chi tiết truyện phụ|Các hồi, điều kiện mở
bond_overview|Thân thiết|Mức thân thiết với anh hùng
bond_gift|Tặng quà|Tặng quà tăng thân thiết
bond_level_up|Tăng mức thân thiết|Mở khóa chỉ số/cảnh truyện
bond_scene|Cảnh thân thiết|Cảnh truyện riêng
hero_quarters|Khu nghỉ anh hùng|Anh hùng sinh hoạt trong làng
`},
  {
    surface: "game", module: "abyss", roles: ["gamer"], screens: `
abyss_home|Vực Thẳm|Chế độ roguelike theo mùa
abyss_path|Bản đồ đường đi|Chọn nhánh node
abyss_event|Sự kiện node|Lựa chọn rủi ro/phần thưởng
abyss_relic_select|Chọn di vật|Di vật thay đổi luật trận
abyss_shop|Thương nhân Vực Thẳm|Mua bằng tiền tạm thời
abyss_rest|Lửa trại|Hồi máu hoặc nâng di vật
abyss_result|Kết quả Vực Thẳm|Điểm, thưởng mùa
`},
  {
    surface: "game", module: "market", roles: ["gamer"], screens: `
market_home|Chợ giao dịch|Mua bán nguyên liệu giữa người chơi
market_listing|Chi tiết rao bán|Giá, số lượng, người bán
market_sell|Đăng bán|Đặt giá trong biên độ cho phép
market_orders|Đơn của tôi|Đơn đang treo
market_history|Lịch sử giao dịch|Giao dịch đã khớp
`},
  {
    surface: "game", module: "guild_war", roles: ["gamer"], screens: `
guild_war_home|Chiến tranh bang|Mùa, lịch, đối thủ
guild_war_map|Bản đồ chiến tranh|Cứ điểm cần chiếm
guild_war_attack|Tấn công cứ điểm|Chọn đội tấn công
guild_war_results|Kết quả chiến tranh|Điểm, thưởng
guild_donate|Quyên góp bang|Đóng góp tài nguyên
guild_log|Nhật ký bang|Hoạt động thành viên
`},
  {
    surface: "game", module: "settings", roles: ["gamer", "user"], screens: `
settings_home|Cài đặt|Trung tâm cài đặt (thay SettingPP)
settings_audio|Âm thanh|Nhạc, hiệu ứng, lồng tiếng (được lưu)
settings_graphics|Đồ họa|Chất lượng, FPS, độ phân giải
settings_controls|Điều khiển|Joystick, độ nhạy
settings_keybind|Phím tắt|Gán phím (desktop)
settings_language|Ngôn ngữ|Đổi locale
settings_accessibility|Trợ năng|Cỡ chữ, mù màu, giảm chuyển động
settings_notifications|Thông báo đẩy|Bật/tắt từng loại
settings_account|Tài khoản|Liên kết, đăng xuất
settings_privacy|Quyền riêng tư|Quản lý đồng ý
data_download|Tải dữ liệu cá nhân|Yêu cầu xuất dữ liệu (GDPR)
delete_account|Xóa tài khoản|Xóa có thời gian chờ
parental_controls|Kiểm soát phụ huynh|Giới hạn chi tiêu/thời gian
spending_limit|Giới hạn chi tiêu|Đặt hạn mức nạp
playtime_report|Báo cáo thời gian chơi|Thời gian chơi theo tuần
credits|Danh đề|Thông tin đội phát triển
help_center|Trung tâm trợ giúp|Bài viết trợ giúp
faq|Câu hỏi thường gặp|FAQ trong game
contact_support|Liên hệ hỗ trợ|Tạo ticket
ticket_list|Ticket của tôi|Ticket đã gửi
ticket_detail|Chi tiết ticket|Trao đổi với CSKH
bug_report|Báo lỗi|Gửi lỗi kèm log + ảnh
feedback_survey|Khảo sát|Khảo sát trong game
`},
  {
    surface: "game", module: "pilot", roles: ["pilot"], screens: `
pilot_welcome|Chào mừng pilot|Giới thiệu chương trình thử nghiệm
pilot_build_info|Thông tin bản build|Phiên bản, môi trường, commit
pilot_tasks|Nhiệm vụ pilot|Kịch bản cần chơi thử
pilot_task_detail|Chi tiết nhiệm vụ pilot|Các bước và điều cần quan sát
pilot_quick_feedback|Góp ý nhanh|Đánh giá 1 chạm tại màn hiện tại
pilot_bug_capture|Chụp lỗi|Chụp màn hình, khoanh vùng, gửi kèm log
pilot_survey|Khảo sát pilot|Khảo sát sau phiên chơi
pilot_known_issues|Lỗi đã biết|Tránh báo trùng
pilot_changelog|Thay đổi bản build|Khác biệt so với bản trước
pilot_rewards|Quà pilot|Quà ghi nhận đóng góp
`},
  {
    surface: "game", module: "debug", roles: ["dev", "qa"], screens: `
debug_menu|Menu debug|Chỉ có ở build dev/qa
debug_screen_jump|Nhảy màn hình|Mở bất kỳ ScreenId với fixture
debug_cheats|Cheat|Cộng tiền, EXP, mở khóa
debug_battle_sandbox|Sandbox trận|Dựng trận tùy ý với seed
debug_atlas_browser|Duyệt atlas|Xem region (thay CheckRegionScreen)
debug_map_inspector|Soi bản đồ|Collider, trigger, spawn
debug_save_editor|Sửa save|Sửa trực tiếp bản lưu
debug_flags|Feature flags|Bật/tắt cờ cục bộ
debug_perf_overlay|Hiệu năng|FPS, draw call, bộ nhớ
debug_logs|Log|Xem log runtime
debug_network|Mạng|Request/response, giả lập lag
debug_locale_preview|Xem bản dịch|Kiểm tra tràn chữ theo locale
debug_automation|Trạng thái automation|Kết nối test agent
`},
  {
    surface: "console", module: "auth", roles: ["admin", "dev", "qa", "creator"], screens: `
login|Đăng nhập nội bộ|SSO nhân viên
sso_callback|SSO callback|Xử lý phản hồi OIDC
mfa|Xác thực 2 lớp|Bắt buộc với staff
session_expired|Hết phiên|Đăng nhập lại
no_access|Không có quyền|Giải thích quyền còn thiếu
workspace_switch|Đổi môi trường|Chọn dev/qa/staging/pilot/prod
`},
  {
    surface: "console", module: "dashboards", roles: ["admin"], screens: `
overview|Tổng quan|KPI chính
realtime|Thời gian thực|CCU, phiên, lỗi
economy|Kinh tế|Nguồn/chi tiền tệ
retention|Giữ chân|D1/D7/D30
monetization|Doanh thu|ARPU, ARPPU, tỉ lệ trả phí
battle_balance|Cân bằng trận|Tỉ lệ thắng theo trận/đội
errors|Lỗi|Crash, exception theo phiên bản
performance|Hiệu năng|FPS, thời gian tải theo thiết bị
`},
  {
    surface: "console", module: "analytics", roles: ["admin"], screens: `
funnels|Phễu|Phễu onboarding và mua hàng
cohorts|Cohort|Phân tích cohort
ltv|LTV|Giá trị vòng đời
churn|Rời bỏ|Dự báo rời bỏ
session_heatmap|Heatmap phiên|Giờ chơi theo ngày
map_heatmap|Heatmap bản đồ|Vị trí người chơi trên từng map
encounter_winrates|Tỉ lệ thắng theo trận|Phát hiện trận quá khó/dễ
hero_pickrates|Tỉ lệ chọn anh hùng|Meta đội hình
item_usage|Sử dụng vật phẩm|Vật phẩm dùng/bỏ phí
recruit_analytics|Phân tích chiêu mộ|Lượt quay, pity
economy_sankey|Dòng tiền|Sankey nguồn→chi
report_builder|Tạo báo cáo|Truy vấn tùy biến
report_list|Danh sách báo cáo|Báo cáo đã lưu
scheduled_reports|Báo cáo định kỳ|Gửi email/Slack định kỳ
kpi_alerts|Cảnh báo KPI|Ngưỡng cảnh báo
`},
  {
    surface: "console", module: "players", roles: ["admin"], screens: `
player_search|Tìm người chơi|Theo ID, email, tên
player_overview|Người chơi — tổng quan|Tài khoản, trạng thái
player_saves|Người chơi — bản lưu|Phiên bản save, khôi phục
player_heroes|Người chơi — anh hùng|Anh hùng sở hữu
player_inventory|Người chơi — túi đồ|Vật phẩm, trang bị
player_ledger|Người chơi — sổ cái tiền tệ|Mọi giao dịch tiền tệ
player_purchases|Người chơi — giao dịch|IAP, hoàn tiền
player_battles|Người chơi — trận đấu|Replay, kết quả
player_sessions|Người chơi — phiên|Đăng nhập, thiết bị
player_devices|Người chơi — thiết bị|Thiết bị đã dùng
player_mail_send|Gửi thư|Gửi thư/quà cho 1 người
player_grant|Cấp phát|Cấp tài nguyên có lý do bắt buộc
player_sanctions|Xử phạt|Khóa chat, khóa tài khoản
player_notes|Ghi chú nội bộ|Ghi chú CSKH
player_audit|Lịch sử thao tác|Mọi thao tác admin lên người chơi
player_gdpr_export|Xuất dữ liệu|Thực hiện yêu cầu GDPR
player_gdpr_delete|Xóa dữ liệu|Xóa theo yêu cầu
player_merge|Gộp tài khoản|Gộp khách vào tài khoản chính
`},
  {
    surface: "console", module: "moderation", roles: ["admin"], screens: `
reports_queue|Hàng đợi tố cáo|Tố cáo chờ xử lý
report_detail|Chi tiết tố cáo|Bằng chứng, quyết định
chat_review|Duyệt chat|Tin nhắn bị gắn cờ
name_review|Duyệt tên|Tên người chơi/bang
ugc_review|Duyệt level UGC|Level bị báo cáo/chờ nổi bật
sanctions_list|Danh sách xử phạt|Xử phạt đang hiệu lực
appeals_queue|Hàng đợi kháng nghị|Kháng nghị chờ xử lý
appeal_detail|Chi tiết kháng nghị|Quyết định kháng nghị
`},
  {
    surface: "console", module: "support", roles: ["admin"], screens: `
tickets_queue|Hàng đợi ticket|Ticket theo SLA
ticket_detail|Chi tiết ticket|Trả lời, gắn nhãn
csat|Mức hài lòng|CSAT theo nhân viên
refund_requests|Yêu cầu hoàn tiền|Duyệt hoàn tiền
`},
  {
    surface: "console", module: "liveops", roles: ["admin"], screens: `
liveops_calendar|Lịch LiveOps|Sự kiện, banner, bảo trì trên 1 lịch
remote_config|Remote config|Tham số runtime theo môi trường
segment_builder|Tạo phân khúc|Điều kiện nhóm người chơi
ab_test_results|Kết quả A/B|Ý nghĩa thống kê
store_pricing_matrix|Bảng giá|Giá theo quốc gia/store
promo_redemptions|Lượt đổi mã|Thống kê mã khuyến mãi
economy_simulator|Mô phỏng kinh tế|Giả lập lạm phát tiền tệ
drop_rate_audit|Kiểm toán tỉ lệ rơi|Tỉ lệ thực tế vs công bố
`},
  {
    surface: "console", module: "operations", roles: ["admin", "dev"], screens: `
service_health|Sức khỏe dịch vụ|Trạng thái từng service
live_ccu_map|Bản đồ CCU|Người chơi online theo vùng
live_battle_feed|Luồng trận trực tiếp|Trận đang diễn ra
live_purchase_feed|Luồng giao dịch|Giao dịch thời gian thực
incident_list|Sự cố|Danh sách sự cố
incident_detail|Chi tiết sự cố|Diễn biến, hành động
oncall_schedule|Lịch trực|Phân công trực
alert_rules|Luật cảnh báo|Ngưỡng hạ tầng
status_page_editor|Sửa trang trạng thái|Cập nhật status công khai
deployments|Triển khai|Lịch sử deploy theo môi trường
environments|Môi trường|Cấu hình từng môi trường
content_release_diff|So sánh bản nội dung|Diff giữa 2 content pack
content_rollback|Hoàn tác nội dung|Rollback content pack
backups|Sao lưu|Sao lưu/khôi phục CSDL
audit_log|Nhật ký kiểm toán|Mọi thao tác staff
integrations|Tích hợp|Store, thanh toán, Slack
localization_status|Tiến độ dịch|% dịch theo locale
system_settings|Cài đặt hệ thống|Cấu hình chung
`},
  {
    surface: "console", module: "finance", roles: ["admin"], screens: `
revenue_report|Báo cáo doanh thu|Theo ngày/store/quốc gia
store_reconciliation|Đối soát store|Khớp giao dịch với Apple/Google
tax_report|Báo cáo thuế|Theo khu vực
chargebacks|Chargeback|Giao dịch bị đòi lại
fraud_detection|Phát hiện gian lận|Giao dịch bất thường
fraud_case|Hồ sơ gian lận|Xử lý một vụ
`},
  {
    surface: "console", module: "anticheat", roles: ["admin", "dev"], screens: `
anticheat_flags|Cờ gian lận|Tài khoản bị gắn cờ
battle_validation_failures|Trận không hợp lệ|Kết quả client ≠ mô phỏng server
anomaly_detail|Chi tiết bất thường|Bằng chứng, replay
device_fingerprints|Dấu vân thiết bị|Nhiều tài khoản cùng thiết bị
ban_waves|Đợt khóa|Lên lịch khóa hàng loạt
`},
  {
    surface: "console", module: "compliance", roles: ["admin"], screens: `
gacha_disclosure|Công bố tỉ lệ|Báo cáo tỉ lệ theo luật từng nước
age_rating|Xếp hạng tuổi|Cấu hình theo khu vực
data_retention|Lưu trữ dữ liệu|Chính sách thời hạn lưu
consent_records|Hồ sơ đồng ý|Lịch sử đồng ý của người chơi
`},
  {
    surface: "studio", module: "editors", roles: ["creator"], screens: `
studio_home|Trang chủ Studio|Việc cần làm, thay đổi gần đây
map_editor|Sửa bản đồ|Lớp nền, collider, vùng (tích hợp Tiled)
map_object_editor|Sửa đối tượng bản đồ|NPC, rương, biển báo
encounter_placer|Đặt trận trên bản đồ|Gắn encounter vào vùng
teleport_graph|Đồ thị cổng dịch chuyển|Liên kết map↔map, phát hiện cổng hỏng
world_graph|Đồ thị thế giới|Vùng, chương, điều kiện mở khóa
dialogue_graph|Sửa hội thoại dạng đồ thị|Nhánh, điều kiện, biến
dialogue_preview|Xem trước hội thoại|Chạy thử hội thoại
cutscene_timeline|Timeline cutscene|Camera, thoại, hoạt ảnh
quest_flow|Sửa luồng nhiệm vụ|Chuỗi nhiệm vụ, điều kiện
quest_preview|Xem trước nhiệm vụ|Chạy thử luồng nhiệm vụ
encounter_grid|Sửa trận 3x3|Kế thừa Level Editor hiện có
encounter_simulator|Mô phỏng trận|Chạy N trận với seed, tỉ lệ thắng
balance_lab|Phòng cân bằng|So sánh chỉ số, lực chiến toàn roster
stat_curve_editor|Đường cong chỉ số|Tăng trưởng theo cấp/sao
formula_playground|Thử công thức|Công thức sát thương/phòng thủ
loot_simulator|Mô phỏng rơi đồ|Phân phối rơi đồ
gacha_simulator|Mô phỏng chiêu mộ|Tỉ lệ + pity
economy_flow|Dòng kinh tế|Nguồn/chi theo tiến trình
skill_vfx_preview|Xem VFX kỹ năng|Ghép kỹ năng với hiệu ứng
animation_preview|Xem hoạt ảnh|Idle/walk/attack…
atlas_packer|Đóng gói atlas|Chạy TexturePacker, xem kết quả
sprite_import|Nhập sprite|Nhập từ Aseprite
sprite_slicer|Cắt sprite|Cắt sheet thành frame
palette_manager|Bảng màu|Palette chuẩn
music_playlist|Danh sách nhạc|Nhạc theo vùng/trạng thái
localization_editor|Sửa bản dịch|Theo key/locale
localization_review|Duyệt bản dịch|Quy trình duyệt
translator_queue|Hàng đợi dịch|Key chưa dịch
translation_memory|Bộ nhớ dịch|Gợi ý từ bản dịch cũ
glossary_editor|Sửa thuật ngữ|Thuật ngữ bắt buộc
lore_wiki|Wiki lore|Tài liệu thế giới
validation_report|Báo cáo kiểm tra nội dung|Lỗi schema, tham chiếu hỏng
content_diff|So sánh nội dung|Diff giữa nhánh nội dung
content_branches|Nhánh nội dung|Làm việc song song
content_publish|Xuất bản nội dung|Build content pack
asset_usage_graph|Đồ thị sử dụng asset|Asset nào dùng ở đâu
unused_assets|Asset không dùng|Danh sách để dọn
ui_layout_editor|Sửa bố cục UI|Layout màn hình theo token
theme_tokens|Design tokens|Màu, font, khoảng cách
screen_catalog|Danh mục màn hình|Duyệt catalog này
`},
  {
    surface: "studio", module: "production", roles: ["creator"], screens: `
character_bible|Hồ sơ nhân vật|Tính cách, giọng, quan hệ
voice_lines|Danh sách thoại|Câu thoại cần thu âm
voice_tracker|Tiến độ thu âm|Trạng thái từng câu
story_timeline|Dòng thời gian cốt truyện|Sự kiện theo niên đại
art_requests|Bảng yêu cầu art|Yêu cầu vẽ từ design
art_request_detail|Chi tiết yêu cầu art|Brief, tham khảo, hạn
art_review|Duyệt art|Duyệt/yêu cầu sửa
concept_gallery|Thư viện concept|Concept art đã duyệt
map_playtest_heatmap|Heatmap playtest|Người chơi kẹt ở đâu
difficulty_curve|Đường cong độ khó|Độ khó theo tiến trình
`},
  {
    surface: "devtools", module: "engineering", roles: ["dev"], screens: `
builds|Danh sách build|Build theo nền tảng/kênh
build_detail|Chi tiết build|Artifact, log, kích thước
build_trigger|Chạy build|Kích hoạt pipeline
pipelines|Pipeline|Trạng thái CI/CD
test_results|Kết quả test|Unit/integration/e2e
flaky_tests|Test chập chờn|Phát hiện test không ổn định
coverage|Độ phủ|Coverage theo module
crashes|Crash|Nhóm crash theo chữ ký
crash_detail|Chi tiết crash|Stack, thiết bị, breadcrumb
log_search|Tìm log|Log tập trung
traces|Trace|Trace phân tán
perf_benchmarks|Benchmark|Hiệu năng theo build
perf_regression|Suy giảm hiệu năng|So sánh giữa build
app_size|Kích thước app|Theo nền tảng
asset_size|Kích thước asset|Asset lớn nhất
api_explorer|API explorer|Gọi thử API
api_schema_diff|Diff schema API|Thay đổi phá vỡ
db_migrations|Migration CSDL|Trạng thái migration
db_readonly_query|Truy vấn chỉ đọc|SQL chỉ đọc có kiểm toán
env_config_diff|Diff cấu hình|So sánh giữa môi trường
secrets_audit|Kiểm toán secret|Secret sắp hết hạn
dependency_updates|Cập nhật thư viện|Thư viện lỗi thời/lỗ hổng
replay_debugger|Debug replay|Kiểm tra tính tất định của trận
save_migration_tester|Kiểm tra migrate save|Chạy migrator trên save mẫu
protocol_inspector|Soi protocol|Gói tin client↔server
screen_registry|Registry màn hình|ScreenId ↔ lớp code
screenshot_diff|So ảnh màn hình|Khác biệt giữa build
missing_loc_keys|Key dịch thiếu|Key dùng trong code chưa có bản dịch
load_tests|Kiểm thử tải|Kết quả load test
chaos_experiments|Chaos test|Thử lỗi hạ tầng
cdn_cache|CDN|Trạng thái cache content pack
content_pack_versions|Phiên bản content pack|Tương thích client
client_config_matrix|Ma trận cấu hình client|Build × môi trường × flag
developer_portal|Cổng nhà phát triển|API công khai cho bên thứ ba
webhooks|Webhook|Đăng ký sự kiện
`},
  {
    surface: "devtools", module: "release", roles: ["dev"], screens: `
code_ownership|Chủ sở hữu mã|Module ↔ đội phụ trách
architecture_rules|Luật kiến trúc|Vi phạm phụ thuộc giữa module
release_train|Chuyến tàu release|Lịch cắt nhánh, freeze
hotfix_request|Yêu cầu hotfix|Quy trình hotfix
sdk_versions|Phiên bản SDK|SDK bên thứ ba theo nền tảng
`},
  {
    surface: "qa", module: "insights", roles: ["qa"], screens: `
bug_duplicates|Lỗi trùng|Gộp lỗi trùng
test_charter|Charter kiểm thử|Mục tiêu phiên khám phá
quality_trend|Xu hướng chất lượng|Lỗi theo release
agent_failure_clusters|Nhóm lỗi agent|Gom lỗi agent theo nguyên nhân
agent_budget|Ngân sách agent|Chi phí mỗi lượt chạy agent
`},
  {
    surface: "qa", module: "quality", roles: ["qa"], screens: `
qa_home|Trang chủ QA|Tình trạng chất lượng bản hiện tại
test_plans|Kế hoạch test|Theo release
test_plan_detail|Chi tiết kế hoạch|Phạm vi, người phụ trách
test_cases|Test case|Kho test case
test_case_edit|Sửa test case|Bước, kỳ vọng, ScreenId liên quan
test_run_create|Tạo lượt test|Chọn case × thiết bị
test_run_execute|Thực thi test|Đánh dấu pass/fail từng bước
test_run_results|Kết quả lượt test|Tổng hợp
bugs|Danh sách lỗi|Lọc theo mức độ
bug_detail|Chi tiết lỗi|Tái hiện, log, video
bug_create|Tạo lỗi|Mẫu báo lỗi chuẩn
bug_triage|Phân loại lỗi|Họp triage
regression_suite|Bộ hồi quy|Case hồi quy tự động
device_matrix|Ma trận thiết bị|Thiết bị × hệ điều hành
device_farm|Trang trại thiết bị|Trạng thái thiết bị thật
agent_runs|Lượt chạy test agent|Kết quả agent tự động
agent_run_detail|Chi tiết lượt agent|Hành động, ảnh, lỗi
agent_scenarios|Kịch bản agent|Mục tiêu agent phải đạt
agent_coverage|Độ phủ màn hình|ScreenId đã/chưa được agent ghé
visual_regression|Hồi quy giao diện|Duyệt khác biệt ảnh
balance_regression|Hồi quy cân bằng|Tỉ lệ thắng thay đổi bất thường
smoke_checklist|Smoke test|Checklist nhanh mỗi build
release_signoff|Ký duyệt release|Điều kiện phát hành
compatibility_report|Tương thích|Kết quả theo thiết bị
accessibility_audit|Kiểm tra trợ năng|Tương phản, cỡ chữ
localization_qa|QA bản dịch|Tràn chữ, sai ngữ cảnh
performance_qa|QA hiệu năng|FPS, bộ nhớ, pin
save_compat_matrix|Tương thích bản lưu|Save cũ × build mới
known_issues|Lỗi đã biết|Bảng lỗi đã biết
exploratory_session|Phiên kiểm thử khám phá|Ghi chú theo thời gian
session_replay|Xem lại phiên|Video + log đồng bộ
crash_repro|Tái hiện crash|Bước tái hiện
test_data_generator|Sinh dữ liệu test|Tạo tài khoản/save theo mẫu
save_fixtures|Thư viện save mẫu|Save ở từng mốc tiến trình
network_conditions|Giả lập mạng|Mất gói, độ trễ
`},
  {
    surface: "pilot", module: "program", roles: ["pilot"], screens: `
pilot_signup|Đăng ký pilot|Đăng ký tham gia thử nghiệm
pilot_nda|Thỏa thuận bảo mật|Ký NDA điện tử
pilot_profile|Hồ sơ pilot|Kinh nghiệm, sở thích thể loại
pilot_devices|Thiết bị của tôi|Thiết bị dùng để test
pilot_builds|Tải build|Build được phép tải
pilot_build_notes|Ghi chú build|Trọng tâm cần test
pilot_missions|Nhiệm vụ pilot|Kịch bản được giao
pilot_mission_detail|Chi tiết nhiệm vụ|Hướng dẫn, hạn chót
pilot_feedback|Góp ý đã gửi|Trạng thái góp ý
pilot_feedback_new|Gửi góp ý|Mẫu góp ý
pilot_bugs|Lỗi đã báo|Trạng thái xử lý
pilot_bug_new|Báo lỗi|Mẫu báo lỗi + tải video
pilot_surveys|Khảo sát|Khảo sát cần làm
pilot_survey_take|Làm khảo sát|Trả lời khảo sát
pilot_leaderboard|Bảng đóng góp|Pilot đóng góp nhiều nhất
pilot_rewards|Quà pilot|Quà đã nhận/có thể nhận
pilot_forum|Diễn đàn pilot|Thảo luận kín
pilot_forum_thread|Chủ đề diễn đàn|Chi tiết thảo luận
pilot_calendar|Lịch playtest|Buổi playtest sắp tới
pilot_playtest_booking|Đặt lịch playtest|Đăng ký buổi có quan sát
pilot_live_room|Phòng playtest trực tiếp|Chia sẻ màn hình, thoại
pilot_build_vote|Bình chọn phương án|So sánh 2 phương án thiết kế
pilot_telemetry_consent|Đồng ý telemetry|Quyền ghi heatmap/video
pilot_exit|Rời chương trình|Rời và xóa dữ liệu pilot
`},
  {
    surface: "pilot", module: "onboarding", roles: ["pilot"], screens: `
pilot_cohort|Nhóm của tôi|Thông tin cohort pilot
pilot_guide|Hướng dẫn pilot|Cách test và báo lỗi hiệu quả
pilot_certificate|Chứng nhận pilot|Ghi nhận đóng góp
`},
  {
    surface: "site", module: "public", roles: ["user", "gamer"], screens: `
landing|Trang chủ|Giới thiệu game, tải về
news|Tin tức|Danh sách tin
news_detail|Chi tiết tin|Bài viết
patch_notes_web|Ghi chú cập nhật|Theo phiên bản
guides|Cẩm nang|Danh sách hướng dẫn
guide_detail|Chi tiết cẩm nang|Bài hướng dẫn
heroes_gallery|Thư viện anh hùng|Toàn bộ anh hùng
hero_page|Trang anh hùng|Chi tiết một anh hùng
media_kit|Media kit|Tài nguyên báo chí
faq_web|FAQ|Câu hỏi thường gặp
support_portal|Cổng hỗ trợ|Gửi/xem ticket
status_page|Trạng thái dịch vụ|Tình trạng server công khai
legal_terms|Điều khoản|ToS
legal_privacy|Chính sách riêng tư|Privacy policy
community_forum|Diễn đàn|Cộng đồng người chơi
forum_thread|Chủ đề diễn đàn|Thảo luận
fan_art|Fan art|Thư viện sáng tác cộng đồng
creator_program|Chương trình creator|Quyền lợi cho UGC creator
ugc_web_browser|Duyệt level UGC trên web|Tìm và gửi level vào game
account_login|Đăng nhập tài khoản|Cổng tài khoản
account_overview|Tổng quan tài khoản|Thông tin, slot
account_security|Bảo mật|Mật khẩu, 2FA, thiết bị
account_purchases|Lịch sử mua|Giao dịch
account_linked|Tài khoản liên kết|Google/Apple
account_delete|Xóa tài khoản|Xóa từ web
redeem_code|Đổi mã quà|Nhập mã khuyến mãi
esports|Esports|Giải đấu chính thức
events_public|Lịch sự kiện|Sự kiện sắp tới
roadmap_public|Lộ trình phát triển|Tính năng sắp ra mắt
`},
];

const matrices = [
  {
    surface: "console", module: "liveops_entities", roles: ["admin"],
    views: [
      ["list", "Danh sách", "Tìm, lọc, sắp xếp"],
      ["detail", "Chi tiết", "Xem đầy đủ + trạng thái"],
      ["editor", "Soạn thảo", "Tạo/sửa có kiểm tra hợp lệ"],
      ["history", "Lịch sử", "Phiên bản, người sửa, diff"],
      ["bulk", "Hàng loạt", "Nhập/xuất CSV, sửa nhiều bản ghi"],
    ],
    entities: `
events|Sự kiện
banners|Banner chiêu mộ
offers|Gói ưu đãi
bundles|Combo
promo_codes|Mã khuyến mãi
mail_templates|Mẫu thư
push_campaigns|Chiến dịch push
announcements|Thông báo
segments|Phân khúc
ab_tests|Thử nghiệm A/B
feature_flags|Feature flag
leaderboard_seasons|Mùa xếp hạng
battle_pass_seasons|Mùa Battle Pass
tournaments|Giải đấu
guilds|Bang hội
staff_users|Tài khoản nhân viên
roles|Vai trò & quyền
api_keys|API key
word_filters|Bộ lọc từ ngữ
maintenance_windows|Lịch bảo trì
store_products|Sản phẩm IAP
support_macros|Mẫu trả lời CSKH
faq_articles|Bài FAQ
news_posts|Bài tin tức
patch_notes|Ghi chú cập nhật
pilot_cohorts|Nhóm pilot
pilot_builds|Build pilot
pilot_surveys|Khảo sát pilot
content_releases|Bản phát hành nội dung
`,
  },
  {
    surface: "studio", module: "content_entities", roles: ["creator"],
    views: [
      ["list", "Danh sách", "Tìm, lọc, kiểm tra lỗi"],
      ["detail", "Chi tiết", "Xem đầy đủ + nơi được tham chiếu"],
      ["editor", "Soạn thảo", "Form theo JSON Schema + xem trước"],
      ["history", "Lịch sử", "Phiên bản, diff, hoàn tác"],
      ["bulk", "Hàng loạt", "Nhập/xuất bảng tính, sửa nhiều"],
      ["promote", "Đẩy môi trường", "So sánh dev→staging→prod và đẩy"],
    ],
    entities: `
hero_classes|Lớp nhân vật
heroes|Anh hùng
skills|Kỹ năng
skill_effects|Hiệu ứng kỹ năng
statuses|Trạng thái (buff/debuff)
passives|Nội tại
items|Vật phẩm
item_categories|Loại vật phẩm
equipment|Trang bị
equipment_sets|Bộ trang bị
gems|Ngọc khảm
recipes|Công thức chế tạo
enemies|Quái
bosses|Boss
boss_phases|Pha boss
encounters|Trận đấu
maps|Bản đồ
regions|Vùng
tilesets|Tileset
npcs|NPC
dialogues|Hội thoại
quests|Nhiệm vụ
quest_chains|Chuỗi nhiệm vụ
achievements|Thành tựu
titles|Danh hiệu
checkin_tables|Bảng điểm danh
loot_tables|Bảng rơi đồ
recruit_pools|Nhóm chiêu mộ
currencies|Tiền tệ
shop_catalogs|Danh mục cửa hàng
pets|Thú cưng
buildings|Công trình làng
cutscenes|Cutscene
tutorials|Hướng dẫn
loc_keys|Key bản dịch
audio_cues|Âm thanh
vfx|Hiệu ứng hình ảnh
sprite_sets|Bộ sprite
ui_themes|Chủ đề UI
game_modes|Chế độ chơi
`,
  },
];

for (const g of groups) g.season = "launch";
for (const m of matrices) m.season = "launch";
groups.push(...expansionGroups);
matrices.push(...expansionMatrices);

const parseLines = (text) =>
  text.trim().split("\n").map((line) => line.split("|").map((s) => s.trim()));

const rows = [];
for (const g of groups) {
  for (const [id, name, purpose] of parseLines(g.screens)) {
    rows.push({ id: `${g.surface}.${g.module}.${id}`, surface: g.surface, module: g.module, name, purpose, roles: g.roles, origin: "unique", season: g.season });
  }
}
for (const m of matrices) {
  for (const [entity, entityName] of parseLines(m.entities)) {
    for (const [view, viewName, viewPurpose] of m.views) {
      rows.push({
        id: `${m.surface}.${m.module}.${entity}.${view}`, surface: m.surface, module: m.module,
        name: `${entityName} — ${viewName}`, purpose: viewPurpose, roles: m.roles, origin: "matrix", season: m.season,
      });
    }
  }
}

const ids = new Set();
for (const r of rows) {
  if (ids.has(r.id)) throw new Error(`Duplicate screen id: ${r.id}`);
  ids.add(r.id);
}

const count = (pred) => rows.filter(pred).length;
const here = dirname(fileURLToPath(import.meta.url));
const outDir = resolve(here, "../../docs/screens");
mkdirSync(outDir, { recursive: true });

const csv = ["id,surface,module,name,purpose,roles,origin,season"]
  .concat(rows.map((r) => [r.id, r.surface, r.module, r.name, r.purpose, r.roles.join(" "), r.origin, r.season]
    .map((v) => `"${String(v).replaceAll('"', '""')}"`).join(",")))
  .join("\n");
writeFileSync(resolve(outDir, "screens.csv"), csv + "\n", "utf8");
writeFileSync(resolve(outDir, "screens.json"), JSON.stringify(rows, null, 2) + "\n", "utf8");

const md = [];
md.push("# Danh mục màn hình (Screen Catalog)", "");
md.push("> Sinh tự động bởi `node tools/screen-catalog/catalog.mjs`. Không sửa tay — sửa `catalog.mjs` rồi chạy lại.", "");
md.push(`**Tổng: ${rows.length} màn hình** — ${count((r) => r.origin === "unique")} màn hình thiết kế riêng, ${count((r) => r.origin === "matrix")} màn hình quản trị thực thể (ma trận thực thể × view).`, "");
md.push("## Theo bề mặt sản phẩm", "", "| Bề mặt | Unique | Matrix | Tổng |", "|---|---:|---:|---:|");
for (const [key, label] of Object.entries(SURFACES)) {
  md.push(`| ${label} | ${count((r) => r.surface === key && r.origin === "unique")} | ${count((r) => r.surface === key && r.origin === "matrix")} | ${count((r) => r.surface === key)} |`);
}
md.push("", "## Theo mùa phát hành", "", "| Mùa | Unique | Matrix | Tổng | Lũy kế |", "|---|---:|---:|---:|---:|");
let cumulative = 0;
for (const [key, label] of Object.entries(SEASONS)) {
  const total = count((r) => r.season === key);
  cumulative += total;
  md.push(`| ${label} | ${count((r) => r.season === key && r.origin === "unique")} | ${count((r) => r.season === key && r.origin === "matrix")} | ${total} | ${cumulative} |`);
}
md.push("", "## Theo vai trò (một màn hình có thể phục vụ nhiều vai trò)", "", "| Vai trò | Số màn hình |", "|---|---:|");
for (const [key, label] of Object.entries(ROLES)) md.push(`| ${label} | ${count((r) => r.roles.includes(key))} |`);
md.push("");
let current = "";
for (const r of rows) {
  const section = `${SURFACES[r.surface]} › ${r.module}`;
  if (section !== current) {
    current = section;
    md.push("", `### ${section}`, "", "| ScreenId | Tên | Mục đích | Vai trò | Mùa |", "|---|---|---|---|---|");
  }
  md.push(`| \`${r.id}\` | ${r.name} | ${r.purpose} | ${r.roles.join(", ")} | ${r.season} |`);
}
writeFileSync(resolve(outDir, "SCREEN_CATALOG.md"), md.join("\n") + "\n", "utf8");

console.log(`total=${rows.length} unique=${count((r) => r.origin === "unique")} matrix=${count((r) => r.origin === "matrix")}`);
for (const key of Object.keys(SURFACES)) console.log(`  ${key}: ${count((r) => r.surface === key)}`);
for (const key of Object.keys(SEASONS)) console.log(`  season ${key}: ${count((r) => r.season === key)} (unique ${count((r) => r.season === key && r.origin === "unique")})`);
for (const key of Object.keys(ROLES)) console.log(`  role ${key}: ${count((r) => r.roles.includes(key))}`);
