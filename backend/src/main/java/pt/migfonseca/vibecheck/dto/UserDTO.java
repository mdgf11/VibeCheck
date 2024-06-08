package pt.migfonseca.vibecheck.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private long id;
    private String email;
    private String username;
    private String spotifyId;
    private int score;

}
