package in.raghunath.DatingApp.Repositories;

import in.raghunath.DatingApp.Models.UserModel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends MongoRepository<UserModel, String> {

}
