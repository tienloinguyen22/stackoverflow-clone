package com.neoflies.mystackoverflowapi.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class CreateQuestionPayload {
  @NotBlank(message = "Question title is required")
  private String title;

  @NotBlank(message = "Question body is required")
  private String body;

  @NotEmpty(message = "Question tags is required")
  private List<String> tags;
}
