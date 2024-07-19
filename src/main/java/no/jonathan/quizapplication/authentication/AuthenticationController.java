package no.jonathan.quizapplication.authentication;

import jakarta.validation.Valid;
import no.jonathan.quizapplication.response.Response;
import no.jonathan.quizapplication.response.ResponseUtil;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
public class AuthenticationController {

  private final AuthenticationService authenticateService;
  private final ResponseUtil responseUtil;

  public AuthenticationController(
      AuthenticationService authenticateService, ResponseUtil responseUtil) {
    this.authenticateService = authenticateService;
    this.responseUtil = responseUtil;
  }

  @PostMapping("register")
  @ResponseStatus(HttpStatus.ACCEPTED)
  public ResponseEntity<?> registerAccount(@RequestBody @Valid RegistrationRequest request) {
    authenticateService.register(request);
    return ResponseEntity.ok().build();
  }

  @PostMapping("authenticate")
  public ResponseEntity<Response<AuthenticationResponse>> authenticate(
      @RequestBody AuthenticationRequest request) {
    return ResponseEntity.ok(
        responseUtil.buildSuccessResponse(authenticateService.authenticate(request)));
  }
}
