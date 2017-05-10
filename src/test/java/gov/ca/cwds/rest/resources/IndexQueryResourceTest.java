package gov.ca.cwds.rest.resources;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import gov.ca.cwds.rest.api.domain.es.IndexQueryRequest;
import gov.ca.cwds.rest.api.domain.es.IndexQueryResponse;
import gov.ca.cwds.rest.core.DoraConstants;
import gov.ca.cwds.rest.services.es.IndexQueryService;
import io.dropwizard.testing.junit.ResourceTestRule;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.hamcrest.junit.ExpectedException;
import org.junit.After;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mockito;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;

/**
 * NOTE : The CWDS API Team has taken the pattern of delegating Resource functions to
 * {@link ServiceBackedResourceDelegate}. As such the tests in here reflect that assumption.
 * 
 * @author CWDS API Team
 */
public class IndexQueryResourceTest {

  private static final String FOUND_RESOURCE = "/" + DoraConstants.RESOURCE_ELASTICSEARCH_INDEX_QUERY + "/people/person/_search";

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static final IndexQueryService svc = mock(IndexQueryService.class);

  private static final SimpleResourceDelegate<String, IndexQueryRequest, IndexQueryResponse, IndexQueryService> delegate =
      new SimpleResourceDelegate<>(svc);

  private final static SimpleResourceDelegate<String, IndexQueryRequest, IndexQueryResponse, IndexQueryService> resourceDelegate =
      mock(delegate.getClass());

  @After
  public void ensureServiceLocatorPopulated() {
    JerseyGuiceUtils.reset();
  }

  @ClassRule
  public static JerseyGuiceRule rule = new JerseyGuiceRule();

  @ClassRule
  public final static ResourceTestRule inMemoryResource = ResourceTestRule.builder()
      .addResource(new IndexQueryResource(resourceDelegate)).build();

  @Before
  public void setup() throws Exception {
    Mockito.reset(resourceDelegate);
  }

  @Test
  public void testPostDelegatesToResourceDelegate() throws Exception {
    Map<String, String> test = new HashMap<>();
    test.put("a", "value");
    inMemoryResource.client().target(FOUND_RESOURCE).request().accept(MediaType.APPLICATION_JSON)
        .post(Entity.entity(test, MediaType.APPLICATION_JSON)).getStatus();
    verify(resourceDelegate, atLeastOnce()).handle(any());
  }

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
}
