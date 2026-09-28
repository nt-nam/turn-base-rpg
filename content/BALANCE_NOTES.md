# Ghi chú cân bằng

## Sau migrate (2026-09-25) (content-compiler, 200 seed mỗi trận)

| Trận | Cấp | 6 anh hùng | Starter một mình | Nhận xét cho creator (P5) |
|---|---:|---:|---:|---|
| encounter.dawnvillage_01.e0 | 1 | 100% | 99% | Trận đầu hợp lý cho người mới |
| encounter.dawnvillage_01.e1 | 1 | 100% | 4% | Trận gương warrior; quái đứng hàng sau (−20% sát thương nhận) nên starter gần như thua |
| encounter.ashwaste_01.e0 | 5 | 100% | 0% | Sát thủ 3 sao cấp 5 — đúng vai trò "trận chặn" |
| encounter.ashwaste_01.e4 | 1 | 100% | 0% (58 hòa) | Tank + khiên kéo trận tới giới hạn 30 vòng |

Toàn bộ trận thắng ~100% với đội đủ 6 lớp: độ khó hiện chỉ đến từ số lượng anh hùng đang có, chưa đến từ cơ chế. Cần thiết kế lại đường cong độ khó khi có roster 18 anh hùng.

## Đo bằng sim-cli (2026-09-26, đang làm dở WP-A3)

Trận hướng dẫn `encounter.dawnvillage_01.e0` (1 thảo khấu sát thủ cấp 1), mỗi anh hùng khởi đầu đứng một mình ở ô 1-0, cấp 1, 200 seed:

| Starter | aldric | nyx | selene | fenn | mirae | borin |
|---|---:|---:|---:|---:|---:|---:|
| Tỉ lệ thắng | 99.0% | 44.5% | 59.0% | 16.5% | 0.0% | 0.0% |

Cả 6 anh hùng đều chọn được làm starter (`starter: true`), nhưng chỉ aldric thắng chắc trận đầu. 5/6 lựa chọn gần như thua trận cốt truyện đầu tiên.

Sức chứa đội hình là 3 cho tới cấp tài khoản 6 (`LineupCapacity`), nên đội "đủ 6 lớp" trong bảng trên không phải đội thật của người chơi. Ở cấp 3, với aldric (cầm `equip.sword_001`) cộng 2 anh hùng chiêu mộ, cả 10 bộ ba đều thắng 100% mọi trận `ashwaste_01` (kể cả trận chặn e0). Riêng `ashwaste_02` thì kết quả phụ thuộc mạnh vào đội hình: e1 cấp 3 thắng từ 30% (nyx+fenn) tới 100% (borin+selene).
