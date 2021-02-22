package com.neoflies.mystackoverflowapi.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class CreateAnswerPayload {
  @NotBlank(message = "Answer body is required")
  private String body;

  @NotBlank(message = "Question is required")
  private String question;
}
