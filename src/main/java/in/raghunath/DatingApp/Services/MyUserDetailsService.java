package in.raghunath.DatingApp.Services;
import in.raghunath.DatingApp.Models.UserModel;
import in.raghunath.DatingApp.Repositories.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Alias for Spring Security's User
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MyUserDetailsService implements UserDetailsService {



    private final UserRepository userRepo;

    public MyUserDetailsService(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Fetch user from MongoDB
        UserModel appUser = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));


        // Map your custom User model to Spring Security's UserDetails
        return  org.springframework.security.core.userdetails.User.builder()
                .username(appUser.getUsername())
                .password(appUser.getPassword())
                .build();
    }
}
