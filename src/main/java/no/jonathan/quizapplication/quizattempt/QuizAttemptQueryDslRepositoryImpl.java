package no.jonathan.quizapplication.quizattempt;

import static no.jonathan.quizapplication.quizattempt.QuizAttemptRepositoryCustom.Specifications.*;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPQLQuery;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.util.Optional;
import java.util.UUID;
import no.jonathan.quizapplication.quizquestion.QQuizQuestion;
import no.jonathan.quizapplication.shared.Hibernate6QuerydslRepositorySupport;
import no.jonathan.quizapplication.shared.QuizAttemptDtoMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public class QuizAttemptQueryDslRepositoryImpl extends Hibernate6QuerydslRepositorySupport
    implements QuizAttemptRepositoryCustom {

  private final QuizAttemptDtoMapper quizAttemptDtoMapper;
  private final QuizAttemptRepository quizAttemptRepository;
  @PersistenceContext private EntityManager entityManager;

  public QuizAttemptQueryDslRepositoryImpl(
      QuizAttemptDtoMapper quizAttemptDtoMapper, QuizAttemptRepository quizAttemptRepository) {
    super(QuizAttempt.class);
    this.quizAttemptDtoMapper = quizAttemptDtoMapper;
    this.quizAttemptRepository = quizAttemptRepository;
  }

  @Override
  public Optional<QuizAttemptDto> findById(Long id) {
    var quizAttemptTable = QQuizAttempt.quizAttempt;
    QQuizQuestion quizQuestionTable = QQuizQuestion.quizQuestion;

    QuizAttempt quizAttempt =
        from(quizAttemptTable)
            .select(quizAttemptTable)
            .where(hasId(id))
            .leftJoin(quizAttemptTable.attemptBy)
            .fetchJoin()
            .leftJoin(quizAttemptTable.quiz)
            .fetchJoin()
            .leftJoin(quizAttemptTable.quiz.questions, quizQuestionTable)
            .fetchJoin()
            .leftJoin(quizQuestionTable.answerOptions)
            .fetchJoin()
            .leftJoin(quizAttemptTable.userAnswers)
            .fetchJoin()
            .fetchOne();

    if (quizAttempt == null) {
      return Optional.empty();
    }
    return Optional.of(quizAttemptDtoMapper.apply(quizAttempt));
  }

  @Override
  public Optional<QuizAttemptDto> findByLink(UUID quizLink) {
    var quizAttemptTable = QQuizAttempt.quizAttempt;
    QQuizQuestion quizQuestionTable = QQuizQuestion.quizQuestion;

    QuizAttempt quizAttempt =
        from(quizAttemptTable)
            .select(quizAttemptTable)
            .where(hasLink(quizLink))
            .leftJoin(quizAttemptTable.attemptBy)
            .fetchJoin()
            .leftJoin(quizAttemptTable.quiz)
            .fetchJoin()
            .leftJoin(quizAttemptTable.quiz.questions, quizQuestionTable)
            .fetchJoin()
            .leftJoin(quizQuestionTable.answerOptions)
            .fetchJoin()
            .leftJoin(quizAttemptTable.userAnswers)
            .fetchJoin()
            .fetchOne();

    if (quizAttempt == null) {
      return Optional.empty();
    }
    return Optional.of(quizAttemptDtoMapper.apply(quizAttempt));
  }

  @Override
  public Page<QuizAttemptCoreProjection> findAll(String title, String email, Pageable pageable) {
    var quizAttemptTable = QQuizAttempt.quizAttempt;

    JPQLQuery<QuizAttemptCoreProjection> query =
        from(quizAttemptTable)
            .select(
                new QQuizAttemptCoreProjection(
                    quizAttemptTable.id,
                    quizAttemptTable.link,
                    quizAttemptTable.quiz.title,
                    quizAttemptTable.quiz.description,
                    quizAttemptTable.createdAt))
            .where(hasEmail(email).and(hasTitle(title)))
            .leftJoin(quizAttemptTable.quiz)
            .orderBy(orderByCreatedAtDesc());

    query = super.getQuerydsl().applyPagination(pageable, query);

    QueryResults<QuizAttemptCoreProjection> results = query.fetchResults();

    return new PageImpl<>(results.getResults(), pageable, results.getTotal());
  }
}
