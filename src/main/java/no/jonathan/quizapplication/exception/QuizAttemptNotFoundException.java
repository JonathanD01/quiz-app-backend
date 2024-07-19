package no.jonathan.quizapplication.exception;

import java.util.UUID;

public class QuizAttemptNotFoundException extends RuntimeException {

  public QuizAttemptNotFoundException(UUID quizAttemptLink) {
    super("Quiz attempt with link '" + quizAttemptLink + "' was not found");
  }
}
