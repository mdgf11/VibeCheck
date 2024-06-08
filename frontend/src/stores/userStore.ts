import { Profile } from '@/types/Profile';
import { defineStore } from 'pinia';
import axios from 'axios';
const env = import.meta.env;

// Define constants
const SPOTIFY_CODE_KEY = 'spotifyCode';
const SPOTIFY_TOKEN_KEY = 'spotifyToken';
const PROFILE_KEY = 'profile';
const IS_LOGGED_IN_KEY = 'isLoggedIn';

const useUserStore = defineStore('user', {
  state: () => ({
    code: null as string | null,
    token: null as string | null,
    isLoggedIn: false,
    profile: null as Profile | null,
  }),
  getters: {
    getToken(state): string | null {
      if (state.token === null) {
        const savedToken = sessionStorage.getItem(SPOTIFY_TOKEN_KEY);
        if (savedToken) {
          state.token = savedToken;
        }
      }
      return state.token;
    },
    getIsLoggedIn(state): boolean {
      if (state.isLoggedIn === false) {
        const sessionIsLoggedIn = sessionStorage.getItem(IS_LOGGED_IN_KEY);
        if (sessionIsLoggedIn === 'true') {
          state.isLoggedIn = true;
        }
      }
      return state.isLoggedIn;
    },
    getProfile(state): Profile | null {
      if (state.profile === null) {
        const savedProfile = sessionStorage.getItem(PROFILE_KEY);
        if (savedProfile) {
          state.profile = JSON.parse(savedProfile) as Profile;
        }
      }
      return state.profile;
    }
  },
  actions: {
    setCode(code: string) {
      this.code = code;
      localStorage.setItem(SPOTIFY_CODE_KEY, code);
    },
    setToken(token: string) {
      this.token = token;
      sessionStorage.setItem(SPOTIFY_TOKEN_KEY, token);
    },
    async login(profile: Profile) {
      try {
        // Send the profile data to the backend to register the user
        const response = await axios.post(env.VITE_APP_BACKEND_URL + '/user/register', {
          email: profile.email,
          spotifyId: profile.id
        });

        // After successful registration, update the user store
        this.profile = profile;
        if (profile) {
          sessionStorage.setItem(PROFILE_KEY, JSON.stringify(profile));
        }

        this.isLoggedIn = true;
        sessionStorage.setItem(IS_LOGGED_IN_KEY, 'true');
      } catch (error) {
        console.error('Failed to register user:', error);
      }
    },
    logout() {
      this.token = null;
      this.isLoggedIn = false;
      this.profile = null;
      this.code = null;
      localStorage.removeItem(SPOTIFY_CODE_KEY);
      sessionStorage.removeItem(SPOTIFY_TOKEN_KEY);
      sessionStorage.removeItem(IS_LOGGED_IN_KEY);
      sessionStorage.removeItem(PROFILE_KEY);
    }
  }
});

export default useUserStore;
