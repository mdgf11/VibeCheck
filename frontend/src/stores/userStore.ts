import { defineStore } from 'pinia';
import { Profile } from '@/types/Profile';

const TOKEN_KEY = 'token';
const USER_KEY = 'user';
const IS_LOGGED_IN_KEY = 'isLoggedIn';

const useUserStore = defineStore('user', {
  state: () => ({
    token: null as string | null,
    user: null as Profile | null,
    isLoggedIn: false,
  }),
  getters: {
    getToken(state) {
      if (state.token === null) {
        const savedToken = sessionStorage.getItem(TOKEN_KEY);
        if (savedToken) {
          state.token = savedToken;
        }
      }
      return state.token;
    },
    getUser(state) {
      if (state.user === null) {
        const savedUser = sessionStorage.getItem(USER_KEY);
        if (savedUser) {
          const parsedUser = JSON.parse(savedUser);
          if (parsedUser.images) {
            parsedUser.images = new Map<number, string>(
              parsedUser.images.map((entry: [string, string]) => [Number(entry[0]), entry[1]])
            );
          }
          state.user = parsedUser;
        }
      }
      return state.user;
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
    setTokenAndUser(data: { token: string, user: Profile }) {
      this.token = data.token;

      // Ensure images is a Map<number, string>
      let images = data.user.images;
      if (!(images instanceof Map)) {
        images = new Map<number, string>(
          Object.entries(images).map(([key, value]) => [Number(key), value as string])
        );
      }

      // Convert the Map to an array of key-value pairs for serialization
      const userToSave = {
        ...data.user,
        images: Array.from(images.entries())
      };

      sessionStorage.setItem(TOKEN_KEY, data.token);
      sessionStorage.setItem(USER_KEY, JSON.stringify(userToSave));
      this.isLoggedIn = true;
      sessionStorage.setItem(IS_LOGGED_IN_KEY, 'true');

      // Update the user in the state with the original Map
      this.user = { ...data.user, images };
    },
    logout() {
      this.token = null;
      this.user = null;
      this.isLoggedIn = false;
      sessionStorage.removeItem(TOKEN_KEY);
      sessionStorage.removeItem(USER_KEY);
      sessionStorage.removeItem(IS_LOGGED_IN_KEY);
    },
    async fetchSearchResults(query: string) {
      try {
        const token = this.getToken;
        const response = await fetch(import.meta.env.VITE_APP_BACKEND_URL + `/search?query=${query}`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        if (!response.ok) {
          throw new Error('Failed to fetch search results');
        }
        return await response.json();
      } catch (error) {
        console.error("Failed to fetch search results:", error);
        throw error;
      }
    },
    async fetchAdminSearchResults(query: string) {
      try {
        const token = this.getToken;
        const response = await fetch(import.meta.env.VITE_APP_BACKEND_URL + `/songs/addArtist?name=${query}`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        console.log(response);
        if (!response.ok) {
          throw new Error('Failed to fetch admin search results');
        }
        return await response.json();
      } catch (error) {
        console.error("Failed to fetch admin search results:", error);
        throw error;
      }
    },
    async adminDiscover() {
      try {
        const token = this.getToken;
        const response = await fetch(import.meta.env.VITE_APP_BACKEND_URL + `/songs/discover`, {
          headers: {
            'Authorization': `Bearer ${token}`
          }
        });
        if (!response.ok) {
          throw new Error('Failed to fetch admin discover');
        }
        return await response.json();
      } catch (error) {
        throw error;
      }
    }
  },
});

export default useUserStore;
