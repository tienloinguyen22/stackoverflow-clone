package com.neoflies.mystackoverflowapi.domains;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.neoflies.mystackoverflowapi.utils.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "tags")
@Getter
@Setter
@NoArgsConstructor
public class Tag extends Auditable<UUID> {
  @Id
  @Column(columnDefinition = "uuid", updatable = false)
  private UUID id;

  private String name;

  private String description;

  private Integer count = 0;

  @JsonInclude()
  @Transient
  private Integer day;

  @JsonInclude()
  @Transient
  private Integer week;
  
  @JsonInclude()
  @Transient
  private boolean watched = false;
}
