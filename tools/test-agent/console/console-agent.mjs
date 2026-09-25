import { spawn } from "node:child_process";
import { existsSync, mkdirSync, mkdtempSync, rmSync, writeFileSync } from "node:fs";
import { tmpdir } from "node:os";
import { dirname, join, resolve } from "node:path";
import { fileURLToPath } from "node:url";

const here = dirname(fileURLToPath(import.meta.url));
const args = Object.fromEntries(process.argv.slice(2).map((arg) => { const [key, ...rest] = arg.replace(/^--/, "").split("="); return [key, rest.join("=")]; }));
const consoleUrl = (args.url ?? "http://localhost:4173").replace(/\/$/, "");
const apiUrl = (args.api ?? "http://localhost:8080").replace(/\/$/, "");
const adminEmail = args.email ?? "admin@pxworld.local";
const adminPassword = args.password ?? "admin-dev-password";
const stamp = new Date().toISOString().replace(/[:.]/g, "-");
const reportDir = resolve(args.out ?? resolve(here, "..", "reports", `console-${stamp}`));
mkdirSync(reportDir, { recursive: true });

const BROWSERS = [
  args.browser,
  process.env.PXWORLD_BROWSER,
  "C:/Program Files (x86)/Microsoft/Edge/Application/msedge.exe",
  "C:/Program Files/Google/Chrome/Application/chrome.exe",
  "/usr/bin/google-chrome",
  "/usr/bin/chromium",
  "/usr/bin/chromium-browser",
].filter(Boolean);

const sleep = (ms) => new Promise((done) => setTimeout(done, ms));

async function api(method, path, token, body) {
  const response = await fetch(apiUrl + path, {
    method,
    headers: { ...(token ? { Authorization: `Bearer ${token}` } : {}), ...(body ? { "Content-Type": "application/json" } : {}) },
    body: body ? JSON.stringify(body) : undefined,
  });
  const text = await response.text();
  if (!response.ok) throw new Error(`${method} ${path} → ${response.status} ${text}`);
  return text ? JSON.parse(text) : undefined;
}

class Browser {
  static async launch() {
    const executable = BROWSERS.find((path) => existsSync(path));
    if (!executable) throw new Error("no Chromium-based browser found; pass --browser=<path>");
    const profile = mkdtempSync(join(tmpdir(), "pxworld-console-agent-"));
    const child = spawn(executable, ["--headless=new", "--remote-debugging-port=0", `--user-data-dir=${profile}`, "--no-first-run", "--no-default-browser-check", "--window-size=1366,900", "about:blank"], { stdio: ["ignore", "ignore", "pipe"] });
    const endpoint = await new Promise((done, fail) => {
      let buffer = "";
      const timer = setTimeout(() => fail(new Error("browser did not expose DevTools")), 20000);
      child.stderr.on("data", (chunk) => {
        buffer += chunk;
        const match = buffer.match(/DevTools listening on (ws:\/\/\S+)/);
        if (match) {
          clearTimeout(timer);
          done(match[1]);
        }
      });
    });
    const browser = new Browser(child, profile, new WebSocket(endpoint));
    await new Promise((done) => browser.socket.addEventListener("open", done, { once: true }));
    return browser;
  }

  constructor(child, profile, socket) {
    this.child = child;
    this.profile = profile;
    this.socket = socket;
    this.nextId = 1;
    this.pending = new Map();
    this.errors = [];
    socket.addEventListener("message", (event) => {
      const message = JSON.parse(event.data);
      if (message.id && this.pending.has(message.id)) {
        const { done, fail } = this.pending.get(message.id);
        this.pending.delete(message.id);
        if (message.error) fail(new Error(message.error.message));
        else done(message.result);
      } else if (message.method === "Runtime.exceptionThrown") {
        this.errors.push(message.params.exceptionDetails.exception?.description ?? message.params.exceptionDetails.text);
      } else if (message.method === "Runtime.consoleAPICalled" && message.params.type === "error") {
        this.errors.push(message.params.args.map((arg) => arg.value ?? arg.description).join(" "));
      }
    });
  }

  send(method, params = {}, sessionId = this.sessionId) {
    const id = this.nextId++;
    this.socket.send(JSON.stringify({ id, method, params, ...(sessionId ? { sessionId } : {}) }));
    return new Promise((done, fail) => this.pending.set(id, { done, fail }));
  }

  async open(url) {
    const { targetId } = await this.send("Target.createTarget", { url: "about:blank" }, null);
    this.sessionId = (await this.send("Target.attachToTarget", { targetId, flatten: true }, null)).sessionId;
    await this.send("Page.enable");
    await this.send("Runtime.enable");
    await this.send("Page.navigate", { url });
  }

  async evaluate(expression) {
    const result = await this.send("Runtime.evaluate", { expression, awaitPromise: true, returnByValue: true });
    if (result.exceptionDetails) throw new Error(result.exceptionDetails.exception?.description ?? result.exceptionDetails.text);
    return result.result.value;
  }

  async waitFor(expression, label, timeout = 10000) {
    const deadline = Date.now() + timeout;
    while (Date.now() < deadline) {
      const value = await this.evaluate(`!!(${expression})`).catch(() => undefined);
      if (value) return value;
      await sleep(100);
    }
    throw new Error(`timed out waiting for ${label}`);
  }

  async screenshot(path) {
    const { data } = await this.send("Page.captureScreenshot", { format: "png" });
    writeFileSync(path, Buffer.from(data, "base64"));
  }

  async close() {
    this.socket.close();
    this.child.kill();
    await sleep(500);
    rmSync(this.profile, { recursive: true, force: true });
  }
}

const js = JSON.stringify;

class ConsoleAgent {
  constructor(browser) {
    this.browser = browser;
    this.visited = new Set();
    this.trace = [];
    this.shots = [];
  }

  async step(label, work) {
    const started = Date.now();
    try {
      await work();
      this.trace.push({ step: label, ok: true, ms: Date.now() - started });
    } catch (error) {
      this.trace.push({ step: label, ok: false, ms: Date.now() - started, error: error.message });
      throw error;
    }
  }

  async go(hash) {
    await this.browser.evaluate(`location.hash = ${js(hash)}`);
  }

  async screen(id) {
    await this.browser.waitFor(`document.querySelector('[data-screen-id=${js(id)}]') && !document.body.innerText.includes('Đang tải…')`, `screen ${id}`);
    const ids = await this.browser.evaluate(`[...document.querySelectorAll('[data-screen-id]')].map((node) => node.dataset.screenId)`);
    ids.forEach((screen) => this.visited.add(screen));
  }

  async fill(selector, value) {
    await this.browser.waitFor(`document.querySelector(${js(selector)})`, selector);
    await this.browser.evaluate(`(() => {
      const node = document.querySelector(${js(selector)});
      const proto = node instanceof HTMLTextAreaElement ? HTMLTextAreaElement.prototype : HTMLInputElement.prototype;
      Object.getOwnPropertyDescriptor(proto, "value").set.call(node, ${js(String(value))});
      node.dispatchEvent(new Event("input", { bubbles: true }));
    })()`);
  }

  async click(text, scope = "body") {
    await this.browser.waitFor(
      `[...document.querySelectorAll(${js(`${scope} button, ${scope} a`)})].some((node) => node.textContent.trim() === ${js(text)} && !node.disabled)`,
      `enabled control "${text}"`,
    );
    await this.browser.evaluate(`[...document.querySelectorAll(${js(`${scope} button, ${scope} a`)})].find((node) => node.textContent.trim() === ${js(text)} && !node.disabled).click()`);
  }

  async expectText(text, timeout) {
    await this.browser.waitFor(`document.body.innerText.includes(${js(text)})`, `text "${text}"`, timeout);
  }

  async shot(label) {
    const path = resolve(reportDir, `${String(this.shots.length + 1).padStart(2, "0")}-${label}.png`);
    await this.browser.screenshot(path);
    this.shots.push(path);
  }
}

const report = { mode: "console", scenario: "console-staff-flows", startedAt: new Date().toISOString(), status: "passed", error: null };
const browser = await Browser.launch();
const agent = new ConsoleAgent(browser);

try {
  const admin = await api("POST", "/auth/login", null, { email: adminEmail, password: adminPassword });
  const suffix = Date.now().toString(36);
  const player = await api("POST", "/auth/register", null, { email: `agent-player-${suffix}@example.com`, password: "agent-password", displayName: `AgentPlayer${suffix}` });
  await api("PUT", "/saves/main", player.token, { expectedRevision: 0, body: JSON.stringify({ agent: true }) });
  await api("POST", "/telemetry", player.token, { clientVersion: "console-agent", events: [{ name: "session.start" }] });

  await browser.open(`${consoleUrl}/#/`);

  await agent.step("login", async () => {
    await agent.screen("console.auth.login");
    await agent.shot("login");
    await agent.fill("input[type=email]", adminEmail);
    await agent.fill("input[type=password]", adminPassword);
    await agent.click("Đăng nhập");
    await agent.screen("console.dashboards.overview");
    await agent.expectText("session.start");
    await agent.shot("dashboard");
  });

  await agent.step("player search and detail", async () => {
    await agent.go("/players");
    await agent.screen("console.players.player_search");
    await agent.fill("input[aria-label='Từ khoá']", `agent-player-${suffix}`);
    await agent.click("Tìm");
    await agent.click(`AgentPlayer${suffix}`);
    await agent.screen("console.players.player_overview");
    await agent.expectText("main");
    await agent.shot("player");
  });

  await agent.step("grant mail", async () => {
    await agent.fill("[data-screen-id='console.players.player_grant'] input[type=number]", 250);
    await agent.fill("[data-screen-id='console.players.player_grant'] input[name=reason]", "console agent grant");
    await agent.click("Gửi", "[data-screen-id='console.players.player_grant']");
    await agent.expectText("Đã gửi thư kèm quà.");
    await agent.expectText("chờ nhận");
  });

  await agent.step("sanction and lift", async () => {
    await agent.fill("[data-screen-id='console.players.player_sanctions'] input[name=reason]", "console agent sanction");
    await agent.click("Khoá", "[data-screen-id='console.players.player_sanctions']");
    await agent.expectText("Đã khoá tài khoản.");
    await agent.expectText("Khoá tới");
    await agent.shot("sanctioned");
    await agent.fill("[data-screen-id='console.players.player_sanctions'] input[name=reason]", "console agent lift");
    await agent.click("Gỡ khoá", "[data-screen-id='console.players.player_sanctions']");
    await agent.expectText("Đã gỡ khoá.");
    await agent.expectText("player.lift");
  });

  await agent.step("audit log", async () => {
    await agent.go("/audit");
    await agent.screen("console.operations.audit_log");
    await agent.expectText("player.sanction");
  });

  await agent.step("content publish and promote", async () => {
    await agent.go("/content");
    await agent.screen("console.operations.deployments");
    await agent.click("Đóng gói content hiện tại");
    await agent.expectText("Đã đóng gói content");
    await agent.click("qa");
    await agent.expectText("đã lên qa.");
    await agent.shot("content");
    const manifest = await api("GET", "/content/manifest?env=qa");
    if (!manifest.version) throw new Error("qa channel has no release after promotion");
  });

  await agent.step("studio edit with validation", async () => {
    await agent.go("/studio");
    await agent.screen("studio.editors.studio_home");
    await agent.click("items");
    await agent.screen("studio.content_entities.items.list");
    await agent.browser.evaluate(`document.querySelector('.record-list li a').click()`);
    await agent.screen("studio.content_entities.items.editor");
    await agent.browser.waitFor(`document.querySelector(".schema-form input[aria-label='$.name']")`, "schema form");
    await agent.click("Kiểm tra");
    await agent.expectText("Hợp lệ.");
    await agent.fill(".schema-form input[aria-label='$.name']", "text.agent.missing");
    await agent.shot("studio-form");
    await agent.click("Kiểm tra");
    await agent.expectText("content rejected");
    await agent.shot("studio-rejected");
    await agent.click("JSON");
    const edited = JSON.parse(await agent.browser.evaluate(`document.querySelector('.editor textarea').value`));
    if (edited.name !== "text.agent.missing") throw new Error("form edit did not reach the JSON view");
    await agent.click("Hoàn tác");
    await agent.click("Biểu mẫu");
    const restored = await agent.browser.evaluate(`document.querySelector(".schema-form input[aria-label='$.name']").value`);
    if (restored === "text.agent.missing") throw new Error("undo did not restore the form");
  });

  await agent.step("qa runs", async () => {
    await api("POST", "/qa/agent-runs", admin.token, { mode: "console-seed", status: "passed", coverage: { visitedCount: 1, registeredCount: 1, launchPercent: 1 } });
    await agent.go("/qa/runs");
    await agent.screen("qa.quality.agent_runs");
    await agent.browser.evaluate(`document.querySelector('table a').click()`);
    await agent.screen("qa.quality.agent_run_detail");
  });

  await agent.step("staff form and sign out", async () => {
    await agent.go("/staff");
    await agent.screen("console.liveops_entities.staff_users.editor");
    await agent.go("/nowhere");
    await agent.screen("console.auth.no_access");
    await agent.click("Đăng xuất");
    await agent.screen("console.auth.login");
  });

  if (browser.errors.length > 0) throw new Error(`page errors:\n${browser.errors.join("\n")}`);
} catch (error) {
  report.status = "failed";
  report.error = error.stack ?? String(error);
  await agent.shot("failure").catch(() => {});
}

await browser.close();
report.finishedAt = new Date().toISOString();
report.trace = agent.trace;
report.screenshots = agent.shots;
report.pageErrors = browser.errors;
report.coverage = { visited: [...agent.visited].sort(), visitedCount: agent.visited.size, registeredCount: agent.visited.size, launchPercent: 0 };
writeFileSync(resolve(reportDir, "report.json"), JSON.stringify(report, null, 2));
console.log(`${report.status.toUpperCase()} console — ${agent.trace.filter((step) => step.ok).length}/${agent.trace.length} steps, ${agent.visited.size} screens`);
if (report.error) console.log(report.error);
console.log(`report: ${reportDir}`);
if (args.upload !== undefined) {
  const { token } = await api("POST", "/auth/login", null, { email: adminEmail, password: adminPassword });
  const { id } = await api("POST", "/qa/agent-runs", token, report);
  console.log(`uploaded as run ${id}`);
}
process.exit(report.status === "passed" ? 0 : 1);
