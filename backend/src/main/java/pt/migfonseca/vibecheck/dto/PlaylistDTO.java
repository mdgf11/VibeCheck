package pt.migfonseca.vibecheck.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlaylistDTO {
    private long id;
    private String name;
    private List<ArtistDTO> artists;
    private List<SongDTO> songs;
    private Map<String, Double> genres;
    private Map<String, Double> vibes;
}
