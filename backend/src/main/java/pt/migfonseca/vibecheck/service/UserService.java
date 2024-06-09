package pt.migfonseca.vibecheck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.migfonseca.vibecheck.dto.UserDTO;
import pt.migfonseca.vibecheck.model.User;
import pt.migfonseca.vibecheck.repo.UserRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.security.auth.login.FailedLoginException;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            return new org.springframework.security.core.userdetails.User(user.get().getEmail(), user.get().getPassword(), new ArrayList<>());
        }
        throw new UsernameNotFoundException("User not found with email: " + email);
    }

    public UserDTO createUser(String email, String username, String password) {
        User user = new User(email, username, passwordEncoder.encode(password));
        user = userRepository.save(user);
        return user.toDTO();
    }

    public UserDTO updateSpotifyId(Long userId, String spotifyId) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setSpotifyId(spotifyId);
            user = userRepository.save(user);
            return user.toDTO();
        }
        return null;
    }

    public List<UserDTO> getLeaderBoard() {
        return userRepository.findAll().stream()
                .map(User::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User registerOrGetUser(se.michaelthelin.spotify.model_objects.specification.User spotifyUser) {
        Optional<User> userOptional = userRepository.findBySpotifyId(spotifyUser.getId());
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            User user = new User(spotifyUser);
            user = userRepository.save(user);
            return user;
        }
    }

    public User registerOrGetUser(String email, String username, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            User user = new User(email, username, password);
            user = userRepository.save(user);
            return user;
        }
    }

    public User login(String email, String password) throws FailedLoginException {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            if (passwordEncoder.matches(password, userOptional.get().getPassword()))
                return userOptional.get();
            else
                throw new FailedLoginException();
        }else
            throw new UsernameNotFoundException(email);
    }
}
