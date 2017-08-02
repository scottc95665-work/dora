package gov.ca.cwds.rest;

import io.dropwizard.testing.ResourceHelpers;
import io.dropwizard.testing.junit.DropwizardAppRule;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.ws.rs.client.Client;
import org.glassfish.jersey.client.JerseyClient;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;


/**
 * @author TPT-2
 */
public abstract class BaseDoraApplicationTest {

  private static final String configFile = "config/test-dora.yml";

  private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
      .ofPattern("yyyy-MM-dd HH:mm:ss");

  @BeforeClass
  public static void setUp() {
  }

  @ClassRule
  public static final DropwizardAppRule<DoraConfiguration> appRule =
      new DropwizardAppRule<DoraConfiguration>(DoraApplication.class,
          ResourceHelpers.resourceFilePath(configFile)) {

        @Override
        public Client client() {
          Client client = super.client();
          if (((JerseyClient) client).isClosed()) {
            client = clientBuilder().build();
          }
          return client;
        }
      };

  @Rule
  public RestClientTestRule clientTestRule = new RestClientTestRule(appRule);

  public String transformDTOtoJSON(Object o) throws Exception {
    return clientTestRule.getMapper().writeValueAsString(o);
  }

  public static LocalDateTime toLocalDateTime(String dateTime) {
    return LocalDateTime.parse(dateTime, DATE_TIME_FORMATTER);
  }

  @After
  public void tearDown() throws Exception {
  }

  private static boolean isIntegrationTestsRunning() {
    return System.getProperty("dora.url") != null;
  }
}
