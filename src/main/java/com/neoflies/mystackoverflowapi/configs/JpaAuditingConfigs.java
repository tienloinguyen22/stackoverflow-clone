package com.neoflies.mystackoverflowapi.configs;

import com.neoflies.mystackoverflowapi.domains.ApplicationUserDetails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

@Configuration
@EnableJpaAuditing(auditorAwareRef = "auditorProvider")
public class JpaAuditingConfigs {
  @Bean
  public AuditorAware<UUID> auditorProvider() {
    return () -> {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      if (authentication.getPrincipal() == "anonymousUser") {
        return Optional.empty();
      }
      ApplicationUserDetails userDetails = (ApplicationUserDetails) authentication.getPrincipal();
      return Optional.ofNullable(userDetails.getId());
    };
  }
}
