package no.jonathan.quizapplication.quizansweroption;

import jakarta.validation.Valid;
import no.jonathan.quizapplication.response.Response;
import no.jonathan.quizapplication.response.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/quizansweroption")
public class QuizAnswerOptionController {

  private final QuizAnswerOptionService quizAnswerOptionService;
  private final ResponseUtil responseUtil;

  public QuizAnswerOptionController(
      QuizAnswerOptionService quizAnswerOptionService, ResponseUtil responseUtil) {
    this.quizAnswerOptionService = quizAnswerOptionService;
    this.responseUtil = responseUtil;
  }

  @PostMapping
  public ResponseEntity<Response<QuizAnswerOptionDto>> createQuizAnswerOption(
      @Valid @RequestBody QuizAnswerOptionCreateRequest createRequest,
      Authentication authentication) {
    return ResponseEntity.ok(
        responseUtil.buildSuccessResponse(
            quizAnswerOptionService.createQuizAnswerOption(createRequest, authentication)));
  }

  @PatchMapping("{quizAnswerOptionId}")
  public ResponseEntity<Response<QuizAnswerOptionDto>> updateQuizAnswerOption(
      @PathVariable("quizAnswerOptionId") Long quizAnswerOptionId,
      @Valid @RequestBody QuizAnswerOptionUpdateRequest updateRequest,
      Authentication authentication) {
    return ResponseEntity.ok(
        responseUtil.buildSuccessResponse(
            quizAnswerOptionService.updateQuizAnswerOption(
                quizAnswerOptionId, updateRequest, authentication)));
  }

  @DeleteMapping("{quizAnswerOptionId}")
  public ResponseEntity<Response<Long>> deleteQuizAnswerOption(
      @PathVariable(name = "quizAnswerOptionId") Long quizAnswerOptionId,
      Authentication authentication) {
    Long deletedQuizAnswerOptionId =
        quizAnswerOptionService.deleteQuizAnswerOption(quizAnswerOptionId, authentication);

    return ResponseEntity.ok(responseUtil.buildSuccessResponse(deletedQuizAnswerOptionId));
  }
}
