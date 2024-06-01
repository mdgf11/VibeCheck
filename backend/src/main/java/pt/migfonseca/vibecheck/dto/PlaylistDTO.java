package pt.migfonseca.vibecheck.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PlaylistDTO {
    String name;
    List<SongDTO> songs;
}
