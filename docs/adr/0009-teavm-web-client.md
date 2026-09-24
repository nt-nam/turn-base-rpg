# ADR 0009: Game bản web build bằng TeaVM

- Trạng thái: Chấp nhận
- Ngày: 2026-09-25
- Liên quan: [MASTER_PLAN §0 D9](../MASTER_PLAN.md)

## Bối cảnh
Module html (GWT) không build được: Gson, freetype, ghi file local.

## Quyết định
Dùng gdx-teavm; save vào IndexedDB qua adapter; không IAP trên web.

## Hệ quả
Web chạy được bytecode Kotlin. Chưa kiểm chứng với Fleks/KTX: spike 3 ngày; nếu thất bại web chỉ còn demo.
