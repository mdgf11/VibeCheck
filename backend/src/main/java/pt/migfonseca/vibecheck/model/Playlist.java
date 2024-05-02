package pt.migfonseca.vibecheck.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@PrimaryKeyJoinColumn(name = "playlistId")
public class Playlist extends RaterEntity{

    private String name;

    @ManyToMany
    @JoinTable(name="song_playlist",
                joinColumns = @JoinColumn(name="playlist_id"),
                inverseJoinColumns = @JoinColumn(name="song_id"))
    private List<Song> songs;

    
}
