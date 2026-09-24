# Báo cáo migrate dữ liệu legacy → content/

> Sinh bởi `node tools/content-migrator/migrate-legacy.mjs` từ `assets/data` (tag `legacy-baseline`).

**Kết quả:** 5 currencies, 6 classes, 16 statuses, 18 skills, 6 heroes, 6 enemies, 10 encounters, 52 items, 166 equipment, 5 quests, 6 achievements, 1 check-in tables, 343 vi keys, 131 en keys.

## Quyết định và điểm cần creator duyệt

- Chỉ số anh hùng/quái: hp×10, atk×10, def×10, speed = 80 + agi×3, critRate‰ = crit×20, critDamage 1500‰.
- class.warrior: đổi tham chiếu lớp "Archer" thành class.ranger.
- class.assassin: đổi tham chiếu lớp "Archer" thành class.ranger.
- class.mage: đổi tham chiếu lớp "Archer" thành class.ranger.
- class.tank: đổi tham chiếu lớp "Archer" thành class.ranger.
- class.mage: weakAgainst cũ ["class.tank","class.warrior"] không khớp với counters của lớp khác ["class.warrior"]; nguồn sự thật mới chỉ là counters.
- crit_up (+250‰ chí mạng) và evasion_up (+300‰ né) là stat_bonus cộng phẳng, vì chỉ số né gốc = 0 khiến hệ số ‰ vô tác dụng.
- Bộ kỹ năng: giữ tên kỹ năng tiếng Việt từ character_base.json; ý đồ hiệu ứng từ skill_base.json (armor→defense_up, dodgeChance→evasion_up, critChance→crit_up, heal/targets→lowest_hp_allies, attackTimes→hits). damageReflection chưa có StatusKind — tank dùng khiên + tăng thủ thay thế.
- Quái: 6 enemy.bandit_* dùng tạm sprite của lớp anh hùng tương ứng (tag art_pending). Atlas 02/04/08/10Knight sẽ được gán khi có concept quái (P3).
- Trận: lưới cũ "cột,hàng" của phe địch → depth = cột (cột 0 gần phe ta nhất = hàng trước), lane = hàng.
- Tạo mới encounter.ashwaste_02.e1/e2 cho 2 đối tượng quái trên wasteland2.tmx vốn không có file trận.
- encounter.dawnvillage_01.e1 (village_0_1 cũ) không có đối tượng quái tương ứng trong village_0.tmx — validator sẽ cảnh báo đến khi map được migrate.
- item.arrowbox_t1: giá cũ -1 là giá trị quy ước "không bán" → không có mục shop.
- Vật phẩm: food dùng được (+tier×100 EXP như code cũ); giá trị itemConfig của metal/water/diamond/gem/gemstore giữ ở materialValue.
- Trang bị: atk/def/hp ×10, agi→speed ×3, crit→critRate‰ ×20, mp (không dùng trong trận cũ) → effectHit ×10.
- mission_004 → quest.side.defend_the_village: reward_007 (khóa treo) → item.food_t1; reward_008 → currency.gold.
- mission_001 → quest.side.the_hidden_stone: reward_001 (khóa treo) → item.diamond_t1; reward_002 → currency.gold.
- mission_002 → quest.side.forge_supplies: reward_003 (khóa treo) → item.metal_t2; reward_004 → currency.gold.
- mission_003 → quest.side.monster_hunt: reward_005 (khóa treo) → item.food_t2; reward_006 → currency.gold.
- mission_005 → quest.side.dungeon_secret: reward_009 (khóa treo) → item.key_t1; reward_010 → currency.gold.
- Thành tựu: trường dec → description (sửa lỗi chính tả "từn"); thêm 3 bậc mục tiêu + thưởng ngọc (dữ liệu cũ chỉ có bộ đếm).
- Luật trận: counterMultiplier 1.25 → 1250‰; maxRounds 100 hành động → 30 vòng CTB; MP cũ (mpSkill2Cost/mpSkill3Cost) thay bằng hồi chiêu + năng lượng tuyệt kỹ.
