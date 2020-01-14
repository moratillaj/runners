package com.runnersteam.runners.config;

import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

import com.runnersteam.runners.exception.ExistingRunnerException;
import com.runnersteam.runners.exception.RunnerNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class RunnersExceptionHandler {

  @ExceptionHandler(value = RunnerNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleRunnerNotFound
      (RunnerNotFoundException runnerMailNotFoundException) {
    log.error("", runnerMailNotFoundException);
    return ResponseEntity.notFound().build();
  }

  @ExceptionHandler(value = ExistingRunnerException.class)
  public ResponseEntity<ErrorResponse> handleExistingRunnerException(
      ExistingRunnerException existingRunnerException) {
    log.error("", existingRunnerException);
    return ResponseEntity.status(CONFLICT).body(
        new ErrorResponse(existingRunnerException.getMessage()));
  }

  @ExceptionHandler
  public ResponseEntity<ErrorResponse> handleInternalError(Exception exception) {
    log.error("", exception);
    return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(
        new ErrorResponse(exception.getMessage())
    );
  }
}
