package com.neoflies.mystackoverflowapi.utils;

import com.neoflies.mystackoverflowapi.domains.ApplicationUserDetails;
import com.neoflies.mystackoverflowapi.domains.JwtToken;
import com.neoflies.mystackoverflowapi.domains.RefreshToken;
import com.neoflies.mystackoverflowapi.domains.User;
import com.neoflies.mystackoverflowapi.exceptions.ResourceNotFoundException;
import com.neoflies.mystackoverflowapi.repositories.HttpCookieOAuth2AuthorizationRequestRepository;
import com.neoflies.mystackoverflowapi.repositories.RefreshTokenRepository;
import com.neoflies.mystackoverflowapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

import static com.neoflies.mystackoverflowapi.repositories.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
  @Autowired
  UserRepository userRepository;

  @Autowired
  RefreshTokenRepository refreshTokenRepository;

  @Autowired
  TokenProvider tokenProvider;

  @Autowired
  HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
    String targetUrl = this.determineTargetUrl(request, response, authentication);
    if (response.isCommitted()) {
      logger.debug("Response has already been committed. Unable to redirect to " + targetUrl);
      return;
    }

    this.clearAuthenticationAttributes(request, response);
    this.getRedirectStrategy().sendRedirect(request, response, targetUrl);
  }

  protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME).map(Cookie::getValue);
    String targetUrl = redirectUri.orElse(getDefaultTargetUrl());

    ApplicationUserDetails userDetails = (ApplicationUserDetails) authentication.getPrincipal();
    UUID userId = userDetails.getId();
    User user = this.userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("sign-in/user-not-found", "User not found"));
    RefreshToken refreshToken = new RefreshToken(UUID.randomUUID(), user);
    RefreshToken result = this.refreshTokenRepository.save(refreshToken);
    JwtToken jwtToken = this.tokenProvider.generateToken(authentication);

    return UriComponentsBuilder.fromUriString(targetUrl)
      .queryParam("accessToken", jwtToken.getToken())
      .queryParam("expiredAt", jwtToken.getExpires())
      .queryParam("refreshToken", result.getToken())
      .build().toUriString();
  }

  protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
    super.clearAuthenticationAttributes(request);
    this.httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
  }
}
