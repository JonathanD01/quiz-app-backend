package no.jonathan.quizapplication.user;

import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

  Optional<User> findByEmail(String username);

  boolean existsByEmail(String username);
}
