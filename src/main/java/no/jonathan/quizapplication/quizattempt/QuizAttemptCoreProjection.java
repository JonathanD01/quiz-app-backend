package no.jonathan.quizapplication.quizattempt;

import com.querydsl.core.annotations.QueryProjection;

import java.time.LocalDateTime;
import java.util.UUID;

public class QuizAttemptCoreProjection {

  public Long id;
  public UUID link;
  public String title;
  public String description;
  public LocalDateTime createdAt;

  @QueryProjection
  public QuizAttemptCoreProjection(Long id, UUID link, String title, String description, LocalDateTime createdAt) {
    this.id = id;
    this.link = link;
    this.title = title;
    this.description = description;
    this.createdAt = createdAt;
  }
}
