package com.neoflies.mystackoverflowapi.domains;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.neoflies.mystackoverflowapi.enums.Gender;
import com.neoflies.mystackoverflowapi.enums.LoginProvider;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;
import java.util.UUID;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {
  @Id
  @Column(columnDefinition = "uuid", updatable = false)
  private UUID id;

  private String email;

  @JsonIgnore
  private String password;

  private String firstName;

  private String lastName;

  private String countryCode;

  private String phoneNumber;

  private Gender gender;

  private Date dob;

  private String bio;

  private String avatarUrl;

  private LoginProvider loginProvider;

  private String providerId;

  private Boolean emailConfirmed = false;

  private Boolean active = true;

  @CreationTimestamp
  private Date createdAt;

  @UpdateTimestamp
  private Date updatedAt;
}
