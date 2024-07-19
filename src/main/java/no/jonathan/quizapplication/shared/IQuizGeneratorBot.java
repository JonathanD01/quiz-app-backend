package no.jonathan.quizapplication.shared;

import org.springframework.core.io.Resource;

public interface IQuizGeneratorBot {

    QuizFromAi getQuizAiFromChatBot(String language, Resource resource);

}
