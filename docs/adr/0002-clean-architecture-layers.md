# ADR 0002: Kiến trúc 4 tầng domain / application / infrastructure / client

- Trạng thái: Chấp nhận
- Ngày: 2026-09-25
- Liên quan: [MASTER_PLAN §0 D2](../MASTER_PLAN.md)

## Bối cảnh
Luật game đang nằm trong widget UI (BagPP, ShopPP, HerosPP...), lưu JSON trực tiếp từ popup, không test được.

## Quyết định
domain: Kotlin thuần, không import libGDX/Ktor/I/O. application: use case + port. infrastructure: adapter. client: libGDX. Luật phụ thuộc kiểm bằng test kiến trúc (Konsist).

## Hệ quả
Domain test được trên JVM và chạy được trên server. Thêm tầng gián tiếp nên phải có quy ước đặt tên rõ ràng (§14).
