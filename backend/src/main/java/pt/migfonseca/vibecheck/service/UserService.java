package pt.migfonseca.vibecheck.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import pt.migfonseca.vibecheck.dto.UserDTO;
import pt.migfonseca.vibecheck.model.User;
import pt.migfonseca.vibecheck.repo.UserRepository;

import javax.security.auth.login.FailedLoginException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Lazy
    private PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            User foundUser = user.get();
            List<GrantedAuthority> authorities = new ArrayList<>();
            if (foundUser.isAdmin()) {
                authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
            }
            return new org.springframework.security.core.userdetails.User(foundUser.getEmail(), foundUser.getPassword(), authorities);
        }
        throw new UsernameNotFoundException("User not found with email: " + email);
    }

    public UserDTO createUser(String email, String username, String password) {
        User user = new User(email, username, passwordEncoder.encode(password));
        user = userRepository.save(user);
        return user.toDTO();
    }

    public UserDTO updateSpotifyTokens(Long userId, String accessToken, String refreshToken) {
        Optional<User> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            user.setSpotifyAccessToken(accessToken);
            user.setSpotifyRefreshToken(refreshToken);
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
            User user = new User(spotifyUser, passwordEncoder.encode("password"));
            user = userRepository.save(user);
            return user;
        }
    }

    public User registerOrGetUser(String email, String username, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            User user = new User(email, username, passwordEncoder.encode(password));
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
                throw new FailedLoginException("");
        } else {
            throw new UsernameNotFoundException(email);
        }
    }

    public String getSpotifyAccessToken() {
        String email = getCurrentUserEmail();
        if (email == null) {
            return null;
        }

        User user = userRepository.findByEmail(email).orElse(null);
        if (user != null) {
            return user.getSpotifyAccessToken();
        }
        return null;
    }

    private String getCurrentUserEmail() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        } else if (principal instanceof String) {
            return (String) principal;
        }
        return null;
    }
}
