package no.jonathan.quizapplication.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public record Response<T>(
    @JsonProperty("response") ResponseType type,
    @JsonProperty("errors") List<ResponseErrorDto> errors,
    @JsonProperty("result") T result) {}
