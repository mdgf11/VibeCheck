import { defineConfig } from 'vite';
import vue from '@vitejs/plugin-vue';
import { resolve } from 'path';
import dotenv from 'dotenv';

// Load the appropriate .env file based on the mode
dotenv.config({ path: `.env.${process.env.VITE_APP_NODE_ENV}` });

const baseURL = process.env.VERCEL_URL
  ? `https://${process.env.VERCEL_URL}`
  : process.env.VITE_APP_BASE_URL;

const port = process.env.VITE_APP_PORT ? parseInt(process.env.VITE_APP_PORT, 10) : 3000;

export default defineConfig({
  plugins: [vue()],
  resolve: {
    alias: {
      '@': resolve(__dirname, 'src'),
      'vue': 'vue/dist/vue.esm-bundler.js' // Alias for Vue
    },
  },
  server: {
    port: port, // Use the port from .env or fallback to 3000
  },
  define: {
    '__VUE_PROD_HYDRATION_MISMATCH_DETAILS__': false,
    'process.env': {
      VITE_APP_BASE_URL: baseURL,
      VITE_APP_PORT: port,
    }
  },
});
