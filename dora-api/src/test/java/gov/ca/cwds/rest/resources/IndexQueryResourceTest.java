package gov.ca.cwds.rest.resources;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;

import gov.ca.cwds.rest.DoraConstants;
import gov.ca.cwds.rest.services.es.IndexQueryService;
import io.dropwizard.testing.junit.ResourceTestRule;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;

import org.hamcrest.junit.ExpectedException;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;

/**
 * @author CWDS API Team
 */
public class IndexQueryResourceTest {

  private static final String FOUND_RESOURCE =
      "/" + DoraConstants.RESOURCE_ELASTICSEARCH_INDEX_QUERY + "/people/person/_search";

  @Rule
  public ExpectedException thrown = ExpectedException.none();

  private static final IndexQueryService svc = mock(IndexQueryService.class);

  @After
  public void ensureServiceLocatorPopulated() {
    JerseyGuiceUtils.reset();
  }

  @ClassRule
  public static JerseyGuiceRule rule = new JerseyGuiceRule();

  @ClassRule
  public final static ResourceTestRule inMemoryResource = ResourceTestRule.builder()
      .addResource(new IndexQueryResource(svc)).build();

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
