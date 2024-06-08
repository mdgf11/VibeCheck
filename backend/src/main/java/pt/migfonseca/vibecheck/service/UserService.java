package pt.migfonseca.vibecheck.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import pt.migfonseca.vibecheck.dto.UserDTO;
import pt.migfonseca.vibecheck.model.User;
import pt.migfonseca.vibecheck.repo.UserRepository;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public UserDTO createUser(String email, String username, String spotifyId, String password) {
        User user;
        if (email != null && password != null && username != null) {
            String encryptedPassword = passwordEncoder.encode(password);
            user = new User(email, username, encryptedPassword);
        } else if (spotifyId != null && username != null) {
            user = new User(username, spotifyId);
        } else {
            throw new IllegalArgumentException("Either email and password or Spotify ID must be provided");
        }
        userRepository.save(user);
        return user.toDTO();
    }

    public UserDTO updateSpotifyId(Long userId, String spotifyId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setSpotifyId(spotifyId);
            userRepository.save(user);
            return user.toDTO();
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    // Logic to get leaderboard
    public List<UserDTO> getLeaderBoard() {
        return userRepository
                .findAll(Sort.by(Sort.Direction.ASC, "score"))
                .stream()
                .map((user) -> user.toDTO())
                .toList();
    }

}
