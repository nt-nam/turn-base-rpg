import { api } from "../api";
import { Page, Stat, Status, useLoad } from "../ui";

export function DashboardPage() {
  const state = useLoad(api.dashboard, []);
  return (
    <Page screen="console.dashboards.overview" title="Tổng quan" actions={<button onClick={state.reload}>Làm mới</button>}>
      <Status state={state}>
        {(data) => {
          const events = Object.entries(data.telemetryLastDay).sort(([, a], [, b]) => b - a);
          const rejectRate = data.battleValidations ? Math.round((data.rejectedBattles / data.battleValidations) * 1000) / 10 : 0;
          return (
            <>
              <div className="stats">
                <Stat label="Môi trường" value={data.env} />
                <Stat label="Content đang phát" value={<code>{data.contentVersion ?? "—"}</code>} />
                <Stat label="Người chơi" value={data.players} />
                <Stat label="Tài khoản" value={data.accounts} />
                <Stat label="Cloud save" value={data.saves} />
                <Stat label="Trận đã xác thực" value={data.battleValidations} />
                <Stat label="Trận bị từ chối" value={`${data.rejectedBattles} (${rejectRate}%)`} tone={rejectRate > 5 ? "warn" : undefined} />
                <Stat label="Lượt chạy agent" value={data.agentRuns} />
              </div>
              <h2>Telemetry 24 giờ qua</h2>
              {events.length === 0 ? (
                <p className="muted">Chưa có sự kiện.</p>
              ) : (
                <table>
                  <thead>
                    <tr>
                      <th>Sự kiện</th>
                      <th className="num">Số lần</th>
                    </tr>
                  </thead>
                  <tbody>
                    {events.map(([name, count]) => (
                      <tr key={name}>
                        <td>
                          <code>{name}</code>
                        </td>
                        <td className="num">{count}</td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              )}
            </>
          );
        }}
      </Status>
    </Page>
  );
}
