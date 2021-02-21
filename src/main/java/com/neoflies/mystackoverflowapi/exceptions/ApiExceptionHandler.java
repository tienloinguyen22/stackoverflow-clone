package com.neoflies.mystackoverflowapi.exceptions;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {
  private static final Logger logger = LoggerFactory.getLogger(ApiExceptionHandler.class);
  private static final Pattern NON_LATIN = Pattern.compile("[^\\w-]");
  private static final Pattern WHITESPACE = Pattern.compile("[\\s]");

  private String makeSlug(String message) {
    if (message == null) {
      return "";
    }

    String noWhiteSpace = WHITESPACE.matcher(message).replaceAll("-");
    String normalized = Normalizer.normalize(noWhiteSpace, Normalizer.Form.NFD);
    String slug = NON_LATIN.matcher(normalized).replaceAll("");
    return slug.toLowerCase(Locale.ENGLISH);
  }

  @ExceptionHandler(BadRequestException.class)
  protected ResponseEntity<ApiException> handleBadRequestException(BadRequestException ex) {
    ApiException apiException = new ApiException(ex.getError(), ex.getMessage(), HttpStatus.BAD_REQUEST);
    return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  protected ResponseEntity<ApiException> handleResourceNotFoundException(ResourceNotFoundException ex) {
    ApiException apiException = new ApiException(ex.getError(), ex.getMessage(), HttpStatus.NOT_FOUND);
    return new ResponseEntity<>(apiException, HttpStatus.NOT_FOUND);
  }

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
    List<String> errors = ex.getBindingResult().getFieldErrors().stream().map((fieldError) -> fieldError.getDefaultMessage()).collect(Collectors.toList());
    String description = request.getDescription(true);
    String uri = description.split(";")[0];
    String[] paths = uri.split("/");
    String message = errors.get(0);
    String error = paths[paths.length - 1] + "/" + this.makeSlug(message);

    ApiException apiException = new ApiException(error, message, HttpStatus.BAD_REQUEST);
    return new ResponseEntity<>(apiException, HttpStatus.BAD_REQUEST);
  }
}
