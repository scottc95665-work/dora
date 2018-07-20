package gov.ca.cwds.rest.services.es;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import gov.ca.cwds.dora.security.FieldFilters;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.EsRestClientManager;
import gov.ca.cwds.rest.api.DoraException;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import io.dropwizard.testing.FixtureHelpers;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.elasticsearch.client.Response;
import org.elasticsearch.client.RestClient;
import org.hamcrest.junit.ExpectedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.powermock.reflect.Whitebox;

/**
 * @author CWDS TPT-2
 */
@SuppressWarnings("javadoc")
public class IndexQueryServiceTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Spy
  @InjectMocks
  private IndexQueryService target; // "Class Under Test"

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void type() {
    assertThat(IndexQueryService.class, notNullValue());
  }

  @Test
  public void instantiation() {
    assertThat(target, notNullValue());
  }

  @Test
  public void testHandleRequest() throws Exception {
    Map<String, String> test = new HashMap<>();
    test.put("a", "value");
    IndexQueryRequest req = new IndexQueryRequest("people", "person", test);
    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration();
    Whitebox.setInternalState(target, "esConfig", esConfig);

    FieldFilters fieldFilters = mock(FieldFilters.class);
    doReturn("").when(target).applyFieldFiltering(anyMap(), anyString());
    Whitebox.setInternalState(target, "fieldFilters", fieldFilters);

    Response response = mock(Response.class);
    StatusLine statusLine = mock(StatusLine.class);

    BasicHttpEntity entity = new BasicHttpEntity();
    when(response.getStatusLine()).thenReturn(statusLine);
    when(statusLine.getStatusCode()).thenReturn(200);

    String fixture = FixtureHelpers.fixture("people-person-meta-data.json");
    entity.setContent(new ByteArrayInputStream(fixture.getBytes()));
    when(response.getEntity()).thenReturn(entity);

    doReturn(response).when(target).performRequest(any(StringEntity.class), anyString());

    assertNotNull(target.handleRequest(req));
  }

  @Test
  public void testDoraException() throws Exception {
    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration();
    ElasticsearchConfiguration.XpackConfiguration xpackConfiguration = new ElasticsearchConfiguration.XpackConfiguration();
    xpackConfiguration.setEnabled(false);
    esConfig.setXpack(xpackConfiguration);
    Whitebox.setInternalState(target, "esConfig", esConfig);
    doThrow(new DoraException("")).when(target)
        .performRequest(any(StringEntity.class), anyString());
    thrown.expect(DoraException.class);
    target.callElasticsearch("http://localhost:8080", "{}", "{}");
  }

  @Test
  public void testCallElasticsearch() throws IOException {
    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration();
    esConfig.setUser("user");
    esConfig.setPassword("password");
    esConfig.setNodes("localhost:1");
    Whitebox.setInternalState(target, "esConfig", esConfig);

    Response response = mock(Response.class);
    doReturn(response).when(target).performRequest(any(StringEntity.class), anyString());

    assertNotNull(target.callElasticsearch("people", "person", "{}"));
  }

  @Test
  public void testPerformRequest() throws IOException {
    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration();
    esConfig.setUser("user");
    esConfig.setPassword("password");
    esConfig.setNodes("localhost:1");
    Whitebox.setInternalState(target, "esConfig", esConfig);

    Response response = mock(Response.class);
    RestClient mockRestClient = mock(RestClient.class);
    doReturn(response).when(mockRestClient).performRequest(anyString(), anyString(), anyMap(),
        any(StringEntity.class));

    EsRestClientManager mockEsRestClientManager = mock(EsRestClientManager.class);
    doReturn(mockRestClient).when(mockEsRestClientManager).getEsRestClient();

    Whitebox.setInternalState(target, "esRestClientManager", mockEsRestClientManager);

    StringEntity stringEntity = new StringEntity("{}", ContentType.APPLICATION_JSON);
    assertNotNull(target.performRequest(stringEntity, "/people/person/_search"));
  }
}
