package no.jonathan.quizapplication.quiz;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

interface QuizRepositoryCustom {

  Optional<Quiz> findById(Long id);

  Optional<QuizDto> findDtoById(Long id);

  Optional<Quiz> findByLink(UUID link);

  Optional<QuizDto> findDtoByLink(UUID link);

  Page<QuizCoreProjection> findAll(String title, String email, Pageable pageable);

  void delete(Quiz quizToDelete);

  interface Specifications {

    static BooleanExpression hasId(Long id) {
      var quizTable = QQuiz.quiz;
      return quizTable.id.eq(id);
    }

    static BooleanExpression hasLink(UUID link) {
      var quizTable = QQuiz.quiz;
      return quizTable.link.eq(link);
    }

    static Predicate byTitleAndEmail(String title, String email) {
      BooleanBuilder booleanBuilder = new BooleanBuilder();
      booleanBuilder.and(hasTitle(title));
      booleanBuilder.and(hasEmail(email));
      return booleanBuilder.getValue();
    }

    static BooleanExpression hasEmail(String email) {
      var quizTable = QQuiz.quiz;
      return quizTable.creator.email.eq(email);
    }

    static OrderSpecifier<LocalDateTime> orderByCreatedAtDesc() {
      var quizTable = QQuiz.quiz;
      return quizTable.createdAt.desc();
    }

    static BooleanExpression hasTitle(String title) {
      if (title == null) {
        return null;
      }
      var quizTable = QQuiz.quiz;
      return quizTable.title.containsIgnoreCase(title);
    }
  }
}
