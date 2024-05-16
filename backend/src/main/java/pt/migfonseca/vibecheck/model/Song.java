package pt.migfonseca.vibecheck.model;

import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
@Entity
@PrimaryKeyJoinColumn(name = "songId")
public class Song extends RaterEntity{    

    public Song(String name, List<String> artists) {
        super();
        this.name = name;
        this.artists = artists;
    }

    private String name;

    private List<String> artists;

    @ManyToMany(mappedBy = "songs")
    private List<Playlist> playlists;

}
