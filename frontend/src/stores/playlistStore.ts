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
    loadPlaylistFromLocalStorage() {
      const savedPlaylist = localStorage.getItem('currentPlaylist');
      if (savedPlaylist) {
        this.playlist = JSON.parse(savedPlaylist) as Playlist;
      }
    }
  }
});

export default usePlaylistStore;
