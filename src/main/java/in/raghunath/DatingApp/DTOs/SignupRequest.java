package in.raghunath.DatingApp.DTOs;

import in.raghunath.DatingApp.Models.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SignupRequest {

    @NotBlank(message = "Username cannot be empty or contain only whitespace") // Use NotBlank for Strings
    private String username;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Please provide a valid email address")
    private String email;

    @NotBlank(message = "Gender cannot be empty")
    private Gender gender;


}