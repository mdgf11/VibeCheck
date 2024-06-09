package pt.migfonseca.vibecheck.util;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import se.michaelthelin.spotify.SpotifyApi;
import se.michaelthelin.spotify.SpotifyHttpManager;
import se.michaelthelin.spotify.exceptions.SpotifyWebApiException;
import se.michaelthelin.spotify.model_objects.specification.User;
import se.michaelthelin.spotify.requests.data.users_profile.GetCurrentUsersProfileRequest;

import java.io.IOException;

@Component
public class SpotifyTokenUtil {

    @Value("${env.spotify.id}")
    private String CLIENT_ID;

    @Value("${env.spotify.secret}")
    private String CLIENT_SECRET;

    @Value("${env.backend.url}")
    private String BACKEND_URL;

    private String REDIRECT_URI = BACKEND_URL + "/user/callback";

    private SpotifyApi spotifyApi;

    private void initializeSpotifyApi() {
        if (spotifyApi == null) {
            spotifyApi = new SpotifyApi.Builder()
                    .setClientId(CLIENT_ID)
                    .setClientSecret(CLIENT_SECRET)
                    .setRedirectUri(SpotifyHttpManager.makeUri(REDIRECT_URI))
                    .build();
        }
    }

    public String extractEmail(String token) throws ParseException {
        initializeSpotifyApi();
        spotifyApi.setAccessToken(token);

        try {
            GetCurrentUsersProfileRequest getCurrentUsersProfileRequest = spotifyApi.getCurrentUsersProfile().build();
            User user = getCurrentUsersProfileRequest.execute();
            String email = user.getEmail();
            if (email == null)
                email = user.getId();
            return email;
        } catch (IOException | SpotifyWebApiException e) {
            e.printStackTrace();
            return null;
        }
    }

    public boolean validateToken(String token, UserDetails userDetails) throws ParseException {
        String username = extractEmail(token);
        return username != null && username.equals(userDetails.getUsername());
    }
}
