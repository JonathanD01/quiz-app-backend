package no.jonathan.quizapplication.quizattempt;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import com.querydsl.core.types.dsl.Expressions;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

interface QuizAttemptRepositoryCustom {

  Optional<QuizAttemptDto> findById(Long id);

  Optional<QuizAttemptDto> findByLink(UUID quizLink);

  Page<QuizAttemptCoreProjection> findAll(String title, String email, Pageable pageable);

  interface Specifications {

    static BooleanExpression hasId(Long id) {
      var quizAttemptTable = QQuizAttempt.quizAttempt;
      return quizAttemptTable.id.eq(id);
    }

    static BooleanExpression hasLink(UUID link) {
      var quizAttemptTable = QQuizAttempt.quizAttempt;
      return quizAttemptTable.link.eq(link);
    }

    static BooleanExpression hasEmail(String email) {
      var quizAttemptTable = QQuizAttempt.quizAttempt;
      return quizAttemptTable.attemptBy.email.eq(email);
    }

    static BooleanExpression hasTitle(String title) {
      if (title == null) {
        return Expressions.TRUE.isTrue();
      }
      var quizAttemptTable = QQuizAttempt.quizAttempt;
      return quizAttemptTable.quiz.title.containsIgnoreCase(title);
    }

    static OrderSpecifier<LocalDateTime> orderByCreatedAtDesc() {
      var quizAttemptTable = QQuizAttempt.quizAttempt;
      return quizAttemptTable.createdAt.desc();
    }
  }
}
