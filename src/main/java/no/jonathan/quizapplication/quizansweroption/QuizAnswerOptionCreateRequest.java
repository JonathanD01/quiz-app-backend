package no.jonathan.quizapplication.quizansweroption;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record QuizAnswerOptionCreateRequest(
    @NotNull(message = "Please provide a quiz question id") Long quizQuestionId,
    @NotEmpty(message = "Please provide a text") String text,
    boolean correct) {}
