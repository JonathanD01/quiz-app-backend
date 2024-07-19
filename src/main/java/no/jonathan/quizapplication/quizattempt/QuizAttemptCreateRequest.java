package no.jonathan.quizapplication.quizattempt;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import no.jonathan.quizapplication.deserializer.QuizUserAnswerDeserializer;

public record QuizAttemptCreateRequest(
    @JsonProperty("quiz_link") @NotNull(message = "Quiz link cannot be null") UUID quizLink,
    @JsonProperty("start_time") @NotNull(message = "Start time cannot be null")
        LocalDateTime startTime,
    @JsonProperty("end_time") @NotNull(message = "End time cannot be null") LocalDateTime endTime,
    @JsonProperty("user_answers")
        @JsonDeserialize(using = QuizUserAnswerDeserializer.class)
        @NotEmpty(message = "Quiz user answers cannot be empty")
        Set<QuizUserAnswer> userAnswers) {}
