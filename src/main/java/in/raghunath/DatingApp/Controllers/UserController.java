package in.raghunath.DatingApp.Controllers;

import in.raghunath.DatingApp.DTOs.UserUpdateRequest;
import in.raghunath.DatingApp.Models.UserModel;
import in.raghunath.DatingApp.Services.RecommendationService;
import in.raghunath.DatingApp.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;
    private final RecommendationService recommendationService;

    public UserController(UserService userService, RecommendationService recommendationService) {
        this.userService = userService;
        this.recommendationService = recommendationService;
    }

    @GetMapping("/api/users")
    public ResponseEntity<List<UserModel>> getAllUsers() {
        List<UserModel> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    //Score-based recommendation
    @GetMapping("/api/users/recommend/{userId}")
    public ResponseEntity<List<UserModel>> recommendUsers(@PathVariable String userId,
                                                          @RequestParam(defaultValue = "5") int topN) {
        List<UserModel> recommendations = recommendationService.recommendUsers(userId, topN);
        return ResponseEntity.ok(recommendations);
    }
    @PutMapping("/api/users/{id}")
    public ResponseEntity<UserModel> updateUser(@PathVariable String id, @RequestBody UserUpdateRequest user) {
        UserModel updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(updatedUser);
    }
    @DeleteMapping("/api/users/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable String id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }



}
