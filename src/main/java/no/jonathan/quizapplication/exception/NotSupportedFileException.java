package no.jonathan.quizapplication.exception;

public class NotSupportedFileException extends RuntimeException {

  public NotSupportedFileException() {
    super("Only .txt files are supported");
  }
}
