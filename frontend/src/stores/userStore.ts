import { defineStore } from 'pinia';

const useUserStore = defineStore('user', {
  state: () => ({
    code: null as string | null,
    isLoggedIn: false,
  }),
  actions: {
    setCode(code: string) {
      console.log('Setting code in store:', code);
      this.code = code;
      this.isLoggedIn = true;
      sessionStorage.setItem('spotifyCode', code);
      sessionStorage.setItem('isLoggedIn', 'true');
    },
    loadCodeFromSessionStorage() {
      const savedCode = sessionStorage.getItem('spotifyCode');
      const sessionIsLoggedIn = sessionStorage.getItem('isLoggedIn');

      if (savedCode && sessionIsLoggedIn) {
        this.code = savedCode;
        this.isLoggedIn = true;
      } else {
        this.logout();
      }
    },
    logout() {
      this.code = null;
      this.isLoggedIn = false;
      sessionStorage.removeItem('spotifyCode');
      sessionStorage.removeItem('isLoggedIn');
    }
  }
});

export default useUserStore;
