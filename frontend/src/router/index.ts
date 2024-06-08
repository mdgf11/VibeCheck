import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import SearchView from '@/views/SearchView.vue';
import GameView from '@/views/GameView.vue';
import PlaylistView from '@/views/PlaylistView.vue';
import LeaderboardView from '@/views/LeaderboardView.vue';
import useUserStore from '@/stores/userStore';
import { getProfile } from '@/services/SpotifyAPIController';

const routes: Array<RouteRecordRaw> = [
  {
    path: '/search',
    name: 'search',
    component: SearchView
  },
  {
    path: '/',
    name: 'default',
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
    path: '/leaderboard',
    name: 'leaderboard',
    component: LeaderboardView
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

        const targetPath = state ? decodeURIComponent(state) : '/search';

        if (code) {
          const userStore = useUserStore();
          userStore.setCode(code);
          getProfile(targetPath);
        }

        this.$router.replace({ path: targetPath });
      }
    }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
