package com.neoflies.mystackoverflowapi.configs;

import com.neoflies.mystackoverflowapi.exceptions.ApiAuthenticationEntryPoint;
import com.neoflies.mystackoverflowapi.repositories.HttpCookieOAuth2AuthorizationRequestRepository;
import com.neoflies.mystackoverflowapi.services.ApplicationUserDetailsService;
import com.neoflies.mystackoverflowapi.utils.OAuth2AuthenticationFailureHandler;
import com.neoflies.mystackoverflowapi.utils.OAuth2AuthenticationSuccessHandler;
import com.neoflies.mystackoverflowapi.utils.TokenAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
public class SecurityConfigs extends WebSecurityConfigurerAdapter {
  @Autowired
  ApplicationUserDetailsService applicationUserDetailsService;

  @Autowired
  OAuth2UserService oAuth2UserService;

  @Autowired
  OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

  @Autowired
  OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

  @Autowired
  HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public TokenAuthenticationFilter tokenAuthenticationFilter() {
    return new TokenAuthenticationFilter();
  }

  @Override
  public void configure(AuthenticationManagerBuilder auth) throws Exception {
    auth
      .userDetailsService(this.applicationUserDetailsService)
      .passwordEncoder(this.passwordEncoder());
  }

  @Bean(BeanIds.AUTHENTICATION_MANAGER)
  @Override
  public AuthenticationManager authenticationManagerBean() throws Exception {
    return super.authenticationManagerBean();
  }

  @Override
  protected void configure(HttpSecurity http) throws Exception {
    http
      .cors()
      .and()
      .sessionManagement()
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
      .and()
      .csrf()
        .disable()
      .formLogin()
        .disable()
      .httpBasic()
        .disable()
      .exceptionHandling()
        .authenticationEntryPoint(new ApiAuthenticationEntryPoint())
      .and()
      .authorizeRequests()
        .antMatchers("/auth/**", "/oauth2/**").permitAll()
        .anyRequest().authenticated()
      .and()
      .oauth2Login()
        .authorizationEndpoint().baseUri("/oauth2/authorize").authorizationRequestRepository(this.httpCookieOAuth2AuthorizationRequestRepository)
        .and()
        .userInfoEndpoint().userService(this.oAuth2UserService)
        .and()
        .successHandler(this.oAuth2AuthenticationSuccessHandler)
        .failureHandler(this.oAuth2AuthenticationFailureHandler);

    http.addFilterBefore(this.tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
  }
}
