package no.jonathan.quizapplication.quizquestion;

import no.jonathan.quizapplication.exception.QuizQuestionNotFoundException;
import no.jonathan.quizapplication.quiz.Quiz;
import no.jonathan.quizapplication.quiz.QuizModuleApi;
import no.jonathan.quizapplication.shared.QuizQuestionDtoMapper;
import no.jonathan.quizapplication.utils.AccessUtil;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class QuizQuestionService {

  private final QuizModuleApi quizModuleApi;
  private final QuizQuestionRepository quizQuestionRepository;
  private final QuizQuestionRepositoryCustom quizQuestionRepositoryCustom;
  private final QuizQuestionDtoMapper quizQuestionDtoMapper;

  public QuizQuestionService(
      QuizModuleApi quizModuleApi,
      QuizQuestionRepository quizQuestionRepository,
      QuizQuestionRepositoryCustom quizQuestionRepositoryCustom,
      QuizQuestionDtoMapper quizQuestionDtoMapper) {
    this.quizModuleApi = quizModuleApi;
    this.quizQuestionRepository = quizQuestionRepository;
    this.quizQuestionRepositoryCustom = quizQuestionRepositoryCustom;
    this.quizQuestionDtoMapper = quizQuestionDtoMapper;
  }

  public QuizQuestionDto createQuizQuestion(
      QuizQuestionCreateRequest createRequest, Authentication authentication) {
    Long quizId = createRequest.quizId();

    Quiz quiz = quizModuleApi.findById(quizId);

    AccessUtil.verifyUserHasAccessToEntity(quiz.getCreatedByEmail(), authentication);

    QuizQuestion createdQuizQuestion = QuizQuestion.fromCreateRequest(quiz, createRequest);

    var savedQuizQuestion = quizQuestionRepository.save(createdQuizQuestion);

    return quizQuestionDtoMapper.apply(savedQuizQuestion);
  }

  public QuizQuestionDto updateQuizQuestion(
      Long quizQuestionId, QuizQuestionUpdateRequest updateRequest, Authentication authentication) {
    QuizQuestion quizQuestion =
        quizQuestionRepositoryCustom
            .findById(quizQuestionId)
            .orElseThrow(() -> new QuizQuestionNotFoundException(quizQuestionId));

    AccessUtil.verifyUserHasAccessToEntity(quizQuestion.getCreatedByEmail(), authentication);

    quizQuestion.updateFromRequest(updateRequest);

    QuizQuestion updatedQuizQuestion = quizQuestionRepository.save(quizQuestion);

    return quizQuestionDtoMapper.apply(updatedQuizQuestion);
  }

  public Long deleteQuizQuestion(Long quizQuestionId, Authentication authentication) {
    QuizQuestion quizQuestion =
        quizQuestionRepositoryCustom
            .findById(quizQuestionId)
            .orElseThrow(() -> new QuizQuestionNotFoundException(quizQuestionId));

    AccessUtil.verifyUserHasAccessToEntity(quizQuestion.getCreatedByEmail(), authentication);

    quizQuestionRepositoryCustom.delete(quizQuestion);

    return quizQuestionId;
  }
}
