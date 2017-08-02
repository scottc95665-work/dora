package gov.ca.cwds.rest.resources;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;

import com.squarespace.jersey2.guice.JerseyGuiceUtils;
import gov.ca.cwds.rest.SwaggerConfiguration;
import io.dropwizard.testing.junit.ResourceTestRule;
import javax.ws.rs.core.MediaType;
import org.junit.After;
import org.junit.ClassRule;
import org.junit.Test;

/**
 * @author TPT-2
 */
public class SwaggerResourceTest {

  @ClassRule
  public static JerseyGuiceRule rule = new JerseyGuiceRule();

  @ClassRule
  public static final ResourceTestRule resources =
      ResourceTestRule.builder().addResource(new SwaggerResource(new SwaggerConfiguration()))
          .build();

  @After
  public void ensureServiceLocatorPopulated() {
    JerseyGuiceUtils.reset();
  }

  @Test
  public void applicationGetReturns406() {
    assertThat(resources.client().target("/swagger").request()
        .accept(MediaType.APPLICATION_JSON).get().getStatus(), is(equalTo(406)));
  }

  @Test
  public void applicationGetReturns404() {
    assertThat(resources.client().target("/").request()
        .accept(MediaType.APPLICATION_JSON).get().getStatus(), is(equalTo(404)));
  }
}
