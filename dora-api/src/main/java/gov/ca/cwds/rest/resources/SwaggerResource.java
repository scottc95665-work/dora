package gov.ca.cwds.rest.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;

import com.google.inject.Inject;

import gov.ca.cwds.rest.SwaggerConfiguration;
import gov.ca.cwds.rest.views.SwaggerView;
import io.swagger.annotations.Api;

/**
 * Interaction with Swagger.
 *
 * @author CWDS API Team
 */
@Api(value = "swagger", hidden = true)
@Path(value = "swagger")
@Produces(MediaType.TEXT_HTML)
public class SwaggerResource {

  private SwaggerConfiguration swaggerConfiguration;

  @Inject
  public SwaggerResource(SwaggerConfiguration swaggerConfiguration) {
    super();
    this.swaggerConfiguration = swaggerConfiguration;
  }

  @GET
  public SwaggerView get(@Context UriInfo uriInfo) {
    UriBuilder ub = uriInfo.getBaseUriBuilder();
    String swaggerjsonUrl = ub.path("swagger.json").build().toASCIIString();
    UriBuilder ub2 = uriInfo.getBaseUriBuilder();
    String callbackUrl = ub2.path("swagger").build().toASCIIString();
    if (swaggerConfiguration.isShowSwagger()) {
      return new SwaggerView(swaggerConfiguration, swaggerjsonUrl, callbackUrl);
    } else {
      throw new WebApplicationException(404);
    }
  }
}
