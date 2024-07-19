package no.jonathan.quizapplication.quiz;

import com.querydsl.core.annotations.QueryProjection;
import java.util.UUID;

public class QuizCoreProjection {

  public Long id;
  public UUID link;
  public Status status;
  public String title;
  public String description;

  @QueryProjection
  public QuizCoreProjection(Long id, UUID link, Status status, String title, String description) {
    this.id = id;
    this.link = link;
    this.status = status;
    this.title = title;
    this.description = description;
  }
}
