package com.neoflies.mystackoverflowapi.domains;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "email_confirmation_codes")
@Getter
@Setter
@NoArgsConstructor
public class EmailConfirmationCode {
  @Id
  @Column(columnDefinition = "uuid", updatable = false)
  private UUID code;

  private Date expires;

  @ManyToOne
  private User user;
}
