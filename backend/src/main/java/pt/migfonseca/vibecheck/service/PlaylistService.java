package pt.migfonseca.vibecheck.service;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import pt.migfonseca.vibecheck.dto.PlaylistDTO;
import pt.migfonseca.vibecheck.dto.SettingsDTO;
import pt.migfonseca.vibecheck.model.*;
import pt.migfonseca.vibecheck.model.ratings.GenreRating;
import pt.migfonseca.vibecheck.model.ratings.VibeRating;
import pt.migfonseca.vibecheck.repo.*;
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

    public PlaylistDTO getPlaylist(String query, String artistString, String type, SettingsDTO settings) throws IOException {
        Optional<Artist> artist = artistRepository.findByName(artistString);
        Set<Song> allSongs = new HashSet<>();

        List<String> newSongsFilter = null;
        if (isUserLoggedIn() && settings.getNewSongs() != null && settings.getNewSongs()) {
            newSongsFilter = getUserPlaybackHistory(userService.getSpotifyAccessToken());
        }

        switch (type) {
            case "artist":
                allSongs = handleArtistType(query, settings, newSongsFilter);
                break;
            case "album":
                if (!artist.isPresent())
                    throw new IOException("Query not found.");
                allSongs = albumRepository.findByNameWithArtist(query, artist.get())
                        .map(album -> getAllSongsByAlbum(album, artist.get()))
                        .map(HashSet::new)
                        .orElseThrow(() -> new IOException("Query not found."));
                break;
            case "song":
                if (!artist.isPresent()) throw new IOException("Query not found.");
                allSongs = songRepository.findByNameWithArtist(query, artist.get())
                        .map(Collections::singleton)
                        .map(HashSet::new)
                        .orElseThrow(() -> new IOException("Query not found."));
                break;
            case "genre":
                allSongs = genreRepository.findByName(query)
                        .map(this::getAllSongsByGenre)
                        .map(HashSet::new)
                        .orElseThrow(() -> new IOException("Query not found."));
                break;
            case "vibe":
                allSongs = vibeRepository.findByName(query)
                        .map(this::getAllSongsByVibe)
                        .map(HashSet::new)
                        .orElseThrow(() -> new IOException("Query not found."));
                break;
            default:
                throw new IOException("Invalid type.");
        }

        List<Song> shuffledSongs = new ArrayList<>(allSongs);
        Collections.shuffle(shuffledSongs);
        Playlist playlist = createPlaylist("Playlist for " + query, new HashSet<>(shuffledSongs));
        setRatings(playlist, new HashSet<>(shuffledSongs));

        return playlist.toDto();
    }

    private Set<Song> handleArtistType(String query, SettingsDTO settings, List<String> newSongsFilter) throws IOException {
        Optional<Artist> queryArtist = artistRepository.findByName(query);
        if (!queryArtist.isPresent()) {
            throw new IOException("Artist not found.");
        }

        int songCount = settings.getNumSongs() != null ? settings.getNumSongs() : Integer.MAX_VALUE;
        long maxDurationMs = settings.getMaxDuration() != null ? settings.getMaxDuration() * 60 * 1000 : Long.MAX_VALUE;

        Set<Song> allSongs = new HashSet<>();

        // Fetch all songs related to the artist in a single query
        List<Song> artistSongs = songRepository.findAllByArtist(queryArtist.get());
        List<Song> featuringSongs = songRepository.findAllByArtistsIn(queryArtist.get().getFeaturingArtists());
        List<Song> genreSongs = songRepository.findAllByGenresIn(queryArtist.get().getTopGenres(3));
        List<Song> vibeSongs = songRepository.findAllByVibesIn(queryArtist.get().getTopVibes(3));

        // Combine all the songs into a single list
        List<Song> allFetchedSongs = new ArrayList<>();
        Collections.shuffle(artistSongs);
        allFetchedSongs.addAll(artistSongs);
        allFetchedSongs.addAll(featuringSongs);
        allFetchedSongs.addAll(genreSongs);
        allFetchedSongs.addAll(vibeSongs);

        // Filter and select songs based on number of songs and maximum duration
        final Set<Song> finalAllSongs = allSongs;

        Set<Song> selectedSongs = allFetchedSongs.stream()
                .filter(song -> !finalAllSongs.contains(song) &&
                        (newSongsFilter == null || !newSongsFilter.contains(song.getSpotifyId())))
                .limit(songCount)
                .collect(Collectors.toSet());

        allSongs.addAll(selectedSongs);

        // Ensure the playlist meets the exact number of songs or the maximum duration
        if (settings.getNumSongs() != null) {
            allSongs = adjustSongCount(allSongs, settings.getNumSongs(), maxDurationMs);
        } else {
            allSongs = adjustDuration(allSongs, maxDurationMs);
        }

        return allSongs;
    }

    private Set<Song> adjustSongCount(Set<Song> songs, int targetCount, long maxDurationMs) {
        List<Song> songList = new ArrayList<>(songs);
        Collections.shuffle(songList);

        Set<Song> adjustedSongs = new HashSet<>();
        long currentDurationMs = 0;

        for (Song song : songList) {
            if (adjustedSongs.size() < targetCount && currentDurationMs + song.getDuration() <= maxDurationMs) {
                adjustedSongs.add(song);
                currentDurationMs += song.getDuration();
            }
        }

        return adjustedSongs;
    }

    private Set<Song> adjustDuration(Set<Song> songs, long maxDurationMs) {
        List<Song> songList = new ArrayList<>(songs);
        Collections.shuffle(songList);

        Set<Song> adjustedSongs = new HashSet<>();
        long currentDurationMs = 0;

        for (Song song : songList) {
            if (currentDurationMs + song.getDuration() <= maxDurationMs) {
                adjustedSongs.add(song);
                currentDurationMs += song.getDuration();
            }
        }

        return adjustedSongs;
    }

    private boolean isUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.isAuthenticated();
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

    private Set<Song> getAllSongsByAlbum(Album album, Artist artist) {
        return new HashSet<>(songRepository.findAllByAlbumAndArtist(album, artist));
    }

    private Set<Song> getAllSongsByGenre(Genre genre) {
        return new HashSet<>(songRepository.findAllByGenre(genre));
    }

    private Set<Song> getAllSongsByVibe(Vibe vibe) {
        return new HashSet<>(songRepository.findAllByVibe(vibe));
    }

    private Playlist createPlaylist(String name, Set<Song> songs) {
        Playlist playlist = new Playlist();
        playlist.setName(name);
        playlist.setSongs(new ArrayList<>(songs));
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

    private void setRatings(Playlist playlist, Set<Song> allSongs) {
        Set<Genre> allGenres = extractGenresFromSongs(new ArrayList<>(allSongs));
        Set<Vibe> allVibes = extractVibesFromSongs(new ArrayList<>(allSongs));

        Map<Genre, Double> genreRatings = calculateAverageGenreRatings(new ArrayList<>(allSongs), allGenres, GenreRating::getGenre);
        Map<Vibe, Double> vibeRatings = calculateAverageVibeRatings(new ArrayList<>(allSongs), allVibes, VibeRating::getVibe);

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
}
