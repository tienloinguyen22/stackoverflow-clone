package com.neoflies.mystackoverflowapi.utils;

import com.neoflies.mystackoverflowapi.domains.ApplicationUserDetails;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class TokenProvider {
  @Autowired
  ApplicationProperties applicationProperties;

  public String generateToken(Authentication authentication) {
    ApplicationUserDetails userDetails = (ApplicationUserDetails) authentication.getPrincipal();
    Date expires = new Date(new Date().getTime() + this.applicationProperties.getAuth().getTokenExpires());
    return Jwts.builder()
            .setSubject(userDetails.getId().toString())
            .setIssuedAt(new Date())
            .setExpiration(expires)
            .signWith(SignatureAlgorithm.HS256, this.applicationProperties.getAuth().getTokenSecret())
            .compact();
  }
}
