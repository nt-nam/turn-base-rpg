export interface Account {
  id: string;
  email?: string;
  displayName: string;
  kind: string;
  roles: string[];
  bannedUntil?: number;
  createdAt: number;
}

export interface Session {
  token: string;
  account: Account;
}

export interface SaveMeta {
  slot: string;
  revision: number;
  updatedAt: number;
  bytes: number;
}

export interface Mail {
  id: string;
  subject: string;
  grants: string;
  claimed: boolean;
  createdAt: number;
}

export interface AuditEntry {
  id: string;
  actorId: string;
  action: string;
  target: string;
  reason: string;
  payload: string;
  createdAt: number;
}

export interface PlayerDetail {
  account: Account;
  saves: SaveMeta[];
  mail: Mail[];
  audit: AuditEntry[];
}

export interface Dashboard {
  env: string;
  contentVersion?: string;
  accounts: number;
  players: number;
  saves: number;
  telemetryLastDay: Record<string, number>;
  battleValidations: number;
  rejectedBattles: number;
  agentRuns: number;
}

export interface Release {
  version: string;
  sha256: string;
  bytes: number;
  records: number;
  publishedBy: string;
  createdAt: number;
}

export interface Issue {
  severity: "ERROR" | "WARNING";
  recordId: string;
  message: string;
}

export interface Validation {
  errors: number;
  warnings: number;
  records: number;
  issues: Issue[];
}

export interface AgentRun {
  id: string;
  mode: string;
  status: string;
  visited: number;
  registered: number;
  launchPercent: number;
  createdAt: number;
}

export type ContentRecord = Record<string, unknown> & { id: string };

export class ApiError extends Error {
  constructor(
    readonly status: number,
    message: string,
    readonly validation?: Validation,
  ) {
    super(message);
  }
}

const base = import.meta.env.VITE_PXWORLD_API ?? "/api";

type Listener = () => void;
const unauthorizedListeners = new Set<Listener>();
export const onUnauthorized = (listener: Listener) => {
  unauthorizedListeners.add(listener);
  return () => unauthorizedListeners.delete(listener);
};

let token: string | null = null;
export const setToken = (value: string | null) => {
  token = value;
};

async function request<T>(method: string, path: string, body?: unknown, raw = false): Promise<T> {
  const headers: Record<string, string> = {};
  if (token) headers.Authorization = `Bearer ${token}`;
  if (body !== undefined) headers["Content-Type"] = "application/json";
  const response = await fetch(base + path, { method, headers, body: body === undefined ? undefined : JSON.stringify(body) });
  const text = await response.text();
  if (!response.ok) {
    if (response.status === 401 && token) unauthorizedListeners.forEach((listener) => listener());
    let message = `${response.status} ${response.statusText}`;
    let validation: Validation | undefined;
    try {
      const parsed = JSON.parse(text);
      message = parsed.error ?? message;
      validation = parsed.detail;
    } catch {
      /* body was not JSON */
    }
    throw new ApiError(response.status, message, validation);
  }
  if (raw) return text as T;
  return (text ? JSON.parse(text) : undefined) as T;
}

const q = encodeURIComponent;

export const api = {
  login: (email: string, password: string) => request<Session>("POST", "/auth/login", { email, password }),
  me: () => request<Account>("GET", "/me"),
  dashboard: () => request<Dashboard>("GET", "/admin/dashboard"),
  players: (query: string) => request<Account[]>("GET", `/admin/players?q=${q(query)}`),
  player: (id: string) => request<PlayerDetail>("GET", `/admin/players/${q(id)}`),
  grant: (id: string, subject: string, grants: Record<string, number>, reason: string) =>
    request<{ id: string }>("POST", `/admin/players/${q(id)}/grant`, { subject, grants, reason }),
  sanction: (id: string, hours: number, reason: string) => request<Account>("POST", `/admin/players/${q(id)}/sanction`, { hours, reason }),
  lift: (id: string, reason: string) => request<Account>("POST", `/admin/players/${q(id)}/lift`, { reason }),
  audit: (target?: string) => request<AuditEntry[]>("GET", `/admin/audit${target ? `?target=${q(target)}` : ""}`),
  createStaff: (email: string, password: string, displayName: string, roles: string[]) =>
    request<Account>("POST", "/admin/staff", { email, password, displayName, roles }),
  releases: () => request<Release[]>("GET", "/content/releases"),
  channels: () => request<Record<string, string>>("GET", "/content/channels"),
  publish: () => request<{ id: string }>("POST", "/content/releases"),
  promote: (env: string, version: string) => request<unknown>("POST", "/content/promote", { env, version }),
  kinds: () => request<string[]>("GET", "/studio/kinds"),
  records: (kind: string) => request<{ kind: string; records: ContentRecord[] }>("GET", `/studio/kinds/${q(kind)}`),
  upsert: (kind: string, record: unknown, dryRun: boolean) =>
    request<Validation>("PUT", `/studio/kinds/${q(kind)}${dryRun ? "?dryRun=true" : ""}`, record),
  agentRuns: () => request<AgentRun[]>("GET", "/qa/agent-runs"),
  agentRun: (id: string) => request<string>("GET", `/qa/agent-runs/${q(id)}`, undefined, true),
};
