package pt.migfonseca.vibecheck.dto;

import lombok.Data;
import pt.migfonseca.vibecheck.model.User;

@Data
public class UserDTO {
     public UserDTO(User user) {
        this.name = user.getUsername();
        this.score = user.getScore();
    }

    private String name;
    private int score;

}
