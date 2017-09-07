package gov.ca.cwds.rest.resources;

import static gov.ca.cwds.rest.DoraConstants.RESOURCE_ELASTICSEARCH_INDEX_QUERY;
import static gov.ca.cwds.rest.DoraConstants.SYSTEM_INFORMATION;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheck.Result;
import gov.ca.cwds.dora.dto.HealthCheckResultDTO;
import gov.ca.cwds.dora.dto.SystemInformationDTO;
import io.dropwizard.setup.Environment;
import io.swagger.annotations.ApiOperation;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import io.swagger.annotations.Api;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


/**
 * A resource providing a basic information about the CWDS API.
 *
 * @author CWDS API Team
 */
@Api(value = SYSTEM_INFORMATION, hidden = true)
@Path(SYSTEM_INFORMATION)
@Produces(MediaType.APPLICATION_JSON)
public class SystemInformationResource {

  private String applicationName;
  private String version;
  private Environment environment;

  /**
   * Constructor
   *
   * @param applicationName The name of the application
   * @param version The version of the API
   */
  @Inject
  public SystemInformationResource(@Named("app.name") String applicationName,
      @Named("app.version") String version, Environment environment) {
    this.applicationName = applicationName;
    this.version = version;
    this.environment = environment;
  }

  /**
   * Get the name of the application.
   *
   * @return the application data
   */
  @GET
  @ApiOperation(value = "Returns System Information", response = SystemInformationDTO.class)
  public SystemInformationDTO get() {
    SystemInformationDTO systemInformationDTO = new SystemInformationDTO();
    systemInformationDTO.setApplicationName(applicationName);
    systemInformationDTO.setVersion(version);

    SortedMap<String, HealthCheckResultDTO> healthCheckResults = new TreeMap<>();
    SortedMap<String, Result> healthChecks = environment.healthChecks().runHealthChecks();
    for(Entry<String, Result> entry : healthChecks.entrySet()) {
      healthCheckResults.put(entry.getKey(), getHealthCheckResultDTO(entry.getValue()));
    }
    systemInformationDTO.setHealthCheckResults(healthCheckResults);

    return systemInformationDTO;
  }

  private HealthCheckResultDTO getHealthCheckResultDTO(HealthCheck.Result result) {
    HealthCheckResultDTO healthCheckResultDTO = new HealthCheckResultDTO();
    healthCheckResultDTO.setResult(result);
    return healthCheckResultDTO;
  }
}
