import { defineStore } from 'pinia';
import { Playlist } from '@/types/Playlist';

const usePlaylistStore = defineStore('playlist', {
  state: () => {
    const storedPlaylist = sessionStorage.getItem('currentPlaylist');
    return {
      playlist: storedPlaylist ? initializePlaylist(JSON.parse(storedPlaylist)) : null,
    };
  },
  actions: {
    setPlaylist(playlist: Playlist) {
      const convertedPlaylist = convertPlaylistToPlainObject(playlist);
      sessionStorage.setItem('currentPlaylist', JSON.stringify(convertedPlaylist));
      this.playlist = playlist;
    },
  },
  getters: {
    getPlaylist: (state) => state.playlist,
  },
});

function convertPlaylistToPlainObject(playlist: Playlist) {
  return {
    ...playlist,
    songs: playlist.songs.map((song) => ({
      ...song,
      images: Object.fromEntries(song.images),
    })),
    artists: playlist.artists.map((artist) => ({
      ...artist,
      images: Object.fromEntries(artist.images),
    })),
  };
}

function initializePlaylist(data: any): Playlist {

  const initializedPlaylist = {
    ...data,
    songs: data.songs.map((song: any) => {
      console.log('Raw song images:', song.images);
      const imagesMap = new Map<number, string>(
        Object.entries(song.images).map(([key, value]) => [Number(key), value as string])
      );
      console.log('Converted images map:', imagesMap);
      return {
        ...song,
        images: imagesMap,
      };
    }),
    artists: data.artists.map((artist: any) => {
      const imagesMap = new Map<number, string>(
        Object.entries(artist.images).map(([key, value]) => [Number(key), value as string])
      );
      return {
        ...artist,
        images: imagesMap,
      };
    }),
  };

  return initializedPlaylist;
}

export default usePlaylistStore;
