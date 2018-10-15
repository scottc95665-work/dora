package gov.ca.cwds.rest.resources;

import static gov.ca.cwds.rest.DoraConstants.SYSTEM_INFORMATION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import gov.ca.cwds.dto.app.HealthCheckResultDto;
import gov.ca.cwds.dto.app.SystemInformationDto;
import gov.ca.cwds.rest.BaseDoraApplicationTest;
import javax.ws.rs.core.MediaType;
import org.junit.Test;

/**
 * @author CWDS TPT-2
 */
public class DoraSmokeTest extends BaseDoraApplicationTest {

  @Test
  public void testApplicationGetReturns200() {
    assertThat(clientTestRule.target(SYSTEM_INFORMATION).request()
        .accept(MediaType.APPLICATION_JSON).get().getStatus(), is(equalTo(200)));
  }

  @Test
  public void testSystemInformationGet() {
    SystemInformationDto systemInformationDto = clientTestRule.target(SYSTEM_INFORMATION)
        .request(MediaType.APPLICATION_JSON).get(SystemInformationDto.class);

    assertThat(systemInformationDto.getApplicationName(), is(equalTo("CWDS Dora")));
    assertThat(systemInformationDto.getVersion(), is(notNullValue()));
  }

  @Test
  public void testHealthy() {
    SystemInformationDto systemInformationDTO = clientTestRule.target(SYSTEM_INFORMATION)
        .request(MediaType.APPLICATION_JSON).get(SystemInformationDto.class);

    assertThat(systemInformationDTO.getHealthCheckResults(), is(notNullValue()));
    assertThat(systemInformationDTO.getHealthCheckResults().values().size(), is(greaterThan(0)));

    boolean isAllHealthy = systemInformationDTO.getHealthCheckResults().values().stream()
        .allMatch(HealthCheckResultDto::isHealthy);
    assertThat(isAllHealthy, is(true));
  }
}
