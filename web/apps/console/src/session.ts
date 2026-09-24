import type { Account, Session } from "./api";

const KEY = "pxworld.console.session";

export const Roles = {
  admin: "staff_admin",
  support: "staff_support",
  liveops: "staff_liveops",
  creator: "staff_creator",
  qa: "staff_qa",
  dev: "staff_dev",
} as const;

export function loadSession(): Session | null {
  try {
    const stored = localStorage.getItem(KEY);
    if (!stored) return null;
    const session = JSON.parse(stored) as Session;
    return tokenExpired(session.token) ? null : session;
  } catch {
    return null;
  }
}

export function storeSession(session: Session | null) {
  try {
    if (session) localStorage.setItem(KEY, JSON.stringify(session));
    else localStorage.removeItem(KEY);
  } catch {
    /* storage unavailable; session lives in memory only */
  }
}

function tokenExpired(token: string): boolean {
  try {
    const payload = JSON.parse(atob(token.split(".")[1].replace(/-/g, "+").replace(/_/g, "/")));
    return typeof payload.exp === "number" && payload.exp * 1000 < Date.now();
  } catch {
    return true;
  }
}

export const hasAny = (account: Account, ...roles: string[]) =>
  account.roles.includes(Roles.admin) || roles.some((role) => account.roles.includes(role));

export const isStaff = (account: Account) => account.roles.some((role) => role.startsWith("staff_"));
