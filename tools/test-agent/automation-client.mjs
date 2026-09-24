export class AutomationClient {
  constructor(url) {
    this.url = url;
    this.nextId = 1;
    this.pending = new Map();
    this.visited = new Set();
    this.trace = [];
  }

  async connect(timeoutMs = 60_000) {
    const deadline = Date.now() + timeoutMs;
    while (Date.now() < deadline) {
      try {
        await this.#open();
        await this.waitFor(async () => (await this.call("session.info")).screen != null, 30_000, "first screen");
        return this;
      } catch {
        await sleep(500);
      }
    }
    throw new Error(`could not reach game at ${this.url}`);
  }

  #open() {
    return new Promise((resolve, reject) => {
      const socket = new WebSocket(this.url);
      socket.onopen = () => {
        this.socket = socket;
        resolve();
      };
      socket.onerror = () => reject(new Error("socket error"));
      socket.onmessage = (message) => {
        const payload = JSON.parse(message.data);
        const waiter = this.pending.get(payload.id);
        if (!waiter) return;
        this.pending.delete(payload.id);
        if (payload.error) waiter.reject(new Error(payload.error.message));
        else waiter.resolve(payload.result);
      };
    });
  }

  call(method, params = {}) {
    const id = this.nextId++;
    return new Promise((resolve, reject) => {
      this.pending.set(id, { resolve, reject });
      this.socket.send(JSON.stringify({ jsonrpc: "2.0", id, method, params }));
    });
  }

  async screen() {
    const info = await this.call("session.info");
    if (info.screen) this.visited.add(info.screen);
    for (const id of info.stack ?? []) this.visited.add(id);
    return info.screen;
  }

  async waitFor(predicate, timeoutMs = 15_000, label = "condition") {
    const deadline = Date.now() + timeoutMs;
    let last;
    while (Date.now() < deadline) {
      try {
        last = await predicate();
        if (last) return last;
      } catch (error) {
        last = error.message;
      }
      await sleep(120);
    }
    throw new Error(`timed out waiting for ${label} (last: ${JSON.stringify(last)})`);
  }

  async waitScreen(screenId, timeoutMs = 15_000) {
    await this.waitFor(async () => (await this.screen()) === screenId, timeoutMs, `screen ${screenId}`);
    this.trace.push(`screen ${screenId}`);
  }

  async tree() {
    return this.call("screen.tree");
  }

  async node(testId) {
    return (await this.tree()).find((node) => node.testId === testId);
  }

  async tap(testId, timeoutMs = 10_000) {
    await this.waitFor(async () => (await this.node(testId))?.enabled, timeoutMs, `enabled ${testId}`);
    await this.call("ui.tap", { testId });
    this.trace.push(`tap ${testId}`);
    await sleep(60);
    await this.screen();
  }

  async type(testId, text) {
    await this.waitFor(async () => this.node(testId), 10_000, `field ${testId}`);
    await this.call("ui.type", { testId, text });
    this.trace.push(`type ${testId} = ${text}`);
  }

  state() {
    return this.call("state.get");
  }

  async assertInvariants(step) {
    const problems = await this.call("invariants.check");
    if (problems.length) throw new Error(`invariants broken after ${step}: ${problems.join("; ")}`);
  }

  screenshot(path) {
    return this.call("capture.screenshot", { path });
  }

  close() {
    this.socket?.close();
  }
}

export const sleep = (ms) => new Promise((resolve) => setTimeout(resolve, ms));

export function assert(condition, message) {
  if (!condition) throw new Error(`assertion failed: ${message}`);
}
