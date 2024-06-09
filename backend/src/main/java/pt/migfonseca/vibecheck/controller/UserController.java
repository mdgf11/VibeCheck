package pt.migfonseca.vibecheck.controller;

import java.util.Map;

import javax.security.auth.login.FailedLoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pt.migfonseca.vibecheck.service.UserService;
import pt.migfonseca.vibecheck.util.JwtUtil;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.model_objects.credentials.AuthorizationCodeCredentials;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.authorization.authorization_code.AuthorizationCodeRequest;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/user")
public class UserController {

    @Value("${env.frontend.url}")
    private String FRONTEND_URL;

    @Value("${env.backend.url}")
    private String BACKEND_URL;

    @Value("${env.spotify.id}")
    private String SPOTIFY_ID;

    @Value("${env.spotify.secret}")
    private String SPOTIFY_SECRET;

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String username = body.get("username");
        String password = body.get("password");
        return new ResponseEntity<>(jwtUtil.generateToken(userService.registerOrGetUser(email, username, password).toUserDetails()), HttpStatus.ACCEPTED);
    }
    
    @CrossOrigin(origins = "http://localhost:8081")
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody Map<String, String> body) throws FailedLoginException {
        String email = body.get("email");
        String password = body.get("password");
        return new ResponseEntity<>(jwtUtil.generateToken(userService.login(email, password).toUserDetails()), HttpStatus.ACCEPTED);
    }
    
    @GetMapping("/callback")
    public void spotifyCallback(@RequestParam("code") String userCode, HttpServletResponse response) throws Exception {

        final SpotifyApi spotifyApi = SpotifyApi.builder()
                .setClientId(SPOTIFY_ID)
                .setClientSecret(SPOTIFY_SECRET)
                .setRedirectUri(SpotifyHttpManager.makeUri(BACKEND_URL + "/user/callback"))
                .build();
                
        AuthorizationCodeRequest authorizationCodeRequest = spotifyApi.authorizationCode(userCode).build();

        try {
            final AuthorizationCodeCredentials authorizationCodeCredentials = authorizationCodeRequest.execute();

            spotifyApi.setAccessToken(authorizationCodeCredentials.getAccessToken());
            spotifyApi.setRefreshToken(authorizationCodeCredentials.getRefreshToken());
        } catch (Exception e ) {
            String frontendUrl = FRONTEND_URL + "/loginFailure";
            response.sendRedirect(frontendUrl);
            throw e;
        }


        final GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi.getCurrentUsersProfile().build();
        User user = null;
        try {
            user = getCurrentUsersProfileRequest.execute();
        } catch (Exception e) {
            String frontendUrl = FRONTEND_URL + "/loginFailure";
            response.sendRedirect(frontendUrl);
            throw e;
        }
        userService.registerOrGetUser(user);
        String frontendUrl = FRONTEND_URL + "/loginSuccess?token=" + spotifyApi.getAccessToken();
        System.out.println("Redirecting to: " + frontendUrl);
        response.sendRedirect(frontendUrl);
    }
}
