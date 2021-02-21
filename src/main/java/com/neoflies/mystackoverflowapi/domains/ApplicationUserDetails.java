package com.neoflies.mystackoverflowapi.domains;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.*;

@Getter
@Setter
public class ApplicationUserDetails implements OAuth2User, UserDetails {
  private UUID id;
  private String email;
  private String password;
  private Boolean active;
  private Collection<GrantedAuthority> authorities;
  private Map<String, Object> attributes;

  public ApplicationUserDetails(UUID id, String email, String password, Boolean active, Collection<GrantedAuthority> authorities) {
    this.id = id;
    this.email = email;
    this.password = password;
    this.active = active;
    this.authorities = authorities;
  }

  public static UserDetails create(User user) {
    List<GrantedAuthority> authorities = new ArrayList<>();
    authorities.add(new SimpleGrantedAuthority("LOGGED_IN"));
    return new ApplicationUserDetails(user.getId(), user.getEmail(), user.getPassword(), user.getActive(), authorities);
  }

  public static ApplicationUserDetails create(User user, Map<String, Object> attributes) {
    ApplicationUserDetails userDetails = (ApplicationUserDetails) ApplicationUserDetails.create(user);
    userDetails.setAttributes(attributes);
    return userDetails;
  }

  @Override
  public Map<String, Object> getAttributes() {
    return this.attributes;
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.email;
  }

  @Override
  public boolean isAccountNonExpired() {
    return this.active;
  }

  @Override
  public boolean isAccountNonLocked() {
    return this.active;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return this.active;
  }

  @Override
  public String getName() {
    return null;
  }
}
