package gov.ca.cwds.xpack.realm.perry;

import java.util.Objects;
import org.elasticsearch.xpack.security.authc.AuthenticationToken;

/**
 * @author CWDS TPT-2
 */
class PerryToken implements AuthenticationToken {

  private String principal;

  PerryToken(String principal) {
    this.principal = principal;
  }

  @Override
  public String principal() {
    return principal;
  }

  @Override
  public Object credentials() {
    return null;
  }

  @Override
  public void clearCredentials() {
    // nothing to do
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    PerryToken that = (PerryToken) o;
    return Objects.equals(principal, that.principal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(principal);
  }
}
