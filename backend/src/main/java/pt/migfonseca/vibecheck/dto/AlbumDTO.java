package pt.migfonseca.vibecheck.dto;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AlbumDTO {
    private String name;
    private Set<String> artists;
    private Set<String> features;
    private Set<String> songs;
    private Set<String> genres;
    private Set<String> vibes;
    private LocalDate date;
    private Map<Integer, String> images;
}
