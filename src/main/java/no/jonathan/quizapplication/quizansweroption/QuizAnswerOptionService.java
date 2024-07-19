package no.jonathan.quizapplication.quizansweroption;

import no.jonathan.quizapplication.exception.QuizAnswerOptionNotFoundException;
import no.jonathan.quizapplication.quizquestion.QuizQuestion;
import no.jonathan.quizapplication.quizquestion.QuizQuestionModuleApi;
import no.jonathan.quizapplication.shared.QuizAnswerOptionDtoMapper;
import no.jonathan.quizapplication.utils.AccessUtil;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class QuizAnswerOptionService {

  private final QuizQuestionModuleApi quizQuestionModuleApi;
  private final QuizAnswerOptionRepository quizAnswerOptionRepository;
  private final QuizAnswerOptionDtoMapper quizAnswerOptionDtoMapper;

  public QuizAnswerOptionService(
      QuizQuestionModuleApi quizQuestionModuleApi,
      QuizAnswerOptionRepository quizAnswerOptionRepository,
      QuizAnswerOptionDtoMapper quizAnswerOptionDtoMapper) {
    this.quizQuestionModuleApi = quizQuestionModuleApi;
    this.quizAnswerOptionRepository = quizAnswerOptionRepository;
    this.quizAnswerOptionDtoMapper = quizAnswerOptionDtoMapper;
  }

  public QuizAnswerOptionDto createQuizAnswerOption(
      QuizAnswerOptionCreateRequest createRequest, Authentication authentication) {
    QuizQuestion quizQuestion = quizQuestionModuleApi.findById(createRequest.quizQuestionId());

    AccessUtil.verifyUserHasAccessToEntity(quizQuestion.getCreatedByEmail(), authentication);

    QuizAnswerOption createdQuizAnswerOption =
        QuizAnswerOption.fromCreateRequest(quizQuestion, createRequest);

    var savedQuizAnswerOption = quizAnswerOptionRepository.save(createdQuizAnswerOption);

    return quizAnswerOptionDtoMapper.apply(savedQuizAnswerOption);
  }

  public QuizAnswerOptionDto updateQuizAnswerOption(
      Long quizAnswerOptionId,
      QuizAnswerOptionUpdateRequest updateRequest,
      Authentication authentication) {
    QuizAnswerOption quizAnswerOption =
        quizAnswerOptionRepository
            .findById(quizAnswerOptionId)
            .orElseThrow(() -> new QuizAnswerOptionNotFoundException(quizAnswerOptionId));

    AccessUtil.verifyUserHasAccessToEntity(quizAnswerOption.getCreatedByEmail(), authentication);

    quizAnswerOption.updateFromRequest(updateRequest);

    QuizAnswerOption updatedQuizAnswerOption = quizAnswerOptionRepository.save(quizAnswerOption);

    return quizAnswerOptionDtoMapper.apply(updatedQuizAnswerOption);
  }

  public Long deleteQuizAnswerOption(Long quizAnswerOptionId, Authentication authentication) {
    QuizAnswerOption quizAnswerOption =
        quizAnswerOptionRepository
            .findById(quizAnswerOptionId)
            .orElseThrow(() -> new QuizAnswerOptionNotFoundException(quizAnswerOptionId));

    AccessUtil.verifyUserHasAccessToEntity(quizAnswerOption.getCreatedByEmail(), authentication);

    quizAnswerOptionRepository.delete(quizAnswerOption);

    return quizAnswerOptionId;
  }
}
