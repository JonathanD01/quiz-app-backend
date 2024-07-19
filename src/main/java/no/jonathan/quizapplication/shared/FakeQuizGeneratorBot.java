package no.jonathan.quizapplication.shared;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class FakeQuizGeneratorBot implements IQuizGeneratorBot {

  @Override
  public QuizFromAi getQuizAiFromChatBot(String language, Resource resource) {
    ClassPathResource classPathResource = new ClassPathResource("example_quiz_ai.json");

    ObjectMapper objectMapper = new ObjectMapper();

    try {
      return objectMapper.readValue(classPathResource.getFile(), QuizFromAi.class);
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
