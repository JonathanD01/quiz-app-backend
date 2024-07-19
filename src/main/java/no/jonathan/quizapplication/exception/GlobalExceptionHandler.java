package no.jonathan.quizapplication.exception;

import no.jonathan.quizapplication.response.Response;
import no.jonathan.quizapplication.response.ResponseErrorDto;
import no.jonathan.quizapplication.response.ResponseUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.List;

@ControllerAdvice
public class GlobalExceptionHandler {

  private final ResponseUtil responseUtil;

  public GlobalExceptionHandler(ResponseUtil responseUtil) {
    this.responseUtil = responseUtil;
  }

  @ExceptionHandler(value = EmailAlreadyTakenException.class)
  public ResponseEntity<Response<ResponseErrorDto>> handleEmailAlreadyTakenException(
      EmailAlreadyTakenException exception) {
    ResponseErrorDto errorDTO = responseUtil.createAPIErrorDTO(exception);
    Response<ResponseErrorDto> response = responseUtil.createAPIResponse(errorDTO);
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(value = QuizNotFoundException.class)
  public ResponseEntity<Response<ResponseErrorDto>> handleQuizNotFoundException(
      QuizNotFoundException exception) {
    ResponseErrorDto errorDTO = responseUtil.createAPIErrorDTO(exception);
    Response<ResponseErrorDto> response = responseUtil.createAPIResponse(errorDTO);
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(value = NoPermissionException.class)
  public ResponseEntity<Response<ResponseErrorDto>> handleNoPermissionException(
      NoPermissionException exception) {
    ResponseErrorDto errorDTO = responseUtil.createAPIErrorDTO(exception);
    Response<ResponseErrorDto> response = responseUtil.createAPIResponse(errorDTO);
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(value = QuizAttemptNotFoundException.class)
  public ResponseEntity<Response<ResponseErrorDto>> handleQuizQuizAttemptNotFoundException(
      QuizAttemptNotFoundException exception) {
    ResponseErrorDto errorDTO = responseUtil.createAPIErrorDTO(exception);
    Response<ResponseErrorDto> response = responseUtil.createAPIResponse(errorDTO);
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(value = NotSupportedFileException.class)
  public ResponseEntity<Response<ResponseErrorDto>> handleNotSupportedFileException(
      NotSupportedFileException exception) {
    ResponseErrorDto errorDTO = responseUtil.createAPIErrorDTO(exception);
    Response<ResponseErrorDto> response = responseUtil.createAPIResponse(errorDTO);
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(value = MaxUploadSizeExceededException.class)
  public ResponseEntity<Response<ResponseErrorDto>> handleMaxUploadSizeExceededException(
      MaxUploadSizeExceededException exception) {
    ResponseErrorDto errorDTO = responseUtil.createAPIErrorDTO(exception);
    Response<ResponseErrorDto> response = responseUtil.createAPIResponse(errorDTO);
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(value = SimpleRateLimitException.class)
  public ResponseEntity<Response<ResponseErrorDto>> handleSimpleRateLimitException(
          SimpleRateLimitException exception) {
    ResponseErrorDto errorDTO = responseUtil.createAPIErrorDTO(exception);
    Response<ResponseErrorDto> response = responseUtil.createAPIResponse(errorDTO);
    return ResponseEntity.badRequest().body(response);
  }

  @ExceptionHandler(value = BindException.class)
  public ResponseEntity<Response<ResponseErrorDto>> handleBindException(BindException exception) {
    List<ResponseErrorDto> errorDTOList =
        responseUtil.createAPIErrorDTOsForBindException(exception);
    Response<ResponseErrorDto> response = responseUtil.createAPIResponse(errorDTOList);
    return ResponseEntity.badRequest().body(response);
  }
}
