# ADR 0003: Battle engine tất định, số nguyên, command → event

- Trạng thái: Chấp nhận
- Ngày: 2026-09-25
- Liên quan: [MASTER_PLAN §0 D3](../MASTER_PLAN.md)

## Bối cảnh
Code cũ mô phỏng hết trận rồi chiếu lại; người chơi không điều khiển; không bao giờ có chí mạng; thanh máu lệch với HP thật.

## Quyết định
BattleEngine nhận BattleCommand, trả BattleEvent; RNG PCG32 theo seed; mọi phép tính dùng Int/Long theo ‰; state bất biến. Kỹ năng dùng hồi chiêu, năng lượng chỉ cho tuyệt kỹ.

## Hệ quả
Replay = seed + commands; tua ngược miễn phí; server xác thực trận bằng cùng engine; golden test giống nhau trên mọi nền tảng. Không dùng float trong domain.
