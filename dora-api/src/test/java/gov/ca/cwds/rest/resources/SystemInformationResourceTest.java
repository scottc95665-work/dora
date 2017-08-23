package gov.ca.cwds.rest.resources;

import static gov.ca.cwds.rest.DoraConstants.SYSTEM_INFORMATION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import gov.ca.cwds.dora.dto.SystemInformationDTO;
import gov.ca.cwds.rest.BaseDoraApplicationTest;
import io.dropwizard.testing.junit.ResourceTestRule;
import javax.ws.rs.core.MediaType;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class SystemInformationResourceTest extends BaseDoraApplicationTest {

  private static final String APP_NAME = "my app";
  private static final String VERSION = "1.0.0";

  @After
  public void ensureServiceLocatorPopulated() {
    JerseyGuiceUtils.reset();
  }

  @ClassRule
  public static JerseyGuiceRule rule = new JerseyGuiceRule();

  @ClassRule
  public static final ResourceTestRule resources =
      ResourceTestRule.builder().addResource(new SystemInformationResource(APP_NAME, VERSION))
          .build();

  @Before
  public void setup() {
  }

  @Test
  public void applicationGetReturns200() {
    assertThat(resources.client().target("/" + SYSTEM_INFORMATION).request()
        .accept(MediaType.APPLICATION_JSON).get().getStatus(), is(equalTo(200)));
  }

  @Test
  public void applicationGetReturnsCorrectName() {
    assertThat(resources.client().target("/" + SYSTEM_INFORMATION).request()
            .accept(MediaType.APPLICATION_JSON).get().readEntity(String.class),
        containsString(APP_NAME));
  }

  @Test
  public void applicationGetReturnsCorrectVersion() {
    assertThat(resources.client().target("/" + SYSTEM_INFORMATION).request()
            .accept(MediaType.APPLICATION_JSON).get().readEntity(String.class),
        containsString(VERSION));
  }

  @Test
  public void applicationGetReturnsV1JsonContentType() {
    assertThat(resources.client().target("/" + SYSTEM_INFORMATION).request()
            .accept(MediaType.APPLICATION_JSON).get().getMediaType().toString(),
        is(equalTo(MediaType.APPLICATION_JSON)));
  }

  @Test
  public void testSystemInformationGet() throws Exception {
    SystemInformationDTO systemInformationDTO = clientTestRule.target(SYSTEM_INFORMATION)
        .request(MediaType.APPLICATION_JSON).get(SystemInformationDTO.class);
    assertThat(systemInformationDTO.getApplicationName(), is(equalTo("CWDS Dora TEST")));
    assertThat(systemInformationDTO.getVersion(), is(equalTo("version")));
  }
}
