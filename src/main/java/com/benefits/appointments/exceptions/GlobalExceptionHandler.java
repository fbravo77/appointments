package com.benefits.appointments.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import java.security.SignatureException;
import java.util.List;
import java.util.NoSuchElementException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AccountStatusException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(BadCredentialsException.class)
  public ProblemDetail handleBadCredentialsException(BadCredentialsException exception) {
    logException(exception);
    return createProblemDetail(HttpStatus.UNAUTHORIZED, "The username or password is incorrect",
        exception.getMessage());
  }

  @ExceptionHandler(AccountStatusException.class)
  public ProblemDetail handleAccountStatusException(AccountStatusException exception) {
    logException(exception);
    return createProblemDetail(HttpStatus.FORBIDDEN, "The account is locked", exception.getMessage());
  }

  @ExceptionHandler(AccessDeniedException.class)
  public ProblemDetail handleAccessDeniedException(AccessDeniedException exception) {
    logException(exception);
    return createProblemDetail(HttpStatus.FORBIDDEN, "You are not authorized to access this resource",
        exception.getMessage());
  }

  @ExceptionHandler(SignatureException.class)
  public ProblemDetail handleSignatureException(SignatureException exception) {
    logException(exception);
    return createProblemDetail(HttpStatus.FORBIDDEN, "The JWT signature is invalid", exception.getMessage());
  }

  @ExceptionHandler(ExpiredJwtException.class)
  public ProblemDetail handleExpiredJwtException(ExpiredJwtException exception) {
    logException(exception);
    return createProblemDetail(HttpStatus.FORBIDDEN, "The JWT token has expired", exception.getMessage());
  }

  @ExceptionHandler(NoSuchElementException.class)
  public ProblemDetail handleNoSuchElementException(NoSuchElementException exception) {
    logException(exception);
    return createProblemDetail(HttpStatus.NOT_FOUND, exception.getMessage(), exception.getMessage());
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ProblemDetail handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
    logException(exception);
    String errorMessage = processFieldErrors(exception.getBindingResult().getFieldErrors());
    return createProblemDetail(HttpStatus.BAD_REQUEST, "Invalid request content", errorMessage);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ProblemDetail handleIllegalArgumentException(IllegalArgumentException exception) {
    logException(exception);
    return createProblemDetail(HttpStatus.BAD_REQUEST, exception.getMessage(), exception.getMessage());
  }

  @ExceptionHandler(RuntimeException.class)
  public ProblemDetail handleRuntimeException(RuntimeException exception) {
    logException(exception);
    return createProblemDetail(HttpStatus.BAD_REQUEST, exception.getMessage(), exception.getMessage());
  }

  @ExceptionHandler(Exception.class)
  public ProblemDetail handleGeneralException(Exception exception) {
    logException(exception);
    return createProblemDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Unknown internal server error.",
        exception.getMessage());
  }

  private void logException(Exception exception) {
    //Improvement Send this to an observability tool
    logger.error("Exception: ", exception);
  }

  private ProblemDetail createProblemDetail(HttpStatus status, String description, String detail) {
    ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(status, detail);
    problemDetail.setProperty("description", description);
    return problemDetail;
  }

  private String processFieldErrors(List<FieldError> fieldErrors) {
    StringBuilder errorMessage = new StringBuilder();
    for (FieldError fieldError : fieldErrors) {
      errorMessage.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append("; ");
    }
    return errorMessage.toString();
  }
}
