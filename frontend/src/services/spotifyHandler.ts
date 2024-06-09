import useUserStore from '@/stores/userStore';
import { Profile } from '@/types/Profile';

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
  const spotifyId = params.get('spotifyId');
  const email = params.get('email');
  const username = params.get('username');
  const score = params.get('score');
  const image64 = params.get('image64');
  const image300 = params.get('image300');
  const id = params.get('id');
  const adminString = params.get('admin');

  // Correctly initialize admin as a boolean
  const admin = adminString === 'true';

  console.log(params);
  const images = new Map<number, string>();
  if (image64) images.set(64, image64);
  if (image300) images.set(300, image300);

  if (token) {
    const user: Profile = {
      id: id || null,
      email: email || null,
      username: username || null,
      spotifyId: spotifyId || null,
      images: images,
      admin: admin,  // Directly use the boolean value
    };
    console.log(user);

    userStore.setTokenAndUser({ token, user });
    window.history.replaceState({}, document.title, "/");
  }
}

