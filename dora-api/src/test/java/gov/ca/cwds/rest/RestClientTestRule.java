package gov.ca.cwds.rest;

import static gov.ca.cwds.rest.SmokeTestUtils.DORA_URL_PROP;
import static gov.ca.cwds.rest.SmokeTestUtils.PERRY_URL_PROP;
import static gov.ca.cwds.rest.SmokeTestUtils.SMOKE_TEST_PASSWORD_ENV;
import static gov.ca.cwds.rest.SmokeTestUtils.SMOKE_TEST_USER_ENV;
import static gov.ca.cwds.rest.SmokeTestUtils.SMOKE_VERIFICATION_CODE_ENV;
import static gov.ca.cwds.rest.SmokeTestUtils.isDevAuthMode;
import static gov.ca.cwds.rest.SmokeTestUtils.isIntegrationAuthMode;
import static io.dropwizard.testing.FixtureHelpers.fixture;

import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import gov.ca.cwds.test.support.AuthParams;
import gov.ca.cwds.test.support.CognitoLoginAuthParams;
import gov.ca.cwds.test.support.CognitoTokenProvider;
import gov.ca.cwds.test.support.JsonIdentityAuthParams;
import gov.ca.cwds.test.support.PerryV2DevModeTokenProvider;
import gov.ca.cwds.test.support.TokenProvider;
import io.dropwizard.testing.junit.DropwizardAppRule;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;
import org.apache.commons.lang3.StringUtils;
import org.glassfish.jersey.client.ClientProperties;
import org.glassfish.jersey.client.JerseyClientBuilder;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author TPT-2
 */
public class RestClientTestRule implements TestRule {

  private static final Logger LOG = LoggerFactory.getLogger(RestClientTestRule.class);

  private static final AuthParams defaultAuthParams = new JsonIdentityAuthParams("{}");
  private static final String DEV_AUTH_MODE_PRINCIPAL = fixture(
      "security/default-login-principal.json");

  private final DropwizardAppRule<DoraConfiguration> dropWizardApplication;

  private Client client;

  RestClientTestRule(DropwizardAppRule<DoraConfiguration> dropWizardApplication) {
    this.dropWizardApplication = dropWizardApplication;
  }

  public WebTarget target(String pathInfo) {
    return target(pathInfo, defaultAuthParams);
  }

  public WebTarget target(String pathInfo, AuthParams authParams) {
    String restUrl = getUriString() + pathInfo;
    String token = extractToken(authParams);
    return client.target(restUrl).queryParam("token", token).register(new LoggingFilter());
  }

  String getUriString() {
    String serverUrlStr = System.getProperty(DORA_URL_PROP);
    return StringUtils.isEmpty(serverUrlStr) ?
        String.format("http://localhost:%s/", dropWizardApplication.getLocalPort()) : serverUrlStr;
  }

  private String extractToken(AuthParams authParams) {
    try {
      if (isDevAuthMode()) {
        TokenProvider tokenProvider = new PerryV2DevModeTokenProvider(client,
            System.getProperty(PERRY_URL_PROP),
            System.getProperty(PERRY_URL_PROP) + "/perry/login");
        return tokenProvider.doGetToken(new JsonIdentityAuthParams(DEV_AUTH_MODE_PRINCIPAL));
      } else if (isIntegrationAuthMode()) {
        String token = System.getProperty("token");
        if (StringUtils.isBlank(token)) {
          System.setProperty("token", "noTokenFound");
          token = new CognitoTokenProvider().doGetToken(getLoginParams());
          System.setProperty("token", token);
        }
        return token;
      } else {
        return "";
      }
    } catch (RuntimeException e) {
      LOG.error("Unable to extract token", e);
      return "";
    }
  }

  @Override
  public Statement apply(Statement statement, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {
        JerseyClientBuilder clientBuilder = new JerseyClientBuilder()
            .property(ClientProperties.CONNECT_TIMEOUT, 5000)
            .property(ClientProperties.READ_TIMEOUT, 20000)
            .hostnameVerifier((hostName, sslSession) -> {
              // Just ignore host verification for test purposes
              return true;
            });

        client = clientBuilder.build();

        // Trust All certificates for test purposes
        TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
          public X509Certificate[] getAcceptedIssuers() {
            return null;
          }

          public void checkClientTrusted(X509Certificate[] certs, String authType) {
          }

          public void checkServerTrusted(X509Certificate[] certs, String authType) {
          }
        }};

        client.getSslContext().init(null, trustAllCerts, new SecureRandom());
        client.register(new JacksonJsonProvider(dropWizardApplication.getObjectMapper()));
        statement.evaluate();
      }
    };
  }

  private static CognitoLoginAuthParams getLoginParams() {
    CognitoLoginAuthParams loginParams = new CognitoLoginAuthParams();
    loginParams.setUser(System.getenv(SMOKE_TEST_USER_ENV));
    loginParams.setPassword(System.getenv(SMOKE_TEST_PASSWORD_ENV));
    loginParams.setCode(System.getenv(SMOKE_VERIFICATION_CODE_ENV));
    loginParams.setUrl(System.getProperty(DORA_URL_PROP));
    return loginParams;
  }
}
