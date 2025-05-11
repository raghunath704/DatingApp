package in.raghunath.DatingApp.DTOs;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AuthResponse {
    private String message;
    private String token;

    // Getters and setters
}
