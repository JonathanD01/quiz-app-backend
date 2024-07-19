package no.jonathan.quizapplication.exception;

import java.util.UUID;

public class QuizNotFoundException extends RuntimeException {

  public QuizNotFoundException(Long quizId) {
    super("Quiz with id '" + quizId + "' was not found");
  }

  public QuizNotFoundException(UUID quizLink) {
    super("Quiz with link '" + quizLink.toString() + "' was not found");
  }
}
