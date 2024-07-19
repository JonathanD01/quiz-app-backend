package no.jonathan.quizapplication.user;

import org.springframework.stereotype.Service;

@Service
public class UserModuleApi {

  private final UserRepository userRepository;

  public UserModuleApi(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public boolean existsByEmail(String email) {
    return userRepository.existsByEmail(email);
  }

  public User save(User user) {
    return userRepository.save(user);
  }
}
