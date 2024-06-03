import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import SearchView from '@/views/SearchView.vue';
import GameView from '@/views/GameView.vue';
import PlaylistView from '@/views/PlaylistView.vue';
import useUserStore from '@/stores/userStore';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/search',
    name: 'search',
    component: SearchView
  },
  {
    path: '/game',
    name: 'game',
    component: GameView
  },
  {
    path: '/playlist',
    name: 'playlist',
    component: PlaylistView
  },
  {
    path: '/',
    redirect: '/search'
  },
  {
    path: '/callback',
    name: 'callback',
    component: {
      template: '<div></div>',
      created() {
        const params = new URLSearchParams(window.location.search);
        const code = params.get("code");
        const state = params.get("state");

        if (code) {
          const userStore = useUserStore();
          userStore.setCode(code);  // Save the code in the store and local storage
        }

        const targetPath = state ? decodeURIComponent(state) : '/search';
        this.$router.replace({ path: targetPath });  // Redirect without the code in the URL
      }
    }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
