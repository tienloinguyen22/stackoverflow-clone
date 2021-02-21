package com.neoflies.mystackoverflowapi.exceptions;

import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.util.Date;

@Getter
@Setter
public class ApiException {
  private Integer status;
  private String error;
  private String message;
  private Date timestamp;

  public ApiException() {
    this.status = HttpStatus.INTERNAL_SERVER_ERROR.value();
    this.error = "common/internal-server-error";
    this.message = "Internal server error";
    this.timestamp = new Date();
  }

  public ApiException(String error, String message, HttpStatus status) {
    this.status = status.value();
    this.error = error;
    this.message = message;
    this.timestamp = new Date();
  }
}
