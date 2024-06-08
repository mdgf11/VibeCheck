export interface Artist {
    name: string;
    genres: Map<string, number>;
    vibes: Map<string, number>;
    popularity: number;
    images: Map<number,string>;
}
  