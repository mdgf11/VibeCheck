package pt.migfonseca.vibecheck.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "account_user",
        uniqueConstraints =
        @UniqueConstraint(columnNames = "username"))
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue
    private Long id;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
    
    private String username;

    private String password;
    
    private int score;
}
