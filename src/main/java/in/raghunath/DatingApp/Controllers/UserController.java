package in.raghunath.DatingApp.Controllers;

import in.raghunath.DatingApp.DTOs.UserUpdateRequest;
import in.raghunath.DatingApp.Models.UserModel;
import in.raghunath.DatingApp.Services.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/users")
    public ResponseEntity<List<UserModel>> getAllUsers() {
        List<UserModel> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
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
