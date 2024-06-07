package pt.migfonseca.vibecheck.service;

import java.io.IOException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.migfonseca.vibecheck.dto.PlaylistDTO;
import pt.migfonseca.vibecheck.model.Album;
import pt.migfonseca.vibecheck.model.Artist;
import pt.migfonseca.vibecheck.model.Genre;
import pt.migfonseca.vibecheck.model.Playlist;
import pt.migfonseca.vibecheck.model.Song;
import pt.migfonseca.vibecheck.model.Vibe;
import pt.migfonseca.vibecheck.repo.AlbumRepository;
import pt.migfonseca.vibecheck.repo.ArtistRepository;
import pt.migfonseca.vibecheck.repo.GenreRepository;
import pt.migfonseca.vibecheck.repo.PlaylistRepository;
import pt.migfonseca.vibecheck.repo.SongRepository;
import pt.migfonseca.vibecheck.repo.VibeRepository;


@Service
public class PlaylistService {

    @Autowired
    PlaylistRepository playlistRepository;

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    SongRepository songRepository;

    @Autowired
    GenreRepository genreRepository;

    @Autowired
    VibeRepository vibeRepository;

    public PlaylistDTO getPlaylist(String query, String artistString, String type) throws IOException {
        Optional<Artist> artist = artistRepository.findByName(artistString);
        switch (type) {
            case "artist":
                Optional<Artist> artistOptional = artistRepository.findByName(query);
                if (!artistOptional.isPresent())
                    break;
                return getPlaylistWithArtist(artistOptional.get());
            
            case "album":
                if (!artist.isPresent())
                    break;
                Optional<Album> albumOptional = albumRepository.findByNameWithArtist(query, artist.get());
                if (!albumOptional.isPresent())
                    break;
                return getPlaylistWithAlbum(albumOptional.get());
    
            case "song":
                if (!artist.isPresent())
                    break;
                Optional<Song> songOptional = songRepository.findByNameWithArtist(query, artist.get());
                if (!songOptional.isPresent())
                    break;
                return getPlaylistWithSong(songOptional.get());
    
            case "genre":
                Optional<Genre> genreOptional = genreRepository.findByName(query);
                if (!genreOptional.isPresent())
                    break;
                return getPlaylistWithGenre(genreOptional.get());
    
            case "vibe":
                Optional<Vibe> vibeOptional = vibeRepository.findByName(query);
                if (!vibeOptional.isPresent())
                    break;
                return getPlaylistWithVibe(vibeOptional.get());
    
            default:
                throw new IOException("Invalid type.");
        }
        throw new IOException("Query not found.");
    }
    
    public PlaylistDTO getPlaylistWithArtist(Artist artist) {
        Playlist playlist = new Playlist();
        playlist.setName("Playlist for " + artist.getName());
        playlist.setArtists(artistRepository
            .findAllArtistsInSongs(artist)
            .stream()
            .limit(10)
            .toList());
        playlist.setSongs(songRepository
            .findAllByArtist(artist)
            .stream()
            .limit(10)
            .toList());
        playlistRepository.save(playlist);
        return playlist.toDto();
    }

    public PlaylistDTO getPlaylistWithAlbum(Album album) {
        PlaylistDTO playlistDTO = new PlaylistDTO();
        return playlistDTO;
    }
    
    public PlaylistDTO getPlaylistWithSong(Song song) {
        PlaylistDTO playlistDTO = new PlaylistDTO();
        playlistDTO.setName("Playlist for " + song);
        return playlistDTO;
    }
    
    public PlaylistDTO getPlaylistWithGenre(Genre genre) {
        PlaylistDTO playlistDTO = new PlaylistDTO();
        playlistDTO.setName("Playlist for " + genre);
        return playlistDTO;
    }
    
    public PlaylistDTO getPlaylistWithVibe(Vibe vibe) {
        PlaylistDTO playlistDTO = new PlaylistDTO();
        playlistDTO.setName("Playlist for " + vibe);
        return playlistDTO;
    }
    
}
