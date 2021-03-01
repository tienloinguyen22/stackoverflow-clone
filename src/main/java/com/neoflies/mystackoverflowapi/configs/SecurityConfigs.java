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
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
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

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:3003"));
    configuration.setAllowedMethods(Arrays.asList("GET","POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("Content-Type", "Access-Control-Allow-Origin", "Access-Control-Allow-Headers", "Origin", "Accept", "X-Requested-With", "Access-Control-Request-Method", "Access-Control-Request-Headers", "Authorization"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
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
        .configurationSource(this.corsConfigurationSource())
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
        .antMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
        .antMatchers(HttpMethod.GET, "/api/questions/**").permitAll()
        .antMatchers(HttpMethod.GET, "/api/answers/**").permitAll()
        .antMatchers(HttpMethod.GET, "/api/tags/**").permitAll()
        .antMatchers(HttpMethod.GET, "/api/users/**").permitAll()
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
