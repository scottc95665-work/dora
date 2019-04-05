package gov.ca.cwds.rest.resources;

import static gov.ca.cwds.dora.DoraUtils.escapeCRLF;
import static gov.ca.cwds.rest.DoraConstants.RESOURCE_ELASTICSEARCH_INDEX_QUERY;

import javax.ws.rs.Consumes;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.hibernate.validator.constraints.NotBlank;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;
import com.google.inject.Inject;

import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest.IndexQueryRequestBuilder;
import gov.ca.cwds.rest.api.domain.es.IndexQueryResponse;
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

  private IndexQueryService indexQueryService;

  @Inject
  public IndexQueryResource(IndexQueryService indexQueryService) {
    this.indexQueryService = indexQueryService;
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
      @HeaderParam("custom_sort") @ApiParam(required = true, name = "custom_sort",
          value = "Custom sort flag", example = "true") Boolean customSort,
      @PathParam("index") @ApiParam(required = true, name = "index",
          value = "The index of the search", example = "facilities") @NotBlank String index,
      @PathParam("type") @ApiParam(required = true, name = "type", value = "The document type",
          example = "facility") @NotBlank String documentType,
      @ApiParam(required = true,
          examples = @Example(@ExampleProperty(mediaType = MediaType.APPLICATION_JSON,
              value = "{\"query\":{\"match_all\":{}}}"))) @ValidJson String requestBody) {
    if (LOGGER.isInfoEnabled()) {
      LOGGER.info("index: {} type: {} query: {}", escapeCRLF(index), escapeCRLF(documentType),
          escapeCRLF(requestBody));
    }

    final String endpoint = String.format("/%s/%s/_search", index.trim(), documentType.trim());
    final IndexQueryRequest request = new IndexQueryRequestBuilder().addEsEndpoint(endpoint)
        .addDocumentType(documentType).addRequestBody(requestBody).addHttpMethod(HttpMethod.POST)
        .addCustomSort(customSort != null && customSort.booleanValue()).build();
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
    IndexQueryRequest request =
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
    IndexQueryRequest request =
        new IndexQueryRequestBuilder().addEsEndpoint(endpoint).addDocumentType(documentType)
            .addRequestBody(requestBody).addHttpMethod(HttpMethod.PUT).build();
    return handleRequest(request);
  }

  private Response handleRequest(IndexQueryRequest request) {
    long startTime = System.currentTimeMillis();
    IndexQueryResponse indexQueryResponse = indexQueryService.handleRequest(request);
    LOGGER.info("Elastic Search operation total time: {}", System.currentTimeMillis() - startTime);
    return Response.status(Response.Status.OK).entity(indexQueryResponse.getResponse()).build();
  }

}

