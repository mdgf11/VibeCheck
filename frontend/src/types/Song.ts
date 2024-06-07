export interface Song {
    name: string;
    artists: string[];
    date: number;
    genres: string[];
    vibes: string[];
    images: Map<number,string>;
    duration: string;
    popularity: number;
  }
  