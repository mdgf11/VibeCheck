package pt.migfonseca.vibecheck.model;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pt.migfonseca.vibecheck.dto.ArtistDTO;
import pt.migfonseca.vibecheck.dto.SearchResponseDTO;
import pt.migfonseca.vibecheck.model.ratings.GenreRating;
import pt.migfonseca.vibecheck.model.ratings.VibeRating;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@PrimaryKeyJoinColumn(name = "artistId")
@Table(name = "artist",
        uniqueConstraints =
        @UniqueConstraint(columnNames = {"name"}))
@NoArgsConstructor
public class Artist extends RaterEntity {

    private String name;

    @ManyToMany
    @JoinTable(name="album_artist",
                joinColumns = @JoinColumn(name="artist_id"),
                inverseJoinColumns = @JoinColumn(name="album_id"),
                uniqueConstraints = @UniqueConstraint(columnNames = {"album_id", "artist_id"}))
    private List<Album> albums;

    @ManyToMany
    @JoinTable(name="feature_artist",
                joinColumns = @JoinColumn(name="artist_id"),
                inverseJoinColumns = @JoinColumn(name="album_id"),
                uniqueConstraints = @UniqueConstraint(columnNames = {"album_id", "artist_id"}))
    private List<Album> features;

    @ManyToMany
    @JoinTable(name="song_artist",
                joinColumns = @JoinColumn(name="artist_id"),
                inverseJoinColumns = @JoinColumn(name="song_id"))
    private List<Song> songs;
    
    private boolean discovered = false;

    private int popularity;
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

    public Artist(String name, String spotifyId, int popularity, List<Image> images) {
        super();
        this.name = name;
        this.spotifyId = spotifyId;
        this.popularity = popularity;
        this.albums = new LinkedList<>();
        this.features = new LinkedList<>();
        this.songs = new LinkedList<>();
        this.images = images;
    }
    
    public void addAlbum(Album album) {
        this.albums.add(album);
    }

    public void addSong(Song song) {
        this.songs.add(song);
    }

    public void addFeature(Album album) {
        this.features.add(album);
    }

    public ArtistDTO toDTO() {
        ArtistDTO newArtistDTO = new ArtistDTO();
        newArtistDTO.setName(this.name);
        
        newArtistDTO.setAlbums(this.albums
            .stream()
            .map(album -> album.getAlbumName())
            .collect(Collectors.toList()));
        
        newArtistDTO.setFeatures(this.features
            .stream()
            .map(feature -> feature.getAlbumName())
            .collect(Collectors.toList()));
        
        newArtistDTO.setSongs(this.getSongs()
            .stream()
            .map(song -> song.getSongName())
            .collect(Collectors.toList()));
        
        newArtistDTO.setGenres(this.genreRatings
                .stream()
                .collect(Collectors.toMap(
                    genreRating -> genreRating.getGenre().getName(),
                    GenreRating::getRating)));
        
        newArtistDTO.setVibes(this.vibeRatings
            .stream()
            .collect(Collectors.toMap(
                vibeRating -> vibeRating.getVibe().getName(),
                VibeRating::getRating)));
        
        newArtistDTO.setSpotifyId(this.spotifyId);
        
        newArtistDTO.setImages(this.images
            .stream()
            .collect(Collectors.toMap(
                Image::getHeight,
                Image::getUrl)));
        
        return newArtistDTO;
    }

    @Override
    public String toString() {
        StringBuilder albumsString = new StringBuilder();
        for (Album album : albums) {
            if (albumsString.length() > 0) {
                albumsString.append(", ");
            }
            albumsString.append(album.getAlbumName());
        }

        StringBuilder featuresString = new StringBuilder();
        for (Album feature : features) {
            if (featuresString.length() > 0) {
                featuresString.append(", ");
            }
            featuresString.append(feature.getAlbumName()); 
        }

        StringBuilder songsString = new StringBuilder();
        for (Song song : songs) {
            if (songsString.length() > 0) {
                songsString.append(", ");
            }
            songsString.append(song.getSongName()); 
        }

        return "Artist{" +
                "name='" + name + '\'' +
                ", albums=[" + albumsString.toString() + "]" +
                ", features=[" + featuresString.toString() + "]" +
                ", songs=[" + songsString.toString() + "]" +
                ", spotifyId='" + spotifyId + '\'' +
                ", discovered=" + discovered +
                '}';
    }

    @Override
    public SearchResponseDTO toResponseDTO() {
        return new SearchResponseDTO(this.name, "artist");
    }    

}

