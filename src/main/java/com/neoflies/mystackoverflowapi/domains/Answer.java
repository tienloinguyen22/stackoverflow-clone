package com.neoflies.mystackoverflowapi.domains;

import com.neoflies.mystackoverflowapi.utils.Auditable;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "answers")
@Getter
@Setter
public class Answer extends Auditable<UUID> {
  @Id
  @Column(columnDefinition = "uuid", updatable = false)
  private UUID id;

  private String body;

  private Integer votes = 0;

  @ManyToOne
  private Question question;
}
