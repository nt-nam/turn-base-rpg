import { useCallback, useEffect, useState, type ReactNode } from "react";
import type { WebScreenId } from "@pxworld/screen-catalog";
import { ApiError, type Validation } from "./api";

export function Page({ screen, title, actions, children }: { screen: WebScreenId; title: string; actions?: ReactNode; children: ReactNode }) {
  useEffect(() => {
    document.title = `${title} · PxWorld Console`;
  }, [title]);
  return (
    <section className="page" data-screen-id={screen}>
      <header className="page-head">
        <h1>{title}</h1>
        {actions && <div className="page-actions">{actions}</div>}
      </header>
      {children}
    </section>
  );
}

export interface Loaded<T> {
  data: T | undefined;
  error: string | undefined;
  loading: boolean;
  reload: () => void;
}

export function useLoad<T>(load: () => Promise<T>, deps: unknown[]): Loaded<T> {
  const [data, setData] = useState<T>();
  const [error, setError] = useState<string>();
  const [loading, setLoading] = useState(true);
  const [tick, setTick] = useState(0);
  useEffect(() => {
    let live = true;
    setLoading(true);
    load()
      .then((value) => live && (setData(value), setError(undefined)))
      .catch((failure: unknown) => live && setError(messageOf(failure)))
      .finally(() => live && setLoading(false));
    return () => {
      live = false;
    };
  }, [...deps, tick]);
  const reload = useCallback(() => setTick((value) => value + 1), []);
  return { data, error, loading, reload };
}

export const messageOf = (failure: unknown) => (failure instanceof Error ? failure.message : String(failure));

export function Status<T>({ state, children }: { state: Loaded<T>; children: (data: T) => ReactNode }) {
  if (state.error) return <Notice tone="error">{state.error}</Notice>;
  if (state.data === undefined) return <p className="muted">Đang tải…</p>;
  return <>{children(state.data)}</>;
}

export function Notice({ tone, children }: { tone: "error" | "ok" | "info"; children: ReactNode }) {
  return (
    <div className={`notice notice-${tone}`} role={tone === "error" ? "alert" : "status"}>
      {children}
    </div>
  );
}

export function useAction() {
  const [busy, setBusy] = useState(false);
  const [result, setResult] = useState<{ tone: "ok" | "error"; text: string; validation?: Validation }>();
  const run = useCallback(async (work: () => Promise<string | void>) => {
    setBusy(true);
    setResult(undefined);
    try {
      const text = await work();
      if (text) setResult({ tone: "ok", text });
    } catch (failure) {
      setResult({ tone: "error", text: messageOf(failure), validation: failure instanceof ApiError ? failure.validation : undefined });
    } finally {
      setBusy(false);
    }
  }, []);
  const feedback = result && (
    <Notice tone={result.tone}>
      {result.text}
      {result.validation && <IssueList validation={result.validation} />}
    </Notice>
  );
  return { busy, run, feedback, setResult };
}

export function IssueList({ validation }: { validation: Validation }) {
  return (
    <div className="issues">
      <p>
        {validation.errors} lỗi · {validation.warnings} cảnh báo · {validation.records} bản ghi
      </p>
      {validation.issues.length > 0 && (
        <ul>
          {validation.issues.slice(0, 50).map((issue, index) => (
            <li key={index} className={issue.severity === "ERROR" ? "issue-error" : "issue-warning"}>
              <code>{issue.recordId}</code> {issue.message}
            </li>
          ))}
        </ul>
      )}
    </div>
  );
}

export const when = (millis?: number) => (millis ? new Date(millis).toLocaleString("vi-VN") : "—");

export function Stat({ label, value, tone }: { label: string; value: ReactNode; tone?: "warn" }) {
  return (
    <div className={`stat ${tone === "warn" ? "stat-warn" : ""}`}>
      <span className="stat-value">{value}</span>
      <span className="stat-label">{label}</span>
    </div>
  );
}

export const link = (path: string) => `#${path}`;
