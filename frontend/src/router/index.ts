import { createRouter, createWebHistory, RouteRecordRaw } from 'vue-router'
import SearchView from '@/views/SearchView.vue'
import GameView from '@/views/GameView.vue'

const env = import.meta.env

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
    path: '/',
    redirect: '/search'
  },
  {
    path: '/callback:catchAll(\\?):code',
    redirect: '/search?:code'
  },
  {
    path: '/search:catchAll(\\?):code',
    redirect: '/search'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router