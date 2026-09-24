import { useState, type FormEvent } from "react";
import { api, type Account } from "../api";
import { Roles, hasAny } from "../session";
import { Page, Status, useAction, useLoad, when } from "../ui";
import { banState } from "./PlayersPage";

export function PlayerDetailPage({ id, viewer }: { id: string; viewer: Account }) {
  const state = useLoad(() => api.player(id), [id]);
  return (
    <Page screen="console.players.player_overview" title="Hồ sơ người chơi" actions={<a href="#/players">← Tìm kiếm</a>}>
      <Status state={state}>
        {(detail) => (
          <>
            <dl className="facts">
              <dt>Tên</dt>
              <dd>{detail.account.displayName}</dd>
              <dt>Email</dt>
              <dd>{detail.account.email ?? "khách"}</dd>
              <dt>ID</dt>
              <dd>
                <code>{detail.account.id}</code>
              </dd>
              <dt>Tạo lúc</dt>
              <dd>{when(detail.account.createdAt)}</dd>
              <dt>Trạng thái</dt>
              <dd>{banState(detail.account)}</dd>
            </dl>

            <div className="columns">
              <div data-screen-id="console.players.player_saves">
                <h2>Cloud save</h2>
                {detail.saves.length === 0 ? (
                  <p className="muted">Chưa có save.</p>
                ) : (
                  <table>
                    <thead>
                      <tr>
                        <th>Slot</th>
                        <th className="num">Revision</th>
                        <th className="num">Kích thước</th>
                        <th>Cập nhật</th>
                      </tr>
                    </thead>
                    <tbody>
                      {detail.saves.map((save) => (
                        <tr key={save.slot}>
                          <td>{save.slot}</td>
                          <td className="num">{save.revision}</td>
                          <td className="num">{(save.bytes / 1024).toFixed(1)} KB</td>
                          <td>{when(save.updatedAt)}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                )}
                <h2>Thư</h2>
                {detail.mail.length === 0 ? (
                  <p className="muted">Không có thư.</p>
                ) : (
                  <ul className="plain">
                    {detail.mail.map((mail) => (
                      <li key={mail.id}>
                        <strong>{mail.subject}</strong> <code>{mail.grants}</code> {mail.claimed ? <span className="badge">đã nhận</span> : <span className="badge badge-info">chờ nhận</span>}
                      </li>
                    ))}
                  </ul>
                )}
              </div>
              <div>
                {hasAny(viewer, Roles.support, Roles.liveops) && <GrantForm id={id} onDone={state.reload} />}
                {hasAny(viewer, Roles.support) && <SanctionForm account={detail.account} onDone={state.reload} />}
              </div>
            </div>

            <h2 data-screen-id="console.players.player_audit">Nhật ký thao tác</h2>
            <AuditTable entries={detail.audit} />
          </>
        )}
      </Status>
    </Page>
  );
}

function GrantForm({ id, onDone }: { id: string; onDone: () => void }) {
  const [subject, setSubject] = useState("Quà hỗ trợ");
  const [itemId, setItemId] = useState("currency.gold");
  const [amount, setAmount] = useState(100);
  const [reason, setReason] = useState("");
  const action = useAction();

  const submit = (event: FormEvent) => {
    event.preventDefault();
    action.run(async () => {
      await api.grant(id, subject, { [itemId]: amount }, reason);
      setReason("");
      onDone();
      return "Đã gửi thư kèm quà.";
    });
  };

  return (
    <form className="card" onSubmit={submit} data-screen-id="console.players.player_grant">
      <h2>Tặng qua thư</h2>
      <label>
        Tiêu đề
        <input value={subject} onChange={(event) => setSubject(event.target.value)} required />
      </label>
      <div className="row">
        <label>
          ID vật phẩm / tiền tệ
          <input value={itemId} onChange={(event) => setItemId(event.target.value)} required />
        </label>
        <label>
          Số lượng
          <input type="number" min={1} value={amount} onChange={(event) => setAmount(Number(event.target.value))} required />
        </label>
      </div>
      <label>
        Lý do (bắt buộc, ghi vào audit)
        <input name="reason" value={reason} onChange={(event) => setReason(event.target.value)} required />
      </label>
      <button className="primary" disabled={action.busy}>
        Gửi
      </button>
      {action.feedback}
    </form>
  );
}

function SanctionForm({ account, onDone }: { account: Account; onDone: () => void }) {
  const [hours, setHours] = useState(24);
  const [reason, setReason] = useState("");
  const action = useAction();
  const banned = !!account.bannedUntil && account.bannedUntil > Date.now();

  const submit = (event: FormEvent) => {
    event.preventDefault();
    action.run(async () => {
      if (banned) await api.lift(account.id, reason);
      else await api.sanction(account.id, hours, reason);
      setReason("");
      onDone();
      return banned ? "Đã gỡ khoá." : "Đã khoá tài khoản.";
    });
  };

  return (
    <form className="card" onSubmit={submit} data-screen-id="console.players.player_sanctions">
      <h2>{banned ? "Gỡ khoá" : "Khoá tài khoản"}</h2>
      {!banned && (
        <label>
          Số giờ
          <input type="number" min={1} max={8760} value={hours} onChange={(event) => setHours(Number(event.target.value))} required />
        </label>
      )}
      <label>
        Lý do (bắt buộc, ghi vào audit)
        <input name="reason" value={reason} onChange={(event) => setReason(event.target.value)} required />
      </label>
      <button className={banned ? "primary" : "danger"} disabled={action.busy}>
        {banned ? "Gỡ khoá" : "Khoá"}
      </button>
      {action.feedback}
    </form>
  );
}

export function AuditTable({ entries }: { entries: { id: string; actorId: string; action: string; target: string; reason: string; createdAt: number }[] }) {
  if (entries.length === 0) return <p className="muted">Chưa có thao tác nào.</p>;
  return (
    <table>
      <thead>
        <tr>
          <th>Lúc</th>
          <th>Hành động</th>
          <th>Đối tượng</th>
          <th>Người làm</th>
          <th>Lý do</th>
        </tr>
      </thead>
      <tbody>
        {entries.map((entry) => (
          <tr key={entry.id}>
            <td>{when(entry.createdAt)}</td>
            <td>
              <code>{entry.action}</code>
            </td>
            <td>
              <code>{entry.target}</code>
            </td>
            <td>
              <code>{entry.actorId.slice(0, 8)}</code>
            </td>
            <td>{entry.reason}</td>
          </tr>
        ))}
      </tbody>
    </table>
  );
}
