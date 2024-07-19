package no.jonathan.quizapplication.quizquestion;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import no.jonathan.quizapplication.quiz.Quiz;
import no.jonathan.quizapplication.quizansweroption.QuizAnswerOption;
import no.jonathan.quizapplication.shared.BaseEntity;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "quiz_questions")
public class QuizQuestion extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quiz_questions_generator")
  @SequenceGenerator(name = "quiz_questions_generator", sequenceName = "quiz_questions_sec")
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "quiz_id")
  private Quiz quiz;

  private String questionText;

  @OneToMany(mappedBy = "question")
  @OrderBy(value = "created_at desc")
  private Set<QuizAnswerOption> answerOptions = new HashSet<>();

  public QuizQuestion() {}

  private QuizQuestion(String questionText) {
    this.questionText = questionText;
  }

  public static QuizQuestion create(String text) {
    return new QuizQuestion(text);
  }

  public static QuizQuestion fromCreateRequest(Quiz quiz, QuizQuestionCreateRequest createRequest) {
    QuizQuestion newQuizCreation = create(createRequest.text());
    newQuizCreation.linkToQuiz(quiz);
    return newQuizCreation;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public Quiz getQuiz() {
    return quiz;
  }

  public String getQuestionText() {
    return questionText;
  }

  public Set<QuizAnswerOption> getAnswerOptions() {
    return answerOptions;
  }

  public void linkToQuiz(Quiz quizToAttachTo) {
    this.quiz = quizToAttachTo;
  }

  public void addAnswerOption(QuizAnswerOption answerOption) {
    answerOptions.add(answerOption);
  }

  public void updateFromRequest(QuizQuestionUpdateRequest updateRequest) {
    if (updateRequest.question() != null) {
      this.questionText = updateRequest.question();
    }
  }
}
