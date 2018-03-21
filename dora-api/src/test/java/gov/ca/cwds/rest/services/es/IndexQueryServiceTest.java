package gov.ca.cwds.rest.services.es;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.DoraException;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.ProtocolVersion;
import org.apache.http.RequestLine;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicRequestLine;
import org.apache.http.message.BasicStatusLine;
import org.elasticsearch.client.Response;
import org.hamcrest.junit.ExpectedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.Whitebox;

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
  public void type() throws Exception {
    assertThat(IndexQueryService.class, notNullValue());
  }

  @Test
  public void instantiation() throws Exception {
    assertThat(target, notNullValue());
  }

  @Test
  public void testHandleRequest() throws Exception {
    Map<String, String> test = new HashMap<>();
    test.put("a", "value");
    IndexQueryRequest req = new IndexQueryRequest("people", "person", test);

    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration();

    Whitebox.setInternalState(target, "esConfig", esConfig);
    doReturn(createResponse()).when(target).callElasticsearch(Mockito.anyString(), Mockito.anyString(), Mockito.anyString());

    thrown.expect(DoraException.class);
    target.handleRequest(req);
  }

  @Test
  public void testDoraException() throws Exception {
    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration();
    ElasticsearchConfiguration.XpackConfiguration xpackConfiguration = new ElasticsearchConfiguration.XpackConfiguration();
    xpackConfiguration.setEnabled(false);
    esConfig.setXpack(xpackConfiguration);
    Whitebox.setInternalState(target, "esConfig", esConfig);
    doThrow(new DoraException("")).when(target)
        .callElasticsearch(Mockito.anyObject(), Mockito.anyString(), Mockito.anyString());
    thrown.expect(DoraException.class);
    target.callElasticsearch("http://localhost:8080", "{}", "");
  }

  private Response createResponse() throws NoSuchMethodException, InstantiationException, IllegalAccessException, java.lang.reflect.InvocationTargetException {
    Constructor<Response> constructor = Response.class.getDeclaredConstructor(RequestLine.class, HttpHost.class, HttpResponse.class);
    constructor.setAccessible(true);
    BasicRequestLine requestLine = new BasicRequestLine("", "", new ProtocolVersion("", 0, 0));
    HttpHost httpHost = new HttpHost("", 0);
    HttpResponse httpResponse = new BasicHttpResponse(new BasicStatusLine(new ProtocolVersion("", 0, 0), 0, ""));
    return constructor.newInstance(requestLine, httpHost, httpResponse);
  }
}
