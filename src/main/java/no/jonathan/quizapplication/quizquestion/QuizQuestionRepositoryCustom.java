package no.jonathan.quizapplication.quizquestion;

import com.querydsl.core.types.dsl.BooleanExpression;
import java.util.Optional;

interface QuizQuestionRepositoryCustom {

  Optional<QuizQuestion> findById(Long id);

  void delete(QuizQuestion quizQuestion);

  interface Specifications {

    static BooleanExpression hasId(Long id) {
      var quizQuestionTable = QQuizQuestion.quizQuestion;
      return quizQuestionTable.id.eq(id);
    }
  }
}
