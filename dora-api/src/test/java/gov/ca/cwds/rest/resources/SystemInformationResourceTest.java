package gov.ca.cwds.rest.resources;

import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_AUDIT_EVENTS_INDEX;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_DEADLOCKS;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_ES_CONFIG;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_ES_STATUS;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PEOPLE_SUMMARY_INDEX;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_PHONETIC_PLUGIN;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_USERS_INDEX;
import static gov.ca.cwds.rest.DoraConstants.HealthCheck.HC_X_PACK_PLUGIN;
import static gov.ca.cwds.rest.DoraConstants.SYSTEM_INFORMATION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import gov.ca.cwds.dto.app.HealthCheckResultDto;
import gov.ca.cwds.dto.app.SystemInformationDto;
import java.util.SortedMap;

import javax.ws.rs.core.MediaType;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;

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
    SystemInformationDto systemInformationDto = clientTestRule.target(SYSTEM_INFORMATION)
        .request(MediaType.APPLICATION_JSON).get(SystemInformationDto.class);

    assertThat(systemInformationDto.getApplicationName(), is(equalTo("CWDS Dora")));
    assertThat(systemInformationDto.getVersion(), is(notNullValue()));
  }

  @Test
  public void testHealthChecksResults() {
    SystemInformationDto systemInformationDto = clientTestRule.target(SYSTEM_INFORMATION)
        .request(MediaType.APPLICATION_JSON).get(SystemInformationDto.class);

    SortedMap<String, HealthCheckResultDto> healthCheckResults =
        systemInformationDto.getHealthCheckResults();

    assertThat(healthCheckResults, is(notNullValue()));
    System.out.println(healthCheckResults.values().size());
    assertThat(healthCheckResults.values().size(), is(equalTo(20)));

    assertHealthCheckResult(healthCheckResults.get(HC_DEADLOCKS), true);
    assertHealthCheckResult(healthCheckResults.get(HC_ES_CONFIG), true);

    // there is no Elasticsearch server available while Unit Tests
    assertHealthCheckResult(healthCheckResults.get(HC_ES_STATUS), true);
    assertHealthCheckResult(healthCheckResults.get(HC_X_PACK_PLUGIN), true);
    assertHealthCheckResult(healthCheckResults.get(HC_PHONETIC_PLUGIN), true);
    assertHealthCheckResult(healthCheckResults.get(HC_PEOPLE_SUMMARY_INDEX), true);
    assertHealthCheckResult(healthCheckResults.get(HC_AUDIT_EVENTS_INDEX), true);
    assertHealthCheckResult(healthCheckResults.get(HC_USERS_INDEX), true);
  }

  private void assertHealthCheckResult(HealthCheckResultDto healthCheckResultDto,
      boolean isHealthy) {
    assertNotNull(healthCheckResultDto);
    assertEquals(healthCheckResultDto.isHealthy(), isHealthy);
  }

  @Test
  public void applicationGetReturnsCorrectBuildNumber() {
    assertThat(clientTestRule.target(SYSTEM_INFORMATION).request().get().readEntity(String.class),
        containsString(BUILD_NUMBER));
  }
}
