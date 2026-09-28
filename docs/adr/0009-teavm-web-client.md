# ADR 0009: Game bản web build bằng TeaVM

- Trạng thái: Chấp nhận
- Ngày: 2026-09-25
- Liên quan: [MASTER_PLAN §0 D9](../MASTER_PLAN.md)

## Bối cảnh
Module html (GWT) không build được: Gson, freetype, ghi file local.

## Quyết định
Dùng gdx-teavm; save vào IndexedDB qua adapter; không IAP trên web.

## Hệ quả
Web chạy được bytecode Kotlin. Chưa kiểm chứng với Fleks/KTX: spike 3 ngày; nếu thất bại web chỉ còn demo.

## Cập nhật 2026-09-26 — spike P6 (tạm dừng giữa chừng, kết luận: chưa quyết định)

### Phiên bản
- `com.github.xpenatan.gdx-teavm:backend-teavm:1.2.0` — bản cuối cùng build trên libGDX 1.13.1 (1.2.1 trở đi kéo gdx 1.13.5/1.14.0). Kéo theo TeaVM `0.12.0` (teavm-core/classlib/jso/tooling). TeaVM 0.12.0 đọc được bytecode Java 17 của `:game:client` và Fleks 2.15.
- `com.github.xpenatan:jMultiplatform:0.1.3` được ghim thay cho `0.1.2` mà backend-teavm 1.2.0 khai báo: 0.1.2 chỉ có trên JitPack, không có trên Maven Central; hai bản có cùng ba class, cùng kích thước.

### Cách build
- Module `:game:platform-web` chỉ được include khi chạy với `-Ppxworld.web=true` (opt-in, vì chưa boot được). Lệnh: `./gradlew -Ppxworld.web=true :game:platform-web:buildWeb` → `game/platform-web/build/dist/webapp/` (`index.html`, `teavm/app.js`, `assets/` gồm asset từ `:tools:asset-pipeline:buildAssets` và `content-pack.json` từ `:game:content:compileContent`).
- `WebBuilderKt` (chạy trên JVM, heap 2 GB chỉ cho task này) gọi `TeaBuilder` của gdx-teavm, optimization `ADVANCED`, obfuscate.
- Launcher `WebLauncherKt`: `WebGame` tạo `GameServices` trong `create()` (lúc này asset đã được preload nên `Gdx.files.internal("content-pack.json")` đọc được): save qua `BrowserSaveStore` trên `Gdx.files.local("saves")` (gdx-teavm lưu `local` vào IndexedDB, đúng quyết định gốc), dùng lại `SaveCodec` nên file save web và desktop giống hệt nhau; replay `InMemoryReplays`; flavor `DEV`; cloud URL lấy từ query `?api=` (mặc định tắt); đồng hồ lấy múi giờ từ `Date.getTimezoneOffset()`.
- Hook cho test: `window.pxworld.screen()`, `.stack()`, `.nodes()` (testId + toạ độ canvas) được cài qua `@JSBody`/`@JSFunctor` khi `GameServices.onReady` chạy.
- `tools/test-agent/web/web-smoke.mjs`: tự phục vụ bundle ở cổng 8095 (`--serve` để chỉ phục vụ), mở Edge/Chrome headless qua CDP, chờ `game.boot.legal_notice`, bấm chuột thật qua CDP để đi tiếp privacy → language → main menu, chụp màn hình, fail khi có lỗi JS/HTTP, ghi kích thước bundle và thời gian tới màn đầu.

### Kết quả đo được
- TeaVM biên dịch toàn bộ client (Fleks, KTX scene2d/actors, kotlinx.serialization, Kotlin 2.4 stdlib): 3.400 class, ~128 s (ADVANCED) / ~66 s (SIMPLE).
- Bundle: tổng 17,8 MB; `app.js` 5,9 MB (1,0 MB gzip) ở ADVANCED, 6,1 MB (1,16 MB gzip) ở SIMPLE; asset 11,8 MB.
- Fleks và kotlinx.serialization **không** gây lỗi biên dịch (gdx-teavm đã emulate `Class.isAnonymousClass/getEnclosingMethod` mà `KClass.simpleName` cần).
- **Chưa boot được tới màn đầu**: chưa đo được thời gian tới `boot.legal_notice`.

### Blocker biên dịch đã gặp và cách xử lý (chỉ trong module web, không đổi hành vi desktop/Android)
TeaVM 0.12.0 (và cả 0.12.3, 0.13.0) thiếu các API sau; chúng được emulate trong source set `game/platform-web/src/emulation/java`, có test JVM so với JDK (`EmulationTest`):
| Thiếu | Được gọi từ | Cách xử lý |
|---|---|---|
| `java.util.concurrent.ConcurrentLinkedQueue` | `StageAutomationDriver.<init>`, `drainRenderThreadWork` | `emu.java.util.concurrent.ConcurrentLinkedQueue` trên `ArrayDeque` (JS đơn luồng) |
| `java.security.MessageDigest` | `SaveCodec.sha256` (`:game:infrastructure`) | `emu.java.security.MessageDigest` SHA-256 thuần Java, khớp JDK từng byte |
| `java.text.Normalizer`, `Normalizer$Form.NFD` | `SlotLoading.slotNameFor` | `emu.java.text.Normalizer` gọi `String.prototype.normalize` |
| `Long.toUnsignedString(J)`, `Integer.toUnsignedString(I)` | `kotlinx.serialization.json.internal.ComposerForUnsignedNumbers.print`, `AbstractJsonTreeEncoder.inlineUnsignedNumberEncoder` | `@Emulate(updateCode = true)` bổ sung hai method |
| `Runtime.maxMemory()` | `DebugPerformanceScreen.refresh` | `@Emulate` trả `Long.MAX_VALUE` (đúng đặc tả JDK khi không có giới hạn) |

Thay đổi duy nhất trong `:game:client`: `StageAutomationDriver.treeNow()` đổi thành public `visibleNodes()` và thêm `screenCenter(node)` để hook web đọc cây UI trên chính luồng render; `tree()` và `invariants()` giữ nguyên hành vi.

### Blocker runtime hiện tại (chưa sửa)
`java.lang.RuntimeException: Class cannot be created (missing no-arg constructor): com.badlogic.gdx.graphics.g2d.GlyphLayout$GlyphRun` — `Pools.get(GlyphRun.class)` → `ReflectionPool` → `ClassReflectionEmu.getConstructor` → `Class.getConstructors()` trả mảng rỗng vì TeaVM đã loại constructor không đối số của `GlyphRun` (không ai gọi trực tiếp). Xảy ra ở cả ADVANCED lẫn SIMPLE, nên không phải do obfuscate. `TeaBuilder` chỉ preserve đúng class `GlyphLayout` (không gồm class lồng), và package mặc định (`com.badlogic.gdx.scenes.scene2d`, `com.badlogic.gdx.math`) chỉ được preserve khi class nằm trong thư mục, không phải trong jar. Cách sửa rẻ nhất cần thử tiếp: `TeaReflectionSupplier.addReflectionClass(...)` cho `GlyphLayout$GlyphRun` và các class libGDX được tạo qua `Pools.obtain(Class)` (`InputEvent`, `ChangeListener$ChangeEvent`, `FocusListener$FocusEvent`, các `scene2d.actions.*` mà client dùng), hoặc thêm chúng vào `TeaBuildConfiguration.classesToPreserve`.

### Tất định
Chưa làm. Việc tiếp theo: build riêng một entry TeaVM chạy `BattleEngine.runAuto(BattleFixtures.balancedBattle(1))`, in `BattleEventLog.render(...)` và so với `game/domain/src/test/resources/golden/balanced_seed_1.txt` bằng Node.

### Kết luận tạm thời
**Chưa quyết định (nghiêng về go)**: mọi blocker biên dịch đều sửa được bằng emulation nhỏ trong module web, không đụng Fleks/KTX/serialization; blocker runtime đầu tiên có hướng sửa rõ nhưng chưa được kiểm chứng. Module giữ opt-in, chưa thêm job CI `web-client`.

### Việc còn lại cho P6
1. Preserve các class tạo qua `ReflectionPool` rồi chạy lại `node tools/test-agent/web/web-smoke.mjs` cho tới khi thấy `game.boot.legal_notice` và main menu; ghi thời gian tới màn đầu.
2. Kiểm tra regex Unicode `\p{M}` trong `slotNameFor` chạy đúng trên TeaVM (tạo nhân vật tên có dấu) và đi tiếp tới `world.world_explore`.
3. Golden battle trên TeaVM như mục Tất định.
4. Khi boot ổn định: include `:game:platform-web` mặc định trong `settings.gradle`, thêm `game/platform-web` vào `ArchitectureTest.MODULES` (package `com.pxworld.web`, ngân sách dòng launcher), thêm job CI `web-client` (build bundle + `web-smoke.mjs`).
