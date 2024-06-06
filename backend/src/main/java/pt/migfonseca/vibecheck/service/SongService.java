package pt.migfonseca.vibecheck.service;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

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

@Service
public class SongService {

    @Autowired
    ArtistRepository artistRepository;

    @Autowired
    AlbumRepository albumRepository;

    @Autowired
    SongRepository songRepository;

    @Autowired
    GenreRepository genreRepository;

    @Autowired
    GenreRatingRepository genreRatingRepository;

    @Autowired
    SpotifyTokenService spotifyTokenService;

    private List<Artist> newArtists;

    private int MAX_TREE_SIZE = 1;

    private int MIN_POPULARITY = 20;

    public List<ArtistDTO> getAllArtists() {
        return artistRepository
            .findAll()
            .stream()
            .map(artist -> artist.toDTO())
            .toList();
    }

    public List<ArtistDTO> getAllUndicoveredArtistsWithPopularity(int size, int popularity) {
        return artistRepository
            .findAll()
            .stream()
            .filter(artist -> !artist.isDiscovered() && artist.getPopularity() >= popularity)
            .map(artist -> artist.toDTO())
            .limit(size)
            .toList();
    }

    public List<SongDTO> getAllSongs() {
        return songRepository
            .findAll()
            .stream()
            .map(song -> song.toDTO())
            .toList();
    }


    public List<ArtistDTO> addArtist(String name, int size) throws IOException {
        newArtists = new LinkedList<>();
        MAX_TREE_SIZE = size;
        JsonNode artistJsonNode = spotifyTokenService.searchArtist(name);
        addArtist(artistJsonNode);
        return newArtists.stream().map(artist -> artist.toDTO()).toList();
    }
    
    @Async
    @Transactional
    public void discover(int size, int popularity) throws IOException {
        MAX_TREE_SIZE = size;
        MIN_POPULARITY = popularity;
        List<Artist> allArtists = artistRepository.findAll();

        // Convert the list to a mutable list
        List<Artist> mutableArtists = new ArrayList<>(allArtists);

        // Ensure lazy collections are loaded if needed
        for (Artist artist : mutableArtists) {
            artist.getAlbums().size();
        }

        // Filter and convert to a mutable list
        mutableArtists = mutableArtists.stream()
            .filter(artist -> !artist.isDiscovered() 
                && artist.getPopularity() >= MIN_POPULARITY)
            .limit(size)
            .collect(Collectors.toCollection(ArrayList::new));

        // Sort the mutable list
        mutableArtists.sort(Comparator.comparing(Artist::getPopularity).reversed());
        mutableArtists.forEach(artist -> System.out.println(artist.getName()));

        List<Artist> fullNewArtists = new LinkedList<>();

        for (Artist artist : mutableArtists) {
            newArtists = new LinkedList<>();
            addArtist(artist);
            newArtists.stream().forEach(newArtist -> {
                if (!fullNewArtists.contains(newArtist)) {
                    fullNewArtists.add(newArtist);
                }
            });
        }
    }

    @Transactional
    public void addAlbums(Artist artist) throws IOException {
        // Iterate through albums
        artist.setDiscovered(true);
        Set<Genre> artistGenres = artist
                .getGenreRatings()
                .stream()
                .map(genreRating -> genreRating.getGenre())
                .collect(Collectors.toSet());
        JsonNode albumsJsonNode = spotifyTokenService.getArtistsAlbums(artist.getSpotifyId());
        for (JsonNode albumJsonNode : albumsJsonNode) {
            Optional<Album> albumOptional = albumRepository.findByNameWithArtist(
                albumJsonNode
                    .get("name")
                    .asText(), artist);
            if (!albumOptional.isPresent())
                albumOptional = albumRepository.findByNameWithFeature(
                    albumJsonNode
                        .get("name")
                        .asText(), artist);
            
            if (albumOptional.isPresent()) {

                for (Artist albumArtist: albumOptional.get().getArtists())
                    if (!albumArtist.isDiscovered())
                        addArtist(albumArtist);
                for (Artist albumArtist: albumOptional.get().getFeatures())
                    if (!albumArtist.isDiscovered())
                        addArtist(albumArtist);
                continue;
            }

            // Get release date
            LocalDate releaseDate = LocalDate.now();
            try {
                releaseDate = LocalDate.parse(albumJsonNode.get("release_date").asText(),
                    DateTimeFormatter.ISO_LOCAL_DATE);
            } catch (Exception e) {
                try {
                    releaseDate = LocalDate.ofYearDay(Integer.parseInt(albumJsonNode.get("release_date").asText()), 1);
                } catch (Exception e2) {
                    e.printStackTrace();
                    e2.printStackTrace();
                }                
            }
            
            // Get and create album artists
            Set<Artist> foundArtists = new HashSet<>();
            Set<String> newArtistIds = new HashSet<>();
            for (JsonNode artistJsonNode : albumJsonNode.get("artists")) {
                String albumArtistName = artistJsonNode.get("name").asText();
                Optional<Artist> albumArtist = artistRepository.findByName(albumArtistName);
                if (!albumArtist.isPresent())
                    newArtistIds.add(artistJsonNode.get("id").asText());
                else if (!albumArtist.get().isDiscovered()) {
                    addArtist(albumArtist.get());
                    foundArtists.add(albumArtist.get());
                } else 
                    foundArtists.add(albumArtist.get());

                    
            }
            Set<Artist> albumArtists = new HashSet<>();
            for (JsonNode newArtistJsonNode : spotifyTokenService.getArtistsFull(newArtistIds)) {
                albumArtists.add(addArtist(newArtistJsonNode));
            }
            albumArtists.addAll(foundArtists);
            // Get and create album tracks, and featured artists
            Set<Song> albumSongs = new HashSet<>();
            Set<Artist> featuredArtists = new HashSet<>();

            String albumName = albumJsonNode.get("name").asText();
            Set<Genre> genres = Set.copyOf(artistGenres);
            for (JsonNode genre: albumJsonNode.get("genres")) {
                Optional<Genre> genreOptional = genreRepository.findByName(genre.asText());
                if (!genreOptional.isPresent()) {
                    genreOptional = Optional.of(new Genre(genre.asText()));
                    genreRepository.save(genreOptional.get());
                }
                genres.add(genreOptional.get());
            }
            trackAndFeatures(albumJsonNode,
                    albumSongs,
                    albumArtists,
                    featuredArtists,
                    genres,
                    releaseDate);
            // Finally create the album in question
            // Also updates every song and artist with the new album

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
            if ("single".equals(albumJsonNode.get("album_type").asText())) {
                continue;
            }
            System.out.println("Saving album: " + albumName);
            List<Image> images = getImages(albumJsonNode.get("images"));
            int popularity = albumJsonNode.get("popularity").asInt();
            newAlbum = Optional.of(new Album(albumName,
                    albumSongs.stream().toList(),
                    albumArtists.stream().toList(),
                    featuredArtists.stream().toList(),
                    images,
                    popularity,
                    releaseDate));
            albumRepository.save(newAlbum.get());
            for (Genre genre: genres)
                genreRatingRepository.save(new GenreRating(newAlbum.get(), genre, 5));

        }
    }

    @Transactional
    private void trackAndFeatures(JsonNode albumJsonNode,
            Set<Song> albumSongs,
            Set<Artist> albumArtists,
            Set<Artist> featuredArtists,
            Set<Genre> genres,
            LocalDate releaseDate) throws IOException {

        int offset = 0;
        int stride = 50;
        JsonNode tracksNode = albumJsonNode.get("tracks").get("items");
        
        boolean isLastBatch;
        do {
            isLastBatch = tracksNode.size() < stride;
            for (JsonNode trackNode : tracksNode) {
                List<Artist> foundArtists = new LinkedList<>();
                Set<String> newArtistIds = new HashSet<>();
                for (JsonNode artistJsonNode : trackNode.get("artists")) {
                    Optional<Artist> trackArtist = artistRepository.findByName(artistJsonNode.get("name").asText());
                    if (!trackArtist.isPresent()) 
                        newArtistIds.add(artistJsonNode.get("id").asText());
                    else if (!trackArtist.get().isDiscovered()){
                        addArtist(trackArtist.get());
                        foundArtists.add(trackArtist.get());
                    } else
                        foundArtists.add(trackArtist.get());
                }
                
                Set<Artist> trackArtists = new HashSet<>();
                for (JsonNode newArtistJsonNode : spotifyTokenService.getArtistsFull(newArtistIds))
                    trackArtists.add(addArtist(newArtistJsonNode));
                trackArtists.addAll(foundArtists);

                // If the artist doesn't belong in the album it's a feature
                for (Artist trackArtist : trackArtists) {
                    if (!albumArtists.contains(trackArtist) 
                            && !featuredArtists.contains(trackArtist)){
                        featuredArtists.add(trackArtist);
                    }
                }

                // Create the song in question
                String trackName = trackNode.get("name").asText();
                Optional<Song> newSong = Optional.empty();

                for (Artist trackArtist : trackArtists) {
                    newSong = songRepository.findByNameWithArtist(trackName, trackArtist);
                    if (newSong.isPresent()) {
                        albumSongs.add(newSong.get());
                        break; 
                    }
                }
                
                if (!newSong.isPresent()){
                    System.out.println("Saving song: " + trackName);
                    List<Image> images = getImages(albumJsonNode.get("images"));
                    int popularity = albumJsonNode.get("popularity").asInt();

                    newSong = Optional.of(new Song(trackName, 
                            trackArtists
                                .stream()
                                .toList(),
                            images,
                            popularity,
                            releaseDate));
                    songRepository.save(newSong.get());
                    for (Genre genre : genres)
                        genreRatingRepository.save(new GenreRating(newSong.get(), genre, 5));

                    albumSongs.add(newSong.get());
                }
            }
            offset += stride;
            if (!isLastBatch) {
                tracksNode = spotifyTokenService.getAlbumTracks(albumJsonNode.get("id").asText(), offset);
            }
            
        } while (!isLastBatch);
        
    }
    
    @Transactional
    private void addArtist(Artist artist) throws IOException {
        if (!artist.isDiscovered() && !newArtists.contains(artist))
            newArtists.add(artist);
        if (newArtists.size() <= MAX_TREE_SIZE && artist.getPopularity() >= MIN_POPULARITY) {            
            addAlbums(artist);
        }
    }

    @Transactional
    public Artist addArtist(JsonNode artistJsonNode) throws IOException {
        Optional<Artist> newArtist = artistRepository.findByName(artistJsonNode.get("name").asText());
        if (newArtist.isPresent()) {
            addArtist(newArtist.get());
            return newArtist.get();
        }
        String artistName = artistJsonNode.get("name").asText();
        String artistId = artistJsonNode.get("id").asText();
        int artistPopularity = artistJsonNode.get("popularity").asInt();
        List<Image> images = getImages(artistJsonNode.get("images"));
        newArtist = Optional.of(new Artist(artistName, artistId, artistPopularity, images));
        System.out.println("Saving artist: " + newArtist.get().getName());
        artistRepository.save(newArtist.get());

        // Creates genres
        for (JsonNode genreNode : artistJsonNode.get("genres")) {
            Optional<Genre> genre = genreRepository.findByName(genreNode.asText());
            if (!genre.isPresent()) {
                genre = Optional.of(new Genre(genreNode.asText()));
                genreRepository.save(genre.get());
            }
            genreRatingRepository.save(new GenreRating(newArtist.get(), genre.get(), 5));
        }
        addArtist(newArtist.get());
        return newArtist.get();
    }

    private List<Image> getImages(JsonNode jsonNode) {
        List<Image> images = new ArrayList<>();
        if (jsonNode.isArray()) {
            for (JsonNode node : jsonNode) {
                int height = node.get("height").asInt();
                String url = node.get("url").asText();
                images.add(new Image(height, url));
            }
        }
        return images;
    }

}