package in.raghunath.DatingApp.Services;


import in.raghunath.DatingApp.Repositories.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service("userSecurity")
public class UserSecurityService {
    private final UserRepository repo;
    public UserSecurityService(UserRepository repo) { this.repo = repo; }

    public boolean isOwner(Authentication auth, String userId) {
        return repo.findById(userId)
                .map(u -> u.getUsername().equals(auth.getName()))
                .orElse(false);
    }
}
