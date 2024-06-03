/// <reference types="vite/client" />

interface ImportMetaEnv {
    readonly VITE_APP_PORT: string
    // Add other environment variables here as needed.
  }
  
  interface ImportMeta {
    readonly env: ImportMetaEnv
  }
  