# Tiến độ viết lại

> Cập nhật: 2026-09-25 · Kế hoạch: [MASTER_PLAN.md](MASTER_PLAN.md) · Điểm quay lại code cũ: tag `legacy-baseline`

## Trạng thái theo phase

| Phase | Trạng thái | Bằng chứng |
|---|---|---|
| P0 Nền móng | **Gần xong** | Version catalog, module Kotlin, `.gitignore` sửa, ADR 0001–0013, CI (`.github/workflows/ci.yml`), codegen `GameScreenId` (600) + `screenIds.ts` (1.494) với `--check` |
| P1 Domain + nội dung | **Lõi xong** | `game/domain` 35 test (6 golden); `content/` migrate 298 bản ghi, 0 lỗi validate; `compileContent` + mô phỏng cân bằng; `game/application` 12 test; save v2 + importer 6 test trên 3 save thật |
| P2 Vertical slice client | Chưa bắt đầu | — |
| P3 Asset pipeline | Chưa bắt đầu | Bản đồ asset legacy đã có trong `content/assets/legacy_asset_map.json` (validator kiểm tra từng region atlas) |
| P4–P8, S1–S4 | Chưa bắt đầu | — |

**Test:** 109 test xanh — domain 35, application 12, screens 3, content 7, infrastructure 6, legacy core 46.

## Còn thiếu trong P0/P1

- [ ] `build-logic/` convention plugins (hiện mỗi module lặp lại khối cấu hình Kotlin)
- [ ] Test kiến trúc (Konsist) cho luật phụ thuộc §4.1
- [ ] Spike TeaVM + Fleks + KTX (ADR 0009)
- [ ] JSON Schema xuất ra cho Studio (hiện schema nằm ở kiểu Kotlin parse chặt)
- [ ] `QuestTracker` + `AchievementTracker` lắng nghe `GameEvent`
- [ ] `sim-cli` riêng cho mô phỏng hàng loạt (hiện nằm trong `compileContent`)
- [ ] Trạng thái `reflect` và `immune` (16/18 trạng thái đã có)

## Lỗi legacy đã sửa trong code mới (có test)

| Lỗi cũ | Sửa ở | Test |
|---|---|---|
| Không bao giờ chí mạng | `BattleEngine.strike` | `critical hits happen when crit rate is positive` |
| Kỹ năng 2/3 gần như không dùng | Hồi chiêu + năng lượng chỉ cho tuyệt kỹ | `skills and ultimates are actually cast` |
| Hòa bị tính là thua | `BattleOutcome.DRAW` | `stalemate ends in a draw…` |
| Trang bị không cộng chỉ số | `GameRules.heroStats` | `equipment bonuses reach hero stats`, `imported equipment now actually raises hero stats` |
| Điểm danh không cộng quà | `GameRules.claimCheckin` | `check-in actually grants its reward…` |
| Mua hàng không lưu tiền | Mua là một transition state → save | `shop purchase debits wallet…` |
| Ghép sao xóa anh hùng đang trong đội hình | `GameRules.raiseStar` | `star raise refuses to consume a hero standing in the lineup` |
| Save ghi vào thư mục repo, hỏng là mất | `FileSaveStore` ghi nguyên tử + 3 backup | `file store rotates backups and recovers…` |
| Luật `.gitignore` giấu 7 file loader khỏi git | Neo `/data/` | commit `4eadbaf` |

## Phát hiện trong lúc làm (đã cập nhật plan)

1. Thiết kế năng lượng ở plan v1 (kỹ năng tốn 40) làm tuyệt kỹ gần như không bao giờ được dùng — golden test phát hiện, đổi sang hồi chiêu (§4.5).
2. `evasion_up`/`crit_up` theo ‰ vô tác dụng khi chỉ số gốc = 0 — thêm `StatBonus` cộng phẳng.
3. Dữ liệu cũ có vòng khắc chế 2-lớp phong phú hơn vòng tròn trong plan — giữ thiết kế của creator; `class.mage` có `weakAgainst` mâu thuẫn (ghi trong `content/MIGRATION_REPORT.md`).
4. Cân bằng: starter một mình thắng 4% ở `encounter.dawnvillage_01.e1` — xem `content/BALANCE_NOTES.md`.

## Cần người duyệt

- Tên 6 anh hùng khởi đầu tôi đặt: Aldric (Warrior), Nyx (Assassin), Selene (Mage), Fenn (Ranger), Mirae (Support), Borin (Tank).
- Quy đổi chỉ số legacy → mới (hp×10, atk×10, def×10, speed = 80 + agi×3, crit×20‰) và bộ kỹ năng mới trong `content/skills/`.
- Phần thưởng nhiệm vụ trước đây là khóa treo (`reward_00x`) đã được gán vật phẩm cụ thể — danh sách trong `content/MIGRATION_REPORT.md`.

## Bước tiếp theo: P2

`game/client` (libGDX + KTX + Fleks), `GameApp` composition root, `Navigator` theo `GameScreenId`, ui kit, 110 màn lõi, `game/platform-desktop`, automation protocol + scenario runner đầu tiên.
