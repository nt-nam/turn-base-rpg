import React, { useMemo, useState } from 'react'
import {
  useEditableFile,
  EditorBar,
  EditableField,
  EditableNumber,
  FieldRow,
  JsonPreview,
  Loading,
  ErrorBox,
  ViewHeader,
  SearchBar,
} from '../common.jsx'

const STAT_OPTIONS = ['hp', 'mp', 'atk', 'def', 'agi', 'crit']

export default function EquipView() {
  const { draft, update, dirty, loading, error, saving, save, revert } =
    useEditableFile('base/equip_base.json')
  const [q, setQ] = useState('')
  const [cat, setCat] = useState('all')

  const list = Array.isArray(draft) ? draft : []
  const categories = useMemo(() => {
    const set = new Set(list.map((e) => e.category).filter(Boolean))
    return ['all', ...Array.from(set)]
  }, [list])

  const filtered = useMemo(() => {
    const s = q.toLowerCase()
    return list
      .map((e, idx) => ({ e, idx }))
      .filter(
        ({ e }) =>
          (cat === 'all' || e.category === cat) &&
          (!s ||
            (e.name || '').toLowerCase().includes(s) ||
            (e.nameRegion || '').toLowerCase().includes(s))
      )
  }, [list, q, cat])

  if (loading) return <Loading />
  if (error) return <ErrorBox message={error} />

  const setField = (idx, field, val) => update((d) => { d[idx][field] = val })
  const setStat = (idx, key, val) =>
    update((d) => {
      d[idx].stats = d[idx].stats || {}
      d[idx].stats[key] = val
    })
  const removeStat = (idx, key) =>
    update((d) => { delete d[idx].stats[key] })
  const addStat = (idx, key) =>
    update((d) => {
      d[idx].stats = d[idx].stats || {}
      if (!(key in d[idx].stats)) d[idx].stats[key] = 0
    })
  const addEquip = () =>
    update((d) =>
      d.push({ nameRegion: 'new_equip', name: 'Moi', category: 'weapon', show: true, currency: 'gem_pink', price: 100, stats: { atk: 0 } })
    )
  const removeEquip = (idx) => update((d) => { d.splice(idx, 1) })

  return (
    <div>
      <ViewHeader title="Trang bi" desc="Sua stats / category / gia — luu thang vao equip_base.json" />
      <EditorBar dirty={dirty} saving={saving} onSave={save} onRevert={revert}
        right={<span className="saved-hint">{list.length} trang bi</span>} />
      <div className="toolbar">
        <SearchBar value={q} onChange={setQ} placeholder="Tim trang bi…" />
        {categories.map((c) => (
          <button key={c} className={`chip ${cat === c ? 'active' : ''}`} onClick={() => setCat(c)}>{c}</button>
        ))}
        <button className="btn small" onClick={addEquip}>+ Them trang bi</button>
      </div>
      <div className="grid">
        {filtered.map(({ e, idx }) => (
          <div className="card" key={idx}>
            <FieldRow label="Ten"><EditableField value={e.name} onChange={(v) => setField(idx, 'name', v)} /></FieldRow>
            <FieldRow label="Region"><EditableField value={e.nameRegion} onChange={(v) => setField(idx, 'nameRegion', v)} /></FieldRow>
            <FieldRow label="Category"><EditableField value={e.category} onChange={(v) => setField(idx, 'category', v)} /></FieldRow>
            <FieldRow label="Currency"><EditableField value={e.currency} onChange={(v) => setField(idx, 'currency', v)} /></FieldRow>
            <FieldRow label="Gia"><EditableNumber value={e.price} onChange={(v) => setField(idx, 'price', v)} /></FieldRow>
            <FieldRow label="Show">
              <input type="checkbox" checked={!!e.show} onChange={(ev) => setField(idx, 'show', ev.target.checked)} />
            </FieldRow>
            <div style={{ marginTop: 8, fontSize: 12, color: 'var(--text-dim)' }}>Stats:</div>
            {Object.entries(e.stats || {}).map(([k, v]) => (
              <div className="reward-row" key={k}>
                <span className="pill" style={{ minWidth: 46 }}>{k}</span>
                <EditableNumber value={v} onChange={(val) => setStat(idx, k, val)} />
                <button className="btn danger small" onClick={() => removeStat(idx, k)}>×</button>
              </div>
            ))}
            <select className="efield" style={{ width: 140, marginTop: 4 }} value=""
              onChange={(ev) => ev.target.value && addStat(idx, ev.target.value)}>
              <option value="">+ them stat…</option>
              {STAT_OPTIONS.filter((s) => !(e.stats && s in e.stats)).map((s) => (
                <option value={s} key={s}>{s}</option>
              ))}
            </select>
            <div>
              <button className="btn danger small" style={{ marginTop: 8 }} onClick={() => removeEquip(idx)}>Xoa</button>
            </div>
          </div>
        ))}
      </div>
      <div style={{ marginTop: 16 }}><JsonPreview data={draft} /></div>
    </div>
  )
}
