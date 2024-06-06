package pt.migfonseca.vibecheck.dto;

import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlaylistDTO {
    private long id;
    private String name;
    private Set<ArtistDTO> artists;
    private Set<SongDTO> songs;
    private Set<String> genres;
    private Set<String> vibes;
}
