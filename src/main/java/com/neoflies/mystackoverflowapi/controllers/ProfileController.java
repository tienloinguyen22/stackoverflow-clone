package com.neoflies.mystackoverflowapi.controllers;

import com.neoflies.mystackoverflowapi.domains.ApplicationUserDetails;
import com.neoflies.mystackoverflowapi.domains.User;
import com.neoflies.mystackoverflowapi.dtos.ConfirmEmailResponse;
import com.neoflies.mystackoverflowapi.exceptions.ResourceNotFoundException;
import com.neoflies.mystackoverflowapi.repositories.UserRepository;
import com.neoflies.mystackoverflowapi.services.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {
  @Autowired
  UserRepository userRepository;

  @Autowired
  EmailService emailService;

  @GetMapping("/me")
  @PreAuthorize("hasAuthority('LOGGED_IN')")
  public ResponseEntity<User> getMyProfile() {
    User user = this.getUserFromAuthentication();
    return new ResponseEntity<>(user, HttpStatus.OK);
  }

  @GetMapping("/resend-confirm-email")
  @PreAuthorize("hasAuthority('LOGGED_IN')")
  public ResponseEntity<ConfirmEmailResponse> resendConfirmEmail() {
    User user = this.getUserFromAuthentication();
    this.emailService.sendConfirmationEmail(user);

    ConfirmEmailResponse confirmEmailResponse = new ConfirmEmailResponse();
    confirmEmailResponse.setSuccess(true);
    return new ResponseEntity<>(confirmEmailResponse, HttpStatus.OK);
  }

  private User getUserFromAuthentication() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    ApplicationUserDetails userDetails = (ApplicationUserDetails) authentication.getPrincipal();
    UUID id = userDetails.getId();
    User user = this.userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("profile/profile-not-found", "Profile not found"));
    return user;
  }
}
