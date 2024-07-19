package no.jonathan.quizapplication.utils;

import no.jonathan.quizapplication.exception.NoPermissionException;
import no.jonathan.quizapplication.user.UserRole;
import org.springframework.security.core.Authentication;

public class AccessUtil {

  public static void verifyUserHasAccessToEntity(
      String createdByEmail, Authentication authentication) {
    boolean isEntityPublic = createdByEmail == null && authentication == null;
    if (isEntityPublic) {
      return;
    }

    if (authentication == null) {
      throw new NoPermissionException();
    }

    boolean wasEntityCreatedAnonymously = createdByEmail == null;
    if (wasEntityCreatedAnonymously) {
      return;
    }

    boolean isCreator = authentication.getName().equals(createdByEmail);
    if (isCreator) {
      return;
    }

    boolean isAdmin =
        authentication.getAuthorities().stream()
            .anyMatch(
                grantedAuthority -> grantedAuthority.getAuthority().equals(UserRole.ADMIN.name()));

    if (isAdmin) {
      return;
    }

    throw new NoPermissionException();
  }
}
