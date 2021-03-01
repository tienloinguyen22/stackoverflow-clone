package com.neoflies.mystackoverflowapi.utils;

import com.neoflies.mystackoverflowapi.domains.ApplicationUserDetails;
import com.neoflies.mystackoverflowapi.domains.User;
import com.neoflies.mystackoverflowapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
public class AuthorizationUtils {
  @Autowired
  UserRepository userRepository;

  public Optional<User> getCurrentUser() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Object principal = authentication.getPrincipal();
    if (principal == "anonymousUser") {
      return Optional.empty();
    }

    ApplicationUserDetails userDetails = (ApplicationUserDetails) principal;
    UUID currentUserId = userDetails.getId();
    Optional<User> currentUser = this.userRepository.findById(currentUserId);
    return currentUser;
  }
}
