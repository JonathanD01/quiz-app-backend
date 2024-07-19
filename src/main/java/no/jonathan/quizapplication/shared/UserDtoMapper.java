package no.jonathan.quizapplication.shared;

import java.util.function.Function;
import no.jonathan.quizapplication.user.User;
import no.jonathan.quizapplication.user.UserDto;

public class UserDtoMapper implements Function<User, UserDto> {

  @Override
  public UserDto apply(User user) {
    return new UserDto(
        user.getId(),
        user.getFirstname(),
        user.getLastname(),
        user.getName(),
        user.isAccountLocked(),
        user.isEnabled(),
        user.getCreatedAt(),
        user.getUpdatedAt(),
        user.getUserRole().name());
  }
}
