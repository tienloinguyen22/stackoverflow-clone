package com.neoflies.mystackoverflowapi.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.EntityListeners;
import javax.persistence.MappedSuperclass;
import java.util.Date;

@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Auditable<U> {
  @CreatedBy
  private U createdBy;

  @CreatedDate
  private Date createdDate;

  @LastModifiedBy
  private U lastModifiedBy;

  @LastModifiedDate
  private Date lastModifiedDate;
}
