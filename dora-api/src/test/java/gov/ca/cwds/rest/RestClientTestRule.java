package gov.ca.cwds.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJsonProvider;
import io.dropwizard.testing.junit.DropwizardAppRule;
import java.net.URI;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
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

  private static final String DORA_URL = "dora.url";
  private final DropwizardAppRule<DoraConfiguration> dropWizardApplication;

  private Client client;

  private ObjectMapper mapper;

  RestClientTestRule(DropwizardAppRule<DoraConfiguration> dropWizardApplication) {
    this.dropWizardApplication = dropWizardApplication;
  }

  public WebTarget target(String pathInfo) {
    String restUrl = getUriString() + pathInfo;
    WebTarget webTarget = client.target(restUrl);
    webTarget.register(new LoggingFilter());
    return webTarget;
  }

  String getUriString() {
    String serverUrlStr = System.getProperty(DORA_URL);
    if (StringUtils.isEmpty(serverUrlStr)) {
      serverUrlStr = composeUriString();
    }
    return serverUrlStr;
  }

  protected URI getServerUrl() {
    return dropWizardApplication.getEnvironment().getApplicationContext().getServer().getURI();
  }

  private String composeUriString() {
    return String.format("http://localhost:%s/", dropWizardApplication.getLocalPort());
  }

  ObjectMapper getMapper() {
    return mapper;
  }

  @Override
  public Statement apply(Statement statement, Description description) {
    return new Statement() {
      @Override
      public void evaluate() throws Throwable {

        JerseyClientBuilder clientBuilder = new JerseyClientBuilder()
            .property(ClientProperties.CONNECT_TIMEOUT, 5000)
            .property(ClientProperties.READ_TIMEOUT, 20000)
            .hostnameVerifier(new HostnameVerifier() {
              @Override
              public boolean verify(String hostName, SSLSession sslSession) {
                // Just ignore host verification for test purposes
                return true;
              }
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

        mapper = dropWizardApplication.getObjectMapper();
        client.register(new JacksonJsonProvider(mapper));
        statement.evaluate();
      }
    };
  }
}
