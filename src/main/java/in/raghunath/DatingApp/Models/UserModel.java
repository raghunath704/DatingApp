package in.raghunath.DatingApp.Models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Users")
public class UserModel {

    @Id
    private String id;

    @Indexed(unique = true)
    private String username;

    @Indexed(unique = true)
    private String email;

    @JsonIgnore
    private String password;

    private String firstName;
    private String lastName;
    private Gender gender;
    private Date dateOfBirth;

    //No need to store this in DB
    @Transient
    @JsonProperty(access = READ_ONLY)
    public Integer getAge() {
        if (dateOfBirth == null) return null;
        LocalDate dob = dateOfBirth.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return Period.between(dob, LocalDate.now()).getYears();
    }
    private String bio;
    private List<Integer> preferredAgeRange;
    private List<String> interests;
    @CreatedDate
    private Date createdAt;
    @CreatedDate
    private Date updatedAt;





}
