# ADR 0001: Kotlin + libGDX + Fleks + KTX cho game client

- Trạng thái: Chấp nhận
- Ngày: 2026-09-25
- Liên quan: [MASTER_PLAN §0 D1](../MASTER_PLAN.md)

## Bối cảnh
Code Java/Ashley hiện tại có service locator tĩnh, popup chứa luật game, Ashley 1.7.4 không còn phát triển. Pipeline libGDX (atlas, Tiled, Android, desktop) vẫn chạy tốt.

## Quyết định
Viết lại bằng Kotlin, giữ libGDX 1.13, thay Ashley bằng Fleks, dùng KTX cho scene2d/assets/async. Module dùng chung phát bytecode JVM 1.8 để tương thích RoboVM.

## Hệ quả
Giữ nguyên toàn bộ asset pipeline; code ngắn và null-safe; dùng chung ngôn ngữ với backend. Đội cần làm quen Kotlin; TeaVM + Fleks + KTX phải spike trước (ADR 0009).
