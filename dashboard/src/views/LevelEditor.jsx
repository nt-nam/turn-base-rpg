import React, { useEffect, useMemo, useState } from 'react'
import {
  useToast,
  useEditableFile,
  EditorBar,
  EditableNumber,
  FieldRow,
  JsonPreview,
  Loading,
  ErrorBox,
  ViewHeader,
  clone,
} from '../common.jsx'
import { listFiles, loadAll, createFile, deleteFile } from '../dataLoader.js'

const COLS = 3
const ROWS = 3

// Danh sach file man = cac file trong thu muc enemy/
function useEnemyFiles() {
  const [files, setFiles] = useState([])
  const [loading, setLoading] = useState(true)
  const reload = () =>
    listFiles()
      .then((all) => {
        setFiles(all.filter((f) => f.startsWith('enemy/')).sort())
        setLoading(false)
      })
      .catch(() => setLoading(false))
  useEffect(() => {
    reload()
  }, [])
  return { files, loading, reload }
}

// Danh sach nhan vat de dat lam quai
function useCharacterOptions() {
  const [opts, setOpts] = useState([])
  useEffect(() => {
    loadAll({
      base: 'base/character_base.json',
      heroes: 'enemies_data.json',
    }).then((d) => {
      const set = new Map()
      if (Array.isArray(d.base)) d.base.forEach((c) => set.set(c.nameRegion, c.name))
      if (Array.isArray(d.heroes))
        d.heroes.forEach((c) => set.set(c.characterId, c.name))
      setOpts(Array.from(set, ([id, name]) => ({ id, name })))
    })
  }, [])
  return opts
}

export default function LevelEditor() {
  const toast = useToast()
  const { files, loading: filesLoading, reload: reloadFiles } = useEnemyFiles()
  const charOptions = useCharacterOptions()
  const [selected, setSelected] = useState(null)

  // Chon file dau tien khi co danh sach
  useEffect(() => {
    if (!selected && files.length) setSelected(files[0])
  }, [files, selected])

  const newStage = async () => {
    const name = prompt('Ten file man moi (vd: wasteland1_6):')
    if (!name) return
    const rel = `enemy/${name.replace(/\.json$/, '')}.json`
    try {
      await createFile(rel, { grid: [], reward: [] })
      toast('Da tao ' + rel, 'ok')
      reloadFiles()
      setSelected(rel)
    } catch (e) {
      toast('Loi tao file: ' + e.message, 'err')
    }
  }

  return (
    <div>
      <ViewHeader title="Level Editor" desc="Build man dau: dat quai vao luoi 3x3, chinh level/star, reward" />
      <div className="row-flex">
        <div style={{ width: 240, flex: 'none' }}>
          <div style={{ display: 'flex', gap: 6, marginBottom: 8 }}>
            <button className="btn small primary" onClick={newStage}>+ Man moi</button>
            <button className="btn small" onClick={reloadFiles}>Tai lai</button>
          </div>
          {filesLoading ? (
            <Loading />
          ) : (
            <div className="list-select">
              {files.map((f) => (
                <div
                  key={f}
                  className={`li ${selected === f ? 'active' : ''}`}
                  onClick={() => setSelected(f)}
                >
                  {f.replace('enemy/', '').replace('.json', '')}
                  <div className="sub">{f}</div>
                </div>
              ))}
              {files.length === 0 && <div className="li">Chua co man nao</div>}
            </div>
          )}
        </div>

        <div className="col">
          {selected ? (
            <StageEditor
              key={selected}
              relPath={selected}
              charOptions={charOptions}
              onDeleted={() => {
                setSelected(null)
                reloadFiles()
              }}
            />
          ) : (
            <div className="loading">Chon mot man de sua</div>
          )}
        </div>
      </div>
    </div>
  )
}

function StageEditor({ relPath, charOptions, onDeleted }) {
  const toast = useToast()
  const { draft, update, dirty, loading, error, saving, save, revert } =
    useEditableFile(relPath)
  const [selCell, setSelCell] = useState(null) // "i,j"

  const grid = draft?.grid || []
  const reward = draft?.reward || []

  const cellMap = useMemo(() => {
    const m = {}
    grid.forEach((g) => {
      m[g.grid] = g
    })
    return m
  }, [grid])

  if (loading) return <Loading />
  if (error) return <ErrorBox message={error} />

  const setCellChar = (key, nameRegion) => {
    update((d) => {
      d.grid = d.grid || []
      const idx = d.grid.findIndex((g) => g.grid === key)
      if (!nameRegion) {
        // xoa
        if (idx >= 0) d.grid.splice(idx, 1)
        return
      }
      if (idx >= 0) {
        d.grid[idx].nameRegion = nameRegion
      } else {
        d.grid.push({ grid: key, characterId: 'enemy', nameRegion, star: 0, level: 1 })
      }
    })
  }

  const setCellField = (key, field, val) => {
    update((d) => {
      const g = d.grid.find((x) => x.grid === key)
      if (g) g[field] = val
    })
  }

  const clearCell = (key) => {
    update((d) => {
      const idx = d.grid.findIndex((g) => g.grid === key)
      if (idx >= 0) d.grid.splice(idx, 1)
    })
    if (selCell === key) setSelCell(null)
  }

  // Reward ops
  const addReward = () =>
    update((d) => {
      d.reward = d.reward || []
      d.reward.push({ type: 'coin', id: 'coin', quantity: 1 })
    })
  const setReward = (i, field, val) =>
    update((d) => {
      d.reward[i][field] = val
    })
  const removeReward = (i) =>
    update((d) => {
      d.reward.splice(i, 1)
    })

  const del = async () => {
    if (!confirm(`Xoa man ${relPath}? (se backup truoc khi xoa)`)) return
    try {
      await deleteFile(relPath)
      toast('Da xoa ' + relPath, 'ok')
      onDeleted()
    } catch (e) {
      toast('Loi xoa: ' + e.message, 'err')
    }
  }

  const nameOf = (id) => charOptions.find((c) => c.id === id)?.name || id

  const selData = selCell ? cellMap[selCell] : null

  return (
    <div>
      <EditorBar
        dirty={dirty}
        saving={saving}
        onSave={save}
        onRevert={revert}
        right={<button className="btn danger small" onClick={del}>Xoa man</button>}
      />

      <div className="row-flex">
        {/* Grid: hien row j=2 tren cung, j=0 duoi (khop toa do game, y huong len) */}
        <div>
          <div
            className="lv-grid"
            style={{ gridTemplateColumns: `repeat(${COLS}, auto)` }}
          >
            {Array.from({ length: ROWS }).map((_, rIdx) => {
              const j = ROWS - 1 - rIdx // hang tren = j lon
              return Array.from({ length: COLS }).map((__, i) => {
                const key = `${i},${j}`
                const cell = cellMap[key]
                return (
                  <div
                    key={key}
                    className={`lv-cell ${cell ? 'filled' : ''} ${
                      selCell === key ? 'selected' : ''
                    }`}
                    onClick={() => setSelCell(key)}
                  >
                    <span className="coord">{key}</span>
                    {cell && (
                      <button
                        className="cell-clear"
                        onClick={(e) => {
                          e.stopPropagation()
                          clearCell(key)
                        }}
                      >
                        ×
                      </button>
                    )}
                    {cell ? (
                      <>
                        <span className="cell-name">{nameOf(cell.nameRegion)}</span>
                        <span className="cell-meta">Lv{cell.level} ★{cell.star}</span>
                      </>
                    ) : (
                      <span>trong</span>
                    )}
                  </div>
                )
              })
            })}
          </div>
          <p className="saved-hint" style={{ marginTop: 6 }}>
            Toa do "cot,hang". Hang 0 o duoi (khop layout trong game).
          </p>
        </div>

        {/* Panel chinh o dang chon */}
        <div className="col">
          <div className="panel">
            <h3>O dang chon: {selCell || '—'}</h3>
            {selCell ? (
              <>
                <FieldRow label="Quai">
                  <select
                    className="efield"
                    value={selData?.nameRegion || ''}
                    onChange={(e) => setCellChar(selCell, e.target.value)}
                  >
                    <option value="">— trong —</option>
                    {charOptions.map((c) => (
                      <option value={c.id} key={c.id}>
                        {c.name} ({c.id})
                      </option>
                    ))}
                  </select>
                </FieldRow>
                {selData && (
                  <>
                    <FieldRow label="Level">
                      <EditableNumber
                        value={selData.level}
                        onChange={(v) => setCellField(selCell, 'level', v)}
                      />
                    </FieldRow>
                    <FieldRow label="Star">
                      <EditableNumber
                        value={selData.star}
                        onChange={(v) => setCellField(selCell, 'star', v)}
                      />
                    </FieldRow>
                  </>
                )}
              </>
            ) : (
              <p className="saved-hint">Bam vao mot o de dat quai.</p>
            )}
          </div>

          <div className="panel">
            <h3>Phan thuong (reward)</h3>
            {reward.map((r, i) => (
              <div className="reward-row" key={i}>
                <input
                  className="efield"
                  value={r.type || ''}
                  placeholder="type"
                  onChange={(e) => setReward(i, 'type', e.target.value)}
                />
                <input
                  className="efield"
                  value={r.id || ''}
                  placeholder="id"
                  onChange={(e) => setReward(i, 'id', e.target.value)}
                />
                <EditableNumber
                  value={r.quantity}
                  onChange={(v) => setReward(i, 'quantity', v)}
                />
                <button className="btn danger small" onClick={() => removeReward(i)}>×</button>
              </div>
            ))}
            <button className="btn small" onClick={addReward}>+ Them reward</button>
          </div>

          <JsonPreview data={draft} />
        </div>
      </div>
    </div>
  )
}
