package no.jonathan.quizapplication.quiz;

import com.fasterxml.jackson.annotation.JsonValue;

public enum Status {
  COMPLETED,
  PENDING,
  FAILED;

  @JsonValue
  @Override
  public String toString() {
    return this.name().toUpperCase();
  }
}
