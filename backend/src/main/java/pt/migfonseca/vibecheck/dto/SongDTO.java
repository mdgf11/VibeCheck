package pt.migfonseca.vibecheck.dto;

import java.util.List;

import lombok.Data;
import pt.migfonseca.vibecheck.model.Song;

@Data
public class SongDTO {

    public SongDTO(Song song) {
        this.name = song.getName();
        this.artists = song.getArtists();
    }

    private String name;
    private List<String> artists;

}   
