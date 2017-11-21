package gov.ca.cwds.rest.resources;

import static gov.ca.cwds.rest.DoraConstants.SYSTEM_INFORMATION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import gov.ca.cwds.dora.dto.HealthCheckResultDTO;
import gov.ca.cwds.dora.dto.SystemInformationDTO;
import gov.ca.cwds.rest.BaseDoraApplicationTest;
import java.util.SortedMap;
import javax.ws.rs.core.MediaType;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author CWDS TPT-2
 */
@SuppressWarnings("javadoc")
public class SystemInformationResourceTest extends BaseDoraApplicationTest {

  @After
  public void ensureServiceLocatorPopulated() {
    JerseyGuiceUtils.reset();
  }

  @Before
  public void setup() {
  }

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
  public void testSystemInformationGet() throws Exception {
    SystemInformationDTO systemInformationDTO = clientTestRule.target(SYSTEM_INFORMATION)
        .request(MediaType.APPLICATION_JSON).get(SystemInformationDTO.class);

    assertThat(systemInformationDTO.getApplicationName(), is(equalTo("CWDS Dora")));
    assertThat(systemInformationDTO.getVersion(), is(notNullValue()));
  }

  @Test
  public void testHealthChecksResults() throws Exception {
    SystemInformationDTO systemInformationDTO = clientTestRule.target(SYSTEM_INFORMATION)
        .request(MediaType.APPLICATION_JSON).get(SystemInformationDTO.class);

    SortedMap<String, HealthCheckResultDTO> healthCheckResults = systemInformationDTO
        .getHealthCheckResults();

    assertThat(healthCheckResults, is(notNullValue()));
    assertThat(healthCheckResults.values().size(), is(equalTo(5)));

    assertHealthCheckResult(healthCheckResults.get("deadlocks"), true);
    assertHealthCheckResult(healthCheckResults.get("dora-es-config"), true);

    // there is no Elasticsearch server available while Unit Tests
    assertHealthCheckResult(healthCheckResults.get("elasticsearch-status"), false);
    assertHealthCheckResult(healthCheckResults.get("elasticsearch-plugin-x-pack"), false);
    assertHealthCheckResult(healthCheckResults.get("elasticsearch-plugin-analysis-phonetic"),
        false);
  }

  private void assertHealthCheckResult(HealthCheckResultDTO healthCheckResultDTO,
      boolean isHealthy) {
    assertThat(healthCheckResultDTO, is(notNullValue()));
    assertThat(healthCheckResultDTO.isHealthy(), is(isHealthy));
  }
}
