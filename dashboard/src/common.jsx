import React, {
  createContext,
  useCallback,
  useContext,
  useEffect,
  useMemo,
  useRef,
  useState,
} from 'react'
import { loadFile, saveFile } from './dataLoader.js'

/* ---------------- Toast ---------------- */
const ToastCtx = createContext(() => {})
export function useToast() {
  return useContext(ToastCtx)
}
export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([])
  const push = useCallback((message, type = 'info') => {
    const id = Math.random().toString(36).slice(2)
    setToasts((t) => [...t, { id, message, type }])
    setTimeout(() => setToasts((t) => t.filter((x) => x.id !== id)), 3200)
  }, [])
  return (
    <ToastCtx.Provider value={push}>
      {children}
      <div className="toast-wrap">
        {toasts.map((t) => (
          <div key={t.id} className={`toast ${t.type}`}>
            {t.message}
          </div>
        ))}
      </div>
    </ToastCtx.Provider>
  )
}

/* ---------------- Deep clone / equal ---------------- */
export const clone = (o) => JSON.parse(JSON.stringify(o))
const eq = (a, b) => JSON.stringify(a) === JSON.stringify(b)

/* ----------------
 * useEditableFile: load 1 file, giu ban nhap (draft), theo doi dirty,
 * cung cap save/revert. `data` la draft co the sua truc tiep.
 * ---------------- */
export function useEditableFile(relPath) {
  const toast = useToast()
  const [original, setOriginal] = useState(null)
  const [draft, setDraft] = useState(null)
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [saving, setSaving] = useState(false)

  const reload = useCallback(() => {
    setLoading(true)
    setError(null)
    loadFile(relPath)
      .then((d) => {
        setOriginal(d)
        setDraft(clone(d))
        setLoading(false)
      })
      .catch((e) => {
        setError(e.message)
        setLoading(false)
      })
  }, [relPath])

  useEffect(() => {
    reload()
  }, [reload])

  const dirty = useMemo(
    () => original != null && draft != null && !eq(original, draft),
    [original, draft]
  )

  // Cap nhat draft: nhan ham updater (nhan ban clone hien tai)
  const update = useCallback((fn) => {
    setDraft((prev) => {
      const next = clone(prev)
      const r = fn(next)
      return r === undefined ? next : r
    })
  }, [])

  const revert = useCallback(() => {
    setDraft(clone(original))
    toast('Da hoan tac thay doi', 'info')
  }, [original, toast])

  const save = useCallback(async () => {
    setSaving(true)
    try {
      const res = await saveFile(relPath, draft)
      setOriginal(clone(draft))
      toast(`Da luu ${relPath}` + (res.backup ? ' (da backup)' : ''), 'ok')
    } catch (e) {
      toast('Loi luu: ' + e.message, 'err')
    } finally {
      setSaving(false)
    }
  }, [relPath, draft, toast])

  return { draft, setDraft, update, original, dirty, loading, error, saving, save, revert, reload }
}

/* ---------------- View header ---------------- */
export function ViewHeader({ title, desc }) {
  return (
    <div className="view-header">
      <h2>{title}</h2>
      {desc && <span className="desc">{desc}</span>}
    </div>
  )
}

export function Loading() {
  return <div className="loading">Dang tai du lieu…</div>
}
export function ErrorBox({ message }) {
  return <div className="error">Loi: {message}</div>
}

/* ---------------- Editor bar (Save / Revert) ---------------- */
export function EditorBar({ dirty, saving, onSave, onRevert, right }) {
  return (
    <div className="editor-bar">
      <button className="btn primary" disabled={!dirty || saving} onClick={onSave}>
        {saving ? 'Dang luu…' : 'Luu (Save)'}
      </button>
      <button className="btn" disabled={!dirty} onClick={onRevert}>
        Hoan tac (Revert)
      </button>
      {dirty ? (
        <span className="saved-hint">
          <span className="dirty-dot" /> Co thay doi chua luu
        </span>
      ) : (
        <span className="saved-hint">Da luu</span>
      )}
      <span style={{ marginLeft: 'auto' }}>{right}</span>
    </div>
  )
}

/* ---------------- Editable fields ---------------- */
export function EditableField({ value, onChange, placeholder, style }) {
  return (
    <input
      className="efield"
      value={value ?? ''}
      placeholder={placeholder}
      style={style}
      onChange={(e) => onChange(e.target.value)}
    />
  )
}

export function EditableNumber({ value, onChange, step = 1 }) {
  return (
    <input
      className="efield num"
      type="number"
      step={step}
      value={value ?? 0}
      onChange={(e) => {
        const v = e.target.value
        onChange(v === '' ? 0 : Number(v))
      }}
    />
  )
}

export function FieldRow({ label, children }) {
  return (
    <div className="field-row">
      <label>{label}</label>
      {children}
    </div>
  )
}

/* Tag input: mang chuoi (skills, counters, weakAgainst) */
export function TagInput({ items = [], onChange, suggestions = [], cls = '' }) {
  const [val, setVal] = useState('')
  const add = (v) => {
    v = v.trim()
    if (v && !items.includes(v)) onChange([...items, v])
    setVal('')
  }
  return (
    <div className="tag-input">
      {items.map((it) => (
        <span className={`tag ${cls}`} key={it}>
          {it}
          <button onClick={() => onChange(items.filter((x) => x !== it))}>×</button>
        </span>
      ))}
      <input
        className="efield"
        style={{ width: 120 }}
        list="tag-suggestions"
        value={val}
        placeholder="+ them…"
        onChange={(e) => setVal(e.target.value)}
        onKeyDown={(e) => {
          if (e.key === 'Enter') add(val)
        }}
        onBlur={() => val && add(val)}
      />
      {suggestions.length > 0 && (
        <datalist id="tag-suggestions">
          {suggestions.map((s) => (
            <option value={s} key={s} />
          ))}
        </datalist>
      )}
    </div>
  )
}

/* ---------------- JSON preview ---------------- */
export function JsonPreview({ data }) {
  const [open, setOpen] = useState(false)
  return (
    <div>
      <button className="btn ghost small" onClick={() => setOpen((o) => !o)}>
        {open ? 'An JSON' : 'Xem JSON'}
      </button>
      {open && <pre className="json-preview">{JSON.stringify(data, null, 2)}</pre>}
    </div>
  )
}

/* ---------------- Search ---------------- */
export function SearchBar({ value, onChange, placeholder }) {
  return (
    <input
      className="search"
      value={value}
      onChange={(e) => onChange(e.target.value)}
      placeholder={placeholder || 'Tim kiem…'}
    />
  )
}
