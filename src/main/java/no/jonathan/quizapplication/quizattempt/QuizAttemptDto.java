package no.jonathan.quizapplication.quizattempt;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import no.jonathan.quizapplication.quiz.QuizDto;
import no.jonathan.quizapplication.user.UserDto;

public record QuizAttemptDto(
    Long id,
    UUID link,
    QuizDto quizDto,
    UserDto userDto,
    LocalDateTime startTime,
    LocalDateTime endTime,
    String createdByEmail,
    int maxScore,
    int score,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    Set<QuizUserAnswer> userAnswers) {}
