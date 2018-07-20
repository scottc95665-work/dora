package gov.ca.cwds.rest.resources;

import static gov.ca.cwds.dora.DoraUtils.escapeCRLF;
import static gov.ca.cwds.rest.DoraConstants.RESOURCE_ELASTICSEARCH_INDEX_QUERY;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryResponse;
import gov.ca.cwds.rest.services.es.IndexQueryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;
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


/**
 * A resource providing a RESTful interface for Elasticsearch Query.
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

  private IndexQueryService indexQueryService;

  /**
   * Constructor
   *
   * @param indexQueryService The IndexQueryService to handle search requests.
   */
  @Inject
  public IndexQueryResource(IndexQueryService indexQueryService) {
    this.indexQueryService = indexQueryService;
  }

  /**
   * Endpoint for Query Search.
   *
   * @param index {@link IndexQueryRequest}
   * @param type Elasticsearch document type
   * @param req JSON {@link IndexQueryRequest}
   * @return web service response
   */
  @POST
  @Timed
  @Path("/{index}/{type}/_search")
  @ApiResponses(value = {@ApiResponse(code = 400, message = "Unable to process JSON"),
      @ApiResponse(code = 401, message = "Not Authorized"),
      @ApiResponse(code = 406, message = "Accept Header not supported")})
  @ApiOperation(value = "Query given ElasticSearch index and type on given search terms", response = JSONObject.class)
  @Consumes(value = MediaType.APPLICATION_JSON)
  public Response searchIndex(
      @PathParam("index")
      @ApiParam(required = true, name = "index", value = "The index of the search", example = "facilities")
          String index,
      @PathParam("type")
      @ApiParam(required = true, name = "type", value = "The document type", example = "facility")
          String type,
      @Valid
      @ApiParam(required = true, examples = @Example(@ExampleProperty(mediaType = MediaType.APPLICATION_JSON, value = "{\"query\":{\"match_all\":{}}}")))
          Object req
  ) {
    long startTime = System.currentTimeMillis();

    logRequestParams(index, type, req);

    IndexQueryRequest indexQueryRequest = new IndexQueryRequest(index, type, req);
    IndexQueryResponse indexQueryResponse = indexQueryService.handleRequest(indexQueryRequest);

    LOGGER.info("Index search total time: {}", (System.currentTimeMillis() - startTime));

    return indexQueryResponse == null ? null
        : Response.status(Response.Status.OK).entity(indexQueryResponse.getSearchResults())
            .build();
  }

  private void logRequestParams(String index, String type, Object req) {
    String idxStr = escapeCRLF(index);
    String typeStr = escapeCRLF(type);
    String regStr = escapeCRLF((null != req) ? req.toString() : null);
    LOGGER.info("index: {}. type: {} query: {}", idxStr, typeStr, regStr);
  }
}
