package gov.ca.cwds.rest.resources;

import static gov.ca.cwds.rest.DoraConstants.SYSTEM_INFORMATION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.SortedMap;

import javax.ws.rs.core.MediaType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;

import gov.ca.cwds.dora.dto.HealthCheckResultDTO;
import gov.ca.cwds.dora.dto.SystemInformationDTO;
import gov.ca.cwds.rest.BaseDoraApplicationTest;

/**
 * @author CWDS TPT-2
 */
@SuppressWarnings("javadoc")
public class SystemInformationResourceTest extends BaseDoraApplicationTest {

  private static final String BUILD_NUMBER = "1";

  @After
  public void ensureServiceLocatorPopulated() {
    JerseyGuiceUtils.reset();
  }

  @Before
  public void setup() {}

  @Test
  public void testApplicationGetReturns200() {
    assertThat(clientTestRule.target(SYSTEM_INFORMATION).request()
        .accept(MediaType.APPLICATION_JSON).get().getStatus(), is(equalTo(200)));
  }

  @Test
  public void testApplicationGetReturnsV1JsonContentType() {
    assertThat(clientTestRule.target(SYSTEM_INFORMATION).request()
        .accept(MediaType.APPLICATION_JSON).get().getMediaType().toString(),
        is(equalTo(MediaType.APPLICATION_JSON)));
  }

  @Test
  public void testSystemInformationGet() {
    SystemInformationDTO systemInformationDTO = clientTestRule.target(SYSTEM_INFORMATION)
        .request(MediaType.APPLICATION_JSON).get(SystemInformationDTO.class);

    assertThat(systemInformationDTO.getApplicationName(), is(equalTo("CWDS Dora")));
    assertThat(systemInformationDTO.getVersion(), is(notNullValue()));
  }

  @Test
  public void testHealthChecksResults() {
    SystemInformationDTO systemInformationDTO = clientTestRule.target(SYSTEM_INFORMATION)
        .request(MediaType.APPLICATION_JSON).get(SystemInformationDTO.class);

    SortedMap<String, HealthCheckResultDTO> healthCheckResults =
        systemInformationDTO.getHealthCheckResults();

    assertThat(healthCheckResults, is(notNullValue()));
    System.out.println(healthCheckResults.values().size());
    assertThat(healthCheckResults.values().size(), is(equalTo(14)));

    assertHealthCheckResult(healthCheckResults.get("deadlocks"), true);
    assertHealthCheckResult(healthCheckResults.get("dora-es-config"), true);

    // there is no Elasticsearch server available while Unit Tests
    assertHealthCheckResult(healthCheckResults.get("elasticsearch-status"), true);
    assertHealthCheckResult(healthCheckResults.get("elasticsearch-plugin-x-pack"), true);
    assertHealthCheckResult(healthCheckResults.get("elasticsearch-plugin-analysis-phonetic"), true);
    // TODO:  re-enable this when the licenses are updated.
    //assertHealthCheckResult(healthCheckResults.get("elasticsearch-index-people-summary"), true);
  }

  private void assertHealthCheckResult(HealthCheckResultDTO healthCheckResultDTO,
      boolean isHealthy) {
    assertNotNull(healthCheckResultDTO);
    assertEquals(healthCheckResultDTO.isHealthy(), isHealthy);
  }

  @Test
  public void applicationGetReturnsCorrectBuildNumber() {
    assertThat(clientTestRule.target(SYSTEM_INFORMATION).request().get().readEntity(String.class),
        containsString(BUILD_NUMBER));
  }
}
