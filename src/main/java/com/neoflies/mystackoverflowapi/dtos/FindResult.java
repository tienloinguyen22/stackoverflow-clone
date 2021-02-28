package com.neoflies.mystackoverflowapi.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class FindResult<T> {
  private List<T> data;

  private Integer total;

  public FindResult(List<T> data, Integer total) {
    this.data = data;
    this.total = total;
  }
}
