package pt.migfonseca.vibecheck.dto;

import java.util.Map;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArtistDTO {
    private String name;
    private Set<String> albums;
    private Set<String> features;
    private Set<String> songs;
    private Set<String> genres;
    private Set<String> vibes;
    private String spotifyId;
    private Map<Integer, String> images;
    

}
