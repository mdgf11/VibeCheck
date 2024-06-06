import { Song } from './Song';
import { Artist } from './Artist';
export interface Playlist {
  title: string;
  songs: Song[];
  artists: Artist[];
}
