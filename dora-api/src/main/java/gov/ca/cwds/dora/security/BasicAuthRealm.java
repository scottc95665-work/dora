package gov.ca.cwds.dora.security;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.realm.AuthenticatingRealm;

/**
 * This realm authorization class looks important, so let's add a token (pun intended) Javadoc
 * comment.
 * 
 * @author CARES Development Team
 */
public class BasicAuthRealm extends AuthenticatingRealm {

  public static final String EXTERNAL_APP_PRINCIPAL = "EXTERNAL_APP";

  private String basicAuthUser;

  private String basicAuthPassword;

  @Override
  public String getName() {
    return "DoraBasicAuthRealm";
  }

  @Override
  public boolean supports(AuthenticationToken token) {
    return token instanceof UsernamePasswordToken;
  }

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) {
    if (basicAuthUser.equals(((UsernamePasswordToken) token).getUsername())) {
      return new SimpleAuthenticationInfo(EXTERNAL_APP_PRINCIPAL, basicAuthPassword,
          this.getName());
    } else {
      return null;
    }
  }

  public void setBasicAuthUser(String basicAuthUser) {
    this.basicAuthUser = basicAuthUser;
  }

  public void setBasicAuthPassword(String basicAuthPassword) {
    this.basicAuthPassword = basicAuthPassword;
  }

}
