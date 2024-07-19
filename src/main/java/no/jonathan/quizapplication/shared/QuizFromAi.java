package no.jonathan.quizapplication.shared;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record QuizFromAi(
    @JsonProperty("quizTitle") String quizTitle,
    @JsonProperty("quizDescription") String quizDescription,
    @JsonProperty("questionByAiList") List<QuizQuestionFromAi> questionByAiList) {}
