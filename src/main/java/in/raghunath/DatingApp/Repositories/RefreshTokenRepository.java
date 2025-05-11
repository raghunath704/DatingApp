package in.raghunath.DatingApp.Repositories;

import in.raghunath.DatingApp.Models.RefreshTokenModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends MongoRepository<RefreshTokenModel, String> {

    Optional<RefreshTokenModel> findByToken(String token);

    // Method to delete a token by its value (used for logout)
    void deleteByToken(String token);

    // Optional: If you want to invalidate all tokens for a user (e.g., password change)
    void deleteByUsername(String username);
}