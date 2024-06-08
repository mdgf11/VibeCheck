package pt.migfonseca.vibecheck.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.migfonseca.vibecheck.dto.UserDTO;

@Data
@Entity
@Table(name = "account_user",
        uniqueConstraints =
        @UniqueConstraint(columnNames = "email"))
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Long id;

    public User(String username, String spotifyId) {
        this.username = username;
        this.spotifyId = spotifyId;
    }

    public User(String email, String username, String encryptedPassword) {
        this.email = email;
        this.username = username;
        this.password = encryptedPassword;

    }

    private String email;

    private String username;

    private String password;
    
    private String spotifyId;

    private int score;

    public UserDTO toDTO() {
        return new UserDTO(id, email, username, spotifyId, score);
    };
}
