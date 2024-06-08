package pt.migfonseca.vibecheck.service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.migfonseca.vibecheck.dto.PlaylistDTO;
import pt.migfonseca.vibecheck.model.Album;
import pt.migfonseca.vibecheck.model.Artist;
import pt.migfonseca.vibecheck.model.Genre;
import pt.migfonseca.vibecheck.model.Playlist;
import pt.migfonseca.vibecheck.model.Song;
import pt.migfonseca.vibecheck.model.Vibe;
import pt.migfonseca.vibecheck.model.ratings.GenreRating;
import pt.migfonseca.vibecheck.model.ratings.VibeRating;
import pt.migfonseca.vibecheck.repo.AlbumRepository;
import pt.migfonseca.vibecheck.repo.ArtistRepository;
import pt.migfonseca.vibecheck.repo.GenreRepository;
import pt.migfonseca.vibecheck.repo.PlaylistRepository;
import pt.migfonseca.vibecheck.repo.SongRepository;
import pt.migfonseca.vibecheck.repo.VibeRepository;


@Service
public class PlaylistService {

    @Autowired
    private PlaylistRepository playlistRepository;

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private VibeRepository vibeRepository;

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
        // Retrieve all songs by the artist
        List<Song> allSongs = songRepository.findAllByArtist(artist);
    
        // Collect all genres from the songs
        Set<Genre> allGenres = allSongs.stream()
            .flatMap(song -> song.getGenreRatings().stream())
            .map(GenreRating::getGenre)
            .collect(Collectors.toSet());
    
        // Collect all vibes from the songs
        Set<Vibe> allVibes = allSongs.stream()
            .flatMap(song -> song.getVibeRatings().stream())
            .map(VibeRating::getVibe)
            .collect(Collectors.toSet());
    
        // Calculate the average genre ratings
        Map<Genre, Double> genreRatings = allGenres.stream()
            .collect(Collectors.toMap(
                genre -> genre,
                genre -> allSongs.stream()
                    .flatMap(song -> song.getGenreRatings().stream())
                    .filter(gr -> gr.getGenre().equals(genre))
                    .mapToDouble(GenreRating::getRating)
                    .average()
                    .orElse(0.0)
            ));
    
        // Calculate the average vibe ratings
        Map<Vibe, Double> vibeRatings = allVibes.stream()
            .collect(Collectors.toMap(
                vibe -> vibe,
                vibe -> allSongs.stream()
                    .flatMap(song -> song.getVibeRatings().stream())
                    .filter(vr -> vr.getVibe().equals(vibe))
                    .mapToDouble(VibeRating::getRating)
                    .average()
                    .orElse(0.0)
            ));
    
        // Create a new playlist
        Playlist playlist = new Playlist();
        playlist.setName("Playlist for " + artist.getName());
        playlist.setArtists(artistRepository
            .findAllArtistsInSongs(artist)
            .stream()
            .limit(10)
            .toList());
        playlist.setSongs(allSongs.stream()
            .limit(10)
            .toList());
    
        // Save the playlist
        playlistRepository.save(playlist);
        
        // Set the average genre ratings
        List<GenreRating> genreRatingList = genreRatings.entrySet().stream()
            .map(entry -> new GenreRating(playlist, entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
        playlist.setGenreRatings(genreRatingList);
    
        // Set the average vibe ratings
        List<VibeRating> vibeRatingList = vibeRatings.entrySet().stream()
            .map(entry -> new VibeRating(playlist, entry.getKey(), entry.getValue()))
            .collect(Collectors.toList());
        playlist.setVibeRatings(vibeRatingList);
    
    
        // Convert to DTO and return
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
