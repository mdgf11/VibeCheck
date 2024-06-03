/// <reference types="vite/client" />

interface ImportMetaEnv {
    readonly VITE_APP_PORT: string
    readonly VITE_APP_BASE_URL: string
    readonly VITE_APP_SPOTIFY_CLIENT_ID: string
    // Add other environment variables here as needed.
  }
  
  interface ImportMeta {
    readonly env: ImportMetaEnv
  }
  