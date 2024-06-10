package pt.migfonseca.vibecheck.dto;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SettingsDTO {
    private Boolean newSongs;
    private Integer numSongs;
    private Integer maxDuration;
    private Map<String, Integer> minSongsPerArtist;
    private Map<String, Integer> maxSongsPerArtist;
    private Map<String, Integer> minSongsPerGenre;
    private Map<String, Integer> maxSongsPerGenre;
    private Map<String, Integer> minSongsPerVibe;
    private Map<String, Integer> maxSongsPerVibe;
}