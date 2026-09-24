# ADR 0005: Nội dung data-driven có JSON Schema và content compiler

- Trạng thái: Chấp nhận
- Ngày: 2026-09-25
- Liên quan: [MASTER_PLAN §0 D5](../MASTER_PLAN.md)

## Bối cảnh
JSON hiện có comment, dấu phẩy thừa, khóa treo; nameRegion vừa là ID vừa là tên region atlas.

## Quyết định
Thư mục content/ là nguồn sự thật; ID nội dung dạng <loại>.<slug> tách khỏi khóa asset; content-compiler validate schema + tham chiếu + luật thiết kế, xuất content pack có hash.

## Hệ quả
Lỗi dữ liệu bị chặn ở CI; Studio và mod dùng chung schema. Phải migrate toàn bộ dữ liệu cũ.
