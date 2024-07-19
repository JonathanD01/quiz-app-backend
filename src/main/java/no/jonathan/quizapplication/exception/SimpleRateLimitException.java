package no.jonathan.quizapplication.exception;

public class SimpleRateLimitException extends RuntimeException {

  public SimpleRateLimitException() {
    super("You are currently rate limited. Please wait an hour");
  }
}
