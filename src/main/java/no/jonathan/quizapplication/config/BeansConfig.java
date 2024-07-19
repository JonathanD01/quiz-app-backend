package no.jonathan.quizapplication.config;

import no.jonathan.quizapplication.response.ResponseUtil;
import no.jonathan.quizapplication.shared.*;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class BeansConfig {

  @Value("${spring.ai.mock}")
  private boolean mockQuizGeneratorBot;

  @Bean
  public ResponseUtil responseUtil() {
    return new ResponseUtil();
  }

  @Bean
  public UserDtoMapper userDtoMapper() {
    return new UserDtoMapper();
  }

  @Bean
  public QuizDtoMapper quizDtoMapper() {
    return new QuizDtoMapper(userDtoMapper(), quizQuestionDtoMapper());
  }

  @Bean
  public QuizQuestionDtoMapper quizQuestionDtoMapper() {
    return new QuizQuestionDtoMapper(quizAnswerOptionDtoMapper());
  }

  @Bean
  public QuizAnswerOptionDtoMapper quizAnswerOptionDtoMapper() {
    return new QuizAnswerOptionDtoMapper();
  }

  @Bean
  public QuizAttemptDtoMapper quizAttemptDtoMapper() {
    return new QuizAttemptDtoMapper(quizDtoMapper(), userDtoMapper());
  }

  @Bean(name = "quizGeneratorBot")
  public IQuizGeneratorBot quizGeneratorBot(ChatClient.Builder builder) {
    if (mockQuizGeneratorBot) {
      return new FakeQuizGeneratorBot();
    }

    return new QuizGeneratorBot(builder);
  }
}
