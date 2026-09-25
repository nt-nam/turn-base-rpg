# 01 · Sản phẩm và thiết kế game

> Ý tưởng, trụ cột thiết kế và **luật thiết kế bắt buộc**. Mọi WP nội dung (đợt C, E) phải tuân theo. Bản đầy đủ: [MASTER_PLAN §2, §3, §18](../MASTER_PLAN.md).

## 1. Một câu định vị
**PXWORLD: Cổ Vật Hoàng Hôn** là RPG theo lượt 2D pixel-art. Người chơi xếp đội 3×3, khám phá bản đồ Tiled, chơi offline-first. Phần online mở rộng gồm cloud save, sự kiện, PvP bất đồng bộ, bang hội và level do người chơi tạo (UGC). Cốt truyện dài 12–15 giờ qua 6 chương. Endgame gồm Tháp, Vực Thẳm, sự kiện và đấu trường.

## 2. Năm trụ cột thiết kế
| # | Trụ cột | Hệ quả bắt buộc |
|---|---|---|
| 1 | **Chiến thuật đọc được** | Mọi con số trong trận đều giải thích được (log trận, xem trước sát thương). Không có cơ chế ẩn. Kết quả là hàm của seed và lệnh. |
| 2 | **Đội hình là trung tâm** | Lưới 3×3 có ý nghĩa: hàng trước nhận ×1.0, hàng giữa ×0.9, hàng sau ×0.8 sát thương. Vòng khắc chế làm việc chọn đội quan trọng hơn chỉ số. |
| 3 | **Tôn trọng thời gian người chơi** | Vòng ngắn 5 phút, có chế độ auto, không bắt chơi mỗi ngày mới giữ được tiến độ. Năng lượng chỉ giới hạn nội dung ngoài cốt truyện. |
| 4 | **Công bằng** | Không có sức mạnh chỉ mua được bằng tiền thật. Tỉ lệ chiêu mộ được công khai (`console.compliance.gacha_disclosure`). Có pity. |
| 5 | **Văn hoá Việt, đọc được toàn cầu** | Lễ hội và tên gọi mang chất Việt, mọi chuỗi có bản vi và en ngay từ đầu. |

## 3. Luật thiết kế bắt buộc (áp cho mọi nội dung mới)
1. **Nội dung là dữ liệu.** Anh hùng, kỹ năng, quái, trận, quest, hội thoại, cửa hàng và sự kiện đều nằm trong `content/`. Không được hard-code nội dung trong Kotlin. Thiếu trường nào thì mở rộng schema, không viết ngoại lệ trong code.
2. **Số nguyên và permille.** Mọi tỉ lệ viết theo ‰ (`critRate: 40` nghĩa là 4%). Không dùng số thực trong content.
3. **ID không bao giờ đổi** sau khi phát hành: `<loại>.<slug>` (ví dụ `hero.aldric`, `encounter.ashwaste_01.e0`). Tên hiển thị là `LocKey`, có đủ vi và en.
4. **Mỗi encounter có vai trò và dải tỉ lệ thắng** (`tutorial`/`normal`/`gate`/`boss`). Nó phải nằm trong dải khi đánh bằng đội hình theo tiến trình (WP-A3). Encounter ngoài dải là lỗi build.
5. **Mọi thay đổi tiền tệ đi qua `Wallet` và để lại `LedgerEntry`** có nguồn và lý do. Không tặng tài nguyên kiểu "ngầm".
6. **Mọi phần thưởng dùng chung một kiểu** (`Reward`/`RewardBundle`, grant). Trận, quest, thư, điểm danh và cửa hàng dùng cùng một đường cấp phát.
7. **Hòa là kết quả riêng**, không phải thua. Trận kết thúc khi một phe hết quân hoặc sau 30 vòng.
8. **Không có nội dung nào chỉ chạy được online** ở launch, trừ tính năng xã hội.
9. **Art chưa có không chặn nội dung**: dùng `art_pending` và placeholder, ghi yêu cầu art (`studio.production.art_requests`).
10. **Không làm** (MASTER_PLAN §18.3): chiến đấu thời gian thực, 3D, MMO đồng bộ, voice chat, mod chạy mã, NFT/blockchain.

## 4. Thế giới
| Region | Tên | Chương | Cơ chế vùng | Map | Trạng thái |
|---|---|---|---|---|---|
| `dawnvillage` | Làng Bình Minh | 1 | Hub, tutorial, phục hồi làng | `map.dawnvillage_01..05` | **Chơi được** (chương 1) |
| `mistgarden` | Vườn Sương | 2 | Sương: quái né cao | `map.mistgarden_01..06` (có trong content) | Chưa có truyện/trận (WP-C1) |
| `ashwaste` | Hoang Mạc Tro | 3 | Bão cát gây Bỏng; boss Bọ Cạp Tro | `map.ashwaste_01..06` | Có vài encounter (chương 1 đi tới đây), chưa có chương 3 (WP-C2) |
| `crystalwood` | Rừng Thạch Anh | 4 | Tinh thể phản sát thương | Chưa có | WP-C3 |
| `echomines` | Hầm Mỏ Vọng | 5 | Câu đố trình tự; quái giáp dày | Chưa có | WP-E6 |
| `duskcitadel` | Thành Hoàng Hôn | 6 | Dark Lord 3 pha; 3 kết thúc | Chưa có | WP-E6 |

**Cốt truyện khung:** người chơi là người giữ đèn cuối cùng của Làng Bình Minh. Dark Lord Vesper đập vỡ "Cổ Vật Bình Minh" thành 6 mảnh để giữ thế giới ở mãi hoàng hôn. Mỗi vùng giữ một mảnh và một anh hùng bị trói bởi lời nguyền. Ở chương 6, hai biến `karma_mercy` và `karma_order` quyết định 3 kết thúc: **Bình Minh** (tha Vesper), **Vĩnh Dạ** (người chơi kế vị), **Tro Tàn** (phá huỷ cổ vật). Phá đảo xong mở NG+.

### 4.1 Khuôn một chương (dùng cho WP-C1..C3, E6)
| Thành phần | Số lượng | Ghi chú |
|---|---:|---|
| Quest chính | 5 | `quest.main.ch<N>_01..05`; quest cuối mở vùng kế |
| Quest phụ | 2–3 | Dạy cơ chế vùng, thưởng trang bị hợp cơ chế |
| NPC có hội thoại | 3–5 | Ít nhất 1 NPC có lựa chọn ảnh hưởng `karma_*` từ chương 3 |
| Anh hùng chiêu mộ qua truyện | 1 | Gắn với mảnh cổ vật của vùng |
| Encounter | 8–10 | Tỉ lệ vai trò: 1 tutorial cơ chế, 5–6 normal, 1–2 gate, 1 boss |
| Boss | 1 | ≥ 2 pha, pha 2 dùng cơ chế vùng |
| Kịch bản test agent | 1 | `scenarios/chapter<N>.mjs` chơi hết chương từ save fixture cuối chương trước |

## 5. Nhân vật và chiến đấu
**6 lớp:** Warrior, Assassin, Mage, Ranger, Support, Tank.
**Vòng khắc chế (thiết kế đích):** `Warrior ▶ Assassin ▶ Mage ▶ Tank ▶ Ranger ▶ Warrior`. Bên khắc gây ×1.25, bên bị khắc gây ×0.85. Support không khắc ai và không bị khắc, đổi lại có hồi máu và buff.

> **Hiện trạng content lệch thiết kế.** Trường `counters` trong `content/hero_classes/hero_classes.json` vẫn giữ nguyên dữ liệu legacy sau khi migrate:
>
> | Lớp | Khắc |
> |---|---|
> | warrior | assassin, mage |
> | assassin | ranger, support |
> | mage | ranger, assassin |
> | ranger | warrior, tank |
> | support, tank | không khắc lớp nào |
>
> Chuẩn hoá về vòng đích là WP-C0 (xem [05 §9](05-work-packages.md)). Làm WP-C0 trước WP-A3, vì đổi khắc chế làm thay đổi mọi tỉ lệ thắng.

**Roster:** hiện có 6 anh hùng, mỗi lớp 1 người: `hero.aldric` (warrior), `hero.nyx`, `hero.selene`, `hero.fenn`, `hero.mirae`, `hero.borin`. Launch cần 18, mỗi lớp 3 (WP-C4).

**Luật trận (người chơi nhìn thấy):**
- Thứ tự lượt theo tốc độ: `actionValue = 10000 / SPD`, tính lại khi SPD đổi.
- Mỗi anh hùng có 3 kỹ năng: 2 kỹ năng thường có hồi chiêu (mặc định 2 lượt của chính đơn vị) và 1 tuyệt kỹ tiêu 100 năng lượng.
- Năng lượng: bắt đầu 25; +20 mỗi hành động; +10 khi bị đánh.
- Trúng: `clamp(950 + (ACC − EVA)/2, 600, 1000)` ‰. Chí mạng nhân `CRIT_DMG` (mặc định 1500‰).
- 18 trạng thái: stun, silence, taunt, shield, burn, poison, bleed, regen, tăng/giảm atk, def, spd, crit_up, evasion_up, reflect, immune.
- Có chế độ auto (`AutoPolicy`), tạm dừng, nhật ký trận, phát lại (replay).

**Chỉ số (một công thức duy nhất):**
`stat = base × (1000 + 80×(level−1)) / 1000 × STAR[star] / 1000 + equipFlat`, rồi `× (1000 + Σbonus‰) / 1000`, với `STAR = [1000, 1150, 1320, 1520, 1750, 2010]`.

## 6. Vòng lặp chơi
| Vòng | Thời lượng | Nội dung |
|---|---|---|
| Ngắn | 5 phút | Khám phá → trận → thưởng → nâng anh hùng (cấp, sao, trang bị, thức ăn) |
| Trung | 1 ngày | Nhiệm vụ ngày, điểm danh, thám hiểm, hầm ngục nguyên liệu, chiêu mộ |
| Dài | tuần/mùa | Chương truyện, Tháp, Vực Thẳm, Battle Pass, chiến tranh bang, giải đấu, level UGC |

## 7. Kinh tế
| ID | Tên | Nguồn | Chi |
|---|---|---|---|
| `currency.gold` | Vàng | Trận, bán đồ, thưởng treo máy | Cửa hàng thường, cường hoá |
| `currency.gem` | Ngọc | Nhiệm vụ, thành tựu, IAP | Chiêu mộ, cửa hàng ngọc |
| `currency.energy` | Năng lượng | Hồi theo thời gian | Vào trận ngoài cốt truyện |
| `currency.guild_token` | Huy hiệu bang | Hoạt động bang | Cửa hàng bang |
| `currency.event_token` | Token sự kiện | Sự kiện | Cửa hàng sự kiện |

**Luật kinh tế:**
- Mọi thay đổi tiền tệ đi qua `Wallet` + `LedgerEntry`.
- Invariant: tổng ledger bằng số dư ví.
- Mỗi nguồn và mỗi chỗ tiêu phải có ở WP thiết kế kinh tế. Không thêm nguồn tiền mới mà thiếu chỗ tiêu tương ứng.
- Mô phỏng kinh tế nằm trong `console.liveops.economy_simulator` (WP-C13).

## 8. Người dùng và bề mặt sản phẩm
| Vai trò | Họ cần | Bề mặt | Số màn launch |
|---|---|---|---:|
| Gamer | Chơi, tiến triển, cạnh tranh, sáng tạo | Game client, Site | 363 |
| User | Tài khoản, bảo mật, thanh toán, quyền riêng tư | Game client, Account Portal | 106 |
| Pilot | Build sớm, nhiệm vụ test, góp ý, săn lỗi | Pilot tools trong client, Pilot Portal | 130 |
| Creator | Nội dung, map, hội thoại, cân bằng, dịch, art; UGC | Creator Studio | 312 |
| QA | Kế hoạch test, lỗi, test agent | QA Hub, Debug menu | 59 |
| Dev | Build, CI, crash, hiệu năng, tất định | Dev Tools, Debug menu | 82 |
| Admin | Người chơi, kiểm duyệt, LiveOps, tài chính, anti-cheat | Web Console | 245 |

Tổng catalog launch: **1.029 màn** (một màn phục vụ nhiều vai trò). Danh mục đầy đủ: [docs/screens/SCREEN_CATALOG.md](../screens/SCREEN_CATALOG.md).

## 9. Sau launch: 4 mùa mở rộng (tóm tắt)
Mỗi tính năng mùa nằm sau `FeatureFlag`, đi kèm content pack của mùa và gói asset tải về. Nó chỉ dùng kiến trúc đã có, không thêm công nghệ lõi.

| Mùa | Chủ đề | Điểm nhấn |
|---|---|---|
| S1 Lễ Hội Đèn Lồng | Lễ hội âm lịch | 12 lớp nâng cao, cây thiên phú, nhuộm đồ (palette shader), câu đố chiến thuật có solver, mùa và thời tiết, Companion PWA |
| S2 Biên Niên Sử | Câu chuyện của người chơi | Tua ngược lượt, replay rẽ nhánh, highlight tự động, Bard AI viết biên niên (có rào chắn và trần chi phí), bỏ phiếu cốt truyện |
| S3 Vết Nứt Thời Gian | Sinh thủ tục | Hầm ngục sinh từ seed (tất định), giấc mơ anh hùng, Thẻ Linh Hồn, speedrun xác minh bằng replay, Workshop mod dữ liệu |
| S4 Bên Kia Bình Minh | Hồi II | 6 vùng mới, hai thế giới trên cùng map, đoàn buôn và chợ, nghề, raid, thú cưỡi, sư phụ |

Chi tiết từng hệ thống và thành phần kiến trúc tương ứng: [MASTER_PLAN §18.2](../MASTER_PLAN.md).
