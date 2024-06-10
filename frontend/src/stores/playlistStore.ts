import { defineStore } from 'pinia';
import { Playlist } from '@/types/Playlist';
import { Song } from '@/types/Song';
import { PlaylistSettings } from '@/types/PlaylistSettings';

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
    async fetchAndCreatePlaylist(name: string, type: string, artist: string = '') {
      try {
        const data = await fetchData(name, type, artist);
        const playlist = createPlaylistFromData(data, name, type, artist);
        this.setPlaylist(playlist);
      } catch (error) {
        console.error("Failed to fetch and create playlist:", error);
      }
    },
    async applySettings(settings: PlaylistSettings) {
      try {
        const data = await applyPlaylistSettings(this.playlist, settings);
        const playlist = createPlaylistFromData(data, this.playlist.query, this.playlist.type, this.playlist.queryArtist);
        this.setPlaylist(playlist);
      } catch (error) {
        console.error("Failed to apply settings and create playlist:", error);
      }
    },
    removeSong(song: Song) {
      if (this.playlist) {
        const index = this.playlist.songs.findIndex((s: Song) => s.id === song.id);
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

async function fetchData(name: string, type: string, artist: string) {
  const env = import.meta.env;
  const queryParams = new URLSearchParams({ query: name, type });
  if (type === 'album' || type === 'song') {
    queryParams.append('artist', artist);
  }
  console.log(`${env.VITE_APP_BACKEND_URL}/playlist/create?${queryParams.toString()}`);
  const response = await fetch(`${env.VITE_APP_BACKEND_URL}/playlist/create?${queryParams.toString()}`);
  return await response.json();
}

function createPlaylistFromData(data: any, name: string, type: string, artist: string): Playlist {
  return {
    title: data.name,
    songs: data.songs.map((song: any) => mapSong(song)),
    artists: data.artists.map((artist: any) => mapArtist(artist)),
    genres: new Map<string, number>(Object.entries(data.genres).map(([key, value]) => [key, Number(value)])),
    vibes: new Map<string, number>(Object.entries(data.vibes).map(([key, value]) => [key, Number(value)])),
    query: name,
    type: type,
    queryArtist: artist,
  };
}

async function applyPlaylistSettings(playlist: Playlist, settings: PlaylistSettings) {
  const env = import.meta.env;
  const queryParams = new URLSearchParams({ query: playlist.query, type: playlist.type });
  if (playlist.type === 'album' || playlist.type === 'song') {
    queryParams.append('artist', playlist.queryArtist);
  }

  const serializableSettings = convertSettingsToSerializable(settings);

  const response = await fetch(`${env.VITE_APP_BACKEND_URL}/playlist/create?${queryParams.toString()}`, {
    method: 'POST',
    headers: {
      'Content-Type': 'application/json',
    },
    body: JSON.stringify(serializableSettings),
  });
  return await response.json();
}

function convertSettingsToSerializable(settings: PlaylistSettings) {
  return {
    ...settings,
    minSongsPerArtist: settings.minSongsPerArtist && settings.minSongsPerArtist.size > 0 ? Object.fromEntries(settings.minSongsPerArtist) : null,
    maxSongsPerArtist: settings.maxSongsPerArtist && settings.maxSongsPerArtist.size > 0 ? Object.fromEntries(settings.maxSongsPerArtist) : null,
    minSongsPerGenre: settings.minSongsPerGenre && settings.minSongsPerGenre.size > 0 ? Object.fromEntries(settings.minSongsPerGenre) : null,
    maxSongsPerGenre: settings.maxSongsPerGenre && settings.maxSongsPerGenre.size > 0 ? Object.fromEntries(settings.maxSongsPerGenre) : null,
    minSongsPerVibe: settings.minSongsPerVibe && settings.minSongsPerVibe.size > 0 ? Object.fromEntries(settings.minSongsPerVibe) : null,
    maxSongsPerVibe: settings.maxSongsPerVibe && settings.maxSongsPerVibe.size > 0 ? Object.fromEntries(settings.maxSongsPerVibe) : null,
  };
}

function mapSong(song: any) {
  return {
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
  };
}

function mapArtist(artist: any) {
  return {
    name: artist.name,
    genres: new Map<string, number>(Object.entries(artist.genres).map(([key, value]) => [key, Number(value)])),
    vibes: new Map<string, number>(Object.entries(artist.vibes).map(([key, value]) => [key, Number(value)])),
    popularity: artist.popularity || 0,
    images: new Map<number, string>(Object.entries(artist.images).map(([key, value]) => [Number(key), value as string])),
  };
}

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
    songs: data.songs.map((song: any) => mapSong(song)),
    artists: data.artists.map((artist: any) => mapArtist(artist)),
    genres: new Map<string, number>(
      Object.entries(data.genres).map(([key, value]) => [key, Number(value)])
    ),
    vibes: new Map<string, number>(
      Object.entries(data.vibes).map(([key, value]) => [key, Number(value)])
    ),
  };
}

export default usePlaylistStore;
