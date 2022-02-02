package springboot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import springboot.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

    @Query("select x from User x where x.username like :user_name and x.password like :password")
    User findByUserNameAndPassword(@Param("user_name") String username, @Param("password") String password);
}
