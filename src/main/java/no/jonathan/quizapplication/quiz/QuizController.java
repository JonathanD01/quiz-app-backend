package no.jonathan.quizapplication.quiz;

import com.fasterxml.jackson.core.JsonProcessingException;
import jakarta.validation.Valid;
import java.io.IOException;
import java.util.UUID;
import no.jonathan.quizapplication.response.Response;
import no.jonathan.quizapplication.response.ResponseUtil;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("api/v1/quizzes")
public class QuizController {

  private final QuizService quizService;
  private final ResponseUtil responseUtil;
  private final SimpMessagingTemplate simpMessagingTemplate;

  public QuizController(
      QuizService quizService,
      ResponseUtil responseUtil,
      SimpMessagingTemplate simpMessagingTemplate) {
    this.quizService = quizService;
    this.responseUtil = responseUtil;
    this.simpMessagingTemplate = simpMessagingTemplate;
  }

  @GetMapping
  public ResponseEntity<Response<PagedModel<QuizCoreProjection>>> getQuizzesByUser(
      @RequestParam(value = "title", required = false) String title,
      Pageable pageable,
      Authentication authentication) {
    Page<QuizCoreProjection> quizzes =
        quizService.getQuizzesByUser(title, authentication.getName(), pageable);

    if (quizzes.isEmpty()) {
      return ResponseEntity.noContent().build();
    }

    return ResponseEntity.ok(responseUtil.buildSuccessResponse(new PagedModel<>(quizzes)));
  }

  @GetMapping("{quizId}")
  public ResponseEntity<Response<QuizDto>> getQuizById(
      @PathVariable("quizId") Long quizId, Authentication authentication) {
    return ResponseEntity.ok(
        responseUtil.buildSuccessResponse(quizService.getQuizById(quizId, authentication)));
  }

  @GetMapping("link/{quizLink}")
  public ResponseEntity<Response<QuizDto>> getQuizByLink(
      @PathVariable("quizLink") UUID quizLink, Authentication authentication) {
    return ResponseEntity.ok(
        responseUtil.buildSuccessResponse(quizService.getQuizByLink(quizLink, authentication)));
  }

  @PostMapping(value = "ai", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public Long createQuizWithAI(
      @RequestParam("file") MultipartFile file,
      @RequestParam("language") String language,
      Authentication authentication)
      throws IOException {

    var result =
        quizService.buildNewQuizWithAI(
            authentication, file.getOriginalFilename(), language, file.getBytes());

    simpMessagingTemplate.convertAndSendToUser(authentication.getName(), "topic/quiz", result);

    return result;
  }

  @PatchMapping("{quizId}")
  public ResponseEntity<Response<QuizDto>> updateQuiz(
      @PathVariable("quizId") Long quizId,
      @Valid @RequestBody QuizUpdateRequest updateRequest,
      Authentication authentication) {
    return ResponseEntity.ok(
        responseUtil.buildSuccessResponse(
            quizService.updateQuiz(quizId, updateRequest, authentication)));
  }

  @DeleteMapping("{quizId}")
  public ResponseEntity<Response<Long>> deleteQuiz(
      @PathVariable(name = "quizId") Long quizId, Authentication authentication) {
    Long deletedQuizId = quizService.deleteQuiz(quizId, authentication);

    return ResponseEntity.ok(responseUtil.buildSuccessResponse(deletedQuizId));
  }
}
