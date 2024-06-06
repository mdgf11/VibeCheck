package pt.migfonseca.vibecheck.model;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import pt.migfonseca.vibecheck.dto.PlaylistDTO;
import pt.migfonseca.vibecheck.dto.SearchResponseDTO;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@PrimaryKeyJoinColumn(name = "playlistId")
@AllArgsConstructor
public class Playlist extends RaterEntity{

    public Playlist(){
        this.albums = new ArrayList<>();
        this.songs = new ArrayList<>();
    }

    public Playlist(String name, List<Song> songs) {
        super();
        this.name = name;
        this.albums = new ArrayList<>();
        this.songs = songs;
        
    }

    private String name;

    @ManyToMany
    @JoinTable(name="song_playlist",
                joinColumns = @JoinColumn(name="playlist_id"),
                inverseJoinColumns = @JoinColumn(name="song_id"))
    private List<Song> songs;

    @ManyToMany
    @JoinTable(name="artist_playlist",
                joinColumns = @JoinColumn(name="playlist_id"),
                inverseJoinColumns = @JoinColumn(name="artist_id"))
    private List<Artist> artists;

    @ManyToMany
    @JoinTable(name="albums_playlist",
                joinColumns = @JoinColumn(name="playlist_id"),
                inverseJoinColumns = @JoinColumn(name="album_id"))
    private List<Album> albums;

    public void addSong(Song song) {
        songs.add(song);
    }

    @Override
    public SearchResponseDTO toResponseDTO() {
        return new SearchResponseDTO(this.name, "playlist");
    }

    public PlaylistDTO toDto() {
        PlaylistDTO playlistDTO = new PlaylistDTO();
        playlistDTO.setName(name);
        playlistDTO.setId(this.raterEntityId);
        playlistDTO.setArtists(artists
                .stream()
                .map(artist->artist.toDTO())
                .collect(Collectors.toSet()));
        playlistDTO.setSongs(songs
                .stream()
                .map(song -> song.toDTO())
                .collect(Collectors.toSet()));
        playlistDTO.setGenres(this.genreRatings
                .stream()
                .map(genreRating -> genreRating.getGenre().getName())
                .collect(Collectors.toSet()));
        playlistDTO.setVibes(this.vibeRatings
                .stream()
                .map(genreRating -> genreRating.getVibe().getName())
                .collect(Collectors.toSet()));
        return playlistDTO;
    }

}
