package com.domeni.kapita.security.jwt.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.core.io.Resource;

@ConfigurationProperties(prefix = "kapita.security.jwt")
public class KapitaJwtSecurityProperties {

  private String issuer;
  private String audience;
  private Resource publicKeyLocation;
  private String jwkSetUri;

  public String getIssuer() {
    return issuer;
  }

  public void setIssuer(String issuer) {
    this.issuer = issuer;
  }

  public String getAudience() {
    return audience;
  }

  public void setAudience(String audience) {
    this.audience = audience;
  }

  public Resource getPublicKeyLocation() {
    return publicKeyLocation;
  }

  public void setPublicKeyLocation(Resource publicKeyLocation) {
    this.publicKeyLocation = publicKeyLocation;
  }

  public String getJwkSetUri() {
    return jwkSetUri;
  }

  public void setJwkSetUri(String jwkSetUri) {
    this.jwkSetUri = jwkSetUri;
  }
}
