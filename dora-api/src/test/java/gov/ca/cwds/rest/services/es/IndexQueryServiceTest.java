package gov.ca.cwds.rest.services.es;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;

import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.DoraException;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryResponse;
import java.io.ByteArrayInputStream;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;
import org.hamcrest.junit.ExpectedException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.internal.util.reflection.Whitebox;

/**
 * @author CWDS API Team
 */
@SuppressWarnings("javadoc")
public class IndexQueryServiceTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Mock
  private IndexQueryRequest req;

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
    req = new IndexQueryRequest("people", "person", test);

    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration("localhost", "9200");
    assertThat(esConfig.getHost(), is(equalTo("localhost")));
    assertThat(esConfig.getPort(), is(equalTo("9200")));

    Whitebox.setInternalState(target, "esConfig", esConfig);
    doReturn("fred").when(target).executionResult(Mockito.anyString(), Mockito.anyString());
    final IndexQueryResponse actual = target.handleRequest(req);
    final IndexQueryResponse expected = new IndexQueryResponse("fred");

    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void testInvalidUrlExecution() throws Exception {
    thrown.expect(DoraException.class);
    target.executionResult("non_valid_url", "test_payload");

  }

  @Test
  public void testExecution() throws Exception {
    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration(null, null);
    ElasticsearchConfiguration.XpackConfiguration xpackConfiguration = new ElasticsearchConfiguration.XpackConfiguration();
    xpackConfiguration.setEnabled(false);
    esConfig.setXpack(xpackConfiguration);
    Whitebox.setInternalState(target, "esConfig", esConfig);

    HttpURLConnection connection = mock(HttpURLConnection.class);
    doReturn(200).when(connection).getResponseCode();
    doReturn(new ByteArrayInputStream("testInputString".getBytes())).when(connection)
        .getInputStream();
    doReturn(connection).when(target).createConnection(Mockito.anyString());

    assertThat("testInputString", is(equalTo(target.executionResult("mockedURL", ""))));
  }

  @Test
  public void testApplySecurity() throws Exception {
    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration(null, null);
    ElasticsearchConfiguration.XpackConfiguration xpackConfiguration = new ElasticsearchConfiguration.XpackConfiguration();
    xpackConfiguration.setEnabled(false);
    esConfig.setXpack(xpackConfiguration);

    Whitebox.setInternalState(target, "esConfig", esConfig);

    HttpURLConnection connection = mock(HttpURLConnection.class);

    doThrow(new RuntimeException()).when(connection)
        .getInputStream();
    doReturn(connection).when(target).createConnection(Mockito.anyString());

    thrown.expect(DoraException.class);
    target.executionResult("mockedURL", "");
  }
}
