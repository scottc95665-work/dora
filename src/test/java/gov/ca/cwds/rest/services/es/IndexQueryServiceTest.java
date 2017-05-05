package gov.ca.cwds.rest.services.es;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import gov.ca.cwds.data.es.ElasticsearchDao;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryResponse;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.search.SearchHits;
import org.hamcrest.junit.ExpectedException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

/**
 * @author CWDS API Team
 *
 */
@SuppressWarnings("javadoc")
public class IndexQueryServiceTest {

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  @Mock
  private ElasticsearchDao dao;

  @Mock
  private SearchHits hits;

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
    Map<String, String> test = new HashMap<String, String>();
    test.put("a", "value");
    req = new IndexQueryRequest("index", test);
    String query = new JSONObject(test).toString();
    when(target.callDao("index", query)).thenReturn(("fred"));
    final IndexQueryResponse actual = target.handleRequest(req);
    IndexQueryResponse expected = new IndexQueryResponse("fred");
    assertThat(actual, is(equalTo(expected)));
  }

}
