import { spawn } from "node:child_process";
import { existsSync, mkdirSync, mkdtempSync, readdirSync, readFileSync, rmSync, statSync, writeFileSync } from "node:fs";
import { createServer } from "node:http";
import { tmpdir } from "node:os";
import { dirname, extname, join, relative, resolve, sep } from "node:path";
import { fileURLToPath } from "node:url";
import { gzipSync } from "node:zlib";

const here = dirname(fileURLToPath(import.meta.url));
const root = resolve(here, "..", "..", "..");
const args = Object.fromEntries(process.argv.slice(2).map((arg) => { const [key, ...rest] = arg.replace(/^--/, "").split("="); return [key, rest.join("=")]; }));
const distribution = resolve(args.dist ?? resolve(root, "game", "platform-web", "build", "dist", "webapp"));
const port = Number(args.port ?? 8095);
const bootTimeout = Number(args.timeout ?? 90000);
const stamp = new Date().toISOString().replace(/[:.]/g, "-");
const reportDir = resolve(args.out ?? resolve(here, "..", "reports", `web-${stamp}`));

const BROWSERS = [
  args.browser,
  process.env.PXWORLD_BROWSER,
  "C:/Program Files (x86)/Microsoft/Edge/Application/msedge.exe",
  "C:/Program Files/Google/Chrome/Application/chrome.exe",
  "/usr/bin/google-chrome",
  "/usr/bin/chromium",
  "/usr/bin/chromium-browser",
].filter(Boolean);

const CONTENT_TYPES = {
  ".html": "text/html; charset=utf-8",
  ".js": "text/javascript; charset=utf-8",
  ".wasm": "application/wasm",
  ".json": "application/json; charset=utf-8",
  ".png": "image/png",
  ".jpg": "image/jpeg",
  ".mp3": "audio/mpeg",
  ".ogg": "audio/ogg",
  ".wav": "audio/wav",
  ".txt": "text/plain; charset=utf-8",
};

const sleep = (ms) => new Promise((done) => setTimeout(done, ms));
const js = JSON.stringify;
const screenId = (id) => `game.${id}`;

function files(directory) {
  return readdirSync(directory, { withFileTypes: true }).flatMap((entry) => {
    const path = join(directory, entry.name);
    return entry.isDirectory() ? files(path) : [path];
  });
}

function measureBundle() {
  const all = files(distribution).map((path) => ({ path: relative(distribution, path).split(sep).join("/"), bytes: statSync(path).size }));
  const scripts = all.filter((file) => file.path.endsWith(".js"));
  const application = resolve(distribution, "teavm", "app.js");
  const sum = (list) => list.reduce((total, file) => total + file.bytes, 0);
  return {
    files: all.length,
    totalBytes: sum(all),
    javascriptBytes: sum(scripts),
    applicationBytes: statSync(application).size,
    applicationGzipBytes: gzipSync(readFileSync(application), { level: 9 }).length,
    assetBytes: sum(all.filter((file) => file.path.startsWith("assets/"))),
    scripts: scripts.map((file) => ({ ...file, gzipBytes: gzipSync(readFileSync(resolve(distribution, file.path)), { level: 9 }).length })),
  };
}

function serve() {
  const server = createServer((request, response) => {
    const path = decodeURIComponent(new URL(request.url, "http://localhost").pathname);
    if (path === "/favicon.ico") {
      response.writeHead(204);
      response.end();
      return;
    }
    const target = resolve(distribution, `.${path.endsWith("/") ? `${path}index.html` : path}`);
    if (!target.startsWith(distribution) || !existsSync(target) || statSync(target).isDirectory()) {
      response.writeHead(404, { "Content-Type": "text/plain" });
      response.end("not found");
      return;
    }
    response.writeHead(200, { "Content-Type": CONTENT_TYPES[extname(target).toLowerCase()] ?? "application/octet-stream", "Cache-Control": "no-store" });
    response.end(readFileSync(target));
  });
  return new Promise((done, fail) => {
    server.once("error", fail);
    server.listen(port, "127.0.0.1", () => done(server));
  });
}

class Browser {
  static async launch() {
    const executable = BROWSERS.find((path) => existsSync(path));
    if (!executable) throw new Error("no Chromium-based browser found; pass --browser=<path>");
    const profile = mkdtempSync(join(tmpdir(), "pxworld-web-smoke-"));
    const child = spawn(executable, ["--headless=new", "--remote-debugging-port=0", `--user-data-dir=${profile}`, "--no-first-run", "--no-default-browser-check", "--autoplay-policy=no-user-gesture-required", "--use-angle=swiftshader", "--enable-unsafe-swiftshader", "--window-size=1280,720", "about:blank"], { stdio: ["ignore", "ignore", "pipe"] });
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
    this.console = [];
    socket.addEventListener("message", (event) => {
      const message = JSON.parse(event.data);
      if (message.id && this.pending.has(message.id)) {
        const { done, fail } = this.pending.get(message.id);
        this.pending.delete(message.id);
        if (message.error) fail(new Error(message.error.message));
        else done(message.result);
      } else if (message.method === "Runtime.exceptionThrown") {
        const details = message.params.exceptionDetails;
        this.errors.push(details.exception?.description ?? details.text);
      } else if (message.method === "Runtime.consoleAPICalled") {
        const text = message.params.args.map((arg) => arg.value ?? arg.description).join(" ");
        this.console.push(`${message.params.type}: ${text}`);
        if (message.params.type === "error") this.errors.push(text);
      } else if (message.method === "Network.loadingFailed" && !message.params.canceled) {
        this.errors.push(`request failed: ${message.params.errorText} ${this.requests.get(message.params.requestId) ?? ""}`);
      } else if (message.method === "Network.requestWillBeSent") {
        this.requests.set(message.params.requestId, message.params.request.url);
      } else if (message.method === "Network.responseReceived" && message.params.response.status >= 400) {
        this.errors.push(`HTTP ${message.params.response.status} ${message.params.response.url}`);
      }
    });
    this.requests = new Map();
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
    await this.send("Network.enable");
    await this.send("Emulation.setDeviceMetricsOverride", { width: 1280, height: 720, deviceScaleFactor: 1, mobile: false });
    await this.send("Page.navigate", { url });
  }

  async evaluate(expression) {
    const result = await this.send("Runtime.evaluate", { expression, awaitPromise: true, returnByValue: true });
    if (result.exceptionDetails) throw new Error(result.exceptionDetails.exception?.description ?? result.exceptionDetails.text);
    return result.result.value;
  }

  async waitFor(expression, label, timeout = 15000) {
    const deadline = Date.now() + timeout;
    while (Date.now() < deadline) {
      const value = await this.evaluate(`(() => { try { return ${expression}; } catch (error) { return undefined; } })()`).catch(() => undefined);
      if (value) return value;
      if (this.errors.length > 0) throw new Error(`page error while waiting for ${label}: ${this.errors[0]}`);
      await sleep(50);
    }
    throw new Error(`timed out waiting for ${label}`);
  }

  async click(x, y) {
    await this.send("Input.dispatchMouseEvent", { type: "mouseMoved", x, y });
    await this.send("Input.dispatchMouseEvent", { type: "mousePressed", x, y, button: "left", buttons: 1, clickCount: 1 });
    await sleep(60);
    await this.send("Input.dispatchMouseEvent", { type: "mouseReleased", x, y, button: "left", buttons: 0, clickCount: 1 });
  }

  async screenshot(path) {
    const { data } = await this.send("Page.captureScreenshot", { format: "png" });
    writeFileSync(path, Buffer.from(data, "base64"));
  }

  async close() {
    this.socket.close();
    const exited = new Promise((done) => this.child.once("exit", done));
    this.child.kill();
    await Promise.race([exited, sleep(5000)]);
    try {
      rmSync(this.profile, { recursive: true, force: true, maxRetries: 10, retryDelay: 200 });
    } catch (error) {
      console.warn(`could not remove browser profile ${this.profile}: ${error.message}`);
    }
  }
}

class WebAgent {
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

  async screen(id, timeout) {
    await this.browser.waitFor(`window.pxworld && window.pxworld.screen() === ${js(screenId(id))}`, `screen ${id}`, timeout);
    this.visited.add(id);
    return this.browser.evaluate("performance.now()");
  }

  async tap(testId) {
    const target = screenId(testId);
    await this.browser.waitFor(`window.pxworld.nodes().some((node) => node.testId === ${js(target)} && node.enabled)`, `enabled ${testId}`);
    const point = await this.browser.evaluate(`(() => {
      const canvas = document.getElementById("canvas");
      const bounds = canvas.getBoundingClientRect();
      const node = window.pxworld.nodes().find((candidate) => candidate.testId === ${js(target)});
      return { x: bounds.left + node.x * bounds.width / canvas.width, y: bounds.top + node.y * bounds.height / canvas.height };
    })()`);
    await this.browser.click(point.x, point.y);
  }

  async shot(label) {
    const path = resolve(reportDir, `${String(this.shots.length + 1).padStart(2, "0")}-${label}.png`);
    await this.browser.screenshot(path);
    this.shots.push(path);
  }
}

if (!existsSync(resolve(distribution, "index.html"))) {
  console.error(`no web bundle at ${distribution}; run ./gradlew -Ppxworld.web=true :game:platform-web:buildWeb first`);
  process.exit(2);
}

const server = await serve();
const url = `http://localhost:${port}/index.html`;
if (args.serve !== undefined) {
  console.log(`serving ${distribution} at ${url} (Ctrl+C to stop)`);
} else {
  mkdirSync(reportDir, { recursive: true });
  const report = { mode: "web", scenario: "web-smoke", startedAt: new Date().toISOString(), status: "passed", error: null, url, bundle: measureBundle() };
  const browser = await Browser.launch();
  const agent = new WebAgent(browser);
  try {
    await browser.open(url);

    await agent.step("boot to legal notice", async () => {
      report.timeToFirstScreenMs = Math.round(await agent.screen("boot.legal_notice", bootTimeout));
      await sleep(300);
      await agent.shot("legal-notice");
    });

    await agent.step("first run to main menu", async () => {
      await agent.tap("boot.legal_notice/accept");
      await agent.screen("boot.privacy_consent");
      await agent.tap("boot.privacy_consent/deny");
      await agent.screen("boot.language_pick");
      await agent.shot("language-pick");
      await agent.tap("boot.language_pick/language/en");
      report.timeToMainMenuMs = Math.round(await agent.screen("boot.main_menu"));
      await sleep(300);
      await agent.shot("main-menu");
    });

    if (browser.errors.length > 0) throw new Error(`page errors:\n${browser.errors.join("\n")}`);
  } catch (error) {
    report.status = "failed";
    report.error = error.stack ?? String(error);
    await agent.shot("failure").catch(() => {});
  }
  report.stack = await browser.evaluate("window.pxworld ? window.pxworld.stack() : null").catch(() => null);
  await browser.close();
  server.close();
  report.finishedAt = new Date().toISOString();
  report.trace = agent.trace;
  report.visited = [...agent.visited];
  report.screenshots = agent.shots;
  report.pageErrors = browser.errors;
  report.console = browser.console;
  writeFileSync(resolve(reportDir, "report.json"), JSON.stringify(report, null, 2));
  const kib = (bytes) => `${(bytes / 1024).toFixed(0)} KiB`;
  console.log(`${report.status.toUpperCase()} web — ${agent.trace.filter((step) => step.ok).length}/${agent.trace.length} steps, screens: ${report.visited.join(", ")}`);
  console.log(`first screen ${report.timeToFirstScreenMs ?? "-"} ms, main menu ${report.timeToMainMenuMs ?? "-"} ms`);
  console.log(`bundle ${kib(report.bundle.totalBytes)} total, app.js ${kib(report.bundle.applicationBytes)} (${kib(report.bundle.applicationGzipBytes)} gzip), assets ${kib(report.bundle.assetBytes)}`);
  if (report.error) console.log(report.error);
  console.log(`report: ${reportDir}`);
  process.exit(report.status === "passed" ? 0 : 1);
}
