package pt.migfonseca.vibecheck.model;

import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pt.migfonseca.vibecheck.dto.ArtistDTO;

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

    public Artist(String name, String spotifyId, int popularity) {
        super();
        this.name = name;
        this.spotifyId = spotifyId;
        this.popularity = popularity;
        this.albums = new LinkedList<>();
        this.features = new LinkedList<>();
        this.songs = new LinkedList<>();
    }

    public ArtistDTO toDTO() {
        ArtistDTO newArtistDTO = new ArtistDTO();
        newArtistDTO.setName(this.name);
        newArtistDTO.setAlbums(this.albums
            .stream()
            .map(album -> album.getAlbumName())
            .toList());
        newArtistDTO.setFeatures(this.features
            .stream()
            .map(feature -> feature.getAlbumName())
            .toList());
        newArtistDTO.setSongs(this.getSongs()
            .stream()
            .map(song -> song.getSongName())
            .toList());
        newArtistDTO.setSpotifyId(spotifyId);
        return newArtistDTO;
    }

    public void addAlbum(Album album) {
        this.albums.add(album);
    }

    public void addSong(Song song) {
        this.songs.add(song);
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

    public void addFeature(Album album) {
        this.features.add(album);
    }

}

