package pt.migfonseca.vibecheck.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.apache.hc.core5.http.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pt.migfonseca.vibecheck.service.UserService;
import pt.migfonseca.vibecheck.util.SpotifyTokenUtil;

import java.io.IOException;
import java.util.logging.Logger;

@Component
@Order(2) // Ensure this filter runs after JwtRequestFilter
public class SpotifyOAuthFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(SpotifyOAuthFilter.class.getName());

    private UserService userService;

    @Autowired
    private SpotifyTokenUtil spotifyTokenUtil;

    @Autowired
    public void setUserService(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain chain)
            throws ServletException, IOException {

        if (SecurityContextHolder.getContext().getAuthentication() == null) {
            final String authorizationHeader = request.getHeader("Authorization");

            String spotifyUsername = null;
            String spotifyToken = null;

            if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                spotifyToken = authorizationHeader.substring(7);
                try {
                    spotifyUsername = spotifyTokenUtil.extractEmail(spotifyToken);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            if (spotifyUsername != null) {
                UserDetails userDetails = this.userService.loadUserByUsername(spotifyUsername);
                try {
                    if (spotifyTokenUtil.validateToken(spotifyToken, userDetails)) {
                        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities());
                        usernamePasswordAuthenticationToken
                                .setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
                        LOGGER.info("Spotify OAuth Token validated and user authenticated");
                    } else {
                        LOGGER.warning("Spotify OAuth Token validation failed");
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        chain.doFilter(request, response);
    }
}
