package pt.migfonseca.vibecheck.dto;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ArtistDTO {
    private String name;
    private List<String> albums;
    private List<String> features;
    private List<String> songs;
    private String spotifyId;
    private Map<Integer, String> images;

}
