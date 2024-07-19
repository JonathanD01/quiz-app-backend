package no.jonathan.quizapplication.quizattempt;

import jakarta.persistence.*;
import java.util.Set;

@Embeddable
public record QuizUserAnswer(Long quizQuestionId, Set<Long> quizAnswerOptionIds) {}
