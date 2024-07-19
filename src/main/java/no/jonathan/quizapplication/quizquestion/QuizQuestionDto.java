package no.jonathan.quizapplication.quizquestion;

import java.util.List;
import no.jonathan.quizapplication.quizansweroption.QuizAnswerOptionDto;

public record QuizQuestionDto(Long id, String text, List<QuizAnswerOptionDto> quizAnswerOptions) {}
