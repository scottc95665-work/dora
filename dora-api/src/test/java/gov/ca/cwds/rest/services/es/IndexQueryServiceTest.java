package gov.ca.cwds.rest.services.es;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;

import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.DoraException;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryResponse;
import java.util.HashMap;
import java.util.Map;
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

    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration("localhost", "9200");
    assertThat(esConfig.getHost(), is(equalTo("localhost")));
    assertThat(esConfig.getPort(), is(equalTo("9200")));

    Whitebox.setInternalState(target, "esConfig", esConfig);
    doReturn("fred").when(target).invokeElasticsearch(Mockito.anyString(), Mockito.anyString());
    final IndexQueryResponse actual = target.handleRequest(req);
    final IndexQueryResponse expected = new IndexQueryResponse("fred");

    assertThat(actual, is(equalTo(expected)));
  }

  @Test
  public void testInvokeElasticsearch() throws Exception {
    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration("localhost", "9200");
    ElasticsearchConfiguration.XpackConfiguration xpackConfiguration = new ElasticsearchConfiguration.XpackConfiguration();
    xpackConfiguration.setEnabled(true);
    esConfig.setXpack(xpackConfiguration);
    Whitebox.setInternalState(target, "esConfig", esConfig);
    doReturn("{ hits: 0 }").when(target).postRequest(Mockito.anyObject(), Mockito.anyString());
    assertThat("{ hits: 0 }", is(equalTo(target.invokeElasticsearch("http://localhost:8080", "{}"))));
  }

  @Test
  public void testDoraException() throws Exception {
    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration("localhost", "9200");
    ElasticsearchConfiguration.XpackConfiguration xpackConfiguration = new ElasticsearchConfiguration.XpackConfiguration();
    xpackConfiguration.setEnabled(false);
    esConfig.setXpack(xpackConfiguration);
    Whitebox.setInternalState(target, "esConfig", esConfig);
    doThrow(new DoraException("")).when(target).postRequest(Mockito.anyObject(), Mockito.anyString());
    thrown.expect(DoraException.class);
    target.invokeElasticsearch("http://localhost:8080", "{}");
  }
}
