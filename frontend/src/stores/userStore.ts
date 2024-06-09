import { defineStore } from 'pinia';
const SPOTIFY_TOKEN_KEY = 'spotifyToken';
const IS_LOGGED_IN_KEY = 'isLoggedIn';

const useUserStore = defineStore('user', {
  state: () => ({
    token: null as string |  null,
    isLoggedIn: false,
  }),
  getters: {
    getToken(state) {
      if (state.token === null) {
        const savedToken = sessionStorage.getItem(SPOTIFY_TOKEN_KEY);
        if (savedToken) {
          state.token = savedToken;
        }
      }
      return state.token;
    },
    getIsLoggedIn(state) {
      if (!state.isLoggedIn) {
        const sessionIsLoggedIn = sessionStorage.getItem(IS_LOGGED_IN_KEY);
        if (sessionIsLoggedIn === 'true') {
          state.isLoggedIn = true;
        }
      }
      return state.isLoggedIn;
    },
  },
  actions: {
    setToken(token: string) {
      this.token = token;
      sessionStorage.setItem(SPOTIFY_TOKEN_KEY, token);
      this.isLoggedIn = true;
      sessionStorage.setItem(IS_LOGGED_IN_KEY, 'true');
    },
    logout() {
      this.token = null;
      this.isLoggedIn = false;
      sessionStorage.removeItem(SPOTIFY_TOKEN_KEY);
      sessionStorage.removeItem(IS_LOGGED_IN_KEY);
    }
  }
});

export default useUserStore;
