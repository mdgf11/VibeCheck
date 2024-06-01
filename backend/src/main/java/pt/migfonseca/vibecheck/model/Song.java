package pt.migfonseca.vibecheck.model;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import pt.migfonseca.vibecheck.dto.SongDTO;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@PrimaryKeyJoinColumn(name = "songId")
@NoArgsConstructor
@Table(name = "song")
public class Song extends RaterEntity{    

    public Song(String name, List<Artist> artists, LocalDate date) {
        super();
        this.songName = name;
        this.artists = artists;
        this.artists.stream().forEach(artist -> artist.addSong(this));
        this.albums = new LinkedList<>();
        this.date = date;
    }

    public Song(String name, List<Artist> artists, Album album, LocalDate date) {
        super();
        this.songName = name;
        this.artists = artists;
        this.artists.stream().forEach(artist -> artist.addSong(this));
        this.albums = new LinkedList<>();
        this.albums.add(album);
        this.date = date;
    }

    private String songName;

    private LocalDate date;

    @ManyToMany(mappedBy = "songs")
    private List<Artist> artists;

    @ManyToMany(mappedBy = "songs")
    private List<Album> albums;

    @ManyToMany(mappedBy = "songs")
    private List<Playlist> playlists;

    public SongDTO toDTO() {
        SongDTO newSongDTO = new SongDTO();
        newSongDTO.setName(this.songName);
        newSongDTO.setArtists(this.artists
            .stream()
            .map(artist -> artist.getName())
            .toList());
        if (!this.albums.isEmpty())
            newSongDTO.setAlbums(this.albums
            .stream()
            .map(album -> album.getAlbumName())
            .toList());
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


}
