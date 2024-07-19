package no.jonathan.quizapplication.shared;

import jakarta.persistence.MappedSuperclass;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@MappedSuperclass
public class BaseEntity {

  @CreatedDate
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  protected LocalDateTime createdAt;

  @LastModifiedDate
  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  protected LocalDateTime updatedAt;

  @LastModifiedBy protected String lastModifiedBy;
  @CreatedBy private String createdBy;

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public String getCreatedByEmail() {
    return createdBy;
  }

  public void setCreatedBy(String createdByEmail) {
    this.createdBy = createdByEmail;
  }
}
