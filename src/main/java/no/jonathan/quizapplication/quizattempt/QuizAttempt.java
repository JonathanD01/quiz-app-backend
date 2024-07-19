package no.jonathan.quizapplication.quizattempt;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;
import no.jonathan.quizapplication.quiz.Quiz;
import no.jonathan.quizapplication.quizansweroption.QuizAnswerOption;
import no.jonathan.quizapplication.quizquestion.QuizQuestion;
import no.jonathan.quizapplication.shared.BaseEntity;
import no.jonathan.quizapplication.user.User;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "quiz_attempts")
public class QuizAttempt extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quiz_attempt_generator")
  @SequenceGenerator(name = "quiz_attempt_generator", sequenceName = "quiz_attempt_sec")
  private Long id;

  private UUID link;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "quiz_id")
  private Quiz quiz;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private User attemptBy;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDateTime startTime;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  private LocalDateTime endTime;

  private int maxScore;

  private int score;

  @ElementCollection
  @CollectionTable(name = "quiz_user_answers", joinColumns = @JoinColumn(name = "quiz_attempt_id"))
  private Set<QuizUserAnswer> userAnswers = new HashSet<>();

  public QuizAttempt() {}

  private QuizAttempt(Quiz quiz, User attemptBy, LocalDateTime startTime, LocalDateTime endTime) {
    this.link = UUID.randomUUID();
    this.quiz = quiz;
    this.attemptBy = attemptBy;
    this.startTime = startTime;
    this.endTime = endTime;
  }

  public static QuizAttempt fromCreateRequest(
      User user, Quiz quiz, QuizAttemptCreateRequest createRequest) {
    QuizAttempt quizAttempt =
        new QuizAttempt(quiz, user, createRequest.startTime(), createRequest.endTime());
    quizAttempt.userAnswers = createRequest.userAnswers();
    quizAttempt.calculateScores();
    return quizAttempt;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public UUID getLink() {
    return link;
  }

  public Quiz getQuiz() {
    return quiz;
  }

  public void setQuiz(Quiz quiz) {
    this.quiz = quiz;
  }

  public User getAttemptBy() {
    return attemptBy;
  }

  public Set<QuizUserAnswer> getUserAnswers() {
    return userAnswers;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public LocalDateTime getEndTime() {
    return endTime;
  }

  public int getMaxScore() {
    return maxScore;
  }

  public int getScore() {
    return score;
  }

  public void calculateScores() {
    int maxScore = 0;
    int score = 0;

    Map<Long, Set<Long>> userAnswerMap = new HashMap<>();
    for (QuizUserAnswer userAnswer : userAnswers) {
      userAnswerMap.put(userAnswer.quizQuestionId(), userAnswer.quizAnswerOptionIds());
    }

    // Iterate through each question and calculate score
    for (QuizQuestion quizQuestion : quiz.getQuestions()) {
      Set<Long> userSelectedOptionIds =
          userAnswerMap.getOrDefault(quizQuestion.getId(), Collections.emptySet());

      for (QuizAnswerOption quizAnswerOption : quizQuestion.getAnswerOptions()) {
        if (quizAnswerOption.isCorrect()) {
          maxScore++;
          if (userSelectedOptionIds.contains(quizAnswerOption.getId())) {
            score++;
          }
        }
      }
    }
    this.maxScore = maxScore;
    this.score = Math.max(0, score);
  }
}
