package com.neoflies.mystackoverflowapi.domains;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class File {
  private String filename;
  private String url;
  private Long size;
}
