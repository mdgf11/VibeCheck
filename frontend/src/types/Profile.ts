export interface Profile {
  id: string | null;
  email: string | null;
  display_name: string | null;
  spotifyId: string | null;
  images: Map<number, string>;
}
