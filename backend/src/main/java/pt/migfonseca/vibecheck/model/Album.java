package pt.migfonseca.vibecheck.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pt.migfonseca.vibecheck.dto.AlbumDTO;
import pt.migfonseca.vibecheck.dto.SearchResponseDTO;
import pt.migfonseca.vibecheck.model.ratings.GenreRating;
import pt.migfonseca.vibecheck.model.ratings.VibeRating;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@PrimaryKeyJoinColumn(name = "albumId")
@NoArgsConstructor
public class Album extends RaterEntity{    

    public Album(String albumName, 
            List<Song> songs,
            List<Artist> albumArtists,
            List<Artist> featuredArtists,
            List<Image> images,
            int popularity,
            LocalDate date
            ) {
        super();
        this.albumName = albumName;
        this.artists = albumArtists;
        this.artists.stream().forEach(artist -> artist.addAlbum(this));
        this.songs = songs;
        this.songs.stream().forEach(song -> song.addAlbum(this));
        this.features = featuredArtists;
        this.features.stream().forEach(feature -> feature.addFeature(this));
        this.date = date;
        this.images = images;
        this.popularity = popularity;
    }

    private String albumName;

    private LocalDate date;

    @ManyToMany(mappedBy = "albums")
    private List<Artist> artists;

    @ManyToMany(mappedBy = "features")
    private List<Artist> features;

    @ManyToMany
    @JoinTable(name="song_album",
                joinColumns = @JoinColumn(name="album_id"),
                inverseJoinColumns = @JoinColumn(name="song_id"))
    private List<Song> songs;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

    private int popularity;

    public AlbumDTO toDTO() {
        AlbumDTO newAlbumDTO = new AlbumDTO();
        newAlbumDTO.setName(this.albumName);
        newAlbumDTO.setDate(this.date);
        newAlbumDTO.setArtists(this.artists
            .stream()
            .map(artist -> artist.getName())
            .toList());
        newAlbumDTO.setSongs(this.songs
            .stream()
            .map(song -> song.getSongName())
            .toList());
        newAlbumDTO.setFeatures(this.features
            .stream()
            .map(feature -> feature.getName())
            .toList());
        newAlbumDTO.setGenres(this.genreRatings
                .stream()
                .collect(Collectors.toMap(
                    genreRating -> genreRating.getGenre().getName(),
                    GenreRating::getRating)));
        
        newAlbumDTO.setVibes(this.vibeRatings
            .stream()
            .collect(Collectors.toMap(
                vibeRating -> vibeRating.getVibe().getName(),
                VibeRating::getRating)));
        newAlbumDTO.setImages(images.stream()
                .collect(Collectors.toMap(
                        Image::getHeight,
                        Image::getUrl)));
        return newAlbumDTO;
    }

    @Override
    public String toString() {
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        StringBuilder artistsString = new StringBuilder();
        for (Artist artist : artists) {
            if (artistsString.length() > 0) {
                artistsString.append(", ");
            }
            artistsString.append(artist.getName());
        }

        StringBuilder featuresString = new StringBuilder();
        for (Artist feature : features) {
            if (featuresString.length() > 0) {
                featuresString.append(", ");
            }
            featuresString.append(feature.getName());
        }

        StringBuilder songsString = new StringBuilder();
        for (Song song : songs) {
            if (songsString.length() > 0) {
                songsString.append(", ");
            }
            songsString.append(song.getSongName()); 
        }

        return "Album{" +
                "name='" + albumName + '\'' +
                ", artists=[" + artistsString.toString() + "]" +
                ", features=[" + featuresString.toString() + "]" +
                ", songs=[" + songsString.toString() + "]" +
                ", date=" + (date != null ? date.format(dateFormat) : "null") +
                '}';
    }

    @Override
    public SearchResponseDTO toResponseDTO() {
        return new SearchResponseDTO(this.albumName, this.artists.get(0).getName(), "album");
    }
    
}

