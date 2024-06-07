package pt.migfonseca.vibecheck.dto;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SongDTO {
    private long id;
    private String name;
    private Set<String> artists;
    private Set<String> albums;
    private Set<String> genres;
    private Set<String> vibes;
    private long duration;
    private Map<Integer, String> images;
    private LocalDate date;
}   
