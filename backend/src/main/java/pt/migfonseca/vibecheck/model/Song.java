package pt.migfonseca.vibecheck.model;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pt.migfonseca.vibecheck.dto.SearchResponseDTO;
import pt.migfonseca.vibecheck.dto.SongDTO;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@PrimaryKeyJoinColumn(name = "songId")
@NoArgsConstructor
@Table(name = "song")
public class Song extends RaterEntity{    

    public Song(String name, List<Artist> artists, List<Image> images, int popularity, LocalDate date) {
        super();
        this.songName = name;
        this.artists = artists;
        this.artists.stream().forEach(artist -> artist.addSong(this));
        this.albums = new LinkedList<>();
        this.date = date;
        this.images = images;
        this.popularity = popularity;
    }

    private String songName;

    private LocalDate date;

    @ManyToMany(mappedBy = "songs")
    private List<Artist> artists;

    @ManyToMany(mappedBy = "songs")
    private List<Album> albums;

    @ManyToMany(mappedBy = "songs")
    private List<Playlist> playlists;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images;

    private int popularity;

    public SongDTO toDTO() {
        SongDTO newSongDTO = new SongDTO();
        newSongDTO.setName(this.songName);
        newSongDTO.setArtists(this.artists
            .stream()
            .map(artist -> artist.getName())
            .collect(Collectors.toSet()));
        newSongDTO.setAlbums(this.albums
            .stream()
            .map(album -> album.getAlbumName())
            .collect(Collectors.toSet()));
        newSongDTO.setGenres(this.genreRatings
            .stream()
            .map(genreRating -> genreRating.getGenre().getName())
            .collect(Collectors.toSet()));
        newSongDTO.setVibes(this.vibeRatings
            .stream()
            .map(genreRating -> genreRating.getVibe().getName())
            .collect(Collectors.toSet()));
        newSongDTO.setDate(this.date);
        return newSongDTO;
    }

    public void addAlbum(Album album) {
        this.albums.add(album);
    }

    @Override
    public String toString() {
        return "Song{" +
                "songName='" + songName + '\'' +
                ", date=" + date +
                ", artists=" + artists.stream().map(Artist::getName).toList() +
                ", albums=" + albums.stream().map(Album::getAlbumName).toList() +
                ", playlists=" + playlists.stream().map(Playlist::getName).toList() +
                '}';
    }

    @Override
    public SearchResponseDTO toResponseDTO() {
        return new SearchResponseDTO(this.songName, artists.get(0).getName(), "song");
    }


}
