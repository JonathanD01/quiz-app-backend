package no.jonathan.quizapplication.quizattempt;

import jakarta.validation.Valid;
import java.util.UUID;
import no.jonathan.quizapplication.response.Response;
import no.jonathan.quizapplication.response.ResponseUtil;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/quizattempts")
public class QuizAttemptController {

  private final QuizAttemptService quizAttemptService;
  private final ResponseUtil responseUtil;

  public QuizAttemptController(QuizAttemptService quizAttemptService, ResponseUtil responseUtil) {
    this.quizAttemptService = quizAttemptService;
    this.responseUtil = responseUtil;
  }

  @GetMapping
  public ResponseEntity<Response<PagedModel<QuizAttemptCoreProjection>>> getQuizAttemptsByUser(
      @RequestParam(value = "title", required = false) String title,
      Pageable pageable,
      Authentication authentication) {
    Page<QuizAttemptCoreProjection> quizAttempts =
        quizAttemptService.getQuizAttemptsByUser(title, authentication, pageable);

    if (quizAttempts.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(responseUtil.buildSuccessResponse(new PagedModel<>(quizAttempts)));
  }

  @GetMapping("{quizAttemptLink}")
  public ResponseEntity<Response<QuizAttemptDto>> getQuizAttemptByUser(
      @PathVariable("quizAttemptLink") UUID quizAttemptLink, Authentication authentication) {
    QuizAttemptDto quizAttemptDto =
        quizAttemptService.getQuizAttemptByLink(quizAttemptLink, authentication);

    return ResponseEntity.ok(responseUtil.buildSuccessResponse(quizAttemptDto));
  }

  @PostMapping
  public ResponseEntity<Response<QuizAttemptDto>> createQuizAttemptForUser(
      @Valid @RequestBody QuizAttemptCreateRequest createRequest, Authentication authentication) {
    QuizAttemptDto quizAttemptDto =
        quizAttemptService.createQuizAttemptForUser(createRequest, authentication);

    return ResponseEntity.ok(responseUtil.buildSuccessResponse(quizAttemptDto));
  }
}
