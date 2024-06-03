import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import dotenv from 'dotenv'

// Load environment variables from .env files
dotenv.config({ path: `.env.${process.env.NODE_ENV}` })

// Use Vercel's built-in environment variable for the base URL
const baseURL = process.env.VERCEL_URL 
  ? `https://${process.env.VERCEL_URL}` 
  : process.env.VITE_APP_BASE_URL;

const port = process.env.VITE_APP_PORT ? parseInt(process.env.VITE_APP_PORT, 10) : 3000

if (isNaN(port)) {
  throw new Error(`Invalid port number: ${process.env.VITE_APP_PORT}`)
}

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
  server: {
    port,
  },
  define: {
    'process.env': {
      VITE_APP_BASE_URL: baseURL,
    }
  }
})
