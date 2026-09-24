# ADR 0010: Screen Catalog là registry có kiểm chứng

- Trạng thái: Chấp nhận
- Ngày: 2026-09-25
- Liên quan: [MASTER_PLAN §0 D10](../MASTER_PLAN.md)

## Bối cảnh
Yêu cầu >1000 màn (sau đó ×2) phải đếm và kiểm tra được.

## Quyết định
tools/screen-catalog sinh docs + GameScreenId.kt + screenIds.ts; --check trong CI; test agent đo độ phủ theo catalog.

## Hệ quả
Code và catalog không thể lệch nhau. Mọi màn mới phải khai báo trong catalog trước.
