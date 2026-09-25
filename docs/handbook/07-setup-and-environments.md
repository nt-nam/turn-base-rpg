# 07 · Cài đặt, chạy, môi trường, CI

> Đối chiếu với nhánh `rewrite` tại commit `af8fd71`. Mọi lệnh viết cho **Git Bash** (Windows) hoặc bash (Linux/macOS) và chạy từ **thư mục gốc repo**, trừ khi ghi khác. PowerShell/cmd: thay `./gradlew` bằng `.\gradlew.bat`, đặt biến bằng `$env:TEN="giatri"`.
> Test và bộ hồi quy: [06-testing.md](06-testing.md). Kế hoạch: [MASTER_PLAN §8](../MASTER_PLAN.md#8-nền-tảng--môi-trường). Tiến độ: [PROGRESS.md](../PROGRESS.md).

## 0. Chạy được trong 10 phút

```bash
git clone https://github.com/nt-nam/turn-base-rpg.git LVpxW && cd LVpxW
git checkout rewrite
(cd web && npm ci)                                                    # cho Console và console agent
./gradlew --settings-file settings-test.gradle :game:domain:test      # lần đầu: tải Gradle 8.12.1 + dependency
./gradlew --settings-file settings-test.gradle :game:platform-desktop:run   # mở game desktop, flavor DEV
```

Kết quả mong đợi: `BUILD SUCCESSFUL`, rồi một cửa sổ `PXWORLD · dev · local` 1280×720 hiện màn pháp lý (lần đầu) hoặc menu chính.

---

## 1. Yêu cầu máy

| Công cụ | Phiên bản | Lấy từ đâu trong repo | Cần cho |
|---|---|---|---|
| JDK | **17** (CI: Temurin 17) | `build-logic/.../pxworld.kotlin-module.gradle.kts`: `jvmToolchain(17)`; [ci.yml](../../.github/workflows/ci.yml) `java-version: 17` | Mọi lệnh Gradle. Repo **không** bật tự tải toolchain, nên JDK 17 phải có sẵn trên máy. Đặt `JAVA_HOME` trỏ vào JDK 17 cho đơn giản |
| Gradle | **8.12.1** qua wrapper | [gradle-wrapper.properties](../../gradle/wrapper/gradle-wrapper.properties) | Không cài Gradle riêng, luôn dùng `./gradlew` |
| Kotlin | **2.4.20** | [libs.versions.toml](../../gradle/libs.versions.toml) | Tự tải qua plugin. `allWarningsAsErrors = true`: cảnh báo biên dịch làm build fail |
| Android Gradle Plugin | **8.10.1** | [build.gradle](../../build.gradle) gốc | Build Android |
| Android SDK | `compileSdk 35`, `minSdk 24`, `targetSdk 35` | [game/platform-android/build.gradle.kts](../../game/platform-android/build.gradle.kts) | Build APK. Cần: *SDK Platform 35*, *Build-Tools*, *Platform-Tools* (`adb`), *Emulator* + một system image x86_64 (máy hiện tại dùng API 36). Module legacy `android/` khai báo `compileSdk 34`, chỉ cần Platform 34 nếu build module đó |
| Node.js | **24** (CI) | [ci.yml](../../.github/workflows/ci.yml) `node-version: 24` | Screen catalog, test agent, Console. Tối thiểu 22: test agent dùng `WebSocket` và `fetch` toàn cục |
| npm | đi kèm Node | [web/package-lock.json](../../web/package-lock.json) | `npm ci` trong `web/` |
| Trình duyệt Chromium | Edge hoặc Chrome, bản nào cũng được | [console-agent.mjs](../../tools/test-agent/console/console-agent.mjs) tìm theo thứ tự: `--browser`, `PXWORLD_BROWSER`, Edge, Chrome (Windows), `/usr/bin/google-chrome`, `chromium`, `chromium-browser` | Console agent |
| Git Bash | đi kèm Git for Windows | Các script `*.sh` là bash | Windows |
| xvfb | `xvfb libgl1 libglu1-mesa` | [ci.yml](../../.github/workflows/ci.yml) job `desktop-agent` | Chạy game không màn hình trên Linux |

Phiên bản thư viện chính (để đọc tài liệu đúng bản): libGDX 1.13.1, KTX 1.13.1-rc1, Fleks 2.15, Ktor 3.6.0, kotlinx.serialization 1.11.0, H2 2.3.232, PostgreSQL JDBC 42.7.13, HikariCP 6.3.0, JUnit 5.10.2, React 19, Vite 7, TypeScript 5.9.

RAM: Gradle chạy với `-Xmx1G` ([gradle.properties](../../gradle.properties)); game + server + Gradle cùng lúc cần khoảng 4 GB trống. Gradle daemon **tắt** (`org.gradle.daemon=false`), nên mỗi lệnh khởi động JVM lại từ đầu.

---

## 2. Dựng máy lần đầu

### 2.1 Clone và nhánh
```bash
git clone https://github.com/nt-nam/turn-base-rpg.git LVpxW
cd LVpxW
git checkout rewrite
```
Trên Linux/macOS, script trong repo được lưu **không có quyền thực thi** (mode `100644`). Chạy một lần:
```bash
chmod +x gradlew tools/test-agent/run-desktop.sh tools/test-agent/console/run-console.sh
```
`.gitattributes` ép `eol=lf` cho mọi file, trừ `*.bat`, nên script bash chạy được trên Windows.

### 2.2 `local.properties` (chỉ khi build Android)
Tạo file ở gốc repo. File này nằm trong `.gitignore`, **không bao giờ commit**.
```properties
sdk.dir=E:/Android/Sdk
```
Windows dùng `/` hoặc `\\`. Linux/macOS dùng ví dụ `sdk.dir=/home/<user>/Android/Sdk`. Thay cho file này, có thể đặt biến `ANDROID_HOME`.

### 2.3 Web
```bash
cd web && npm ci && cd ..
```
Kết quả: thư mục `web/node_modules`, có `web/node_modules/vite/bin/vite.js` (script console agent gọi thẳng file này).

### 2.4 Build đầu tiên = cổng JVM của CI
```bash
./gradlew --settings-file settings-test.gradle :game:domain:test :game:application:test :game:screens:test :game:content:test :game:infrastructure:test :game:content:compileContent :game:client:test :server:app:test :tools:architecture:test :core:test :lwjgl3:compileJava
node tools/screen-catalog/catalog.mjs --check
```
Kết quả mong đợi:
- Gradle in bảng cân bằng của `compileContent` (xem §4.2) rồi `BUILD SUCCESSFUL`.
- Catalog in `total=2094 unique=1321 matrix=773`, thống kê theo bề mặt và mùa, exit code 0.

Lần đầu mất 5–15 phút vì phải tải Gradle, dependency và biên dịch `build-logic`.

### 2.5 IDE
IntelliJ IDEA: *Open* thư mục gốc, chọn Gradle JDK = 17. IDE nạp `settings.gradle` (đầy đủ, gồm Android), nên cần `local.properties` hoặc `ANDROID_HOME`. Nếu không làm Android, vẫn nên chạy lệnh bằng terminal với `--settings-file settings-test.gradle`.

---

## 3. Hai file settings Gradle

| File | Gồm module | Cần Android SDK | Dùng cho |
|---|---|---|---|
| [settings.gradle](../../settings.gradle) (mặc định) | legacy `lwjgl3 core android ios html` + mọi module mới + `:game:platform-android` | **Có**. Thiếu SDK sẽ báo `SDK location not found` ngay khi cấu hình | `:game:platform-android:*`, job `android` của CI, `run-console.sh` (gọi `./gradlew :server:app:installDist` không kèm `--settings-file`) |
| [settings-test.gradle](../../settings-test.gradle) | `core lwjgl3` + `:game:{domain,application,screens,content,infrastructure,client,automation,platform-desktop}`, `:tools:{asset-pipeline,architecture}`, `:server:app` | Không | Test JVM, game desktop, server, `run-desktop.sh` |

Quy tắc: mọi lệnh **không** dính Android đều thêm `--settings-file settings-test.gradle`. Máy không có Android SDK vẫn làm được mọi việc, trừ build APK và `run-console.sh`.

---

## 4. Lệnh theo nhóm

### 4.1 Test JVM
| Lệnh | Việc làm | Kết quả mong đợi |
|---|---|---|
| Dòng CI ở §2.4 | Toàn bộ test JVM + biên dịch content + biên dịch desktop legacy | `BUILD SUCCESSFUL`. Khi fail, báo cáo HTML ở `<module>/build/reports/tests/test/index.html` |
| `./gradlew --settings-file settings-test.gradle :game:domain:test` | Một module | Như trên |
| `./gradlew --settings-file settings-test.gradle :game:application:test --tests 'com.pxworld.application.GameRulesTest'` | Một lớp test | Như trên |
| `./gradlew --settings-file settings-test.gradle :game:domain:test --tests '*BattleEngineTest.faster*'` | Một phương thức (khớp theo tên) | Như trên |
| `./gradlew --settings-file settings-test.gradle :game:domain:test -DupdateGolden=true` | Ghi lại file golden trận đấu | Chỉ chạy theo quy trình ở [06 §4](06-testing.md#4-golden-battle-test) |

Gradle bỏ qua test đã chạy nếu đầu vào không đổi (`UP-TO-DATE`). Muốn ép chạy lại: thêm `--rerun-tasks`.

### 4.2 Nội dung (content)
| Lệnh | Việc làm | Kết quả mong đợi |
|---|---|---|
| `./gradlew --settings-file settings-test.gradle :game:content:compileContent` | Đọc `content/`, kiểm tra (tham chiếu, asset legacy trong `assets/`), mô phỏng từng encounter, ghi content pack | In `validation: 0 errors, N warnings, M records`, bảng `encounter balance at recommended level, … seeds each` (cờ `<-- too hard` nếu tỉ lệ thắng thấp), `content pack: …/content-pack-<hash12>.json`. Có lỗi thì in từng lỗi và exit 1 |
| `./gradlew --settings-file settings-test.gradle :game:content:exportSchemas` | Sinh lại `content/schemas/*.schema.json` từ kiểu record Kotlin | In `wrote 17 schemas to …/content/schemas`. Commit các file thay đổi. `ContentSchemaTest` fail nếu schema cũ |

Đầu ra của `compileContent`: `game/content/build/content-pack/` gồm `content-pack.json`, `content-pack-<hash12>.json`, `manifest.json`. Game desktop và APK tự gọi task này khi build.

### 4.3 Asset
| Lệnh | Việc làm | Kết quả mong đợi |
|---|---|---|
| `./gradlew --settings-file settings-test.gradle :tools:asset-pipeline:buildAssets` | Đóng gói asset chạy từ `assets/` (legacy), `art/` và các tham chiếu trong `content/`: cắt tileset theo map, nén nền trận, sinh font bitmap từ font OFL | In `asset pipeline: N files, X MB` và 8 file lớn nhất; dòng `PROBLEM …` làm exit 1. Đầu ra ở `tools/asset-pipeline/build/assets/` (có `asset-manifest.json`) |

Game desktop và APK tự phụ thuộc task này.

### 4.4 Screen catalog
| Lệnh | Việc làm | Kết quả mong đợi |
|---|---|---|
| `node tools/screen-catalog/catalog.mjs` | Sinh `docs/screens/{screens.csv,screens.json,SCREEN_CATALOG.md}`, `game/screens/.../GameScreenId.kt`, `web/packages/screen-catalog/src/screenIds.ts` | In thống kê. Commit mọi file sinh ra |
| `node tools/screen-catalog/catalog.mjs --check` | Chỉ so sánh, không ghi (CI) | Exit 0. Khi lệch: `Screen catalog outputs are stale. Run: node tools/screen-catalog/catalog.mjs`, liệt kê file, exit 1 |

### 4.5 Game desktop
| Cách | Lệnh | Ghi chú |
|---|---|---|
| Qua Gradle | `./gradlew --settings-file settings-test.gradle :game:platform-desktop:run` | Tự build content pack + asset. Thư mục làm việc = `game/platform-desktop/`. Gradle đứng ở `EXECUTING` tới khi đóng cửa sổ |
| Launcher cài sẵn | `./gradlew --settings-file settings-test.gradle :game:platform-desktop:installDist` rồi chạy `game/platform-desktop/build/install/platform-desktop/bin/platform-desktop` (Linux/macOS) hoặc `…/bin/platform-desktop.bat` (Windows, gọi được từ Git Bash) | Cách `run-desktop.sh` dùng. Chạy từ gốc repo. Nên dùng khi cần server và game cùng lúc (xem §4.8) |

Ví dụ đặt biến (bash):
```bash
PXWORLD_FLAVOR=QA PXWORLD_API_URL=off PXWORLD_SAVE_DIR="$PWD/tmp-saves" ./gradlew --settings-file settings-test.gradle :game:platform-desktop:run
```
Phím: `W A S D` hoặc mũi tên để đi, `Esc` để quay lại, `F1` mở menu debug (flavor DEV/QA).

Khác biệt do **thư mục làm việc**:
- Nhập save legacy tự động đọc `data/select/*`, `lwjgl3/data/select/*`, `../data/select/*` **tính từ thư mục làm việc** ([DesktopLauncher.kt](../../game/platform-desktop/src/main/kotlin/com/pxworld/desktop/DesktopLauncher.kt)). Chạy launcher từ gốc repo trên máy có thư mục `data/` cũ (không nằm trong git) sẽ nhập các save đó. Chạy bằng `:run` thì không nhập gì.
- *Báo lỗi* và *Tải dữ liệu của tôi* ghi `bug-reports/` và `exports/` vào thư mục làm việc. Hai thư mục này **chưa có trong `.gitignore`**, đừng commit chúng.

### 4.6 Server
| Cách | Lệnh | Ghi chú |
|---|---|---|
| Qua Gradle | `./gradlew --settings-file settings-test.gradle :server:app:run` | Thư mục làm việc = gốc repo, nên `content/`, `assets/`, `build/pxworld-dev*.db` tự đúng |
| Launcher | `./gradlew --settings-file settings-test.gradle :server:app:installDist` rồi `server/app/build/install/app/bin/app` (hoặc `app.bat`) | Phải chạy từ gốc repo, hoặc đặt `PXWORLD_CONTENT_DIR` và `PXWORLD_LEGACY_ASSETS_DIR` |

Kiểm tra:
```bash
curl -s http://localhost:8080/health
# {"status":"ok","env":"dev","contentVersion":"<12 ký tự hex>"}
curl -s http://localhost:8080/metrics      # pxworld_accounts_total …, dạng Prometheus text
```
Khi khởi động, server: chạy migration SQL, tạo admin dev nếu chưa có staff nào (§9), đóng gói `content/` hiện tại thành một release và đẩy lên kênh bằng `PXWORLD_ENV` nếu kênh đó còn trống.

### 4.7 Web Console
| Lệnh (từ `web/`) | Việc làm | Kết quả |
|---|---|---|
| `npm run console:dev` | Vite dev server `http://localhost:5173`. Proxy `/api/*` → `PXWORLD_API` (mặc định `http://localhost:8080`), bỏ tiền tố `/api` ([vite.config.ts](../../web/apps/console/vite.config.ts)) | Mở trình duyệt, đăng nhập bằng tài khoản staff (§9) |
| `npm run console:build` | `tsc --noEmit && vite build` → `web/apps/console/dist/` (đã ignore) | Lỗi kiểu TypeScript làm build fail |
| `npm run typecheck` | Kiểm tra kiểu mọi workspace | Exit 0 |
| `PXWORLD_API=http://localhost:8080 node node_modules/vite/bin/vite.js preview apps/console --port 4173 --strictPort` | Phục vụ bản build ở `http://localhost:4173`, proxy giống dev | Cách `run-console.sh` dùng |

Console gọi API qua `/api` cùng origin, nên không cần CORS. Chỉ khi build với `VITE_PXWORLD_API=<url tuyệt đối>` mới cần thêm host vào `PXWORLD_CORS_HOSTS`.

### 4.8 Chạy đủ bộ trên một máy (server + Console + game có cloud)
```bash
./gradlew --settings-file settings-test.gradle :server:app:installDist :game:platform-desktop:installDist
cp -r content /tmp/pxw-content                    # để Studio không ghi đè content/ của repo
# terminal 1: server cổng 8080
PXWORLD_CONTENT_DIR=/tmp/pxw-content server/app/build/install/app/bin/app
# terminal 2: Console http://localhost:5173
cd web && npm run console:dev
# terminal 3: game DEV, cloud mặc định http://localhost:8080
PXWORLD_SAVE_DIR="$HOME/pxw-local/saves" game/platform-desktop/build/install/platform-desktop/bin/platform-desktop.bat
```
Dùng launcher `installDist` cho tiến trình chạy lâu: hai lệnh `./gradlew … run` song song trong cùng repo có thể phải chờ khoá của nhau.

### 4.9 Android
Build APK debug (dùng `settings.gradle` đầy đủ, cần §2.2):
```bash
./gradlew :game:platform-android:assembleDebug
# → game/platform-android/build/outputs/apk/debug/platform-android-debug.apk
```
`preBuild` tự chạy `copyAndroidNatives`, `bundleContentPack` (content pack vào assets) và `:tools:asset-pipeline:buildAssets`.

Emulator (công thức duy nhất đã khởi động được trên máy Windows hiện tại, xem [PROGRESS](../PROGRESS.md#điểm-tạm-dừng-2026-09-25)):
```bash
export PATH="$PATH:/e/Android/Sdk/platform-tools:/e/Android/Sdk/emulator"
emulator -avd Medium_Phone_API_36.0 -no-window -gpu swiftshader_indirect -feature -Vulkan -no-snapshot -no-audio -no-boot-anim &
adb wait-for-device
until [ "$(adb shell getprop sys.boot_completed | tr -d '\r')" = "1" ]; do sleep 2; done
adb install -r game/platform-android/build/outputs/apk/debug/platform-android-debug.apk
adb shell am start -n com.game.pxworld/com.pxworld.android.AndroidLauncher
adb exec-out screencap -p > android.png          # -no-window nên xem màn hình qua ảnh chụp
```
- AVD `Medium_Phone_API_36.0` tạo trong Android Studio › Device Manager (tên mặc định của "Medium Phone API 36"). AVD khác cũng dùng được, chỉ cần đổi tên.
- Lần mở đầu tiên Android hiện hộp thoại hệ thống **"Viewing full screen"** che game. Bấm *Got it*, hoặc chạy `adb shell settings put secure immersive_mode_confirmations confirmed` trước khi mở app (lệnh này nằm trong kế hoạch [WP-A1](05-work-packages.md#wp-a1--android-chạy-thật-và-test-agent-trên-emulator), chưa kiểm chứng trên API 36).
- Nhật ký: `adb logcat -d > logcat.txt`. Chỉ lỗi crash: `adb logcat -d AndroidRuntime:E '*:S'`.
- Xoá dữ liệu app: `adb shell pm clear com.game.pxworld`. Xem save: `adb shell run-as com.game.pxworld ls files/saves`.
- Tắt emulator: `adb emu kill`.
- Server cho emulator: build debug trỏ `http://10.0.2.2:8080` (địa chỉ máy host nhìn từ emulator). Server Ktor nghe trên mọi interface nên không cần cấu hình thêm. Đổi URL: `./gradlew :game:platform-android:assembleDebug -Ppxworld.apiUrl=http://10.0.2.2:18080`.
- Máy thật qua USB: `adb reverse tcp:8080 tcp:8080` rồi build với `-Ppxworld.apiUrl=http://localhost:8080`. Bản debug chỉ cho phép HTTP không mã hoá tới `10.0.2.2` và `localhost` ([network_security_config.xml](../../game/platform-android/src/debug/res/xml/network_security_config.xml)), nên IP LAN kiểu `http://192.168.x.x` sẽ bị chặn.
- **Chưa có:** automation server trên Android, `tools/test-agent/run-android.sh`, job CI chạy emulator. Đang làm ở WP-A1.

### 4.10 Test agent (chi tiết ở [06 §5–6](06-testing.md#5-test-agent-desktop))
```bash
tools/test-agent/run-desktop.sh "$PWD/agent-reports/scenario"                  # core-loop
SCENARIO=chapter1 tools/test-agent/run-desktop.sh "$PWD/agent-reports/chapter1"
SCENARIO=cloud tools/test-agent/run-desktop.sh "$PWD/agent-reports/cloud"      # tự bật server :18080
MODE=explore tools/test-agent/run-desktop.sh "$PWD/agent-reports/explore"
tools/test-agent/console/run-console.sh --out="$PWD/agent-reports/console"     # server :18080 + preview :4173
```
Trên Linux không màn hình, thêm `xvfb-run -a -s "-screen 0 1280x720x24"` trước mỗi lệnh `run-desktop.sh`. Mỗi lệnh kết thúc bằng dòng `PASSED …` hoặc `FAILED …` và `report: <thư mục>`.

---

## 5. Biến môi trường

### 5.1 Game desktop ([DesktopLauncher.kt](../../game/platform-desktop/src/main/kotlin/com/pxworld/desktop/DesktopLauncher.kt))
| Biến | Mặc định | Ý nghĩa |
|---|---|---|
| `PXWORLD_FLAVOR` | `DEV` | `DEV` / `QA` / `PILOT` / `RELEASE`, không phân biệt hoa thường. Giá trị sai làm game dừng ngay khi khởi động |
| `PXWORLD_ENV` | `local` | Chỉ là nhãn: tên thư mục save mặc định và tiêu đề cửa sổ |
| `PXWORLD_SAVE_DIR` | `~/.pxworld/<PXWORLD_ENV>/saves` | Thư mục save |
| `PXWORLD_AUTOMATION_PORT` | `47017` | Cổng automation WebSocket, chỉ bind `127.0.0.1`. Chỉ mở khi flavor có automation (DEV/QA/PILOT). Giá trị `0` hoặc âm sẽ tắt |
| `PXWORLD_WIDTH`, `PXWORLD_HEIGHT` | `1280`, `720` | Kích thước cửa sổ |
| `PXWORLD_API_URL` | DEV: `http://localhost:8080`. Flavor khác: không có cloud | URL server cloud. `off` tắt cloud ở mọi flavor. Không có cloud thì màn Tài khoản báo "Bản dựng này chưa kết nối máy chủ." và không gửi telemetry |

### 5.2 Server ([ServerConfig.kt](../../server/app/src/main/kotlin/com/pxworld/server/ServerConfig.kt))
| Biến | Mặc định khi `PXWORLD_ENV=dev` | Mặc định khi env khác | Ý nghĩa |
|---|---|---|---|
| `PXWORLD_ENV` | `dev` | — | Tên môi trường. Là kênh content được tự đẩy lúc khởi động, và tạo issuer JWT `pxworld-<env>` |
| `PXWORLD_JWT_SECRET` | `pxworld-dev-secret-change-me` | **bắt buộc** (thiếu thì dừng với `PXWORLD_JWT_SECRET must be set outside dev`) | Khoá ký JWT. Token sống 12 giờ |
| `PXWORLD_PORT` | `8080` | `8080` | Cổng HTTP |
| `PXWORLD_DB_URL` | `jdbc:h2:file:./build/pxworld-dev;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE` | như cột trái | JDBC URL. Postgres: `jdbc:postgresql://localhost:5432/pxworld` |
| `PXWORLD_DB_USER`, `PXWORLD_DB_PASSWORD` | trống | trống | Tài khoản DB |
| `PXWORLD_CONTENT_DIR` | `content` (tính từ thư mục làm việc) | như cột trái | Thư mục content mà Studio đọc/ghi và server đóng gói |
| `PXWORLD_LEGACY_ASSETS_DIR` | `assets` | `assets` | Dùng để kiểm tra đường dẫn asset khi validate content |
| `PXWORLD_CONTENT_WRITABLE` | `true` | `false` | `false`: Studio vẫn kiểm tra được nhưng *Lưu* trả 409 `content is read-only in this environment` |
| `PXWORLD_ADMIN_EMAIL` | `admin@pxworld.local` | không có | Email admin tạo lúc khởi động |
| `PXWORLD_ADMIN_PASSWORD` | `admin-dev-password` | không có | Mật khẩu admin đó. Ngoài dev phải tự đặt cả hai biến thì mới có admin |
| `PXWORLD_CORS_HOSTS` | `localhost:5173` | `localhost:5173` | Danh sách host cách nhau dấu phẩy, cho phép `http` và `https` |

### 5.3 Android ([build.gradle.kts](../../game/platform-android/build.gradle.kts))
| BuildConfig | Debug | Release | Đổi bằng |
|---|---|---|---|
| `PXWORLD_API_URL` | `http://10.0.2.2:8080` | `""` (chuỗi rỗng = không có cloud) | `-Ppxworld.apiUrl=…` (debug), `-Ppxworld.releaseApiUrl=…` (release). Đặt được trong `~/.gradle/gradle.properties` |
| `PXWORLD_FLAVOR` | `QA` | `PILOT` | Sửa build file |
| `VERSION_NAME` | `2.0.0-dev` | `2.0.0-dev` | Client gửi `android-2.0.0-dev` làm `clientVersion` trong telemetry |

`applicationId = com.game.pxworld`, activity `com.pxworld.android.AndroidLauncher`, khoá xoay ngang (`sensorLandscape`). Release chưa có cấu hình ký.

### 5.4 Script test agent
| Biến | Mặc định | Dùng ở | Ý nghĩa |
|---|---|---|---|
| `MODE` | `scenario` | `run-desktop.sh` | `scenario` hoặc `explore`, chuyển thành `--mode` của `run.mjs` |
| `SCENARIO` | (`core-loop`) | `run-desktop.sh` | Tên file trong `tools/test-agent/scenarios/`. `cloud` tự bật `WITH_SERVER` |
| `WITH_SERVER` | trống | `run-desktop.sh` | Khác rỗng: build và chạy server H2 in-memory ở `PXWORLD_API_PORT` |
| `PXWORLD_API_PORT` | `18080` | `run-desktop.sh`, `run-console.sh` | Cổng server của agent |
| `PXWORLD_CONSOLE_PORT` | `4173` | `run-console.sh` | Cổng `vite preview` (`--strictPort`: cổng bận thì fail) |
| `PXWORLD_FLAVOR` | `DEV` | `run-desktop.sh` | Truyền tiếp cho game |
| `PXWORLD_TEST_API` | `http://localhost:18080` | kịch bản `cloud` | URL API mà kịch bản gọi trực tiếp. `run-desktop.sh` tự đặt |
| `PXWORLD_ADMIN_EMAIL`, `PXWORLD_ADMIN_PASSWORD` | giá trị dev | kịch bản `cloud` | Đăng nhập admin để tặng thư |
| `PXWORLD_TOKEN` | — | `run.mjs --upload` | Bearer token khi không truyền `--token` |
| `PXWORLD_BROWSER` | — | `console-agent.mjs` | Đường dẫn Edge/Chrome |
| `CI` | — | `run-console.sh` | Có giá trị thì luôn `npm ci`. Không có thì chỉ `npm ci` khi thiếu `web/node_modules` |
| `TMPDIR` | `/tmp` | `run-console.sh` | Nơi ghi `pxworld-console-server.log`, `pxworld-console-web.log` |

`run-desktop.sh` còn tự đặt cho game: `PXWORLD_SAVE_DIR=<out>/saves`, `PXWORLD_ENV=qa`, `PXWORLD_API_URL=off`, hoặc `http://localhost:<PXWORLD_API_PORT>` khi có server. Server của agent chạy với `PXWORLD_DB_URL=jdbc:h2:mem:…` và `PXWORLD_CONTENT_WRITABLE=false`. Riêng console agent dùng một **bản sao tạm** của `content/` với `PXWORLD_CONTENT_WRITABLE=true`.

### 5.5 Web
| Biến | Khi nào | Ý nghĩa |
|---|---|---|
| `PXWORLD_API` | lúc chạy `vite` dev/preview | Đích proxy của `/api`, mặc định `http://localhost:8080` |
| `VITE_PXWORLD_API` | lúc build | Gốc API trong bundle, mặc định `/api` |

---

## 6. Flavor và môi trường

### 6.1 Flavor client ([GameServices.kt](../../game/client/src/main/kotlin/com/pxworld/client/core/GameServices.kt))
| Flavor | Debug tools (nút *Công cụ debug*, `F1`, cheat) | Automation (cổng 47017) | Cloud mặc định (desktop) | Dùng ở đâu hôm nay |
|---|---|---|---|---|
| `DEV` | có | có | `http://localhost:8080` | Mặc định desktop, `run-desktop.sh` |
| `QA` | có | có | không | APK debug |
| `PILOT` | không | có | không | APK release |
| `RELEASE` | không | không | không | Chưa có build nào dùng |

MASTER_PLAN §13.2 muốn flavor `qa` "không có cheat phá kinh tế". Hiện `QA` vẫn có cheat, giống `DEV`.

### 6.2 Môi trường (MASTER_PLAN §8.2 so với hôm nay)
| Env | Mục đích theo kế hoạch | Có hôm nay? |
|---|---|---|
| `local` | Dev trên máy | **Có**, và là môi trường duy nhất. Server H2 file hoặc in-memory, không có docker-compose |
| `dev` | Tích hợp mỗi lần merge | **Không** có server triển khai. `dev` chỉ là giá trị `PXWORLD_ENV` mặc định và là một kênh content trong DB |
| `qa` | QA thủ công + test agent | **Không** có server. `PXWORLD_ENV=qa` chỉ là nhãn save trong `run-desktop.sh`; `qa` là một kênh content |
| `staging` | Bản sao prod | **Không**. Chỉ có kênh content `staging` |
| `pilot` | Chương trình pilot | **Không**. Không có cả kênh content `pilot` (server chỉ nhận `dev, qa, staging, prod`) |
| `prod` | Người chơi thật | **Không**. Chỉ có kênh content `prod` |

Không có Dockerfile, Helm, Terraform hay pipeline deploy. Client hôm nay dùng content pack **đóng gói sẵn** trong bản build và không đọc `/content/manifest`, nên đẩy kênh trong Console chưa đổi được nội dung game.

---

## 7. Dữ liệu nằm ở đâu

| Dữ liệu | Vị trí | Ghi chú |
|---|---|---|
| Save desktop | `~/.pxworld/<PXWORLD_ENV>/saves/<slot>.save.json` | Slot = tên người chơi viết thường, bỏ dấu, `_` thay khoảng trắng (`Agent Tester` → `agent_tester`). Trùng tên thì thêm `_2`, `_3`… |
| Bản sao lưu | `<slot>.1.save.json.bak` … `<slot>.3.save.json.bak` | Xoay vòng 3 bản. Save chính hỏng thì tự đọc bản sao |
| Replay trận | `<saves>/replays/replay-<seed>.json` | Giữ 30 bản mới nhất. Cùng định dạng với `POST /battles/validate` |
| Dấu đã nhập legacy | `<saves>/.legacy-imports/` | Mỗi thư mục legacy chỉ nhập một lần |
| Nguồn save legacy | `data/select/<x>/info.json`, `lwjgl3/data/select/…`, `../data/select/…` tính từ thư mục làm việc | Fixture mẫu: `tools/test-agent/fixtures/legacy_saves/{a_desktop,a_repo_root,y_desktop}` |
| Tuỳ chọn máy (đã đồng ý pháp lý, trả lời quyền riêng tư, ngôn ngữ, đồng ý thống kê) | `~/.prefs/pxworld` | **Dùng chung** cho mọi `PXWORLD_ENV`, mọi flavor và cả test agent |
| Phiên đăng nhập cloud | `~/.prefs/pxworld.cloud.<url chuẩn hoá>`, ví dụ `pxworld.cloud.http_localhost_18080` | Mỗi URL server một file |
| Báo lỗi / xuất dữ liệu | `<thư mục làm việc>/bug-reports/report-<ms>.txt`, `<thư mục làm việc>/exports/pxworld-export-<slot>.json` | Chưa có trong `.gitignore` |
| Save Android | `/data/data/com.game.pxworld/files/saves/` | Tuỳ chọn: `shared_prefs/pxworld.xml` |
| DB server dev | `<thư mục làm việc>/build/pxworld-dev.mv.db` | H2 file, còn nguyên giữa các lần chạy. Test và agent dùng `jdbc:h2:mem:` |
| Content nguồn | `content/` (17 loại + `localization/`, `schemas/`) | Studio ghi vào `PXWORLD_CONTENT_DIR` |
| Content pack | `game/content/build/content-pack/` → chép vào `game/platform-desktop/build/generated/content/` và assets Android | |
| Asset sinh ra | `tools/asset-pipeline/build/assets/` | Không commit |
| Báo cáo agent | `tools/test-agent/reports/<thời điểm>/` (đã ignore) hoặc thư mục `--out` | Console agent: `tools/test-agent/reports/console-<thời điểm>/` |
| Báo cáo test Gradle | `<module>/build/reports/tests/test/index.html` | |

Đặt lại trạng thái:
```bash
rm -f ~/.prefs/pxworld                   # thấy lại màn pháp lý / quyền riêng tư / ngôn ngữ
rm -f ~/.prefs/pxworld.cloud.*           # đăng xuất cloud trên mọi server
rm -rf ~/.pxworld/local/saves            # xoá save của env local
rm -f build/pxworld-dev.*.db             # xoá DB server dev (tắt server trước)
adb shell pm clear com.game.pxworld      # Android
```

---

## 8. CI ([.github/workflows/ci.yml](../../.github/workflows/ci.yml))

Kích hoạt: mọi `pull_request` và `push` vào **`main`**. Push lên `rewrite` hay nhánh WP **không** chạy CI nếu chưa mở PR. Chưa có workflow nightly hay release. Chưa dùng secret nào.

| Job | Phụ thuộc | Chạy gì | Artifact | Chạy lại trên máy |
|---|---|---|---|---|
| `screen-catalog` | — | `node tools/screen-catalog/catalog.mjs --check` | — | Lệnh y hệt |
| `jvm` | — | Dòng Gradle ở §2.4 (JDK Temurin 17) | `test-reports` **chỉ khi fail**: `game/*/build/reports/tests`, `server/*/build/reports/tests`, `core/build/reports/tests` | Lệnh y hệt |
| `desktop-agent` | `jvm` | Cài `xvfb libgl1 libglu1-mesa`; dưới `xvfb-run` chạy lần lượt: `core-loop`, `SCENARIO=chapter1`, `SCENARIO=cloud` (có server thật), `MODE=explore` | `agent-reports` (luôn luôn): `report.json`, ảnh `.png`, `game.log`, `server.log` (khi có server), `saves/` | §4.10. Linux headless thì thêm `xvfb-run` |
| `console-agent` | `jvm` | `tools/test-agent/console/run-console.sh --out="$PWD/agent-reports/console"` (runner có sẵn Chrome và Android SDK) | `console-agent-report` (luôn luôn) | Lệnh y hệt. Cần Edge/Chrome và Android SDK (§3) |
| `android` | `jvm` | `./gradlew :game:platform-android:assembleDebug` | `pxworld-android-debug`: file APK | Lệnh y hệt, cần §2.2 |

PROGRESS ghi CI **chưa từng chạy thật** vì nhánh chưa được push. Lần đầu mở PR, hãy xem kỹ cả năm job.

Kế hoạch §8.3 còn thiếu: ktlint, detekt, Konsist (thay bằng `:tools:architecture`), đo coverage, smoke 60 ScreenId, nightly, deploy `dev`.

---

## 9. Tài khoản dev

| Tài khoản | Cách có | Dùng cho |
|---|---|---|
| `admin@pxworld.local` / `admin-dev-password` (role `staff_admin`, có quyền mọi role) | Server tự tạo lúc khởi động **khi `PXWORLD_ENV=dev`**, DB chưa có staff nào và email chưa tồn tại | Console, kịch bản `cloud`, console agent. **Chỉ dùng ở máy dev** |
| Staff khác (`staff_support`, `staff_liveops`, `staff_creator`, `staff_qa`, `staff_dev`) | Console › *Staff* (chỉ admin), mật khẩu ≥ 10 ký tự | Kiểm tra phân quyền |
| Người chơi | Trong game: Cài đặt › Tài khoản và cloud › *Đăng nhập bằng email* › *Tạo tài khoản*, hoặc `POST /auth/register` | Cloud save, thư |
| Khách | Trong game: *Chơi bằng tài khoản khách* | Cloud save không cần email |

Ngoài `dev`, server **không** tự tạo admin nếu thiếu `PXWORLD_ADMIN_EMAIL`/`PXWORLD_ADMIN_PASSWORD`, và dừng nếu thiếu `PXWORLD_JWT_SECRET`. Admin chỉ được tạo một lần: với DB H2 file, đổi `PXWORLD_ADMIN_PASSWORD` sau đó không có tác dụng, phải xoá DB (§7).

Tạo nhanh một người chơi bằng API:
```bash
curl -s -X POST http://localhost:8080/auth/register -H 'Content-Type: application/json' \
  -d '{"email":"tester1@example.com","password":"tester-password","displayName":"Tester 1"}'
# → {"token":"…","account":{…}}
```

---

## 10. Xử lý sự cố

| Triệu chứng | Nguyên nhân | Cách xử lý |
|---|---|---|
| Emulator tắt ngay, log nhắc Vulkan | Emulator dò Vulkan và crash trên máy không có GPU phù hợp | Dùng đúng lệnh ở §4.9: `-gpu swiftshader_indirect -feature -Vulkan` |
| `SDK location not found. Define a valid SDK location…` | Lệnh dùng `settings.gradle` đầy đủ mà máy không có `local.properties`/`ANDROID_HOME` | Tạo `local.properties` (§2.2), hoặc thêm `--settings-file settings-test.gradle` nếu lệnh không dính Android. `run-console.sh` luôn cần SDK: chạy trước `./gradlew --settings-file settings-test.gradle :server:app:installDist` cũng không giúp, vì script tự gọi lại Gradle với settings đầy đủ |
| `No matching toolchains found … languageVersion=17` | Máy không có JDK 17 | Cài Temurin 17, đặt `JAVA_HOME`, mở terminal mới |
| `Permission denied` khi chạy `./gradlew` hoặc script (Linux/macOS) | File lưu không có bit thực thi | `chmod +x gradlew tools/test-agent/run-desktop.sh tools/test-agent/console/run-console.sh` |
| `$'\r': command not found` | Script bị lưu với CRLF | `sed -i 's/\r$//' <file>`. Không tắt `eol=lf` trong `.gitattributes` |
| Cổng đã bị chiếm: `47017` (automation), `8080` (server dev), `18080` (server agent), `4173` (preview), `5173` (Vite dev) | Tiến trình cũ còn chạy | Tìm PID: `netstat -ano \| grep -E ":(47017\|8080\|18080\|4173\|5173) " \| grep LISTENING`. Diệt trong Git Bash: `taskkill //PID <pid> //F`. PowerShell: `Stop-Process -Id <pid> -Force`. Linux/macOS: `lsof -i :4173` rồi `kill <pid>` |
| Sau `run-console.sh` hoặc `run-desktop.sh` vẫn còn `node … vite.js preview` hoặc `java` | Trên Windows, `kill` trong `trap` của script không phải lúc nào cũng diệt được tiến trình con native | Diệt theo PID như dòng trên. Lần chạy sau fail ngay vì `--strictPort` |
| Agent điều khiển nhầm game cũ | Mở game thứ hai khi game đầu còn chạy: cổng 47017 bận, game mới chỉ in `automation server error: …` rồi vẫn chạy **không có** automation | Đóng mọi cửa sổ PXWORLD trước khi chạy agent, hoặc đặt `PXWORLD_AUTOMATION_PORT` khác và truyền `--port` cho `run.mjs` |
| `could not reach game at ws://127.0.0.1:47017` | Game chưa mở, crash lúc khởi động, flavor `RELEASE`, hoặc cổng khác | Đọc `game.log` trong thư mục báo cáo |
| `game is still starting` | Automation trả lời trước khi `GameApp` sẵn sàng | Không cần làm gì, client tự thử lại trong 60 giây |
| `no Chromium-based browser found; pass --browser=<path>` | Không thấy Edge/Chrome ở đường dẫn quen thuộc | `run-console.sh --browser="C:/…/chrome.exe"` hoặc `PXWORLD_BROWSER=…` |
| `Database may be already in use` (H2) | Hai server cùng mở `build/pxworld-dev.mv.db` | Tắt server kia, hoặc cho server thứ hai dùng `PXWORLD_DB_URL="jdbc:h2:mem:x;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1"` |
| H2 và Postgres khác nhau | Mọi test, agent và CI chỉ chạy H2 ở chế độ `MODE=PostgreSQL`. **Chưa có** test nào chạy trên Postgres thật | Chạy Postgres: tạo DB trống, đặt `PXWORLD_DB_URL=jdbc:postgresql://localhost:5432/pxworld`, `PXWORLD_DB_USER`, `PXWORLD_DB_PASSWORD`. Migration `V1__init.sql` tự chạy lúc khởi động. Lỗi nào chỉ xuất hiện trên Postgres thì báo mức S2 |
| Studio lưu làm thay đổi `content/` trong repo | Server dev mặc định `PXWORLD_CONTENT_WRITABLE=true` và `PXWORLD_CONTENT_DIR=content` | Trỏ `PXWORLD_CONTENT_DIR` vào một bản sao (§4.8), hoặc `git checkout -- content/` sau khi test |
| `content is read-only in this environment` | `PXWORLD_CONTENT_WRITABLE=false`, hoặc env khác `dev` | Đặt `PXWORLD_CONTENT_WRITABLE=true` |
| Server dừng với `PXWORLD_JWT_SECRET must be set outside dev` | `PXWORLD_ENV` khác `dev` | Đặt `PXWORLD_JWT_SECRET` |
| Build fail vì một cảnh báo Kotlin | `allWarningsAsErrors = true` | Sửa cảnh báo, không tắt cờ |
| `Screen catalog outputs are stale` | Sửa `catalog.mjs`/`expansion.mjs` mà chưa sinh lại | `node tools/screen-catalog/catalog.mjs`, commit các file sinh ra |
| `ContentSchemaTest` fail | Đổi kiểu record trong `game/content` mà chưa xuất schema | `./gradlew --settings-file settings-test.gradle :game:content:exportSchemas`, commit `content/schemas/` |
| `compileContent` exit 1 | Content có lỗi | Đọc các dòng lỗi phía trên dòng `validation:` |
| Không thấy lại màn pháp lý / ngôn ngữ | Tuỳ chọn lưu ở `~/.prefs/pxworld`, dùng chung mọi env. Test agent cũng ghi vào đây | `rm -f ~/.prefs/pxworld` |
| Tự dưng có slot lạ khi mở game | Launcher chạy từ gốc repo đã nhập save legacy trong `data/` hoặc `lwjgl3/data/` | Dùng `PXWORLD_SAVE_DIR` riêng, hoặc chạy bằng `:game:platform-desktop:run` |
| Tham số kiểu `/PID`, `/F` bị Git Bash đổi thành `C:/Program Files/Git/PID` | Git Bash tự đổi đối số trông giống đường dẫn POSIX | Viết `//PID //F`, hoặc đặt `MSYS_NO_PATHCONV=1` trước lệnh |
| Đường dẫn `$PWD/…` truyền cho `node`/`java` | Git Bash tự đổi `/d/code/…` thành `D:/code/…` cho cả đối số lẫn biến môi trường, nên các script chạy đúng | Nếu một công cụ vẫn cần đường dẫn Windows, dùng `"$(pwd -W)"` |
| Hộp thoại "Viewing full screen" che game Android | Lần đầu mở app ở chế độ immersive | §4.9 |
| App Android không gọi được server qua IP LAN | Bản debug chặn HTTP không mã hoá, trừ `10.0.2.2` và `localhost` | `adb reverse` + `localhost` (§4.9) |
| Gradle chậm | Daemon bị tắt trong `gradle.properties` | Thêm `--daemon` cho lệnh trên máy mình. Không sửa `gradle.properties` trong repo |
| Gradle in cảnh báo deprecated về `--settings-file` | Gradle 8 đánh dấu tuỳ chọn này sẽ bỏ | Không ảnh hưởng với 8.12.1. Khi nâng lên Gradle 9 phải đổi cách tách module (ghi ADR) |

---

## 11. Đang làm (chưa có trên nhánh `rewrite`)
Các việc sau đang được làm ở nhánh hoặc worktree khác. Đừng dựa vào chúng khi test:
- Android chạy test agent: `AutomationServer` trong `AndroidLauncher`, `tools/test-agent/run-android.sh`, job CI emulator ([WP-A1](05-work-packages.md#wp-a1--android-chạy-thật-và-test-agent-trên-emulator)).
- `sim-cli` mô phỏng cân bằng độc lập (WP-A2).
- Telemetry explorer trong Console (WP-A4).
- Spike web client TeaVM (WP-A5).
