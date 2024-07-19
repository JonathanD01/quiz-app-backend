package no.jonathan.quizapplication.shared;

import com.fasterxml.jackson.annotation.JsonProperty;

public record QuizAnswerOptionFromAi(
    @JsonProperty("answerOptionText") String answerOptionText,
    @JsonProperty("correct") boolean correct) {}
