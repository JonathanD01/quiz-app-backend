package no.jonathan.quizapplication.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import no.jonathan.quizapplication.quizattempt.QuizUserAnswer;

public class QuizUserAnswerDeserializer extends JsonDeserializer<Set<QuizUserAnswer>> {

  @Override
  public Set<QuizUserAnswer> deserialize(JsonParser jsonParser, DeserializationContext context)
      throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    JsonNode rootNode = jsonParser.getCodec().readTree(jsonParser);
    Set<QuizUserAnswer> answers = new HashSet<>();

    Iterator<JsonNode> jsonNodeElements = rootNode.elements();
    while (jsonNodeElements.hasNext()) {
      var jsonNodeElement = jsonNodeElements.next();
      Long quizQuestionId = mapper.convertValue(jsonNodeElement.get("quizQuestionId"), Long.class);
      Set<Long> quizAnswerOptionIds = mapper.convertValue(jsonNodeElement.get("quizAnswerOptionIds"), new TypeReference<>() {});

      QuizUserAnswer answer = new QuizUserAnswer(quizQuestionId, quizAnswerOptionIds);
      answers.add(answer);
    }
    return answers;
  }
}
