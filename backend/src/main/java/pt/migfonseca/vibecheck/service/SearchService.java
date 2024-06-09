package pt.migfonseca.vibecheck.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.migfonseca.vibecheck.dto.SearchResponseDTO;
import pt.migfonseca.vibecheck.model.Album;
import pt.migfonseca.vibecheck.model.Artist;
import pt.migfonseca.vibecheck.model.Genre;
import pt.migfonseca.vibecheck.model.Song;
import pt.migfonseca.vibecheck.model.Vibe;
import pt.migfonseca.vibecheck.repo.AlbumRepository;
import pt.migfonseca.vibecheck.repo.ArtistRepository;
import pt.migfonseca.vibecheck.repo.GenreRepository;
import pt.migfonseca.vibecheck.repo.SongRepository;
import pt.migfonseca.vibecheck.repo.VibeRepository;

@Service
public class SearchService {

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

    public List<SearchResponseDTO> searchQuery(String query, String type) {        
        List<SearchResponseDTO> results = new ArrayList<>();
        if (type == null || type.isEmpty()) {
            // Search for artists
            List<Artist> artists = artistRepository.findByNameContainingIgnoreCase(query);
            results.addAll(artists
                    .stream()
                    .limit(20)
                    .sorted(Comparator.comparing(Artist::getPopularity).reversed())
                    .map(artist -> artist.toResponseDTO())
                    .toList());
    
            // Search for albums
            List<Album> albums = albumRepository.findByAlbumNameContainingIgnoreCase(query);
            results.addAll(albums
                    .stream()
                    .limit(20)
                    .sorted(Comparator.comparing(Album::getPopularity).reversed())
                    .map(album -> album.toResponseDTO())
                    .toList());
    
            // Search for songs
            List<Song> songs = songRepository.findBySongNameContainingIgnoreCase(query);
            results.addAll(songs
                    .stream()
                    .limit(20)
                    .sorted(Comparator.comparing(Song::getPopularity).reversed())
                    .map(song -> song.toResponseDTO())
                    .toList());
    
            // Search for vibes
            List<Vibe> vibes = vibeRepository.findByNameContainingIgnoreCase(query);
            results.addAll(vibes.stream().map(vibe -> vibe.toResponseDTO()).limit(20).toList());
    
            // Search for genres
            List<Genre> genres = genreRepository.findByNameContainingIgnoreCase(query);
            results.addAll(genres.stream().map(genre -> genre.toResponseDTO()).limit(20).toList());
        } else {
            switch (type.toLowerCase()) {
                case "artist":
                    List<Artist> artists = artistRepository.findByNameContainingIgnoreCase(query);
                    results.addAll(artists
                            .stream()
                            .limit(20)
                            .sorted(Comparator.comparing(Artist::getPopularity).reversed())
                            .map(artist -> artist.toResponseDTO())
                            .toList());
                    break;
                case "album":
                    List<Album> albums = albumRepository.findByAlbumNameContainingIgnoreCase(query);
                    results.addAll(albums
                            .stream()
                            .limit(20)
                            .sorted(Comparator.comparing(Album::getPopularity).reversed())
                            .map(album -> album.toResponseDTO())
                            .toList());
                    break;
                case "song":
                    List<Song> songs = songRepository.findBySongNameContainingIgnoreCase(query);
                    results.addAll(songs
                            .stream()
                            .limit(20)
                            .sorted(Comparator.comparing(Song::getPopularity).reversed())
                            .map(song -> song.toResponseDTO())
                            .toList());
                    break;
                case "vibe":
                    List<Vibe> vibes = vibeRepository.findByNameContainingIgnoreCase(query);
                    results.addAll(vibes.stream().map(vibe -> vibe.toResponseDTO()).limit(20).toList());
                    break;
                case "genre":
                    List<Genre> genres = genreRepository.findByNameContainingIgnoreCase(query);
                    results.addAll(genres.stream().map(genre -> genre.toResponseDTO()).limit(20).toList());                    
                    break;
                default:
                    // Optionally handle unknown types here
                    break;
            }
        }
    
        return results;
    }
    
}