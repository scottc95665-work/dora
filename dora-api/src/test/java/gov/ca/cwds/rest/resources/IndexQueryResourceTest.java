package gov.ca.cwds.rest.resources;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import gov.ca.cwds.rest.DoraConstants;
import gov.ca.cwds.rest.ElasticsearchConfiguration;
import gov.ca.cwds.rest.api.domain.es.IndexQueryResponse;
import gov.ca.cwds.rest.services.es.IndexQueryService;
import io.dropwizard.testing.junit.ResourceTestRule;

import java.io.ByteArrayInputStream;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import javax.ws.rs.core.Response;
import org.apache.commons.io.IOUtils;
import org.hamcrest.junit.ExpectedException;
import org.junit.After;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import org.mockito.Mockito;
import org.mockito.internal.util.reflection.Whitebox;

/**
 * @author CWDS API Team
 */
public class IndexQueryResourceTest {

  private static final String FOUND_RESOURCE =
      "/" + DoraConstants.RESOURCE_ELASTICSEARCH_INDEX_QUERY + "/people/person/_search";

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static final IndexQueryService indexQueryService = mock(IndexQueryService.class);

  @ClassRule
  public final static ResourceTestRule inMemoryResource = ResourceTestRule.builder()
      .addResource(new IndexQueryResource(indexQueryService)).build();

  @BeforeClass
  public static void setUp() {
    ElasticsearchConfiguration esConfig = new ElasticsearchConfiguration("localhost", "9200");
    Whitebox.setInternalState(indexQueryService, "esConfig", esConfig);

    doReturn(new IndexQueryResponse("fred")).when(indexQueryService).handleRequest(Mockito.any());
  }

  @After
  public void ensureServiceLocatorPopulated() {
    JerseyGuiceUtils.reset();
  }

  @ClassRule
  public static JerseyGuiceRule rule = new JerseyGuiceRule();

  @Test
  public void testPostNullGives400() throws Exception {
    final int actual =
        inMemoryResource.client().target(FOUND_RESOURCE).request()
            .accept(MediaType.APPLICATION_JSON)
            .post(Entity.entity("test", MediaType.APPLICATION_JSON)).getStatus();

    int expected = 400;
    assertThat(actual, is(expected));
  }

  @Test
  public void testPostNonJsonGives400() throws Exception {
    final int actual =
        inMemoryResource.client().target(FOUND_RESOURCE).request()
            .accept(MediaType.APPLICATION_JSON)
            .post(Entity.entity("test", MediaType.APPLICATION_JSON)).getStatus();

    int expected = 400;
    assertThat(actual, is(expected));
  }

  @Test
  public void testSearchIndex() throws Exception {
    Response actualResponse = inMemoryResource.client().target(FOUND_RESOURCE)
        .request(MediaType.APPLICATION_JSON).post(Entity.json(""));

    String actualResponseBody = IOUtils
        .toString((ByteArrayInputStream) actualResponse.getEntity(), "UTF-8");

    assertThat(actualResponse.getStatus(), is(200));
    assertThat(actualResponseBody, is("fred"));
  }
}
