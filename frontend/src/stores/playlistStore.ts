import { defineStore } from 'pinia';
import { Playlist } from '@/types/Playlist';

const usePlaylistStore = defineStore('playlist', {
  state: () => ({
    playlist: null as Playlist | null,
  }),
  actions: {
    setPlaylist(playlist: Playlist) {
      this.playlist = playlist;
      localStorage.setItem('currentPlaylist', JSON.stringify(playlist));
    },
  },
  getters: {
    getPlaylist: (state) => {
      if (state.playlist === null) {
        const storedPlaylist = localStorage.getItem('currentPlaylist');
        if (storedPlaylist) {
          state.playlist = JSON.parse(storedPlaylist);
        }
      }
      return state.playlist;
    },
  },
});

export default usePlaylistStore;
