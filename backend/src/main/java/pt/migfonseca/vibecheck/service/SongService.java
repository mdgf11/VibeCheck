package pt.migfonseca.vibecheck.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import pt.migfonseca.vibecheck.dto.ArtistDTO;
import pt.migfonseca.vibecheck.dto.SongDTO;
import pt.migfonseca.vibecheck.model.Album;
import pt.migfonseca.vibecheck.model.Artist;
import pt.migfonseca.vibecheck.model.Genre;
import pt.migfonseca.vibecheck.model.Image;
import pt.migfonseca.vibecheck.model.Song;
import pt.migfonseca.vibecheck.model.ratings.GenreRating;
import pt.migfonseca.vibecheck.repo.AlbumRepository;
import pt.migfonseca.vibecheck.repo.ArtistRepository;
import pt.migfonseca.vibecheck.repo.GenreRatingRepository;
import pt.migfonseca.vibecheck.repo.GenreRepository;
import pt.migfonseca.vibecheck.repo.SongRepository;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.ArtistSimplified;
import se.michaelthelin.spotify.model_objects.specification.Track;

@Service
public class SongService {

    @Autowired
    private ArtistRepository artistRepository;

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private SongRepository songRepository;

    @Autowired
    private GenreRepository genreRepository;

    @Autowired
    private GenreRatingRepository genreRatingRepository;

    @Autowired
    private SpotifyTokenService spotifyTokenService;

    public List<ArtistDTO> getAllArtists() {
        return artistRepository.findAll().stream().map(Artist::toDTO).toList();
    }

    public List<ArtistDTO> getAllUndicoveredArtistsWithPopularity(int size, int popularity) {
        return artistRepository.findAll().stream()
                .filter(artist -> !artist.isDiscovered() && artist.getPopularity() >= popularity)
                .map(Artist::toDTO)
                .limit(size)
                .toList();
    }

    public List<SongDTO> getAllSongs() {
        return songRepository.findAll().stream().map(Song::toDTO).toList();
    }

    public List<ArtistDTO> addArtist(String name, int size, int popularity) throws IOException, SpotifyWebApiException, ParseException {
        List<Artist> newArtists = new LinkedList<>();
        se.michaelthelin.spotify.model_objects.specification.Artist artist = spotifyTokenService.searchArtist(name);
        addArtist(artist, size, popularity, newArtists);
        return newArtists.stream().map(Artist::toDTO).toList();
    }

    @Async
    @Transactional
    public void discover(int size, int popularity) throws IOException, SpotifyWebApiException, ParseException {
        List<Artist> allArtists = artistRepository.findAll();

        List<Artist> mutableArtists = new ArrayList<>(allArtists);

        for (Artist artist : mutableArtists) {
            artist.getAlbums().size();
        }

        mutableArtists = mutableArtists.stream()
                .filter(artist -> !artist.isDiscovered() && artist.getPopularity() >= popularity)
                .limit(size)
                .collect(Collectors.toCollection(ArrayList::new));

        mutableArtists.sort(Comparator.comparing(Artist::getPopularity).reversed());
        mutableArtists.forEach(artist -> System.out.println(artist.getName()));

        List<Artist> fullNewArtists = new LinkedList<>();

        for (Artist artist : mutableArtists) {
            List<Artist> newArtists = new LinkedList<>();
            addArtist(artist, size, popularity, newArtists);
            newArtists.stream().forEach(newArtist -> {
                if (!fullNewArtists.contains(newArtist)) {
                    fullNewArtists.add(newArtist);
                }
            });
        }
    }

    @Transactional
    public void addAlbums(Artist artist, int size, int popularity, List<Artist> newArtists) throws IOException, SpotifyWebApiException, ParseException {
        artist.setDiscovered(true);
        artistRepository.save(artist);

        try {
            List<Genre> artistGenres = artist.getGenreRatings().stream().map(GenreRating::getGenre).toList();

            List<se.michaelthelin.spotify.model_objects.specification.Album> albums = spotifyTokenService.getArtistAlbums(artist.getSpotifyId());
            for (se.michaelthelin.spotify.model_objects.specification.Album album : albums) {
                Optional<Album> albumOptional = albumRepository.findByNameWithArtist(album.getName(), artist);
                if (!albumOptional.isPresent()) {
                    albumOptional = albumRepository.findByNameWithFeature(album.getName(), artist);
                }

                if (albumOptional.isPresent()) {
                    for (Artist albumArtist : albumOptional.get().getArtists())
                        if (!albumArtist.isDiscovered())
                            addArtist(albumArtist, size, popularity, newArtists);
                    for (Artist albumArtist : albumOptional.get().getFeatures())
                        if (!albumArtist.isDiscovered())
                            addArtist(albumArtist, size, popularity, newArtists);
                    continue;
                }

                LocalDate releaseDate = parseReleaseDate(album.getReleaseDate());

                List<Artist> foundArtists = new ArrayList<>();
                List<String> newArtistIds = new ArrayList<>();
                for (ArtistSimplified artistObj : album.getArtists()) {
                    String albumArtistName = artistObj.getName();
                    Optional<Artist> albumArtist = artistRepository.findByName(albumArtistName);
                    if (!albumArtist.isPresent())
                        newArtistIds.add(artistObj.getId());
                    else if (!albumArtist.get().isDiscovered()) {
                        addArtist(albumArtist.get(), size, popularity, newArtists);
                        foundArtists.add(albumArtist.get());
                    } else
                        foundArtists.add(albumArtist.get());
                }
                List<Artist> albumArtists = new ArrayList<>();
                for (se.michaelthelin.spotify.model_objects.specification.Artist newArtist : spotifyTokenService.getArtistsFull(newArtistIds)) {
                    albumArtists.add(addArtist(newArtist, size, popularity, newArtists));
                }
                albumArtists.addAll(foundArtists);

                List<Song> albumSongs = new ArrayList<>();
                List<Artist> featuredArtists = new ArrayList<>();

                String albumName = album.getName();
                List<Genre> genres = new ArrayList<>(artistGenres);
                for (String genre : album.getGenres()) {
                    Optional<Genre> genreOptional = genreRepository.findByName(genre);
                    if (!genreOptional.isPresent()) {
                        genreOptional = Optional.of(new Genre(genre));
                        genreRepository.save(genreOptional.get());
                    }
                    genres.add(genreOptional.get());
                }
                trackAndFeatures(album, albumSongs, albumArtists, featuredArtists, genres, releaseDate, size, popularity, newArtists);

                Optional<Album> newAlbum = Optional.empty();
                for (Artist albumArtist : albumArtists) {
                    if (newAlbum.isPresent())
                        break;
                    newAlbum = albumRepository.findByNameWithArtist(albumName, albumArtist);
                }
                for (Artist featuredArtist : featuredArtists) {
                    if (newAlbum.isPresent())
                        break;
                    newAlbum = albumRepository.findByNameWithArtist(albumName, featuredArtist);
                }
                if (newAlbum.isPresent())
                    continue;
                if (albumSongs.size() <= 1) {
                    continue;
                }
                System.out.println("Saving album: " + albumName);
                List<Image> images = getImages(album.getImages());
                int newPopularity = album.getPopularity();
                newAlbum = Optional.of(new Album(albumName, albumSongs.stream().toList(), albumArtists.stream().toList(), featuredArtists.stream().toList(), images, newPopularity, releaseDate));
                albumRepository.save(newAlbum.get());
                for (Genre genre : genres)
                    genreRatingRepository.save(new GenreRating(newAlbum.get(), genre, 5));
            }
        } catch (IOException | SpotifyWebApiException e) {
            artist.setDiscovered(false);
            artistRepository.save(artist);
            throw e;
        }
    }

    @Transactional
    private void trackAndFeatures(se.michaelthelin.spotify.model_objects.specification.Album album, List<Song> albumSongs, List<Artist> albumArtists, List<Artist> featuredArtists, List<Genre> genres, LocalDate releaseDate, int size, int popularity, List<Artist> newArtists) throws IOException, SpotifyWebApiException, ParseException {

        List<Track> tracks = spotifyTokenService.getAlbumTrackObjects(album.getId());

        for (Track track : tracks) {

            List<Artist> foundArtists = new LinkedList<>();
            List<String> newArtistIds = new ArrayList<>();
            for (ArtistSimplified artistObj : track.getArtists()) {
                Optional<Artist> trackArtist = artistRepository.findByName(artistObj.getName());
                if (!trackArtist.isPresent()) {
                    newArtistIds.add(artistObj.getId());
                } else if (!trackArtist.get().isDiscovered()) {
                    addArtist(trackArtist.get(), size, popularity, newArtists);
                    foundArtists.add(trackArtist.get());
                } else {
                    foundArtists.add(trackArtist.get());
                }
            }

            List<Artist> trackArtists = new ArrayList<>();
            for (se.michaelthelin.spotify.model_objects.specification.Artist newArtist : spotifyTokenService.getArtistsFull(newArtistIds)) {
                trackArtists.add(addArtist(newArtist, size, popularity, newArtists));
            }
            trackArtists.addAll(foundArtists);

            for (Artist trackArtist : trackArtists) {
                if (!albumArtists.contains(trackArtist) && !featuredArtists.contains(trackArtist)) {
                    featuredArtists.add(trackArtist);
                }
            }

            String trackName = track.getName();
            Optional<Song> newSong = Optional.empty();

            for (Artist trackArtist : trackArtists) {
                newSong = songRepository.findByNameWithArtist(trackName, trackArtist);
                if (newSong.isPresent()) {
                    albumSongs.add(newSong.get());
                    break;
                }
            }

            if (!newSong.isPresent()) {
                System.out.println("Saving song: " + trackName);
                List<Image> images = getImages(album.getImages());
                int newPopularity = track.getPopularity();
                if (newPopularity == 0) {
                    newPopularity = album.getPopularity();
                    if (popularity == 0) {
                        newPopularity = albumArtists.get(0).getPopularity();
                    }
                }
                long duration = track.getDurationMs();
                newSong = Optional.of(new Song(trackName, trackArtists.stream().toList(), images, newPopularity, duration, releaseDate));
                songRepository.save(newSong.get());
                for (Genre genre : genres) {
                    genreRatingRepository.save(new GenreRating(newSong.get(), genre, 5));
                }

                albumSongs.add(newSong.get());
            }
        }
    }

    @Transactional
    private void addArtist(Artist artist, int size, int popularity, List<Artist> newArtists) throws IOException, SpotifyWebApiException, ParseException {
        if (!artist.isDiscovered() && !newArtists.contains(artist))
            newArtists.add(artist);
        if (newArtists.size() <= size && artist.getPopularity() >= popularity) {
            addAlbums(artist, size, popularity, newArtists);
        }
    }

    @Transactional
    public Artist addArtist(se.michaelthelin.spotify.model_objects.specification.Artist artistObj, int size, int popularity, List<Artist> newArtists) throws IOException, SpotifyWebApiException, ParseException {
        Optional<Artist> newArtist = artistRepository.findByName(artistObj.getName());
        if (newArtist.isPresent()) {
            addArtist(newArtist.get(), size, popularity, newArtists);
            return newArtist.get();
        }
        String artistName = artistObj.getName();
        String artistId = artistObj.getId();
        int artistPopularity = artistObj.getPopularity();
        List<Image> images = getImages(artistObj.getImages());
        newArtist = Optional.of(new Artist(artistName, artistId, artistPopularity, images));
        System.out.println("Saving artist: " + newArtist.get().getName());
        artistRepository.save(newArtist.get());

        for (String genre : artistObj.getGenres()) {
            Optional<Genre> genreOpt = genreRepository.findByName(genre);
            if (!genreOpt.isPresent()) {
                genreOpt = Optional.of(new Genre(genre));
                genreRepository.save(genreOpt.get());
            }
            GenreRating genreRating = new GenreRating(newArtist.get(), genreOpt.get(), 5);
            newArtist.get().addGenreRatingToEntityAndGenre(genreRating);
            genreRatingRepository.save(genreRating);
        }
        addArtist(newArtist.get(), size, popularity, newArtists);
        return newArtist.get();
    }

    private List<Image> getImages(se.michaelthelin.spotify.model_objects.specification.Image[] images) {
        List<Image> imageList = new ArrayList<>();
        for (se.michaelthelin.spotify.model_objects.specification.Image image : images) {
            imageList.add(new Image(image.getHeight(), image.getUrl()));
        }
        return imageList;
    }

    private LocalDate parseReleaseDate(String releaseDate) {
        LocalDate date = LocalDate.now();
        try {
            date = LocalDate.parse(releaseDate, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (Exception e) {
            try {
                date = LocalDate.ofYearDay(Integer.parseInt(releaseDate), 1);
            } catch (Exception e2) {
                e.printStackTrace();
                e2.printStackTrace();
            }
        }
        return date;
    }
}
