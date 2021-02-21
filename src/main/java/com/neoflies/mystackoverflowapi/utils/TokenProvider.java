package com.neoflies.mystackoverflowapi.utils;

import com.neoflies.mystackoverflowapi.domains.ApplicationUserDetails;
import com.neoflies.mystackoverflowapi.domains.JwtToken;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

@Component
public class TokenProvider {
  private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

  @Autowired
  ApplicationProperties applicationProperties;

  public JwtToken generateToken(Authentication authentication) {
    ApplicationUserDetails userDetails = (ApplicationUserDetails) authentication.getPrincipal();
    Date expires = new Date(new Date().getTime() + this.applicationProperties.getAuth().getTokenExpires());
    String token = Jwts.builder()
            .setSubject(userDetails.getId().toString())
            .setIssuedAt(new Date())
            .setExpiration(expires)
            .signWith(SignatureAlgorithm.HS256, this.applicationProperties.getAuth().getTokenSecret())
            .compact();

    return new JwtToken(token, expires);
  }

  public Boolean validateToken(String token) {
    try {
      Jwts.parser().setSigningKey(this.applicationProperties.getAuth().getTokenSecret()).parseClaimsJws(token);
      return true;
    } catch (SignatureException ex) {
      this.logger.error("Invalid JWT signature");
    } catch (MalformedJwtException ex) {
      this.logger.error("Invalid JWT token");
    } catch (ExpiredJwtException ex) {
      this.logger.error("Expired JWT token");
    } catch (UnsupportedJwtException ex) {
      this.logger.error("Unsupported JWT token");
    } catch (IllegalArgumentException ex) {
      this.logger.error("JWT claims string is empty");
    }
    return false;
  }

  public UUID getUserIdFromToken(String token) {
    Claims claims = Jwts.parser().setSigningKey(this.applicationProperties.getAuth().getTokenSecret()).parseClaimsJws(token).getBody();
    return UUID.fromString(claims.getSubject());
  }
}
