package no.jonathan.quizapplication.quizquestion;

import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.repository.CrudRepository;

interface QuizQuestionRepository extends CrudRepository<QuizQuestion, Long> {

  @EntityGraph(attributePaths = {"answerOptions"})
  Optional<QuizQuestion> getById(Long id);
}
