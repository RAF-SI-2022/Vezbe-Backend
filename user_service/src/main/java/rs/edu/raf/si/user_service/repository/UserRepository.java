package rs.edu.raf.si.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import rs.edu.raf.si.user_service.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findUserByUsername(String username);

}
