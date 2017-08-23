package gov.ca.cwds.rest.resources;

import static gov.ca.cwds.rest.DoraConstants.SYSTEM_INFORMATION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import gov.ca.cwds.dora.dto.HealthCheckResultDTO;
import gov.ca.cwds.dora.dto.SystemInformationDTO;
import javax.ws.rs.core.MediaType;
import org.junit.Test;

/**
 * @author CWDS TPT-2
 */
public class DoraSmokeTest extends SystemInformationResourceTest {
  @Test
  public void testHealthy() throws Exception {
    super.testSystemInformationGet();

    SystemInformationDTO systemInformationDTO = clientTestRule.target(SYSTEM_INFORMATION)
        .request(MediaType.APPLICATION_JSON).get(SystemInformationDTO.class);

    boolean isAllHealthy = systemInformationDTO.getHealthCheckResults().values().stream()
        .allMatch(HealthCheckResultDTO::isHealthy);
    assertThat(isAllHealthy, is(true));
  }
}
