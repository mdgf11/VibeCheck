import useUserStore from '@/stores/userStore';

export async function redirectToSpotify() {
  const clientId = import.meta.env.VITE_APP_SPOTIFY_CLIENT_ID;
  const redirectUri = `${import.meta.env.VITE_APP_BACKEND_URL}/user/callback`;
  console.log("Redirect URI: " + redirectUri);
  const params = new URLSearchParams();
  params.append("client_id", clientId);
  params.append("response_type", "code");
  params.append("redirect_uri", redirectUri);
  params.append("scope", "user-read-private user-read-email");

  window.location.href = "https://accounts.spotify.com/authorize?" + params.toString();
}

export function handleRedirect() {
  const userStore = useUserStore();
  const params = new URLSearchParams(window.location.search);
  const token = params.get('token');

  if (token) {
    userStore.setToken(token);
    window.history.replaceState({}, document.title, "/");
  }
}
