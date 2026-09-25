# BUG · <Tóm tắt: ở đâu + làm gì + sai thế nào>

| Trường | Giá trị |
|---|---|
| Mức độ | S1 chặn / S2 nghiêm trọng / S3 vừa / S4 nhẹ (định nghĩa ở 06-testing.md) |
| Bề mặt | game-desktop / game-android / game-web / console / server |
| Build | commit `git rev-parse --short HEAD`, flavor (DEV/QA/PILOT/RELEASE) |
| Môi trường | local / qa / pilot; OS, thiết bị, độ phân giải |
| ScreenId | ví dụ `game.battle.battle_main` hoặc `console.players.player_overview` |
| Tái hiện | luôn / thỉnh thoảng (x/10) / một lần |
| Test case liên quan | `TC-…` hoặc kịch bản agent `…` |

**Các bước tái hiện**
1.
2.

**Kết quả mong đợi**

**Kết quả thực tế**

**Đính kèm** (bắt buộc với S1, S2)
- [ ] Ảnh chụp hoặc video
- [ ] `report.json` của test agent (nếu tìm thấy bằng agent)
- [ ] `game.log` / `logcat.txt` / `server.log`
- [ ] File save (`~/.pxworld/<env>/saves/…`) hoặc seed và lệnh của trận (replay)
- [ ] Request/response (ẩn token)

**Ghi chú phân tích** (người sửa điền): nguyên nhân gốc, test đã thêm để lỗi không quay lại.
