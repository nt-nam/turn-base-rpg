import React, { useState } from 'react'
import { ToastProvider } from './common.jsx'
import LevelEditor from './views/LevelEditor.jsx'
import CharactersView from './views/CharactersView.jsx'
import SkillsView from './views/SkillsView.jsx'
import ItemsView from './views/ItemsView.jsx'
import EquipView from './views/EquipView.jsx'
import MissionsView from './views/MissionsView.jsx'
import ConfigView from './views/ConfigView.jsx'

const TABS = [
  { id: 'level', label: 'Level Editor', icon: '🗺️', comp: LevelEditor },
  { id: 'characters', label: 'Nhan vat', icon: '🛡️', comp: CharactersView },
  { id: 'skills', label: 'Ky nang', icon: '✨', comp: SkillsView },
  { id: 'items', label: 'Item', icon: '🧪', comp: ItemsView },
  { id: 'equip', label: 'Trang bi', icon: '⚔️', comp: EquipView },
  { id: 'missions', label: 'Nhiem vu', icon: '📜', comp: MissionsView },
  { id: 'config', label: 'Config', icon: '⚙️', comp: ConfigView },
]

export default function App() {
  const [active, setActive] = useState('level')
  const ActiveComp = TABS.find((t) => t.id === active).comp

  return (
    <ToastProvider>
      <div className="app">
        <aside className="sidebar">
          <h1>LVpxW Studio</h1>
          <p className="subtitle">Game Content Studio — sua & luu thang vao assets/data</p>
          <nav>
            {TABS.map((t) => (
              <button
                key={t.id}
                className={`nav-item ${active === t.id ? 'active' : ''}`}
                onClick={() => setActive(t.id)}
              >
                <span className="icon">{t.icon}</span>
                {t.label}
              </button>
            ))}
          </nav>
        </aside>
        <main className="main">
          <ActiveComp />
        </main>
      </div>
    </ToastProvider>
  )
}
