package in.raghunath.DatingApp.Services;

import in.raghunath.DatingApp.DTOs.UserUpdateRequest;
import in.raghunath.DatingApp.Models.UserModel;
import in.raghunath.DatingApp.Repositories.UserRepository;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    @PreAuthorize("@userSecurity.isOwner(authentication, #id)")
    public UserModel updateUser(String id, UserUpdateRequest user) {
        UserModel currentUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());
        currentUser.setBio(user.getBio());
        currentUser.setInterests(user.getInterests());
        currentUser.setPreferredAgeRange(user.getPreferredAgeRange());
        currentUser.setDateOfBirth(user.getDateOfBirth());
        currentUser.setBio(user.getBio());
        currentUser.setUpdatedAt(new Date());
        return userRepository.save(currentUser);
    }
    @PreAuthorize("@userSecurity.isOwner(authentication, #id)")
    public void deleteUserById(String id) {
        userRepository.deleteById(id);
    }
    public UserModel getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
    }
    public UserModel getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }
}
