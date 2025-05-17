package in.raghunath.DatingApp.Services;

import in.raghunath.DatingApp.Models.UserModel;
import in.raghunath.DatingApp.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserModel createUser(UserModel user) {
        UserModel currentUser = new UserModel();
        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());

        userRepository.save(currentUser);
        return currentUser;

    }
    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }

    public UserModel updateUser(String id, UserModel user) {
        UserModel currentUser = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        currentUser.setFirstName(user.getFirstName());
        currentUser.setLastName(user.getLastName());
        currentUser.setBio(user.getBio());
        currentUser.setDateOfBirth(user.getDateOfBirth());
        currentUser.setUpdatedAt(new Date());
        return userRepository.save(currentUser);
    }
}
