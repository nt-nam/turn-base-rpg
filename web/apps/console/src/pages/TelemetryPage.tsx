import { useEffect, useState, type FormEvent } from "react";
import { api, type TelemetryBucket, type TelemetryEvent, type TelemetryQuery, type TelemetrySummary } from "../api";
import { Notice, Page, Stat, Status, useAction, useLoad, when } from "../ui";
import { TelemetryChart, chartLayers, formatCount, seriesClass, type ChartLayer } from "./TelemetryChart";

const HOUR = 60 * 60 * 1000;
const PRESETS = [
  { id: "1h", label: "1 giờ", millis: HOUR },
  { id: "24h", label: "24 giờ", millis: 24 * HOUR },
  { id: "7d", label: "7 ngày", millis: 7 * 24 * HOUR },
  { id: "30d", label: "30 ngày", millis: 30 * 24 * HOUR },
] as const;
const DEFAULT_PRESET: PresetId = "24h";
const BUCKET_NAMES: Record<TelemetryBucket, string> = { minute: "phút", hour: "giờ", day: "ngày" };
const BUCKETS = Object.keys(BUCKET_NAMES) as TelemetryBucket[];
const EVENT_PAGE = 50;

type PresetId = (typeof PRESETS)[number]["id"];

export interface TelemetryView {
  range: PresetId | "custom";
  from?: number;
  to?: number;
  name?: string;
  clientVersion?: string;
  bucket?: TelemetryBucket;
}

export function telemetryPath(view: TelemetryView) {
  const parameters = new URLSearchParams({ range: view.range });
  if (view.range === "custom" && view.from !== undefined && view.to !== undefined) {
    parameters.set("from", String(view.from));
    parameters.set("to", String(view.to));
  }
  if (view.name) parameters.set("name", view.name);
  if (view.clientVersion) parameters.set("clientVersion", view.clientVersion);
  if (view.bucket) parameters.set("bucket", view.bucket);
  return `/telemetry?${parameters}`;
}

function parseView(search: string): TelemetryView {
  const parameters = new URLSearchParams(search);
  const text = (key: string) => parameters.get(key)?.trim() || undefined;
  const shared = {
    name: text("name"),
    clientVersion: text("clientVersion"),
    bucket: BUCKETS.find((bucket) => bucket === parameters.get("bucket")),
  };
  const from = Number(parameters.get("from"));
  const to = Number(parameters.get("to"));
  if (parameters.get("range") === "custom" && parameters.has("from") && parameters.has("to") && Number.isFinite(from) && Number.isFinite(to) && from < to) {
    return { range: "custom", from, to, ...shared };
  }
  return { range: PRESETS.find((preset) => preset.id === parameters.get("range"))?.id ?? DEFAULT_PRESET, ...shared };
}

function resolveRange(view: TelemetryView, now: number) {
  if (view.range === "custom" && view.from !== undefined && view.to !== undefined) return { from: view.from, to: view.to };
  const preset = PRESETS.find((entry) => entry.id === view.range) ?? PRESETS[1];
  return { from: now - preset.millis, to: now };
}

const pad = (value: number) => String(value).padStart(2, "0");

function localInput(millis: number) {
  const date = new Date(millis);
  return `${date.getFullYear()}-${pad(date.getMonth() + 1)}-${pad(date.getDate())}T${pad(date.getHours())}:${pad(date.getMinutes())}`;
}

const share = (count: number, total: number) => `${(total ? (count / total) * 100 : 0).toLocaleString("vi-VN", { maximumFractionDigits: 1 })}%`;

export function TelemetryPage({ search }: { search: string }) {
  const view = parseView(search);
  const state = useLoad(async () => {
    const range = resolveRange(view, Date.now());
    const summary = await api.telemetrySummary({ ...range, name: view.name, clientVersion: view.clientVersion, bucket: view.bucket });
    return { view, summary };
  }, [search]);

  const open = (next: TelemetryView) => {
    const path = telemetryPath(next);
    if (window.location.hash === `#${path}`) state.reload();
    else window.location.hash = path;
  };

  return (
    <Page screen="console.analytics.report_builder" title="Khám phá telemetry" actions={<button onClick={state.reload}>Làm mới</button>}>
      <TelemetryFilters view={view} summary={state.data?.summary} onChange={open} />
      <Status state={state}>{(loaded) => <TelemetryResults view={loaded.view} summary={loaded.summary} refreshing={state.loading} />}</Status>
    </Page>
  );
}

function TelemetryFilters({ view, summary, onChange }: { view: TelemetryView; summary?: TelemetrySummary; onChange: (next: TelemetryView) => void }) {
  const [customOpen, setCustomOpen] = useState(view.range === "custom");
  const [draftFrom, setDraftFrom] = useState(view.from === undefined ? "" : localInput(view.from));
  const [draftTo, setDraftTo] = useState(view.to === undefined ? "" : localInput(view.to));
  const [problem, setProblem] = useState<string>();

  useEffect(() => {
    if (view.range !== "custom" || view.from === undefined || view.to === undefined) {
      setCustomOpen(false);
      return;
    }
    setCustomOpen(true);
    setDraftFrom(localInput(view.from));
    setDraftTo(localInput(view.to));
  }, [view.range, view.from, view.to]);

  const openCustom = () => {
    const range = summary ? { from: summary.from, to: summary.to } : resolveRange(view, Date.now());
    setDraftFrom(localInput(range.from));
    setDraftTo(localInput(range.to));
    setCustomOpen(true);
  };

  const applyCustom = (event: FormEvent) => {
    event.preventDefault();
    const from = new Date(draftFrom).getTime();
    const to = new Date(draftTo).getTime();
    if (!Number.isFinite(from) || !Number.isFinite(to)) return setProblem("Nhập đủ thời điểm bắt đầu và kết thúc.");
    if (from >= to) return setProblem("Thời điểm bắt đầu phải trước thời điểm kết thúc.");
    setProblem(undefined);
    onChange({ ...view, range: "custom", from, to, bucket: undefined });
  };

  const names = summary?.availableNames ?? [];
  const nameOptions = view.name && !names.some((entry) => entry.name === view.name) ? [...names, { name: view.name, count: 0 }] : names;
  const versions = (summary?.availableClientVersions ?? []).flatMap((entry) => (entry.clientVersion ? [{ version: entry.clientVersion, count: entry.count }] : []));
  const versionOptions = view.clientVersion && !versions.some((entry) => entry.version === view.clientVersion) ? [...versions, { version: view.clientVersion, count: 0 }] : versions;

  return (
    <div className="telemetry-filters">
      <div className="filter-row">
        <div className="tabs" role="group" aria-label="Khoảng thời gian">
          {PRESETS.map((preset) => (
            <button
              key={preset.id}
              type="button"
              className={view.range === preset.id && !customOpen ? "tab-active" : undefined}
              aria-pressed={view.range === preset.id && !customOpen}
              onClick={() => {
                setCustomOpen(false);
                setProblem(undefined);
                onChange({ ...view, range: preset.id, from: undefined, to: undefined, bucket: undefined });
              }}
            >
              {preset.label}
            </button>
          ))}
          <button type="button" className={customOpen ? "tab-active" : undefined} aria-pressed={customOpen} onClick={openCustom}>
            Tùy chọn
          </button>
        </div>
        <label>
          Sự kiện
          <select name="name" value={view.name ?? ""} onChange={(event) => onChange({ ...view, name: event.target.value || undefined })}>
            <option value="">Tất cả sự kiện</option>
            {nameOptions.map((entry) => (
              <option key={entry.name} value={entry.name}>
                {entry.name} ({formatCount(entry.count)})
              </option>
            ))}
          </select>
        </label>
        <label>
          Phiên bản client
          <select name="clientVersion" value={view.clientVersion ?? ""} onChange={(event) => onChange({ ...view, clientVersion: event.target.value || undefined })}>
            <option value="">Tất cả phiên bản</option>
            {versionOptions.map((entry) => (
              <option key={entry.version} value={entry.version}>
                {entry.version} ({formatCount(entry.count)})
              </option>
            ))}
          </select>
        </label>
        <label>
          Chu kỳ
          <select name="bucket" value={view.bucket ?? ""} onChange={(event) => onChange({ ...view, bucket: BUCKETS.find((bucket) => bucket === event.target.value) })}>
            <option value="">Tự động{summary && !view.bucket ? ` (${BUCKET_NAMES[summary.bucket]})` : ""}</option>
            {BUCKETS.map((bucket) => (
              <option key={bucket} value={bucket}>
                Theo {BUCKET_NAMES[bucket]}
              </option>
            ))}
          </select>
        </label>
        {(view.name || view.clientVersion) && (
          <button type="button" onClick={() => onChange({ ...view, name: undefined, clientVersion: undefined })}>
            Bỏ lọc
          </button>
        )}
      </div>
      {customOpen && (
        <form className="filter-row" onSubmit={applyCustom}>
          <label>
            Từ
            <input type="datetime-local" name="from" value={draftFrom} onChange={(event) => setDraftFrom(event.target.value)} required />
          </label>
          <label>
            Đến
            <input type="datetime-local" name="to" value={draftTo} onChange={(event) => setDraftTo(event.target.value)} required />
          </label>
          <button className="primary">Áp dụng</button>
          <span className="muted">Giờ địa phương của trình duyệt.</span>
        </form>
      )}
      {problem && <Notice tone="error">{problem}</Notice>}
    </div>
  );
}

function TelemetryResults({ view, summary, refreshing }: { view: TelemetryView; summary: TelemetrySummary; refreshing: boolean }) {
  const layers = chartLayers(summary);
  const slotOf = (name: string): ChartLayer["slot"] => layers.find((layer) => layer.key === name)?.slot ?? "other";
  const bucketName = BUCKET_NAMES[summary.bucket];
  return (
    <div className={refreshing ? "telemetry-results refreshing" : "telemetry-results"}>
      <p className="muted">
        {when(summary.from)} → {when(summary.to)}
      </p>
      <div className="stats">
        <Stat label="Sự kiện" value={formatCount(summary.total)} />
        <Stat label="Tài khoản khác nhau" value={formatCount(summary.distinctAccounts)} />
        <Stat label="Sự kiện ẩn danh" value={formatCount(summary.anonymousEvents)} />
        <Stat label="Loại sự kiện" value={formatCount(summary.totals.length)} />
        <Stat label="Chu kỳ" value={`1 ${bucketName} · ${formatCount(summary.bucketStarts.length)} cột`} />
      </div>
      <h2>
        Số sự kiện theo {bucketName}
        {summary.bucket === "day" && <span className="muted"> (ranh giới ngày theo UTC)</span>}
      </h2>
      {summary.total === 0 ? <p className="muted">Không có sự kiện trong khoảng thời gian này.</p> : <TelemetryChart summary={summary} layers={layers} />}
      <div className="columns">
        <div>
          <h2>Tổng theo sự kiện</h2>
          {summary.totals.length === 0 ? (
            <p className="muted">Không có sự kiện.</p>
          ) : (
            <table className="telemetry-totals">
              <thead>
                <tr>
                  <th>Sự kiện</th>
                  <th className="num">Số lần</th>
                  <th className="num">Tỉ lệ</th>
                </tr>
              </thead>
              <tbody>
                {summary.totals.map((entry) => (
                  <tr key={entry.name} data-name={entry.name}>
                    <td>
                      <span className={`swatch ${seriesClass(slotOf(entry.name))}`} />
                      <a href={`#${telemetryPath({ ...view, name: entry.name })}`}>
                        <code>{entry.name}</code>
                      </a>
                    </td>
                    <td className="num">{formatCount(entry.count)}</td>
                    <td className="num">{share(entry.count, summary.total)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
        <div>
          <h2>Theo phiên bản client</h2>
          {summary.clientVersions.length === 0 ? (
            <p className="muted">Không có sự kiện.</p>
          ) : (
            <table>
              <thead>
                <tr>
                  <th>Phiên bản</th>
                  <th className="num">Số lần</th>
                  <th className="num">Tỉ lệ</th>
                </tr>
              </thead>
              <tbody>
                {summary.clientVersions.map((entry) => (
                  <tr key={entry.clientVersion ?? ""}>
                    <td>
                      {entry.clientVersion ? (
                        <a href={`#${telemetryPath({ ...view, clientVersion: entry.clientVersion })}`}>
                          <code>{entry.clientVersion}</code>
                        </a>
                      ) : (
                        <span className="muted">không rõ</span>
                      )}
                    </td>
                    <td className="num">{formatCount(entry.count)}</td>
                    <td className="num">{share(entry.count, summary.total)}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </div>
      </div>
      <h2>Sự kiện gốc</h2>
      <RawEvents
        key={`${summary.from}:${summary.to}:${view.name ?? ""}:${view.clientVersion ?? ""}`}
        query={{ from: summary.from, to: summary.to, name: view.name, clientVersion: view.clientVersion }}
      />
    </div>
  );
}

interface EventPage {
  events: TelemetryEvent[];
  nextCursor?: string;
}

function RawEvents({ query }: { query: TelemetryQuery }) {
  const first = useLoad(() => api.telemetryEvents({ ...query, limit: EVENT_PAGE }), []);
  const [more, setMore] = useState<EventPage[]>([]);
  const action = useAction();
  return (
    <Status state={first}>
      {(page) => {
        const pages = [page, ...more];
        const events = pages.flatMap((entry) => entry.events);
        const cursor = pages[pages.length - 1].nextCursor;
        if (events.length === 0) return <p className="muted">Không có sự kiện.</p>;
        return (
          <>
            <table className="telemetry-events">
              <thead>
                <tr>
                  <th>Lúc</th>
                  <th>Sự kiện</th>
                  <th>Tài khoản</th>
                  <th>Phiên bản</th>
                  <th>Payload</th>
                </tr>
              </thead>
              <tbody>
                {events.map((event) => (
                  <tr key={event.id}>
                    <td>{when(event.createdAt)}</td>
                    <td>
                      <code>{event.name}</code>
                    </td>
                    <td>{event.accountId ? <code title={event.accountId}>{event.accountId.slice(0, 8)}</code> : <span className="muted">ẩn danh</span>}</td>
                    <td>{event.clientVersion ?? <span className="muted">—</span>}</td>
                    <td>
                      <Payload value={event.payload} />
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
            <div className="table-footer">
              <span className="muted" data-shown={events.length}>
                Đang hiện {formatCount(events.length)} sự kiện{cursor ? "" : ", đã hết"}.
              </span>
              {cursor && (
                <button
                  disabled={action.busy}
                  onClick={() =>
                    action.run(async () => {
                      const next = await api.telemetryEvents({ ...query, limit: EVENT_PAGE, cursor });
                      setMore((current) => [...current, next]);
                    })
                  }
                >
                  Tải thêm
                </button>
              )}
            </div>
            {action.feedback}
          </>
        );
      }}
    </Status>
  );
}

function Payload({ value }: { value: unknown }) {
  const compact = JSON.stringify(value) ?? "";
  if (compact === "" || compact === "{}") return <span className="muted">{"{}"}</span>;
  return (
    <details className="payload">
      <summary>
        <code>{compact.length > 48 ? `${compact.slice(0, 47)}…` : compact}</code>
      </summary>
      <pre className="json-block">{JSON.stringify(value, null, 2)}</pre>
    </details>
  );
}
