package pt.migfonseca.vibecheck.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
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

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Image> images = new LinkedList<>();

    private int score = 0;

    private boolean admin = false;

    public UserDTO toDTO() {
        return new UserDTO(id,
                email,
                username,
                spotifyId,
                score,
                images.stream().collect(Collectors.toMap(Image::getHeight, Image::getUrl)),
                admin);
    }

    public User(se.michaelthelin.spotify.model_objects.specification.User spotifyUser, String password) {
        this.username = spotifyUser.getDisplayName();
        this.email = spotifyUser.getEmail();
        if (this.email == null)
            this.email = spotifyUser.getId();
        this.spotifyId = spotifyUser.getId();
        this.images = List.of(spotifyUser.getImages()).stream().map(image -> new Image(image.getHeight(), image.getUrl())).toList();
        this.password = password;
    }

    public UserDetails toUserDetails() {
        
        return new org.springframework.security.core.userdetails.User(this.email, this.password, new ArrayList<>());
    }
}
