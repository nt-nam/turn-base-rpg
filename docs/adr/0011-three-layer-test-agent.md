# ADR 0011: Test agent 3 lớp

- Trạng thái: Chấp nhận
- Ngày: 2026-09-25
- Liên quan: [MASTER_PLAN §0 D11](../MASTER_PLAN.md)

## Bối cảnh
Cần một agent thao tác được trên sản phẩm thật và đo độ phủ.

## Quyết định
(1) sim-cli headless cho cân bằng; (2) automation protocol WebSocket JSON-RPC trong flavor dev/qa/pilot; (3) explorer agent Claude Agent SDK qua MCP + Playwright cho web.

## Hệ quả
Có độ phủ màn hình đo được và lỗi tái hiện được bằng seed. Tốn chi phí model cho lượt nightly (có ngân sách).
