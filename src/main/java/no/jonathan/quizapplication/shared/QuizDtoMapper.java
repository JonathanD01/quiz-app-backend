package no.jonathan.quizapplication.shared;

import java.util.function.Function;
import no.jonathan.quizapplication.quiz.Quiz;
import no.jonathan.quizapplication.quiz.QuizDto;
import org.springframework.stereotype.Component;

@Component
public class QuizDtoMapper implements Function<Quiz, QuizDto> {

  private final UserDtoMapper userDtoMapper;
  private final QuizQuestionDtoMapper quizQuestionDtoMapper;

  public QuizDtoMapper(UserDtoMapper userDtoMapper, QuizQuestionDtoMapper quizQuestionDtoMapper) {
    this.userDtoMapper = userDtoMapper;
    this.quizQuestionDtoMapper = quizQuestionDtoMapper;
  }

  @Override
  public QuizDto apply(Quiz quiz) {
    return new QuizDto(
        quiz.getId(),
        quiz.getLink(),
        quiz.getStatus(),
        userDtoMapper.apply(quiz.getCreator()),
        quiz.getTitle(),
        quiz.getDescription(),
        quiz.isShared(),
        quiz.getCreatedAt(),
        quiz.getUpdatedAt(),
        quiz.getCreatedByEmail(),
        quiz.getQuestions().stream().map(quizQuestionDtoMapper).toList());
  }
}
