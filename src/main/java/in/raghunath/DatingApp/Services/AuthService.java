package in.raghunath.DatingApp.Services;
import in.raghunath.DatingApp.DTOs.AuthResponse;
import in.raghunath.DatingApp.DTOs.LoginRequest;
import in.raghunath.DatingApp.DTOs.SignupRequest;
import in.raghunath.DatingApp.Models.RefreshTokenModel;
import in.raghunath.DatingApp.Models.UserModel;
import in.raghunath.DatingApp.Repositories.UserRepository;
import in.raghunath.DatingApp.Utils.JwtUtil;
import org.springframework.beans.factory.annotation.Value; // Import Value
import org.springframework.http.ResponseCookie; // Import ResponseCookie
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication; // Import Authentication
import org.springframework.security.core.context.SecurityContextHolder; // Import SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Optional: for signup atomicity

import java.util.Date;


@Service
public class AuthService {

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final RefreshTokenService refreshTokenService;

    @Value("${app.jwt.refresh-cookie-name}")
    private String refreshTokenCookieName;

    @Value("${app.jwt.refreshToken.expiration}")
    private Long refreshTokenDurationMs;

    public AuthService(UserRepository userRepo,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil,
                       AuthenticationManager authenticationManager,
                       RefreshTokenService refreshTokenService) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authenticationManager = authenticationManager;
        this.refreshTokenService = refreshTokenService; // Assign injected service
    }

    @Transactional
    public AuthResponse registerUser(SignupRequest signupRequest) {
        if (userRepo.findByUsername(signupRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists: " + signupRequest.getUsername());
        }
        if (userRepo.findByEmail(signupRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists: " + signupRequest.getEmail());
        }

        UserModel user = new UserModel();
        user.setUsername(signupRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setEmail(signupRequest.getEmail());
        user.setGender(signupRequest.getGender());
        user.setCreatedAt(new Date());
        userRepo.save(user);
        return new AuthResponse("Registration successful. Please login.", null);
    }

    public LoginResult loginUser(LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String username = authentication.getName();
        String accessToken = jwtUtil.generateAccessToken(username);
        RefreshTokenModel refreshToken = refreshTokenService.createRefreshToken(username);
        // Create HttpOnly cookie for the Refresh Token
        ResponseCookie refreshTokenCookie = ResponseCookie
                .from(refreshTokenCookieName, refreshToken.getToken())
                .httpOnly(true)
                .secure(true)
                .path("/auth")
                .maxAge(refreshTokenDurationMs / 1000)
                .sameSite("Strict")
                .build();

        // Return both the Access Token (for client JS) and the Cookie (for browser)
        return new LoginResult(new AuthResponse("Login successful", accessToken), refreshTokenCookie);
    }

    public void logoutUser(String refreshTokenValue) {
        if (refreshTokenValue != null) {
            refreshTokenService.deleteByToken(refreshTokenValue);
            //Blacklist the current Access Token if needed (advanced) :To be done later
        }
        SecurityContextHolder.clearContext();
    }


    // Helper record to return both AuthResponse and Cookie from loginUser
    public record LoginResult(AuthResponse authResponse, ResponseCookie cookie) {}
}