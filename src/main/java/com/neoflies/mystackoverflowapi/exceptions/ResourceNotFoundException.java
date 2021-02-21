package com.neoflies.mystackoverflowapi.exceptions;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResourceNotFoundException extends RuntimeException {
  private String error;

  public ResourceNotFoundException() {
    super("Not found");
    this.error = "common/not-found";
  }

  public ResourceNotFoundException(String error, String message) {
    super(message);
    this.error = error;
  }
}
