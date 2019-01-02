package gov.ca.cwds.rest.resources;

import static gov.ca.cwds.rest.DoraConstants.SYSTEM_INFORMATION;

import java.io.InputStream;
import java.net.URL;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;

import gov.ca.cwds.dto.app.SystemInformationDto;
import gov.ca.cwds.rest.resources.system.AbstractSystemInformationResource;
import io.dropwizard.setup.Environment;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;


/**
 * A resource providing a basic information about the CWDS API.
 *
 * @author CWDS API Team
 */
@Api(value = SYSTEM_INFORMATION)
@Path(SYSTEM_INFORMATION)
@Produces(MediaType.APPLICATION_JSON)
public class SystemInformationResource extends AbstractSystemInformationResource {

  private static final String BUILD_VERSION = "Dora-Api-Version";
  private static final String BUILD_NUMBER = "Dora-Api-Build";
  private static final String N_A = "N/A";

  private static final Logger LOGGER = LoggerFactory.getLogger(SystemInformationResource.class);

  /**
   * Constructor
   *
   * @param applicationName The name of the application
   * @param version The version of the API
   */
  @Inject
  public SystemInformationResource(@Named("app.name") String applicationName,
      @Named("app.version") String version, Environment environment) {
    super(environment.healthChecks());
    super.applicationName = applicationName;
    final Attributes manifestProperties = getManifestProperties();
    String value = manifestProperties.getValue(BUILD_VERSION);
    super.version = StringUtils.isBlank(value) ? N_A : value;
    value = manifestProperties.getValue(BUILD_NUMBER);
    super.buildNumber = StringUtils.isBlank(value) ? N_A : value;
    super.gitCommitHash = N_A;
    super.systemHealthStatusStrategy = new DoraSystemHealthStatusStrategy();
  }

  /**
   * Get the name of the application.
   *
   * @return the application data
   */
  @GET
  @ApiResponses(value = {@ApiResponse(code = 401, message = "Not Authorized"),
      @ApiResponse(code = 404, message = "Not found"),
      @ApiResponse(code = 465, message = "CARES Service is not healthy")})
  @ApiOperation(value = "Returns System Information", response = SystemInformationDto.class)
  public Response get() {
    return super.buildResponse();
  }

  private Attributes getManifestProperties() {
    Attributes attributes = new Attributes();
    String resource = "/" + this.getClass().getName().replace('.', '/') + ".class";
    String fullPath = this.getClass().getResource(resource).toExternalForm();
    String archivePath = fullPath.substring(0, fullPath.length() - resource.length());
    if (archivePath.endsWith("\\WEB-INF\\classes") || archivePath.endsWith("/WEB-INF/classes")) {
      // Required for WAR files.
      archivePath = archivePath.substring(0, archivePath.length() - "/WEB-INF/classes".length());
    }
    try (InputStream input = new URL(archivePath + "/META-INF/MANIFEST.MF").openStream()) {
      attributes = new Manifest(input).getMainAttributes();
    } catch (Exception e) {
      LOGGER.error("Loading properties from MANIFEST failed! {}", e);
    }
    return attributes;
  }
}
