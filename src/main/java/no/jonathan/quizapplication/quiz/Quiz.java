package no.jonathan.quizapplication.quiz;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import no.jonathan.quizapplication.quizattempt.QuizAttempt;
import no.jonathan.quizapplication.quizquestion.QuizQuestion;
import no.jonathan.quizapplication.shared.BaseEntity;
import no.jonathan.quizapplication.user.User;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = "quizzes")
public class Quiz extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "quiz_generator")
  @SequenceGenerator(name = "quiz_generator", sequenceName = "quiz_sec")
  private Long id;

  private UUID link;

  @Enumerated(EnumType.STRING)
  private Status status;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "creator_id")
  private User creator;

  private String title;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name = "is_shared")
  private boolean shared;

  @OneToMany(mappedBy = "quiz")
  private Set<QuizAttempt> quizAttempts = new HashSet<>();

  @OneToMany(mappedBy = "quiz")
  @OrderBy(value = "created_at desc")
  private Set<QuizQuestion> questions = new HashSet<>();

  public Quiz() {}

  private Quiz(User creator) {
    this.link = UUID.randomUUID();
    this.status = Status.PENDING;
    this.creator = creator;
  }

  public static Quiz createUnfinishedQuiz(User creator) {
    return new Quiz(creator);
  }

  public Long getId() {
    return id;
  }

  public UUID getLink() {
    return link;
  }

  public Status getStatus() {
    return status;
  }

  public User getCreator() {
    return creator;
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public void updateStatus(Status status) {
    this.status = status;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public boolean isShared() {
    return shared;
  }

  public Set<QuizQuestion> getQuestions() {
    return questions;
  }

  public Quiz addQuestion(QuizQuestion question) {
    questions.add(question);
    return this;
  }

  public void updateFromRequest(QuizUpdateRequest updateRequest) {
    if (updateRequest.title() != null) this.title = updateRequest.title();
    if (updateRequest.description() != null) this.description = updateRequest.description();
    this.shared = updateRequest.shared();
  }
}
