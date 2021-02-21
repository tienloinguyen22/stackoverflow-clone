package com.neoflies.mystackoverflowapi.services;

import com.neoflies.mystackoverflowapi.domains.ApplicationUserDetails;
import com.neoflies.mystackoverflowapi.domains.User;
import com.neoflies.mystackoverflowapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.util.UUID;

@Service
public class ApplicationUserDetailsService implements UserDetailsService {
  @Autowired
  UserRepository userRepository;

  @Override
  public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
    User user = this.userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("User not found for email: " + email));
    return ApplicationUserDetails.create(user);
  }

  public UserDetails loadUserById(UUID id) {
    User user = this.userRepository.findById(id).orElseThrow(() -> new ResourceAccessException("User not found"));
    return ApplicationUserDetails.create(user);
  }
}
