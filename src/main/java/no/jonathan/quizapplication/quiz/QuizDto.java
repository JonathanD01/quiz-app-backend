package no.jonathan.quizapplication.quiz;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import no.jonathan.quizapplication.quizquestion.QuizQuestionDto;
import no.jonathan.quizapplication.user.UserDto;

public record QuizDto(
    Long id,
    UUID link,
    Status status,
    UserDto creator,
    String title,
    String description,
    boolean shared,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String createdByEmail,
    List<QuizQuestionDto> questions) {}
