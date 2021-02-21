package com.neoflies.mystackoverflowapi.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BadRequestException extends RuntimeException {
  private String error;

  public BadRequestException() {
    super("Bad request");
    this.error = "common/bad-request";
  }

  public BadRequestException(String error, String message) {
    super(message);
    this.error = error;
  }
}
