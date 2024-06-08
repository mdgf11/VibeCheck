export interface Song {
  id: string;
  name: string;
  artists: string[];
  date: number;
  genres: string[];
  vibes: string[];
  images: Map<number,string>;
  duration: string;
  popularity: number;
  albums: string[];
}
