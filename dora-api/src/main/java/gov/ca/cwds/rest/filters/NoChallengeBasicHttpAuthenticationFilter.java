package gov.ca.cwds.rest.filters;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.shiro.web.filter.authc.BasicHttpAuthenticationFilter;

public class NoChallengeBasicHttpAuthenticationFilter extends BasicHttpAuthenticationFilter {

  @Override
  protected boolean onAccessDenied(ServletRequest request, ServletResponse response)
      throws Exception {
    if (isLoginAttempt(request, response)) {
      executeLogin(request, response);
    }
    return true;
  }

}
