import { useEffect, useMemo, useState } from "react";
import { webScreens, type WebScreenId } from "@pxworld/screen-catalog";
import { api, type ContentRecord } from "../api";
import { Notice, Page, Status, messageOf, useAction, useLoad } from "../ui";
import { ObjectField, defaultFor, unwrap, type Schema } from "./SchemaForm";

const registered = new Set<string>(webScreens.map((screen) => screen.id));
const entityScreen = (kind: string, view: "list" | "editor"): WebScreenId => {
  const id = `studio.content_entities.${kind}.${view}`;
  return registered.has(id) ? (id as WebScreenId) : "studio.editors.studio_home";
};

export function StudioHomePage() {
  const state = useLoad(api.kinds, []);
  return (
    <Page screen="studio.editors.studio_home" title="Studio">
      <p className="muted">Chọn loại dữ liệu để sửa. Mọi thay đổi đều chạy validator của content trước khi ghi.</p>
      <Status state={state}>
        {(kinds) => (
          <ul className="tiles">
            {kinds.map((kind) => (
              <li key={kind}>
                <a href={`#/studio/${kind}`}>{kind}</a>
              </li>
            ))}
          </ul>
        )}
      </Status>
    </Page>
  );
}

export function StudioKindPage({ kind, selected }: { kind: string; selected?: string }) {
  const state = useLoad(() => api.records(kind), [kind]);
  const schema = useLoad(() => api.schema(kind), [kind]);
  const [filter, setFilter] = useState("");
  const records = useMemo(
    () => (state.data?.records ?? []).filter((record) => JSON.stringify(record).toLowerCase().includes(filter.toLowerCase())),
    [state.data, filter],
  );
  const current = state.data?.records.find((record) => record.id === selected);

  return (
    <Page screen={entityScreen(kind, selected ? "editor" : "list")} title={`Studio · ${kind}`} actions={<a href="#/studio">← Tất cả loại</a>}>
      <Status state={state}>
        {() => (
          <div className="split">
            <aside className="record-list">
              <input placeholder="Lọc" value={filter} onChange={(event) => setFilter(event.target.value)} aria-label="Lọc bản ghi" />
              <p className="muted">{records.length} bản ghi</p>
              <ul>
                {records.map((record) => (
                  <li key={record.id}>
                    <a className={record.id === selected ? "active" : ""} href={`#/studio/${kind}/${encodeURIComponent(record.id)}`}>
                      {record.id}
                    </a>
                  </li>
                ))}
              </ul>
              <a className="button" href={`#/studio/${kind}/__new`}>
                + Bản ghi mới
              </a>
            </aside>
            <div className="record-editor">
              {selected === "__new" ? (
                <RecordEditor kind={kind} schema={schema.data} initial={{ ...(schema.data ? (defaultFor(schema.data) as object) : {}), id: `${kind.replace(/s$/, "")}.new` }} onSaved={state.reload} />
              ) : current ? (
                <RecordEditor key={current.id} kind={kind} schema={schema.data} initial={current} onSaved={state.reload} />
              ) : (
                <p className="muted">Chọn một bản ghi ở bên trái.</p>
              )}
            </div>
          </div>
        )}
      </Status>
    </Page>
  );
}

function RecordEditor({ kind, schema, initial, onSaved }: { kind: string; schema?: Schema; initial: ContentRecord; onSaved: () => void }) {
  const original = useMemo(() => JSON.stringify(initial, null, 2), [initial]);
  const [text, setText] = useState(original);
  const [mode, setMode] = useState<"form" | "json">("form");
  const formValue = useMemo(() => {
    try {
      return JSON.parse(text) as Record<string, unknown>;
    } catch {
      return undefined;
    }
  }, [text]);
  const [parseError, setParseError] = useState<string>();
  const action = useAction();

  useEffect(() => setText(original), [original]);
  const { setResult } = action;
  useEffect(() => setResult(undefined), [text, setResult]);

  const parsed = (): unknown => {
    try {
      const value = JSON.parse(text);
      setParseError(undefined);
      return value;
    } catch (failure) {
      setParseError(messageOf(failure));
      return undefined;
    }
  };

  const submit = (dryRun: boolean) => {
    const record = parsed();
    if (record === undefined) return;
    action.run(async () => {
      const validation = await api.upsert(kind, record, dryRun);
      if (!dryRun) onSaved();
      const summary = `${validation.errors} lỗi, ${validation.warnings} cảnh báo trên ${validation.records} bản ghi.`;
      return dryRun ? `Hợp lệ. ${summary}` : `Đã lưu. ${summary}`;
    });
  };

  const dirty = text !== original;
  return (
    <div className="editor">
      <div className="tabs" role="tablist">
        <button role="tab" aria-selected={mode === "form"} className={mode === "form" ? "tab-active" : ""} onClick={() => setMode("form")} disabled={!schema}>
          Biểu mẫu
        </button>
        <button role="tab" aria-selected={mode === "json"} className={mode === "json" ? "tab-active" : ""} onClick={() => setMode("json")}>
          JSON
        </button>
      </div>
      {mode === "form" && schema && formValue ? (
        <div className="schema-form">
          <ObjectField schema={unwrap(schema).schema} value={formValue} onChange={(next) => setText(JSON.stringify(next, null, 2))} path="$" />
        </div>
      ) : (
        <>
          {mode === "form" && schema && <Notice tone="info">JSON đang lỗi nên không hiện được biểu mẫu; sửa ở tab JSON.</Notice>}
          <textarea spellCheck={false} value={text} onChange={(event) => setText(event.target.value)} aria-label="JSON bản ghi" />
        </>
      )}
      {parseError && <Notice tone="error">JSON lỗi: {parseError}</Notice>}
      <div className="buttons">
        <button onClick={() => submit(true)} disabled={action.busy}>
          Kiểm tra
        </button>
        <button className="primary" onClick={() => submit(false)} disabled={action.busy || !dirty}>
          Lưu
        </button>
        <button onClick={() => setText(original)} disabled={!dirty}>
          Hoàn tác
        </button>
      </div>
      {action.feedback}
    </div>
  );
}
