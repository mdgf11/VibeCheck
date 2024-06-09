package pt.migfonseca.vibecheck.filter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import pt.migfonseca.vibecheck.model.User;
import pt.migfonseca.vibecheck.service.UserService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

@Component
@Order(2) // Ensure this filter runs after JwtRequestFilter
public class AdminAuthorizationFilter extends OncePerRequestFilter {

    private static final Logger LOGGER = Logger.getLogger(AdminAuthorizationFilter.class.getName());

    private static final List<String> WHITELISTED_URLS = List.of(
        "/user/login", 
        "/user/register", 
        "/user/callback", 
        "/loginSuccess", 
        "/loginFailure", 
        "/oauth2/authorization/spotify", 
        "/login/oauth2/code/spotify"
    );

    private final UserService userService;

    @Autowired
    public AdminAuthorizationFilter(@Lazy UserService userService) {
        this.userService = userService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String requestUri = request.getRequestURI();

        // Skip filtering for whitelisted URLs
        if (WHITELISTED_URLS.stream().anyMatch(requestUri::startsWith)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (requestUri.startsWith("/songs")) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.isAuthenticated()) {
                Object principal = authentication.getPrincipal();
                String email = null;

                if (principal instanceof UserDetails) {
                    email = ((UserDetails) principal).getUsername();
                } else if (principal instanceof String) {
                    email = (String) principal;
                }

                if (email != null) {
                    User user = userService.findByEmail(email).orElse(null);
                    if (user != null && user.isAdmin()) {
                        LOGGER.info("User has admin role, proceeding with request");
                        filterChain.doFilter(request, response);
                        return;
                    } else {
                        LOGGER.warning("User does not have admin role");
                        response.sendError(HttpServletResponse.SC_FORBIDDEN, "You do not have the necessary permissions to access this resource.");
                        return;
                    }
                } else {
                    LOGGER.warning("Could not extract user email from principal");
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You are not authenticated.");
                    return;
                }
            } else {
                LOGGER.warning("User is not authenticated");
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "You are not authenticated.");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
