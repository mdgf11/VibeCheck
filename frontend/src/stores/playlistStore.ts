import { defineStore } from 'pinia';
import { Playlist } from '@/types/Playlist';
import { Song } from '@/types/Song';

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
    async fetchAndCreatePlaylist(name: string, type: string) {
      const env = import.meta.env;
      try {
        const response = await fetch(`${env.VITE_APP_BACKEND_URL}/playlist?query=${name}&type=${type}`);
        const data = await response.json();

        // Logging data to debug
        console.log('Fetched data:', data);

        // Create Playlist object
        const playlist: Playlist = {
          title: data.name,
          songs: data.songs.map((song: any) => ({
            id: song.id ? song.id.toString() : '',
            name: song.name,
            artists: song.artists,
            date: song.date,
            genres: new Map<string, number>(Object.entries(song.genres).map(([key, value]) => [key, Number(value)])),
            vibes: new Map<string, number>(Object.entries(song.vibes).map(([key, value]) => [key, Number(value)])),
            images: new Map<number, string>(Object.entries(song.images).map(([key, value]) => [Number(key), value as string])),
            duration: song.duration,
            popularity: song.popularity || 0,
            albums: song.albums,
          })),
          artists: data.artists.map((artist: any) => ({
            name: artist.name,
            genres: new Map<string, number>(Object.entries(artist.genres).map(([key, value]) => [key, Number(value)])),
            vibes: new Map<string, number>(Object.entries(artist.vibes).map(([key, value]) => [key, Number(value)])),
            popularity: artist.popularity || 0,
            images: new Map<number, string>(Object.entries(artist.images).map(([key, value]) => [Number(key), value as string])),
          })),
          genres: new Map<string, number>(Object.entries(data.genres).map(([key, value]) => [key, Number(value)])),
          vibes: new Map<string, number>(Object.entries(data.vibes).map(([key, value]) => [key, Number(value)])),
        };

        // Set the playlist
        this.setPlaylist(playlist);
      } catch (error) {
        console.error("Failed to fetch and create playlist:", error);
      }
    },
    removeSong(song: Song) {
      if (this.playlist) {
        const index = this.playlist.songs.findIndex((s) => s.id === song.id);
        if (index > -1) {
          this.playlist.songs.splice(index, 1);
          this.setPlaylist(this.playlist); // Update the playlist in sessionStorage
        }
      }
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
      genres: Object.fromEntries(song.genres),
      vibes: Object.fromEntries(song.vibes),
      images: Object.fromEntries(song.images),
    })),
    artists: playlist.artists.map((artist) => ({
      ...artist,
      genres: Object.fromEntries(artist.genres),
      vibes: Object.fromEntries(artist.vibes),
      images: Object.fromEntries(artist.images),
    })),
    genres: Object.fromEntries(playlist.genres),
    vibes: Object.fromEntries(playlist.vibes),
  };
}

function initializePlaylist(data: any): Playlist {
  return {
    ...data,
    songs: data.songs.map((song: any) => {
      const genresMap = new Map<string, number>(
        Object.entries(song.genres).map(([key, value]) => [key, Number(value)])
      );
      const vibesMap = new Map<string, number>(
        Object.entries(song.vibes).map(([key, value]) => [key, Number(value)])
      );
      const imagesMap = new Map<number, string>(
        Object.entries(song.images).map(([key, value]) => [Number(key), value as string])
      );
      return {
        ...song,
        id: song.id ? song.id.toString() : '',
        genres: genresMap,
        vibes: vibesMap,
        images: imagesMap,
        albums: song.albums,
      };
    }),
    artists: data.artists.map((artist: any) => {
      const genresMap = new Map<string, number>(
        Object.entries(artist.genres).map(([key, value]) => [key, Number(value)])
      );
      const vibesMap = new Map<string, number>(
        Object.entries(artist.vibes).map(([key, value]) => [key, Number(value)])
      );
      const imagesMap = new Map<number, string>(
        Object.entries(artist.images).map(([key, value]) => [Number(key), value as string])
      );
      return {
        ...artist,
        genres: genresMap,
        vibes: vibesMap,
        images: imagesMap,
      };
    }),
    genres: new Map<string, number>(
      Object.entries(data.genres).map(([key, value]) => [key, Number(value)])
    ),
    vibes: new Map<string, number>(
      Object.entries(data.vibes).map(([key, value]) => [key, Number(value)])
    ),
  };
}

export default usePlaylistStore;
