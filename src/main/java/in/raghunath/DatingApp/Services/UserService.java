package in.raghunath.DatingApp.Services;

import in.raghunath.DatingApp.Models.UserModel;
import in.raghunath.DatingApp.Repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public UserModel createUser(UserModel user) {
        UserModel currentUser = new UserModel();
        currentUser.setName(user.getName());
        currentUser.setAge(user.getAge());
        userRepository.save(currentUser);
        return currentUser;

    }
    public List<UserModel> getAllUsers() {
        return userRepository.findAll();
    }
}
