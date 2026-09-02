package ru.yandex.practicum.filmorate.controller.handler;

import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ru.yandex.practicum.filmorate.controller.exceptions.*;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(DuplicateKeyException.class)
  public ResponseEntity<ErrorResponse> handleDuplicateKey(DuplicateKeyException ex) {
    ErrorResponse response = new ErrorResponse("Значение уже существует");
    log.error(response.toString());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(DuplicationExceptions.class)
  public ResponseEntity<ErrorResponse> handleDuplication(DuplicationExceptions ex) {
    String message = ex.getMessage();
    ErrorResponse response = new ErrorResponse(message);

    log.error(response.toString());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }

  @ExceptionHandler({
    UserNotFound.class,
    FeedNotFound.class,
    FilmNotFound.class,
    ReviewNotFound.class,
    NotFoundException.class
  })
  public ResponseEntity<ErrorResponse> handleUserNotFound(RuntimeException ex) {
    String message = ex.getMessage();
    ErrorResponse response = new ErrorResponse(message);

    log.error(response.toString());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(ConditionsNotMetException.class)
  public ResponseEntity<ErrorResponse> handleConditionsNotMet(ConditionsNotMetException ex) {
    String message = ex.getMessage();
    ErrorResponse response = new ErrorResponse(message);

    log.error(response.toString());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleValid(MethodArgumentNotValidException ex) {
    String message =
        ex.getBindingResult().getFieldErrors().stream()
            .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
            .collect(Collectors.joining("; "));
    ErrorResponse response = new ErrorResponse(message);

    log.error(response.toString());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
    String message =
        ex.getConstraintViolations().stream()
            .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
            .collect(Collectors.joining("; "));
    ErrorResponse response = new ErrorResponse(message);

    log.error(response.toString());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception ex) {
    String message = ex.getMessage();
    ErrorResponse response = new ErrorResponse(message);

    log.error(response.toString());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}
