import { assert, sleep } from "../automation-client.mjs";

const S = (id) => `game.${id}`;
const gold = (state) => state.balances["currency.gold"] ?? 0;
const apiUrl = (process.env.PXWORLD_TEST_API ?? "http://localhost:18080").replace(/\/$/, "");
const adminEmail = process.env.PXWORLD_ADMIN_EMAIL ?? "admin@pxworld.local";
const adminPassword = process.env.PXWORLD_ADMIN_PASSWORD ?? "admin-dev-password";

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

export const name = "cloud";
export const description = "Sign in by email, back up, resolve a conflict from another device, claim support mail, send telemetry";

export async function run(agent, shot) {
  const email = `cloud-${Date.now().toString(36)}@example.com`;
  const password = "cloud-agent-password";
  const player = await api("POST", "/auth/register", null, { email, password, displayName: "Cloud Agent" });

  await agent.passFirstRun();
  await agent.waitScreen(S("boot.main_menu"), 20_000);
  await agent.tap(S("boot.main_menu/new_game"));
  await agent.waitScreen(S("onboarding.hero_create_class"));
  await agent.tap(S("onboarding.hero_create_class/pick/aldric"));
  await agent.waitScreen(S("onboarding.hero_create_name"));
  await agent.type(S("onboarding.hero_create_name/name"), "Cloud Tester");
  await agent.tap(S("onboarding.hero_create_name/next"));
  await agent.tap(S("onboarding.hero_create_confirm/start"));
  await agent.waitScreen(S("world.world_explore"));
  await agent.settle();
  const slot = "cloud_tester";

  await agent.call("screen.open", { screenId: S("settings.settings_home") });
  await agent.waitScreen(S("settings.settings_home"));
  await agent.tap(S("settings.settings_home/open/account"));
  await agent.waitScreen(S("settings.settings_account"));
  if (await agent.node(S("settings.settings_account/sign_out"))) await agent.tap(S("settings.settings_account/sign_out"));
  await agent.waitFor(async () => agent.node(S("settings.settings_account/signed_out")), 5000, "signed-out state");
  await shot("01-account-signed-out");

  await agent.tap(S("settings.settings_account/email"));
  await agent.waitScreen(S("boot.login_email"));
  await agent.type(S("boot.login_email/email"), email);
  await agent.type(S("boot.login_email/password"), "wrong-password-123");
  await agent.tap(S("boot.login_email/sign_in"));
  await agent.waitFor(async () => agent.node(S("boot.login_email/problem")), 10_000, "wrong password is reported");
  await shot("02-login-rejected");
  await agent.type(S("boot.login_email/password"), password);
  await agent.tap(S("boot.login_email/sign_in"));
  await agent.waitScreen(S("settings.settings_account"));
  await agent.waitFor(async () => agent.node(S("settings.settings_account/signed_in")), 10_000, "signed-in state");

  await agent.tap(S("settings.settings_account/upload"));
  let saves = await agent.waitFor(async () => { const list = await api("GET", "/saves", player.token); return list.length ? list : null; }, 10_000, "cloud save uploaded");
  assert(saves[0].slot === slot && saves[0].revision === 1, `first upload is revision 1: ${JSON.stringify(saves)}`);
  await agent.waitFor(async () => (await agent.node(S(`settings.settings_account/slot/${slot}`)))?.text?.includes("1"), 5000, "synced revision shown");
  await shot("03-uploaded");

  const cloudCopy = await api("GET", `/saves/${slot}`, player.token);
  await api("PUT", `/saves/${slot}`, player.token, { expectedRevision: 1, body: cloudCopy.body });
  await agent.tap(S("settings.settings_account/upload"));
  await agent.waitScreen(S("boot.save_conflict"));
  await agent.waitFor(async () => (await agent.node(S("boot.save_conflict/cloud")))?.text?.includes("Cloud Tester"), 10_000, "cloud copy previewed");
  await shot("04-conflict");
  await agent.tap(S("boot.save_conflict/keep_local"));
  await agent.waitScreen(S("settings.settings_account"));
  saves = await api("GET", "/saves", player.token);
  assert(saves[0].revision === 3, `keeping this device writes revision 3: ${JSON.stringify(saves)}`);

  const admin = await api("POST", "/auth/login", null, { email: adminEmail, password: adminPassword });
  const [account] = await api("GET", `/admin/players?q=${encodeURIComponent(email)}`, admin.token);
  await api("POST", `/admin/players/${account.id}/grant`, admin.token, { subject: "Agent gift", grants: { "currency.gold": 250 }, reason: "cloud scenario" });
  const before = gold(await agent.state());
  await agent.tap(S("settings.settings_account/mail"));
  await agent.waitScreen(S("social.mail_inbox"));
  const mailbox = await api("GET", "/mail", player.token);
  const mailId = mailbox[0].id;
  await agent.tap(S(`social.mail_inbox/claim/${mailId}`));
  await agent.waitFor(async () => agent.node(S(`social.mail_inbox/claimed/${mailId}`)), 10_000, "mail marked claimed");
  const after = gold(await agent.state());
  assert(after === before + 250, `mail grants 250 gold: ${before} → ${after}`);
  await agent.assertInvariants("mail claim");
  await shot("05-mail-claimed");
  const replay = await api("POST", `/mail/${mailId}/claim`, player.token).catch((error) => error.message);
  assert(String(replay).includes("409"), `server refuses a second claim: ${replay}`);

  await agent.call("screen.open", { screenId: S("settings.settings_privacy") });
  await agent.waitScreen(S("settings.settings_privacy"));
  await agent.tap(S("settings.settings_privacy/analytics"));
  await agent.call("screen.open", { screenId: S("settings.settings_home") });
  await agent.waitScreen(S("settings.settings_home"));
  await agent.tap(S("settings.settings_home/main_menu"));
  await agent.waitScreen(S("boot.main_menu"));
  await agent.tap(S("boot.main_menu/continue"));
  await agent.waitScreen(S("world.world_explore"));
  await agent.waitFor(async () => {
    const dashboard = await api("GET", "/admin/dashboard", admin.token);
    return dashboard.telemetryLastDay["session.start"] ? dashboard : null;
  }, 45_000, "session.start telemetry reaches the server");
  await sleep(200);
}
