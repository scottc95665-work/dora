package gov.ca.cwds.rest.resources;

import static gov.ca.cwds.rest.resources.IndexQueryResource.DFS_QUERY_THEN_FETCH;
import static gov.ca.cwds.rest.resources.IndexQueryResource.DFS_QUERY_THEN_FETCH_QUERY_PARAM;
import static gov.ca.cwds.rest.resources.IndexQueryResource.SEARCH_TYPE_PARAM;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.function.Function;

import javax.ws.rs.HttpMethod;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.Invocation.Builder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.commons.io.IOUtils;
import org.hamcrest.junit.ExpectedException;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.powermock.reflect.Whitebox;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;

import gov.ca.cwds.dora.tracelog.DoraTraceLogService;
import gov.ca.cwds.rest.DoraConstants;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryResponse;
import gov.ca.cwds.rest.services.es.IndexQueryService;
import io.dropwizard.testing.junit.ResourceTestRule;

/**
 * @author CWDS API Team
 */
public class IndexQueryResourceTest {

  private static final String RESOURCE_BASE_PATH =
      "/" + DoraConstants.RESOURCE_ELASTICSEARCH_INDEX_QUERY + "/people/person/";
  private static final String RESOURCE_ADD_DOCUMENT_PATH = RESOURCE_BASE_PATH + "1/_create";
  private static final String RESOURCE_UPDATE_DOCUMENT_PATH = RESOURCE_BASE_PATH + "1";

  private static final String SEARCH_RESOURCE = RESOURCE_BASE_PATH + "_search";
  private static final String COUNT_RESOURCE = RESOURCE_BASE_PATH + "_count";
  public static final String VALID_JSON = "{\"a\":1}";
  public static final String INVALID_JSON = "test";

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static final IndexQueryService indexQueryService = mock(IndexQueryService.class);
  private static final DoraTraceLogService doraTraceLogService = mock(DoraTraceLogService.class);

  @ClassRule
  public final static ResourceTestRule inMemoryResource = ResourceTestRule.builder()
      .addResource(new IndexQueryResource(indexQueryService, doraTraceLogService)).build();

  @Before
  public void setUp() {
    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration();
    esConfig.setNodes("localhost:9200");
    Whitebox.setInternalState(indexQueryService, "esConfig", esConfig);

    IndexQueryRequest mockCountQueryRequest = new IndexQueryRequest.IndexQueryRequestBuilder()
        .addEsEndpoint("/people/person/_count").addDocumentType("person").addRequestBody(VALID_JSON)
        .addHttpMethod(HttpMethod.POST).build();

    doReturn(new IndexQueryResponse("fred")).when(indexQueryService).handleRequest(Mockito.any());
    doReturn(new IndexQueryResponse("1")).when(indexQueryService)
        .handleRequest(mockCountQueryRequest);
  }

  @After
  public void tearDown() {
    reset(indexQueryService);
  }

  @After
  public void ensureServiceLocatorPopulated() {
    JerseyGuiceUtils.reset();
  }

  @ClassRule
  public static JerseyGuiceRule rule = new JerseyGuiceRule();

  @Test
  public void testSearchNull() throws Exception {
    testInvalidJsonPassedPost(SEARCH_RESOURCE, null);
  }

  @Test
  public void testSearchInvalidQuery() throws Exception {
    testInvalidJsonPassedPost(SEARCH_RESOURCE, INVALID_JSON);
  }

  @Test
  public void testCountIndexNull() throws Exception {
    testInvalidJsonPassedPost(COUNT_RESOURCE, null);
  }

  @Test
  public void testCountIndexInvalidQuery() throws Exception {
    testInvalidJsonPassedPost(COUNT_RESOURCE, INVALID_JSON);
  }

  @Test(expected = IllegalStateException.class)
  public void testAddNewDocumentNull() throws Exception {
    testInvalidJsonPassedPut(RESOURCE_ADD_DOCUMENT_PATH, null);
  }

  @Test
  public void testAddNewDocumentInvalidJson() throws Exception {
    testInvalidJsonPassedPut(RESOURCE_ADD_DOCUMENT_PATH, INVALID_JSON);
  }

  @Test(expected = IllegalStateException.class)
  public void testUpdateExistingDocumentNull() throws Exception {
    testInvalidJsonPassedPut(RESOURCE_UPDATE_DOCUMENT_PATH, null);
  }

  @Test
  public void testUpdateExistingDocumentInvalidJson() throws Exception {
    testInvalidJsonPassedPut(RESOURCE_UPDATE_DOCUMENT_PATH, INVALID_JSON);
  }

  @Test
  public void testSearchIndexWithDfs() throws Exception {
    IndexQueryRequest request =
        testSearchIndex(SEARCH_RESOURCE + "?" + DFS_QUERY_THEN_FETCH_QUERY_PARAM + "=true");
    assertThat(request.getParameters().get(SEARCH_TYPE_PARAM), is(DFS_QUERY_THEN_FETCH));
  }

  @Test
  public void testSearchIndexDefaultDfs() throws Exception {
    IndexQueryRequest request = testSearchIndex(SEARCH_RESOURCE);
    assertThat(request.getParameters().isEmpty(), is(false));
  }

  @Test
  public void testSearchIndexWithDfsFalse() throws Exception {
    IndexQueryRequest request =
        testSearchIndex(SEARCH_RESOURCE + "?" + DFS_QUERY_THEN_FETCH_QUERY_PARAM + "=false");
    assertThat(request.getParameters().isEmpty(), is(true));
  }

  private IndexQueryRequest testSearchIndex(String path) throws IOException {
    ArgumentCaptor<IndexQueryRequest> requestCaptor =
        ArgumentCaptor.forClass(IndexQueryRequest.class);
    testResource(path, builder -> builder.post(Entity.json(VALID_JSON)), "fred");
    verify(indexQueryService).handleRequest(requestCaptor.capture());
    return requestCaptor.getValue();
  }

  @Test
  public void testCountIndex() throws Exception {
    testResource(COUNT_RESOURCE, builder -> builder.post(Entity.json(VALID_JSON)), "1");
  }

  @Test
  public void testAddDocumentToIndex() throws Exception {
    testResource(RESOURCE_ADD_DOCUMENT_PATH, builder -> builder.put(Entity.json(VALID_JSON)),
        "fred");
  }

  @Test
  public void testUpdateExistingDocument() throws Exception {
    testResource(RESOURCE_UPDATE_DOCUMENT_PATH, builder -> builder.put(Entity.json(VALID_JSON)),
        "fred");
  }

  private void testResource(String path, Function<Builder, Response> restOperation,
      String assertResponseBody) throws IOException {
    Response actualResponse = restOperation
        .apply(inMemoryResource.client().target(path).request(MediaType.APPLICATION_JSON));

    String actualResponseBody = IOUtils.toString((ByteArrayInputStream) actualResponse.getEntity(),
        StandardCharsets.UTF_8.displayName());

    assertThat(actualResponse.getStatus(), is(200));
    assertThat(actualResponseBody, is(assertResponseBody));
  }

  private void testInvalidJsonPassed(String path, Function<Builder, Response> restOperation) {
    final int actual = restOperation
        .apply(inMemoryResource.client().target(path).request().accept(MediaType.APPLICATION_JSON))
        .getStatus();
    int expected = 422;
    assertThat(actual, is(expected));
  }

  private void testInvalidJsonPassedPost(String path, String invalidJson) {
    testInvalidJsonPassed(path, builder -> builder.post(Entity.json(invalidJson)));
  }

  private void testInvalidJsonPassedPut(String path, String invalidJson) {
    testInvalidJsonPassed(path, builder -> builder.put(Entity.json(invalidJson)));
  }

}
