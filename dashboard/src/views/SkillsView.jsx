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

export default function SkillsView() {
  const [tab, setTab] = useState('group')
  return (
    <div>
      <ViewHeader title="Ky nang" desc="Sua effect / type / group — luu thang vao file" />
      <div className="toolbar">
        <button className={`chip ${tab === 'group' ? 'active' : ''}`} onClick={() => setTab('group')}>
          Skill theo group (skill_data)
        </button>
        <button className={`chip ${tab === 'class' ? 'active' : ''}`} onClick={() => setTab('class')}>
          Skill theo class (skill_base)
        </button>
      </div>
      {tab === 'group' ? <GroupSkills /> : <ClassSkills />}
    </div>
  )
}

function GroupSkills() {
  const { draft, update, dirty, loading, error, saving, save, revert } =
    useEditableFile('skill_data.json')
  const [q, setQ] = useState('')

  const list = Array.isArray(draft) ? draft : []
  const filtered = useMemo(() => {
    const s = q.toLowerCase()
    return list
      .map((sk, idx) => ({ sk, idx }))
      .filter(
        ({ sk }) =>
          !s ||
          (sk.name || '').toLowerCase().includes(s) ||
          (sk.nameRegion || '').toLowerCase().includes(s) ||
          (sk.group || '').toLowerCase().includes(s) ||
          (sk.type || '').toLowerCase().includes(s)
      )
  }, [list, q])

  if (loading) return <Loading />
  if (error) return <ErrorBox message={error} />

  const setField = (idx, field, val) => update((d) => { d[idx][field] = val })
  const setEffect = (idx, ei, field, val) =>
    update((d) => { d[idx].effect[ei][field] = val })
  const addEffect = (idx) =>
    update((d) => {
      d[idx].effect = d[idx].effect || []
      d[idx].effect.push({ stat: 'atk', percent: 100, desc: '' })
    })
  const removeEffect = (idx, ei) => update((d) => { d[idx].effect.splice(ei, 1) })

  return (
    <div>
      <EditorBar dirty={dirty} saving={saving} onSave={save} onRevert={revert}
        right={<span className="saved-hint">{list.length} skill · skill_data.json</span>} />
      <div className="toolbar">
        <SearchBar value={q} onChange={setQ} placeholder="Tim skill…" />
      </div>
      <div className="grid">
        {filtered.map(({ sk, idx }) => (
          <div className="card" key={idx}>
            <FieldRow label="Ten"><EditableField value={sk.name} onChange={(v) => setField(idx, 'name', v)} /></FieldRow>
            <FieldRow label="Region"><EditableField value={sk.nameRegion} onChange={(v) => setField(idx, 'nameRegion', v)} /></FieldRow>
            <FieldRow label="Group"><EditableField value={sk.group} onChange={(v) => setField(idx, 'group', v)} /></FieldRow>
            <FieldRow label="Type"><EditableField value={sk.type} onChange={(v) => setField(idx, 'type', v)} /></FieldRow>
            <FieldRow label="Desc"><EditableField value={sk.desc} onChange={(v) => setField(idx, 'desc', v)} /></FieldRow>
            <div style={{ marginTop: 8, fontSize: 12, color: 'var(--text-dim)' }}>Effect:</div>
            {(sk.effect || []).map((e, ei) => (
              <div className="reward-row" key={ei}>
                <input className="efield" style={{ width: 70 }} value={e.stat || ''} placeholder="stat"
                  onChange={(ev) => setEffect(idx, ei, 'stat', ev.target.value)} />
                <EditableNumber value={e.percent} onChange={(v) => setEffect(idx, ei, 'percent', v)} />
                <input className="efield" value={e.desc || ''} placeholder="mo ta"
                  onChange={(ev) => setEffect(idx, ei, 'desc', ev.target.value)} />
                <button className="btn danger small" onClick={() => removeEffect(idx, ei)}>×</button>
              </div>
            ))}
            <button className="btn small" onClick={() => addEffect(idx)}>+ effect</button>
          </div>
        ))}
      </div>
      <div style={{ marginTop: 16 }}><JsonPreview data={draft} /></div>
    </div>
  )
}

function ClassSkills() {
  const { draft, update, dirty, loading, error, saving, save, revert } =
    useEditableFile('base/skill_base.json')

  const list = Array.isArray(draft) ? draft : []
  if (loading) return <Loading />
  if (error) return <ErrorBox message={error} />

  // Moi phan tu: { name, "1":{name,description,effect{...}}, "2":..., "3":... }
  const slotKeys = (cls) => Object.keys(cls).filter((k) => k !== 'name')

  const setSlotField = (idx, slot, field, val) =>
    update((d) => { d[idx][slot][field] = val })
  const setSlotEffect = (idx, slot, effKey, val) =>
    update((d) => { d[idx][slot].effect[effKey] = Number(val) })

  return (
    <div>
      <EditorBar dirty={dirty} saving={saving} onSave={save} onRevert={revert}
        right={<span className="saved-hint">{list.length} class · skill_base.json</span>} />
      <div className="grid">
        {list.map((cls, idx) => (
          <div className="card" key={idx}>
            <div className="card-title" style={{ textTransform: 'capitalize' }}>{cls.name}</div>
            {slotKeys(cls).map((slot) => (
              <div key={slot} className="panel" style={{ padding: 10, marginBottom: 8 }}>
                <div className="card-sub">Slot #{slot}</div>
                <FieldRow label="Ten">
                  <EditableField value={cls[slot].name} onChange={(v) => setSlotField(idx, slot, 'name', v)} />
                </FieldRow>
                <FieldRow label="Mo ta">
                  <EditableField value={cls[slot].description} onChange={(v) => setSlotField(idx, slot, 'description', v)} />
                </FieldRow>
                {Object.entries(cls[slot].effect || {}).map(([ek, ev]) => (
                  <FieldRow label={ek} key={ek}>
                    <EditableNumber value={ev} onChange={(v) => setSlotEffect(idx, slot, ek, v)} />
                  </FieldRow>
                ))}
              </div>
            ))}
          </div>
        ))}
      </div>
      <div style={{ marginTop: 16 }}><JsonPreview data={draft} /></div>
    </div>
  )
}
