import React, { useState } from 'react'
import {
  useEditableFile,
  EditorBar,
  EditableNumber,
  FieldRow,
  JsonPreview,
  Loading,
  ErrorBox,
  ViewHeader,
} from '../common.jsx'

export default function ConfigView() {
  const [tab, setTab] = useState('battle')
  return (
    <div>
      <ViewHeader title="Config" desc="Sua thong so he thong — luu thang vao file config" />
      <div className="toolbar">
        <button className={`chip ${tab === 'battle' ? 'active' : ''}`} onClick={() => setTab('battle')}>
          battle_config
        </button>
        <button className={`chip ${tab === 'item' ? 'active' : ''}`} onClick={() => setTab('item')}>
          itemConfig
        </button>
      </div>
      {tab === 'battle' ? <BattleConfig /> : <ItemConfig />}
    </div>
  )
}

function BattleConfig() {
  const { draft, update, dirty, loading, error, saving, save, revert } =
    useEditableFile('config/battle_config.json')
  if (loading) return <Loading />
  if (error) return <ErrorBox message={error} />
  const obj = draft && typeof draft === 'object' && !Array.isArray(draft) ? draft : {}

  const setKey = (k, v) => update((d) => { d[k] = v })

  return (
    <div>
      <EditorBar dirty={dirty} saving={saving} onSave={save} onRevert={revert} />
      <div className="panel" style={{ maxWidth: 420 }}>
        {Object.entries(obj).map(([k, v]) => (
          <FieldRow label={k} key={k}>
            <EditableNumber value={v} step={0.01} onChange={(val) => setKey(k, val)} />
          </FieldRow>
        ))}
      </div>
      <JsonPreview data={draft} />
    </div>
  )
}

function ItemConfig() {
  const { draft, update, dirty, loading, error, saving, save, revert } =
    useEditableFile('config/itemConfig.json')
  if (loading) return <Loading />
  if (error) return <ErrorBox message={error} />

  // itemConfig la mang chua 1 object: [ { food:{tier1:..}, metal:{...} } ]
  const root = Array.isArray(draft) ? draft[0] || {} : draft || {}

  const setVal = (group, tier, val) =>
    update((d) => {
      const target = Array.isArray(d) ? d[0] : d
      target[group][tier] = val
    })

  return (
    <div>
      <EditorBar dirty={dirty} saving={saving} onSave={save} onRevert={revert} />
      <div className="grid">
        {Object.entries(root).map(([group, tiers]) => (
          <div className="card" key={group}>
            <div className="card-title" style={{ textTransform: 'capitalize' }}>{group}</div>
            {Object.entries(tiers).map(([tier, v]) => (
              <FieldRow label={tier} key={tier}>
                <EditableNumber value={v} onChange={(val) => setVal(group, tier, val)} />
              </FieldRow>
            ))}
          </div>
        ))}
      </div>
      <div style={{ marginTop: 16 }}><JsonPreview data={draft} /></div>
    </div>
  )
}
