package springboot.services;

import PasswordHash.HashUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import springboot.model.Session;
import springboot.model.User;
import springboot.repository.SessionRepository;
import springboot.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalTime;


@RestController
public class UserControllerHibernate {
    private static final Logger LOGGER = LogManager.getLogger();

    private UserRepository userRepository;
    private SessionRepository sessionRepository;

    public UserControllerHibernate(UserRepository userRepository, SessionRepository sessionRepository) {
        this.userRepository = userRepository;
        this.sessionRepository = sessionRepository;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User login) {
        User user = userRepository.findByUserNameAndPassword(login.getUsername(), login.getPassword());

        if (user == null) {
            LOGGER.info("Error: credentials are invalid");
            return new ResponseEntity<>("", HttpStatus.valueOf(401));
        }

        String token = login.getUsername() + "/"  + LocalDate.now() + "/" + LocalTime.now();
        String HashedToken = HashUtils.getCryptoHash(token, "SHA-256");
        System.out.println("Unhashed token for Postman: " + token);

        Session session = new Session();
        session.setToken(HashedToken);
        session.setId(user.getId());
        sessionRepository.save(session);

        return new ResponseEntity<>(token, HttpStatus.valueOf(200));
    }
}