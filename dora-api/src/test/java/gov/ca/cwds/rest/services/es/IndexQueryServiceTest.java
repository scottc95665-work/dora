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
import gov.ca.cwds.rest.ElasticSearchConfiguration;
import gov.ca.cwds.rest.api.DoraException;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest.IndexQueryRequestBuilder;
import io.dropwizard.testing.FixtureHelpers;
import java.io.ByteArrayInputStream;
import org.apache.http.StatusLine;
import org.apache.http.entity.BasicHttpEntity;
import org.elasticsearch.client.Response;
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
    String testBody = "{\"a\": \"value\"}";
    IndexQueryRequest request = new IndexQueryRequestBuilder().addDocumentType("person")
        .addRequestBody(testBody).build();
    ElasticSearchConfiguration esConfig = new ElasticSearchConfiguration();
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

    doReturn(response).when(target).performRequest(request);

    assertNotNull(target.handleRequest(request));
  }

  @Test
  public void testDoraException() throws Exception {
    ElasticSearchConfiguration esConfig = new ElasticSearchConfiguration();
    ElasticSearchConfiguration.XpackConfiguration xpackConfiguration = new ElasticSearchConfiguration.XpackConfiguration();
    xpackConfiguration.setEnabled(false);
    esConfig.setXpack(xpackConfiguration);
    Whitebox.setInternalState(target, "esConfig", esConfig);
    doThrow(new DoraException("")).when(target)
        .performRequest(any());
    thrown.expect(DoraException.class);
    target.handleRequest(
        new IndexQueryRequestBuilder().addRequestBody("{}").addEndpoint("http://localhost:8080")
            .build());
  }

}
