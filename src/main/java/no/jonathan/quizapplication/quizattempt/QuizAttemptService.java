package no.jonathan.quizapplication.quizattempt;

import jakarta.transaction.Transactional;
import java.util.UUID;
import no.jonathan.quizapplication.exception.QuizAttemptNotFoundException;
import no.jonathan.quizapplication.quiz.Quiz;
import no.jonathan.quizapplication.quiz.QuizModuleApi;
import no.jonathan.quizapplication.shared.QuizAttemptDtoMapper;
import no.jonathan.quizapplication.user.User;
import no.jonathan.quizapplication.utils.AccessUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Transactional
@Service
public class QuizAttemptService {

  private final QuizModuleApi quizModuleApi;
  private final QuizAttemptRepository quizAttemptRepository;
  private final QuizAttemptRepositoryCustom quizAttemptRepositoryCustom;
  private final QuizAttemptDtoMapper quizAttemptDtoMapper;

  public QuizAttemptService(
      QuizModuleApi quizModuleApi,
      QuizAttemptRepository quizAttemptRepository,
      QuizAttemptRepositoryCustom quizAttemptRepositoryCustom,
      QuizAttemptDtoMapper quizAttemptDtoMapper) {
    this.quizModuleApi = quizModuleApi;
    this.quizAttemptRepository = quizAttemptRepository;
    this.quizAttemptRepositoryCustom = quizAttemptRepositoryCustom;
    this.quizAttemptDtoMapper = quizAttemptDtoMapper;
  }

  public Page<QuizAttemptCoreProjection> getQuizAttemptsByUser(
      String title, Authentication authentication, Pageable pageable) {
    return quizAttemptRepositoryCustom.findAll(title, authentication.getName(), pageable);
  }

  public QuizAttemptDto getQuizAttemptByLink(UUID quizAttemptLink, Authentication authentication) {
    QuizAttemptDto quizAttempt =
        quizAttemptRepositoryCustom
            .findByLink(quizAttemptLink)
            .orElseThrow(() -> new QuizAttemptNotFoundException(quizAttemptLink));

    AccessUtil.verifyUserHasAccessToEntity(quizAttempt.createdByEmail(), authentication);

    return quizAttempt;
  }

  public QuizAttemptDto createQuizAttemptForUser(
      QuizAttemptCreateRequest createRequest, Authentication authentication) {
    User user = authentication != null ? ((User) authentication.getPrincipal()) : null;

    Quiz quiz = quizModuleApi.findByLink(createRequest.quizLink());

    if (!quiz.isShared()) {
      AccessUtil.verifyUserHasAccessToEntity(quiz.getCreatedByEmail(), authentication);
    }

    QuizAttempt newQuizAttempt = QuizAttempt.fromCreateRequest(user, quiz, createRequest);
    QuizAttempt savedQuizAttempt = quizAttemptRepository.save(newQuizAttempt);
    return quizAttemptDtoMapper.apply(savedQuizAttempt);
  }
}
