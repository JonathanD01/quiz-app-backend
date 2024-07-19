package no.jonathan.quizapplication.quizquestion;

import jakarta.validation.Valid;
import no.jonathan.quizapplication.response.Response;
import no.jonathan.quizapplication.response.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/quizquestion")
public class QuizQuestionController {

  private final QuizQuestionService quizQuestionService;
  private final ResponseUtil responseUtil;

  public QuizQuestionController(
      QuizQuestionService quizQuestionService, ResponseUtil responseUtil) {
    this.quizQuestionService = quizQuestionService;
    this.responseUtil = responseUtil;
  }

  @PostMapping
  public ResponseEntity<Response<QuizQuestionDto>> createQuizQuestion(
      @Valid @RequestBody QuizQuestionCreateRequest createRequest, Authentication authentication) {
    return ResponseEntity.ok(
        responseUtil.buildSuccessResponse(
            quizQuestionService.createQuizQuestion(createRequest, authentication)));
  }

  @PatchMapping("{quizQuestionId}")
  public ResponseEntity<Response<QuizQuestionDto>> updateQuizQuestion(
      @PathVariable("quizQuestionId") Long quizQuestionId,
      @Valid @RequestBody QuizQuestionUpdateRequest updateRequest,
      Authentication authentication) {
    return ResponseEntity.ok(
        responseUtil.buildSuccessResponse(
            quizQuestionService.updateQuizQuestion(quizQuestionId, updateRequest, authentication)));
  }

  @DeleteMapping("{quizQuestionId}")
  public ResponseEntity<Response<Long>> deleteQuizQuestion(
      @PathVariable(name = "quizQuestionId") Long quizQuestionId, Authentication authentication) {
    Long deletedQuizQuestionId =
        quizQuestionService.deleteQuizQuestion(quizQuestionId, authentication);

    return ResponseEntity.ok(responseUtil.buildSuccessResponse(deletedQuizQuestionId));
  }
}
