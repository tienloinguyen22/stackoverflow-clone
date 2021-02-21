package com.neoflies.mystackoverflowapi.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
public class RefreshTokenPayload {
  @NotBlank(message = "Refresh Token is required")
  private String refreshToken;
}
