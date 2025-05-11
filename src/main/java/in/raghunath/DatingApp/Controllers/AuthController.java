package in.raghunath.DatingApp.Controllers;

import in.raghunath.DatingApp.DTOs.ApiResponse;
import in.raghunath.DatingApp.DTOs.AuthResponse;
import in.raghunath.DatingApp.DTOs.LoginRequest;
import in.raghunath.DatingApp.DTOs.SignupRequest;
import in.raghunath.DatingApp.Exceptions.TokenRefreshException;
import in.raghunath.DatingApp.Models.RefreshTokenModel;
import in.raghunath.DatingApp.Services.AuthService;
import in.raghunath.DatingApp.Services.RefreshTokenService;
import in.raghunath.DatingApp.Utils.JwtUtil;
import jakarta.servlet.http.Cookie; // Use jakarta imports for Spring Boot 3+
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value; // Import Value
import org.springframework.http.HttpHeaders; // Import HttpHeaders
import org.springframework.http.ResponseCookie; // Import ResponseCookie
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("/auth") // Base path for all auth endpoints
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService; // Inject RT service
    private final JwtUtil jwtUtil; // Inject JwtUtil for refresh

    @Value("${app.jwt.refresh-cookie-name}") // Inject cookie name
    private String refreshTokenCookieName;

    // Update constructor
    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtUtil jwtUtil) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        try {
            AuthResponse response = authService.registerUser(signupRequest);
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(new AuthResponse(ex.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            AuthService.LoginResult loginResult = authService.loginUser(loginRequest);
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, loginResult.cookie().toString())
                    .body(loginResult.authResponse());
        } catch (Exception ex) {
            return ResponseEntity.status(401).body(new ApiResponse(false,"Login failed: " + ex.getMessage()));
        }
    }

    // --- NEW LOGOUT ENDPOINT ---
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // 1. Extract Refresh Token from Cookie
        String refreshTokenValue = extractRefreshTokenFromCookie(request);

        // 2. Invalidate the Refresh Token in the database via AuthService
        authService.logoutUser(refreshTokenValue); // AuthService handles deletion and context clearing

        // 3. Create a cookie that instructs the browser to delete the refresh token cookie
        ResponseCookie clearCookie = ResponseCookie.from(refreshTokenCookieName, "") // Empty value
                .httpOnly(true)
                .secure(true) // Match secure attribute from login
                .path("/auth") // IMPORTANT: Must match the path set during login
                .maxAge(0) // Expire immediately
                .sameSite("Strict") // Match SameSite attribute from login
                .build();

        // 4. Return response with the clearing cookie
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                .body(new ApiResponse(true,"Logout successful!")); // Use AuthResponse for consistency
    }

    // --- NEW REFRESH TOKEN ENDPOINT ---
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(HttpServletRequest request) {
        String refreshTokenValue = extractRefreshTokenFromCookie(request);

        if (refreshTokenValue == null) {
            return ResponseEntity.status(401).body(new AuthResponse("Refresh token is missing", null));
        }

        try {
            // Find the token in DB and verify it's not expired
            RefreshTokenModel refreshToken = refreshTokenService.findByToken(refreshTokenValue)
                    .map(refreshTokenService::verifyExpiration)
                    .orElseThrow(() -> new TokenRefreshException(refreshTokenValue, "Refresh token not found in database!"));

            // Generate a new Access Token
            String newAccessToken = jwtUtil.generateAccessToken(refreshToken.getUsername());

            // Return the new Access Token
            return ResponseEntity.ok(new AuthResponse("Token refreshed successfully", newAccessToken));

        } catch (TokenRefreshException ex) {
            // If token is expired or invalid, force logout by clearing cookie
            ResponseCookie clearCookie = ResponseCookie.from(refreshTokenCookieName, "").path("/auth").maxAge(0).httpOnly(true).secure(true).sameSite("Strict").build();
            return ResponseEntity.status(403) // Forbidden
                    .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                    .body(new AuthResponse("Refresh token invalid: " + ex.getMessage(), null));
        } catch (Exception ex) {
            // Log unexpected errors
            ResponseCookie clearCookie = ResponseCookie.from(refreshTokenCookieName, "").path("/auth").maxAge(0).httpOnly(true).secure(true).sameSite("Strict").build();
            return ResponseEntity.status(500)
                    .header(HttpHeaders.SET_COOKIE, clearCookie.toString())
                    .body(new AuthResponse("Error refreshing token: " + ex.getMessage(), null));
        }
    }


    // --- Helper method to extract cookie ---
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return null;
        }
        // Use Stream API for cleaner extraction
        return Arrays.stream(cookies)
                .filter(cookie -> refreshTokenCookieName.equals(cookie.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }
}