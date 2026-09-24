# Giấy phép tài nguyên

> Bắt buộc hoàn tất trước khi phát hành thương mại (MASTER_PLAN §9.5). Mọi file trong bản build đến từ `tools/asset-pipeline`; cột "Trong build" lấy từ `asset-manifest.json`.

| Tài nguyên | Đường dẫn nguồn | Trong build | Giấy phép | Trạng thái |
|---|---|---|---|---|
| Be Vietnam Pro (Medium, Bold) | `art/fonts/*.ttf` | Có (`fonts/pxworld_*.fnt`) | SIL OFL 1.1 — `art/fonts/OFL.txt` | Đã xác minh |
| Sprite nhân vật Knight (10 bộ) | `assets/atlas/characters/` | 6 bộ | Không rõ | **Cần xác minh nguồn** |
| Icon vật phẩm (218 region) | `assets/atlas/inventory/item.*` | Có | Không rõ | **Cần xác minh nguồn** |
| VFX kỹ năng 5 màu | `assets/atlas/skill/skill.*` | Có | Không rõ | **Cần xác minh nguồn** |
| UI popup/icon/wood | `assets/atlas/ui/` | icon + popup | Không rõ | **Cần xác minh nguồn** |
| Bản đồ làng (fullmap) | `assets/tilemap/map/fullmap.png` | Chỉ các ô đã dùng | Không rõ | **Cần xác minh nguồn** |
| Bản đồ vườn / hoang mạc | `assets/tilemap/map/{garden,wasteland}*.png` | Chỉ các ô đã dùng | Không rõ | **Cần xác minh nguồn** |
| Nền trận 4 mùa | `assets/texture/battle/*.png` | JPG 1920×1080 | Không rõ | **Cần xác minh nguồn** |
| NPC | `assets/texture/npc/npc1..3.png` | Có | Không rõ | **Cần xác minh nguồn** |
| Nhạc `world1.mp3`, `battle.mp3` | `assets/music/` | Có | Không rõ | **Cần xác minh nguồn** |
| SFX `bubble_*` | `assets/sound/` | 3 file | Không rõ (tên gợi ý từ một template bubble-shooter) | **Cần thay thế** |
| Arial Unicode MS | `assets/font/arial_uni_30.*` | **Không** (chỉ còn trong code legacy) | Monotype, thương mại | Đã loại khỏi build mới |

Khi xác minh xong một dòng, ghi nguồn (URL/tác giả), giấy phép và ngày kiểm tra. Gói nào không rõ nguồn sẽ được thay qua quy trình `studio.production.art_requests`.
