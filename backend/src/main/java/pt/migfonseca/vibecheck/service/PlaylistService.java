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

    private List<Song> applyFilters(List<Song> songs, Set<Song> existingSongs, SettingsDTO settings, List<String> newSongsFilter) {
        songs = songs.stream()
                .filter(song -> !existingSongs.contains(song))
                .collect(Collectors.toList());
    
        // Apply new songs filter
        if (newSongsFilter != null) {
            songs = songs.stream()
                    .filter(song -> !newSongsFilter.contains(song.getSpotifyId()))
                    .collect(Collectors.toList());
        }
    
        // Apply settings filter
        if (settings.getMaxDuration() != null) {
            songs = songs.stream()
                    .filter(song -> song.getDuration() <= settings.getMaxDuration() * 60 * 1000)
                    .collect(Collectors.toList());
        }
        
        return songs;
    }    

    private Set<Song> selectRandomAndPopularArtistSongs(Artist artist, Set<Song> existingSongs, int count, SettingsDTO settings, List<String> newSongsFilter) {
        Set<Song> allSongsByArtist = new HashSet<>(songRepository.findAllByArtist(artist));
        Set<Song> selectedSongs = new HashSet<>();
        Random random = new Random();
        List<Song> allSongsList = new ArrayList<>(allSongsByArtist);

        // Filter out existing songs
        allSongsList.removeAll(existingSongs);

        // Apply new songs filter
        if (newSongsFilter != null) {
            allSongsList = allSongsList.stream()
                    .filter(song -> !newSongsFilter.contains(song.getSpotifyId()))
                    .collect(Collectors.toList());
        }

        // Apply settings filter
        if (settings.getMaxDuration() != null) {
            allSongsList = allSongsList.stream()
                    .filter(song -> song.getDuration() <= settings.getMaxDuration() * 60 * 1000)
                    .collect(Collectors.toList());
        }

        // Determine the minimum and maximum number of songs by the artist
        int minSongsByArtist = Math.max(count, settings.getMinSongsPerArtist() != null ? settings.getMinSongsPerArtist().getOrDefault(artist.getName(), 0) : 0);
        int maxSongsByArtist = settings.getMaxSongsPerArtist() != null ? settings.getMaxSongsPerArtist().getOrDefault(artist.getName(), Integer.MAX_VALUE) : Integer.MAX_VALUE;

        if (minSongsByArtist > maxSongsByArtist) {
            throw new IllegalArgumentException("Minimum songs by artist cannot be greater than the maximum.");
        }

        // Ensure the number of songs to be selected is within the allowed range
        int initialSelectionCount = Math.min(minSongsByArtist * 3, allSongsList.size());

        // Select random songs first
        while (selectedSongs.size() < initialSelectionCount) {
            int randomIndex = random.nextInt(allSongsList.size());
            Song song = allSongsList.get(randomIndex);
            if (selectedSongs.add(song)) {
                allSongsList.remove(randomIndex);
            }
        }

        // Sort the selected songs by popularity
        List<Song> sortedSelectedSongs = new ArrayList<>(selectedSongs);
        sortedSelectedSongs.sort(Comparator.comparingInt(Song::getPopularity).reversed());

        // Limit the selected songs to the required amount, considering the maximum limit
        return sortedSelectedSongs.stream()
                .limit(Math.min(maxSongsByArtist, minSongsByArtist))
                .collect(Collectors.toCollection(HashSet::new));
    }

    private boolean canAddSongWithDuration(long currentDurationMs, Song song, SettingsDTO settings) {
        long maxDurationMs = (settings.getMaxDuration() != null ? settings.getMaxDuration() : Integer.MAX_VALUE) * 60 * 1000;
        return currentDurationMs + song.getDuration() <= maxDurationMs;
    }    

    private Set<Song> getFeaturingArtistSongs(Artist artist, Set<Song> existingSongs, int minCount, int maxCount, SettingsDTO settings, List<String> newSongsFilter) {
        Set<Artist> featuringArtists = songRepository.findAllByArtist(artist).stream()
                .flatMap(song -> song.getArtists().stream())
                .filter(featArtist -> !featArtist.equals(artist))
                .collect(Collectors.toSet());
    
        List<Song> featuringSongs = songRepository.findAll().stream()
                .filter(song -> song.getArtists().stream().anyMatch(featuringArtists::contains))
                .collect(Collectors.toList());
    
        featuringSongs = applyFilters(featuringSongs, existingSongs, settings, newSongsFilter);
    
        Collections.shuffle(featuringSongs);
    
        Set<Song> resultSongs = new HashSet<>();
        long currentDurationMs = existingSongs.stream().mapToLong(Song::getDuration).sum();
    
        for (Song song : featuringSongs) {
            if (canAddSongWithDuration(currentDurationMs, song, settings)) {
                resultSongs.add(song);
                currentDurationMs += song.getDuration();
                if (resultSongs.size() >= maxCount) break;
            }
        }
    
        while (resultSongs.size() < minCount && !featuringSongs.isEmpty()) {
            Song song = featuringSongs.remove(0);
            if (!resultSongs.contains(song) && canAddSongWithDuration(currentDurationMs, song, settings)) {
                resultSongs.add(song);
                currentDurationMs += song.getDuration();
            }
        }
    
        return resultSongs;
    }
    
    private Set<Song> getTopGenreSongs(Artist artist, Set<Song> existingSongs, int minCount, int maxCount, SettingsDTO settings, List<String> newSongsFilter) {
        Set<Genre> topGenres = artist.getGenreRatings().stream()
                .sorted(Comparator.comparingDouble(GenreRating::getRating).reversed())
                .map(GenreRating::getGenre)
                .limit(3)
                .collect(Collectors.toSet());
    
        List<Song> genreSongs = songRepository.findAll().stream()
                .filter(song -> song.getGenreRatings().stream().map(GenreRating::getGenre).anyMatch(topGenres::contains))
                .collect(Collectors.toList());
    
        genreSongs = applyFilters(genreSongs, existingSongs, settings, newSongsFilter);
    
        Collections.shuffle(genreSongs);
    
        Set<Song> resultSongs = new HashSet<>();
        long currentDurationMs = existingSongs.stream().mapToLong(Song::getDuration).sum();
    
        for (Song song : genreSongs) {
            if (canAddSongWithDuration(currentDurationMs, song, settings)) {
                resultSongs.add(song);
                currentDurationMs += song.getDuration();
                if (resultSongs.size() >= maxCount) break;
            }
        }
    
        while (resultSongs.size() < minCount && !genreSongs.isEmpty()) {
            Song song = genreSongs.remove(0);
            if (!resultSongs.contains(song) && canAddSongWithDuration(currentDurationMs, song, settings)) {
                resultSongs.add(song);
                currentDurationMs += song.getDuration();
            }
        }
    
        return resultSongs;
    }
    
    private Set<Song> getTopVibesSongs(Artist artist, Set<Song> existingSongs, int minCount, int maxCount, SettingsDTO settings, List<String> newSongsFilter) {
        Set<Vibe> topVibes = artist.getVibeRatings().stream()
                .sorted(Comparator.comparingDouble(VibeRating::getRating).reversed())
                .map(VibeRating::getVibe)
                .limit(3)
                .collect(Collectors.toSet());
    
        List<Song> vibeSongs = songRepository.findAll().stream()
                .filter(song -> song.getVibeRatings().stream().map(VibeRating::getVibe).anyMatch(topVibes::contains))
                .collect(Collectors.toList());
    
        vibeSongs = applyFilters(vibeSongs, existingSongs, settings, newSongsFilter);
    
        Collections.shuffle(vibeSongs);
    
        Set<Song> resultSongs = new HashSet<>();
        long currentDurationMs = existingSongs.stream().mapToLong(Song::getDuration).sum();
    
        for (Song song : vibeSongs) {
            if (canAddSongWithDuration(currentDurationMs, song, settings)) {
                resultSongs.add(song);
                currentDurationMs += song.getDuration();
                if (resultSongs.size() >= maxCount) break;
            }
        }
    
        while (resultSongs.size() < minCount && !vibeSongs.isEmpty()) {
            Song song = vibeSongs.remove(0);
            if (!resultSongs.contains(song) && canAddSongWithDuration(currentDurationMs, song, settings)) {
                resultSongs.add(song);
                currentDurationMs += song.getDuration();
            }
        }
    
        return resultSongs;
    }
    
    private Set<Song> finishOffPlaylist(Artist artist, Set<Song> allSongs, int finalCount, SettingsDTO settings, List<String> newSongsFilter) {
        int currentSize = allSongs.size();
        int remainingCount = finalCount - currentSize;
    
        if (remainingCount <= 0) {
            return allSongs;
        }
    
        Set<Song> additionalSongs = new HashSet<>();
    
        // Add minimum artist songs
        additionalSongs.addAll(generateMinimumSongs(artist, allSongs, settings, "artist", newSongsFilter));
        additionalSongs = ensureDurationWithinLimit(additionalSongs, allSongs, settings);
    
        // Add minimum genre songs
        if (additionalSongs.size() < remainingCount) {
            additionalSongs.addAll(generateMinimumSongs(artist, allSongs, settings, "genre", newSongsFilter));
            additionalSongs = ensureDurationWithinLimit(additionalSongs, allSongs, settings);
        }
    
        // Add minimum vibe songs
        if (additionalSongs.size() < remainingCount) {
            additionalSongs.addAll(generateMinimumSongs(artist, allSongs, settings, "vibe", newSongsFilter));
            additionalSongs = ensureDurationWithinLimit(additionalSongs, allSongs, settings);
        }
    
        // Add remaining songs to fill the count, considering all types
        if (additionalSongs.size() < remainingCount) {
            additionalSongs.addAll(selectRandomAndPopularArtistSongs(artist, allSongs, remainingCount - additionalSongs.size(), settings, newSongsFilter));
            additionalSongs = ensureDurationWithinLimit(additionalSongs, allSongs, settings);
    
            if (additionalSongs.size() < remainingCount) {
                additionalSongs.addAll(getTopGenreSongs(artist, allSongs, remainingCount - additionalSongs.size(), remainingCount - additionalSongs.size(), settings, newSongsFilter));
                additionalSongs = ensureDurationWithinLimit(additionalSongs, allSongs, settings);
            }
    
            if (additionalSongs.size() < remainingCount) {
                additionalSongs.addAll(getTopVibesSongs(artist, allSongs, remainingCount - additionalSongs.size(), remainingCount - additionalSongs.size(), settings, newSongsFilter));
                additionalSongs = ensureDurationWithinLimit(additionalSongs, allSongs, settings);
            }
    
            if (additionalSongs.size() < remainingCount) {
                additionalSongs.addAll(getFeaturingArtistSongs(artist, allSongs, remainingCount - additionalSongs.size(), remainingCount - additionalSongs.size(), settings, newSongsFilter));
                additionalSongs = ensureDurationWithinLimit(additionalSongs, allSongs, settings);
            }
        }
    
        allSongs.addAll(additionalSongs);
        return allSongs;
    }
    
    private Set<Song> generateMinimumSongs(Artist artist, Set<Song> allSongs, SettingsDTO settings, String type, List<String> newSongsFilter) {
        Set<Song> minimumSongs = new HashSet<>();
    
        switch (type) {
            case "artist":
                int minSongsByArtist = settings.getMinSongsPerArtist() != null ? settings.getMinSongsPerArtist().getOrDefault(artist.getName(), 0) : 0;
                int maxSongsByArtist = settings.getMaxSongsPerArtist() != null ? settings.getMaxSongsPerArtist().getOrDefault(artist.getName(), Integer.MAX_VALUE) : Integer.MAX_VALUE;
    
                if (minSongsByArtist > maxSongsByArtist) {
                    throw new IllegalArgumentException("Minimum songs by artist cannot be greater than the maximum.");
                }
    
                minimumSongs.addAll(selectRandomAndPopularArtistSongs(artist, allSongs, minSongsByArtist, settings, newSongsFilter));
                break;
    
            case "genre":
                int minSongsPerGenre = settings.getMinSongsPerGenre() != null ? settings.getMinSongsPerGenre().values().stream().mapToInt(Integer::intValue).sum() : 0;
                int maxSongsPerGenre = settings.getMaxSongsPerGenre() != null ? settings.getMaxSongsPerGenre().values().stream().mapToInt(Integer::intValue).sum() : Integer.MAX_VALUE;
    
                if (minSongsPerGenre > maxSongsPerGenre) {
                    throw new IllegalArgumentException("Minimum songs per genre cannot be greater than the maximum.");
                }
    
                minimumSongs.addAll(getTopGenreSongs(artist, allSongs, minSongsPerGenre, maxSongsPerGenre, settings, newSongsFilter));
                break;
    
            case "vibe":
                int minSongsPerVibe = settings.getMinSongsPerVibe() != null ? settings.getMinSongsPerVibe().values().stream().mapToInt(Integer::intValue).sum() : 0;
                int maxSongsPerVibe = settings.getMaxSongsPerVibe() != null ? settings.getMaxSongsPerVibe().values().stream().mapToInt(Integer::intValue).sum() : Integer.MAX_VALUE;
    
                if (minSongsPerVibe > maxSongsPerVibe) {
                    throw new IllegalArgumentException("Minimum songs per vibe cannot be greater than the maximum.");
                }
    
                minimumSongs.addAll(getTopVibesSongs(artist, allSongs, minSongsPerVibe, maxSongsPerVibe, settings, newSongsFilter));
                break;
        }
    
        return minimumSongs;
    }
    
    private Set<Song> ensureDurationWithinLimit(Set<Song> additionalSongs, Set<Song> allSongs, SettingsDTO settings) {
        List<Song> additionalSongsList = new ArrayList<>(additionalSongs);
        Collections.shuffle(additionalSongsList);

        long currentDurationMs = allSongs.stream().mapToLong(Song::getDuration).sum();
        long maxDurationMs = (settings.getMaxDuration() != null ? (settings.getMaxDuration() + 5) * 60 * 1000 : Long.MAX_VALUE);

        Set<Song> resultSongs = new HashSet<>();
        for (Song song : additionalSongsList) {
            if (currentDurationMs + song.getDuration() <= maxDurationMs) {
                resultSongs.add(song);
                currentDurationMs += song.getDuration();
            } else {
                // Try to add another song that fits within the remaining time
                for (Song replacement : additionalSongsList) {
                    if (!resultSongs.contains(replacement) && currentDurationMs + replacement.getDuration() <= maxDurationMs) {
                        resultSongs.add(replacement);
                        currentDurationMs += replacement.getDuration();
                        break;
                    }
                }
            }
        }
        return resultSongs;
    }

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

        Playlist playlist = createPlaylist("Playlist for " + query, allSongs);
        setRatings(playlist, allSongs);

        return playlist.toDto();
    }

    private Set<Song> handleArtistType(String query, SettingsDTO settings, List<String> newSongsFilter) throws IOException {
        Optional<Artist> queryArtist = artistRepository.findByName(query);
        if (!queryArtist.isPresent()) {
            throw new IOException("Artist not found.");
        }

        int fraction = 4;
        int songCount = settings.getNumSongs() != null ? settings.getNumSongs() * fraction : (settings.getMaxDuration() != null ? settings.getMaxDuration() * 4 * fraction : 0);
        Set<Song> allSongs = new HashSet<>();

        // Select random and popular songs
        Set<Song> selectedSongs = selectRandomAndPopularArtistSongs(queryArtist.orElseThrow(() -> new IOException("Artist not found.")), allSongs, songCount / (fraction * fraction), settings, newSongsFilter);
        allSongs.addAll(selectedSongs);

        // Add songs from featuring artists
        Set<Song> featuringSongs = getFeaturingArtistSongs(queryArtist.orElseThrow(() -> new IOException("Artist not found.")), allSongs, songCount / (fraction * fraction), songCount, settings, newSongsFilter);
        allSongs.addAll(featuringSongs);

        // Add top genre songs
        Set<Song> genreSongs = getTopGenreSongs(queryArtist.orElseThrow(() -> new IOException("Artist not found.")), allSongs, songCount / (fraction * fraction), songCount, settings, newSongsFilter);
        allSongs.addAll(genreSongs);

        // Add top vibe songs
        Set<Song> vibeSongs = getTopVibesSongs(queryArtist.orElseThrow(() -> new IOException("Artist not found.")), allSongs, songCount / (fraction * fraction), songCount, settings, newSongsFilter);
        allSongs.addAll(vibeSongs);

        // Finish off the song count
        allSongs = finishOffPlaylist(queryArtist.orElseThrow(() -> new IOException("Artist not found.")), allSongs, settings.getNumSongs() != null ? settings.getNumSongs() : settings.getMaxDuration() * 4, settings, newSongsFilter);

        return allSongs;
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
