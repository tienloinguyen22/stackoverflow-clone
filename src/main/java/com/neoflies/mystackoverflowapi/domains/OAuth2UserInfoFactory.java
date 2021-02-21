package com.neoflies.mystackoverflowapi.domains;

import com.neoflies.mystackoverflowapi.enums.LoginProvider;
import com.neoflies.mystackoverflowapi.exceptions.OAuth2AuthenticationProcessingException;

import java.util.Map;

public class OAuth2UserInfoFactory {
  public static OAuth2UserInfo getOAuth2UserInfo(String registrationId, Map<String, Object> attributes) {
    if (registrationId.equalsIgnoreCase(LoginProvider.GOOGLE.toString())) {
      return new GoogleOAuth2UserInfo(attributes);
    } else if (registrationId.equalsIgnoreCase(LoginProvider.FACEBOOK.toString())) {
      return new FacebookOAuth2UserInfo(attributes);
    } else {
      throw new OAuth2AuthenticationProcessingException("common/service-provider-not-supported", String.format("Login with %s is not supported", registrationId));
    }
  }
}
