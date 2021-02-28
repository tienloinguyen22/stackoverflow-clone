package com.neoflies.mystackoverflowapi.domains;

import com.neoflies.mystackoverflowapi.utils.Auditable;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "questions")
@Getter
@Setter
@NoArgsConstructor
public class Question extends Auditable<UUID> {
  @Id
  @Column(columnDefinition = "uuid", updatable = false)
  private UUID id;

  private String title;

  @Column(columnDefinition = "text")
  private String body;

  @ManyToMany(targetEntity = Tag.class)
  private List<Tag> tags = new LinkedList<>();

  private Integer views = 0;

  private Integer votes = 0;

  private String slug;
}
