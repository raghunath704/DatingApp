package in.raghunath.DatingApp.DTOs;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class LoginRequest {
    private String username;
    private String password;
}