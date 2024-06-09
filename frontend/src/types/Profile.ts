export interface Profile {
  id: string | null;
  email: string | null;
  username: string | null;
  spotifyId: string | null;
  images: Map<number, string>;
  admin: boolean | null;
}
