package com.neoflies.mystackoverflowapi.exceptions;

import org.springframework.security.core.AuthenticationException;

public class OAuth2AuthenticationProcessingException extends AuthenticationException {
  private String error;

  public OAuth2AuthenticationProcessingException() {
    super("OAuth2 processing exception");
    this.error = "common/oauth2-processing-exception";
  }

  public OAuth2AuthenticationProcessingException(String error, String message) {
    super(message);
    this.error = error;
  }
}
