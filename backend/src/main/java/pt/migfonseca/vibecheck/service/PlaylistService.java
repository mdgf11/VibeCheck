package pt.migfonseca.vibecheck.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import pt.migfonseca.vibecheck.dto.PlaylistDTO;
import pt.migfonseca.vibecheck.dto.SettingsDTO;
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
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.PagingCursorbased;
import se.michaelthelin.spotify.model_objects.specification.PlayHistory;
import se.michaelthelin.spotify.requests.data.player.GetCurrentUsersRecentlyPlayedTracksRequest;

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

    @Autowired
    private UserService userService;

     // Method to be added
     private List<Song> selectRandomArtistSongs(List<Song> allSongs, int count) {
        Random random = new Random();
        
        return random.ints(0, allSongs.size())
                .distinct()
                .limit(count)
                .mapToObj(allSongs::get)
                .collect(Collectors.toList());
    }

    // Method to be added
    private List<Song> selectPopularSongs(List<Song> songs, int count) {
        return songs.stream()
                .sorted(Comparator.comparingInt(Song::getPopularity).reversed())
                .limit(count)
                .collect(Collectors.toList());
    }

    // Method to be added
    private List<Song> getTopGenreSongs(Artist artist, int count) {
        Set<Genre> topGenres = artist.getGenreRatings().stream()
                .sorted(Comparator.comparingDouble(GenreRating::getRating).reversed())
                .map(GenreRating::getGenre)
                .limit(3)
                .collect(Collectors.toSet());

        List<Song> genreSongs = songRepository.findAll().stream()
                .filter(song -> song.getArtists().stream().noneMatch(a -> a.equals(artist)))
                .filter(song -> song.getGenreRatings().stream().map(GenreRating::getGenre).anyMatch(topGenres::contains))
                .collect(Collectors.toList());

        Collections.shuffle(genreSongs);

        return genreSongs.stream()
                .limit(count)
                .sorted(Comparator.comparingInt(Song::getPopularity).reversed())
                .collect(Collectors.toList());
    }

    // Modified method
    public PlaylistDTO getPlaylist(String query, String artistString, String type, SettingsDTO settings) throws IOException {
        Optional<Artist> artist = artistRepository.findByName(artistString);
        List<Song> allSongs;

        switch (type) {
            case "artist":
                allSongs = artistRepository.findByName(query)
                        .map(this::getAllSongsByArtist)
                        .orElseThrow(() -> new IOException("Query not found."));
                int fraction = 4;
                int songCount = settings.getNumSongs() != null ? settings.getNumSongs() * fraction : (settings.getMaxDuration() != null ? settings.getMaxDuration() * 4 * fraction : allSongs.size());
                List<Song> selectedSongs = selectRandomArtistSongs(allSongs, songCount);
                List<Song> popularSongs = selectPopularSongs(selectedSongs, selectedSongs.size() / (fraction*fraction));
                allSongs = popularSongs;
                fraction = 3;
                songCount = settings.getNumSongs() != null ? settings.getNumSongs() * fraction : (settings.getMaxDuration() != null ? settings.getMaxDuration() * 4 * fraction : allSongs.size());
                List<Song> genreSongs = getTopGenreSongs(artist.orElseThrow(() -> new IOException("Artist not found.")), songCount / (fraction*fraction));
                allSongs.addAll(genreSongs);

                break;
            case "album":
                if (!artist.isPresent()) throw new IOException("Query not found.");
                allSongs = albumRepository.findByNameWithArtist(query, artist.get())
                        .map(album -> getAllSongsByAlbum(album, artist.get()))
                        .orElseThrow(() -> new IOException("Query not found."));
                break;
            case "song":
                if (!artist.isPresent()) throw new IOException("Query not found.");
                allSongs = songRepository.findByNameWithArtist(query, artist.get())
                        .map(List::of)
                        .orElseThrow(() -> new IOException("Query not found."));
                break;
            case "genre":
                allSongs = genreRepository.findByName(query)
                        .map(this::getAllSongsByGenre)
                        .orElseThrow(() -> new IOException("Query not found."));
                break;
            case "vibe":
                allSongs = vibeRepository.findByName(query)
                        .map(this::getAllSongsByVibe)
                        .orElseThrow(() -> new IOException("Query not found."));
                break;
            default:
                throw new IOException("Invalid type.");
        }

        allSongs = filterSongsBySettings(allSongs, settings);
        
        Playlist playlist = createPlaylist("Playlist for " + query, allSongs);
        setRatings(playlist, allSongs);

        return playlist.toDto();
    }

    private boolean isUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
    }

    private List<Song> filterSongsBySpotifyPlaybackHistory(List<Song> songs) {
        String accessToken = userService.getSpotifyAccessToken();
        if (accessToken == null) {
            return songs;
        }
        List<String> playedTrackIds = getUserPlaybackHistory(accessToken);
    
        return songs.stream()
                .filter(song -> playedTrackIds.contains(song.getSpotifyId()))
                .collect(Collectors.toList());
    }

    private List<String> getUserPlaybackHistory(String accessToken) {
        SpotifyApi spotifyApi = new SpotifyApi.Builder()
                .setAccessToken(accessToken)
                .build();
        
        List<String> trackIds = new ArrayList<>();
        int limit = 50;
        boolean doExtraIteration = false;

        try {
            for (int i = 0; i < 2; i++) { // Perform up to 2 iterations
                GetCurrentUsersRecentlyPlayedTracksRequest request = spotifyApi.getCurrentUsersRecentlyPlayedTracks()
                    .limit(limit)
                    .build();
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Thread was interrupted, Failed to complete operation");
                }
                PagingCursorbased<PlayHistory> playHistoryPaging = request.execute();
                
                trackIds.addAll(parseTrackIdsFromResponse(playHistoryPaging));

                // If the number of items retrieved is less than the limit, perform one extra iteration
                if (playHistoryPaging.getItems().length < limit) {
                    if (doExtraIteration) {
                        break;
                    } else {
                        doExtraIteration = true;
                    }
                }
            }
        } catch (IOException | SpotifyWebApiException | ParseException e) {
            e.printStackTrace();
        }
        return trackIds;
    }
    
    private List<String> parseTrackIdsFromResponse(PagingCursorbased<PlayHistory> playHistoryPaging) {
        List<String> trackIds = new ArrayList<>();
        for (PlayHistory playHistory : playHistoryPaging.getItems()) {
            trackIds.add(playHistory.getTrack().getId());
        }
        return trackIds;
    }

    private List<Song> getAllSongsByArtist(Artist artist) {
        return songRepository.findAllByArtist(artist);
    }

    private List<Song> getAllSongsByAlbum(Album album, Artist artist) {
        return songRepository.findAllByAlbumAndArtist(album, artist);
    }

    private List<Song> getAllSongsByGenre(Genre genre) {
        return songRepository.findAllByGenre(genre);
    }

    private List<Song> getAllSongsByVibe(Vibe vibe) {
        return songRepository.findAllByVibe(vibe);
    }

    private List<Song> filterSongsBySettings(List<Song> songs, SettingsDTO settings) {
        if (settings.getNewSongs() != null) {
            if (isUserLoggedIn()) {
                songs = filterSongsBySpotifyPlaybackHistory(songs);
            }
        }
        if (settings.getMaxDuration() != null) {
            songs = songs.stream()
                    .filter(song -> song.getDuration() <= settings.getMaxDuration())
                    .collect(Collectors.toList());
        }
        if (settings.getNumSongs() != null) {
            songs = songs.stream()
                    .limit(settings.getNumSongs())
                    .collect(Collectors.toList());
        }
        if (settings.getMinSongsPerArtist() != null) {
            for (Map.Entry<String, Integer> entry : settings.getMinSongsPerArtist().entrySet()) {
                songs = applyArtistConstraint(songs, entry.getKey(), entry.getValue(), true);
            }
        }
        if (settings.getMaxSongsPerArtist() != null) {
            for (Map.Entry<String, Integer> entry : settings.getMaxSongsPerArtist().entrySet()) {
                songs = applyArtistConstraint(songs, entry.getKey(), entry.getValue(), false);
            }
        }
        if (settings.getMinSongsPerGenre() != null) {
            for (Map.Entry<String, Integer> entry : settings.getMinSongsPerGenre().entrySet()) {
                songs = applyGenreConstraint(songs, entry.getKey(), entry.getValue(), true);
            }
        }
        if (settings.getMaxSongsPerGenre() != null) {
            for (Map.Entry<String, Integer> entry : settings.getMaxSongsPerGenre().entrySet()) {
                songs = applyGenreConstraint(songs, entry.getKey(), entry.getValue(), false);
            }
        }
        if (settings.getMinSongsPerVibe() != null) {
            for (Map.Entry<String, Integer> entry : settings.getMinSongsPerVibe().entrySet()) {
                songs = applyVibeConstraint(songs, entry.getKey(), entry.getValue(), true);
            }
        }
        if (settings.getMaxSongsPerVibe() != null) {
            for (Map.Entry<String, Integer> entry : settings.getMaxSongsPerVibe().entrySet()) {
                songs = applyVibeConstraint(songs, entry.getKey(), entry.getValue(), false);
            }
        }
        return songs;
    }

    private Playlist createPlaylist(String name, List<Song> songs) {
        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setSongs(songs);
        Set<Artist> artists = songs.stream()
            .flatMap(song -> song.getArtists().stream())
            .collect(Collectors.toSet());
        playlist.setArtists(new ArrayList<>(artists));
        Set<Album> albums = songs.stream()
            .flatMap(song -> song.getAlbums().stream())
            .collect(Collectors.toSet());
        playlist.setAlbums(new ArrayList<>(albums));
        playlistRepository.save(playlist);
        return playlist;
    }

    private void setRatings(Playlist playlist, List<Song> allSongs) {
        Set<Genre> allGenres = extractGenresFromSongs(allSongs);
        Set<Vibe> allVibes = extractVibesFromSongs(allSongs);

        Map<Genre, Double> genreRatings = calculateAverageGenreRatings(allSongs, allGenres, GenreRating::getGenre);
        Map<Vibe, Double> vibeRatings = calculateAverageVibeRatings(allSongs, allVibes, VibeRating::getVibe);

        List<GenreRating> genreRatingList = genreRatings.entrySet().stream()
                .map(entry -> new GenreRating(playlist, entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        playlist.setGenreRatings(genreRatingList);

        List<VibeRating> vibeRatingList = vibeRatings.entrySet().stream()
                .map(entry -> new VibeRating(playlist, entry.getKey(), entry.getValue()))
                .collect(Collectors.toList());
        playlist.setVibeRatings(vibeRatingList);
    }

    private Set<Genre> extractGenresFromSongs(List<Song> songs) {
        return songs.stream()
                .flatMap(song -> song.getGenreRatings().stream())
                .map(GenreRating::getGenre)
                .collect(Collectors.toSet());
    }

    private Set<Vibe> extractVibesFromSongs(List<Song> songs) {
        return songs.stream()
                .flatMap(song -> song.getVibeRatings().stream())
                .map(VibeRating::getVibe)
                .collect(Collectors.toSet());
    }

    private <T> Map<T, Double> calculateAverageGenreRatings(List<Song> songs, Set<T> items, Function<GenreRating, T> keyExtractor) {
        return items.stream()
                .collect(Collectors.toMap(
                        item -> item,
                        item -> songs.stream()
                                .flatMap(song -> song.getGenreRatings().stream())
                                .filter(gr -> keyExtractor.apply(gr).equals(item))
                                .mapToDouble(GenreRating::getRating)
                                .average()
                                .orElse(0.0)
                ));
    }

    private <T> Map<T, Double> calculateAverageVibeRatings(List<Song> songs, Set<T> items, Function<VibeRating, T> keyExtractor) {
        return items.stream()
                .collect(Collectors.toMap(
                        item -> item,
                        item -> songs.stream()
                                .flatMap(song -> song.getVibeRatings().stream())
                                .filter(gr -> keyExtractor.apply(gr).equals(item))
                                .mapToDouble(VibeRating::getRating)
                                .average()
                                .orElse(0.0)
                ));
    }

    private List<Song> applyArtistConstraint(List<Song> songs, String artistName, int limit, boolean min) {
        Map<Artist, List<Song>> songsByArtist = songs.stream()
                .collect(Collectors.groupingBy(song -> song.getArtists().stream().filter(artist -> artist.getName().equals(artistName)).findFirst().orElse(null)));

        return songsByArtist.entrySet().stream()
                .filter(entry -> entry.getKey() != null && (min ? entry.getValue().size() >= limit : entry.getValue().size() <= limit))
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toList());
    }

    private List<Song> applyGenreConstraint(List<Song> songs, String genreName, int limit, boolean min) {
        Map<Genre, List<Song>> songsByGenre = songs.stream()
                .flatMap(song -> song.getGenreRatings().stream())
                .filter(genreRating -> genreRating.getGenre().getName().equals(genreName))
                .collect(Collectors.groupingBy(GenreRating::getGenre, Collectors.mapping(gr -> (Song) gr.getRaterEntity(), Collectors.toList())));

        return songsByGenre.entrySet().stream()
                .filter(entry -> min ? entry.getValue().size() >= limit : entry.getValue().size() <= limit)
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toList());
    }

    private List<Song> applyVibeConstraint(List<Song> songs, String vibeName, int limit, boolean min) {
        Map<Vibe, List<Song>> songsByVibe = songs.stream()
                .flatMap(song -> song.getVibeRatings().stream())
                .filter(vibeRating -> vibeRating.getVibe().getName().equals(vibeName))
                .collect(Collectors.groupingBy(VibeRating::getVibe, Collectors.mapping(vr -> (Song) vr.getRaterEntity(), Collectors.toList())));

        return songsByVibe.entrySet().stream()
                .filter(entry -> min ? entry.getValue().size() >= limit : entry.getValue().size() <= limit)
                .flatMap(entry -> entry.getValue().stream())
                .collect(Collectors.toList());
    }
}
