export interface PlaylistSettings {
    numSongs: number | null;
    maxDuration: number | null;
    minSongsPerArtist: Map<String, number> | null;
    maxSongsPerArtist: Map<String, number> | null;
    minSongsPerGenre: Map<String, number> | null;
    maxSongsPerGenre: Map<String, number> | null;
    minSongsPerVibe: Map<String, number> | null;
    maxSongsPerVibe: Map<String, number> | null;
  }
  