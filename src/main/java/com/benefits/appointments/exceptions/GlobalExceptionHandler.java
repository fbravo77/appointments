package com.benefits.appointments.exceptions;

import io.jsonwebtoken.ExpiredJwtException;
import java.security.SignatureException;
import java.util.List;
import java.util.NoSuchElementException;
import org.springframework.http.HttpStatusCode;
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
  @ExceptionHandler(Exception.class)
  public ProblemDetail handleSecurityException(Exception exception) {
    ProblemDetail errorDetail = null;

    // TODO send this stack trace to an observability tool
    exception.printStackTrace();

    if (exception instanceof BadCredentialsException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), exception.getMessage());
      errorDetail.setProperty("description", "The username or password is incorrect");
    }

    else if (exception instanceof AccountStatusException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
      errorDetail.setProperty("description", "The account is locked");
    }

    else if (exception instanceof AccessDeniedException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
      errorDetail.setProperty("description", "You are not authorized to access this resource");
    }

    else if (exception instanceof SignatureException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
      errorDetail.setProperty("description", "The JWT signature is invalid");
    }

    else if (exception instanceof ExpiredJwtException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403), exception.getMessage());
      errorDetail.setProperty("description", "The JWT token has expired");
    }
    else if (exception instanceof NoSuchElementException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(404), exception.getMessage());
      errorDetail.setProperty("description", exception.getMessage());
    }
    else if (exception instanceof MethodArgumentNotValidException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), processFieldErrors(((MethodArgumentNotValidException) exception).getBindingResult().getFieldErrors()));
      errorDetail.setProperty("description", "Invalid request content");
    }
    else if (exception instanceof IllegalArgumentException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), exception.getMessage());
      errorDetail.setProperty("description", exception.getMessage());
    }
    else if (exception instanceof RuntimeException) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(400), exception.getMessage());
      errorDetail.setProperty("description", exception.getMessage());
    }
    if (errorDetail == null) {
      errorDetail = ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(500), exception.getMessage());
      errorDetail.setProperty("description", "Unknown internal server error.");
    }

    return errorDetail;
  }

  private String processFieldErrors(List<FieldError> fieldErrors) {
    StringBuilder errorMessage = new StringBuilder();
    for (org.springframework.validation.FieldError fieldError: fieldErrors) {
      errorMessage.append(fieldError.getField()).append(": ").append(fieldError.getDefaultMessage()).append("; ");
    }
    return errorMessage.toString();
  }

}
