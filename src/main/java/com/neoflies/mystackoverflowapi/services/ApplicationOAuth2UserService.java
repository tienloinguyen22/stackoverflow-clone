package com.neoflies.mystackoverflowapi.services;

import com.neoflies.mystackoverflowapi.domains.*;
import com.neoflies.mystackoverflowapi.enums.LoginProvider;
import com.neoflies.mystackoverflowapi.exceptions.OAuth2AuthenticationProcessingException;
import com.neoflies.mystackoverflowapi.repositories.AuthorityRepository;
import com.neoflies.mystackoverflowapi.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ApplicationOAuth2UserService extends DefaultOAuth2UserService {
  @Autowired
  UserRepository userRepository;

  @Autowired
  AuthorityRepository authorityRepository;

  @Override
  public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
    OAuth2User oAuth2User = super.loadUser(userRequest);

    try {
      return processOAuth2User(userRequest, oAuth2User);
    } catch (AuthenticationException ex) {
      throw ex;
    } catch (Exception ex) {
      throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
    }
  }

  private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
    String loginType = oAuth2UserRequest.getClientRegistration().getRegistrationId();
    OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(loginType, oAuth2User.getAttributes());
    if (oAuth2UserInfo.getEmail().isBlank()) {
      throw new OAuth2AuthenticationProcessingException("common/email-not-found-from-oauth2-provider", "Email not found from OAuth2 provider");
    }

    Optional<User> optionalUser = this.userRepository.findByEmail(oAuth2UserInfo.getEmail());
    if (optionalUser.isPresent()) {
      User existedUser = optionalUser.get();
      if (!existedUser.getLoginProvider().toString().equals(loginType)) {
        throw new OAuth2AuthenticationProcessingException("common/email-already-in-use", "Email already in use");
      } else {
        return ApplicationUserDetails.create(existedUser, oAuth2User.getAttributes());
      }
    } else {
      List<Authority> userAuthorities = this.authorityRepository.findAll();

      User newUser = new User();
      newUser.setId(UUID.randomUUID());
      newUser.setEmail(oAuth2UserInfo.getEmail());
      newUser.setEmailConfirmed(true);
      newUser.setFirstName(oAuth2UserInfo.getFirstName());
      newUser.setLastName(oAuth2UserInfo.getLastName());
      newUser.setAvatarUrl(oAuth2UserInfo.getImageUrl());
      newUser.setLoginProvider(loginType.equals("facebook") ? LoginProvider.FACEBOOK : LoginProvider.GOOGLE);
      newUser.setProviderId(oAuth2UserInfo.getId());
      newUser.setAuthorities(userAuthorities);
      this.userRepository.save(newUser);

      return ApplicationUserDetails.create(newUser, oAuth2User.getAttributes());
    }
  }
}
