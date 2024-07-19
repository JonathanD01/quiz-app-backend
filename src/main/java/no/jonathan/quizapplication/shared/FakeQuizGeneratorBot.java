package no.jonathan.quizapplication.shared;

import java.io.IOException;
import java.util.List;
import java.util.StringJoiner;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

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
