package com.neoflies.mystackoverflowapi.domains;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class JwtToken {
  private String token;
  private Date expires;

  public JwtToken(String token, Date expires) {
    this.token = token;
    this.expires = expires;
  }
}
