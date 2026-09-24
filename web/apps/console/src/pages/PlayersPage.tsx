import { useState, type FormEvent } from "react";
import { api, type Account } from "../api";
import { Notice, Page, link, messageOf, when } from "../ui";

export function PlayersPage() {
  const [query, setQuery] = useState("");
  const [results, setResults] = useState<Account[]>();
  const [error, setError] = useState<string>();

  const search = async (event: FormEvent) => {
    event.preventDefault();
    setError(undefined);
    try {
      setResults(await api.players(query));
    } catch (failure) {
      setError(messageOf(failure));
    }
  };

  return (
    <Page screen="console.players.player_search" title="Tìm người chơi">
      <form className="inline-form" onSubmit={search}>
        <input placeholder="Email, tên hiển thị hoặc ID" value={query} onChange={(event) => setQuery(event.target.value)} aria-label="Từ khoá" />
        <button className="primary">Tìm</button>
      </form>
      {error && <Notice tone="error">{error}</Notice>}
      {results && results.length === 0 && <p className="muted">Không có kết quả.</p>}
      {results && results.length > 0 && (
        <table>
          <thead>
            <tr>
              <th>Tên</th>
              <th>Email</th>
              <th>Loại</th>
              <th>Tạo lúc</th>
              <th>Trạng thái</th>
            </tr>
          </thead>
          <tbody>
            {results.map((account) => (
              <tr key={account.id}>
                <td>
                  <a href={link(`/players/${account.id}`)}>{account.displayName}</a>
                </td>
                <td>{account.email ?? "—"}</td>
                <td>{account.kind}</td>
                <td>{when(account.createdAt)}</td>
                <td>{banState(account)}</td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </Page>
  );
}

export const banState = (account: Account) =>
  account.bannedUntil && account.bannedUntil > Date.now() ? <span className="badge badge-bad">Khoá tới {when(account.bannedUntil)}</span> : <span className="badge">Bình thường</span>;
