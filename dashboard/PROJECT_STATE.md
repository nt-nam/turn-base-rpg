# LVpxW - Game Content Studio — TRANG THAI DU AN

> File nay ghi lai trang thai/tien do de session sau mo len la nam duoc ngay.
> Cap nhat lan cuoi: 2026-09-23.

## 1. Muc tieu
Cong cu local giup **sua thong so game + build level nhanh**, luu **thang vao `assets/data`**
cua project game RPG LVpxW (LibGDX/Java), khong can mo IDE sua tay JSON.

Vi tri: thu muc `dashboard/` trong project (`D:\code\libgdx\LVpxW\dashboard`).

## 2. Trang thai hien tai: HOAN TAT
Tat ca 8 hang muc trong plan da xong va da verify. Cong cu chay duoc.

### Da verify (bang chung)
- `npm run build` OK — 40 modules, khong loi.
- Round-trip test bang Node (giong app that): **11/11 PASS** cho moi loai file —
  doc -> sua -> save -> doc lai dung -> khoi phuc == nguyen van ban goc (giu UTF-8 tieng Viet + cau truc).
- Backend API test: health/list/read/write/backup/delete OK, path traversal bi chan.
- `git status assets/data` SACH — khong lam thay doi file du lieu goc sau khi test.

## 3. Cach chay
```bash
cd dashboard
npm install        # neu chua cai (da co node_modules thi bo qua)
npm run dev        # chay CA backend (API :5179) + frontend (:5180)
```
Mo http://localhost:5180. Chay rieng: `npm run server` / `npm run dev:web`.
Yeu cau: Node (da test tren v24), npm 11.

## 4. Kien truc
- **Backend** `server/index.js` — Express, port 5179, bind 127.0.0.1.
  - API: `GET /api/health`, `GET /api/list`, `GET/PUT/POST/DELETE /api/file?path=...`
  - `resolveSafe()` chan path traversal, chi cho `.json` trong `assets/data`.
  - `sanitizeJson()` doc JSON "gan-chuan" (comment `//`, trailing comma).
  - `backupFile()` — MOI lan Save/Xoa deu backup ban cu vao `dashboard/.backups/<timestamp>/...`.
  - Ghi file dang JSON indent 2 + newline cuoi.
- **Frontend** React + Vite (port 5180), proxy `/api` -> 5179.
  - `src/dataLoader.js` — client goi API: loadFile/saveFile/createFile/deleteFile/listFiles/loadAll.
  - `src/common.jsx` — `useEditableFile(relPath)` (dirty-state + save/revert), `ToastProvider/useToast`,
    `EditorBar`, `EditableField`, `EditableNumber`, `FieldRow`, `TagInput`, `JsonPreview`, `SearchBar`.
  - `src/App.jsx` — 7 tab, boc `ToastProvider`.
  - `src/views/*` — cac editor.

## 5. Cac tab / editor
| Tab | File sua | Ghi chu |
|-----|----------|---------|
| Level Editor | `enemy/*.json` | Luoi 3x3, dat quai + level/star, reward, tao/nhan ban/xoa man |
| Nhan vat | `base/character_base.json`, `enemies_data.json` | stats, skill, counter, weak |
| Ky nang | `skill_data.json`, `base/skill_base.json` | effect theo group + skill theo class |
| Item | `base/items_base.json` | bang inline |
| Trang bi | `base/equip_base.json` | stats, category, gia |
| Nhiem vu | `base/mission_base.json`, `base/achievement.json` | title/target/reward |
| Config | `config/battle_config.json`, `config/itemConfig.json` | thong so he thong |

## 6. Toa do Level Editor (quan trong)
- Grid string dang `"cot,hang"` = `"i,j"`, ca hai 0..2 (luoi 3x3).
- Trong game (EntityFactory.createEnemyTeam): `posX = startX + i*tile`, `posY = startY + j*tile`.
- => `i` = cot (ngang), `j` = hang (doc), **hang 0 nam DUOI** (y huong len).
- Editor render 3 cot, hang tren cung la j=2 cho khop layout game.
- Cell object: `{ grid:"i,j", characterId:"enemy", nameRegion, star, level }`.
- Reward object: `{ type, id, quantity }`.

## 7. An toan / luu y
- Dev tool local, bind 127.0.0.1, KHONG deploy len moi truong chung.
- Moi Save/Xoa deu backup tu dong (`dashboard/.backups/`, da gitignore).
- Backend ghi JSON indent 2 => cac file goc dang compact (mang 1 dong) se thanh multiline khi luu.
  Gia tri & tinh hop le JSON KHONG doi, game van doc dung. Neu muon giu style compact -> can sua backend.
- Nen dung kem git de review truoc khi commit.
- CANH BAO tu kinh nghiem: KHONG dung PowerShell ConvertTo-Json/ConvertFrom-Json de sua file du lieu
  (lam hong encoding tieng Viet + them wrapper "value"). Neu can test bang script, dung Node (fetch + JSON.stringify).

## 8. Cau truc file da tao trong dashboard/
```
dashboard/
  package.json          (react, react-dom, express, vite, concurrently)
  vite.config.js        (proxy /api -> 5179, port 5180)
  index.html
  README.md
  PROJECT_STATE.md      (file nay)
  .gitignore            (node_modules, dist, .vite, .backups)
  server/
    index.js            (backend API)
  src/
    main.jsx
    App.jsx
    styles.css
    dataLoader.js
    common.jsx
    views/
      LevelEditor.jsx
      CharactersView.jsx
      SkillsView.jsx
      ItemsView.jsx
      EquipView.jsx
      MissionsView.jsx
      ConfigView.jsx
```

## 9. Y tuong mo rong (chua lam — cho session sau neu muon)
- Hien sprite/anh tu `assets/atlas` trong card nhan vat/trang bi (hien chi hien text).
- Editor lineup nguoi choi (`select/*/lineup.json`) dung lai grid giong Level Editor.
- Tab so sanh/can bang stats (bang xep hang ATK/DEF/HP theo level).
- Nut "Backup/Restore" trong UI (hien backup nam o `.backups`, chua co UI restore).
- Tuy chon giu format compact khi ghi (hien mac dinh indent 2).
- Editor daily_rewards, itemConfig nang cao, mission cua tung save-slot (`select/a/*`).

## 10. File du lieu game co van de da biet (khong phai loi cong cu)
- `enemies_data.json`, `skill_data.json`, `maininfo.json`: co comment `//xoa cai nay` (dau/cuoi file).
- `equip_base.json`: co trailing comma (vd sau `"hp": 51,`).
- => Backend sanitizer tu xu ly khi DOC. Khi LUU se thanh JSON chuan (bo comment/trailing comma).
- `select/base/items.json`: file rong 0 byte (khong dung).
```
