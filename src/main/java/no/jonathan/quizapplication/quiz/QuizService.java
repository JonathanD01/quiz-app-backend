package no.jonathan.quizapplication.quiz;

import io.github.resilience4j.ratelimiter.RequestNotPermitted;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.transaction.Transactional;
import java.util.UUID;
import no.jonathan.quizapplication.exception.NotSupportedFileException;
import no.jonathan.quizapplication.exception.QuizNotFoundException;
import no.jonathan.quizapplication.exception.SimpleRateLimitException;
import no.jonathan.quizapplication.shared.QuizDtoMapper;
import no.jonathan.quizapplication.user.User;
import no.jonathan.quizapplication.utils.AccessUtil;
import no.jonathan.quizapplication.utils.QuizUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class QuizService {

  private static final String SERVICE_NAME = "quiz-ai-service";

  private final QuizRepository quizRepository;
  private final QuizRepositoryCustom quizRepositoryCustom;
  private final QuizUtil quizUtil;
  private final QuizDtoMapper quizDtoMapper;

  public QuizService(
      QuizRepository quizRepository,
      QuizRepositoryCustom quizRepositoryCustom,
      QuizUtil quizUtil,
      QuizDtoMapper quizDtoMapper) {
    this.quizRepository = quizRepository;
    this.quizRepositoryCustom = quizRepositoryCustom;
    this.quizUtil = quizUtil;
    this.quizDtoMapper = quizDtoMapper;
  }

  public Page<QuizCoreProjection> getQuizzesByUser(String title, String email, Pageable pageable) {

    return quizRepositoryCustom.findAll(title, email, pageable);
  }

  @Transactional
  public QuizDto getQuizById(Long quizId, Authentication authentication) {
    QuizDto quizDto =
        quizRepositoryCustom
            .findDtoById(quizId)
            .orElseThrow(() -> new QuizNotFoundException(quizId));

    AccessUtil.verifyUserHasAccessToEntity(quizDto.createdByEmail(), authentication);

    return quizDto;
  }

  public QuizDto getQuizByLink(UUID quizLink, Authentication authentication) {
    QuizDto quizDto =
        quizRepositoryCustom
            .findDtoByLink(quizLink)
            .orElseThrow(() -> new QuizNotFoundException(quizLink));

    if (!quizDto.shared()) {
      AccessUtil.verifyUserHasAccessToEntity(quizDto.createdByEmail(), authentication);
    }

    return quizDto;
  }

  @RateLimiter(name = SERVICE_NAME, fallbackMethod = "buildNewQuizWithAIFallbackMethod")
  public Long buildNewQuizWithAI(
      Authentication authentication, String fileName, String language, byte[] bytes) {
    boolean isFileNotTxt = !StringUtils.endsWithIgnoreCase(fileName, "txt");
    if (isFileNotTxt) {
      throw new NotSupportedFileException();
    }

    User quizCreator = ((User) authentication.getPrincipal());
    Quiz quiz = Quiz.createUnfinishedQuiz(quizCreator);
    var newQuiz = quizRepository.save(quiz);

    quizUtil.continueAndFinishQuizCreation(newQuiz, quizCreator.getName(), language, bytes);

    return newQuiz.getId();
  }

  public Long deleteQuiz(Long quizId, Authentication authentication) {
    Quiz quizToDelete =
        quizRepositoryCustom.findById(quizId).orElseThrow(() -> new QuizNotFoundException(quizId));

    AccessUtil.verifyUserHasAccessToEntity(quizToDelete.getCreatedByEmail(), authentication);

    quizRepositoryCustom.delete(quizToDelete);

    return quizToDelete.getId();
  }

  public QuizDto updateQuiz(
      Long quizId, QuizUpdateRequest updateRequest, Authentication authentication) {
    Quiz quiz =
        quizRepositoryCustom.findById(quizId).orElseThrow(() -> new QuizNotFoundException(quizId));

    AccessUtil.verifyUserHasAccessToEntity(quiz.getCreatedByEmail(), authentication);

    quiz.updateFromRequest(updateRequest);

    Quiz updatedQuiz = quizRepository.save(quiz);

    return quizDtoMapper.apply(updatedQuiz);
  }

  private Long buildNewQuizWithAIFallbackMethod(RequestNotPermitted requestNotPermitted) {
    throw new SimpleRateLimitException();
  }
}
