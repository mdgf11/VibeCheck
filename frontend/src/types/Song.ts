export interface Song {
  id: string;
  name: string;
  artists: string[];
  albums: string[];
  genres: Map<string, number>;
  vibes: Map<string, number>;
  images: Map<number,string>;
  duration: string;
  popularity: number;
  date: number;
}
