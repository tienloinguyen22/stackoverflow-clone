package com.neoflies.mystackoverflowapi.domains;

import com.neoflies.mystackoverflowapi.utils.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "answers_votes")
@Getter
@Setter
@NoArgsConstructor
public class AnswerVote extends Auditable<UUID> {
  @Id
  @Column(columnDefinition = "uuid", updatable = false)
  private UUID id;

  @ManyToOne
  private Answer answer;

  @ManyToOne
  private User user;

  private Integer vote;
}
