import React, { useState } from 'react'
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
} from '../common.jsx'

export default function MissionsView() {
  const [tab, setTab] = useState('missions')
  return (
    <div>
      <ViewHeader title="Nhiem vu & Thanh tuu" desc="Sua title / target / reward — luu thang vao file" />
      <div className="toolbar">
        <button className={`chip ${tab === 'missions' ? 'active' : ''}`} onClick={() => setTab('missions')}>
          Nhiem vu (mission_base)
        </button>
        <button className={`chip ${tab === 'ach' ? 'active' : ''}`} onClick={() => setTab('ach')}>
          Thanh tuu (achievement)
        </button>
      </div>
      {tab === 'missions' ? <Missions /> : <Achievements />}
    </div>
  )
}

function Missions() {
  const { draft, update, dirty, loading, error, saving, save, revert } =
    useEditableFile('base/mission_base.json')
  const list = Array.isArray(draft) ? draft : []
  if (loading) return <Loading />
  if (error) return <ErrorBox message={error} />

  const setField = (idx, f, v) => update((d) => { d[idx][f] = v })
  const setReward = (idx, ri, f, v) => update((d) => { d[idx].rewards[ri][f] = v })
  const addReward = (idx) =>
    update((d) => {
      d[idx].rewards = d[idx].rewards || []
      d[idx].rewards.push({ nameRegion: 'reward_x', type: 'item', quantity: 1 })
    })
  const removeReward = (idx, ri) => update((d) => { d[idx].rewards.splice(ri, 1) })
  const addMission = () =>
    update((d) =>
      d.push({ idBase: 'mission_x', title: 'Moi', description: '', progress: 0, targetAmount: 1, rewards: [] })
    )
  const removeMission = (idx) => update((d) => { d.splice(idx, 1) })

  return (
    <div>
      <EditorBar dirty={dirty} saving={saving} onSave={save} onRevert={revert}
        right={<span className="saved-hint">{list.length} mission</span>} />
      <button className="btn small" style={{ marginBottom: 10 }} onClick={addMission}>+ Them nhiem vu</button>
      <div className="grid">
        {list.map((m, idx) => (
          <div className="card" key={idx}>
            <FieldRow label="ID"><EditableField value={m.idBase} onChange={(v) => setField(idx, 'idBase', v)} /></FieldRow>
            <FieldRow label="Title"><EditableField value={m.title} onChange={(v) => setField(idx, 'title', v)} /></FieldRow>
            <FieldRow label="Mo ta"><EditableField value={m.description} onChange={(v) => setField(idx, 'description', v)} /></FieldRow>
            <FieldRow label="Target"><EditableNumber value={m.targetAmount} onChange={(v) => setField(idx, 'targetAmount', v)} /></FieldRow>
            <div style={{ marginTop: 8, fontSize: 12, color: 'var(--text-dim)' }}>Rewards:</div>
            {(m.rewards || []).map((r, ri) => (
              <div className="reward-row" key={ri}>
                <input className="efield" value={r.type || ''} placeholder="type"
                  onChange={(e) => setReward(idx, ri, 'type', e.target.value)} />
                <input className="efield" value={r.nameRegion || ''} placeholder="nameRegion"
                  onChange={(e) => setReward(idx, ri, 'nameRegion', e.target.value)} />
                <EditableNumber value={r.quantity} onChange={(v) => setReward(idx, ri, 'quantity', v)} />
                <button className="btn danger small" onClick={() => removeReward(idx, ri)}>×</button>
              </div>
            ))}
            <button className="btn small" onClick={() => addReward(idx)}>+ reward</button>
            <div><button className="btn danger small" style={{ marginTop: 8 }} onClick={() => removeMission(idx)}>Xoa mission</button></div>
          </div>
        ))}
      </div>
      <div style={{ marginTop: 16 }}><JsonPreview data={draft} /></div>
    </div>
  )
}

function Achievements() {
  const { draft, update, dirty, loading, error, saving, save, revert } =
    useEditableFile('base/achievement.json')
  const list = Array.isArray(draft) ? draft : []
  if (loading) return <Loading />
  if (error) return <ErrorBox message={error} />

  const setField = (idx, f, v) => update((d) => { d[idx][f] = v })

  return (
    <div>
      <EditorBar dirty={dirty} saving={saving} onSave={save} onRevert={revert}
        right={<span className="saved-hint">{list.length} thanh tuu</span>} />
      <div className="grid">
        {list.map((a, idx) => (
          <div className="card" key={idx}>
            <FieldRow label="ID"><EditableField value={a.idBase} onChange={(v) => setField(idx, 'idBase', v)} /></FieldRow>
            <FieldRow label="Ten"><EditableField value={a.name} onChange={(v) => setField(idx, 'name', v)} /></FieldRow>
            <FieldRow label="Mo ta"><EditableField value={a.dec} onChange={(v) => setField(idx, 'dec', v)} /></FieldRow>
            {'number' in a && (
              <FieldRow label="Number"><EditableNumber value={a.number} onChange={(v) => setField(idx, 'number', v)} /></FieldRow>
            )}
          </div>
        ))}
      </div>
      <div style={{ marginTop: 16 }}><JsonPreview data={draft} /></div>
    </div>
  )
}
