import useUserStore from '@/stores/userStore';
const env = import.meta.env;

export async function getProfile(currentPath: string) {
  const clientId = env.VITE_APP_SPOTIFY_CLIENT_ID;
  const params = new URLSearchParams(window.location.search);
  const code = params.get("code");

  const userStore = useUserStore();

  if (!code) {
    await redirectToAuthCodeFlow(clientId, currentPath);
  } else {
    userStore.setCode(code);  // Save code in user store and local storage
    const accessToken = await getAccessToken(clientId, code);
    const profile = await fetchProfile(accessToken);
    console.log(profile);
  }
}
async function redirectToAuthCodeFlow(clientId: string, currentPath: string) {
    const verifier = await generateCodeVerifier(128);
    const challenge = await generateCodeChallenge(verifier);

    localStorage.setItem("verifier", verifier);

    const redirectUri = env.VITE_APP_NODE_ENV == 'production'
        ? `${env.VITE_APP_BASE_URL}/callback`
        : `${env.VITE_APP_BASE_URL}:${env.VITE_APP_PORT}/callback`;

    const params = new URLSearchParams();
    params.append("client_id", clientId);
    params.append("response_type", "code");
    params.append("redirect_uri", redirectUri);
    params.append("scope", "user-read-private user-read-email");
    params.append("code_challenge_method", "S256");
    params.append("code_challenge", challenge);
    params.append("state", encodeURIComponent(currentPath));

    window.location.href = "https://accounts.spotify.com/authorize?" + params.toString();
}

async function generateCodeVerifier(length: number) {
    let text = '';
    const possible = 'ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789';

    for (let i = 0; i < length; i++) {
        text += possible.charAt(Math.floor(Math.random() * possible.length));
    }
    return text;
}

async function generateCodeChallenge(codeVerifier: string) {
    const data = new TextEncoder().encode(codeVerifier);
    const digest = await window.crypto.subtle.digest('SHA-256', data);
    return btoa(String.fromCharCode.apply(null, [...new Uint8Array(digest)]))
        .replace(/\+/g, '-')
        .replace(/\//g, '_')
        .replace(/=+$/, '');
}

export async function getAccessToken(clientId: string, code: string): Promise<string> {
    const verifier = localStorage.getItem("verifier");
    const redirectUri = env.VITE_APP_NODE_ENV == 'production'
        ? `${env.VITE_APP_BASE_URL}/callback`
        : `${env.VITE_APP_BASE_URL}:${env.VITE_APP_PORT}/callback`;

    const params = new URLSearchParams();
    params.append("client_id", clientId);
    params.append("grant_type", "authorization_code");
    params.append("code", code);
    params.append("redirect_uri", redirectUri);
    if (verifier != null)
        params.append("code_verifier", verifier);

    const result = await fetch("https://accounts.spotify.com/api/token", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: params
    });

    const { access_token } = await result.json();
    return access_token;
}

async function fetchProfile(token: string): Promise<JSON> {
    const result = await fetch("https://api.spotify.com/v1/me", {
        method: "GET", headers: { Authorization: `Bearer ${token}` }
    });

    return await result.json();
}
