package no.jonathan.quizapplication.utils;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Collection;
import java.util.List;
import net.datafaker.Faker;
import no.jonathan.quizapplication.exception.NoPermissionException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@ExtendWith(MockitoExtension.class)
class AccessUtilTest {

  private Faker faker;

  @Mock private AccessUtil underTest;

  @BeforeEach
  void setUp() {
    this.faker = new Faker();
  }

  @Test
  @DisplayName("It should verify if user has access to entity if the user is the creator")
  void itShouldVerifyUserHasAccessToEntity() {
    // Given
    String createdByEmail = faker.internet().emailAddress();
    Authentication mockAuthentication = mock(Authentication.class);

    // When
    when(mockAuthentication.getName()).thenReturn(createdByEmail);

    // Then
    AccessUtil.verifyUserHasAccessToEntity(createdByEmail, mockAuthentication);

    verify(mockAuthentication, times(1)).getName();
  }

  @Test
  @DisplayName("It should verify if the user have role_admin and not the owner of the entity")
  void itShouldVerifyUserHasAccessToEntity_IfUserRoleAdmin() {
    // Given
    String createdByEmail = faker.internet().emailAddress();
    String authenticationName = createdByEmail + ".au";
    Authentication mockAuthentication = mock(Authentication.class);
    Collection<SimpleGrantedAuthority> grantedAuthorities =
        List.of(new SimpleGrantedAuthority("ADMIN"));

    // When
    when(mockAuthentication.getName()).thenReturn(authenticationName);

    when(mockAuthentication.getAuthorities()).thenReturn((Collection) grantedAuthorities);

    // Then
    AccessUtil.verifyUserHasAccessToEntity(createdByEmail, mockAuthentication);

    verify(mockAuthentication, times(1)).getName();
    verify(mockAuthentication, times(1)).getAuthorities();
  }

  @Test
  @DisplayName("It should not verify if the user have role_user and not the owner of the entity")
  void itShouldVerifyUserHasAccessToEntity_IfUserRoleUser() {
    // Given
    String createdByEmail = faker.internet().emailAddress();
    String authenticationName = createdByEmail + ".au";
    Authentication mockAuthentication = mock(Authentication.class);
    Collection<SimpleGrantedAuthority> grantedAuthorities =
        List.of(new SimpleGrantedAuthority("USER"));

    // When
    when(mockAuthentication.getName()).thenReturn(authenticationName);

    when(mockAuthentication.getAuthorities()).thenReturn((Collection) grantedAuthorities);

    // Then
    assertThatThrownBy(
            () -> AccessUtil.verifyUserHasAccessToEntity(createdByEmail, mockAuthentication))
        .isInstanceOf(NoPermissionException.class)
        .hasMessage("You are not allowed to do this!");

    verify(mockAuthentication, times(1)).getName();
    verify(mockAuthentication, times(1)).getAuthorities();
  }

  @Test
  @DisplayName(
      "It will throw if authentication is null when verifying if user has access to entity")
  void itWillThrowIfAuthenticationIsNullWhenVerifyUserHasAccessToEntity() {
    // Given
    String createdByEmail = faker.internet().emailAddress();

    // When
    // Then
    assertThatThrownBy(() -> AccessUtil.verifyUserHasAccessToEntity(createdByEmail, null))
        .isInstanceOf(NoPermissionException.class)
        .hasMessage("You are not allowed to do this!");
  }
}
