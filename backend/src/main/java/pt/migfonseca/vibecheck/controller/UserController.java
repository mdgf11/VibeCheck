package pt.migfonseca.vibecheck.controller;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.FailedLoginException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pt.migfonseca.vibecheck.dto.UserDTO;
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

    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> register(@RequestBody Map<String, String> body) {
        String email = body.get("email");
        String username = body.get("username");
        String password = body.get("password");
        pt.migfonseca.vibecheck.model.User registeredUser = userService.registerOrGetUser(email, username, password);
        String token = jwtUtil.generateToken(registeredUser.toUserDetails());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", registeredUser.toDTO());

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody Map<String, String> body) throws FailedLoginException {
        String email = body.get("email");
        String password = body.get("password");
        pt.migfonseca.vibecheck.model.User loggedInUser = userService.login(email, password);
        String token = jwtUtil.generateToken(loggedInUser.toUserDetails());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("user", loggedInUser.toDTO());

        return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
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
        } catch (Exception e) {
            String frontendUrl = FRONTEND_URL + "/loginFailure";
            response.sendRedirect(frontendUrl);
            throw e;
        }

        final GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi.getCurrentUsersProfile().build();
        User spotifyUser = null;
        try {
            spotifyUser = getCurrentUsersProfileRequest.execute();
        } catch (Exception e) {
            String frontendUrl = FRONTEND_URL + "/loginFailure";
            response.sendRedirect(frontendUrl);
            throw e;
        }

        pt.migfonseca.vibecheck.model.User user = userService.registerOrGetUser(spotifyUser);
        
        String redirectUrl = buildRedirectUrl(FRONTEND_URL, spotifyApi.getAccessToken(), user.toDTO());
        response.sendRedirect(redirectUrl);
    }

    private String buildRedirectUrl(String baseUrl, String token, UserDTO userDTO) throws UnsupportedEncodingException {
        StringBuilder redirectUrl = new StringBuilder(baseUrl + "/loginSuccess?");
        redirectUrl.append("token=").append(URLEncoder.encode(token, StandardCharsets.UTF_8.toString()));
        redirectUrl.append("&id=").append(userDTO.getId());
        redirectUrl.append("&email=").append(URLEncoder.encode(userDTO.getEmail(), StandardCharsets.UTF_8.toString()));
        redirectUrl.append("&username=").append(URLEncoder.encode(userDTO.getUsername(), StandardCharsets.UTF_8.toString()));
        redirectUrl.append("&spotifyId=").append(URLEncoder.encode(userDTO.getSpotifyId(), StandardCharsets.UTF_8.toString()));
        redirectUrl.append("&score=").append(userDTO.getScore());
        redirectUrl.append("&admin=").append(userDTO.isAdmin());
        
        for (Map.Entry<Integer, String> entry : userDTO.getImages().entrySet()) {
            redirectUrl.append("&image" + entry.getKey() + "=").append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.toString()));
        }

        return redirectUrl.toString();
    }

}
