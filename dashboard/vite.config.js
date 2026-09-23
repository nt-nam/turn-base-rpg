import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// Cau hinh Vite cho Game Content Studio
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5180,
    open: true,
    proxy: {
      '/api': 'http://127.0.0.1:5179',
    },
  },
})
