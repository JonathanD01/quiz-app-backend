package no.jonathan.quizapplication.authentication;

import java.util.HashMap;
import no.jonathan.quizapplication.config.JwtService;
import no.jonathan.quizapplication.exception.EmailAlreadyTakenException;
import no.jonathan.quizapplication.user.User;
import no.jonathan.quizapplication.user.UserModuleApi;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthenticationService {

  private final UserModuleApi userModuleApi;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final AuthenticationManager authenticationManager;

  public AuthenticationService(
      UserModuleApi userModuleApi,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      AuthenticationManager authenticationManager) {
    this.userModuleApi = userModuleApi;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.authenticationManager = authenticationManager;
  }

  @Transactional
  public void register(RegistrationRequest request) {
    var user =
        User.builder()
            .firstname(request.firstname())
            .lastname(request.lastname())
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .build();

    var userExists = userModuleApi.existsByEmail(request.email());

    if (userExists) {
      throw new EmailAlreadyTakenException(request.email());
    }

    userModuleApi.save(user);
  }

  public AuthenticationResponse authenticate(AuthenticationRequest request) {
    var auth =
        authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password()));

    return getAuthenticationResponse(auth);
  }

  private AuthenticationResponse getAuthenticationResponse(Authentication auth) {
    var claims = new HashMap<String, Object>();
    var user = ((User) auth.getPrincipal());
    claims.put("fullName", user.getFullName());
    claims.put("firstname", user.getFirstname());
    claims.put("lastname", user.getLastname());

    var jwtToken = jwtService.generateToken(claims, (User) auth.getPrincipal());
    return new AuthenticationResponse(jwtToken);
  }
}
