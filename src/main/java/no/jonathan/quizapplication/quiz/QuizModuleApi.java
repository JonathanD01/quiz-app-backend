package no.jonathan.quizapplication.quiz;

import java.util.UUID;
import no.jonathan.quizapplication.exception.QuizNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class QuizModuleApi {

  private final QuizRepository quizRepository;
  private final QuizRepositoryCustom quizRepositoryCustom;

  public QuizModuleApi(QuizRepository quizRepository, QuizRepositoryCustom quizRepositoryCustom) {
    this.quizRepository = quizRepository;
    this.quizRepositoryCustom = quizRepositoryCustom;
  }

  public Quiz findById(Long quizId) {
    return quizRepositoryCustom
        .findById(quizId)
        .orElseThrow(() -> new QuizNotFoundException(quizId));
  }

  public Quiz save(Quiz unfinishedQuiz) {
    return quizRepository.save(unfinishedQuiz);
  }

  public Quiz findByLink(UUID link) {
    return quizRepositoryCustom.findByLink(link).orElseThrow(() -> new QuizNotFoundException(link));
  }
}
