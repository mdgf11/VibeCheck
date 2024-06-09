import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router';
import SearchView from '@/views/SearchView.vue';
import GameView from '@/views/GameView.vue';
import PlaylistView from '@/views/PlaylistView.vue';
import LeaderboardView from '@/views/LeaderboardView.vue';
import { handleRedirect } from '@/services/spotifyHandler';

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
    path: '/loginSuccess',
    name: 'loginSuccess',
    component: {
      template: '<div>Login Successful</div>',
      created() {
        handleRedirect(); // Handle the redirect from the backend
        this.$router.replace({ path: '/' });
      }
    }
  }
];

const router = createRouter({
  history: createWebHistory(),
  routes
});

export default router;
