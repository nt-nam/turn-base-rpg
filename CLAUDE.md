@AGENTS.md

# Riêng cho Claude Code

- Khi nhận việc thuộc repo này, gọi skill phù hợp trong `.claude/skills/` trước khi làm: nhận việc gọi `pxworld-work-package`, trước khi merge hoặc báo xong gọi `pxworld-verify`.
- Khi giao việc cho subagent, dùng định nghĩa trong `.claude/agents/` và tạo worktree bằng lệnh ở AGENTS.md §7. Không dùng `isolation: "worktree"`, vì chế độ đó dựng worktree từ `main`, không phải từ `rewrite`.
- Subagent chạy nền: không đọc file transcript của subagent (rất lớn); chờ thông báo hoàn thành.
- Người dùng có thể nói "tiếp tục" hoặc "tạm dừng". Tiếp tục thì làm theo AGENTS.md §1; tạm dừng thì làm theo AGENTS.md §8.
