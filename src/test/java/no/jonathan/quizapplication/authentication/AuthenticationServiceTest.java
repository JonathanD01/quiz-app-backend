package no.jonathan.quizapplication.authentication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import net.datafaker.Faker;
import no.jonathan.quizapplication.config.JwtService;
import no.jonathan.quizapplication.exception.EmailAlreadyTakenException;
import no.jonathan.quizapplication.user.User;
import no.jonathan.quizapplication.user.UserModuleApi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.quickperf.junit5.QuickPerfTest;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
@QuickPerfTest
class AuthenticationServiceTest {

  private final Faker faker = new Faker();

  private final String commonEmail = faker.internet().emailAddress();

  @Mock private UserModuleApi userModuleApi;

  @Mock private PasswordEncoder passwordEncoder;

  @Mock private JwtService jwtService;

  @Mock private AuthenticationManager authenticationManager;

  @InjectMocks private AuthenticationService underTest;

  private User user;

  @BeforeEach
  void setUp() {
    user =
        User.builder()
            .firstname(faker.name().firstName())
            .lastname(faker.name().lastName())
            .email(faker.internet().emailAddress())
            .password(faker.internet().password())
            .enabled(false)
            .build();
  }

  @AfterEach
  void tearDown() {}

  @Test
  @DisplayName("It should register a user")
  void canRegister() {
    // Given
    var registerRequest =
        new RegistrationRequest(
            faker.name().firstName(),
            faker.name().lastName(),
            commonEmail,
            faker.internet().password());

    // When
    when(userModuleApi.existsByEmail(registerRequest.email())).thenReturn(false);

    when(userModuleApi.save(any())).thenReturn(user);

    // Then
    underTest.register(registerRequest);

    verify(userModuleApi, times(1)).existsByEmail(registerRequest.email());
    verify(userModuleApi, times(1)).save(any());
  }

  @Test
  @DisplayName("It should not register a user if email is taken")
  void itWillThrowWhenRegisteringWithExistingEmail() {
    // Given
    var registerRequest =
        new RegistrationRequest(
            faker.name().firstName(),
            faker.name().lastName(),
            commonEmail,
            faker.internet().password());

    // When
    when(userModuleApi.existsByEmail(registerRequest.email())).thenReturn(true);

    // Then
    assertThatThrownBy(() -> underTest.register(registerRequest))
        .isInstanceOf(EmailAlreadyTakenException.class)
        .hasMessage(String.format("Email %s is already taken", registerRequest.email()));

    verify(userModuleApi, never()).save(any());
  }

  @Test
  @DisplayName("It should try to authenticate")
  void canAuthenticate() {
    // Given
    var authRequest = new AuthenticationRequest(user.getName(), user.getPassword());

    var mockAuthentication = mock(Authentication.class);

    // When
    when(authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getName(), user.getPassword())))
        .thenReturn(mockAuthentication);

    when(mockAuthentication.getPrincipal()).thenReturn(user);

    when(jwtService.generateToken(any(), any())).thenReturn("super-secret-jwt-token");

    // Then
    var result = underTest.authenticate(authRequest);

    assertThat(result.token()).isEqualTo("super-secret-jwt-token");
  }
}
