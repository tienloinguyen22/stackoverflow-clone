package com.neoflies.mystackoverflowapi.controllers;

import com.neoflies.mystackoverflowapi.domains.User;
import com.neoflies.mystackoverflowapi.dtos.AuthResponse;
import com.neoflies.mystackoverflowapi.dtos.LoginPayload;
import com.neoflies.mystackoverflowapi.dtos.SignUpPayload;
import com.neoflies.mystackoverflowapi.enums.LoginProvider;
import com.neoflies.mystackoverflowapi.exceptions.BadRequestException;
import com.neoflies.mystackoverflowapi.repositories.UserRepository;
import com.neoflies.mystackoverflowapi.utils.TokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
  @Autowired
  AuthenticationManager authenticationManager;

  @Autowired
  TokenProvider tokenProvider;

  @Autowired
  UserRepository userRepository;

  @Autowired
  PasswordEncoder passwordEncoder;

  @PostMapping("/sign-in")
  public ResponseEntity<AuthResponse> signIn(@Valid @RequestBody LoginPayload body) {
    Authentication authentication = this.authenticationManager.authenticate(
      new UsernamePasswordAuthenticationToken(body.getEmail(), body.getPassword())
    );
    SecurityContextHolder.getContext().setAuthentication(authentication);

    String accessToken = this.tokenProvider.generateToken(authentication);
    AuthResponse authResponse = new AuthResponse();
    authResponse.setAccessToken(accessToken);
    return new ResponseEntity<>(authResponse, HttpStatus.OK);
  }

  @PostMapping("/sign-up")
  public ResponseEntity<User> singUp(@Valid @RequestBody SignUpPayload body) {
    Boolean existedEmail = this.userRepository.existsByEmail(body.getEmail());
    if (existedEmail) {
      throw new BadRequestException("Email already in use");
    }

    User user = new User();
    user.setId(UUID.randomUUID());
    user.setEmail(body.getEmail());
    user.setPassword(this.passwordEncoder.encode(body.getPassword()));
    user.setFirstName(body.getFirstName());
    user.setLastName(body.getLastName());
    user.setLoginProvider(LoginProvider.EMAIL);

    User result = this.userRepository.save(user);
    return new ResponseEntity<>(result, HttpStatus.OK);
  }
}
