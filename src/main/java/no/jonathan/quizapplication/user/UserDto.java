package no.jonathan.quizapplication.user;

import java.time.LocalDateTime;

public record UserDto(
    Long id,
    String firstname,
    String lastname,
    String email,
    boolean accountLocked,
    boolean enabled,
    LocalDateTime createdAt,
    LocalDateTime updatedAt,
    String userRole) {}
