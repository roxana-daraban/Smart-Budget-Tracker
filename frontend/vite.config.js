import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    open: true, // Deschide automat în browser la npm run dev
    port: 8081,
  },
})
