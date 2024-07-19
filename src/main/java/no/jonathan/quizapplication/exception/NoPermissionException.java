package no.jonathan.quizapplication.exception;

public class NoPermissionException extends RuntimeException {

  public NoPermissionException() {
    super("You are not allowed to do this!");
  }
}
