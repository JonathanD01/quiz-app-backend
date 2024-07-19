package no.jonathan.quizapplication.shared;

import java.util.function.Function;
import no.jonathan.quizapplication.quizquestion.QuizQuestion;
import no.jonathan.quizapplication.quizquestion.QuizQuestionDto;
import org.springframework.stereotype.Component;

@Component
public class QuizQuestionDtoMapper implements Function<QuizQuestion, QuizQuestionDto> {

  private final QuizAnswerOptionDtoMapper quizAnswerOptionDtoMapper;

  public QuizQuestionDtoMapper(QuizAnswerOptionDtoMapper quizAnswerOptionDtoMapper) {
    this.quizAnswerOptionDtoMapper = quizAnswerOptionDtoMapper;
  }

  @Override
  public QuizQuestionDto apply(QuizQuestion question) {
    return new QuizQuestionDto(
        question.getId(),
        question.getQuestionText(),
        question.getAnswerOptions().stream().map(quizAnswerOptionDtoMapper).toList());
  }
}
