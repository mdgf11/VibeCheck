package pt.migfonseca.vibecheck.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import pt.migfonseca.vibecheck.filter.AdminAuthorizationFilter;
import pt.migfonseca.vibecheck.filter.JwtRequestFilter;
import pt.migfonseca.vibecheck.filter.SpotifyOAuthFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtRequestFilter jwtRequestFilter;
    private final SpotifyOAuthFilter spotifyOAuthFilter;
    private final AdminAuthorizationFilter adminAuthorizationFilter;
    private final ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    public SecurityConfig(JwtRequestFilter jwtRequestFilter, SpotifyOAuthFilter spotifyOAuthFilter, AdminAuthorizationFilter adminAuthorizationFilter, ClientRegistrationRepository clientRegistrationRepository) {
        this.jwtRequestFilter = jwtRequestFilter;
        this.spotifyOAuthFilter = spotifyOAuthFilter;
        this.adminAuthorizationFilter = adminAuthorizationFilter;
        this.clientRegistrationRepository = clientRegistrationRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/user/login", "/user/register", "/user/callback", "/loginSuccess", "/loginFailure", "/oauth2/**", "/login/oauth2/**").permitAll()
                .requestMatchers("/search/**", "/playlist/**").permitAll() // Allow access to search and playlist endpoints
                .anyRequest().authenticated()
            )
            .oauth2Login(oauth2 -> oauth2
                .clientRegistrationRepository(clientRegistrationRepository)
                .defaultSuccessUrl("/user/callback", true)
                .failureUrl("/loginFailure")
            )
            .logout(logout -> logout
                .logoutSuccessHandler(oidcLogoutSuccessHandler())
            );

        // Add filters in the correct order
        http.addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterAfter(spotifyOAuthFilter, JwtRequestFilter.class);
        http.addFilterAfter(adminAuthorizationFilter, SpotifyOAuthFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public OidcClientInitiatedLogoutSuccessHandler oidcLogoutSuccessHandler() {
        OidcClientInitiatedLogoutSuccessHandler successHandler = new OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository);
        successHandler.setPostLogoutRedirectUri("{baseUrl}");
        return successHandler;
    }
}

