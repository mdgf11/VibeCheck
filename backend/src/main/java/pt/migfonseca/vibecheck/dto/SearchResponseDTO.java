package pt.migfonseca.vibecheck.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponseDTO {

    private String name;
    private String artist;
    private String type;

    public SearchResponseDTO(String name, String type) {
        this.name=name;
        this.type=type;
    }
}
