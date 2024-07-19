package no.jonathan.quizapplication.quizansweroption;

import jakarta.persistence.*;
import no.jonathan.quizapplication.quizquestion.QuizQuestion;
import no.jonathan.quizapplication.shared.BaseEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "quiz_answer_options")
public class QuizAnswerOption extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quiz_answer_option_generator")
  @SequenceGenerator(name = "quiz_answer_option_generator", sequenceName = "quiz_answer_option_sec")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "quiz_question_id")
  private QuizQuestion question;

  private String answerText;

  private boolean correct;

  public QuizAnswerOption() {}

  private QuizAnswerOption(String answerText, boolean correct) {
    this.answerText = answerText;
    this.correct = correct;
  }

  public static QuizAnswerOption create(String answerText, boolean correct) {
    return new QuizAnswerOption(answerText, correct);
  }

  public static QuizAnswerOption fromCreateRequest(
      QuizQuestion quizQuestion, QuizAnswerOptionCreateRequest createRequest) {
    QuizAnswerOption quizAnswerOption = create(createRequest.text(), createRequest.correct());
    quizAnswerOption.linkToQuestion(quizQuestion);
    return quizAnswerOption;
  }

  public Long getId() {
    return id;
  }

  public String getAnswerText() {
    return answerText;
  }

  public boolean isCorrect() {
    return correct;
  }

  public void linkToQuestion(QuizQuestion question) {
    this.question = question;
  }

  public QuizQuestion getQuestion() {
    return this.question;
  }

  public void updateFromRequest(QuizAnswerOptionUpdateRequest updateRequest) {
    if (updateRequest.text() != null) this.answerText = updateRequest.text();
    this.correct = updateRequest.correct();
  }
}
