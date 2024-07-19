package no.jonathan.quizapplication.quizansweroption;

import jakarta.annotation.Nullable;

public record QuizAnswerOptionUpdateRequest(@Nullable String text, boolean correct) {}
