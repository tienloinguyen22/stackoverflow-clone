package com.neoflies.mystackoverflowapi.utils;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app")
@Getter
@Setter
public class ApplicationProperties {
  private final Auth auth = new Auth();
  private final Aws aws = new Aws();

  @Getter
  @Setter
  public static class Auth {
    private String tokenSecret;
    private Integer tokenExpires;
  }

  @Getter
  @Setter
  public static class Aws {
    private String endPointUrl;
    private String bucketName;
    private String accessKey;
    private String secretKey;
  }
}
