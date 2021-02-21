package com.neoflies.mystackoverflowapi.domains;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "authorities")
@Getter
@Setter
public class Authority {
  @Id
  private String authority;
}
