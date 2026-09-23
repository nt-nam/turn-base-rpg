# KE HOACH REFACTOR: Chuan hoa kien truc Screen + ECS

> Muc tieu: Lam sach kien truc Screen+ECS hien co (Huong A). Moi Screen doc lap,
> ECS khong ro ri trang thai giua cac man, de them Screen moi. GIU NGUYEN hanh vi game.
> Tao ngay: 2026-09-23.

## 0. Nguyen tac xuyen suot
- **Khong doi hanh vi game** (behavior-preserving refactor). Chi doi cau truc.
- **Moi buoc phai verify**: `gradlew.bat test` (unit test) + `gradlew.bat build` (compile) PASS truoc khi sang buoc sau.
- Lam **tung buoc nho, commit-able**. Neu 1 buoc lam vo build/test -> sua ngay hoac revert buoc do, khong di tiep.
- Neu buoc nao rui ro cao (dong den game loop) -> mo ta ro truoc khi lam.
- Khong xoa tinh nang. Class debug (CheckRegionScreen) chi go khoi luong build cuoi, khong xoa voi.

## 1. Cac van de da xac dinh (bang chung tu doc code)
| # | Van de | Bang chung | Anh huong |
|---|--------|-----------|-----------|
| P1 | `Engine` la `static` + singleton toan cuc, dung chung moi Screen | `BaseScreen.protected static Engine engine`; `MainGame.getEngine()` | Ro ri entity/system giua cac man; kho test |
| P2 | Vong doi Screen khong nhat quan (add system trong `show()`, cleanup trung o `hide()`+`dispose()`) | `WorldMapScreen.show/hide/dispose`, `BattleScreen.show/hide/dispose` | Kho suy luan, de double-dispose |
| P3 | Trang thai `static` mutable rai rac | `BattleScreen.instance/isPause/ENEMY_TEAM/skill`, `WorldMapScreen.map/btnNextMap/btnAttackBattle` | Khong reset khi vao lai; kho test |
| P4 | `loadingAsset()` static + switch trong `ScreenManager.needLoadingFor/createScreen` | `ScreenManager` | Vi pham Open/Closed: them screen phai sua ScreenManager |
| P5 | Logic nghiep vu nhoi trong Screen | `setupEnemies`, `setupTeleportTriggers` trong WorldMapScreen | Tang View qua nang |

## 2. Kien truc dich (target)
- **Screen layer**: `MainGame` (Game) -> `ScreenManager` (dieu phoi) -> cac `BaseScreen`.
  - `BaseScreen` cung cap vong doi RO RANG: `onEnter()` (setup systems/entities), `onExit()` (teardown), tach khoi `show()/hide()` cua LibGDX de kiem soat.
  - Moi Screen dung ECS override `registerSystems(engine)` / `registerEntities(engine)` thay vi nhoi vao `show()`.
- **ECS layer**: Engine KHONG con static. Screen dung ECS so huu vong doi Engine cua no (hoac Engine duoc reset sach — `removeAllSystems + removeAllEntities` — o dung 1 cho khi enter/exit).
  - Chuan hoa: chi 1 diem cleanup, khong trung.
- **Loading**: thay switch static bang co che khai bao (moi Screen tu khai bao asset can load) de ScreenManager khong phai biet tung loai.

## 3. Cac buoc thuc hien (step-by-step)

### BUOC 0 — Baseline (chua sua code)
- Chay `gradlew.bat test` va `gradlew.bat build`, ghi lai ket qua GOC (pass/fail, so test).
- Muc dich: co moc so sanh. Neu baseline da fail san -> ghi nhan, khong tinh la loi do refactor.
- **Verify**: luu output.

### BUOC 1 — Bo `static` khoi Engine trong BaseScreen (P1) [rui ro thap]
- Doi `protected static Engine engine;` -> `protected final Engine engine;` (van lay tu `MainGame.getEngine()` truoc mat de khong doi hanh vi).
- Kiem tra 3 system tham chieu `MainGame.getEngine()` (`CollisionUpdateSystem`, `DebugDrawSystem`, `EnemyCollisionSystem`) van chay dung.
- **Verify**: build + test PASS. Khong doi hanh vi (van 1 engine).

### BUOC 2 — Chuan hoa vong doi ECS ve 1 diem (P2) [rui ro thap-trung]
- Trong `BaseScreen`: them `onEnter()` / `onExit()` mac dinh rong; goi `onEnter()` trong `show()` sau khi add rootGroup, goi `onExit()` trong `hide()`.
- Screen dung ECS: chuyen phan `engine.addSystem(...)` + setup entity tu `show()` sang `onEnter()`; don cleanup ve `onExit()` (goi `engine.removeAllSystems(); engine.removeAllEntities();` DUY NHAT 1 cho).
- Go cleanup trung trong `dispose()` (giu `dispose()` chi lo asset/UI, khong dung cham ECS 2 lan).
- **Verify sau tung Screen**: build + test PASS. Chay tay `gradlew.bat lwjgl3:run` de mat kiem tra WorldMap + Battle vao/ra khong crash (neu moi truong cho phep chay GUI).

### BUOC 3 — Tach logic setup ra khoi WorldMapScreen (P5) [rui ro thap]
- Chuyen `setupEnemies`, `setupTeleportTriggers` sang mot lop helper/factory (vd `WorldMapEntityBuilder` trong `ecs/factory`), giu chu ky goi giong het.
- WorldMapScreen chi goi builder. Khong doi logic ben trong.
- **Verify**: build + test PASS.

### BUOC 4 — Gom trang thai static cua Screen (P3) [rui ro trung]
- `BattleScreen`: bo `static` cho `isPause`, `ENEMY_TEAM`, `skill`, `mapBattle`, `skillBaseList`, `characterBaseList` (chuyen thanh field instance). Xu ly `instance`/`showBtnNextMap`/`showBtnAttackBattle` (static duoc goi tu ngoai) can kiem tra ai goi -> giu API nhung tro ve field instance qua tham chieu Screen hien tai neu buoc.
- Tim tat ca caller cua cac static nay truoc khi doi (grep). Neu co caller ngoai -> thiet ke cau noi an toan.
- **Verify**: build + test PASS. Kiem tra caller khong con tham chieu static cu.

### BUOC 5 — Loading theo khai bao thay vi switch (P4) [rui ro trung]
- Cho moi Screen dung ECS/asset: dinh nghia cach khai bao asset can load (vd method instance `preload()` hoac interface `AssetLoading`).
- `ScreenManager.needLoadingFor` doi tu switch cung sang kiem tra Screen co implement co che preload khong.
- Giu hanh vi Loading y het (van qua LoadingScreen).
- **Verify**: build + test PASS.

### BUOC 6 — Don dep & nhat quan hoa [rui ro thap]
- Bo `System.out.println` dư (ScreenManager...), thay bang `Gdx.app.log`.
- Chuan hoa dat ten/format cho cac file da dung cham.
- Danh gia go `CheckRegionScreen` (class debug) khoi luong build release — CHI khi xac nhan khong con dung.
- **Verify**: build + test PASS lan cuoi.

## 4. Chien luoc verify tong the
- Sau MOI buoc: `gradlew.bat test` + `gradlew.bat build`.
- Bo test hien co lam luoi an toan cho tang combat: `BattleStateTest`, `BattleSimulatorTest`, `TurnResultTest`, `CalculateHelperTest`, `CollisionUtilsTest`.
- Neu co the chay GUI: smoke test luong Menu -> SelectPlayer -> WorldMap -> Battle -> ra WorldMap.
- Neu 1 buoc khong verify duoc (moi truong chan) -> ghi ro, thu cach khac truoc khi bo qua.

## 5. Pham vi KHONG lam (out of scope lan nay)
- Khong ep UI popup (ShopPP/BagPP/HerosPP...) vao ECS (chong scene2d, rui ro cao).
- Khong refactor `DataHelper` god class trong dot nay (co the lam dot sau).
- Khong doi gameplay/so lieu can bang.

## 6. TRANG THAI: HOAN TAT (2026-09-23)
Tat ca 6 buoc da xong, behavior-preserving. Bang chung:
- Baseline: 45 test, 8 fail co san (7 BattleSimulatorTest NPE skillBase null + 1 CalculateHelperTest xp(10)). 37 pass.
- Sau MOI buoc (1..6): `:core:test` van dung **45 tests, 8 failed** — khong phat sinh fail moi. `:core:compileJava --rerun-tasks` BUILD SUCCESSFUL.
- Verify tong the cuoi: `:lwjgl3:compileJava` (module desktop) BUILD SUCCESSFUL.

Thay doi chinh:
- B1: `BaseScreen.engine` static -> final field.
- B2: BaseScreen them `onEnter()/onExit()`; cleanup ECS ve 1 diem (onExit); WorldMap/Battle/NewPlayer bo cleanup trung.
- B3: Tach `WorldMapEntityBuilder` (ecs/factory) khoi WorldMapScreen.
- B4: Bo static mutable o BattleScreen (isPause/ENEMY_TEAM/skill/mapBattle/skillBaseList/characterBaseList) va WorldMapScreen (map). Giu static co chu dinh: BattleScreen.instance, WorldMapScreen.btnNextMap/btnAttackBattle + showBtn* (cau noi System->Screen).
- B5: ScreenManager thay 2 switch cung bang bang dang ky EnumMap<ScreenType,ScreenSpec>.
- B6: Doi System.out.println -> Gdx.app.debug/log trong ScreenManager, TurnActionSystem, EnemyCollisionSystem, TeleportTriggerSystem, SkillStateSystem, SelectPlayerScreen. Giu CheckRegionScreen (khong xoa tinh nang debug).

Con lai (dot sau, out of scope): System.out.println trong tang du lieu/UI (JsonSaver, JsonUtil, DataHelper, BattleLogger, cac *PP widget).

## 7. DOT 2 - Tach Screen con lon + don dep (2026-09-23)
Behavior-preserving. Bang chung: moi task `:core:compileJava --rerun-tasks` OK; verify cuoi `:core:compileJava` + `:lwjgl3:compileJava` OK; **45 tests, 0 failures, 0 errors**; game khoi dong khong crash (log LoadingScreen -> MenuScreen show()).

Da lam trong cac phien truoc dot 2:
- Sua 8 test fail co san: CalculateHelperTest (xp(10) 50000->5000, loi go test), ListSkillComponent guard null + test dung constructor rong. -> 45 pass.
- Refactor DataHelper god class (647 LOC -> facade 136 LOC): tach 7 loader trong com.game.utils.data (AccountProfileLoader, HeroLoader, ItemEquipLoader, SkillCharacterLoader, MissionRewardLoader, BattleMapLoader, JsonAccess). Giu 27 method static facade, khong pha 60 caller.
- Don toan bo System.out/err/printStackTrace (9 file) sang Gdx.app.debug/error.

Dot 2 - tach Screen:
- WorldMapScreen: tach `WorldMapAssets.load()` (preload asset). HUD/popup giu trong Screen (rang buoc rootGroup).
- NewPlayerScreen: tach `NewPlayerAssets.load()` + `NewPlayerAccountCreator.create(name, knightId)` (persistence: profile/lineup/hero_full/accounts/info/maininfo).
- SelectPlayerScreen: tach `SelectPlayerAssets.load()` + `SelectPlayerLoader.selectAccount(engine, account)` (tao entity ECS + load profile/equip/hero/mission...).

Con lai (dot sau, out of scope): tach cac widget PP lon (HerosPP 523, ShopPP 298, BagPP 318...) - static-heavy, rui ro cao khi khong chay GUI verify; smoke test GUI day du luong Battle can user chay tay.
