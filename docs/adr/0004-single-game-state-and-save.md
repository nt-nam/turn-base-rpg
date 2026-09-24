# ADR 0004: Một GameState và một file save mỗi slot

- Trạng thái: Chấp nhận
- Ngày: 2026-09-25
- Liên quan: [MASTER_PLAN §0 D4](../MASTER_PLAN.md)

## Bối cảnh
GameSessionManager có ~50 field public; 8 file lưu rời; nhiều thay đổi (túi đồ, mua hàng, thưởng) không bao giờ được lưu.

## Quyết định
GameState bất biến + GameStore; SaveGame v2 một file có schemaVersion, ghi nguyên tử + checksum; LegacyV1Importer đọc save cũ.

## Hệ quả
Hết lỗi mất dữ liệu; save → load → save ổn định từng byte. Cần viết migrator và giữ fixture save cũ.
