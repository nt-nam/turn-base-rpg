# ADR 0006: Tách nguồn art (Git LFS) khỏi assets sinh tự động

- Trạng thái: Chấp nhận
- Ngày: 2026-09-25
- Liên quan: [MASTER_PLAN §0 D6](../MASTER_PLAN.md)

## Bối cảnh
38 MB nền battle, tileset 6052×5837 vượt giới hạn texture mobile/web, file trùng byte, không có file nguồn thiết kế.

## Quyết định
art/ chứa nguồn (Aseprite, Tiled project, wav, ttf) qua Git LFS; assets/ do tools/asset-pipeline sinh; CI chặn texture > 2048 và file trùng.

## Hệ quả
Build tái lập được, APK nhỏ hơn. Cần cài Git LFS và Aseprite CLI trên máy build.
