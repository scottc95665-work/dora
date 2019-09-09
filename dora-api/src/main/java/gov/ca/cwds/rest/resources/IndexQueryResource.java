package gov.ca.cwds.rest.resources;

import static gov.ca.cwds.dora.DoraUtils.escapeCRLF;
import static gov.ca.cwds.rest.DoraConstants.RESOURCE_ELASTICSEARCH_INDEX_QUERY;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.NotBlank;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;

import gov.ca.cwds.dora.tracelog.DoraTraceLogService;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest.IndexQueryRequestBuilder;
import gov.ca.cwds.rest.api.domain.es.IndexQueryResponse;
import gov.ca.cwds.rest.filters.RequestExecutionContext;
import gov.ca.cwds.rest.services.es.IndexQueryService;
import gov.ca.cwds.rest.validation.ValidJson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.swagger.annotations.Example;
import io.swagger.annotations.ExampleProperty;


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

  private static final Logger LOGGER = LoggerFactory.getLogger(IndexQueryResource.class);
  public static final String DFS_QUERY_THEN_FETCH_QUERY_PARAM = "dfsQueryThenFetch";
  public static final String SEARCH_TYPE_PARAM = "search_type";
  public static final String DFS_QUERY_THEN_FETCH = "dfs_query_then_fetch";

  private IndexQueryService indexQueryService;
  private DoraTraceLogService doraTraceLogService;

  @Inject
  public IndexQueryResource(IndexQueryService indexQueryService,
      DoraTraceLogService doraTraceLogService) {
    this.indexQueryService = indexQueryService;
    this.doraTraceLogService = doraTraceLogService;
  }

  /**
   * Endpoint for Query Search.
   */
  @POST
  @Timed
  @Path("/{index}/{type}/_search")
  @ApiResponses(value = {@ApiResponse(code = 400, message = "Unable to process JSON"),
      @ApiResponse(code = 401, message = "Not Authorized"),
      @ApiResponse(code = 406, message = "Accept Header not supported")})
  @ApiOperation(value = "Query given Elasticsearch index and type on given search terms",
      response = JSONObject.class)
  @Consumes(value = MediaType.APPLICATION_JSON)
  public Response searchIndex(
      @PathParam("index") @ApiParam(required = true, name = "index",
          value = "The index of the search", example = "facilities") @NotBlank String index,
      @PathParam("type") @ApiParam(required = true, name = "type", value = "The document type",
          example = "facility") @NotBlank String documentType,
      @ApiParam(required = true,
          examples = @Example(@ExampleProperty(mediaType = MediaType.APPLICATION_JSON,
              value = "{\"query\":{\"match_all\":{}}}"))) @ValidJson String requestBody,
      @QueryParam("calling_application") @ApiParam(required = false, name = "calling_application",
          value = "Calling application", example = "Snapshot") String callingApplication,
      @QueryParam(DFS_QUERY_THEN_FETCH_QUERY_PARAM) @DefaultValue("true") @ApiParam(
          required = false, name = "dfsQueryThenFetch", value = "Distributed Frequency Search",
          example = "true") boolean isDfsQueryThenFetch) {
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("index: {}, type: {}, isDfsQueryThenFetch: {}, callingApplication: {}, JSON: {}",
          escapeCRLF(index), escapeCRLF(documentType), isDfsQueryThenFetch, callingApplication,
          escapeCRLF(requestBody));
    }

    final String endpoint = String.format("/%s/%s/_search", index.trim(), documentType.trim());
    final IndexQueryRequestBuilder builder = new IndexQueryRequestBuilder().addEsEndpoint(endpoint)
        .addDocumentType(documentType).addRequestBody(requestBody).addHttpMethod(HttpMethod.POST);
    if (isDfsQueryThenFetch) {
      builder.addParameter(SEARCH_TYPE_PARAM, DFS_QUERY_THEN_FETCH);
    }

    // Get the current user. JUnit tests lack a request context and default to "anonymous".
    final RequestExecutionContext ctx = RequestExecutionContext.instance();
    final String userId = ctx != null ? ctx.getUserId() : "anonymous";

    // CANS-180: Trace Log: save Snapshot queries only.
    if (StringUtils.isNotBlank(callingApplication)
        && "snapshot".equalsIgnoreCase(callingApplication) && StringUtils.isNotBlank(index)
        && "people-summary".equals(index) && !"anonymous".equals(userId)) {
      doraTraceLogService.logSearchQuery(escapeCRLF(userId), escapeCRLF(index),
          escapeCRLF(requestBody));
    } else {
      LOGGER.debug("Not a Snapshot query. Don't save to Trace Log.");
    }

    return handleRequest(builder.build());
  }

  /**
   * Endpoint for Query Count.
   */
  @POST
  @Timed
  @Path("/{index}/{type}/_count")
  @ApiResponses(value = {@ApiResponse(code = 400, message = "Unable to process JSON"),
      @ApiResponse(code = 401, message = "Not Authorized"),
      @ApiResponse(code = 406, message = "Accept Header not supported")})
  @ApiOperation(
      value = "Number of matches given Elasticsearch index and type on given search terms",
      response = JSONObject.class)
  public Response getDocumentCount(
      @PathParam("index") @ApiParam(required = true, name = "index",
          value = "The index of the search", example = "facilities") @NotBlank String index,
      @PathParam("type") @ApiParam(required = true, name = "type", value = "The document type",
          example = "facility") @NotBlank String documentType,
      @ApiParam(required = true,
          examples = @Example(@ExampleProperty(mediaType = MediaType.APPLICATION_JSON,
              value = "{\"query\":{\"match_all\":{}}}"))) @ValidJson String requestBody) {
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("index: {} type: {} body: {}", escapeCRLF(index), escapeCRLF(documentType),
          escapeCRLF(requestBody));
    }

    final String endpoint = String.format("/%s/%s/_count", index.trim(), documentType.trim());
    final IndexQueryRequest request =
        new IndexQueryRequestBuilder().addEsEndpoint(endpoint).addDocumentType(documentType)
            .addRequestBody(requestBody).addHttpMethod(HttpMethod.POST).build();
    return handleRequest(request);
  }

  /**
   * Endpoint for adding documents into Elastic Search index.
   */
  @PUT
  @Timed
  @Path("/{index}/{type}/{id}/_create")
  @ApiResponses(value = {@ApiResponse(code = 400, message = "Unable to process JSON"),
      @ApiResponse(code = 401, message = "Not Authorized"),
      @ApiResponse(code = 406, message = "Accept Header not supported")})
  @ApiOperation(value = "Inserts new document {type} with id = {id} to {index}",
      response = JSONObject.class)
  @Consumes(value = MediaType.APPLICATION_JSON)
  public Response addNewDocument(
      @PathParam("index") @ApiParam(required = true, name = "index",
          value = "The index of the search", example = "facilities") @NotBlank String index,
      @PathParam("type") @ApiParam(required = true, name = "type", value = "The document type",
          example = "facility") @NotBlank String documentType,
      @PathParam("id") @ApiParam(required = true, name = "id", value = "The document id",
          example = "123") @NotBlank String id,
      @ApiParam(required = true, name = "requestBody", value = "New Document content - valid json",
          example = "{\"a\": 1}") @ValidJson String requestBody) {
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("index: {} type: {} id: {} body: {}", escapeCRLF(index), escapeCRLF(documentType),
          escapeCRLF(id), escapeCRLF(requestBody));
    }

    final String endpoint =
        String.format("/%s/%s/%s/_create", index.trim(), documentType.trim(), id);
    final IndexQueryRequest request =
        new IndexQueryRequestBuilder().addEsEndpoint(endpoint).addDocumentType(documentType)
            .addRequestBody(requestBody).addHttpMethod(HttpMethod.PUT).build();
    return handleRequest(request);
  }

  /**
   * Endpoint for updating existing document in Elastic Search index.
   */
  @PUT
  @Timed
  @Path("/{index}/{type}/{id}")
  @ApiResponses(value = {@ApiResponse(code = 400, message = "Unable to process JSON"),
      @ApiResponse(code = 401, message = "Not Authorized"),
      @ApiResponse(code = 406, message = "Accept Header not supported")})
  @ApiOperation(value = "Updates existing document {type} with id = {id} in {index}",
      response = JSONObject.class)
  @Consumes(value = MediaType.APPLICATION_JSON)
  public Response updateDocument(
      @PathParam("index") @ApiParam(required = true, name = "index",
          value = "The index of the search", example = "facilities") @NotBlank String index,
      @PathParam("type") @ApiParam(required = true, name = "type", value = "The document type",
          example = "facility") @NotBlank String documentType,
      @PathParam("id") @ApiParam(required = true, name = "id", value = "The document id",
          example = "1") @NotBlank String id,
      @ApiParam(required = true, name = "requestBody",
          value = "Updated content for document - valid json",
          example = "{\"a\": 1}") @ValidJson String requestBody) {
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("index: {} type: {} body: {}", escapeCRLF(index), escapeCRLF(documentType),
          escapeCRLF(requestBody));
    }

    final String endpoint = String.format("/%s/%s/%s", index.trim(), documentType.trim(), id);
    final IndexQueryRequest request =
        new IndexQueryRequestBuilder().addEsEndpoint(endpoint).addDocumentType(documentType)
            .addRequestBody(requestBody).addHttpMethod(HttpMethod.PUT).build();
    return handleRequest(request);
  }

  private Response handleRequest(IndexQueryRequest request) {
    final long startTime = System.currentTimeMillis();
    final IndexQueryResponse indexQueryResponse = indexQueryService.handleRequest(request);
    LOGGER.info("Elastic Search operation total time: {}", System.currentTimeMillis() - startTime);
    return Response.status(Response.Status.OK).entity(indexQueryResponse.getResponse()).build();
  }

}
