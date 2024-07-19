package no.jonathan.quizapplication.shared;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record QuizQuestionFromAi(
    @JsonProperty("questionText") String questionText,
    @JsonProperty("quizAnswerOptionFromAis")
        List<QuizAnswerOptionFromAi> quizAnswerOptionFromAis) {}
