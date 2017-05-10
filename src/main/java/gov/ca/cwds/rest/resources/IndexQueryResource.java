package gov.ca.cwds.rest.resources;

import static gov.ca.cwds.rest.DoraConstants.RESOURCE_ELASTICSEARCH_INDEX_QUERY;

import gov.ca.cwds.inject.IndexQueryServiceResource;
import gov.ca.cwds.rest.api.ApiException;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryResponse;
import gov.ca.cwds.rest.services.es.IndexQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import javax.validation.Valid;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

/**
 * A resource providing a RESTful interface for Elasticsearch Query. It delegates functions to
 * {@link SimpleResourceDelegate}. It decorates the {@link SimpleResourceService} not in
 * functionality but with @see <a href=
 * "https://github.com/swagger-api/swagger-core/wiki/Annotations-1.5.X">Swagger Annotations</a> and
 * <a href="https://jersey.java.net/documentation/latest/user-guide.html#jaxrs-resources">Jersey
 * Annotations</a>
 *
 * @author CWDS API Team
 */
@Api(value = RESOURCE_ELASTICSEARCH_INDEX_QUERY, tags = {RESOURCE_ELASTICSEARCH_INDEX_QUERY})
@Path(value = RESOURCE_ELASTICSEARCH_INDEX_QUERY)
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class IndexQueryResource {

    /**
     * Logger for this class.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(IndexQueryResource.class);


    private SimpleResourceDelegate<String, IndexQueryRequest, IndexQueryResponse, IndexQueryService> resourceDelegate;

    /**
     * Constructor
     *
     * @param resourceDelegate The resourceDelegate to delegate to.
     */
    @Inject
    public IndexQueryResource(
            @IndexQueryServiceResource SimpleResourceDelegate<String, IndexQueryRequest, IndexQueryResponse, IndexQueryService> resourceDelegate) {
        this.resourceDelegate = resourceDelegate;
    }

    /**
     * Endpoint for Query Search.
     *
     * @param index {@link IndexQueryRequest}
     * @param type Elasticsearch document type
     * @param req   JSON {@link IndexQueryRequest}
     * @return web service response
     */
    @POST
    @Path("/{index}/{type}/_search")
    @ApiResponses(value = {@ApiResponse(code = 400, message = "Unable to process JSON"),
            @ApiResponse(code = 401, message = "Not Authorized"),
            @ApiResponse(code = 406, message = "Accept Header not supported")})
    @ApiOperation(value = "Query given ElasticSearch index and type on given search terms", response = JSONObject.class)
    @Consumes(value = MediaType.APPLICATION_JSON)
    public Response searchIndex(
            @PathParam("index") @ApiParam(required = true, name = "index", value = "The index of the search") String index,
            @PathParam("type") @ApiParam(required = true, name = "type", value = "The document type") String type,
            @Valid @ApiParam(required = true) Object req
    ) {
        try {
            IndexQueryRequest indexQueryRequest = new IndexQueryRequest(index, type, req);
            IndexQueryResponse indexQueryResponse =
                    (IndexQueryResponse) resourceDelegate.handle(indexQueryRequest).getEntity();

            return indexQueryResponse == null ? null
                    : Response.status(Response.Status.OK).entity(indexQueryResponse.getSearchResults()).build();
        } catch (RuntimeException e) {
            LOGGER.error("Query ERROR: {}", e.getMessage(), e);
            throw new ApiException("Query ERROR: " + e.getMessage(), e);
        }
    }

}
