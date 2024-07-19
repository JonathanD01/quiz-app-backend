package no.jonathan.quizapplication.quizquestion;

import jakarta.validation.constraints.NotEmpty;

public record QuizQuestionUpdateRequest(
    @NotEmpty(message = "Please provide a question") String question) {}
