import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/cards': 'https://tesl-deck-builder-production.up.railway.app',
      '/images': 'https://tesl-deck-builder-production.up.railway.app',
      '/download-pdf': 'https://tesl-deck-builder-production.up.railway.app',
    }
  },
  define: {
    'import.meta.env.VITE_API_URL': JSON.stringify(
      process.env.VITE_API_URL || ''
    )
  }
})