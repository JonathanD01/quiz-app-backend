package no.jonathan.quizapplication.shared;

import java.util.function.Function;
import no.jonathan.quizapplication.quizattempt.QuizAttempt;
import no.jonathan.quizapplication.quizattempt.QuizAttemptDto;

public class QuizAttemptDtoMapper implements Function<QuizAttempt, QuizAttemptDto> {

  private final QuizDtoMapper quizDtoMapper;
  private final UserDtoMapper userDtoMapper;

  public QuizAttemptDtoMapper(QuizDtoMapper quizDtoMapper, UserDtoMapper userDtoMapper) {
    this.quizDtoMapper = quizDtoMapper;
    this.userDtoMapper = userDtoMapper;
  }

  @Override
  public QuizAttemptDto apply(QuizAttempt quizAttempt) {
    var nullableUserDto =
        quizAttempt.getAttemptBy() != null ? userDtoMapper.apply(quizAttempt.getAttemptBy()) : null;
    return new QuizAttemptDto(
        quizAttempt.getId(),
        quizAttempt.getLink(),
        quizDtoMapper.apply(quizAttempt.getQuiz()),
        nullableUserDto,
        quizAttempt.getStartTime(),
        quizAttempt.getEndTime(),
        quizAttempt.getCreatedByEmail(),
        quizAttempt.getMaxScore(),
        quizAttempt.getScore(),
        quizAttempt.getCreatedAt(),
        quizAttempt.getUpdatedAt(),
        quizAttempt.getUserAnswers());
  }
}
