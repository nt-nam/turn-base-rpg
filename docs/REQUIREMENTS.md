# Yêu cầu của chủ dự án

> Tổng hợp từ các buổi làm việc giữa chủ dự án và Claude, từ 2026-09-24 đến 2026-09-29. §1–§7 là bản đã sắp xếp lại; **§8 là nguyên văn** chủ dự án đã gõ, trích từ lịch sử phiên làm việc.
> Tài liệu này là **nguồn gốc** của mọi plan. Khi plan và yêu cầu mâu thuẫn, yêu cầu thắng. Khi yêu cầu mơ hồ, xem cách diễn giải đã chốt ở §4, hoặc hỏi chủ dự án.

## 1. Mục tiêu
**Viết lại toàn bộ dự án** (yêu cầu ưu tiên: "REWRITE PROJECT"). Lập plan hoàn chỉnh trước, được duyệt rồi mới thực hiện.

## 2. Quyền đã cấp cho người làm
| # | Được phép |
|---|---|
| Q1 | Dọn sạch toàn bộ code cũ và chuyển sang kiến trúc tốt nhất |
| Q2 | Tái cấu trúc hình ảnh và file thiết kế |
| Q3 | Định nghĩa lại, thêm hoặc xoá đối tượng |
| Q4 | Đổi tên cho rõ nghĩa; tên tự giải thích nên không cần comment |
| Q5 | Mở rộng nền tảng và môi trường |
| Q6 | Sáng tạo nội dung mới |
| Q7 | Xây test agent tự chạy |
| Q8 | Toàn quyền với mọi giai đoạn trên nhánh `rewrite`, không cần xin duyệt từng bước |

## 3. Yêu cầu bắt buộc
| # | Yêu cầu | Mức |
|---|---|---|
| R1 | Phục vụ đủ **7 vai trò**: admin, dev, creator, QA, user, gamer, pilot | Sàn tối thiểu |
| R2 | **Hơn 1.000 màn hình**, đếm được | Sàn tối thiểu |
| R3 | Sáng tạo hơn nữa, nhưng trong khả năng của kiến trúc đã chọn | Mở rộng |
| R4 | **Nhân đôi mọi chỉ số quy mô** so với bản plan đầu | Mở rộng |
| R5 | Có test agent tự chạy, kiểm chứng sản phẩm thật | Bắt buộc |

## 4. Cách diễn giải đã chốt
| Từ trong yêu cầu | Hiểu là |
|---|---|
| "polit" | **pilot**: người chơi thử nghiệm trong chương trình beta/pilot |
| "creater level" | Cả **nhân viên thiết kế nội dung** (Creator Studio) lẫn **người chơi tự tạo level** (UGC) |
| "hơn 1000 màn" | Mỗi màn là một route hoặc trạng thái điều hướng có `ScreenId` riêng. Biến thể responsive, trạng thái loading/rỗng/lỗi và tham số không được tính (định nghĩa ở MASTER_PLAN §12) |

## 5. Quy tắc làm việc do chủ dự án đặt
| # | Quy tắc | Ghi ở |
|---|---|---|
| W1 | Làm trên nhánh `rewrite`, mỗi việc một nhánh `rewrite-<wp>` | [AGENTS.md §4](../AGENTS.md) |
| W2 | **Commit không gắn tag** | AGENTS.md §4 |
| W3 | Việc code được chia cho nhiều agent chạy song song; một người hoặc agent đóng vai PM | AGENTS.md §7 |
| W4 | Khi được bảo "tạm dừng": chốt phần đang làm, commit, lưu trạng thái để làm tiếp sau | AGENTS.md §8 |
| W5 | Plan phải đủ chi tiết để **người khác làm tiếp và test ngay được**: có luật, ý tưởng, kiến trúc bắt buộc, kỹ năng cần có và tài liệu | [handbook](handbook/README.md) |
| W6 | Phong cách làm việc phải nằm **trong repo**, cho cả người lẫn agent AI | AGENTS.md, CLAUDE.md, `.claude/` |
| W7 | Tài liệu chỉ cần **chỉ chỗ và plan**. Phần còn thiếu ghi thành task để người sau tự hoàn thành | [05 §8](handbook/05-work-packages.md) (WP-X8, D-01…D-07) |
| W8 | Báo cáo: kết luận trước, một đáp án tốt nhất, không chắc thì nói rõ, không lan man | AGENTS.md §6 |

## 6. Truy vết: yêu cầu → nơi đáp ứng → trạng thái
| Yêu cầu | Đáp ứng ở | Trạng thái 2026-09-29 |
|---|---|---|
| Mục tiêu (§1) | [MASTER_PLAN.md](MASTER_PLAN.md) → [05-work-packages.md](handbook/05-work-packages.md) | Plan xong. Đang làm: P0, P1 và P2 đạt; P3 và P4 làm một phần |
| Q1 kiến trúc mới | MASTER_PLAN §4, [02-architecture.md](handbook/02-architecture.md), ADR 0001–0015 | Có. Code legacy chờ xoá (WP-A6) |
| Q2 hình ảnh và thiết kế | MASTER_PLAN §9, `tools/asset-pipeline`, `art/` | Pipeline lõi có; còn Git LFS, giấy phép asset, design tokens (X4, X6, X3) |
| Q3, Q4 đối tượng và tên | MASTER_PLAN §6, [03-engineering-rules.md](handbook/03-engineering-rules.md) | Có trong code mới; 03 còn khung (WP-X8) |
| Q5 nền tảng và môi trường | MASTER_PLAN §8, [07-setup-and-environments.md](handbook/07-setup-and-environments.md) | Desktop và Android chạy; web đang spike (A5); iOS chờ quyết định D-04 |
| Q6 nội dung | [01-product-and-game-design.md](handbook/01-product-and-game-design.md), `content/` | Chương 1 chơi được; chương 2–6 ở đợt C và E |
| Q7, R5 test agent | `tools/test-agent/`, [06-testing.md](handbook/06-testing.md) | Chạy trên desktop, Android và Console; 06 còn khung (WP-X8) |
| R1 bảy vai trò | MASTER_PLAN §3, [SCREEN_CATALOG.md](screens/SCREEN_CATALOG.md) | Đã phân màn cho cả 7 vai trò trong catalog |
| R2 hơn 1.000 màn | [SCREEN_CATALOG.md](screens/SCREEN_CATALOG.md): **1.029** màn launch | Catalog đạt. Chạy thật: 133 màn game và khoảng 18 màn web; lộ trình lên 1.029 ở 05 đợt B–E |
| R3, R4 sáng tạo, ×2 | MASTER_PLAN §18: **2.094** màn sau 4 mùa, 14 bề mặt, mọi chỉ số ≥ ×2 | Plan xong; thực hiện sau launch (S1–S4) |
| W1–W8 | AGENTS.md, CLAUDE.md, `.claude/skills/`, `.claude/agents/` | Có |

## 7. Việc chủ dự án cần quyết
Các quyết định còn treo nằm ở [05 §8.2](handbook/05-work-packages.md) (D-01…D-07). Người làm chỉ đề xuất, không tự quyết.

## 8. Nguyên văn yêu cầu
Giữ nguyên chính tả gốc. Chỉ trích các tin nhắn về dự án; bỏ phần hỏi đáp riêng về đăng nhập Claude CLI (2026-09-25).

**2026-09-24 — yêu cầu gốc**
```text
Priority alert: REWRITE PROJECT
tôi cần bạn từ những resource của dự án mà cho tôi được bản tài liệu plan hoàn chỉnh của dự án 
- được phép clean toàn bộ code và chuyển về 1 kiến trúc hoàn hảo nhất mà bạn biết đến hiện tại 
- được phép tái cấu trúc toàn bộ tài nguyên hình ảnh và cả file thiết kế trong toàn bộ các nền tảng
- được phép thiết lập lại bộ những đối tượng có , tạo thêm mới những đối tượng cần hoặc liên quan, được phép xóa luôn các dối tượng xem là sai hoặc không cần thiết cho dự án
- có thể đổi tên code hoăc file sao cho tường minh nhất, code dẽ hiểu - không cần comment
- được phép mở rộng ra nhiều nền tảng, nhiều môi trường,
- được phép mở rộng dự án
- được phép sáng tạo nội dung
- được phép tự khởi chạy, tự tạo một agent test có thể thao tác trên sản phẩm tạo ra
* giới hạn sàn là: có thể phủ được hết toàn bộ đối tượng từ admin - dev - creater level - QA - user - gemer - polit + có hơn 1000 màn hình thao tác với mục đích khác nhau
```

**2026-09-24 — mở rộng ×2**
```text
Tôi yêu cầu thêm được sự sáng tạo cao hơn nữa và các sáng tạo đó vẫn nằm trong khả năng của kiến trúc dự án được lựa chọn - con số cụ thể là quy mô phải đạt được chỉ số gấp đôi hiện tại
```

**2026-09-24 — duyệt plan và giao quyền**
```text
Được làm đi
```
```text
tôi cho bạn làm toàn quyền với tất cả giai đoạn tiếp theo - cứ tạo nhánh mới là rewrite
-chấp nhận toàn bộ tiến trình
```
```text
commit không gắng tag
```

**2026-09-25 — tạm dừng lần đầu**
```text
tạm pause và lưu trạng thái tại đây
```

**2026-09-26 — cách làm việc và bàn giao**
```text
vì là việc viết code nên có thể cho bạn mở nhiều agent code để đảm bảo xong hết queue task đã đề ra trước đó nhanh - bạn đảm nhiệm PM thôi cũng được
```
```text
vì là thời gian có giới hạn nên tôi đề nghị nên làm plan thật chi tiết để người khác làm - người khác có thẻ dựa trên plan đó để teesst luôn - cần có chỉ dẫn rõ ràng luật, ý tưởng, và kiến trúc bắt buộc tuân theo, skill cần trogn dự án này và rất nhiều tài liệu cho nội dung này
```
```text
xong được phần nào thì nhớ ngừng đi - để lúc khác làm tiếp
```

**2026-09-29 — chuyển giao**
```text
tiến trình làm lại tiếp theo lại thì cần chuyển giao công nghệ và chuyển giao plan, phong cách làm việc cũng như toàn bộ thông tin cho người khác làm tiếp các tiến độ còn đang gian dỡ thì cần đưa cho họ cái gì
```
```text
1. commit, merge và push 
2. đưa phong cách làm việc vào trong agent/ trong repo
3. đặt ghi nhớ trong docs để người khác tự hoàn thành - chỉ cần nhắc là có cần phần đó trong các tasks là được
```
```text
không cần viết và chạy teesst mà
```
```text
viết tài liệu chỉ chỗ và plan phục vụ yêu cầu của tôi thôi
```
