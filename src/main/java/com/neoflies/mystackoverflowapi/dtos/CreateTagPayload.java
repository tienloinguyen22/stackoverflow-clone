package com.neoflies.mystackoverflowapi.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Getter
@Setter
@NoArgsConstructor
public class CreateTagPayload {
  @NotBlank(message = "Tag name is required")
  @Pattern(regexp = "^[a-z0-9-]*$", message = "Tag name contains only lowercase characters, numbers & hyphens")
  private String name;

  @NotBlank(message = "Tag description is required")
  private String description;
}
