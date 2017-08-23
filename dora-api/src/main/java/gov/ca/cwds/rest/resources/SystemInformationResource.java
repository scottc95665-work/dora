package gov.ca.cwds.rest.resources;

import static gov.ca.cwds.rest.DoraConstants.SYSTEM_INFORMATION;

import gov.ca.cwds.dora.dto.SystemInformationDTO;
import io.swagger.annotations.ApiOperation;
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

  /**
   * Constructor
   *
   * @param applicationName The name of the application
   * @param version The version of the API
   */
  @Inject
  public SystemInformationResource(@Named("app.name") String applicationName,
      @Named("app.version") String version) {
    this.applicationName = applicationName;
    this.version = version;
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
    return systemInformationDTO;
  }
}
