package no.jonathan.quizapplication.exception;

public class QuizAnswerOptionNotFoundException extends RuntimeException {

  public QuizAnswerOptionNotFoundException(Long quizAnswerOptionId) {
    super("Quiz answer option with id '" + quizAnswerOptionId + "' was not found");
  }
}
