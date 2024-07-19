package no.jonathan.quizapplication.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.ZonedDateTime;
import org.springframework.http.HttpStatus;

@JsonInclude(JsonInclude.Include.USE_DEFAULTS)
public record ResponseErrorDto(
    String message, @JsonProperty("http_status") HttpStatus httpStatus, ZonedDateTime timestamp) {}
