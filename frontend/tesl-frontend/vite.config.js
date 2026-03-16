import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/cards': 'http://localhost:8080',
      '/images': 'http://localhost:8080',
      '/download-pdf': 'http://localhost:8080',
    }
  }
})