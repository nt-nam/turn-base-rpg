# 08 · Công thức làm việc với game client

> Dành cho kỹ sư client. Nên đọc trước [02-architecture.md](02-architecture.md) (luật phụ thuộc) và [07-setup-and-environments.md](07-setup-and-environments.md) (dựng máy). Mọi đường dẫn và lệnh trong tài liệu này đã được đối chiếu với code nhánh `rewrite` ngày 2026-09-26.
>
> Lệnh chạy từ gốc repo. Trên Windows, dùng Git Bash. Gradle luôn chạy với `--settings-file settings-test.gradle` để không cần Android SDK.

**Lưu ý về kiến trúc thật:** MASTER_PLAN §4.3 mô tả mỗi màn có ba lớp Screen/Presenter/View. Code hiện tại gộp chúng vào **một lớp** kế thừa `GameScreen`: hàm `build()` dựng cây scene2d từ `GameState`, còn nút bấm gọi luật qua `context.act`. Các công thức dưới đây đi theo code thật.

---

## 0. Nắm nhanh client

### 0.1 Bản đồ file

| File | Vai trò |
|---|---|
| [GameApp.kt](../../game/client/src/main/kotlin/com/pxworld/client/GameApp.kt) | Composition root: tạo `Stage` 1280×720 (`ExtendViewport`), `AssetService`, `UiKit`, `Navigator`, `AudioDirector`, `CloudSync`, `ScreenContext`. Mỗi khung hình: cấu hình âm thanh, chọn nhạc, `navigator.update`, vẽ thế giới, vẽ stage, gửi telemetry. Phím Esc/Back gọi `navigator.back()`, phím F1 mở debug menu (chỉ khi `flavor.debugTools`) |
| [core/GameServices.kt](../../game/client/src/main/kotlin/com/pxworld/client/core/GameServices.kt) | `BuildFlavor`, `GameClock`, `GameServices` (content, saves, clock, flavor, replays, cloudUrl, clientVersion + các luật `rules`, `collection`, `quests`, `newGame`, `battles`, `catalog`), `GameSession` (store đang mở, `lastBattle`, `pendingBattleLog`) |
| [navigation/Navigation.kt](../../game/client/src/main/kotlin/com/pxworld/client/navigation/Navigation.kt) | `GameScreen`, `Presentation` (`FULL`, `MODAL`), `ScreenArgs`, `ScreenRegistry`, `Navigator` |
| [navigation/ScreenContext.kt](../../game/client/src/main/kotlin/com/pxworld/client/navigation/ScreenContext.kt) | Mọi thứ một màn được dùng, kèm `act()` để gọi luật |
| [screens/ScreenSupport.kt](../../game/client/src/main/kotlin/com/pxworld/client/screens/ScreenSupport.kt) | Lớp cơ sở `StandardScreen`, `ModalScreen`, `TextPageScreen`, `ConfirmScreen`; `SpriteActor`; `Lookup` (tra tên, icon, sprite từ content) |
| [screens/DefaultScreens.kt](../../game/client/src/main/kotlin/com/pxworld/client/screens/DefaultScreens.kt) | Registry `GameScreenId → factory`. Màn không có ở đây thì không mở được |
| `screens/<feature>/<Feature>Screens.kt`, `<Feature>ExtraScreens.kt` | Màn theo module của catalog: `boot`, `onboarding`, `world`, `battle`, `heroes`, `inventory`, `economy`, `progression`, `settings`, `debug` |
| [ui/UiKit.kt](../../game/client/src/main/kotlin/com/pxworld/client/ui/UiKit.kt), [ui/Widgets.kt](../../game/client/src/main/kotlin/com/pxworld/client/ui/Widgets.kt) | `Tokens` (màu, khoảng cách), skin sinh bằng code, widget có `testId` |
| [world/](../../game/client/src/main/kotlin/com/pxworld/client/world/) | `MapLayout` (đọc object layer Tiled), `Pathfinder` (A*), `WorldEcs.kt` (component và system Fleks) |
| [core/AssetService.kt](../../game/client/src/main/kotlin/com/pxworld/client/core/AssetService.kt), [core/AudioDirector.kt](../../game/client/src/main/kotlin/com/pxworld/client/core/AudioDirector.kt), [core/Localization.kt](../../game/client/src/main/kotlin/com/pxworld/client/core/Localization.kt) | Tài nguyên theo asset key, âm thanh theo cue, chuỗi theo key |
| [core/HttpCloudGateway.kt](../../game/client/src/main/kotlin/com/pxworld/client/core/HttpCloudGateway.kt) | Cài đặt `CloudGateway` bằng `Gdx.net`, lưu token theo URL máy chủ |
| [automation/StageAutomationDriver.kt](../../game/client/src/main/kotlin/com/pxworld/client/automation/StageAutomationDriver.kt) | Phía game của automation protocol: cây UI, tap, mở màn, trạng thái, invariant |
| [ClientTextTest.kt](../../game/client/src/test/kotlin/com/pxworld/client/ClientTextTest.kt) | Test: mọi key `ui.*` có đủ vi/en; màn đăng ký đều thuộc launch |

### 0.2 Một thao tác đi qua đâu

1. Người chơi bấm nút → `Widgets.button` phát âm `audio.sfx.click` rồi gọi lambda của màn.
2. Màn gọi `context.act(message) { state -> context.services.rules.xxx(state, …) }`.
3. `GameStore.dispatch` chạy luật (hàm thuần, trả `Transition(state, events)`), rồi chạy `followUp` = `QuestTracker.react` + `CollectionRules.record` (tiến độ quest, nhật ký).
4. Store **ghi save ngay** (`SaveRepository.save`), rồi báo listener.
5. `GameApp.watchStore` nhận `(state, events)`: map event sang telemetry, rồi `navigator.broadcast` tới **các màn đang hiển thị**. Mặc định `onStateChanged` gọi `rebuild()`.
6. Nếu luật ném `GameRuleViolation`, `act` hiện toast đỏ `ui.error.rule` và trả `null`. Mọi exception khác làm crash game.

### 0.3 Lệnh dùng xuyên suốt

```bash
./gradlew --settings-file settings-test.gradle :game:application:test :game:client:test :tools:architecture:test
./gradlew --settings-file settings-test.gradle :game:platform-desktop:installDist
PXWORLD_FLAVOR=DEV PXWORLD_API_URL=off game/platform-desktop/build/install/platform-desktop/bin/platform-desktop
node tools/screen-catalog/catalog.mjs --check
tools/test-agent/run-desktop.sh "$PWD/agent-reports/scenario"
MODE=explore tools/test-agent/run-desktop.sh "$PWD/agent-reports/scenario"
```

Trên Windows, launcher là `platform-desktop.bat`. `:game:platform-desktop:run` cũng chạy được, nhưng thư mục làm việc khi đó là `game/platform-desktop`, nên save legacy trong `data/` sẽ không được tự nhập.

Biến môi trường desktop (đọc trong `DesktopOptions.fromEnvironment`):

| Biến | Mặc định | Ý nghĩa |
|---|---|---|
| `PXWORLD_FLAVOR` | `DEV` | `DEV`, `QA`, `PILOT`, `RELEASE` |
| `PXWORLD_ENV` | `local` | Tên môi trường, cũng là thư mục save |
| `PXWORLD_SAVE_DIR` | `~/.pxworld/<env>/saves` | Thư mục save và replay |
| `PXWORLD_AUTOMATION_PORT` | `47017` | Cổng automation, chỉ mở khi `flavor.automation` và giá trị > 0 |
| `PXWORLD_API_URL` | `http://localhost:8080` khi flavor `DEV`, không có ở flavor khác | `off` để tắt cloud |
| `PXWORLD_WIDTH`, `PXWORLD_HEIGHT` | `1280`, `720` | Kích thước cửa sổ |

---

## 1. Thêm một màn hình game

**Khi nào dùng:** làm một màn còn thiếu trong catalog (bảng ở §11), hoặc thêm màn mới hoàn toàn.

**Ví dụ xuyên suốt:** màn `game.economy.energy_refill` ("Nạp năng lượng"), mở từ menu tạm dừng, đổi ngọc lấy năng lượng. Màn này gọi luật `CollectionRules.refillEnergy` ở §2. Nếu luật chưa có, làm §2 trước. Số liệu (20 ngọc → 60 năng lượng) chỉ là minh hoạ, giá thật do thiết kế kinh tế quyết định (WP-C11).

### Bước 1 — Chọn ScreenId trong catalog

1. Tìm màn trong [docs/screens/SCREEN_CATALOG.md](../screens/SCREEN_CATALOG.md) hoặc [screens.json](../screens/screens.json). Hằng số Kotlin là ID bỏ tiền tố `game.`, đổi `.` thành `_`, viết hoa: `game.economy.energy_refill` → `GameScreenId.ECONOMY_ENERGY_REFILL`.
2. **Nếu ID đã có** (đa số trường hợp, còn 224 màn launch chưa làm), sang bước 2. Không chạy lại generator.
3. **Nếu cần ID mới:**
   1. Sửa [tools/screen-catalog/catalog.mjs](../../tools/screen-catalog/catalog.mjs): thêm một dòng `slug|Tên|Mục đích` vào block `screens` của group có `surface: "game", module: "<module>"`. ID sinh ra là `game.<module>.<slug>`. Màn thuộc mùa S1–S4 thì sửa [expansion.mjs](../../tools/screen-catalog/expansion.mjs).
   2. Chạy `node tools/screen-catalog/catalog.mjs`. Lệnh này ghi lại 6 file: `docs/screens/{screens.csv,screens.json,SCREEN_CATALOG.md}`, [GameScreenId.kt](../../game/screens/src/main/kotlin/com/pxworld/screens/GameScreenId.kt) và [web/packages/screen-catalog/src/screenIds.ts](../../web/packages/screen-catalog/src/screenIds.ts).
   3. Chạy `node tools/screen-catalog/catalog.mjs --check` (CI job `screen-catalog` chạy đúng lệnh này) và `:game:screens:test` (`GameScreenIdTest` so thứ tự enum với catalog).
   4. Commit **cả** `catalog.mjs` và 6 file sinh ra trong cùng một commit. Không sửa tay file sinh ra.
4. Không bao giờ đổi tên hoặc xoá ScreenId đã có: telemetry, frame Figma và kịch bản test agent đều dùng ID.

### Bước 2 — Chọn lớp cơ sở

| Lớp | Dùng khi | Bạn viết |
|---|---|---|
| `StandardScreen` | Màn toàn phần có header (tiêu đề + nút Back `<screenId>/back`) | `titleKey`, `body(content)`, tuỳ chọn `showBack = false` |
| `ModalScreen` | Hộp thoại nổi trên màn dưới, nền scrim, rộng 420–900 px | `dialog(content)` |
| `ConfirmScreen` | Hộp thoại xác nhận hai nút `cancel` và `confirm` | `titleText`, `messageText`, `confirm()`, tuỳ chọn `confirmText`, `confirmStyle` |
| `TextPageScreen` | Trang chữ nhiều mục (FAQ, credits) | `titleKey`, `sections: List<Pair<headingKey, bodyKey>>` |
| `CloudScreen` ([AccountScreens.kt](../../game/client/src/main/kotlin/com/pxworld/client/screens/settings/AccountScreens.kt)) | Màn gọi máy chủ | xem §7 |
| `TutorialScreen` ([TutorialScreens.kt](../../game/client/src/main/kotlin/com/pxworld/client/screens/onboarding/TutorialScreens.kt)) | Hộp thoại hướng dẫn một lần | ID tutorial; thêm vào `Tutorials.order` |
| `GameScreen` | Màn tự vẽ (world, battle, splash) | `build(content)`, `update`, `renderWorld`, `resize` |

Vòng đời của `GameScreen`:

| Hàm | Khi nào chạy |
|---|---|
| `build(content)` qua `rebuild()` | Khi push, khi màn trên nó bị `back()`, và mỗi lần state đổi nếu màn đang hiển thị. Phải nhanh và chỉ đọc state |
| `onShow()` | Một lần, ngay sau lần `build` đầu tiên. Chỗ để gọi mạng, mở tutorial |
| `update(delta)` | Mỗi khung hình, cho mọi màn đang hiển thị |
| `renderWorld(delta)` | Mỗi khung hình, chỉ cho màn `FULL` dưới cùng của phần đang hiển thị (world, battle) |
| `onStateChanged(state, events)` | Sau mỗi `dispatch`, chỉ cho màn đang hiển thị. Mặc định là `rebuild()` |
| `onHide()`, `dispose()` | Khi bị gỡ khỏi stack |

"Đang hiển thị" nghĩa là màn `FULL` trên cùng cùng mọi `MODAL` phía trên nó. Màn `FULL` bị che **không** nhận `onStateChanged`; khi quay lại nó chỉ được `rebuild()`.

### Bước 3 — Viết lớp màn

`ScreenContext` cung cấp:

| Thuộc tính | Dùng để |
|---|---|
| `services.content` | `ContentBundle`: mọi bản ghi content (`heroes`, `items`, `maps`, `localization`…) |
| `services.catalog` | `ContentCatalog`: giá, phần thưởng, quest, achievement dưới dạng đã chuẩn hoá |
| `services.rules`, `services.collection`, `services.quests`, `services.newGame`, `services.battles` | Luật (application) và bộ dựng trận |
| `services.clock`, `services.flavor`, `services.saves`, `services.replays` | Đồng hồ, flavor, kho save, kho replay |
| `state`, `store` | `GameState` hiện tại. **Ném lỗi `no game loaded` nếu chưa mở save.** Màn mở được từ menu chính phải kiểm tra `context.session.store == null` trước |
| `act(message) { … }` | Gọi luật. Trả `Transition?`: `null` nghĩa là luật từ chối và toast đỏ đã hiện |
| `navigator` | Điều hướng và `toast(message, positive)` |
| `text` | `Localization`: `text("ui.key", arg0, arg1)` thay `{0}`, `{1}` |
| `widgets` (trong màn viết là `ui`) | Tạo widget có `testId` |
| `ui` (`UiKit`), `assets`, `audio`, `batch` | Skin và drawable, tài nguyên, âm thanh, `SpriteBatch` dùng chung |
| `preferences`, `logs`, `debugFlags`, `cloud` | Tuỳ chọn cấp thiết bị (pháp lý, đồng ý analytics, locale), log, cờ debug, `CloudSync?` |

Widget (`Widgets.kt`) và style (`UiKit.kt`):

| Hàm | Ghi chú |
|---|---|
| `ui.label(value, style, testId, wrap)` | Style: `title`, `heading`, `body`, `muted`, `small`, `positive`, `negative`. `wrap = true` cần cell có `.width(...)` |
| `ui.button(testId, caption, style, enabled) { … }` | Style: `primary`, `secondary`, `danger`, `ghost`, `tab`, `tab-active` |
| `ui.textField(testId, value, hint)` | Đọc chữ bằng `field.text` ngay lúc bấm nút, không dựa vào sự kiện gõ phím |
| `ui.panel(padding, color)`, `ui.scroll(content, testId)`, `ui.image(region, size, testId)`, `ui.bar(fraction, color, w, h)`, `ui.centered(value)` | |
| `Tokens.SPACE_XS/S/M/L` = 4/8/16/24, `Tokens.BUTTON_HEIGHT` = 48 | Không viết số màu hay khoảng cách tuỳ tiện |
| `Lookup(context)` | `heroName`, `itemName`, `itemIcon`, `currencyName`, `grantLabel(grant)`, `statLines(stats)`… |

Thêm lớp vào [EconomyExtraScreens.kt](../../game/client/src/main/kotlin/com/pxworld/client/screens/economy/EconomyExtraScreens.kt). File này đã import sẵn mọi thứ cần dùng:

```kotlin
class EnergyRefillScreen(context: ScreenContext, args: ScreenArgs) : ModalScreen(GameScreenId.ECONOMY_ENERGY_REFILL, context, args) {

    override fun dialog(content: Table) {
        val lookup = Lookup(context)
        val wallet = context.state.wallet
        val gems = wallet.balance(Currencies.GEM)
        content.add(ui.label(text("ui.energy.title"), "title", testId("title"))).colspan(2).row()
        content.add(ui.label(text("ui.energy.balance", wallet.balance(Currencies.ENERGY), gems), "muted", testId("balance"))).colspan(2).padBottom(Tokens.SPACE_M).row()
        content.add(ui.label(text("ui.energy.offer", EconomyTuning.ENERGY_PER_REFILL, EconomyTuning.GEMS_PER_ENERGY_REFILL, lookup.currencyName(Currencies.GEM)), "body", testId("offer"), wrap = true))
            .width(480f).colspan(2).padBottom(Tokens.SPACE_M).row()
        content.add(ui.button(testId("cancel"), text("ui.common.cancel"), "secondary") { context.navigator.back() }).growX().padRight(Tokens.SPACE_S)
        content.add(ui.button(testId("confirm"), text("ui.energy.refill"), enabled = gems >= EconomyTuning.GEMS_PER_ENERGY_REFILL) {
            context.act(text("ui.energy.done")) { context.services.collection.refillEnergy(it) }
        }).growX()
    }
}
```

Không cần tự làm mới số dư. `act` → store → `broadcast` → màn đang hiển thị được `rebuild()`.

### Bước 4 — Đăng ký

Trong [DefaultScreens.kt](../../game/client/src/main/kotlin/com/pxworld/client/screens/DefaultScreens.kt), thêm import và một dòng vào map:

```kotlin
import com.pxworld.client.screens.economy.EnergyRefillScreen
```
```kotlin
            GameScreenId.ECONOMY_ENERGY_REFILL to ::EnergyRefillScreen,
```

Một lớp phục vụ nhiều ScreenId thì nhận `id` qua constructor phụ, như `ShopHomeScreen` hay `BattleResultScreen`:

```kotlin
            GameScreenId.ECONOMY_SHOP_ITEMS to { context, args -> ShopHomeScreen(GameScreenId.ECONOMY_SHOP_ITEMS, context, args) },
```

Chỉ đăng ký màn mùa `LAUNCH`: `ClientTextTest.every registered screen belongs to the launch catalog` sẽ đỏ nếu đăng ký màn S1–S4.

### Bước 5 — Điều hướng và tham số

| Hàm `Navigator` | Tác dụng | Ví dụ thật |
|---|---|---|
| `open(id, args)` | Push lên stack | Chi tiết quest từ danh sách quest |
| `replace(id, args)` | Bỏ màn trên cùng rồi push | Menu tạm dừng → màn đích; xác nhận mua → kết quả mua |
| `replaceFrom(screen, id, args)` | Bỏ `screen` **và mọi thứ phía trên nó**, rồi push | Trận kết thúc thay chính màn trận, kể cả khi đang mở modal tạm dừng |
| `back()` | Bỏ màn trên cùng; màn lộ ra được `rebuild()`. Không làm gì khi stack còn 1 | Nút Back, phím Esc |
| `backTo(id)` | Bỏ tới khi `id` ở trên cùng | Bản đồ vùng → về world |
| `reset(id, args)` | Xoá stack, push `id` | Về menu chính, vào world sau khi tải save |
| `toast(message, positive)` | Bong bóng chữ 1,8 giây | Thông báo thành công hoặc lỗi |

Tham số là `Map<String, String>`: `ScreenArgs.of("quest" to quest.id)`. Đọc bằng `args["quest"]` (ném `missing screen argument quest` nếu thiếu) hoặc `args.optional("quest")`. Chỉ truyền ID (instance ID của hero hay trang bị, ID content), không truyền object. Màn đích tự tra lại trong `context.state`.

Mở màn ví dụ từ menu tạm dừng: thêm một dòng vào list `entries` của `PauseMenuScreen` ([WorldScreens.kt](../../game/client/src/main/kotlin/com/pxworld/client/screens/world/WorldScreens.kt)):

```kotlin
            "energy" to GameScreenId.ECONOMY_ENERGY_REFILL,
```

Nhãn nút là `text("ui.pause.$key")`, một **key động**. Vì vậy phải thêm `energy` vào họ `"ui.pause."` trong `dynamicFamilies` của [ClientTextTest.kt](../../game/client/src/test/kotlin/com/pxworld/client/ClientTextTest.kt) (xem §3).

### Bước 6 — testId cho automation

- Mọi actor tương tác đặt `name = "<screenId>/<element>"` qua `testId("element")`. Automation chỉ thấy actor có `/` trong tên.
- Phần tử lặp theo dữ liệu: `testId("buy/${offer.id}")`, `testId("claim/${quest.id}")`.
- Header tự sinh `<screenId>/back` và `<screenId>/title`. Đơn vị trong trận là `game.battle.battle_main/unit/ally#0`.
- Nút tiêu tài nguyên hoặc phá dữ liệu phải dùng một từ có trong regex `denied` của explorer ([run.mjs](../../tools/test-agent/run.mjs)): `confirm`, `buy`, `claim`, `sell_selected`, `delete`, `dismiss`… Ví dụ trên dùng `confirm` nên explorer không bấm. Nếu cần từ mới, thêm từ đó vào regex.

### Bước 7 — Chuỗi hiển thị

Thêm key vào `tools/content-migrator/ui-strings-extended.mjs` rồi chạy generator (chi tiết ở §3):

```js
  "ui.energy.title": ["Nạp năng lượng", "Refill energy"],
  "ui.energy.balance": ["Năng lượng {0} | Ngọc {1}", "Energy {0} | Gems {1}"],
  "ui.energy.offer": ["Nhận {0} năng lượng với {1} {2}", "Get {0} energy for {1} {2}"],
  "ui.energy.refill": ["Nạp", "Refill"],
  "ui.energy.done": ["Đã nạp năng lượng", "Energy refilled"],
  "ui.pause.energy": ["Năng lượng", "Energy"],
```

### Bước 8 — Explorer và kịch bản

**Explorer đến màn của bạn bằng cách nào:** `run.mjs --mode=explore` gọi `screen.registered`. Với mỗi màn đã đăng ký (trừ tập `destructive`), nó gọi `screen.open` với `argsFor[screenId] ?? {}`, chụp ảnh, bấm tối đa 6 nút `TextButton` bật và không bị `denied`, rồi `invariants.check`. Nếu có key trong `argsFor` nhưng giá trị rỗng, màn bị bỏ qua (ghi `trace`).

- Màn **cần tham số** thì thêm một dòng vào `argsFor` trong `explore()`, lấy từ `state` sẵn có. Ví dụ thật: `"game.progression.quest_detail": firstQuest && { quest: firstQuest }`.
- Màn cần dữ liệu người chơi chưa có (hero thứ hai, trang bị…) thì thêm cheat (§8) và bấm cheat đó trong `primeMerge`.
- Explorer **không tự tạo save**. Nó bấm `continue` ở menu chính nếu có slot trong `PXWORLD_SAVE_DIR`. Cách chắc chắn nhất là chạy `core-loop` trước với **cùng thư mục out**, rồi chạy explorer:

```bash
tools/test-agent/run-desktop.sh "$PWD/agent-reports/scenario"
MODE=explore tools/test-agent/run-desktop.sh "$PWD/agent-reports/scenario"
```

- Explorer chỉ ghi lỗi từng màn vào `trace`, **không** làm run FAILED. Luôn mở `report.json` và kiểm tra: không có dòng `explore error on game.economy.energy_refill`; `coverage.registeredNotVisited` không chứa màn của bạn.

Kịch bản riêng cho luồng chính của màn: tạo `tools/test-agent/scenarios/energy-refill.mjs`. Tên file chính là giá trị `SCENARIO`.

```js
import { assert } from "../automation-client.mjs";

export const name = "energy-refill";
export const description = "Refill energy with gems from the pause menu";

export async function run(agent, shot) {
  await agent.passFirstRun();
  await agent.waitScreen("game.boot.main_menu", 20_000);
  await agent.tap("game.boot.main_menu/new_game");
  await agent.waitScreen("game.onboarding.hero_create_class");
  await agent.tap("game.onboarding.hero_create_class/pick/aldric");
  await agent.waitScreen("game.onboarding.hero_create_name");
  await agent.type("game.onboarding.hero_create_name/name", "Energy Tester");
  await agent.tap("game.onboarding.hero_create_name/next");
  await agent.waitScreen("game.onboarding.hero_create_confirm");
  await agent.tap("game.onboarding.hero_create_confirm/start");
  await agent.waitScreen("game.world.world_explore");
  const before = await agent.state();
  await agent.tap("game.world.world_explore/menu");
  await agent.tap("game.world.pause_menu/energy");
  await agent.waitScreen("game.economy.energy_refill");
  await shot("01-energy-refill");
  await agent.tap("game.economy.energy_refill/confirm");
  const after = await agent.waitFor(async () => {
    const state = await agent.state();
    return (state.balances["currency.energy"] ?? 0) > 0 ? state : null;
  }, 5000, "energy refilled");
  assert(after.balances["currency.gem"] === before.balances["currency.gem"] - 20, "refill costs 20 gems");
  await agent.assertInvariants("energy refill");
}
```

```bash
SCENARIO=energy-refill tools/test-agent/run-desktop.sh "$PWD/agent-reports/energy"
```

### Kiểm tra

1. `node tools/screen-catalog/catalog.mjs --check`
2. `./gradlew --settings-file settings-test.gradle :game:screens:test :game:application:test :game:client:test :tools:architecture:test`
3. Chạy game, F1 → **Nhảy tới màn hình** (`game.debug.debug_screen_jump`): dòng coverage tăng 1, màn mới có trong danh sách. Chỉ dùng Screen Jump cho màn **không cần tham số**.
4. Kịch bản mới PASSED; `core-loop` và `chapter1` vẫn PASSED; explorer không có lỗi cho màn mới.

### Lỗi thường gặp

| Triệu chứng | Nguyên nhân | Sửa |
|---|---|---|
| Crash `screen game.x.y is not implemented yet` | Quên đăng ký trong `DefaultScreens` | Bước 4 |
| Crash `missing screen argument …` | Người gọi (hoặc Screen Jump, hoặc explorer) không truyền tham số | Truyền đủ; dùng `args.optional` nếu tham số không bắt buộc; thêm `argsFor` |
| Crash `no game loaded` | Màn đọc `context.state` khi chưa mở save | Kiểm tra `context.session.store == null` rồi hiện `ui.debug.needs_game` hoặc tắt nút, như `SettingsHomeScreen` |
| Nhãn hiện nguyên chuỗi `ui.xxx`; invariant báo `untranslated key` | Thiếu key, hoặc key động chưa khai báo họ | §3 |
| Test agent báo `no visible actor …` | `testId` sai, actor bị ẩn, hoặc tên không có `/` | Dùng `testId(...)`; xem `screen.tree` |
| Test agent báo `… is covered by …` | Có modal hoặc tutorial đang che | Đóng modal trước; thêm màn vào `DISMISSIBLE` trong `automation-client.mjs` nếu là màn tự đóng được |
| Số liệu cũ sau khi quay lại từ màn khác | Lưu `context.state` vào field từ lúc tạo màn | Luôn đọc `context.state` bên trong `build` |
| Màn chậm khi có thay đổi state | `build` làm việc nặng (load atlas, dựng map) | Chuyển sang `onShow` hoặc cache trong field |
| Màn `FULL` đổi state mà world không phản ứng | World bị che nên không nhận `onStateChanged`, chỉ `rebuild()` | Làm màn đó `MODAL`, hoặc xử lý trong `build` của màn dưới |
| Chữ nhập bị mất khi màn dựng lại | Đọc chữ qua sự kiện gõ phím | Giữ tham chiếu `TextField`, đọc `field.text` lúc bấm (xem `LoginEmailScreen.capture`) |

---

## 2. Thêm một luật game / use case

**Khi nào dùng:** mọi thay đổi `GameState` do người chơi gây ra (mua, nhận, nâng cấp, tiêu). Màn hình **không bao giờ** tự tính hay tự sửa state.

### Luật nằm ở đâu

| File (`game/application/.../application/`) | Chứa |
|---|---|
| [GameRules.kt](../../game/application/src/main/kotlin/com/pxworld/application/GameRules.kt) | `Counters`, `Currencies`; `GameRules`: `heroStats`, `equip`, `unequip`, `useExperienceItem`, `buyItem`, `buyEquipment`, `claimCheckin`, `recruit`, `raiseStar`, `placeInLineup`, `finishBattle`, `enterMap`, `talkTo`, `claimQuest`, `claimAchievementTier`, `grant`; `spend` (private) |
| [Collection.kt](../../game/application/src/main/kotlin/com/pxworld/application/Collection.kt) | `EconomyTuning` (hằng số kinh tế); `CollectionRules`: bán, phân rã, cường hoá, sa thải, khoá hero, đổi ngọc, treo máy, preset đội hình, `record` (nhật ký), `fastTravel`, `markTutorial`, `addPlayTime` |
| [Progression.kt](../../game/application/src/main/kotlin/com/pxworld/application/Progression.kt) | `LineupCapacity`, `QuestTracker` (`startAll`, `react`), `GameState.isQuestActive`, `NewGame` |
| [GameEvent.kt](../../game/application/src/main/kotlin/com/pxworld/application/GameEvent.kt) | `GameEvent` (sealed), `Transition`, `GameRuleViolation` |
| [ContentCatalog.kt](../../game/application/src/main/kotlin/com/pxworld/application/ContentCatalog.kt) | Cổng đọc content (`Grant`, `Price`, `QuestSummary`…). Cài đặt thật: `ContentBundleCatalog` trong `game/content` |
| `game/domain/.../progression/GameState.kt`, `.../economy/Wallet.kt` | State bất biến và invariant; `Wallet.credit/debit` trả `WalletChange(wallet, entry)` |

### Quy ước bắt buộc

1. Chữ ký: `fun ten(state: GameState, …): Transition`. Hàm thuần: không I/O, không `System.currentTimeMillis`, không `Random()`. Thời gian và seed đi vào qua tham số (`nowMillis`, `todayEpochDay`, `seed`). `:tools:architecture:test` quét và chặn các lời gọi này trong `domain` và `application`.
2. Từ chối hành động của người chơi bằng `throw GameRuleViolation("…")`. Chỉ loại này được `context.act` bắt. `require`/`check` trong domain (ví dụ `Inventory.addItem` với số lượng ≤ 0, `Wallet.credit` với số ≤ 0) sẽ **crash**, nên phải kiểm tra trước khi gọi.
3. Ví và ledger: cộng tiền qua `rules.grant(state, grants, LedgerReason(kind, ref))`. Trừ tiền qua `wallet.debit`, rồi **luôn** nối `change.entry` vào `ledgerTail` (`takeLast(GameState.LEDGER_TAIL_SIZE)`) và phát `GameEvent.CurrencyChanged`. Automation có invariant `ledger tail for X disagrees with wallet`. Tiêu ngọc thì tăng `Counters.GEMS_SPENT`.
4. Hằng số cân bằng đặt trong `EconomyTuning` hoặc companion của lớp luật. Số liệu thuộc về content thì đọc qua `ContentCatalog`.
5. Luật mới đặt vào `CollectionRules` nếu thuộc sưu tập/kinh tế phụ; vào `GameRules` nếu là vòng lặp lõi (trận, quest, cửa hàng). `GameServices` đã tạo sẵn cả hai, nên không cần sửa wiring.

### Bước làm — ví dụ `refillEnergy`

1. `GameRules.kt`, object `Currencies`, thêm:

```kotlin
    const val ENERGY = "currency.energy"
```

2. `Collection.kt`, object `EconomyTuning`, thêm:

```kotlin
    const val ENERGY_PER_REFILL: Long = 60
    const val GEMS_PER_ENERGY_REFILL: Long = 20
```

3. `Collection.kt`, lớp `CollectionRules`, thêm hàm (theo đúng mẫu `exchangeGems`):

```kotlin
    fun refillEnergy(state: GameState): Transition {
        val cost = EconomyTuning.GEMS_PER_ENERGY_REFILL
        val change = try {
            state.wallet.debit(Currencies.GEM, cost, LedgerReason("energy_refill"))
        } catch (shortfall: com.pxworld.domain.economy.InsufficientFunds) {
            throw GameRuleViolation(shortfall.message ?: "insufficient funds")
        }
        val paid = state.copy(
            wallet = change.wallet,
            ledgerTail = (state.ledgerTail + change.entry).takeLast(GameState.LEDGER_TAIL_SIZE),
            stats = state.stats.increment(Counters.GEMS_SPENT, cost),
        )
        val gained = rules.grant(paid, listOf(Grant(GrantKind.CURRENCY, Currencies.ENERGY, EconomyTuning.ENERGY_PER_REFILL)), LedgerReason("energy_refill"))
        return Transition(gained.state, listOf(GameEvent.CurrencyChanged(change.entry)) + gained.events)
    }
```

4. Test trong [CollectionRulesTest.kt](../../game/application/src/test/kotlin/com/pxworld/application/CollectionRulesTest.kt). Hàm `game()` của lớp test có sẵn 1.000 vàng, 200 ngọc và 4 `item.food_t1`:

```kotlin
    @Test
    fun `energy refill spends gems, grants energy and writes both ledger entries`() {
        val refill = collection.refillEnergy(game())
        assertEquals(200 - EconomyTuning.GEMS_PER_ENERGY_REFILL, refill.state.wallet.balance(Currencies.GEM))
        assertEquals(EconomyTuning.ENERGY_PER_REFILL, refill.state.wallet.balance(Currencies.ENERGY))
        assertEquals(listOf("energy_refill", "energy_refill"), refill.state.ledgerTail.takeLast(2).map { it.reason.kind })
        assertEquals(2, refill.events.filterIsInstance<GameEvent.CurrencyChanged>().size)
        assertEquals(EconomyTuning.GEMS_PER_ENERGY_REFILL, refill.state.stats.value(Counters.GEMS_SPENT))
    }

    @Test
    fun `energy refill is refused without enough gems`() {
        val broke = collection.exchangeGems(game(), 200).state
        assertThrows<GameRuleViolation> { collection.refillEnergy(broke) }
    }
```

Test của `GameRules` dùng `FakeCatalog` trong [GameRulesTest.kt](../../game/application/src/test/kotlin/com/pxworld/application/GameRulesTest.kt). Nếu luật cần dữ liệu content mới, thêm hàm vào interface `ContentCatalog`, cài đặt trong `ContentBundleCatalog` (`game/content`) **và** trong `FakeCatalog`.

5. Gọi từ màn: `context.act(text("ui.energy.done")) { context.services.collection.refillEnergy(it) }` (xem §1).

### Khi nào thêm `GameEvent` mới

Chỉ thêm khi có nơi cần nghe: tiến độ quest (`QuestTracker.contribution`), nhật ký (`CollectionRules.record`), telemetry (`TelemetryMapping.of` trong [Cloud.kt](../../game/application/src/main/kotlin/com/pxworld/application/Cloud.kt)), hoặc màn kết quả (`BattleResultScreen`). Ví dụ:

```kotlin
    data class EnergyRefilled(val amount: Long) : GameEvent
```

Các `when` hiện có trên `GameEvent` đều có nhánh `else`, nên thêm subtype không làm hỏng biên dịch. Hãy tự tìm mọi `is GameEvent.` để quyết định chỗ nào cần xử lý thêm.

### Khi luật cần thêm trường vào state (ảnh hưởng save)

Ví và túi đồ là map mở, nên loại tiền hay vật phẩm mới **không** cần sửa save. Chỉ khi thêm trường mới vào `GameState` thì làm đủ 5 bước:

1. `GameState.kt`: thêm trường có **giá trị mặc định** (thường nằm trong `PlayerJournal` hoặc `PlayerSettings`).
2. [SaveGameDocument.kt](../../game/infrastructure/src/main/kotlin/com/pxworld/infrastructure/save/SaveGameDocument.kt): thêm trường tương ứng vào `*Document`, **bắt buộc có mặc định**. Ví dụ `val tutorialsSeen: List<String> = emptyList()`. Thiếu mặc định thì mọi save cũ báo `CorruptSave`.
3. `SaveGameMapper.toDocument` và `toState`: map hai chiều; set và map phải sắp xếp (`sorted()`, `toSortedMap()`) để save ổn định từng byte.
4. Test tương thích: giữ fixture cũ và thêm fixture mới vào `tools/test-agent/fixtures/`, theo mẫu `v2 saves written before the journal existed still load` trong `SaveAndImportTest`. Không tăng `SaveCodec.SCHEMA_VERSION` trừ khi phá tương thích (cần ADR).
5. `./gradlew --settings-file settings-test.gradle :game:infrastructure:test`.

Save cũ luôn đọc được. Ngược lại, client cũ **không** đọc được save có trường mới (`ignoreUnknownKeys = false`), và đó là chủ đích.

### Kiểm tra
`./gradlew --settings-file settings-test.gradle :game:application:test :game:infrastructure:test :tools:architecture:test`, sau đó `core-loop` PASSED.

### Lỗi thường gặp

| Triệu chứng | Nguyên nhân | Sửa |
|---|---|---|
| `:tools:architecture:test` đỏ ở `domain randomness and time come from explicit inputs` | Dùng `System.currentTimeMillis`, `LocalDate.now`, `Random()`… trong application | Nhận `nowMillis` hoặc `seed` làm tham số; màn truyền `context.services.clock.nowMillis()` |
| Game crash thay vì toast đỏ | Ném `IllegalArgumentException`/`IllegalStateException`, hoặc để domain `require` ném | Kiểm tra trước, ném `GameRuleViolation` |
| Invariant `ledger tail … disagrees with wallet` | Trừ ví mà không nối ledger | Mẫu ở bước 3 |
| `lineup references a hero that is not owned` (khi tải save hoặc trong test) | Xoá hero hay trang bị mà không gỡ khỏi đội hình hoặc người mặc | Làm theo `dismissHero`/`raiseStar`: gỡ trước rồi mới xoá |
| Quest không tiến dù có event | Quest có `requires` chưa hoàn thành, hoặc `objectiveKind` không có trong `QuestTracker.contribution` | Thêm nhánh vào `contribution` **và** `OBJECTIVE_KINDS` của `ContentValidator` **và** họ `ui.quests.objective.` trong `ClientTextTest` |

---

## 3. Thêm chuỗi UI / key bản địa hoá

**Khi nào dùng:** mọi chữ hiển thị trong client không đến từ content. Tên hero, item, quest… là **key content**, xem [09 §3.7](09-recipes-content.md#37-bản-địa-hoá-cho-mọi-thứ-ở-trên).

### Pipeline thật

| Nguồn (sửa ở đây) | Sinh ra (không sửa tay) |
|---|---|
| [tools/content-migrator/ui-strings.mjs](../../tools/content-migrator/ui-strings.mjs) (object `strings`) và [ui-strings-extended.mjs](../../tools/content-migrator/ui-strings-extended.mjs) (object `extendedStrings`) | `content/localization/vi.ui.json`, `content/localization/en.ui.json` (579 key, sắp theo alphabet) |

`ContentLoader` gộp mọi `content/localization/<locale>.*.json` theo phần tên trước dấu chấm đầu tiên. Vì vậy `vi.json` (key content) và `vi.ui.json` (key UI) tạo thành một bảng `vi`. `Localization` tra theo thứ tự: locale hiện tại → `vi` → trả **chính key**.

### Bước làm

1. Thêm dòng vào `extendedStrings` (hoặc `strings`), dạng `"ui.<khu>.<tên>": ["tiếng Việt", "English"]`. Tham số viết `{0}`, `{1}`… Trùng key thì script ném `duplicate ui key`.
2. Chạy:

```bash
node tools/content-migrator/ui-strings.mjs
```

   Kết quả: `ui strings: <N> keys x 2 locales`, hai file `*.ui.json` được ghi lại.
3. Dùng trong màn: `text("ui.energy.offer", 60, 20, gemName)`. Mọi màn đều có `text`.
4. Nếu key được **ghép động** (`"ui.pause.$key"`, `"ui.stat.${statKey(stat)}"`), thêm hậu tố vào đúng họ trong `dynamicFamilies` của `ClientTextTest`, hoặc tạo họ mới:

```kotlin
        "ui.pause." to "heroes lineup bag shop recruit checkin quests achievements settings map minimap tracker codex exchange idle replays tips energy".split(" "),
```

5. `./gradlew --settings-file settings-test.gradle :game:client:test`.

### ClientTextTest hoạt động thế nào
- Quét mọi chuỗi literal dạng `"ui.xxx"` trong `game/client/src/main/kotlin` (regex `"(ui\.[a-z0-9_.]+[a-z0-9_])"`), cộng tất cả `prefix + suffix` của `dynamicFamilies`. Rồi kiểm tra từng key có trong bảng `vi` **và** `en`.
- Chuỗi có `$` không khớp regex, nên key động **không được phát hiện tự động**. Họ động hiện có: `ui.pause.`, `ui.slot.`, `ui.stat.`, `ui.settings.`, `ui.language.`, `ui.account.kind.`, `ui.debug.`, `ui.debug.flag.`, `ui.battle.pause_`, `ui.skill.slot.`, `ui.skill.target.`, `ui.heroes.link.`, `ui.heroes.tool.`, `ui.bag.action.`, `ui.recruit.link.`, `ui.outcome.`, `ui.quests.objective.`, `ui.region.`, `ui.counter.`, `ui.result.`, `ui.tips.`, `ui.glossary.`, `ui.controls.`, `ui.credits.`, `ui.help.`, `ui.faq.`, `ui.patch.`, `ui.tutorial_*.`.
- Lưới an toàn khi chạy: invariant của automation báo `untranslated key ui.x at <testId>` cho mọi label có chữ bắt đầu bằng `ui.`.

### Lỗi thường gặp

| Triệu chứng | Nguyên nhân | Sửa |
|---|---|---|
| Sửa `vi.ui.json` rồi mất | File được sinh ra | Sửa trong `.mjs` rồi chạy lại |
| Test xanh nhưng game hiện `ui.region.crystalwood` | Key động chưa khai báo trong họ | Thêm hậu tố vào `dynamicFamilies` |
| Chữ có ô vuông hoặc bị mất ký tự | Font bitmap chỉ chứa ký tự có trong localization lúc build (`AssetPipeline.charsetFrom`) | Build lại asset (tự chạy khi build desktop). Ký tự đặc biệt dùng trong code mà không có trong localization thì thêm vào `EXTRA_GLYPHS` |
| Toast đỏ hiện câu tiếng Anh | `ui.error.rule` chèn `GameRuleViolation.message`, là chuỗi kỹ thuật tiếng Anh | Hạn chế đã biết. Kiểm tra điều kiện trước ở UI (tắt nút) để người chơi ít gặp |
| `{1}` hiện nguyên trong chữ | Truyền thiếu tham số | Số tham số phải khớp placeholder |

---

## 4. Khám phá thế giới

### Cách world hoạt động

| Thành phần | File | Việc |
|---|---|---|
| `WorldExploreScreen` | [WorldScreens.kt](../../game/client/src/main/kotlin/com/pxworld/client/screens/world/WorldScreens.kt) | `loadMap(mapId, spawn)`: tra `MapRecord` → `assets.map(record.asset)` → `MapLayout` → `OrthogonalTiledMapRenderer` → dựng một `World` Fleks riêng cho map. Mỗi khung hình kiểm tra trigger, hiện nút `talk` / `inspect_enemy` / `travel`, và lưu vị trí 5 giây một lần qua `rules.enterMap` |
| `MapLayout` | [MapLayout.kt](../../game/client/src/main/kotlin/com/pxworld/client/world/MapLayout.kt) | Đọc các object layer theo **tên** (bảng dưới) |
| Fleks | [WorldEcs.kt](../../game/client/src/main/kotlin/com/pxworld/client/world/WorldEcs.kt) | Component `Transform`, `Motion`, `Body`, `Appearance`, `PlayerControlled`; system `ControlSystem` (bàn phím WASD/mũi tên, joystick, lộ trình) → `MovementSystem` (va chạm theo trục x rồi y) → `AnimationSystem` (idle/run, hướng mặt) → `RenderSystem` (camera bám người chơi trong biên map, vẽ tile rồi sprite sắp theo y) |
| `Pathfinder` | [Pathfinder.kt](../../game/client/src/main/kotlin/com/pxworld/client/world/Pathfinder.kt) | A* trên lưới 8 px, dùng cho `steerTo(x, y)` (automation `world.moveTo`) |
| `Dialogues`, `NpcDialogueScreen`, `DialogueChoiceScreen` | [DialogueScreens.kt](../../game/client/src/main/kotlin/com/pxworld/client/screens/world/DialogueScreens.kt) | Chọn hội thoại theo quest, hành động của lựa chọn |

Trigger được kiểm tra trong `WorldExploreScreen.update`, không phải trong system ECS. Toạ độ là toạ độ libGDX (trục y hướng lên, TmxMapLoader đã lật so với Tiled). Kịch bản test lấy toạ độ từ `world.info`, không lấy từ Tiled.

### Object layer trong Tiled (`assets/tilemap/map/<legacy>.tmx`)

| Layer (đúng tên) | Loại object | Thuộc tính | Dùng làm |
|---|---|---|---|
| `wall` | Rectangle, polygon, ellipse | — | Collider |
| `line` | Polyline | — | Collider dạng đường |
| `player` | Point | `index` (int) | Điểm xuất hiện. `spawn(index)` rơi về điểm đầu tiên nếu không tìm thấy |
| `teleport` | Rectangle | `map` (string, **legacyName** của map đích), `spawn` (int, `index` ở map đích), `name` (nhãn nút) | Nút "Đi tới {name}" |
| `enemies` | Rectangle hoặc point | `id` (int) | Trigger trận. ID trận là `encounter.<map bỏ "map.">.e<id>` |
| `npc` | Point | `name` (string) | Chỗ đứng NPC, khớp `placements[].object` trong content. `name` rỗng thì bỏ qua |

Các thuộc tính khác (`block`, `needClearMap`, `vn`, `Description`) hiện bị bỏ qua. Layer tile phải mã hoá **CSV** và tileset phải là file `.tsx` ngoài cùng cỡ ô, vì asset pipeline cắt lại tileset (§10).

### Công thức: thêm một teleport

1. Trong Tiled, trên map nguồn, vẽ rectangle ở layer `teleport` với `map` = `legacyName` của map đích (ví dụ `garden1`), `spawn` = một `index` có trong layer `player` của map đích, `name` = nhãn.
2. Map đích phải có `MapRecord` với `legacyName` trùng. Nếu không, nút hiện toast `ui.world.travel_blocked`.
3. Kiểm tra: F1 → Nhảy tới màn hình → `game.debug.debug_map_inspector`, chọn map. Màn hiện số teleport và danh sách `-> nhãn (legacy#spawn)`. Validator content **không** kiểm tra teleport trong tmx.
4. Chạy game, đi vào vùng teleport, bấm `game.world.world_explore/travel`. Nếu map đích có quest `reach_map` thì quest phải tiến.

### Công thức: thêm tương tác NPC

**Chỉ dữ liệu** (NPC, hội thoại, điều kiện theo quest): làm theo [09 §3.4](09-recipes-content.md#34-thêm-quest--hội-thoại--npc). Không cần sửa code.

**Hành động mới cho lựa chọn hội thoại** (ví dụ mở màn điểm danh):

1. [ContentValidator.kt](../../game/content/src/main/kotlin/com/pxworld/content/ContentValidator.kt):

```kotlin
    private val DIALOGUE_ACTIONS = setOf("open_shop", "open_recruit", "open_bag", "open_checkin")
```

2. `Dialogues.perform` trong [DialogueScreens.kt](../../game/client/src/main/kotlin/com/pxworld/client/screens/world/DialogueScreens.kt):

```kotlin
            "open_checkin" -> context.navigator.open(GameScreenId.ECONOMY_DAILY_CHECKIN)
```

3. Content dùng `"action": "open_checkin"` trong một `choices[]`. Lựa chọn có `action` thì không đặt `next`. `DialogueChoiceScreen` gọi `back()` rồi mới `perform`.
4. Kiểm tra: `:game:content:test`, rồi nói chuyện với NPC trong game. Explorer mở `game.world.dialogue_choice` với `dialogue.merchant_trade`; nếu cần explorer đi qua hội thoại mới thì sửa `argsFor` tương ứng.

`talk_to_npc` được tính **ngay lúc mở hội thoại** (`NpcDialogueScreen.onShow` gọi `rules.talkTo`), không đợi tới node cuối.

### Công thức: thêm component và system Fleks

Ví dụ: NPC quay mặt về phía người chơi.

```kotlin
class FacesPlayer : Component<FacesPlayer> {
    override fun type() = FacesPlayer
    companion object : ComponentType<FacesPlayer>()
}

class FacePlayerSystem : IteratingSystem(family { all(FacesPlayer, Transform, Appearance) }) {

    private val players = world.family { all(PlayerControlled, Transform) }

    override fun onTickEntity(entity: Entity) {
        val player = players.firstOrNull() ?: return
        entity[Appearance].facingLeft = player[Transform].position.x < entity[Transform].position.x
    }
}
```

1. Viết vào `WorldEcs.kt`. Package `com.pxworld.client.world` không được import luật game.
2. Trong `WorldExploreScreen.loadMap`, đăng ký trong `systems { }`, **trước** `RenderSystem`: `add(FacePlayerSystem())`. Thêm `it += FacesPlayer()` vào entity NPC.
3. Dependency (camera, batch, layout) lấy bằng `inject()`, và phải có trong `injectables { }`.
4. Mỗi lần đổi map, `World` cũ bị `dispose()` và dựng lại. Không giữ entity qua các map.

### Công thức: loại trigger mới (rương, biển báo…)

1. `MapLayout`: thêm `data class ChestTrigger(val bounds: Rectangle, val chestId: String)` và `val chests = layer("chest").map { … triggerBounds(it) … }`.
2. `WorldExploreScreen.update`: tìm trigger chồng lên `footprint` như `nearbyEncounter`, rồi gọi `refreshActions()`. `refreshActions` thêm nút `testId("open_chest")`.
3. Nút gọi **luật** (§2), ví dụ `context.act { context.services.collection.openChest(it, chestId) }`. Trạng thái "đã mở" nằm trong `GameState`, nên phải theo quy trình save ở §2.
4. `triggers()`: thêm danh sách để `world.info` trả về, giúp kịch bản di chuyển tới đó.
5. Màn catalog liên quan: `game.world.chest_open`, `game.world.signpost_read` (còn thiếu).

### Lỗi thường gặp

| Triệu chứng | Nguyên nhân | Sửa |
|---|---|---|
| Đứng lên chỗ quái, nút `inspect_enemy` bị tắt | Không có `EncounterRecord` với ID `encounter.<map>.e<id>` | Đặt đúng ID theo quy ước (09 §3.2) |
| NPC không xuất hiện | `placements[].object` khác `name` trong layer `npc`, hoặc map khác | Sửa cho khớp từng ký tự (có khoảng trắng, ví dụ `village chief`) |
| Sau khi di chuyển nhanh từ **Bản đồ vùng**, world vẫn vẽ map cũ | `RegionMapScreen` là màn `FULL` và dùng `backTo(world)`. World chỉ được `rebuild()`, không nhận `onStateChanged`, nên không `loadMap`. Sau ≤ 5 giây, `persistPosition` ghi lại map cũ | Lỗi đã biết, cần WP sửa. Hiện chỉ `FastTravelScreen` (modal) chạy đúng |
| Người chơi kẹt trong tường khi vào map | Điểm spawn nằm trong collider | Dời point ở layer `player` |

---

## 5. Trình diễn trận đấu

### `BattleMainScreen` điều khiển `BattleEngine` thế nào ([BattleScreens.kt](../../game/client/src/main/kotlin/com/pxworld/client/screens/battle/BattleScreens.kt))

1. `init`: dựng `ReplaySlot` từ đội hình (hero, cấp, sao, ô, bonus trang bị), seed = `clock.nowMillis()` (hoặc seed của replay). `services.battles.battle(seed, lineup, encounterId)` → `BattleEngine.start` → state đầu và danh sách event.
2. Mọi event được xếp vào `queue` (để phát) và `log` (cho màn nhật ký). **State của domain luôn đi trước** hàng đợi. Vì vậy phần hình chỉ được cập nhật theo event, không đọc thẳng `state` để vẽ HP hay trạng thái.
3. `update(delta)`: nếu có màn khác ở trên cùng (`navigator.current !== this`) thì trận **tự dừng**. Nếu không, `wait -= delta * speed`, phát event cho tới khi `wait > 0` (`play()` trả số giây chờ). Khi hết hàng đợi:
   - có `outcome` → `finish`;
   - replay → lệnh kế tiếp trong `script`;
   - `auto` bật hoặc lượt phe địch → `BattleEngine.autoCommand(state)` (xem `AutoPolicy`), chờ `AI_THINK_SECONDS`;
   - lượt phe ta → hiện nút kỹ năng `skill/<skillId>`. Kỹ năng cần chọn mục tiêu thì đặt `pendingSkill`, rồi chờ bấm đơn vị `unit/<side#slot>` hoặc mở `game.battle.target_select`.
4. `finish(outcome)`: lưu `ReplayRecord` (`replay-<seed>`), gọi `rules.finishBattle` qua `context.act` (thưởng, XP, bộ đếm, quest), ghi `session.lastBattle`, và sau 1 giây gọi `navigator.replaceFrom(this, <victory|defeat|draw>, encounter)`.
5. Tạm dừng: nút `pause` mở `game.battle.battle_pause` (modal). Nhật ký: nút `log` ghi `session.pendingBattleLog` rồi mở `game.battle.battle_log`, mỗi dòng là `BattleEvent.describe()`. Tốc độ: nút `speed` quay vòng 1→2→4; giá trị đầu lấy từ `settings.battleSpeed`. Bỏ chạy: `FleeConfirmScreen` gọi `battle.flee()` (tính là `DEFEAT`).
6. Replay: `game.battle.replay_viewer` chính là `BattleMainScreen` với tham số `replay`. Nó dùng seed, đội hình và lệnh đã lưu, không trao thưởng, và toast `ui.replay.finished` khi hết. Replay chạy trên **content hiện tại**. Đổi chỉ số hay kỹ năng có thể làm một lệnh cũ không còn hợp lệ, và khi đó `BattleEngine.apply` ném `IllegalBattleCommand`.

`play()` là một `when` phủ hết mọi `BattleEvent` (sealed). Thêm event mới vào domain thì client **không biên dịch** cho tới khi xử lý event đó, và các golden test ở `game/domain/src/test/resources/golden` sẽ đổi.

### Công thức: thêm icon trạng thái trên đầu đơn vị

Hiện trạng: trạng thái chỉ hiện thành chữ nổi một lần (`floatText`), không có icon lâu dài.

1. **Content** (cần creator và kỹ sư cùng làm): thêm trường tuỳ chọn vào `StatusRecord` trong [ContentRecords.kt](../../game/content/src/main/kotlin/com/pxworld/content/ContentRecords.kt): `val icon: String? = null`. Validator: trong vòng `bundle.statuses.forEach`, thêm `status.icon?.let { asset(status.id, it) }`, và cộng `bundle.statuses.mapNotNull { it.icon }` vào `usedAssets`. Chạy `./gradlew --settings-file settings-test.gradle :game:content:exportSchemas` rồi commit `content/schemas/statuses.schema.json` (`ContentSchemaTest` bắt schema cũ). Map khoá `icon:status/<slug>` trong `content/assets/legacy_asset_map.json`.
2. **`CombatantActor`**: thêm hàng icon (import `com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup`):

```kotlin
    private val statusRow = HorizontalGroup().apply { space(2f) }
    private val statusIcons = mutableMapOf<String, Actor>()

    fun showStatus(statusId: String, icon: Actor) {
        statusIcons.remove(statusId)?.remove()
        statusIcons[statusId] = icon
        statusRow.addActor(icon)
    }

    fun hideStatus(statusId: String) {
        statusIcons.remove(statusId)?.remove()
    }

    fun clearStatuses() {
        statusIcons.values.forEach { it.remove() }
        statusIcons.clear()
    }
```

   Trong `init`, thêm `statusRow.setPosition(0f, SIZE + 32f)` và `addActor(statusRow)`.
3. **`BattleMainScreen.play()`**: cập nhật theo event, và bỏ `StatusExpired` khỏi nhánh `is BattleEvent.BattleStarted, is BattleEvent.StatusExpired -> 0f`:

```kotlin
            is BattleEvent.StatusApplied -> {
                units[event.target]?.showStatus(event.statusId, statusBadge(event.statusId, event.stacks))
                floatText(event.target, text(context.services.content.statuses.firstOrNull { it.id == event.statusId }?.name ?: event.statusId), Tokens.energy)
                STATUS_SECONDS
            }
            is BattleEvent.StatusExpired -> {
                units[event.target]?.hideStatus(event.statusId)
                0f
            }
```

   Trong nhánh `UnitDefeated`, gọi thêm `unit.clearStatuses()`. Engine xoá trạng thái của đơn vị chết mà **không** phát `StatusExpired`.

```kotlin
    private fun statusBadge(statusId: String, stacks: Int): Actor {
        val record = context.services.content.statuses.firstOrNull { it.id == statusId }
        val icon = record?.icon
        val suffix = if (stacks > 1) stacks.toString() else ""
        return if (icon != null && context.assets.has(icon)) ui.image(context.assets.region(icon), STATUS_ICON_SIZE)
        else ui.label(text(record?.name ?: statusId).take(2) + suffix, "small")
    }
```

   Thêm `const val STATUS_ICON_SIZE: Float = 20f` vào companion.

### Công thức: hiệu ứng hình ảnh mới

- **VFX của kỹ năng:** chỉ cần dữ liệu. `SkillRecord.vfx` là khoá `vfx:<màu>/<trạng thái>`, trỏ tới `atlas#region` có nhiều frame đánh số (`AssetService.effect`). `SkillUsed` sinh `EffectActor` trên mỗi mục tiêu.
- **Hiệu ứng theo event** (rung màn khi chí mạng, cut-in tuyệt kỹ): viết trong nhánh tương ứng của `play()` bằng `Actions` của scene2d trên `arena` hoặc `CombatantActor`. Giá trị trả về là thời gian giữ nhịp (giây, bị chia cho `speed`).

```kotlin
                if (event.critical) arena.addAction(Actions.sequence(Actions.moveBy(6f, 0f, 0.04f), Actions.moveBy(-12f, 0f, 0.08f), Actions.moveBy(6f, 0f, 0.04f)))
```

- Cài đặt `reducedMotion` có trong `PlayerSettings` nhưng **chưa màn nào đọc**. Hiệu ứng mới nên bỏ qua rung/lướt khi `context.state.settings.reducedMotion`.

### Kiểm tra
- F1 → Nhảy tới màn hình → `game.debug.debug_battle_sandbox`: bấm **Chiến đấu** ở bất kỳ encounter nào (cần đã mở save).
- `core-loop` (đánh tay và auto), explorer (mở mọi modal của trận rồi bật `battle.auto` tới khi có kết quả; khẳng định màn trận không còn trong stack).
- `./gradlew --settings-file settings-test.gradle :game:domain:test` nếu có chạm domain.

---

## 6. Âm thanh

[AudioDirector.kt](../../game/client/src/main/kotlin/com/pxworld/client/core/AudioDirector.kt) đọc cue từ `content/audio_cues/*.json` (`id`, `kind` = `music`/`sound`, `asset`, `volumePercent`) và phân giải `asset` qua `assetMap`.

| API | Hành vi |
|---|---|
| `playMusic(cueId)` | Đổi nhạc (lặp). Gọi lại cùng cue thì không làm gì. `null` thì tắt |
| `playSound(cueId)` | Phát một lần nếu `soundEnabled`. Cue không tồn tại hoặc file lỗi thì im lặng (không crash) |
| `configure(music, sound)` | `GameApp.render` gọi **mỗi khung hình** với `state.settings.musicEnabled/soundEnabled`. Chưa mở save thì cả hai là `true` |

Hằng số sẵn có: `WORLD_MUSIC`, `BATTLE_MUSIC`, `CLICK` (mọi nút qua `Widgets`), `HIT`, `CRITICAL` (trận).

**Phát SFX mới:** creator thêm cue (09 §3.6), kỹ sư thêm hằng số `const val CONFIRM = "audio.sfx.confirm"` vào companion của `AudioDirector` và gọi `context.audio.playSound(AudioDirector.CONFIRM)` ở chỗ cần. Chỉ gọi từ client, không bao giờ từ luật.

**Nhạc theo vùng:** việc chọn nhạc nằm cứng trong `GameApp.musicFor` (module `battle` → nhạc trận, còn lại → nhạc world). Muốn mỗi vùng một bản, sửa như sau (cue `audio.music.<region>` do creator thêm):

```kotlin
    private fun musicFor(screen: GameScreenId?): String? = when {
        screen == null -> null
        screen.module == "battle" -> AudioDirector.BATTLE_MUSIC
        else -> regionMusic() ?: AudioDirector.WORLD_MUSIC
    }

    private fun regionMusic(): String? {
        val mapId = context.session.store?.state?.position?.mapId ?: return null
        val region = services.content.maps.firstOrNull { it.id == mapId }?.region?.removePrefix("region.") ?: return null
        return "audio.music.$region".takeIf { cue -> services.content.audioCues.any { it.id == cue } }
    }
```

Kiểm tra: `session.info` của automation trả `music` (cue đang phát) và `musicEnabled`. `core-loop` khẳng định `audio.music.world` khi khám phá và `audio.music.battle` trong trận. Nếu đổi cue, sửa cả kịch bản.

---

## 7. Tính năng cloud trong client

`context.cloud` là `CloudSync?`. Nó `null` khi không có URL máy chủ (`PXWORLD_API_URL=off`, hoặc flavor khác `DEV` mà không đặt URL). API trong [Cloud.kt](../../game/application/src/main/kotlin/com/pxworld/application/Cloud.kt): `signInAsGuest`, `signIn`, `register`, `signOut`, `list`, `upload`, `keepLocal`, `preview`, `takeCloud`, `mail`, `claim`, `report`, `syncedRevision`, `account`. Callback được đưa về **render thread** (`Gdx.app.postRunnable`), nên được phép `rebuild()` ngay trong callback.

| `CloudResult` | Ý nghĩa | Cách xử lý chuẩn |
|---|---|---|
| `Ok(value)` | Thành công | Cập nhật field, `rebuild()` |
| `Conflict(current)` | Máy chủ có revision mới hơn (HTTP 409) | Mở `BOOT_SAVE_CONFLICT` với `AccountScreen.conflictArgs(current)` |
| `Rejected(status, message)` | 4xx/5xx. `401` nghĩa là phiên hết hạn và `CloudSync` đã tự xoá token | `ui.cloud.signed_out` nếu 401, còn lại `ui.cloud.rejected` |
| `Unreachable(message)` | Không kết nối được | `ui.cloud.unreachable` |

### Công thức: màn gọi máy chủ — ví dụ `game.social.mail_detail`

Kế thừa `CloudScreen` (trong [AccountScreens.kt](../../game/client/src/main/kotlin/com/pxworld/client/screens/settings/AccountScreens.kt)). Lớp này có `busy`, `problem`, `request<T>(call) { ok -> }` (tự bật busy, gọi, map lỗi qua `describe`, rồi `rebuild`), `statusRows(content)` và `action(content, element, key, style, enabled) { }`.

```kotlin
class MailDetailScreen(context: ScreenContext, args: ScreenArgs) : CloudScreen(GameScreenId.SOCIAL_MAIL_DETAIL, context, args) {

    override val titleKey = "ui.mail.detail"
    private val mailId = args["mail"]
    private var loaded: CloudMail? = null
    private var requested = false

    override fun onShow() {
        if (requested) return
        requested = true
        request<List<CloudMail>>({ mail(it) }) { listed -> loaded = listed.firstOrNull { it.id == mailId } }
    }

    override fun body(content: Table) {
        statusRows(content)
        val item = loaded ?: return
        val lookup = Lookup(context)
        content.add(ui.label(item.subject, "title", testId("subject"))).colspan(2).row()
        CloudSync.grantsOf(item, context.services.catalog).forEach { content.add(ui.label("+ ${lookup.grantLabel(it)}", "positive")).colspan(2).left().row() }
        action(content, "claim", "ui.mail.claim", enabled = !item.claimed && context.session.store != null) {
            request<List<Grant>>({ claim(item.id, context.services.catalog, it) }) { granted ->
                context.act(text("ui.mail.claimed")) { state -> context.services.rules.grant(state, granted, CloudSync.mailReason(item.id)) }
                loaded = item.copy(claimed = true)
            }
        }
    }
}
```

1. Đặt `onShow` sau cờ `requested`: `rebuild` chạy nhiều lần, còn request chỉ được gửi một lần.
2. Máy chủ xác nhận trước, rồi client mới cộng quà **cục bộ** qua `rules.grant` với `LedgerReason("mail", id)`. Không cộng khi request thất bại.
3. Đăng ký màn, thêm `ui.mail.detail`, và thêm `"game.social.mail_detail": { mail: "none" }` vào `argsFor` của explorer. Explorer chạy với cloud tắt, nên màn chỉ cần mở được mà không crash.
4. Kiểm tra với máy chủ thật: `SCENARIO=cloud tools/test-agent/run-desktop.sh "$PWD/agent-reports/cloud"` (script tự dựng server H2). Thêm bước vào [cloud.mjs](../../tools/test-agent/scenarios/cloud.mjs) nếu màn nằm trên luồng đó.

**Thêm một lời gọi API mới:** (1) thêm hàm vào interface `CloudGateway` và hàm bọc trong `CloudSync` (qua `authorized(done) { token, reply -> … }`); (2) cài đặt trong `HttpCloudGateway.send(...)`; (3) cài đặt trong `FakeServer` của [CloudSyncTest.kt](../../game/application/src/test/kotlin/com/pxworld/application/CloudSyncTest.kt) và viết test; (4) route phía server theo [10-recipes-server-and-console.md](10-recipes-server-and-console.md).

### Telemetry có đồng ý

- Nguồn duy nhất: `TelemetryMapping.of(GameEvent)` trong `Cloud.kt`, cộng `session.start` do `GameApp` gửi. `GameApp.track` chỉ ghi khi `cloud != null` **và** `preferences.analyticsConsent`. Cờ đồng ý đặt ở `game.boot.privacy_consent` và `game.settings.settings_privacy`, lưu theo thiết bị trong `AppPreferences`.
- Gửi theo lô ≤ 50 event mỗi 30 giây. Lỗi thì trả lại buffer (tối đa 200).
- Thêm event: thêm nhánh vào `TelemetryMapping.of`. Tên dạng `<khu>.<động_từ>`, payload chỉ gồm chuỗi.

```kotlin
        is GameEvent.AchievementTierClaimed -> TelemetryEvent("achievement.claim", mapOf("achievement" to event.achievementId, "tier" to event.tier.toString()))
```

- Màn hình **không có** API ghi telemetry trực tiếp. Muốn đo một hành động thì cho luật phát `GameEvent`, rồi map event đó.

---

## 8. Debug menu và cheat

- Vào debug menu bằng F1 hoặc nút `game.boot.main_menu/debug`. Cả hai chỉ hiện khi `services.flavor.debugTools` (`DEV`, `QA`). `DebugMenuScreen` liệt kê 3 mục: Nhảy tới màn hình, Cheat, Duyệt atlas. Các màn debug khác (sandbox trận, soi map, xem save, cờ, hiệu năng, log, xem bản dịch, automation) mở qua **Nhảy tới màn hình**.
- Chặn theo flavor **chỉ nằm ở lối vào**. Màn debug vẫn được đăng ký ở mọi flavor, và automation (`PILOT` có `automation = true`) mở được bằng `screen.open`. Cheat mới phải tự kiểm tra: `if (!context.services.flavor.debugTools) return` ở đầu `body`.
- `DebugFlags` (`showPerformance`, `showColliders`, `fastBattles`) bật/tắt được nhưng **chưa có code nào đọc**.

### Công thức: thêm cheat cấp phát

Ví dụ: +100 năng lượng, dùng `Currencies.ENERGY` từ §2. Trong `DebugCheatsScreen` ([DebugScreens.kt](../../game/client/src/main/kotlin/com/pxworld/client/screens/debug/DebugScreens.kt)), thêm một dòng vào list `grants`:

```kotlin
            "energy" to Grant(GrantKind.CURRENCY, Currencies.ENERGY, 100),
```

1. Nhãn là key động `ui.debug.grant_$key`: thêm `"ui.debug.grant_energy": ["+100 năng lượng", "+100 energy"]` vào `ui-strings-extended.mjs`, chạy generator, và thêm `grant_energy` vào họ `"ui.debug."` trong `ClientTextTest`.
2. Mọi cheat đi qua `rules.grant` với `LedgerReason("debug_cheat", key)`, nên ledger vẫn khớp ví. Không sửa `GameState` trực tiếp trong màn.
3. testId là `game.debug.debug_cheats/energy`.

### Công thức: cheat theo từng bản ghi content

Ví dụ: nhận một anh hùng bất kỳ, rất tiện cho creator thử hero mới. Thêm vào cuối `body` của `DebugCheatsScreen` (cần `import com.pxworld.client.screens.Lookup`):

```kotlin
        val lookup = Lookup(context)
        content.add(ui.label(text("ui.debug.grant_specific_hero"), "heading")).padTop(Tokens.SPACE_M).row()
        val heroes = Table()
        context.services.content.heroes.forEachIndexed { index, hero ->
            heroes.add(ui.button(testId("hero_pick/${hero.id}"), lookup.heroName(hero.id), "ghost") {
                context.act(text("ui.debug.granted")) { context.services.rules.grant(it, listOf(Grant(GrantKind.HERO, hero.id, 1)), LedgerReason("debug_cheat", hero.id)) }
            }).pad(Tokens.SPACE_XS)
            if (index % 4 == 3) heroes.row()
        }
        content.add(heroes).row()
```

`ui.debug.grant_specific_hero` là key literal, nên `ClientTextTest` tự bắt. Chỉ cần thêm chuỗi.

Cheat cần biến đổi state phức tạp (hoàn thành quest, mở khoá map): viết một hàm trong `application` có test (§2) rồi gọi qua `context.act`. Không dựng `GameState.copy(...)` trong màn debug.

### Dùng cheat để mồi dữ liệu cho explorer

Trong `explore()` của [run.mjs](../../tools/test-agent/run.mjs), hàm `primeMerge` mở `game.debug.debug_cheats` và bấm `hero`, `food`, `equipment`. Thêm bước cho dữ liệu màn của bạn cần:

```js
    await client.tap("game.debug.debug_cheats/energy");
```

Explorer tự bấm tối đa 6 nút không bị `denied` trên mỗi màn, kể cả nút cheat. Vì vậy cheat phải an toàn khi bị bấm lặp lại.

---

## 9. Launcher nền tảng

| Launcher | Được phép | Không được phép |
|---|---|---|
| [DesktopLauncher.kt](../../game/platform-desktop/src/main/kotlin/com/pxworld/desktop/DesktopLauncher.kt) (92 dòng) | Đọc biến môi trường vào `DesktopOptions`; nạp `content-pack.json` từ classpath; tạo `FileSaveStore`, `FileReplayStore`, `SystemClock`; chạy `LegacySaveMigration`; khởi động `AutomationServer`; cấu hình cửa sổ | Luật game, UI, đọc content để quyết định gameplay, gọi mạng |
| [AndroidLauncher.java](../../game/platform-android/src/main/java/com/pxworld/android/AndroidLauncher.java) (79 dòng) | Nạp `content-pack.json` từ asset APK; save ở `getFilesDir()/saves`; flavor và URL từ `BuildConfig` (debug: `QA` + `http://10.0.2.2:8080`; release: `PILOT` + `pxworld.releaseApiUrl`) | Như trên |

Luật được test tự động ([ArchitectureTest.kt](../../tools/architecture/src/test/kotlin/com/pxworld/architecture/ArchitectureTest.kt)):
- `platform launchers only wire modules together`: tổng số dòng `src/main` của **mỗi** module launcher phải < `LAUNCHER_LINE_BUDGET` = 250.
- `packages match their module`: desktop dùng `com.pxworld.desktop`, Android dùng `com.pxworld.android`.
- `client does not touch files or threads directly`: `game/client` không import `java.io.File`, `java.nio.file.*`, `Thread`, `Executors`. File I/O nằm trong `game/infrastructure` hoặc launcher.

Adapter hiện có:

| Cổng | Định nghĩa | Cài đặt |
|---|---|---|
| `GameClock` | `client/core/GameServices.kt` | `SystemClock` (desktop: object; Android: lớp lồng) |
| `SaveRepository` | `application/GameStore.kt` | `FileSaveStore` (ghi tmp + fsync + rename, giữ 3 bản `.bak`, checksum SHA-256) |
| `ReplayRepository` | `application/Replays.kt` | `FileReplayStore(dir, capacity)`, mặc định `InMemoryReplays` |
| `CloudGateway`, `CloudCredentialStore` | `application/Cloud.kt` | `HttpCloudGateway`, `PreferencesCredentialStore` (client/core, tạo trong `GameApp`) |

**Thêm tham số vào `GameServices`:** luôn thêm vào **cuối** constructor, có giá trị mặc định. `AndroidLauncher` gọi constructor theo vị trí từ Java (8 tham số, nhờ `@JvmOverloads`); chèn vào giữa sẽ làm Android không biên dịch.

### Ví dụ A — desktop báo đúng phiên bản client (hiện luôn gửi `"dev"`)

```kotlin
data class DesktopOptions(
    val flavor: BuildFlavor,
    val environment: String,
    val saveDirectory: File,
    val automationPort: Int?,
    val width: Int,
    val height: Int,
    val cloudUrl: String?,
    val clientVersion: String,
)
```

Trong `fromEnvironment` thêm `clientVersion = read("PXWORLD_CLIENT_VERSION") ?: "desktop-dev",`. Trong `main` truyền `clientVersion = options.clientVersion` vào `GameServices(...)`.

### Ví dụ B — khung cho WP-A1: bật automation trên Android

Làm theo đúng mẫu desktop (`driver` được gán trong `onReady`):

```java
    private static final int AUTOMATION_PORT = 47017;
    private StageAutomationDriver driver;
    private AutomationServer automation;
```

```java
        BuildFlavor flavor = BuildConfigFlavor.current();
        if (flavor.getAutomation()) {
            automation = new AutomationServer(AUTOMATION_PORT, new AutomationProtocol(() -> driver));
            automation.start();
        }
        GameServices services = new GameServices(
                loadContent(),
                new FileSaveStore(saves, 3),
                new SystemClock(),
                flavor,
                new FileReplayStore(new File(saves, "replays"), 30),
                api -> {
                    driver = ((GameApp) api).getAutomation();
                    return Unit.INSTANCE;
                },
                BuildConfig.PXWORLD_API_URL.isEmpty() ? null : BuildConfig.PXWORLD_API_URL,
                "android-" + BuildConfig.VERSION_NAME
        );
```

```java
    @Override
    protected void onDestroy() {
        if (automation != null) {
            try {
                automation.stop(500);
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
            }
        }
        super.onDestroy();
    }
```

Thêm `implementation(project(":game:automation"))` vào `game/platform-android/build.gradle.kts`. Kết nối từ máy dev bằng `adb forward tcp:47017 tcp:47017`. **Chưa chắc chắn:** `StageAutomationDriver.screenshot` dùng `java.nio.file.Paths`, vốn chỉ có từ API 26, trong khi `minSdk` = 24. Cần thử trên emulator API 24, hoặc đổi sang `Gdx.files.absolute(target)`.

---

## 10. Asset

### Asset key và `AssetService`

Content và code chỉ dùng **khoá** (`sprite:hero/aldric`, `icon:item/sword_000`, `vfx:orange/attack`, `bg:battle/spring`, `map:dawnvillage_01`, `music:world`, `sfx:click`, `font:body`). Bảng khoá → đường dẫn nằm ở [content/assets/legacy_asset_map.json](../../content/assets/legacy_asset_map.json). Đường dẫn tính từ gốc thư mục asset đã build (cấu trúc giống `assets/`).

| Hàm `AssetService` | Khoá phải trỏ tới | Ghi chú |
|---|---|---|
| `sprite(key)` | `*.atlas` (animation theo tên region, frame theo `index`) hoặc `*.png` (một frame `idle`) | Tên animation: `idle`, `run`, `attack`, `hurt`, `die` (`SpriteSet`). Thiếu tên nào thì dùng `idle` |
| `region(key)` | `file.atlas#region` | Icon |
| `effect(key)` | `file.atlas#region` nhiều frame | VFX, phát một lần |
| `texture(key)` | File ảnh | Nền trận |
| `map(key)` | `*.tmx` | |
| `resolve(key)`, `has(key)`, `atlasPaths()` | | `resolve` ném `unmapped asset key` nếu thiếu |

### `tools/asset-pipeline` làm gì (`AssetPipeline.run`)

```bash
./gradlew --settings-file settings-test.gradle :tools:asset-pipeline:buildAssets
```

1. Đọc `content/`, lấy **mọi giá trị** trong `legacy_asset_map.json` (không phải mọi file trong `assets/`).
2. `.atlas`: chép file atlas và các trang `.png` của nó. Pipeline **không** chạy TexturePacker. Art mới phải được đóng atlas sẵn (TexturePacker của libGDX: file `idle_0.png`, `idle_1.png` → region `idle` với index 0, 1).
3. `.tmx`: cắt tileset chỉ giữ các ô được dùng, thành `<map>_tiles.png/.tsx`, rồi ghi lại tmx.
4. File khác: chép nguyên. Đường dẫn bắt đầu bằng `backgrounds/` hoặc `fonts/` được coi là **sinh ra** và bị bỏ qua khi chép.
5. Nền trận: cứng 4 mùa, `texture/battle/<mùa>.png` → `backgrounds/battle_<mùa>.jpg` (rộng 1920, JPEG 85%). Thêm nền mới phải sửa `BATTLE_BACKGROUNDS`.
6. Font: sinh `fonts/pxworld_{title,body,small}.fnt` từ `art/fonts/BeVietnamPro-*.ttf` (cỡ 30/20/16), bộ ký tự lấy từ toàn bộ localization.
7. Kiểm tra: ảnh > 2048 px hoặc hai file trùng SHA-1 → `PROBLEM …` và thoát mã 1 (build desktop và Android đều hỏng).
8. Ghi `tools/asset-pipeline/build/assets/asset-manifest.json`. Desktop dùng thư mục này làm resources, Android dùng làm assets.

Thư mục nguồn `art/` theo MASTER_PLAN §9.2 **mới chỉ dùng cho font**. Mọi ảnh và âm thanh mới hiện đặt trong `assets/`.

### Công thức: thêm art mới (icon, sprite, VFX, âm thanh)

1. Đặt file vào `assets/<nhóm>/...` (tên ASCII, chữ thường, `snake_case`).
2. Thêm khoá vào `content/assets/legacy_asset_map.json`, giữ thứ tự alphabet.
3. Tham chiếu khoá từ bản ghi content (09) hoặc từ code (hằng số).
4. Thêm một dòng vào [art/LICENSES.md](../../art/LICENSES.md): tài nguyên, đường dẫn nguồn, có trong build hay không, giấy phép, trạng thái (nguồn/URL/tác giả và ngày kiểm tra). **Không merge art không rõ giấy phép.**
5. `./gradlew --settings-file settings-test.gradle :game:content:compileContent :tools:asset-pipeline:buildAssets`. Validator báo `asset key … has no mapping` / `maps to missing …` / `mapped but never used`.
6. Mở game, F1 → Duyệt atlas (`game.debug.debug_atlas_browser`) để xem region.

---

## 11. Màn hình game theo module: đã làm / tổng

Nguồn: `docs/screens/screens.json` (surface `game`, season `launch`, 357 màn) so với các `GameScreenId` được đăng ký trong `DefaultScreens.kt` (133 màn). Không có `GameScreenId` nào được tham chiếu trong `game/client/src/main` mà chưa đăng ký. Các mùa S1–S4 có thêm 243 màn game, không tính ở đây.

| Module | Đã làm | Tổng launch | Còn thiếu |
|---|---:|---:|---|
| boot | 12 | 28 | age_gate, tos_accept, content_download, maintenance, force_update, server_select, login_hub, register_email, verify_email, forgot_password, reset_password, guest_warning, account_link, account_switch, ban_notice, reconnect |
| onboarding | 7 | 13 | hero_create_look, prologue, tutorial_interact, tutorial_skill, tutorial_equip, tutorial_shop |
| world | 10 | 24 | inn_rest, chest_open, encounter_locked, day_night_info, weather_info, photo_mode, emote_wheel, interaction_prompt, puzzle_switch, puzzle_sequence, signpost_read, lore_book, save_point, hud_customize |
| battle | 19 | 28 | battle_intro, lineup_confirm, item_select, auto_battle, battle_speed, ultimate_cutin, boss_phase, revive_prompt, replay_share |
| heroes | 16 | 18 | hero_skill_upgrade, hero_awaken |
| inventory | 15 | 18 | equipment_refine, set_bonus, bag_expand |
| economy | 15 | 26 | shop_rotating, iap_store, iap_receipt, iap_restore, banner_detail, recruit_animation, recruit_result_multi, monthly_pass, battle_pass, battle_pass_rewards, energy_refill |
| progression | 11 | 28 | quest_main, quest_daily, quest_weekly, quest_accept, quest_complete, chapter_select, chapter_intro, story_recap, cutscene_player, cutscene_skip, ending_select, ending_credits, new_game_plus, title_collection, codex_lore, cg_album, jukebox |
| settings | 15 | 23 | settings_keybind, settings_notifications, parental_controls, spending_limit, contact_support, ticket_list, ticket_detail, feedback_survey |
| social | 1 | 29 | mọi màn trừ `mail_inbox` |
| debug | 12 | 13 | debug_network |
| village | 0 | 10 | cả module |
| gathering | 0 | 6 | cả module |
| companions | 0 | 6 | cả module |
| smithy | 0 | 8 | cả module |
| modes | 0 | 29 | cả module |
| ugc | 0 | 15 | cả module |
| bonds | 0 | 7 | cả module |
| abyss | 0 | 7 | cả module |
| market | 0 | 5 | cả module |
| guild_war | 0 | 6 | cả module |
| pilot | 0 | 10 | cả module |
| **Tổng** | **133** | **357** | **224 (37,3% đã làm)** |

Tính lại sau mỗi lần thêm màn:

```bash
node -e '
const fs=require("fs");
const rows=JSON.parse(fs.readFileSync("docs/screens/screens.json","utf8")).filter(r=>r.surface==="game"&&r.season==="launch");
const reg=new Set([...fs.readFileSync("game/client/src/main/kotlin/com/pxworld/client/screens/DefaultScreens.kt","utf8").matchAll(/GameScreenId\.([A-Z0-9_]+) to/g)].map(m=>m[1]));
const c=id=>id.split(".").slice(1).join("_").toUpperCase();
const t={};for(const r of rows){const m=t[r.module]??={done:0,total:0};m.total++;if(reg.has(c(r.id)))m.done++}
console.table(t)'
```

Ghi số mới vào `docs/PROGRESS.md` (mục "Số liệu") trong cùng PR.
