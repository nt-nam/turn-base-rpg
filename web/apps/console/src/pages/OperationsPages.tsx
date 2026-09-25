import { useState, type FormEvent } from "react";
import { api, type Release } from "../api";
import { Roles } from "../session";
import { Page, Status, useAction, useLoad, when } from "../ui";
import { AuditTable } from "./PlayerDetailPage";

const ENVIRONMENTS = ["dev", "qa", "staging", "prod"];

export function AuditPage() {
  const [target, setTarget] = useState("");
  const [applied, setApplied] = useState("");
  const state = useLoad(() => api.audit(applied || undefined), [applied]);
  return (
    <Page screen="console.operations.audit_log" title="Nhật ký thao tác">
      <form
        className="inline-form"
        onSubmit={(event) => {
          event.preventDefault();
          setApplied(target.trim());
        }}
      >
        <input placeholder="Lọc theo đối tượng, ví dụ account:<id>" value={target} onChange={(event) => setTarget(event.target.value)} aria-label="Đối tượng" />
        <button>Lọc</button>
      </form>
      <Status state={state}>{(entries) => <AuditTable entries={entries} />}</Status>
    </Page>
  );
}

export function ContentReleasesPage({ canPromote, canPublish }: { canPromote: boolean; canPublish: boolean }) {
  const releases = useLoad(api.releases, []);
  const channels = useLoad(api.channels, []);
  const action = useAction();
  const reload = () => {
    releases.reload();
    channels.reload();
  };

  const publish = () =>
    action.run(async () => {
      const { id } = await api.publish();
      reload();
      return `Đã đóng gói content ${id}.`;
    });

  return (
    <Page screen="console.operations.deployments" title="Phát hành content" actions={canPublish && <button className="primary" onClick={publish} disabled={action.busy}>Đóng gói content hiện tại</button>}>
      {action.feedback}
      <h2>Kênh</h2>
      <Status state={channels}>
        {(map) => (
          <div className="stats">
            {ENVIRONMENTS.map((env) => (
              <div className="stat" key={env}>
                <span className="stat-value">
                  <code>{map[env] ?? "—"}</code>
                </span>
                <span className="stat-label">{env}</span>
              </div>
            ))}
          </div>
        )}
      </Status>
      {releases.data && releases.data.length > 1 && <ReleaseDiff releases={releases.data} channels={channels.data ?? {}} />}
      <h2>Bản phát hành</h2>
      <Status state={releases}>
        {(rows) => (
          <table>
            <thead>
              <tr>
                <th>Phiên bản</th>
                <th className="num">Bản ghi</th>
                <th className="num">Kích thước</th>
                <th>Tạo lúc</th>
                <th>Đang ở</th>
                {canPromote && <th>Đẩy lên</th>}
              </tr>
            </thead>
            <tbody>
              {rows.map((release) => (
                <tr key={release.version}>
                  <td>
                    <code title={release.sha256}>{release.version}</code>
                  </td>
                  <td className="num">{release.records}</td>
                  <td className="num">{(release.bytes / 1024).toFixed(0)} KB</td>
                  <td>{when(release.createdAt)}</td>
                  <td>
                    {Object.entries(channels.data ?? {})
                      .filter(([, version]) => version === release.version)
                      .map(([env]) => (
                        <span key={env} className="badge badge-info">
                          {env}
                        </span>
                      ))}
                  </td>
                  {canPromote && (
                    <td className="buttons">
                      {ENVIRONMENTS.filter((env) => channels.data?.[env] !== release.version).map((env) => (
                        <button
                          key={env}
                          disabled={action.busy}
                          onClick={() =>
                            action.run(async () => {
                              if (env === "prod" && !window.confirm(`Đẩy ${release.version} lên prod?`)) return;
                              await api.promote(env, release.version);
                              reload();
                              return `${release.version} đã lên ${env}.`;
                            })
                          }
                        >
                          {env}
                        </button>
                      ))}
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Status>
    </Page>
  );
}

function ReleaseDiff({ releases, channels }: { releases: Release[]; channels: Record<string, string> }) {
  const [from, setFrom] = useState(channels.prod ?? releases[releases.length - 1].version);
  const [to, setTo] = useState(releases[0].version);
  const state = useLoad(() => api.diff(from, to), [from, to]);
  const pick = (value: string, change: (next: string) => void, label: string) => (
    <select value={value} onChange={(event) => change(event.target.value)} aria-label={label}>
      {releases.map((release) => (
        <option key={release.version} value={release.version}>
          {release.version}
          {Object.entries(channels)
            .filter(([, version]) => version === release.version)
            .map(([env]) => ` · ${env}`)
            .join("")}
        </option>
      ))}
    </select>
  );
  return (
    <section className="card" data-screen-id="console.operations.content_release_diff">
      <h2>So sánh hai bản</h2>
      <div className="field-row">
        {pick(from, setFrom, "Bản gốc")}
        <span>→</span>
        {pick(to, setTo, "Bản mới")}
      </div>
      <Status state={state}>
        {(diff) =>
          diff.kinds.length === 0 && diff.tables.length === 0 ? (
            <p className="muted">Hai bản giống hệt nhau.</p>
          ) : (
            <div className="diff">
              {diff.kinds.map((kind) => (
                <div key={kind.kind}>
                  <h3>
                    {kind.kind} <span className="muted">+{kind.added.length} −{kind.removed.length} ~{kind.changed.length}</span>
                  </h3>
                  <ul className="plain">
                    {kind.added.map((id) => (
                      <li key={`a${id}`} className="diff-added">
                        + <code>{id}</code>
                      </li>
                    ))}
                    {kind.removed.map((id) => (
                      <li key={`r${id}`} className="diff-removed">
                        − <code>{id}</code>
                      </li>
                    ))}
                    {kind.changed.map((record) => (
                      <li key={`c${record.id}`}>
                        ~ <code>{record.id}</code> <span className="muted">{record.fields.join(", ")}</span>
                      </li>
                    ))}
                  </ul>
                </div>
              ))}
              {diff.tables.map((table) => (
                <p key={table.table}>
                  <code>{table.table}</code>{" "}
                  <span className="muted">
                    +{table.added} −{table.removed} ~{table.changed}
                    {table.sample.length > 0 && ` · ${table.sample.slice(0, 5).join(", ")}`}
                  </span>
                </p>
              ))}
            </div>
          )
        }
      </Status>
    </section>
  );
}

export function AgentRunsPage() {
  const state = useLoad(api.agentRuns, []);
  return (
    <Page screen="qa.quality.agent_runs" title="Lượt chạy test agent" actions={<button onClick={state.reload}>Làm mới</button>}>
      <p className="muted">
        Tải báo cáo lên bằng <code>node tools/test-agent/run.mjs --upload=http://localhost:8080 --token=…</code>
      </p>
      <Status state={state}>
        {(runs) =>
          runs.length === 0 ? (
            <p className="muted">Chưa có lượt chạy.</p>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>Lúc</th>
                  <th>Chế độ</th>
                  <th>Kết quả</th>
                  <th className="num">Màn đã thăm</th>
                  <th className="num">% catalog launch</th>
                </tr>
              </thead>
              <tbody>
                {runs.map((run) => (
                  <tr key={run.id}>
                    <td>
                      <a href={`#/qa/runs/${run.id}`}>{when(run.createdAt)}</a>
                    </td>
                    <td>{run.mode}</td>
                    <td>
                      <span className={`badge ${run.status === "passed" ? "badge-ok" : "badge-bad"}`}>{run.status}</span>
                    </td>
                    <td className="num">
                      {run.visited}/{run.registered}
                    </td>
                    <td className="num">
                      <Meter percent={run.launchPercent} />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          )
        }
      </Status>
    </Page>
  );
}

function Meter({ percent }: { percent: number }) {
  return (
    <span className="meter" title={`${percent}%`}>
      <span className="meter-fill" style={{ width: `${Math.min(100, percent)}%` }} />
      <span className="meter-text">{percent}%</span>
    </span>
  );
}

interface AgentReport {
  mode: string;
  status: string;
  scenario?: string;
  error?: string | null;
  startedAt?: string;
  finishedAt?: string;
  coverage?: { visitedCount: number; registeredCount: number; launchPercent: number; registeredNotVisited?: string[]; visited?: string[] };
  trace?: { method?: string; ok?: boolean }[];
}

export function AgentRunDetailPage({ id }: { id: string }) {
  const state = useLoad(async () => JSON.parse(await api.agentRun(id)) as AgentReport, [id]);
  return (
    <Page screen="qa.quality.agent_run_detail" title="Chi tiết lượt chạy" actions={<a href="#/qa/runs">← Danh sách</a>}>
      <Status state={state}>
        {(report) => (
          <>
            <dl className="facts">
              <dt>Chế độ</dt>
              <dd>
                {report.mode}
                {report.scenario ? ` · ${report.scenario}` : ""}
              </dd>
              <dt>Kết quả</dt>
              <dd>
                <span className={`badge ${report.status === "passed" ? "badge-ok" : "badge-bad"}`}>{report.status}</span>
              </dd>
              <dt>Thời gian</dt>
              <dd>
                {report.startedAt ?? "—"} → {report.finishedAt ?? "—"}
              </dd>
              <dt>Độ phủ</dt>
              <dd>
                {report.coverage ? `${report.coverage.visitedCount}/${report.coverage.registeredCount} màn đăng ký · ${report.coverage.launchPercent}% catalog launch` : "—"}
              </dd>
              <dt>Lệnh automation</dt>
              <dd>{report.trace?.length ?? 0}</dd>
            </dl>
            {report.error && <pre className="error-block">{report.error}</pre>}
            {report.coverage?.registeredNotVisited && report.coverage.registeredNotVisited.length > 0 && (
              <>
                <h2>Màn đã đăng ký nhưng chưa thăm</h2>
                <ul className="chips">
                  {report.coverage.registeredNotVisited.map((screen) => (
                    <li key={screen}>
                      <code>{screen}</code>
                    </li>
                  ))}
                </ul>
              </>
            )}
          </>
        )}
      </Status>
    </Page>
  );
}

const STAFF_ROLES = Object.values(Roles);

export function StaffPage() {
  const [email, setEmail] = useState("");
  const [displayName, setDisplayName] = useState("");
  const [password, setPassword] = useState("");
  const [roles, setRoles] = useState<string[]>([Roles.support]);
  const action = useAction();

  const submit = (event: FormEvent) => {
    event.preventDefault();
    action.run(async () => {
      const account = await api.createStaff(email, password, displayName, roles);
      setEmail("");
      setPassword("");
      setDisplayName("");
      return `Đã tạo ${account.displayName} (${account.roles.join(", ")}).`;
    });
  };

  return (
    <Page screen="console.liveops_entities.staff_users.editor" title="Thêm staff">
      <form className="card narrow" onSubmit={submit}>
        <label>
          Email
          <input type="email" value={email} onChange={(event) => setEmail(event.target.value)} required />
        </label>
        <label>
          Tên hiển thị
          <input value={displayName} onChange={(event) => setDisplayName(event.target.value)} required />
        </label>
        <label>
          Mật khẩu tạm (≥ 10 ký tự)
          <input type="password" minLength={10} value={password} onChange={(event) => setPassword(event.target.value)} required autoComplete="new-password" />
        </label>
        <fieldset>
          <legend>Vai trò</legend>
          {STAFF_ROLES.map((role) => (
            <label key={role} className="check">
              <input
                type="checkbox"
                checked={roles.includes(role)}
                onChange={(event) => setRoles(event.target.checked ? [...roles, role] : roles.filter((item) => item !== role))}
              />
              {role.replace("staff_", "")}
            </label>
          ))}
        </fieldset>
        <button className="primary" disabled={action.busy || roles.length === 0}>
          Tạo
        </button>
        {action.feedback}
      </form>
    </Page>
  );
}
