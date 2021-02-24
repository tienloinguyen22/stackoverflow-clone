package com.neoflies.mystackoverflowapi.controllers;

import com.neoflies.mystackoverflowapi.domains.*;
import com.neoflies.mystackoverflowapi.dtos.*;
import com.neoflies.mystackoverflowapi.enums.LoginProvider;
import com.neoflies.mystackoverflowapi.exceptions.BadRequestException;
import com.neoflies.mystackoverflowapi.exceptions.ResourceNotFoundException;
import com.neoflies.mystackoverflowapi.repositories.AuthorityRepository;
import com.neoflies.mystackoverflowapi.repositories.EmailConfirmationCodeRepository;
import com.neoflies.mystackoverflowapi.repositories.RefreshTokenRepository;
import com.neoflies.mystackoverflowapi.repositories.UserRepository;
import com.neoflies.mystackoverflowapi.services.ApplicationUserDetailsService;
import com.neoflies.mystackoverflowapi.services.EmailService;
import com.neoflies.mystackoverflowapi.utils.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;

import javax.mail.MessagingException;
import javax.validation.Valid;
import java.util.*;

@RestController
@RequestMapping("/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  TokenProvider tokenProvider;

  @Autowired
  UserRepository userRepository;

  @Autowired
  RefreshTokenRepository refreshTokenRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @Autowired
  ApplicationUserDetailsService applicationUserDetailsService;

  @Autowired
  AuthorityRepository authorityRepository;

  @Autowired
  EmailConfirmationCodeRepository emailConfirmationCodeRepository;

  @Autowired
  EmailService emailService;

  @PostMapping("/sign-in")
  public ResponseEntity<AuthResponse> signIn(@Valid @RequestBody LoginPayload body) {
    Authentication authentication = this.authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword())
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);

    ApplicationUserDetails userDetails = (ApplicationUserDetails) authentication.getPrincipal();
    UUID userId = userDetails.getId();
    User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("sign-in/user-not-found", "User not found"));
    RefreshToken refreshToken = new RefreshToken(UUID.randomUUID(), user);
    RefreshToken result = this.refreshTokenRepository.save(refreshToken);

    JwtToken jwtToken = this.tokenProvider.generateToken(authentication);
    AuthResponse authResponse = new AuthResponse();
    authResponse.setAccessToken(jwtToken.getToken());
    authResponse.setExpiredAt(jwtToken.getExpires());
    authResponse.setRefreshToken(result.getToken().toString());

    return new ResponseEntity<>(authResponse, HttpStatus.OK);
  }

  @PostMapping("/sign-up")
  public ResponseEntity<User> singUp(@Valid @RequestBody SignUpPayload body) {
    Boolean existedEmail = this.userRepository.existsByEmail(body.getEmail());
    if (existedEmail) {
      throw new BadRequestException("sign-up/email-already-in-used", "Email already in use");
    }

    List<Authority> userAuthorities = this.authorityRepository.findAll();
    User user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail(body.getEmail());
    user.setPassword(this.passwordEncoder.encode(body.getPassword()));
    user.setFirstName(body.getFirstName());
    user.setLastName(body.getLastName());
    user.setLoginProvider(LoginProvider.EMAIL);
    user.setAuthorities(userAuthorities);
    User result = this.userRepository.save(user);

    this.emailService.sendConfirmationEmail(result);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }

  @PostMapping("/refresh-token")
  public ResponseEntity<AuthResponse> refreshToken(@Valid @RequestBody RefreshTokenPayload body) {
    RefreshToken existedRefreshToken = this.refreshTokenRepository.findById(UUID.fromString(body.getRefreshToken())).orElseThrow(() -> new ResourceNotFoundException("refresh-token/token-not-found", "Token not found"));
    User user = existedRefreshToken.getUser();
    this.refreshTokenRepository.delete(existedRefreshToken);

    RefreshToken refreshToken = new RefreshToken(UUID.randomUUID(), user);
    RefreshToken result = this.refreshTokenRepository.save(refreshToken);

    UserDetails userDetails = this.applicationUserDetailsService.loadUserById(user.getId());
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

    JwtToken jwtToken = this.tokenProvider.generateToken(authenticationToken);
    AuthResponse authResponse = new AuthResponse();
    authResponse.setAccessToken(jwtToken.getToken());
    authResponse.setExpiredAt(jwtToken.getExpires());
    authResponse.setRefreshToken(result.getToken().toString());
    return new ResponseEntity<>(authResponse, HttpStatus.OK);
  }

  @PostMapping("/confirm-email")
  public ResponseEntity<ConfirmEmailResponse> confirmEmail(@Valid @RequestBody ConfirmEmailPayload body) {
    EmailConfirmationCode emailConfirmationCode = this.emailConfirmationCodeRepository.findById(UUID.fromString(body.getCode()))
      .orElseThrow(() -> new BadRequestException("confirm-email/confirm-code-not-found", "Confirm code not found"));
    Boolean expired = emailConfirmationCode.getExpires().getTime() < new Date().getTime();
    if (expired) {
      throw new BadRequestException("confirm-email/confirm-code-expired", "Confirm code expired");
    }

    User user = emailConfirmationCode.getUser();
    user.setEmailConfirmed(true);
    this.userRepository.save(user);

    ConfirmEmailResponse confirmEmailResponse = new ConfirmEmailResponse();
    confirmEmailResponse.setSuccess(true);
    return new ResponseEntity<>(confirmEmailResponse, HttpStatus.OK);
  }
}
