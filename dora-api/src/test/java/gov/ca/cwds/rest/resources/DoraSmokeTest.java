package gov.ca.cwds.rest.resources;

import static gov.ca.cwds.rest.DoraConstants.SYSTEM_INFORMATION;
import static gov.ca.cwds.rest.SmokeTestUtils.BLANK_JSON_ENTITY;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import gov.ca.cwds.dto.app.HealthCheckResultDto;
import gov.ca.cwds.dto.app.SystemInformationDto;
import gov.ca.cwds.rest.BaseDoraApplicationTest;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author CWDS TPT-2
 */
public class DoraSmokeTest extends BaseDoraApplicationTest {
  private static final Logger LOGGER = LoggerFactory.getLogger(DoraSmokeTest.class);
  private static final String FACILITIES_COUNT_PATH = "dora/facilities/facility/_count";

  @Test
  public void testApplicationGetReturns200() {
    WebTarget target = clientTestRule.target(SYSTEM_INFORMATION);
    LOGGER.info("Smoke Test target: " + target.getUri().toString());
    
    assertThat(target.request()
        .accept(MediaType.APPLICATION_JSON).get().getStatus(), is(equalTo(200)));
  }

  @Test
  public void testSystemInformationGet() {
    WebTarget target = clientTestRule.target(SYSTEM_INFORMATION);
    LOGGER.info("Smoke Test target: " + target.getUri().toString());
    SystemInformationDto systemInformationDto = target.request(MediaType.APPLICATION_JSON)
        .get(SystemInformationDto.class);

    assertThat(systemInformationDto.getApplicationName(), is(equalTo("CWDS Dora")));
    assertThat(systemInformationDto.getVersion(), is(notNullValue()));
  }

  @Test
  public void testHealthy() {
    WebTarget target = clientTestRule.target(SYSTEM_INFORMATION);
    LOGGER.info("Smoke Test target: " + target.getUri().toString());
    SystemInformationDto systemInformationDTO = target.request(MediaType.APPLICATION_JSON)
        .get(SystemInformationDto.class);

    assertThat(systemInformationDTO.getHealthCheckResults(), is(notNullValue()));
    assertThat(systemInformationDTO.getHealthCheckResults().values().size(), is(greaterThan(0)));

    boolean isAllHealthy = systemInformationDTO.getHealthCheckResults().values().stream()
        .allMatch(HealthCheckResultDto::isHealthy);
    assertThat(isAllHealthy, is(true));
  }

  @Test
  public void testAuthorizedToRequestIndexCount() {
    WebTarget target = clientTestRule.target(FACILITIES_COUNT_PATH);
    LOGGER.info("Smoke Test target: " + target.getUri().toString());
    Response actualResponse = target.request(MediaType.APPLICATION_JSON).post(BLANK_JSON_ENTITY);
    
    assertThat(actualResponse.getStatus(), anyOf(is(200), is(404)));
  }
}
