package no.jonathan.quizapplication.quizquestion;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record QuizQuestionCreateRequest(
    @NotNull(message = "Please provide a quiz id") Long quizId,
    @NotEmpty(message = "Please provide a text for the question") String text) {}
