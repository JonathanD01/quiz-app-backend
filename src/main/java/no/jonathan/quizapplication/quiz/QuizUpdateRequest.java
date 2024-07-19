package no.jonathan.quizapplication.quiz;

import jakarta.annotation.Nullable;

public record QuizUpdateRequest(
    @Nullable String title, @Nullable String description, boolean shared) {}
