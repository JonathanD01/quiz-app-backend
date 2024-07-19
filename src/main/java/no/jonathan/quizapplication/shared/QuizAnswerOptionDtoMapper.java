package no.jonathan.quizapplication.shared;

import java.util.function.Function;
import no.jonathan.quizapplication.quizansweroption.QuizAnswerOption;
import no.jonathan.quizapplication.quizansweroption.QuizAnswerOptionDto;
import org.springframework.stereotype.Component;

@Component
public class QuizAnswerOptionDtoMapper implements Function<QuizAnswerOption, QuizAnswerOptionDto> {

  @Override
  public QuizAnswerOptionDto apply(QuizAnswerOption answerOption) {
    return new QuizAnswerOptionDto(
        answerOption.getId(), answerOption.getAnswerText(), answerOption.isCorrect());
  }
}
