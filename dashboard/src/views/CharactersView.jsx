import React, { useMemo, useState } from 'react'
import {
  useEditableFile,
  EditorBar,
  EditableField,
  EditableNumber,
  FieldRow,
  TagInput,
  JsonPreview,
  Loading,
  ErrorBox,
  ViewHeader,
  SearchBar,
} from '../common.jsx'

const FILES = [
  { rel: 'base/character_base.json', label: 'Class base (character_base)' },
  { rel: 'enemies_data.json', label: 'Danh sach hero (enemies_data)' },
]

const STAT_KEYS = ['hp', 'mp', 'atk', 'def', 'agi', 'crit']

export default function CharactersView() {
  const [file, setFile] = useState(FILES[0].rel)
  return (
    <div>
      <ViewHeader title="Nhan vat" desc="Sua stats / skill / counter — luu thang vao file" />
      <div className="toolbar">
        {FILES.map((f) => (
          <button
            key={f.rel}
            className={`chip ${file === f.rel ? 'active' : ''}`}
            onClick={() => setFile(f.rel)}
          >
            {f.label}
          </button>
        ))}
      </div>
      <CharacterEditor key={file} relPath={file} />
    </div>
  )
}

function CharacterEditor({ relPath }) {
  const { draft, update, dirty, loading, error, saving, save, revert } =
    useEditableFile(relPath)
  const [q, setQ] = useState('')

  const list = Array.isArray(draft) ? draft : []
  const idKey = list[0] && 'characterId' in list[0] ? 'characterId' : 'nameRegion'

  const filtered = useMemo(() => {
    const s = q.toLowerCase()
    return list
      .map((c, idx) => ({ c, idx }))
      .filter(
        ({ c }) =>
          !s ||
          (c.name || '').toLowerCase().includes(s) ||
          (c[idKey] || '').toLowerCase().includes(s) ||
          (c.classType || c.type || '').toLowerCase().includes(s)
      )
  }, [list, q, idKey])

  if (loading) return <Loading />
  if (error) return <ErrorBox message={error} />

  const setField = (idx, field, val) =>
    update((d) => {
      d[idx][field] = val
    })

  const addChar = () =>
    update((d) => {
      const base = { name: 'Moi', [idKey]: 'new_id_' + d.length }
      STAT_KEYS.forEach((k) => (base[k] = 0))
      base.skills = []
      d.push(base)
    })

  const removeChar = (idx) =>
    update((d) => {
      d.splice(idx, 1)
    })

  return (
    <div>
      <EditorBar
        dirty={dirty}
        saving={saving}
        onSave={save}
        onRevert={revert}
        right={
          <span className="saved-hint">{list.length} nhan vat · {relPath}</span>
        }
      />
      <div className="toolbar">
        <SearchBar value={q} onChange={setQ} placeholder="Tim theo ten / id / class…" />
        <button className="btn small" onClick={addChar}>+ Them nhan vat</button>
      </div>

      <div className="grid">
        {filtered.map(({ c, idx }) => (
          <div className="card" key={idx}>
            <FieldRow label="Ten">
              <EditableField value={c.name} onChange={(v) => setField(idx, 'name', v)} />
            </FieldRow>
            <FieldRow label="ID">
              <EditableField value={c[idKey]} onChange={(v) => setField(idx, idKey, v)} />
            </FieldRow>
            <FieldRow label={c.classType != null ? 'classType' : 'type'}>
              <EditableField
                value={c.classType ?? c.type}
                onChange={(v) => setField(idx, c.classType != null ? 'classType' : 'type', v)}
              />
            </FieldRow>
            {'level' in c && (
              <FieldRow label="Level">
                <EditableNumber value={c.level} onChange={(v) => setField(idx, 'level', v)} />
              </FieldRow>
            )}
            {STAT_KEYS.map((k) =>
              k in c ? (
                <FieldRow label={k.toUpperCase()} key={k}>
                  <EditableNumber value={c[k]} onChange={(v) => setField(idx, k, v)} />
                </FieldRow>
              ) : null
            )}
            {'skills' in c && (
              <FieldRow label="Skills">
                <TagInput
                  items={c.skills || []}
                  cls="accent"
                  onChange={(v) => setField(idx, 'skills', v)}
                />
              </FieldRow>
            )}
            {'counters' in c && (
              <FieldRow label="Counters">
                <TagInput
                  items={c.counters || []}
                  cls="good"
                  onChange={(v) => setField(idx, 'counters', v)}
                />
              </FieldRow>
            )}
            {'weakAgainst' in c && (
              <FieldRow label="Weak vs">
                <TagInput
                  items={c.weakAgainst || []}
                  cls="bad"
                  onChange={(v) => setField(idx, 'weakAgainst', v)}
                />
              </FieldRow>
            )}
            {'desc' in c && (
              <FieldRow label="Desc">
                <EditableField value={c.desc} onChange={(v) => setField(idx, 'desc', v)} />
              </FieldRow>
            )}
            <button
              className="btn danger small"
              style={{ marginTop: 8 }}
              onClick={() => removeChar(idx)}
            >
              Xoa
            </button>
          </div>
        ))}
      </div>

      <div style={{ marginTop: 16 }}>
        <JsonPreview data={draft} />
      </div>
    </div>
  )
}
