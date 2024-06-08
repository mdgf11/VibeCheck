package pt.migfonseca.vibecheck.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SongDTO {
    private long id;
    private String name;
    private List<String> artists;
    private List<String> albums;
    private long duration;
    private Map<Integer, String> images;
    private LocalDate date;
    private Map<String, Double> genres;
    private Map<String, Double> vibes;
}   
