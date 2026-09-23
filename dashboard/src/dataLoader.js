// Client goi backend API (/api) doc-ghi truc tiep vao assets/data.

async function req(method, url, body) {
  const opts = { method, headers: {} }
  if (body !== undefined) {
    opts.headers['Content-Type'] = 'application/json'
    opts.body = JSON.stringify(body)
  }
  const res = await fetch(url, opts)
  const json = await res.json().catch(() => ({}))
  if (!res.ok) throw new Error(json.error || `HTTP ${res.status}`)
  return json
}

// Doc 1 file JSON trong assets/data (path tuong doi, vd 'base/character_base.json')
export async function loadFile(relPath) {
  const json = await req('GET', `/api/file?path=${encodeURIComponent(relPath)}`)
  return json.data
}

// Ghi 1 file (ghi de, backend tu backup)
export async function saveFile(relPath, data) {
  return req('PUT', `/api/file?path=${encodeURIComponent(relPath)}`, { data })
}

// Tao file moi
export async function createFile(relPath, data) {
  return req('POST', `/api/file?path=${encodeURIComponent(relPath)}`, { data })
}

// Xoa file
export async function deleteFile(relPath) {
  return req('DELETE', `/api/file?path=${encodeURIComponent(relPath)}`)
}

// Liet ke tat ca file .json
export async function listFiles() {
  const json = await req('GET', '/api/list')
  return json.files
}

// Load nhieu file cung luc -> { key: data | {__error} }
export async function loadAll(map) {
  const entries = Object.entries(map)
  const results = await Promise.all(
    entries.map(async ([key, relPath]) => {
      try {
        return [key, await loadFile(relPath)]
      } catch (e) {
        console.error(relPath, e)
        return [key, { __error: e.message }]
      }
    })
  )
  return Object.fromEntries(results)
}
