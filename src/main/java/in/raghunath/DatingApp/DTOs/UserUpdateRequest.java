package in.raghunath.DatingApp.DTOs;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import in.raghunath.DatingApp.Models.Gender;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Data
@Getter
@Setter
public class UserUpdateRequest {
    private String firstName;
    private String lastName;
    private String bio;
    private Date dateOfBirth;
    private List<Integer> preferredAgeRange;
    private List<String> interests;
}
