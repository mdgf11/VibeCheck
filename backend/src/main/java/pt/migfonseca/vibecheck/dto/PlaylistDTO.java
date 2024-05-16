package pt.migfonseca.vibecheck.dto;

import java.util.List;

import lombok.Data;
import pt.migfonseca.vibecheck.model.Playlist;

@Data
public class PlaylistDTO {

    public PlaylistDTO(Playlist playlist) {
        this.name = playlist.getName();
        this.songs = playlist.getSongs()
                        .stream()
                        .map(song -> new SongDTO(song))
                        .toList();
        
    }

    String name;
    List<SongDTO> songs;
}
