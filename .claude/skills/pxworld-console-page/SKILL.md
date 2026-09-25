---
name: pxworld-console-page
description: Add or change a PXWORLD web Console page (React 19 + TypeScript + Vite) with a catalog WebScreenId, role-gated navigation, existing CSS tokens, and a console-agent (CDP) step proving it works. Use for any change under web/apps/console.
---

# Thêm trang Console

Nguồn chi tiết: `docs/handbook/10-recipes-server-and-console.md`.

## Checklist
1. **ScreenId**
   - Chọn ID có sẵn trong `docs/screens/screens.json` (surface `console`, `studio`, `qa`, `devtools`…).
   - Prop `screen` của `Page` có kiểu `WebScreenId`, nên ID ngoài catalog sẽ không biên dịch được.
   - Nếu cần ID mới, sửa `tools/screen-catalog/catalog.mjs` rồi sinh lại.
2. **API:** gọi qua `web/apps/console/src/api.ts`. Server chưa có endpoint thì làm theo skill `pxworld-server-endpoint` trước.
3. **Trang**
   - Đặt trong `web/apps/console/src/pages/`, theo mẫu một trang cùng nhóm.
   - Có trạng thái đang tải, lỗi và rỗng.
   - Không dùng `any`.
   - Chữ giao diện bằng tiếng Việt, giọng văn như các trang hiện có.
4. **Điều hướng:** thêm route trong `App.tsx`. Hiện link trong nav theo vai trò của staff. Nếu trang có bộ lọc, lưu bộ lọc vào hash URL.
5. **Style**
   - Dùng lại class và biến CSS trong `styles.css`; màu mới phải khai báo cho cả chế độ sáng và tối.
   - Kiểm tra ở chiều rộng 375 px.
6. **Console agent:** thêm bước vào `tools/test-agent/console/console-agent.mjs`. Bước này seed dữ liệu qua API nếu cần, mở trang, thao tác, `expectText` kết quả và `shot` ảnh.
7. **Kiểm chứng**
   ```bash
   (cd web && npm ci && npm run typecheck && npm run console:build)   # npm ci chỉ cần lần đầu
   node tools/screen-catalog/catalog.mjs --check
   tools/test-agent/console/run-console.sh --out="$PWD/agent-reports/console"
   ```
   Kết quả phải là `PASSED console` và `pageErrors` rỗng trong `report.json`.
