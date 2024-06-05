package pt.migfonseca.vibecheck.dto;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AlbumDTO {
    private String name;
    private List<String> artists;
    private List<String> features;
    private List<String> songs;
    private LocalDate date;
    private Map<Integer, String> images;
}
