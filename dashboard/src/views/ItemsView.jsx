import React, { useMemo, useState } from 'react'
import {
  useEditableFile,
  EditorBar,
  EditableField,
  EditableNumber,
  JsonPreview,
  Loading,
  ErrorBox,
  ViewHeader,
  SearchBar,
} from '../common.jsx'

export default function ItemsView() {
  const { draft, update, dirty, loading, error, saving, save, revert } =
    useEditableFile('base/items_base.json')
  const [q, setQ] = useState('')

  const list = Array.isArray(draft) ? draft : []
  const filtered = useMemo(() => {
    const s = q.toLowerCase()
    return list
      .map((it, idx) => ({ it, idx }))
      .filter(
        ({ it }) =>
          !s ||
          (it.name || '').toLowerCase().includes(s) ||
          (it.nameRegion || '').toLowerCase().includes(s) ||
          (it.detail || '').toLowerCase().includes(s)
      )
  }, [list, q])

  if (loading) return <Loading />
  if (error) return <ErrorBox message={error} />

  const setField = (idx, field, val) => update((d) => { d[idx][field] = val })
  const addItem = () =>
    update((d) =>
      d.push({ nameRegion: 'new_item', name: 'Moi', tier: 1, show: true, currency: 'coin', price: 100 })
    )
  const removeItem = (idx) => update((d) => { d.splice(idx, 1) })

  return (
    <div>
      <ViewHeader title="Item" desc="Sua inline — luu thang vao items_base.json" />
      <EditorBar dirty={dirty} saving={saving} onSave={save} onRevert={revert}
        right={<span className="saved-hint">{list.length} item</span>} />
      <div className="toolbar">
        <SearchBar value={q} onChange={setQ} placeholder="Tim item…" />
        <button className="btn small" onClick={addItem}>+ Them item</button>
      </div>
      <table className="table">
        <thead>
          <tr>
            <th>Ten</th><th>ID/Region</th><th>Tier</th><th>Currency</th>
            <th>Gia</th><th>Show</th><th>Detail</th><th></th>
          </tr>
        </thead>
        <tbody>
          {filtered.map(({ it, idx }) => (
            <tr key={idx}>
              <td><EditableField value={it.name} onChange={(v) => setField(idx, 'name', v)} /></td>
              <td><EditableField value={it.nameRegion} onChange={(v) => setField(idx, 'nameRegion', v)} /></td>
              <td><EditableNumber value={it.tier} onChange={(v) => setField(idx, 'tier', v)} /></td>
              <td><EditableField value={it.currency} onChange={(v) => setField(idx, 'currency', v)} /></td>
              <td><EditableNumber value={it.price} onChange={(v) => setField(idx, 'price', v)} /></td>
              <td>
                <input type="checkbox" checked={!!it.show}
                  onChange={(e) => setField(idx, 'show', e.target.checked)} />
              </td>
              <td><EditableField value={it.detail} onChange={(v) => setField(idx, 'detail', v)} /></td>
              <td><button className="btn danger small" onClick={() => removeItem(idx)}>×</button></td>
            </tr>
          ))}
        </tbody>
      </table>
      <div style={{ marginTop: 16 }}><JsonPreview data={draft} /></div>
    </div>
  )
}
