package pt.migfonseca.vibecheck.dto;

import java.util.Map;

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
    private Map<Integer, String> images;
    private boolean admin;

    public UserDTO(String jwt) {
    }
}
