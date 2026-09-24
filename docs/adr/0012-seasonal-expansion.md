# ADR 0012: Mở rộng theo mùa sau launch

- Trạng thái: Chấp nhận
- Ngày: 2026-09-25
- Liên quan: [MASTER_PLAN §0 D12](../MASTER_PLAN.md)

## Bối cảnh
Quy mô ×2 không thể nằm trong bản launch.

## Quyết định
4 mùa × 12 tuần; mỗi tính năng mùa sau FeatureFlag + content pack mùa + gói asset tải từ xa.

## Hệ quả
Launch gọn; tắt được từng tính năng khi có sự cố. Cần quản lý tương thích content pack theo minClientVersion.
