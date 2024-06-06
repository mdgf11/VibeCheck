package pt.migfonseca.vibecheck.dto;

import java.time.LocalDate;
import java.util.Set;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SongDTO {
    private String name;
    private Set<String> artists;
    private Set<String> albums;
    private Set<String> genres;
    private Set<String> vibes;
    private LocalDate date;
}   
