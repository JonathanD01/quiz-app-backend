package no.jonathan.quizapplication.shared;

import java.util.List;
import java.util.StringJoiner;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.Resource;

public class QuizGeneratorBot implements IQuizGeneratorBot {

  private final String template =
      """
          You are an esteemed professor renowned for creating comprehensive and educational quizzes.

          Your task is to generate a quiz based on the provided information that ensures thorough coverage of all topics mentioned. The quiz should:

          - Address every significant topic in the provided information sequentially, ensuring no topic is omitted.
          - Create questions that cover all aspects and nuances of the topics, ensuring a deep and comprehensive understanding.
          - If the information consists of questions, take all the questions in the order provided and create answers based on your own knowledge.
          - Ensure the questions are unique and not similar.
          - All questions should be written in singularity. This means that a question should not have the word "each" in it, because a question can have max two or three correct answers.
          - Include additional questions if necessary to help students gain a deeper understanding of the topics, but ensure all provided questions are addressed first.
          - Each question should offer at least four different answer options. The answers should be designed in a way that requires students to carefully read and think about them.
          - There cannot be more than two correct answers per question.
          - Use the provided information to create accurate questions and answers. If the information is insufficient, you are allowed to create your own accurate answers.
          - The language of the quiz should be {language}.
          - If a language is specified, the student is likely an exchange student. Therefore, the title and description for the quiz should also be generated in that language. In fact, all text should be in that language.

          INFORMATION:
          {information}
          """;
  private final ChatClient chatClient;

  @Value("${ai.max-length}")
  private int maxLength;

  public QuizGeneratorBot(ChatClient.Builder builder) {
    this.chatClient = builder.build();
  }

  private static String collectDocumentsContent(List<Document> documents, int maxLength) {
    StringJoiner joiner = new StringJoiner(System.lineSeparator());
    int currentLength = 0;

    for (Document document : documents) {
      String content = document.getContent().replaceAll("\\s+", " ");
      int length = content.length();

      if (currentLength + length > maxLength) {
        int remainingLength = maxLength - currentLength;
        joiner.add(content.substring(0, remainingLength));
        break;
      } else {
        joiner.add(content);
        currentLength += length;
      }
    }

    return joiner.toString();
  }

  @Override
  public QuizFromAi getQuizAiFromChatBot(String language, Resource resource) {
    var documents = textReader(resource).get();

    String subStringedInformation = collectDocumentsContent(documents, maxLength);

    try {
      return chatClient
          .prompt()
          .user(
              u ->
                  u.text(template)
                      .param("information", subStringedInformation)
                      .param("language", language))
          .call()
          .entity(new ParameterizedTypeReference<>() {});
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private TextReader textReader(Resource resource) {
    return new TextReader(resource);
  }
}
