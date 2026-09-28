import { useCallback, useEffect, useState, type ReactNode } from "react";
import { onUnauthorized, setToken, type Account, type Session } from "./api";
import { Roles, hasAny, loadSession, storeSession } from "./session";
import { Page } from "./ui";
import { LoginPage } from "./pages/LoginPage";
import { DashboardPage } from "./pages/DashboardPage";
import { PlayersPage } from "./pages/PlayersPage";
import { PlayerDetailPage } from "./pages/PlayerDetailPage";
import { AgentRunDetailPage, AgentRunsPage, AuditPage, ContentReleasesPage, StaffPage } from "./pages/OperationsPages";
import { StudioHomePage, StudioKindPage } from "./pages/StudioPages";
import { TelemetryPage } from "./pages/TelemetryPage";

interface NavEntry {
  path: string;
  label: string;
  roles: string[];
}

const TELEMETRY_READERS = [Roles.liveops, Roles.dev, Roles.qa];

const NAV: NavEntry[] = [
  { path: "/", label: "Tổng quan", roles: Object.values(Roles) },
  { path: "/players", label: "Người chơi", roles: [Roles.support, Roles.liveops] },
  { path: "/audit", label: "Nhật ký", roles: [Roles.support, Roles.liveops] },
  { path: "/content", label: "Phát hành", roles: [Roles.liveops, Roles.dev, Roles.creator, Roles.qa] },
  { path: "/telemetry", label: "Telemetry", roles: TELEMETRY_READERS },
  { path: "/studio", label: "Studio", roles: [Roles.creator, Roles.dev] },
  { path: "/qa/runs", label: "Test agent", roles: [Roles.qa, Roles.dev] },
  { path: "/staff", label: "Staff", roles: [Roles.admin] },
];

function useHashPath() {
  const read = () => window.location.hash.replace(/^#/, "") || "/";
  const [path, setPath] = useState(read);
  useEffect(() => {
    const update = () => setPath(read());
    window.addEventListener("hashchange", update);
    return () => window.removeEventListener("hashchange", update);
  }, []);
  return path;
}

export function App() {
  const [session, setSession] = useState<Session | null>(() => {
    const stored = loadSession();
    setToken(stored?.token ?? null);
    return stored;
  });
  const [expired, setExpired] = useState(false);
  const path = useHashPath();

  const signIn = useCallback((next: Session | null) => {
    setToken(next?.token ?? null);
    storeSession(next);
    setSession(next);
  }, []);

  useEffect(() => {
    const unsubscribe = onUnauthorized(() => {
      setExpired(true);
      signIn(null);
    });
    return () => {
      unsubscribe();
    };
  }, [signIn]);

  if (!session)
    return (
      <LoginPage
        expired={expired}
        onSignedIn={(next) => {
          setExpired(false);
          signIn(next);
        }}
      />
    );

  const account = session.account;
  return (
    <div className="shell">
      <nav className="sidebar" aria-label="Điều hướng chính">
        <div className="brand">PxWorld</div>
        <ul>
          {NAV.filter((entry) => hasAny(account, ...entry.roles)).map((entry) => (
            <li key={entry.path}>
              <a href={`#${entry.path}`} className={isActive(path, entry.path) ? "active" : ""}>
                {entry.label}
              </a>
            </li>
          ))}
        </ul>
        <div className="who">
          <strong>{account.displayName}</strong>
          <span className="muted">{account.roles.map((role) => role.replace("staff_", "")).join(", ")}</span>
          <button onClick={() => signIn(null)}>Đăng xuất</button>
        </div>
      </nav>
      <main className="content">{route(path, account)}</main>
    </div>
  );
}

const withoutQuery = (path: string) => path.split("?")[0];

const isActive = (current: string, target: string) =>
  target === "/" ? withoutQuery(current) === "/" : withoutQuery(current) === target || current.startsWith(`${target}/`);

function guard(account: Account, roles: string[], page: ReactNode) {
  if (hasAny(account, ...roles)) return page;
  return (
    <Page screen="console.auth.no_access" title="Không có quyền">
      <p>Vai trò hiện tại không mở được trang này.</p>
    </Page>
  );
}

function route(path: string, account: Account): ReactNode {
  const parts = withoutQuery(path).split("/").filter(Boolean).map(decodeURIComponent);
  const support = [Roles.support, Roles.liveops];
  switch (parts[0]) {
    case undefined:
      return <DashboardPage canExploreTelemetry={hasAny(account, ...TELEMETRY_READERS)} />;
    case "players":
      return guard(account, support, parts[1] ? <PlayerDetailPage id={parts[1]} viewer={account} /> : <PlayersPage />);
    case "audit":
      return guard(account, support, <AuditPage />);
    case "content":
      return guard(
        account,
        [Roles.liveops, Roles.dev, Roles.creator, Roles.qa],
        <ContentReleasesPage canPublish={hasAny(account, Roles.liveops, Roles.dev)} canPromote={hasAny(account, Roles.liveops)} />,
      );
    case "studio":
      return guard(account, [Roles.creator, Roles.dev], parts[1] ? <StudioKindPage kind={parts[1]} selected={parts[2]} /> : <StudioHomePage />);
    case "qa":
      return guard(account, [Roles.qa, Roles.dev], parts[2] ? <AgentRunDetailPage id={parts[2]} /> : <AgentRunsPage />);
    case "telemetry":
      return guard(account, TELEMETRY_READERS, <TelemetryPage search={path.slice(withoutQuery(path).length + 1)} />);
    case "staff":
      return guard(account, [Roles.admin], <StaffPage />);
    default:
      return (
        <Page screen="console.auth.no_access" title="Không tìm thấy">
          <p>
            Không có trang <code>{path}</code>.
          </p>
        </Page>
      );
  }
}
