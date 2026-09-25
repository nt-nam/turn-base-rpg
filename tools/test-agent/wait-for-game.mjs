import { AutomationClient } from "./automation-client.mjs";

const args = Object.fromEntries(process.argv.slice(2).map((arg) => arg.replace(/^--/, "").split("=")));
const port = args.port ?? "47017";
const timeoutSeconds = Number(args.timeout ?? 180);

try {
  const agent = await new AutomationClient(`ws://127.0.0.1:${port}`).connect(timeoutSeconds * 1000);
  const info = await agent.call("session.info");
  agent.close();
  console.log(`game answered on port ${port}: screen ${info.screen}, ${info.width}x${info.height}, flavor ${info.flavor}`);
  process.exit(0);
} catch (error) {
  console.error(`game did not answer on port ${port} within ${timeoutSeconds}s: ${error.message}`);
  process.exit(1);
}
