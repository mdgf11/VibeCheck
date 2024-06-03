import { defineConfig } from 'vite'
import vue from '@vitejs/plugin-vue'
import { resolve } from 'path'
import dotenv from 'dotenv'

// Load the appropriate .env file based on the mode
dotenv.config({ path: `.env.${process.env.NODE_ENV}` })

// Determine the base URL
const baseURL = process.env.VERCEL_URL 
  ? `https://${process.env.VERCEL_URL}`
  : process.env.VITE_APP_BASE_URL;

const port = process.env.VITE_APP_PORT ? parseInt(process.env.VITE_APP_PORT, 10) : undefined;

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
    },
  },
  server: {
    port: port || 3000,  // Use the port from .env or fallback to 3000
  },
  define: {
    'process.env': {
      VITE_APP_BASE_URL: baseURL,
      VITE_APP_PORT: port,
    }
  }
})
