package in.raghunath.DatingApp.Services;

import in.raghunath.DatingApp.Exceptions.TokenRefreshException;
import in.raghunath.DatingApp.Models.RefreshTokenModel;
import in.raghunath.DatingApp.Repositories.RefreshTokenRepository;
import in.raghunath.DatingApp.Repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${app.jwt.refreshToken.expiration}")
    private Long refreshTokenDurationMs;

    private final RefreshTokenRepository refreshTokenRepo;
    private final UserRepository userRepo; // To ensure user exists

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepo, UserRepository userRepo) {
        this.refreshTokenRepo = refreshTokenRepo;
        this.userRepo = userRepo;
    }

    public Optional<RefreshTokenModel> findByToken(String token) {
        return refreshTokenRepo.findByToken(token);
    }

    public RefreshTokenModel createRefreshToken(String username) {
        // Ensure the user actually exists before creating a token for them
        userRepo.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Error: User not found for refresh token creation - " + username));

        RefreshTokenModel refreshToken = new RefreshTokenModel();
        refreshToken.setUsername(username);
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setToken(UUID.randomUUID().toString()); // Generate a secure random opaque token

        refreshToken = refreshTokenRepo.save(refreshToken);
        return refreshToken;
    }

    public RefreshTokenModel verifyExpiration(RefreshTokenModel token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepo.delete(token); // Clean up expired tokens
            throw new TokenRefreshException(token.getToken(), "Refresh token was expired. Please make a new signin request");
        }
        return token;
    }

    // Core method for logout: Delete the token from the database
    public void deleteByToken(String token) {
        refreshTokenRepo.deleteByToken(token);
    }

    // Optional: Invalidate all tokens for a user
    public void deleteByUsername(String username) {
        refreshTokenRepo.deleteByUsername(username);
    }
}