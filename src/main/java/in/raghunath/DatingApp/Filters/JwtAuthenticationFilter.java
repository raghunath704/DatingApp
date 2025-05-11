package in.raghunath.DatingApp.Filters;
import in.raghunath.DatingApp.Utils.JwtUtil;
import lombok.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtUtil jwtUtil, UserDetailsService userDetailsService) {
        this.jwtUtil = jwtUtil;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String usernameFromToken = null;

        // Typically the token is passed in the format "Bearer <token>"
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            try {
                usernameFromToken = jwtUtil.getUsernameFromToken(token); // Extract username from token
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid JWT Token received: " + e.getMessage());
            } catch (io.jsonwebtoken.ExpiredJwtException e) {
                logger.warn("Expired JWT Token received: " + e.getMessage());
            }

            // If we got a username AND SecurityContext is empty (user not already authenticated)
            if (usernameFromToken != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                // Load UserDetails from database ONLY if token is valid
                UserDetails userDetails = userDetailsService.loadUserByUsername(usernameFromToken);

                // Validate the ACCESS token against the loaded UserDetails' username
                if (jwtUtil.validateAccessToken(token, userDetails.getUsername())) {
                    // If token is valid, create authentication token
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, // Principal
                                    null,        // Credentials (not needed for JWT)
                                    userDetails.getAuthorities() // Authorities
                            );

                    // Set details (IP address, session ID if applicable)
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // Set the authentication in the SecurityContext
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    logger.debug("Authentication successful for user: " + usernameFromToken);
                } else {
                    logger.warn("JWT Token validation failed for user: " + usernameFromToken);
                }
            }
        } else {
            logger.trace("No JWT Token found in Authorization header");
        }

        // Continue the filter chain
        filterChain.doFilter(request, response);
    }
}
