package gov.ca.cwds.rest.resources;

import static gov.ca.cwds.rest.DoraConstants.SYSTEM_INFORMATION;

import com.codahale.metrics.health.HealthCheck;
import com.codahale.metrics.health.HealthCheck.Result;
import gov.ca.cwds.dora.dto.HealthCheckResultDTO;
import gov.ca.cwds.dora.dto.SystemInformationDTO;
import gov.ca.cwds.rest.api.ApiException;
import io.dropwizard.setup.Environment;
import io.swagger.annotations.ApiOperation;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;
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
@Api(value = SYSTEM_INFORMATION)
@Path(SYSTEM_INFORMATION)
@Produces(MediaType.APPLICATION_JSON)
public class SystemInformationResource {

  private static final String VERSION_PROPERTIES_FILE = "system-information.properties";
  private static final String BUILD_NUMBER = "build.number";

  private String applicationName;
  private String version;
  private Environment environment;
  private String buildNumber;

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
    Properties versionProperties = getVersionProperties();
    this.buildNumber = versionProperties.getProperty(BUILD_NUMBER);
  }

  private Properties getVersionProperties() {
    Properties versionProperties = new Properties();
    try {
      InputStream is = ClassLoader.getSystemResourceAsStream(VERSION_PROPERTIES_FILE);
      versionProperties.load(is);
    } catch (IOException e) {
      throw new ApiException("Can't read version.properties", e);
    }
    return versionProperties;
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
    systemInformationDTO.setBuildNumber(buildNumber);

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
