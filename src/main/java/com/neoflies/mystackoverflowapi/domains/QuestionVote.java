package com.neoflies.mystackoverflowapi.domains;

import com.neoflies.mystackoverflowapi.utils.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "questions_votes")
@Getter
@Setter
@NoArgsConstructor
public class QuestionVote extends Auditable<UUID> {
  @Id
  @Column(columnDefinition = "uuid", updatable = false)
  private UUID id;

  @ManyToOne
  private Question question;

  @ManyToOne
  private User user;

  private Integer vote;
}
