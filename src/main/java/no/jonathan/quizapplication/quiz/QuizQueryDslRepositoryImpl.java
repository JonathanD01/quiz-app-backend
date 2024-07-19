package no.jonathan.quizapplication.quiz;

import static no.jonathan.quizapplication.quiz.QuizRepositoryCustom.Specifications.*;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.EntityPath;
import com.querydsl.jpa.JPQLQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import no.jonathan.quizapplication.quizansweroption.QQuizAnswerOption;
import no.jonathan.quizapplication.quizattempt.QQuizAttempt;
import no.jonathan.quizapplication.quizattempt.QQuizUserAnswer;
import no.jonathan.quizapplication.quizattempt.QuizAttempt;
import no.jonathan.quizapplication.quizquestion.QQuizQuestion;
import no.jonathan.quizapplication.shared.Hibernate6QuerydslRepositorySupport;
import no.jonathan.quizapplication.shared.QuizDtoMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
class QuizQueryDslRepositoryImpl extends Hibernate6QuerydslRepositorySupport
    implements QuizRepositoryCustom {

  private final QuizDtoMapper quizDtoMapper;
  @PersistenceContext private EntityManager entityManager;

  public QuizQueryDslRepositoryImpl(QuizDtoMapper quizDtoMapper) {
    super(Quiz.class);
    this.quizDtoMapper = quizDtoMapper;
  }

  @Override
  public Optional<Quiz> findById(Long id) {
    var quizTable = QQuiz.quiz;
    QQuizQuestion quizQuestionTable = QQuizQuestion.quizQuestion;

    Quiz quiz =
        from(quizTable)
            .select(quizTable)
            .where(hasId(id))
            .leftJoin(quizTable.creator)
            .fetchJoin()
            .leftJoin(quizTable.questions, quizQuestionTable)
            .fetchJoin()
            .leftJoin(quizQuestionTable.answerOptions)
            .fetchJoin()
            .fetchOne();

    return Optional.ofNullable(quiz);
  }

  @Override
  public Optional<QuizDto> findDtoById(Long id) {
    var quizTable = QQuiz.quiz;
    QQuizQuestion quizQuestionTable = QQuizQuestion.quizQuestion;

    Quiz quiz =
        from(quizTable)
            .select(quizTable)
            .where(hasId(id))
            .leftJoin(quizTable.creator)
            .fetchJoin()
            .leftJoin(quizTable.questions, quizQuestionTable)
            .fetchJoin()
            .leftJoin(quizQuestionTable.answerOptions)
            .fetchJoin()
            .fetchOne();

    if (quiz == null) {
      return Optional.empty();
    }
    return Optional.of(quizDtoMapper.apply(quiz));
  }

  @Override
  public Optional<Quiz> findByLink(UUID link) {
    var quizTable = QQuiz.quiz;
    QQuizQuestion quizQuestionTable = QQuizQuestion.quizQuestion;

    Quiz quiz =
        from(quizTable)
            .select(quizTable)
            .where(hasLink(link))
            .leftJoin(quizTable.creator)
            .fetchJoin()
            .leftJoin(quizTable.questions, quizQuestionTable)
            .fetchJoin()
            .leftJoin(quizQuestionTable.answerOptions)
            .fetchJoin()
            .fetchOne();

    return Optional.ofNullable(quiz);
  }

  @Override
  public Optional<QuizDto> findDtoByLink(UUID link) {
    var quizTable = QQuiz.quiz;
    QQuizQuestion quizQuestionTable = QQuizQuestion.quizQuestion;

    Quiz quiz =
        from(quizTable)
            .select(quizTable)
            .where(hasLink(link))
            .leftJoin(quizTable.creator)
            .fetchJoin()
            .leftJoin(quizTable.questions, quizQuestionTable)
            .fetchJoin()
            .leftJoin(quizQuestionTable.answerOptions)
            .fetchJoin()
            .fetchOne();

    if (quiz == null) {
      return Optional.empty();
    }
    return Optional.of(quizDtoMapper.apply(quiz));
  }

  @Override
  public Page<QuizCoreProjection> findAll(String title, String email, Pageable pageable) {
    var quizTable = QQuiz.quiz;

    JPQLQuery<QuizCoreProjection> query =
        from(quizTable)
            .select(
                new QQuizCoreProjection(
                    quizTable.id,
                    quizTable.link,
                    quizTable.status,
                    quizTable.title,
                    quizTable.description))
            .leftJoin(quizTable.creator)
            .where(byTitleAndEmail(title, email))
            .orderBy(orderByCreatedAtDesc());

    query = super.getQuerydsl().applyPagination(pageable, query);

    QueryResults<QuizCoreProjection> results = query.fetchResults();

    return new PageImpl<>(results.getResults(), pageable, results.getTotal());
  }

  @Transactional
  @Override
  public void delete(Quiz quizToDelete) {
    // https://thorben-janssen.com/avoid-cascadetype-delete-many-assocations/#problems-with-cascadetyperemove-for-tomany-associations

    // Fetch quiz attempt ids
    var quizAttemptTable = QQuizAttempt.quizAttempt;
    JPQLQuery<Long> quizAttemptIdsQuery = from(quizAttemptTable)
            .select(quizAttemptTable.id)
            .where(quizAttemptTable.quiz.id.eq(quizToDelete.getId()));

    List<Long> quizAttemptIds = quizAttemptIdsQuery.fetch();
    //

    // Remove elemental collection QuizUserAnswer
    Query q = entityManager.createNativeQuery("DELETE FROM quiz_user_answers qua WHERE qua.quiz_attempt_id IN (:ids)");
    q.setParameter("ids", quizAttemptIds);
    q.executeUpdate();

    // Fetch quiz question ids
    var quizQuestionTable = QQuizQuestion.quizQuestion;
    JPQLQuery<Long> quizQuestionIdsQuery = from(quizQuestionTable)
            .select(quizQuestionTable.id)
            .where(quizQuestionTable.quiz.id.eq(quizToDelete.getId()));

    List<Long> quizQuestionIds = quizQuestionIdsQuery.fetch();
    //

    // Fetch quiz answer options ids
    var quizAnswerOptionsTable = QQuizAnswerOption.quizAnswerOption;
    JPQLQuery<Long> quizAnswerOptionsTableIdsQuery = from(quizAnswerOptionsTable)
            .select(quizAnswerOptionsTable.id)
            .where(quizAnswerOptionsTable.question.id.in(quizQuestionIds));

    List<Long> quizAnswerOptionsIds = quizAnswerOptionsTableIdsQuery.fetch();
    //

    // remove quiz attempts
    q = entityManager.createNativeQuery("DELETE FROM quiz_attempts qa WHERE qa.id IN (:ids)");
    q.setParameter("ids", quizAttemptIds);
    q.executeUpdate();

    // Remove quiz answer options
    q = entityManager.createNativeQuery("DELETE FROM quiz_answer_options qao WHERE qao.id IN (:ids)");
    q.setParameter("ids", quizAnswerOptionsIds);
    q.executeUpdate();

    // Remove quiz questions
    q = entityManager.createNativeQuery("DELETE FROM quiz_questions qq WHERE qq.id IN (:ids)");
    q.setParameter("ids", quizQuestionIds);
    q.executeUpdate();

    // remove quiz
    entityManager.remove(quizToDelete);
  }
}
