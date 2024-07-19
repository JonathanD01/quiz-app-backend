package no.jonathan.quizapplication.quizquestion;

import no.jonathan.quizapplication.exception.QuizQuestionNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class QuizQuestionModuleApi {

  private final QuizQuestionRepository quizQuestionRepository;

  public QuizQuestionModuleApi(QuizQuestionRepository quizQuestionRepository) {
    this.quizQuestionRepository = quizQuestionRepository;
  }

  public QuizQuestion findById(Long id) {
    return quizQuestionRepository
        .findById(id)
        .orElseThrow(() -> new QuizQuestionNotFoundException(id));
  }
}
