import express from 'express'
import fs from 'fs'
import path from 'path'
import { fileURLToPath } from 'url'

const __filename = fileURLToPath(import.meta.url)
const __dirname = path.dirname(__filename)

// dashboard/ nam trong <project>/dashboard -> DATA_ROOT = <project>/assets/data
const PROJECT_ROOT = path.resolve(__dirname, '..', '..')
const DATA_ROOT = path.join(PROJECT_ROOT, 'assets', 'data')
const BACKUP_ROOT = path.resolve(__dirname, '..', '.backups')

const PORT = process.env.PORT || 5179
const app = express()
app.use(express.json({ limit: '5mb' }))

// ---- Helpers ----

// Chuyen path tuong doi (client gui, dung dau '/') thanh path tuyet doi an toan
// trong DATA_ROOT. Chan path traversal.
function resolveSafe(relPath) {
  if (typeof relPath !== 'string' || !relPath.length) {
    throw new Error('Thieu tham so path')
  }
  // Chuan hoa: bo dau '/' dau, doi '\' thanh '/'
  const clean = relPath.replace(/\\/g, '/').replace(/^\/+/, '')
  const abs = path.resolve(DATA_ROOT, clean)
  const rootWithSep = DATA_ROOT.endsWith(path.sep) ? DATA_ROOT : DATA_ROOT + path.sep
  if (abs !== DATA_ROOT && !abs.startsWith(rootWithSep)) {
    throw new Error('Path nam ngoai assets/data: ' + relPath)
  }
  if (!abs.endsWith('.json')) {
    throw new Error('Chi cho phep file .json')
  }
  return abs
}

// Lam sach JSON "gan-chuan": bo comment // va trailing comma
function sanitizeJson(text) {
  let out = ''
  let inString = false
  let escaped = false
  for (let i = 0; i < text.length; i++) {
    const c = text[i]
    if (inString) {
      out += c
      if (escaped) escaped = false
      else if (c === '\\') escaped = true
      else if (c === '"') inString = false
      continue
    }
    if (c === '"') { inString = true; out += c; continue }
    if (c === '/' && text[i + 1] === '/') {
      while (i < text.length && text[i] !== '\n') i++
      out += '\n'; continue
    }
    if (c === ',') {
      let j = i + 1
      while (j < text.length && /\s/.test(text[j])) j++
      if (text[j] === '}' || text[j] === ']') continue
    }
    out += c
  }
  return out.trim()
}

function parseLoose(raw) {
  if (!raw.trim()) return null
  try { return JSON.parse(raw) } catch { return JSON.parse(sanitizeJson(raw)) }
}

// Liet ke tat ca file .json trong DATA_ROOT (de quy), tra path tuong doi
function listJsonFiles(dir = DATA_ROOT, base = DATA_ROOT) {
  const out = []
  for (const e of fs.readdirSync(dir, { withFileTypes: true })) {
    const p = path.join(dir, e.name)
    if (e.isDirectory()) out.push(...listJsonFiles(p, base))
    else if (e.name.endsWith('.json')) {
      out.push(path.relative(base, p).replace(/\\/g, '/'))
    }
  }
  return out
}

function backupFile(abs, relPath) {
  if (!fs.existsSync(abs)) return null
  const ts = new Date().toISOString().replace(/[:.]/g, '-')
  const dest = path.join(BACKUP_ROOT, ts, relPath)
  fs.mkdirSync(path.dirname(dest), { recursive: true })
  fs.copyFileSync(abs, dest)
  return dest
}

// ---- Routes ----

app.get('/api/health', (req, res) => {
  res.json({ ok: true, dataRoot: DATA_ROOT })
})

app.get('/api/list', (req, res) => {
  try {
    res.json({ files: listJsonFiles() })
  } catch (e) {
    res.status(500).json({ error: e.message })
  }
})

app.get('/api/file', (req, res) => {
  try {
    const abs = resolveSafe(req.query.path)
    if (!fs.existsSync(abs)) return res.status(404).json({ error: 'Khong tim thay file' })
    const raw = fs.readFileSync(abs, 'utf8')
    res.json({ path: req.query.path, data: parseLoose(raw) })
  } catch (e) {
    res.status(400).json({ error: e.message })
  }
})

// Ghi file: backup ban cu -> ghi JSON dep (indent 2)
app.put('/api/file', (req, res) => {
  try {
    const relPath = req.query.path
    const abs = resolveSafe(relPath)
    const body = req.body
    if (body === undefined || body === null || body.data === undefined) {
      return res.status(400).json({ error: 'Thieu body.data' })
    }
    // Validate JSON serialize duoc
    const text = JSON.stringify(body.data, null, 2) + '\n'
    const backup = backupFile(abs, relPath)
    fs.mkdirSync(path.dirname(abs), { recursive: true })
    fs.writeFileSync(abs, text, 'utf8')
    res.json({ ok: true, path: relPath, backup: backup ? path.relative(PROJECT_ROOT, backup) : null })
  } catch (e) {
    res.status(400).json({ error: e.message })
  }
})

// Tao file moi (khong ghi de neu da ton tai)
app.post('/api/file', (req, res) => {
  try {
    const relPath = req.query.path
    const abs = resolveSafe(relPath)
    if (fs.existsSync(abs)) return res.status(409).json({ error: 'File da ton tai' })
    const data = req.body?.data ?? []
    fs.mkdirSync(path.dirname(abs), { recursive: true })
    fs.writeFileSync(abs, JSON.stringify(data, null, 2) + '\n', 'utf8')
    res.json({ ok: true, path: relPath })
  } catch (e) {
    res.status(400).json({ error: e.message })
  }
})

// Xoa file (co backup)
app.delete('/api/file', (req, res) => {
  try {
    const relPath = req.query.path
    const abs = resolveSafe(relPath)
    if (!fs.existsSync(abs)) return res.status(404).json({ error: 'Khong tim thay file' })
    const backup = backupFile(abs, relPath)
    fs.unlinkSync(abs)
    res.json({ ok: true, backup: backup ? path.relative(PROJECT_ROOT, backup) : null })
  } catch (e) {
    res.status(400).json({ error: e.message })
  }
})

app.listen(PORT, '127.0.0.1', () => {
  console.log(`[LVpxW Studio API] http://127.0.0.1:${PORT}`)
  console.log(`  DATA_ROOT   = ${DATA_ROOT}`)
  console.log(`  BACKUP_ROOT = ${BACKUP_ROOT}`)
})
