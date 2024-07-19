package no.jonathan.quizapplication.quizquestion;

import static no.jonathan.quizapplication.quizquestion.QuizQuestionRepositoryCustom.Specifications.hasId;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import java.util.Optional;
import no.jonathan.quizapplication.quiz.*;
import no.jonathan.quizapplication.shared.Hibernate6QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

@Repository
class QuizQuestionQueryDslRepositoryImpl extends Hibernate6QuerydslRepositorySupport
    implements QuizQuestionRepositoryCustom {

  @PersistenceContext private EntityManager entityManager;

  public QuizQuestionQueryDslRepositoryImpl() {
    super(Quiz.class);
  }

  @Override
  public Optional<QuizQuestion> findById(Long id) {
    var quizQuestionTable = QQuizQuestion.quizQuestion;

    QuizQuestion quizQuestion =
        from(quizQuestionTable)
            .select(quizQuestionTable)
            .where(hasId(id))
            .leftJoin(quizQuestionTable.answerOptions)
            .fetchJoin()
            .fetchOne();

    return Optional.ofNullable(quizQuestion);
  }

  @Transactional
  @Override
  public void delete(QuizQuestion quizQuestion) {
    // https://thorben-janssen.com/avoid-cascadetype-delete-many-assocations/#problems-with-cascadetyperemove-for-tomany-associations

    // Remove quiz answer options
    Query q =
        entityManager.createNativeQuery(
            "DELETE FROM quiz_answer_options qao WHERE qao.quiz_question_id = (:id)");
    q.setParameter("id", quizQuestion.getId());
    q.executeUpdate();

    // remove quiz question
    entityManager.remove(quizQuestion);
  }
}
