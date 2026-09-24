import { useState, type FormEvent } from "react";
import { api, type Session } from "../api";
import { isStaff } from "../session";
import { Notice, messageOf } from "../ui";

export function LoginPage({ onSignedIn, expired }: { onSignedIn: (session: Session) => void; expired: boolean }) {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState<string>();
  const [busy, setBusy] = useState(false);

  const submit = async (event: FormEvent) => {
    event.preventDefault();
    setBusy(true);
    setError(undefined);
    try {
      const session = await api.login(email, password);
      if (!isStaff(session.account)) setError("Tài khoản này không có quyền vào Console.");
      else onSignedIn(session);
    } catch (failure) {
      setError(messageOf(failure));
    } finally {
      setBusy(false);
    }
  };

  return (
    <main className="login" data-screen-id="console.auth.login">
      <form className="login-card" onSubmit={submit}>
        <h1>PxWorld Console</h1>
        <p className="muted">Đăng nhập bằng tài khoản staff.</p>
        {expired && <Notice tone="info">Phiên đã hết hạn, hãy đăng nhập lại.</Notice>}
        {error && <Notice tone="error">{error}</Notice>}
        <label>
          Email
          <input type="email" autoComplete="username" value={email} onChange={(event) => setEmail(event.target.value)} required />
        </label>
        <label>
          Mật khẩu
          <input type="password" autoComplete="current-password" value={password} onChange={(event) => setPassword(event.target.value)} required />
        </label>
        <button className="primary" disabled={busy}>
          {busy ? "Đang đăng nhập…" : "Đăng nhập"}
        </button>
      </form>
    </main>
  );
}
