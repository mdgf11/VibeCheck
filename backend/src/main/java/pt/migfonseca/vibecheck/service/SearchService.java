package pt.migfonseca.vibecheck.service;

import java.util.ArrayList;
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
    ArtistRepository artistRepository;

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    SongRepository songRepository;

    @Autowired
    GenreRepository genreRepository;

    @Autowired
    VibeRepository vibeRepository;

    public List<SearchResponseDTO> searchQuery(String query) {
        List<SearchResponseDTO> results = new ArrayList<>();

        // Search for artists
        List<Artist> artists = artistRepository.findByNameContainingIgnoreCase(query);
        for (Artist artist : artists) {
            results.add(artist.toResponseDTO());
        }

        // Search for albums
        List<Album> albums = albumRepository.findByAlbumNameContainingIgnoreCase(query);
        for (Album album : albums) {
            results.add(album.toResponseDTO());
        }

        // Search for songs
        List<Song> songs = songRepository.findBySongNameContainingIgnoreCase(query);
        for (Song song : songs) {
            results.add(song.toResponseDTO());
        }

        // Search for vibes
        List<Vibe> vibes = vibeRepository.findByNameContainingIgnoreCase(query);
        for (Vibe vibe : vibes) {
            results.add(vibe.toResponseDTO());
        }

        // Search for genres
        List<Genre> genres = genreRepository.findByNameContainingIgnoreCase(query);
        for (Genre genre : genres) {
            results.add(genre.toResponseDTO());
        }

        return results;
    }

}