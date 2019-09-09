package gov.ca.cwds.rest.resources;

import static gov.ca.cwds.rest.DoraConstants.SYSTEM_INFORMATION;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.ca.cwds.dto.app.SystemInformationDto;
import gov.ca.cwds.rest.BaseDoraApplicationTest;

/**
 * @author CWDS TPT-2
 */
public class DoraSmokeTest extends BaseDoraApplicationTest {

  private static final Logger LOGGER = LoggerFactory.getLogger(DoraSmokeTest.class);
  private static final String FACILITIES_COUNT_PATH = "dora/facilities/facility/_count";

  @Test
  public void testApplicationGetReturns200() {
    final WebTarget target = clientTestRule.target(SYSTEM_INFORMATION);
    LOGGER.info("Smoke Test target: " + target.getUri().toString());

    assertThat(target.request().accept(MediaType.APPLICATION_JSON).get().getStatus(),
        is(equalTo(200)));
  }

  @Test
  public void testSystemInformationGet() {
    final WebTarget target = clientTestRule.target(SYSTEM_INFORMATION);
    LOGGER.info("Smoke Test target: " + target.getUri().toString());
    final SystemInformationDto systemInformationDto =
        target.request(MediaType.APPLICATION_JSON).get(SystemInformationDto.class);

    assertThat(systemInformationDto.getApplicationName(), is(equalTo("CWDS Dora")));
    assertThat(systemInformationDto.getVersion(), is(notNullValue()));
  }

  @Test
  public void testHealthy() {
    final WebTarget target = clientTestRule.target(SYSTEM_INFORMATION);
    LOGGER.info("Smoke Test target: " + target.getUri().toString());
    SystemInformationDto systemInformationDTO =
        target.request(MediaType.APPLICATION_JSON).get(SystemInformationDto.class);

    assertThat(systemInformationDTO, is(notNullValue()));
    assertThat(systemInformationDTO.isHealthStatus(), is(true));
  }

  @Test
  @Ignore
  public void testAuthorizedToRequestIndexCount() throws Exception {
    final WebTarget target = clientTestRule.target(FACILITIES_COUNT_PATH);
    LOGGER.info("Smoke Test target: " + target.getUri().toString());
    final Response actualResponse = target.request(MediaType.APPLICATION_JSON)
        // .post(BLANK_JSON_ENTITY)
        .post(Entity.json(
            "{\"query\":{\"bool\":{\"should\":[{\"bool\":{\"must\":[{\"query_string\":{\"default_field\":\"name\",\"query\":\"*Bay* *Area* *Crisis* *Nursery*\"}},{\"query_string\":{\"query\":\"*1506* *Menocino* *Drive* *Concord* *CA*\",\"fields\":[\"full_residential_address\",\"full_mailing_address\"]}}]}}]}}}"));

    assertThat(actualResponse.getStatus(), anyOf(is(200), is(404)));
  }

}
