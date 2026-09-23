# LVpxW - Game Content Studio

Cong cu local (React + Vite + Express) giup **sua thong so game va build level nhanh**,
luu **thang vao `assets/data`** cua project (khong can mo IDE sua tay JSON).

## Chay
```bash
cd dashboard
npm install
npm run dev     # chay CA backend (API :5179) + frontend (:5180) cung luc
```
Mo http://localhost:5180. (Chay rieng: `npm run server` va `npm run dev:web`.)

## Cac tab
- **Level Editor**: chon/tao/nhan ban/xoa man trong `data/enemy/*.json`. Luoi 3x3, click o de
  dat quai (chon tu character_base + enemies_data), chinh level/star, them/xoa reward. Save ghi thang file.
  - Toa do o dang `"cot,hang"`, hang 0 nam duoi (khop layout trong game).
- **Nhan vat**: sua `base/character_base.json` va `enemies_data.json` (stats, skill, counter, weak).
- **Ky nang**: sua `skill_data.json` (effect theo group) va `base/skill_base.json` (skill theo class).
- **Item**: bang sua inline `base/items_base.json`.
- **Trang bi**: sua `base/equip_base.json` (stats, category, gia).
- **Nhiem vu**: sua `base/mission_base.json` + `base/achievement.json`.
- **Config**: sua `config/battle_config.json` + `config/itemConfig.json`.

Moi tab co nut **Save / Revert**, canh bao khi co thay doi chua luu, va nut **Xem JSON**.

## An toan
- Backend chi ghi trong `assets/data` (validate path, chan traversal), chi file `.json`,
  bind `127.0.0.1` (chi may local). Day la dev tool, khong deploy len moi truong chung.
- **Moi lan Save/Xoa deu tu dong backup** ban cu vao `dashboard/.backups/<timestamp>/...`.
- File duoc ghi dang JSON indent 2 (dep, de git diff). Gia tri & tinh hop le JSON giu nguyen.
- Nen dung kem git de review thay doi truoc khi commit.

## Kien truc
- `server/index.js`: Express API — `GET /api/list`, `GET/PUT/POST/DELETE /api/file?path=...`,
  co sanitizer doc JSON "gan-chuan" (comment `//`, trailing comma).
- `src/dataLoader.js`: client goi API.
- `src/common.jsx`: `useEditableFile` (dirty-state + save/revert), toast, editable helpers.
- `src/views/*`: cac editor.
```
